import {Component, OnInit} from '@angular/core';
import {SponsorOfferResponse} from '../../../../services/models/sponsor-offer-response';
import {SponsorOfferControllerService} from '../../../../services/services/sponsor-offer-controller.service';
import {SponsorshipControllerService} from '../../../../services/services/sponsorship-controller.service';
import {PageResponseSponsorOfferResponse} from '../../../../services/models/page-response-sponsor-offer-response';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {PageResponseSponsorshipResponse} from '../../../../services/models/page-response-sponsorship-response';
import {SponsorOfferMultipartChooseRequest} from '../../../../services/models/sponsor-offer-multipart-choose-request';
import {ToastrService} from 'ngx-toastr';


@Component({
  selector: 'app-offers',
  standalone: false,
  templateUrl: './offers.component.html',
  styleUrls: ['./offers.component.scss']
})
export class OffersComponent implements OnInit {
  userSponsorships: Map<number, number> = new Map<number, number>();
  sponsorOffers: SponsorOfferResponse[] = [];
  selectedOfferId: number | null = null;
  createdSponsorshipId: number | null = null;
  currentSponsorshipOfferId: number | null = null;
  adForm: FormGroup;
  selectedImageFile: File | null = null;
  loading: boolean = true;
  isSubmitting: boolean = false;
  page: number = 0;
  size: number = 4;
  totalPages: number = 0;
  totalElements: number = 0;
  apiUrl = 'http://localhost:8088/api/v1';

  constructor(
    private fb: FormBuilder,
    private offerService: SponsorOfferControllerService,
    private sponsorshipService: SponsorshipControllerService,
    private http: HttpClient,
    private toastService: ToastrService  // Inject ToastrService
  ) {
    this.adForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadOffers();
    this.checkUserActiveSponsorship();
  }

  ngOnDestroy(): void {
    // Cleanup, if needed
  }

  loadOffers(): void {
    this.loading = true;
    this.offerService.getAllSponsorOffers({ page: this.page, size: this.size }).subscribe({
      next: (res: PageResponseSponsorOfferResponse) => {
        this.sponsorOffers = res.content || [];
        this.totalPages = res.totalPages || 0;
        this.totalElements = res.totalElements || 0;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load sponsor offers.', err);
        this.loading = false;
        this.showToastMessage('Error', 'Failed to load sponsor offers.', 'error');
      }
    });
  }

  onChoose(offer: SponsorOfferResponse): void {
    if (!offer.id) {
      console.error('Offer ID is undefined.');
      this.showToastMessage('Error', 'Invalid offer selected.', 'error');
      return;
    }
    this.selectedOfferId = offer.id;
    this.adForm.reset();
    this.selectedImageFile = null;
  }

  onImageSelected(event: Event): void {
    const inputEl = event.target as HTMLInputElement;
    if (inputEl.files && inputEl.files.length > 0) {
      this.selectedImageFile = inputEl.files[0];
    } else {
      this.selectedImageFile = null;
    }
  }

  onSubmitAd(offerId: number): void {
    if (this.adForm.invalid) {
      return;
    }

    const sponsorAdRequest = {
      title: this.adForm.value.title,
      content: this.adForm.value.content,
      designColors: this.adForm.value.designColors || []
    };

    const requestString = JSON.stringify(sponsorAdRequest);
    const multipartRequest: SponsorOfferMultipartChooseRequest = {
      request: requestString,
      images: this.selectedImageFile ? [this.selectedImageFile] : []
    };

    const params = {
      offerId,
      body: multipartRequest
    };

    this.isSubmitting = true;

    this.offerService.chooseSponsorOffer(params).subscribe({
      next: (sponsorshipId: number) => {
        console.log('Sponsorship created with id', sponsorshipId);
        this.userSponsorships.set(offerId, sponsorshipId);
        this.createdSponsorshipId = sponsorshipId;
        this.currentSponsorshipOfferId = offerId;
        this.selectedOfferId = null;
        this.showToastMessage('Success', 'Sponsorship created successfully!', 'success');
        this.loadOffers();
        this.isSubmitting = false;
      },
      error: (err) => {
        console.error('Error creating sponsorship:', err);
        this.showToastMessage('Error', 'Failed to create sponsorship.', 'error');
        this.isSubmitting = false;
      }
    });
  }

