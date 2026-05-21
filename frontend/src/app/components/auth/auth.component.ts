import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './auth.html',
  styleUrl: './auth.css'
})
export class AuthComponent {
  private apiService = inject(ApiService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  // View state signals
  viewMode = signal<'login' | 'register-farmer' | 'register-company' | 'verify-otp' | 'forgot-password' | 'reset-password'>('login');
  isLoading = signal(false);

  // Form Model states
  loginData = { username: '', password: '' };
  farmerRegisterData = { email: '', mobile: '', password: '', name: '', address: '', aadhaarNumber: '' };
  companyRegisterData = { email: '', mobile: '', password: '', name: '', address: '', companyName: '', gstNumber: '', panNumber: '' };
  otpData = { target: '', code: '' };
  forgotPasswordData = { target: '' };
  resetPasswordData = { target: '', code: '', newPassword: '' };

  toggleMode(mode: typeof this.viewMode extends () => infer T ? T : any) {
    this.viewMode.set(mode);
  }

  onLogin() {
    this.isLoading.set(true);
    this.apiService.login(this.loginData).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Login successful!');
        this.navigateUser(res.data.role);
      },
      error: (err) => {
        this.isLoading.set(false);
        const errMsg = err.error?.message || 'Login failed. Please verify credentials.';
        this.toastService.error(errMsg);
      }
    });
  }

  onRegisterFarmer() {
    this.isLoading.set(true);
    this.apiService.registerFarmer(this.farmerRegisterData).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Registration initiated!');
        this.otpData.target = this.farmerRegisterData.mobile;
        this.viewMode.set('verify-otp');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Farmer registration failed.');
      }
    });
  }

  onRegisterCompany() {
    this.isLoading.set(true);
    this.apiService.registerCompany(this.companyRegisterData).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Registration initiated!');
        this.otpData.target = this.companyRegisterData.email;
        this.viewMode.set('verify-otp');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Company registration failed.');
      }
    });
  }

  onVerifyOtp() {
    this.isLoading.set(true);
    this.apiService.verifyOtp(this.otpData).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'OTP verified! Registration is complete.');
        this.loginData.username = this.otpData.target;
        this.viewMode.set('login');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Invalid or expired OTP.');
      }
    });
  }

  onForgotPassword() {
    this.isLoading.set(true);
    this.apiService.forgotPassword(this.forgotPasswordData).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'OTP sent successfully!');
        this.resetPasswordData.target = this.forgotPasswordData.target;
        this.viewMode.set('reset-password');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Failed to initiate forgot password.');
      }
    });
  }

  onResetPassword() {
    this.isLoading.set(true);
    this.apiService.resetPassword(this.resetPasswordData).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.toastService.success(res.message || 'Password reset successfully!');
        this.loginData.username = this.resetPasswordData.target;
        this.viewMode.set('login');
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error(err.error?.message || 'Failed to reset password.');
      }
    });
  }

  private navigateUser(role: string) {
    if (role === 'FARMER') {
      this.router.navigate(['/farmer']);
    } else if (role === 'COMPANY') {
      this.router.navigate(['/company']);
    } else if (role === 'ADMIN' || role === 'SUPERVISOR') {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
