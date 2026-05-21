import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminComponent implements OnInit {
  protected apiService = inject(ApiService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  // View state signals
  activeTab = signal<'dashboard' | 'farmers' | 'companies' | 'practice-logs' | 'carbon-credits' | 'withdrawals' | 'formulas' | 'audit-logs'>('dashboard');
  isLoading = signal(false);

  // Dashboard KPI data
  kpis = signal<any>(null);

  // Entities lists signals
  farmers = signal<any[]>([]);
  companies = signal<any[]>([]);
  practiceLogs = signal<any[]>([]);
  carbonCredits = signal<any[]>([]);
  withdrawals = signal<any[]>([]);
  auditLogs = signal<any[]>([]);

  // Selection states for actions (for modals/dialogs)
  selectedItem = signal<any>(null);
  showActionModal = signal(false);
  actionType = signal<'farmer' | 'company' | 'practice-log' | 'carbon-credit' | 'withdrawal-approve' | 'withdrawal-reject' | 'list-marketplace'>('farmer');
  
  // Action details
  actionComments = '';
  actionStatus: 'APPROVED' | 'REJECTED' | 'CANCELED' | 'VERIFIED' = 'APPROVED';
  pricePerCredit = 10.0;
  quantity = 0;
  validUntil = '';

  // Credit formula forms controls
  showFormulaModal = signal(false);
  formulaData = {
    practiceType: 'TREE_PLANTATION',
    baseCoefficient: 1.5,
    maxCap: 100.0,
    version: '1.0'
  };

  ngOnInit() {
    // Check if user is authenticated and is admin or supervisor
    if (!this.apiService.isAuthenticated() || !this.apiService.isAdmin()) {
      this.toastService.error('Unauthorized access. Redirected to login.');
      this.router.navigate(['/auth']);
      return;
    }

    this.refreshData();
  }

  refreshData() {
    this.fetchDashboard();
    this.fetchFarmers();
    this.fetchCompanies();
    this.fetchPracticeLogs();
    this.fetchCarbonCredits();
    this.fetchWithdrawals();
    this.fetchAuditLogs();
  }

  fetchDashboard() {
    this.apiService.getAdminDashboard().subscribe({
      next: (res) => { if (res.success) this.kpis.set(res.data); },
      error: () => this.toastService.error('Failed to load admin dashboard.')
    });
  }

  fetchFarmers() {
    this.apiService.getAdminFarmers().subscribe({
      next: (res) => { if (res.success) this.farmers.set(res.data); },
      error: () => this.toastService.error('Failed to load farmers list.')
    });
  }

  fetchCompanies() {
    this.apiService.getAdminCompanies().subscribe({
      next: (res) => { if (res.success) this.companies.set(res.data); },
      error: () => this.toastService.error('Failed to load companies list.')
    });
  }

  fetchPracticeLogs() {
    this.apiService.getAdminPracticeLogs().subscribe({
      next: (res) => { if (res.success) this.practiceLogs.set(res.data); },
      error: () => this.toastService.error('Failed to load practice logs.')
    });
  }

  fetchCarbonCredits() {
    this.apiService.getAdminCarbonCredits().subscribe({
      next: (res) => { if (res.success) this.carbonCredits.set(res.data); },
      error: () => this.toastService.error('Failed to load carbon credits.')
    });
  }

  fetchWithdrawals() {
    this.apiService.getAdminWithdrawals().subscribe({
      next: (res) => { if (res.success) this.withdrawals.set(res.data); },
      error: () => this.toastService.error('Failed to load withdrawal requests.')
    });
  }

  fetchAuditLogs() {
    this.apiService.getAdminAuditLogs().subscribe({
      next: (res) => { if (res.success) this.auditLogs.set(res.data); },
      error: () => this.toastService.error('Failed to load audit logs.')
    });
  }

  // --- Modals for Approvals/Rejections ---
  openActionModal(item: any, type: typeof this.actionType extends () => infer T ? T : any) {
    this.selectedItem.set(item);
    this.actionType.set(type);
    this.actionComments = '';
    this.actionStatus = type === 'carbon-credit' ? 'VERIFIED' : 'APPROVED';
    if (type === 'carbon-credit' || type === 'list-marketplace') {
      this.pricePerCredit = 10.0;
      this.quantity = item.co2offsetValue || item.finalCredits || 0;
      this.validUntil = '';
    }
    this.showActionModal.set(true);
  }

  closeActionModal() {
    this.showActionModal.set(false);
    this.selectedItem.set(null);
  }

  submitAction() {
    const id = this.selectedItem()?.id;
    if (!id) return;

    this.isLoading.set(true);
    let apiCall;

    switch (this.actionType()) {
      case 'farmer':
        apiCall = this.apiService.verifyFarmer(id, this.actionStatus as 'APPROVED' | 'REJECTED', this.actionComments);
        break;
      case 'company':
        apiCall = this.apiService.verifyCompany(id, this.actionStatus as 'APPROVED' | 'REJECTED', this.actionComments);
        break;
      case 'practice-log':
        apiCall = this.apiService.verifyPracticeLog(id, this.actionStatus as 'APPROVED' | 'REJECTED', this.actionComments);
        break;
      case 'carbon-credit':
        apiCall = this.apiService.verifyCarbonCredit(
          id,
          this.actionStatus as 'VERIFIED' | 'REJECTED',
          this.actionComments,
          this.actionStatus === 'VERIFIED' ? this.pricePerCredit : undefined,
          this.actionStatus === 'VERIFIED' ? this.quantity : undefined
        );
        break;
      case 'list-marketplace':
        apiCall = this.apiService.listVerifiedCredit(
          id,
          this.pricePerCredit,
          this.quantity,
          this.validUntil ? this.validUntil : undefined
        );
        break;
      case 'withdrawal-approve':
        apiCall = this.apiService.approveWithdrawal(id, this.actionComments);
        break;
      case 'withdrawal-reject':
        apiCall = this.apiService.rejectWithdrawal(id, this.actionComments);
        break;
    }

    apiCall.subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Action completed successfully.');
        this.closeActionModal();
        this.refreshData();
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Action failed.');
      }
    });
  }

  // --- Formula management ---
  openFormulaModal() {
    this.formulaData = {
      practiceType: 'TREE_PLANTATION',
      baseCoefficient: 1.5,
      maxCap: 100.0,
      version: '1.0'
    };
    this.showFormulaModal.set(true);
  }

  closeFormulaModal() {
    this.showFormulaModal.set(false);
  }

  submitFormula() {
    this.isLoading.set(true);
    this.apiService.configureFormula(
      this.formulaData.practiceType,
      this.formulaData.baseCoefficient,
      this.formulaData.maxCap,
      this.formulaData.version
    ).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Credit formula configured successfully.');
        this.closeFormulaModal();
        this.refreshData();
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Formula configuration failed.');
      }
    });
  }

  logout() {
    this.apiService.logout();
    this.router.navigate(['/auth']);
  }
}
