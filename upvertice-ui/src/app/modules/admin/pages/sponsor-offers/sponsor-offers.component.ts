import {Component, OnInit} from '@angular/core';
import {SponsorOfferResponse} from '../../../../services/models/sponsor-offer-response';
import {SponsorOfferControllerService} from '../../../../services/services/sponsor-offer-controller.service';
import {MatDialog} from '@angular/material/dialog';
import {SponsorOfferFormDialogComponent} from './sponsor-offer-form-dialog.component';
import { ToastrService } from 'ngx-toastr';
@Component({
  selector: 'app-sponsor-offers',
  standalone: false,
  templateUrl: './sponsor-offers.component.html',
  styleUrls: ['./sponsor-offers.component.scss']
})
export class SponsorOffersComponent implements OnInit{
  offers: SponsorOfferResponse[] = [];
  page = 0;
  size = 5;
  totalPages = 0;

  constructor(
    private sponsorOfferService: SponsorOfferControllerService,
    private dialog: MatDialog,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchOffers();
  }

  fetchOffers(): void {
    this.sponsorOfferService.getAllSponsorOffers({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (res) => {
        this.offers = res.content || [];
        this.totalPages = res.totalPages || 0;
      },
      error: (err) => {
        this.toastr.error('Failed to load sponsor offers', 'Error');
        console.error('Failed to load sponsor offers:', err);
      }
    });
  }

  openFormDialog(offer?: SponsorOfferResponse): void {
    const dialogRef = this.dialog.open(SponsorOfferFormDialogComponent, {
      width: '600px',
      data: offer || null,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.page = 0;
        this.fetchOffers();
        this.toastr.success('Offer saved successfully');
      }
    });
  }

  deleteOffer(id: number): void {
    if (confirm('Are you sure you want to delete this offer?')) {
      this.sponsorOfferService.deleteSponsorOffer({ offerId: id }).subscribe({
        next: () => {
          this.toastr.success('Offer deleted successfully');
          this.fetchOffers();
        },
        error: (err) => {
          this.toastr.error('Failed to delete offer', 'Error');
          console.error('Delete failed:', err);
        }
      });
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.fetchOffers();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.fetchOffers();
    }
  }

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.fetchOffers();
    }
  }

}
