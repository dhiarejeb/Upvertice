import { Component } from '@angular/core';
import {SponsorAdResponse} from '../../../../services/models/sponsor-ad-response';
import {SponsorAdControllerService} from '../../../../services/services/sponsor-ad-controller.service';
import {MatDialog} from '@angular/material/dialog';
import {SponsorAdDialogComponent} from './sponsor-ad-dialog.component';

@Component({
  selector: 'app-sponsor-ads',
  standalone: false,
  templateUrl: './sponsor-ads.component.html',
  styleUrls: ['./sponsor-ads.component.scss']
})
export class SponsorAdsComponent {
  sponsorAds: SponsorAdResponse[] = [];
  page = 0;
  size = 10;

  constructor(
    private sponsorAdService: SponsorAdControllerService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadSponsorAds();
  }

  loadSponsorAds(): void {
    this.sponsorAdService.getAllSponsorAds({ page: this.page, size: this.size }).subscribe({
      next: (res) => {
        this.sponsorAds = res.content ?? [];
      },
      error: (err) => console.error('Failed to load sponsor ads', err)
    });
  }

  onDelete(adId: number): void {
    this.sponsorAdService.deleteSponsorAd({ adId }).subscribe({
      next: () => {
        this.sponsorAds = this.sponsorAds.filter(ad => ad.id !== adId);
      },
      error: err => console.error('Delete failed', err)
    });
  }

  onEdit(ad: SponsorAdResponse): void {
    const dialogRef = this.dialog.open(SponsorAdDialogComponent, {
      width: '600px',
      data: ad
    });

    dialogRef.afterClosed().subscribe(updated => {
      if (updated) this.loadSponsorAds();
    });
  }
}


