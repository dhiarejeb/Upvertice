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

const routes: Routes = [
  {
    path: '',
    redirectTo: 'advertiser',  // Default route that redirects to the Advertiser module
    pathMatch: 'full',
  },
  {
    path: 'advertiser',
    component: AdvertiserComponent , //AdvertiserComponent, // 👈 layout with <router-outlet>
    canActivate: [authGuard],
    children: [
      { path: '', component: WelcomeComponent , canActivate: [authGuard]},           // /advertiser
      //{ path: 'dashboard', component: DashboardComponent ,canActivate: [authGuard]},
      { path: 'offers', component: OffersComponent ,canActivate: [authGuard] },
      {
        path: 'dashboard',
        component: SponsorshipsListComponent
      },
      {
        path: 'sponsorships/:id',
        component: SponsorshipDetailsComponent
      }
    ]
  }
  //  {
  //   path: 'advertiser',
  //    component: AdvertiserComponent,  // Eagerly load AdvertiserComponent
  // },
   //{
    // path: 'advertiser/welcome',
    // component: WelcomeComponent,  // Eagerly load AdvertiserComponent
   //},
  /*{
    path: 'advertiser',
    loadChildren: () =>
      import('./modules/advertiser/advertiser.module').then(m => m.AdvertiserModule),
    canActivate: [authGuard],  // Optionally protect the entire Advertiser module
  },*/
  // {
  //   path: '**',  // Catch-all route for invalid paths
  //   redirectTo: 'advertiser',  // You can also redirect to a 404 page
  // }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
