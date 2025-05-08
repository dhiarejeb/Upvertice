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
export class SponsorAdsAdvertiserComponent {
  sponsorAds: SponsorAdResponse[] = [];
  page = 0;
  size = 10;
  totalPages = 0;
  pageInput = 1;

  constructor(
    private sponsorAdService: SponsorAdControllerService,
    private dialog: MatDialog,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadSponsorAds();
  }

  loadSponsorAds(): void {
    this.sponsorAdService.getAllSponsorAds({ page: this.page, size: this.size }).subscribe({
      next: (res) => {
        this.sponsorAds = res.content ?? [];
        this.totalPages = res.totalPages ?? 0;
        this.pageInput = this.page + 1;
        this.toastr.success('Sponsor ads loaded successfully!', 'Success');
      },
      error: (err) => {
        console.error('Failed to load sponsor ads', err);
        this.toastr.error('Failed to load sponsor ads. Please try again.', 'Error');
      }
    });
  }

  onDelete(adId: number): void {
    this.sponsorAdService.deleteSponsorAd({ adId }).subscribe({
      next: () => {
        this.sponsorAds = this.sponsorAds.filter(ad => ad.id !== adId);
        this.toastr.success('Sponsor ad deleted successfully!', 'Success');
      },
      error: err => {
        console.error('Delete failed', err);
        this.toastr.error('Failed to delete sponsor ad. Please try again.', 'Error');
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
        this.loadSponsorAds();
        this.toastr.success('Sponsor ad updated successfully!', 'Success');
      } else {
        this.toastr.error('Failed to update sponsor ad.', 'Error');
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


