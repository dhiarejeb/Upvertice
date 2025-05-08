import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AdminComponent} from './admin/admin.component';

import {AdminMenuComponent} from './components/admin-menu/admin-menu.component';
import {HomeComponent} from './pages/home/home.component';


import {AdminRoutingModule} from './admin-routing.module';
import { RouterModule } from '@angular/router';
import { SponsorOffersComponent } from './pages/sponsor-offers/sponsor-offers.component';
import {SponsorOfferFormDialogComponent} from './pages/sponsor-offers/sponsor-offer-form-dialog.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatSelectModule} from '@angular/material/select';
import { SponsorAdsComponent } from './pages/sponsor-ads/sponsor-ads.component';
import {SponsorAdDialogComponent} from './pages/sponsor-ads/sponsor-ad-dialog.component';
import { SponsorshipManagerComponent } from './pages/sponsorship-manager/sponsorship-manager.component';

import {MatCell, MatColumnDef, MatHeaderCell, MatHeaderRow, MatRow, MatTable} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatPaginator} from '@angular/material/paginator'; // <-- Add this
import { ColorChromeModule } from 'ngx-color/chrome';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { SponsorshipTableComponent } from './pages/sponsorship-manager/sponsorship-table/sponsorship-table.component';
import { EditSponsorshipModalComponent } from './pages/sponsorship-manager/edit-sponsorship-modal/edit-sponsorship-modal.component';
import { DeleteConfirmationModalComponent } from './pages/sponsorship-manager/delete-confirmation-modal/delete-confirmation-modal.component';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import { ProvidershipManagerComponent } from './pages/providership-manager/providership-manager.component';
import { ConfirmDialogComponent } from './pages/providership-manager/confirm-dialog/confirm-dialog.component';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatCheckbox} from '@angular/material/checkbox';
import { SupplierOffersComponent } from './pages/supplier-offers/supplier-offers.component';
import { SupplierTransactionsComponent } from './pages/supplier-transactions/supplier-transactions.component';
import {
  SupplierTransactionDetailsAdminComponent,

} from './pages/supplier-transactions/supplier-transaction-details/supplier-transaction-details.component';
import {
  ProvidershipDetailsAdminComponent
} from './pages/providership-manager/providership-details/providership-details.component';

@NgModule({
  declarations: [
    AdminComponent,
    AdminMenuComponent,
    HomeComponent,
    SponsorOffersComponent,
    SponsorOfferFormDialogComponent,
    SponsorAdsComponent,
    SponsorAdDialogComponent,
    SponsorshipManagerComponent,
    SponsorshipTableComponent,
    EditSponsorshipModalComponent,
    DeleteConfirmationModalComponent,
    ProvidershipManagerComponent,
    ConfirmDialogComponent,
    SupplierOffersComponent,
    SupplierTransactionsComponent,
    ProvidershipDetailsAdminComponent,
    SupplierTransactionDetailsAdminComponent

  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    RouterModule,
    ReactiveFormsModule,

    // Angular Material Modules
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    FormsModule,
    MatTable,
    MatProgressSpinner,
    MatCell,
    MatHeaderCell,
    MatColumnDef,
    MatIcon,
    MatHeaderRow,
    MatRow,
    MatPaginator,
    MatTableModule,
    MatPaginatorModule,
    MatTabGroup,
    MatTab,
    ColorChromeModule,
    MatProgressBar,
    MatCheckbox


  ]
})
export class AdminModule { }
