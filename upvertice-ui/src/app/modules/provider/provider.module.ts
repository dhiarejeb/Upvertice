import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProviderComponent } from './provider.component';
import {

  MyProvidershipProviderComponent
} from './pages/my-providership-provider/my-providership.component';
import {RouterLink, RouterModule, RouterOutlet} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { HomeProviderComponent } from './pages/home-provider/home-provider.component';
import { ProviderMenuComponent } from './component/provider-menu/provider-menu.component';

import {
  SupplierTransactionsProviderComponent
} from './pages/supplier-transactions-provider/supplier-transactions.component';
import {
  SupplierTransactionDetailsProviderComponent
} from './pages/supplier-transactions-provider/supplier-transaction-details/supplier-transaction-details.component';
import {
  ProvidershipDetailsProviderComponent
} from './pages/my-providership-provider/providership-details/providership-details.component';



@NgModule({
  declarations: [
    ProviderComponent,
    MyProvidershipProviderComponent,
    HomeProviderComponent,
    ProviderMenuComponent,
    ProvidershipDetailsProviderComponent,
    SupplierTransactionsProviderComponent,
    SupplierTransactionDetailsProviderComponent
  ],
  imports: [
    CommonModule,
    RouterOutlet,
    ReactiveFormsModule,
    RouterLink,
    RouterModule,
    FormsModule
  ]
})
export class ProviderModule { }
