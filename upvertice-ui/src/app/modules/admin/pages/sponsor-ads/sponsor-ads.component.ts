import { Component } from '@angular/core';
import {SponsorAdResponse} from '../../../../services/models/sponsor-ad-response';
import {SponsorAdControllerService} from '../../../../services/services/sponsor-ad-controller.service';
import {MatDialog} from '@angular/material/dialog';
import {SponsorAdDialogComponent} from './sponsor-ad-dialog.component';
import {ToastrService} from 'ngx-toastr';

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
  totalPages = 0;
  pageInput = 1;
  loading = false;

  constructor(
    private sponsorAdService: SponsorAdControllerService,
    private dialog: MatDialog,
    private toastService: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadSponsorAds();
  }

  loadSponsorAds(): void {
    this.loading = true;
    this.sponsorAdService.getAllSponsorAds({ page: this.page, size: this.size }).subscribe({
      next: (res) => {
        this.sponsorAds = res.content ?? [];
        this.totalPages = res.totalPages ?? 0;
        this.pageInput = this.page + 1;
        this.toastService.success('Sponsor ads loaded successfully');
      },
      error: (err) => {
        console.error('Failed to load sponsor ads', err);
        this.toastService.error('Failed to load sponsor ads');
      },
      complete: () => this.loading = false
    });
  }

  onDelete(adId: number): void {
    this.sponsorAdService.deleteSponsorAd({ adId }).subscribe({
      next: () => {
        this.sponsorAds = this.sponsorAds.filter(ad => ad.id !== adId);
        this.toastService.success('Sponsor ad deleted successfully');
      },
      error: err => {
        console.error('Delete failed', err);
        this.toastService.error('Failed to delete sponsor ad');
      }
    });
  }

  onEdit(ad: SponsorAdResponse): void {
    const dialogRef = this.dialog.open(SponsorAdDialogComponent, {
      width: '600px',
      data: ad
    });

    dialogRef.afterClosed().subscribe(updated => {
      if (updated) {
        this.toastService.success('Sponsor ad updated successfully');
        this.loadSponsorAds();
      }
    });
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadSponsorAds();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadSponsorAds();
    }
  }

  goToPage(page: number): void {
    const newPage = page - 1;
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadSponsorAds();
    }
  }

}


