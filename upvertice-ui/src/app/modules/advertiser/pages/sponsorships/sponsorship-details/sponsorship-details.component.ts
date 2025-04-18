import {Component, OnInit} from '@angular/core';
import {SponsorshipControllerService} from '../../../../../services/services/sponsorship-controller.service';
import {ActivatedRoute} from '@angular/router';
import {SponsorshipResponse} from '../../../../../services/models/sponsorship-response';
import {ProvidershipResponse} from '../../../../../services/models/providership-response';
import {SupplierTransactionResponse} from '../../../../../services/models/supplier-transaction-response';

@Component({
  selector: 'app-sponsorship-details',
  standalone: false,
  templateUrl: './sponsorship-details.component.html',
  styleUrls: ['./sponsorship-details.component.scss']
})
export class SponsorshipDetailsComponent implements OnInit {
  sponsorship?: SponsorshipResponse;

  constructor(
    private route: ActivatedRoute,
    private sponsorshipService: SponsorshipControllerService
  ) {}

  ngOnInit(): void {
    const sponsorshipId = Number(this.route.snapshot.paramMap.get('id'));
    this.sponsorshipService.getSponsorshipById({ sponsorshipId }).subscribe({
      next: res => this.sponsorship = res
    });
  }

  getProviderProgress(p?: ProvidershipResponse): number {
    return (p?.totalProduct ?? 0) > 0
      ? Math.round(((p?.producedProduct ?? 0) / (p?.totalProduct ?? 0)) * 100)
      : 0;
  }

  getSupplierProgress(tx: SupplierTransactionResponse): number {
    const total = tx.supplierOffer?.quantityAvailable ?? 0;
    const sold = tx.quantitySold ?? 0;
    return total > 0 ? Math.round((sold / total) * 100) : 0;
  }
}

