import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SupplierSupplierOffersComponent } from './pages/supplier-supplier-offers/supplier-supplier-offers.component';
import { SupplierComponent } from './supplier/supplier.component';
import { SupplierMenuComponent } from './component/supplier-menu/supplier-menu.component';
import {RouterLink, RouterModule, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import { WelcomeSupplierComponent } from './pages/welcome-supplier/welcome-supplier.component';
import {
  SupplierTransactionsSupplierComponent
} from './pages/supplier-transactions-supplier/supplier-transactions.component';
import {
  SupplierTransactionDetailsSupplierComponent
} from './pages/supplier-transactions-supplier/supplier-transaction-details/supplier-transaction-details.component';



@NgModule({
  declarations: [
    SupplierSupplierOffersComponent,
    SupplierComponent,
    SupplierMenuComponent,
    WelcomeSupplierComponent,
    SupplierTransactionsSupplierComponent,
    SupplierTransactionDetailsSupplierComponent
  ],
  imports: [
    CommonModule,
    RouterOutlet,
    FormsModule,
    RouterModule,
    RouterLink
  ]
})
export class SupplierModule { }
