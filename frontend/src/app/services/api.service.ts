import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface User {
  email: string;
  name: string;
  role: 'FARMER' | 'COMPANY' | 'ADMIN' | 'SUPERVISOR';
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080';

  // Auth state signals
  token = signal<string | null>(localStorage.getItem('token'));
  currentUser = signal<User | null>(null);
  
  isAuthenticated = computed(() => !!this.token());
  isAdmin = computed(() => this.currentUser()?.role === 'ADMIN' || this.currentUser()?.role === 'SUPERVISOR');
  isFarmer = computed(() => this.currentUser()?.role === 'FARMER');
  isCompany = computed(() => this.currentUser()?.role === 'COMPANY');

  constructor(private http: HttpClient) {
    // Attempt to restore user profile from local storage
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        this.currentUser.set(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('currentUser');
      }
    }
  }

  private getHeaders() {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const t = this.token();
    if (t) {
      headers = headers.set('Authorization', `Bearer ${t}`);
    }
    return headers;
  }

  // --- Auth APIs ---
  registerFarmer(body: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/v1/auth/register/farmer`, body);
  }

  registerCompany(body: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/v1/auth/register/company`, body);
  }

  verifyOtp(body: { target: string; code: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/v1/auth/verify-otp`, body);
  }

  login(body: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/api/v1/auth/login`, body).pipe(
      tap(res => {
        if (res.success && res.data) {
          const t = res.data.token;
          const user: User = {
            email: res.data.email,
            name: res.data.name,
            role: res.data.role
          };
          localStorage.setItem('token', t);
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.token.set(t);
          this.currentUser.set(user);
        }
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.token.set(null);
    this.currentUser.set(null);
  }

  forgotPassword(body: { target: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/v1/auth/forgot-password`, body);
  }

  resetPassword(body: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/v1/auth/reset-password`, body);
  }

  updateProfile(name: string, address: string): Observable<any> {
    const params = new HttpParams().set('name', name).set('address', address);
    return this.http.put(`${this.baseUrl}/api/v1/auth/profile`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  // --- Farmer Dashboard APIs ---
  registerParcel(parcelName: string, areaInAcres: number, location: string, landType: string): Observable<any> {
    const params = new HttpParams()
      .set('parcelName', parcelName)
      .set('areaInAcres', areaInAcres.toString())
      .set('location', location)
      .set('landType', landType);
    return this.http.post(`${this.baseUrl}/api/v1/farmer/parcels`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  editParcel(id: number, parcelName?: string, areaInAcres?: number, location?: string, landType?: string): Observable<any> {
    let params = new HttpParams();
    if (parcelName) params = params.set('parcelName', parcelName);
    if (areaInAcres) params = params.set('areaInAcres', areaInAcres.toString());
    if (location) params = params.set('location', location);
    if (landType) params = params.set('landType', landType);

    return this.http.put(`${this.baseUrl}/api/v1/farmer/parcels/${id}`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  getMyParcels(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/farmer/parcels`, { headers: this.getHeaders() });
  }

  logPractice(
    parcelId: number,
    practiceType: string,
    cropCategory: string,
    growingSeason: string,
    quantity: number,
    sowingDate: string,
    harvestingDate: string,
    proofDocumentBase64?: string
  ): Observable<any> {
    const params = new HttpParams()
      .set('parcelId', parcelId.toString())
      .set('practiceType', practiceType)
      .set('cropCategory', cropCategory)
      .set('growingSeason', growingSeason)
      .set('quantity', quantity.toString())
      .set('sowingDate', sowingDate)
      .set('harvestingDate', harvestingDate);

    // Pass the raw base64 string directly in the request body
    const body = proofDocumentBase64 || '';
    const headers = this.getHeaders().set('Content-Type', 'text/plain');

    return this.http.post(`${this.baseUrl}/api/v1/farmer/practice-logs`, body, {
      headers,
      params
    });
  }

  resubmitPracticeLog(
    id: number,
    practiceType?: string,
    cropCategory?: string,
    growingSeason?: string,
    quantity?: number,
    sowingDate?: string,
    harvestingDate?: string,
    proofDocumentBase64?: string
  ): Observable<any> {
    let params = new HttpParams();
    if (practiceType) params = params.set('practiceType', practiceType);
    if (cropCategory) params = params.set('cropCategory', cropCategory);
    if (growingSeason) params = params.set('growingSeason', growingSeason);
    if (quantity) params = params.set('quantity', quantity.toString());
    if (sowingDate) params = params.set('sowingDate', sowingDate);
    if (harvestingDate) params = params.set('harvestingDate', harvestingDate);

    const body = proofDocumentBase64 || '';
    const headers = this.getHeaders().set('Content-Type', 'text/plain');

    return this.http.put(`${this.baseUrl}/api/v1/farmer/practice-logs/${id}`, body, {
      headers,
      params
    });
  }

  getMyPracticeLogs(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/farmer/practice-logs`, { headers: this.getHeaders() });
  }

  getFarmerWallet(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/farmer/wallet`, { headers: this.getHeaders() });
  }

  requestWithdrawal(amount: number, bankDetails?: string): Observable<any> {
    let params = new HttpParams().set('amount', amount.toString());
    if (bankDetails) params = params.set('bankDetails', bankDetails);
    
    return this.http.post(`${this.baseUrl}/api/v1/farmer/withdraw`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  getFarmerWithdrawalHistory(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/farmer/withdrawals`, { headers: this.getHeaders() });
  }

  getFarmerDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/farmer/dashboard`, { headers: this.getHeaders() });
  }

  getCarbonAssets(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/farmer/carbon-assets`, { headers: this.getHeaders() });
  }

  // --- Company Dashboard / Marketplace APIs ---
  searchListings(filters: { practiceType?: string; minPrice?: number; maxPrice?: number; location?: string }): Observable<any> {
    let params = new HttpParams();
    if (filters.practiceType) params = params.set('practiceType', filters.practiceType);
    if (filters.minPrice !== undefined) params = params.set('minPrice', filters.minPrice.toString());
    if (filters.maxPrice !== undefined) params = params.set('maxPrice', filters.maxPrice.toString());
    if (filters.location) params = params.set('location', filters.location);

    return this.http.get(`${this.baseUrl}/api/v1/company/listings`, {
      headers: this.getHeaders(),
      params
    });
  }

  getListingDetails(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/company/listings/${id}`, { headers: this.getHeaders() });
  }

  getCart(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/company/cart`, { headers: this.getHeaders() });
  }

  addToCart(listingId: number): Observable<any> {
    const params = new HttpParams().set('listingId', listingId.toString());
    return this.http.post(`${this.baseUrl}/api/v1/company/cart`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  removeFromCart(listingId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/v1/company/cart/${listingId}`, { headers: this.getHeaders() });
  }

  purchaseListing(listingId: number): Observable<any> {
    const params = new HttpParams().set('listingId', listingId.toString());
    return this.http.post(`${this.baseUrl}/api/v1/company/purchase`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  getCompanyPurchaseHistory(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/company/purchases`, { headers: this.getHeaders() });
  }

  toggleFavourite(listingId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/v1/company/favourites/${listingId}`, null, { headers: this.getHeaders() });
  }

  getFavourites(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/company/favourites`, { headers: this.getHeaders() });
  }

  getCompanyCertificates(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/company/certificates`, { headers: this.getHeaders() });
  }

  getCompanyDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/company/dashboard`, { headers: this.getHeaders() });
  }

  // --- Admin Dashboard APIs ---
  getAdminFarmers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/farmers`, { headers: this.getHeaders() });
  }

  getAdminCompanies(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/companies`, { headers: this.getHeaders() });
  }

  getAdminPracticeLogs(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/practice-logs`, { headers: this.getHeaders() });
  }

  getAdminCarbonCredits(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/carbon-credits`, { headers: this.getHeaders() });
  }

  getAdminWithdrawals(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/withdrawals`, { headers: this.getHeaders() });
  }

  getAdminAuditLogs(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/audit-logs`, { headers: this.getHeaders() });
  }

  getAdminDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/v1/admin/dashboard`, { headers: this.getHeaders() });
  }

  verifyFarmer(farmerId: number, status: 'APPROVED' | 'REJECTED', comments: string): Observable<any> {
    const params = new HttpParams()
      .set('farmerId', farmerId.toString())
      .set('status', status)
      .set('comments', comments);
    return this.http.post(`${this.baseUrl}/api/v1/admin/verify/farmer`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  verifyCompany(companyId: number, status: 'APPROVED' | 'REJECTED', comments: string): Observable<any> {
    const params = new HttpParams()
      .set('companyId', companyId.toString())
      .set('status', status)
      .set('comments', comments);
    return this.http.post(`${this.baseUrl}/api/v1/admin/verify/company`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  verifyPracticeLog(logId: number, status: 'APPROVED' | 'REJECTED', comments: string): Observable<any> {
    const params = new HttpParams()
      .set('logId', logId.toString())
      .set('status', status)
      .set('comments', comments);
    return this.http.post(`${this.baseUrl}/api/v1/admin/verify/practice-log`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  verifyCarbonCredit(creditId: number, status: 'APPROVED' | 'REJECTED' | 'CANCELED', comments: string): Observable<any> {
    const params = new HttpParams()
      .set('creditId', creditId.toString())
      .set('status', status)
      .set('comments', comments);
    return this.http.post(`${this.baseUrl}/api/v1/admin/verify/carbon-credit`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  approveWithdrawal(withdrawalId: number, comments: string): Observable<any> {
    const params = new HttpParams()
      .set('withdrawalId', withdrawalId.toString())
      .set('comments', comments);
    return this.http.post(`${this.baseUrl}/api/v1/admin/verify/withdrawal/approve`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  rejectWithdrawal(withdrawalId: number, comments: string): Observable<any> {
    const params = new HttpParams()
      .set('withdrawalId', withdrawalId.toString())
      .set('comments', comments);
    return this.http.post(`${this.baseUrl}/api/v1/admin/verify/withdrawal/reject`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  configureFormula(practiceType: string, baseCoefficient: number, maxCap: number, version: string): Observable<any> {
    const params = new HttpParams()
      .set('practiceType', practiceType)
      .set('baseCoefficient', baseCoefficient.toString())
      .set('maxCap', maxCap.toString())
      .set('version', version);
    return this.http.post(`${this.baseUrl}/api/v1/admin/formula`, null, {
      headers: this.getHeaders(),
      params
    });
  }

  // --- Public Certificate Retrieval API ---
  getPublicCertificatePdfUrl(certificateId: string): string {
    return `${this.baseUrl}/api/v1/public/certificates/${certificateId}/pdf`;
  }
}
