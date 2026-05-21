import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-company',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './company.html',
  styleUrl: './company.css'
})
export class CompanyComponent implements OnInit {
  protected apiService = inject(ApiService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  // Dashboard & Marketplace State Signals
  kpis = signal<any>(null);
  listings = signal<any[]>([]);
  cart = signal<any[]>([]);
  favourites = signal<any[]>([]);
  purchases = signal<any[]>([]);
  certificates = signal<any[]>([]);

  // Filters state
  filters = { practiceType: '', minPrice: undefined as number | undefined, maxPrice: undefined as number | undefined, location: '' };

  // View control signals
  activeTab = signal<'dashboard' | 'marketplace' | 'cart' | 'purchases' | 'certificates'>('dashboard');
  isLoading = signal(false);

  ngOnInit() {
    // Redirect if not authenticated or not a company
    if (!this.apiService.isAuthenticated() || !this.apiService.isCompany()) {
      this.router.navigate(['/auth']);
      return;
    }

    this.refreshAllData();
  }

  refreshAllData() {
    this.fetchDashboard();
    this.fetchListings();
    this.fetchCart();
    this.fetchFavourites();
    this.fetchPurchases();
    this.fetchCertificates();
  }

  fetchDashboard() {
    this.apiService.getCompanyDashboard().subscribe({
      next: (res) => { if (res.success) this.kpis.set(res.data); },
      error: () => this.toastService.error('Failed to load dashboard metrics.')
    });
  }

  fetchListings() {
    this.apiService.searchListings(this.filters).subscribe({
      next: (res) => { if (res.success) this.listings.set(res.data); },
      error: () => this.toastService.error('Failed to load marketplace listings.')
    });
  }

  fetchCart() {
    this.apiService.getCart().subscribe({
      next: (res) => { if (res.success) this.cart.set(res.data); },
      error: () => this.toastService.error('Failed to load shopping cart.')
    });
  }

  fetchFavourites() {
    this.apiService.getFavourites().subscribe({
      next: (res) => { if (res.success) this.favourites.set(res.data); },
      error: () => this.toastService.error('Failed to load favourite listings.')
    });
  }

  fetchPurchases() {
    this.apiService.getCompanyPurchaseHistory().subscribe({
      next: (res) => { if (res.success) this.purchases.set(res.data); },
      error: () => this.toastService.error('Failed to load purchase history.')
    });
  }

  fetchCertificates() {
    this.apiService.getCompanyCertificates().subscribe({
      next: (res) => { if (res.success) this.certificates.set(res.data); },
      error: () => this.toastService.error('Failed to load certificates.')
    });
  }

  applyFilters() {
    this.fetchListings();
  }

  clearFilters() {
    this.filters = { practiceType: '', minPrice: undefined, maxPrice: undefined, location: '' };
    this.fetchListings();
  }

  addToCart(listingId: number) {
    this.apiService.addToCart(listingId).subscribe({
      next: (res) => {
        this.toastService.success(res.message || 'Added to cart.');
        this.refreshAllData();
      },
      error: (err) => {
        this.toastService.error(err.error?.message || 'Failed to add item to cart.');
      }
    });
  }

  removeFromCart(listingId: number) {
    this.apiService.removeFromCart(listingId).subscribe({
      next: (res) => {
        this.toastService.success(res.message || 'Removed from cart.');
        this.refreshAllData();
      },
      error: (err) => {
        this.toastService.error(err.error?.message || 'Failed to remove item.');
      }
    });
  }

  purchaseListing(listingId: number) {
    this.isLoading.set(true);
    this.apiService.purchaseListing(listingId).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Credits purchased successfully!');
        this.refreshAllData();
        this.activeTab.set('certificates');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Failed to purchase credits.');
      }
    });
  }

  checkoutCart() {
    if (this.cart().length === 0) return;
    this.isLoading.set(true);
    
    // Purchase all items sequentially
    const items = [...this.cart()];
    let currentIdx = 0;

    const purchaseNext = () => {
      if (currentIdx >= items.length) {
        this.isLoading.set(false);
        this.toastService.success('Checkout completed successfully!');
        this.refreshAllData();
        this.activeTab.set('certificates');
        return;
      }
      
      const item = items[currentIdx];
      this.apiService.purchaseListing(item.id).subscribe({
        next: () => {
          currentIdx++;
          purchaseNext();
        },
        error: (err) => {
          this.isLoading.set(false);
          this.toastService.error(`Failed to purchase listing ${item.title || item.id}: ${err.error?.message || ''}`);
          this.refreshAllData();
        }
      });
    };

    purchaseNext();
  }

  toggleFavourite(listingId: number) {
    this.apiService.toggleFavourite(listingId).subscribe({
      next: (res) => {
        this.toastService.info(res.message || 'Toggled favourite status.');
        this.fetchFavourites();
        this.fetchListings();
      },
      error: () => this.toastService.error('Failed to toggle favourite.')
    });
  }

  isFavourite(listingId: number): boolean {
    return this.favourites().some(f => f.id === listingId);
  }

  downloadPdf(certificateId: string) {
    const url = this.apiService.getPublicCertificatePdfUrl(certificateId);
    window.open(url, '_blank');
  }

  logout() {
    this.apiService.logout();
    this.router.navigate(['/auth']);
  }
}
