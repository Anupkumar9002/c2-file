package com.carbon.platform.service;

import com.carbon.platform.entity.Farmer;
import com.carbon.platform.entity.LandParcel;
import com.carbon.platform.enums.ApprovalStatus;
import com.carbon.platform.enums.LandType;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.repository.FarmerRepository;
import com.carbon.platform.repository.LandParcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LandParcelService {

    private final LandParcelRepository landParcelRepository;
    private final FarmerRepository farmerRepository;

    public LandParcelService(LandParcelRepository landParcelRepository, FarmerRepository farmerRepository) {
        this.landParcelRepository = landParcelRepository;
        this.farmerRepository = farmerRepository;
    }

    @Transactional
    public LandParcel addParcel(String farmerEmail, String parcelName, Double areaInAcres, String location, LandType landType) {
        Farmer farmer = farmerRepository.findByUserEmail(farmerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Farmer profile not found for user: " + farmerEmail));

        if (farmer.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new InvalidActionException("Only APPROVED farmers can register land parcels.");
        }

        if (areaInAcres <= 0 || areaInAcres > 10000) {
            throw new InvalidActionException("Land area must be a positive decimal up to 10,000 acres.");
        }

        LandParcel parcel = new LandParcel(farmer, parcelName, areaInAcres, location, landType);
        return landParcelRepository.save(parcel);
    }

    @Transactional
    public LandParcel editParcel(String farmerEmail, Long parcelId, String parcelName, Double areaInAcres, String location, LandType landType) {
        LandParcel parcel = landParcelRepository.findById(parcelId)
                .orElseThrow(() -> new EntityNotFoundException("Land parcel not found with ID: " + parcelId));

        if (!parcel.getFarmer().getUser().getEmail().equals(farmerEmail)) {
            throw new InvalidActionException("You are not authorized to edit this land parcel.");
        }

        if (areaInAcres != null) {
            if (areaInAcres <= 0 || areaInAcres > 10000) {
                throw new InvalidActionException("Land area must be a positive decimal up to 10,000 acres.");
            }
            parcel.setAreaInAcres(areaInAcres);
        }

        if (parcelName != null) parcel.setParcelName(parcelName);
        if (location != null) parcel.setLocation(location);
        if (landType != null) parcel.setLandType(landType);

        return landParcelRepository.save(parcel);
    }

    public List<LandParcel> getParcelsByFarmer(String farmerEmail) {
        return landParcelRepository.findByFarmerUserEmail(farmerEmail);
    }

    public List<LandParcel> getAllParcels() {
        return landParcelRepository.findAll();
    }
}
