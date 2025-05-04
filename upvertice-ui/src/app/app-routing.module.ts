import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {authGuard} from './core/authGuard/auth-guard.service';
import {WelcomeComponent} from './modules/advertiser/pages/welcome/welcome.component';
//import {DashboardComponent} from './modules/advertiser/pages/dashboard/dashboard.component';
import {OffersComponent} from './modules/advertiser/pages/offers/offers.component';
import {AdvertiserComponent} from './modules/advertiser/advertiser/advertiser.component';
import {
  SponsorshipsListComponent
} from './modules/advertiser/pages/sponsorships/sponsorships-list/sponsorships-list.component';
import {
  SponsorshipDetailsComponent
} from './modules/advertiser/pages/sponsorships/sponsorship-details/sponsorship-details.component';
import {AdminComponent} from './modules/admin/admin/admin.component';
import {HomeComponent} from './modules/admin/pages/home/home.component';
import {DashboardComponent} from './modules/admin/pages/dashboard/dashboard.component';
import {ManagerComponent} from './modules/admin/pages/manager/manager.component';
import {RoleRedirectComponent} from './modules/role-redirect/role-redirect.component';
import {SponsorOffersComponent} from './modules/admin/pages/sponsor-offers/sponsor-offers.component';
import {SponsorAdsComponent} from './modules/admin/pages/sponsor-ads/sponsor-ads.component';
import {SponsorshipManagerComponent} from './modules/admin/pages/sponsorship-manager/sponsorship-manager.component';
import {ProvidershipManagerComponent} from './modules/admin/pages/providership-manager/providership-manager.component';
import {SupplierOffersComponent} from './modules/admin/pages/supplier-offers/supplier-offers.component';
import {
  SupplierTransactionsComponent
} from './modules/admin/pages/supplier-transactions/supplier-transactions.component';
import {UserManagementComponent} from './modules/shared/user-management/user-management.component';
import {ProviderComponent} from './modules/provider/provider.component';
import {HomeProviderComponent} from './modules/provider/pages/home-provider/home-provider.component';

import {
  SupplierTransactionDetailsAdminComponent,

} from './modules/admin/pages/supplier-transactions/supplier-transaction-details/supplier-transaction-details.component';
import {
  MyProvidershipProviderComponent
} from './modules/provider/pages/my-providership-provider/my-providership.component';
import {
  ProvidershipDetailsProviderComponent
} from './modules/provider/pages/my-providership-provider/providership-details/providership-details.component';
import {
  SupplierTransactionsProviderComponent
} from './modules/provider/pages/supplier-transactions-provider/supplier-transactions.component';
import {
  SupplierTransactionDetailsProviderComponent
} from './modules/provider/pages/supplier-transactions-provider/supplier-transaction-details/supplier-transaction-details.component';
import {
  ProvidershipDetailsAdminComponent
} from './modules/admin/pages/providership-manager/providership-details/providership-details.component';
import {SupplierComponent} from './modules/supplier/supplier/supplier.component';
import {WelcomeSupplierComponent} from './modules/supplier/pages/welcome-supplier/welcome-supplier.component';
import {
  SupplierSupplierOffersComponent
} from './modules/supplier/pages/supplier-supplier-offers/supplier-supplier-offers.component';
import {
  SupplierTransactionsSupplierComponent
} from './modules/supplier/pages/supplier-transactions-supplier/supplier-transactions.component';
import {
  SupplierTransactionDetailsSupplierComponent
} from './modules/supplier/pages/supplier-transactions-supplier/supplier-transaction-details/supplier-transaction-details.component';



const routes: Routes = [
  {
    path: '',
    pathMatch: 'full', // Ensure this route only matches the exact empty path
    canActivate: [authGuard],
    component: RoleRedirectComponent // this will redirect based on role
  },
  {
    path: 'advertiser',
    canActivate: [authGuard],
    component: AdvertiserComponent,
    children: [
      { path: '', component: WelcomeComponent },
      { path: 'offers', component: OffersComponent },
      { path: 'dashboard', component: SponsorshipsListComponent },
      { path: 'sponsorships/:id', component: SponsorshipDetailsComponent },
      { path: 'userManager', component: UserManagementComponent}
    ]
  },

  {
    path: 'admin', // Full path: /admin
    canActivate: [authGuard],
    component: AdminComponent,
    children: [
      { path: '', component: HomeComponent }, // /admin
      { path: 'dashboard', component: DashboardComponent }, // /admin/dashboard
      { path: 'sponsorOfferManager', component: SponsorOffersComponent} ,
      { path: 'sponsorAdsManager', component: SponsorAdsComponent} ,
      { path: 'sponsorshipManager', component: SponsorshipManagerComponent} ,
      { path: 'providershipManager', component:ProvidershipManagerComponent} ,
      { path: "providership-details/:id", component: ProvidershipDetailsAdminComponent },
      { path: 'supplierOfferManager', component: SupplierOffersComponent} ,
      { path: 'supplierTransactionManager', component: SupplierTransactionsComponent},
      { path: "supplier-transaction-details/:id", component: SupplierTransactionDetailsAdminComponent },
      { path: 'userManager', component: UserManagementComponent}

    ]
  }
,
  {
    path: 'provider', // Full path: /admin
    canActivate: [authGuard],
    component: ProviderComponent,
    children: [
      { path: '', component: HomeProviderComponent },
      { path: 'providerships', component: MyProvidershipProviderComponent},
      { path: "providership-details/:id", component: ProvidershipDetailsProviderComponent },
      { path: 'supplierOfferManager', component: SupplierOffersComponent},
      { path: 'supplierTransactionManager', component: SupplierTransactionsProviderComponent},
      { path: "supplier-transaction-details/:id", component: SupplierTransactionDetailsProviderComponent },
      { path: 'userManager', component: UserManagementComponent}
    ]
  },
  {
    path: 'supplier', // Full path: /admin
    canActivate: [authGuard],
    component: SupplierComponent,
    children: [
      { path: '', component: WelcomeSupplierComponent},
      { path: 'supplierOffers', component: SupplierSupplierOffersComponent},
      { path: 'supplierTransactionManagerSupplier', component: SupplierTransactionsSupplierComponent},
      { path: "supplier-transaction-details/:id", component: SupplierTransactionDetailsSupplierComponent },
      { path: 'userManager', component: UserManagementComponent}

      ]
  },
  { path: '**', redirectTo: '' } // wildcard to catch unknown routes
];



@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
