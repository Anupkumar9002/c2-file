import { Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth.component';
import { FarmerComponent } from './components/farmer/farmer.component';
import { CompanyComponent } from './components/company/company.component';
import { AdminComponent } from './components/admin/admin.component';

export const routes: Routes = [
  { path: '', redirectTo: 'auth', pathMatch: 'full' },
  { path: 'auth', component: AuthComponent },
  { path: 'farmer', component: FarmerComponent },
  { path: 'company', component: CompanyComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: 'auth' }
];