  trySubmitWithoutImage(offerId: number, sponsorAdRequest: any): void {
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(sponsorAdRequest)], { type: 'application/json' }));

    const url = `${this.apiUrl}/sponsor-offers/chooseSponsorOffer/${offerId}`;

    this.http.post<number>(url, formData).subscribe({
      next: (sponsorshipId: number) => {
        console.log('Sponsorship created with id (alternative method)', sponsorshipId);
        this.createdSponsorshipId = sponsorshipId;
        this.currentSponsorshipOfferId = offerId;
        this.selectedOfferId = null;
        this.showToastMessage('Success', 'Sponsorship created successfully!', 'success');
        this.loadOffers();
        this.isSubmitting = false;
      },
      error: (err) => {
        console.error('Error creating sponsorship (alternative method):', err);
        this.showToastMessage('Error', 'Failed to create sponsorship.', 'error');
        this.isSubmitting = false;
      }
    });
  }

  onUndo(offerId: number, sponsorshipId: number): void {
    this.sponsorshipService.deleteSponsorship({ sponsorshipId }).subscribe({
      next: () => {
        console.log('Sponsorship undone successfully.');
        this.showToastMessage('Success', 'Sponsorship undone successfully!', 'success');
        this.loadOffers();
      },
      error: (err) => {
        console.error('Failed to undo sponsorship:', err);
        if (err.status === 404) {
          this.showToastMessage('Success', 'Sponsorship undone successfully!', 'success');
          this.loadOffers();
        } else {
          this.showToastMessage('Warning', 'The sponsorship may have been removed, but there was an error in the response.', 'error');
          this.loadOffers();
        }
      }
    });
  }

  onCancelForm(): void {
    this.selectedOfferId = null;
    this.adForm.reset();
    this.selectedImageFile = null;
  }

  onPageChange(page: number): void {
    this.page = page;
    this.loadOffers();
  }

  showToastMessage(title: string, message: string, type: 'success' | 'error'): void {
    if (type === 'success') {
      this.toastService.success(message, title);
    } else {
      this.toastService.error(message, title);
    }
  }

  getStatusClass(status: string): string {
    if (!status) return '';

    const lowerStatus = status.toLowerCase();

    if (lowerStatus.includes('available')) {
      return 'status-available';
    } else if (lowerStatus.includes('pending')) {
      return 'status-pending';
    } else if (lowerStatus.includes('sold')) {
      return 'status-sold';
    } else if (lowerStatus.includes('expired')) {
      return 'status-expired';
    }

    return '';
  }

  checkUserActiveSponsorship(): void {
    this.sponsorshipService.getAllSponsorships({ page: 0, size: 100 }).subscribe({
      next: (response: PageResponseSponsorshipResponse) => {
        this.userSponsorships.clear();

        // Check if response.content is defined and filter it
        const activeSponshorships = (response.content || []).filter(s =>
          s.status?.toUpperCase() === 'PENDING' ||
          s.status?.toUpperCase() === 'APPROVED'
        );

        activeSponshorships.forEach(sponsorship => {
          if (sponsorship.sponsorOffer?.id) {
            this.userSponsorships.set(sponsorship.sponsorOffer.id, sponsorship.id || 0);
          }
        });

        if (activeSponshorships.length > 0 && activeSponshorships[0].sponsorOffer?.id) {
          this.createdSponsorshipId = activeSponshorships[0].id || null;
          this.currentSponsorshipOfferId = activeSponshorships[0].sponsorOffer.id;
        } else {
          this.createdSponsorshipId = null;
          this.currentSponsorshipOfferId = null;
        }
      },
      error: (err) => {
        console.error('Failed to check user sponsorships:', err);
        this.userSponsorships.clear();
        this.createdSponsorshipId = null;
        this.currentSponsorshipOfferId = null;
      }
    });
  }

  hasActiveSponsorship(offerId: number): boolean {
    return this.userSponsorships.has(offerId);
  }

  getSponsorshipId(offerId: number): number {
    return this.userSponsorships.get(offerId) || 0;
  }

  // Add these methods to your component class to handle carousel navigation
// Store the current image index for each carousel
  carouselIndices: { [key: number]: number } = {};

// Navigate to previous image
  prevImage(offerId: number): void {
    if (!this.carouselIndices[offerId]) {
      this.carouselIndices[offerId] = 0;
    }

    const offer = this.sponsorOffers.find(o => o.id === offerId);
    if (!offer || !offer.explainImages || offer.explainImages.length <= 1) return;

    this.carouselIndices[offerId] = (this.carouselIndices[offerId] - 1 + offer.explainImages.length) % offer.explainImages.length;

    // Force update the carousel
    this.updateCarousel(offerId);
  }

// Navigate to next image
  nextImage(offerId: number): void {
    if (!this.carouselIndices[offerId]) {
      this.carouselIndices[offerId] = 0;
    }

    const offer = this.sponsorOffers.find(o => o.id === offerId);
    if (!offer || !offer.explainImages || offer.explainImages.length <= 1) return;

    this.carouselIndices[offerId] = (this.carouselIndices[offerId] + 1) % offer.explainImages.length;

    // Force update the carousel
    this.updateCarousel(offerId);
  }

// Update carousel DOM elements
  updateCarousel(offerId: number): void {
    setTimeout(() => {
      const carouselId = `carousel-${offerId}`;
      const carousel = document.getElementById(carouselId);
      if (!carousel) return;

      const items = carousel.querySelectorAll('.carousel-item');
      items.forEach((item, index) => {
        if (index === this.carouselIndices[offerId]) {
          item.classList.add('active');
        } else {
          item.classList.remove('active');
        }
      });
    }, 0);
  }
}




