import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import {SponsorshipControllerService} from '../../../../services/services/sponsorship-controller.service';
import {PageResponseSponsorshipResponse} from '../../../../services/models/page-response-sponsorship-response';

import {SponsorshipResponse} from '../../../../services/models/sponsorship-response';
import {MatSnackBar} from '@angular/material/snack-bar';
import {EditSponsorshipModalComponent} from './edit-sponsorship-modal/edit-sponsorship-modal.component';
import {DeleteConfirmationModalComponent} from './delete-confirmation-modal/delete-confirmation-modal.component';
import {PatchSponsorship$Params} from '../../../../services/fn/sponsorship-controller/patch-sponsorship';
import {SponsorshipPatchMultipartRequest} from '../../../../services/models/sponsorship-patch-multipart-request';
import {ToastrService} from 'ngx-toastr';





@Component({
  selector: 'app-sponsorship-manager',
  templateUrl: './sponsorship-manager.component.html',
  standalone : false ,
  styleUrls: ['./sponsorship-manager.component.scss']
})
export class SponsorshipManagerComponent implements OnInit {
  sponsorships: SponsorshipResponse[] = [];
  loading = true;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  activeTab = 'all';

  constructor(
    private sponsorshipService: SponsorshipControllerService,
    private dialog: MatDialog,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.fetchSponsorships();
  }

  fetchSponsorships(page = 0): void {
    this.loading = true;

    const fetch$ = this.activeTab === 'all'
      ? this.sponsorshipService.getAllSponsorships({ page, size: this.pageSize })
      : this.sponsorshipService.getSponsorshipsByStatus({
        status: this.activeTab.toUpperCase() as 'PENDING' | 'APPROVED' | 'REJECTED' | 'FINISHED',
        page,
        size: this.pageSize
      });

    fetch$.subscribe({
      next: (response) => this.handleSponsorshipsResponse(response),
      error: (error) => this.handleError('Failed to load sponsorships', error)
    });
  }

  private handleSponsorshipsResponse(response: PageResponseSponsorshipResponse): void {
    this.sponsorships = response.content || [];
    this.totalPages = response.totalPages || 0;
    this.currentPage = response.number || 0;
    this.loading = false;
  }

  onTabChange(tab: string): void {
    this.activeTab = tab;
    this.fetchSponsorships(0);
  }

  onPageChange(page: number): void {
    this.fetchSponsorships(page);
  }

  openEditModal(sponsorship: SponsorshipResponse): void {
    const dialogRef = this.dialog.open(EditSponsorshipModalComponent, {
      width: '600px',
      data: { sponsorship }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result.type === 'status') {
          this.updateSponsorshipStatus(sponsorship.id!, result.status);
        } else if (result.type === 'ad') {
          this.updateSponsorshipAd(sponsorship.id!, result.sponsorAd, result.image);
        }
      }
    });
  }

  openDeleteModal(sponsorship: SponsorshipResponse): void {
    const dialogRef = this.dialog.open(DeleteConfirmationModalComponent, {
      width: '400px',
      data: { sponsorshipId: sponsorship.id }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteSponsorship(sponsorship.id!);
      }
    });
  }

  updateSponsorshipStatus(sponsorshipId: number, newStatus: string): void {
    const patchRequest: SponsorshipPatchMultipartRequest = {
      request: JSON.stringify({ newStatus })
    };

    this.sponsorshipService.patchSponsorship({
      sponsorshipId,
      body: patchRequest
    }).subscribe({
      next: () => {
        this.toastr.success(`Sponsorship status updated to ${newStatus}`);
        this.fetchSponsorships(this.currentPage);
      },
      error: (error) => this.handleError('Failed to update sponsorship status', error)
    });
  }

  updateSponsorshipAd(sponsorshipId: number, sponsorAd: any, image?: File): void {
    const patchRequest: SponsorshipPatchMultipartRequest = {
      request: JSON.stringify({ sponsorAdData: sponsorAd })
    };
    if (image) {
      patchRequest.image = image;
    }

    this.sponsorshipService.patchSponsorship({
      sponsorshipId,
      body: patchRequest
    }).subscribe({
      next: () => {
        this.toastr.success('Sponsorship ad updated successfully');
        this.fetchSponsorships(this.currentPage);
      },
      error: (error) => this.handleError('Failed to update sponsorship ad', error)
    });
  }

  deleteSponsorship(sponsorshipId: number): void {
    this.sponsorshipService.deleteSponsorship({ sponsorshipId }).subscribe({
      next: () => {
        this.toastr.success(`Sponsorship ID ${sponsorshipId} deleted successfully`);
        this.fetchSponsorships(this.currentPage);
      },
      error: (error) => this.handleError('Failed to delete sponsorship', error)
    });
  }

  private handleError(message: string, error: any): void {
    console.error(message, error);
    this.toastr.error(message, 'Error');
    this.loading = false;
  }
}
