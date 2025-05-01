import {NgModule} from '@angular/core';
import {CommonModule, DatePipe, DecimalPipe} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms'

import {AdvertiserRoutingModule} from './advertiser-routing.module';

import {WelcomeComponent} from './pages/welcome/welcome.component';
import {OffersComponent} from './pages/offers/offers.component';

import {AdvertiserMenuComponent} from './components/advertiser-menu/advertiser-menu.component';
import {AdvertiserComponent} from './advertiser/advertiser.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatIconModule} from '@angular/material/icon';
import {BaseChartDirective} from 'ng2-charts';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';


import { SponsorshipsListComponent } from './pages/sponsorships/sponsorships-list/sponsorships-list.component';
import {SponsorshipDetailsComponent} from './pages/sponsorships/sponsorship-details/sponsorship-details.component';


@NgModule({
  declarations: [
    WelcomeComponent,
    OffersComponent,
    AdvertiserMenuComponent,
    AdvertiserComponent,
    SponsorshipDetailsComponent,
    //DashboardComponent,
    SponsorshipsListComponent,


  ],
  imports: [
    CommonModule,
    AdvertiserRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    DatePipe,
    DecimalPipe,
    MatExpansionModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    BaseChartDirective,
    MatProgressBarModule,


  ],
  providers: [
    DatePipe,
    DecimalPipe
  ]
})
export class AdvertiserModule { }
