import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';



// Modified version for your needs
/*const routes: Routes = [
  {
    path: '',
    component: WelcomeComponent,
    canActivate: [authGuard], // good: protects advertiser routes
    children: [
      //{ path: '', redirectTo: 'welcome', pathMatch: 'full' },
      //{ path: 'welcome', component: WelcomeComponent },
      { path: 'offers', component: OffersComponent , canActivate: [authGuard] },
      { path: 'dashboard', component: DashboardComponent , canActivate: [authGuard]},

      //{ path: '', pathMatch: 'full', redirectTo: 'welcome' }
    ]
  }
];*/
/*const routes: Routes = [
  {
    path: '',
    component: WelcomeComponent,
    canActivate: [authGuard],
  },
  {
    path: 'offers',
    component: OffersComponent,
    canActivate: [authGuard],
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
  },
];*/
const routes: Routes = [

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdvertiserRoutingModule { }
