import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-farmer',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './farmer.html',
  styleUrl: './farmer.css'
})
export class FarmerComponent implements OnInit {
  protected apiService = inject(ApiService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  // Dashboard Data Signals
  kpis = signal<any>(null);
  parcels = signal<any[]>([]);
  practiceLogs = signal<any[]>([]);
  wallet = signal<any>(null);
  withdrawals = signal<any[]>([]);
  carbonAssets = signal<any[]>([]);

  // Navigation tabs
  activeTab = signal<'dashboard' | 'parcels' | 'practice-logs' | 'wallet' | 'carbon-assets'>('dashboard');

  // Modal forms controls
  showParcelModal = signal(false);
  showPracticeModal = signal(false);
  showWithdrawModal = signal(false);
  showAuditModal = signal(false);
  isLoading = signal(false);
  selectedAsset = signal<any>(null);
  parsedAuditDetails = signal<any>(null);

  // Forms Models
  parcelData = { id: null as number | null, parcelName: '', areaInAcres: 0, location: '', landType: 'DRYLAND' };
  logData = {
    parcelId: 0,
    practiceType: 'TREE_PLANTATION',
    cropCategory: 'FOOD',
    growingSeason: 'KHARIF',
    quantity: 0,
    sowingDate: '',
    harvestingDate: '',
    proofDocumentBase64: ''
  };
  withdrawData = { amount: 0, bankDetails: '' };

  ngOnInit() {
    // If not authenticated or not a farmer, kick back to login
    if (!this.apiService.isAuthenticated() || !this.apiService.isFarmer()) {
      this.router.navigate(['/auth']);
      return;
    }

    this.refreshAllData();
  }

  refreshAllData() {
    this.fetchDashboard();
    this.fetchParcels();
    this.fetchPracticeLogs();
    this.fetchWallet();
    this.fetchWithdrawals();
    this.fetchCarbonAssets();
  }

  fetchDashboard() {
    this.apiService.getFarmerDashboard().subscribe({
      next: (res) => { if (res.success) this.kpis.set(res.data); },
      error: () => this.toastService.error('Failed to load dashboard metrics.')
    });
  }

  fetchParcels() {
    this.apiService.getMyParcels().subscribe({
      next: (res) => { if (res.success) this.parcels.set(res.data); },
      error: () => this.toastService.error('Failed to load land parcels.')
    });
  }

  fetchPracticeLogs() {
    this.apiService.getMyPracticeLogs().subscribe({
      next: (res) => { if (res.success) this.practiceLogs.set(res.data); },
      error: () => this.toastService.error('Failed to load practice logs.')
    });
  }

  fetchWallet() {
    this.apiService.getFarmerWallet().subscribe({
      next: (res) => { if (res.success) this.wallet.set(res.data); },
      error: () => this.toastService.error('Failed to load wallet balance.')
    });
  }

  fetchWithdrawals() {
    this.apiService.getFarmerWithdrawalHistory().subscribe({
      next: (res) => { if (res.success) this.withdrawals.set(res.data); },
      error: () => this.toastService.error('Failed to load withdrawal history.')
    });
  }

  fetchCarbonAssets() {
    this.apiService.getCarbonAssets().subscribe({
      next: (res) => { if (res.success) this.carbonAssets.set(res.data); },
      error: () => this.toastService.error('Failed to load carbon assets directory.')
    });
  }

  openAddParcel() {
    this.parcelData = { id: null, parcelName: '', areaInAcres: 0, location: '', landType: 'DRYLAND' };
    this.showParcelModal.set(true);
  }

  openEditParcel(parcel: any) {
    this.parcelData = {
      id: parcel.id,
      parcelName: parcel.parcelName,
      areaInAcres: parcel.areaInAcres,
      location: parcel.location,
      landType: parcel.landType
    };
    this.showParcelModal.set(true);
  }

  closeParcelModal() {
    this.showParcelModal.set(false);
  }

  saveParcel() {
    this.isLoading.set(true);
    const apiCall = this.parcelData.id
      ? this.apiService.editParcel(this.parcelData.id, this.parcelData.parcelName, this.parcelData.areaInAcres, this.parcelData.location, this.parcelData.landType)
      : this.apiService.registerParcel(this.parcelData.parcelName, this.parcelData.areaInAcres, this.parcelData.location, this.parcelData.landType);

    apiCall.subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Land parcel saved successfully.');
        this.showParcelModal.set(false);
        this.refreshAllData();
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Failed to save land parcel.');
      }
    });
  }

  openAddPractice() {
    if (this.parcels().length === 0) {
      this.toastService.warning('Please register at least one land parcel first.');
      return;
    }
    this.logData = {
      parcelId: this.parcels()[0].id,
      practiceType: 'TREE_PLANTATION',
      cropCategory: 'FOOD',
      growingSeason: 'KHARIF',
      quantity: 0,
      sowingDate: '',
      harvestingDate: '',
      proofDocumentBase64: ''
    };
    this.showPracticeModal.set(true);
  }

  closePracticeModal() {
    this.showPracticeModal.set(false);
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64Str = reader.result as string;
        // Strip data prefix: e.g. "data:image/png;base64,iVBOR..." -> "iVBOR..."
        const base64Clean = base64Str.split(',')[1];
        this.logData.proofDocumentBase64 = base64Clean;
      };
      reader.readAsDataURL(file);
    }
  }

  savePracticeLog() {
    this.isLoading.set(true);
    this.apiService.logPractice(
      this.logData.parcelId,
      this.logData.practiceType,
      this.logData.cropCategory,
      this.logData.growingSeason,
      this.logData.quantity,
      this.logData.sowingDate,
      this.logData.harvestingDate,
      this.logData.proofDocumentBase64
    ).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Practice log submitted successfully.');
        this.showPracticeModal.set(false);
        this.refreshAllData();
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Failed to submit practice log.');
      }
    });
  }

  openWithdraw() {
    this.withdrawData = { amount: 0, bankDetails: '' };
    this.showWithdrawModal.set(true);
  }

  closeWithdrawModal() {
    this.showWithdrawModal.set(false);
  }

  requestWithdrawal() {
    this.isLoading.set(true);
    this.apiService.requestWithdrawal(this.withdrawData.amount, this.withdrawData.bankDetails).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Withdrawal request submitted.');
        this.showWithdrawModal.set(false);
        this.refreshAllData();
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Failed to submit withdrawal request.');
      }
    });
  }

  viewAssetAuditTrail(asset: any) {
    this.selectedAsset.set(asset);
    if (asset.calculationDetails) {
      try {
        this.parsedAuditDetails.set(JSON.parse(asset.calculationDetails));
      } catch (e) {
        console.error('Failed to parse asset calculation details JSON', e);
        this.parsedAuditDetails.set(null);
      }
    } else {
      this.parsedAuditDetails.set(null);
    }
    this.showAuditModal.set(true);
  }

  closeAuditModal() {
    this.showAuditModal.set(false);
    this.selectedAsset.set(null);
    this.parsedAuditDetails.set(null);
  }

  logout() {
    this.apiService.logout();
    this.router.navigate(['/auth']);
  }
}
