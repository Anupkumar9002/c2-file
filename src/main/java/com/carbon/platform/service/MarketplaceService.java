package com.carbon.platform.service;

import com.carbon.platform.entity.*;
import com.carbon.platform.enums.ApprovalStatus;
import com.carbon.platform.enums.CreditStatus;
import com.carbon.platform.enums.ListingStatus;
import com.carbon.platform.enums.PracticeType;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.repository.CarbonCreditRepository;
import com.carbon.platform.repository.FarmerRepository;
import com.carbon.platform.repository.ListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketplaceService {

    private final ListingRepository listingRepository;
    private final CarbonCreditRepository carbonCreditRepository;
    private final FarmerRepository farmerRepository;

    // Cart in-memory simulation per user email (for US-050)
    private final java.util.Map<String, List<Long>> carts = new java.util.HashMap<>();
    
    // Favourites in-memory simulation per user email (for US-048)
    private final java.util.Map<String, List<Long>> favourites = new java.util.HashMap<>();

    public MarketplaceService(ListingRepository listingRepository,
                              CarbonCreditRepository carbonCreditRepository,
                              FarmerRepository farmerRepository) {
        this.listingRepository = listingRepository;
        this.carbonCreditRepository = carbonCreditRepository;
        this.farmerRepository = farmerRepository;
    }

    @Transactional
    public Listing createListing(String farmerEmail, Long creditId, Double quantity, Double pricePerCredit, LocalDate validUntil) {
        Farmer farmer = farmerRepository.findByUserEmail(farmerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Farmer profile not found"));

        if (farmer.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new InvalidActionException("Only APPROVED farmers can list carbon credits.");
        }

        CarbonCredit credit = carbonCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("Carbon credit not found with ID: " + creditId));

        if (!credit.getFarmer().getId().equals(farmer.getId())) {
            throw new InvalidActionException("This carbon credit does not belong to you.");
        }

        if (credit.getStatus() != CreditStatus.VERIFIED) {
            throw new InvalidActionException("Only VERIFIED carbon credits can be listed on the marketplace.");
        }

        if (quantity <= 0 || quantity > credit.getFinalCredits()) {
            throw new InvalidActionException("Quantity to list must be positive and cannot exceed " + credit.getFinalCredits());
        }

        if (pricePerCredit <= 0) {
            throw new InvalidActionException("Price per credit must be a positive decimal.");
        }

        Listing listing = new Listing();
        listing.setFarmer(farmer);
        listing.setCarbonCredit(credit);
        listing.setQuantity(quantity);
        listing.setPricePerCredit(pricePerCredit);
        listing.setValidUntil(validUntil != null ? validUntil : LocalDate.now().plusMonths(3));
        listing.setStatus(ListingStatus.ACTIVE);

        // Update Carbon Credit status to listed
        credit.setStatus(CreditStatus.LISTED);
        carbonCreditRepository.save(credit);

        return listingRepository.save(listing);
    }

    public List<Listing> searchListings(PracticeType practiceType, Double minPrice, Double maxPrice, String location) {
        return listingRepository.findByStatus(ListingStatus.ACTIVE).stream()
                .filter(l -> practiceType == null || l.getCarbonCredit().getPracticeLog().getPracticeType() == practiceType)
                .filter(l -> minPrice == null || l.getPricePerCredit() >= minPrice)
                .filter(l -> maxPrice == null || l.getPricePerCredit() <= maxPrice)
                .filter(l -> location == null || location.trim().isEmpty() || 
                             (l.getCarbonCredit().getPracticeLog().getParcel().getLocation() != null &&
                              l.getCarbonCredit().getPracticeLog().getParcel().getLocation().toLowerCase().contains(location.toLowerCase())))
                .toList();
    }

    public List<Listing> getFarmerListings(String farmerEmail) {
        return listingRepository.findByFarmerUserEmail(farmerEmail);
    }

    public Listing getListingDetail(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found with ID: " + id));
    }

    // Cart operations (US-050)
    public void addToCart(String email, Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new InvalidActionException("Listing is no longer active.");
        }
        carts.computeIfAbsent(email, k -> new ArrayList<>()).add(listingId);
    }

    public List<Listing> getCart(String email) {
        List<Long> listingIds = carts.getOrDefault(email, new ArrayList<>());
        return listingRepository.findAllById(listingIds).stream()
                .filter(l -> l.getStatus() == ListingStatus.ACTIVE)
                .toList();
    }

    public void removeFromCart(String email, Long listingId) {
        List<Long> list = carts.get(email);
        if (list != null) {
            list.remove(listingId);
        }
    }

    public void clearCart(String email) {
        carts.remove(email);
    }

    // Favourites operations (US-048)
    public void toggleFavourite(String email, Long listingId) {
        List<Long> list = favourites.computeIfAbsent(email, k -> new ArrayList<>());
        if (list.contains(listingId)) {
            list.remove(listingId);
        } else {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
            list.add(listingId);
        }
    }

    public List<Listing> getFavourites(String email) {
        List<Long> listingIds = favourites.getOrDefault(email, new ArrayList<>());
        return listingRepository.findAllById(listingIds);
    }
}
