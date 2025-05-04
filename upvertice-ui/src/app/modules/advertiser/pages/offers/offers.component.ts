
import {Component, OnInit} from '@angular/core';
import {SponsorOfferResponse} from '../../../../services/models/sponsor-offer-response';

import { Subscription, timer , finalize } from 'rxjs';
import {SponsorOfferControllerService} from '../../../../services/services/sponsor-offer-controller.service';
import {SponsorshipControllerService} from '../../../../services/services/sponsorship-controller.service';
import {PageResponseSponsorOfferResponse} from '../../../../services/models/page-response-sponsor-offer-response';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {PageResponseSponsorshipResponse} from '../../../../services/models/page-response-sponsorship-response';
import {SponsorOfferMultipartRequest} from '../../../../services/models/sponsor-offer-multipart-request';



@Component({
  selector: 'app-offers',
  standalone: false,
  templateUrl: './offers.component.html',
  styleUrls: ['./offers.component.scss']
})
export class OffersComponent implements OnInit {
  // Add this property to track multiple sponsorships
  userSponsorships: Map<number, number> = new Map<number, number>(); // Map<offerId, sponsorshipId>
  // List of sponsor offers
  sponsorOffers: SponsorOfferResponse[] = [];
  // Offer for which the ad form is open
  selectedOfferId: number | null = null;
  // Sponsorship id created from the backend (if any)
  createdSponsorshipId: number | null = null;
  // Offer id that has an active sponsorship (to show the Undo button only on that card)
  currentSponsorshipOfferId: number | null = null;
  // Reactive form for ad submission
  adForm: FormGroup;
  // File for ad image (optional)
  selectedImageFile: File | null = null;

  // Loading state
  loading: boolean = true;
  isSubmitting: boolean = false;

  // Pagination variables
  page: number = 0;
  size: number = 4; // Reduced to show 4 cards per page for better layout
  totalPages: number = 0;
  totalElements: number = 0;

  // Toast notification
  showToast: boolean = false;
  toastMessage: string = '';
  toastTitle: string = '';
  toastType: 'success' | 'error' = 'success';
  toastTimeout: any;

  // Base API URL (adjust as necessary)
  apiUrl = 'http://localhost:8088/api/v1';

  constructor(
    private fb: FormBuilder,
    private offerService: SponsorOfferControllerService,
    private sponsorshipService: SponsorshipControllerService,
    private http: HttpClient
  ) {
    // Build the form with required fields
    this.adForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadOffers();
    this.checkUserActiveSponsorship(); // Added this line
  }

  ngOnDestroy(): void {
    // Clear any timeouts to prevent memory leaks
    if (this.toastTimeout) {
      clearTimeout(this.toastTimeout);
    }
  }

  // Load offers from the backend
  loadOffers(): void {
    this.loading = true;
    this.offerService.getAllSponsorOffers({page: this.page, size: this.size}).subscribe({
      next: (res: PageResponseSponsorOfferResponse) => {
        // Provide default values (e.g., 0) if undefined
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

  // When a user clicks "Choose", open the form on that card
  onChoose(offer: SponsorOfferResponse): void {
    if (!offer.id) {
      console.error('Offer ID is undefined.');
      this.showToastMessage('Error', 'Invalid offer selected.', 'error');
      return;
    }
    this.selectedOfferId = offer.id;
    this.adForm.reset();
    this.selectedImageFile = null;
    // Clear any previous sponsorship state
    //this.createdSponsorshipId = null;
    //this.currentSponsorshipOfferId = null;
  }

  // File input change handler for the ad form
  onImageSelected(event: Event): void {
    const inputEl = event.target as HTMLInputElement;
    if (inputEl.files && inputEl.files.length > 0) {
      this.selectedImageFile = inputEl.files[0];
    } else {
      // Clear the selected file if the user cancels the file selection
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
      designColors: this.adForm.value.designColors || [] // Include if needed
    };

    const requestBlob = new Blob([JSON.stringify(sponsorAdRequest)], {type: 'application/json'});

    const multipartRequest: SponsorOfferMultipartRequest = {
      request: requestBlob as any,  // TS type workaround (Blob not string)
      explainImages: this.selectedImageFile ? [this.selectedImageFile] : []
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

  // Update the onSubmitAd method to handle the optional image properly
  // Update the onSubmitAd method to add to the map
  /*onSubmitAd(offerId: number): void {
    // Existing code...
    if (this.adForm.invalid) {
      return;
    }

    const sponsorAdRequest = {
      title: this.adForm.value.title,
      content: this.adForm.value.content
    };

    const formData = new FormData();

    // Append sponsor ad request as JSON blob
    formData.append('request', new Blob([JSON.stringify(sponsorAdRequest)], { type: 'application/json' }));

    // Only append image if one is selected
    if (this.selectedImageFile) {
      formData.append('images', this.selectedImageFile);
    } else {
      // Add a dummy empty blob if no image is provided
      // This ensures the multipart form data is properly formatted
      formData.append('images', new Blob([], { type: 'application/octet-stream' }));
    }

    const url = `${this.apiUrl}/sponsor-offers/chooseSponsorOffer/${offerId}`;

    // Show loading state
    this.isSubmitting = true;

    this.http.post<number>(url, formData).subscribe({
      next: (sponsorshipId: number) => {
        console.log('Sponsorship created with id', sponsorshipId);

        // Add to the map of user sponsorships
        this.userSponsorships.set(offerId, sponsorshipId);

        // For backward compatibility
        this.createdSponsorshipId = sponsorshipId;
        this.currentSponsorshipOfferId = offerId;

        this.selectedOfferId = null;
        this.isSubmitting = false;
        this.showToastMessage('Success', 'Sponsorship created successfully!', 'success');
        // Reload offers to reflect any changes
        this.loadOffers();
      },
      // Existing error handling...
      error: (err) => {
        console.error('Error creating sponsorship:', err);

        // Try an alternative approach if the first one fails
        if (!this.selectedImageFile) {
          // If no image was provided, and we got an error, try without the empty blob
          this.trySubmitWithoutImage(offerId, sponsorAdRequest);
        } else {
          this.showToastMessage('Error', 'Failed to create sponsorship.', 'error');
          this.isSubmitting = false;
        }
      }
    });

  }*/


// Add a fallback method to try submission without any image field
  trySubmitWithoutImage(offerId: number, sponsorAdRequest: any): void {
    // Create a new FormData without any image field
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(sponsorAdRequest)], {type: 'application/json'}));

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

  // Submit ad form to create the sponsorship for the selected offer
  /*onSubmitAd(offerId: number): void {
    if (this.adForm.invalid) {
      return;
    }

    const sponsorAdRequest = {
      title: this.adForm.value.title,
      content: this.adForm.value.content
    };

    const formData = new FormData();
    // Append sponsor ad request as JSON blob
    formData.append('request', new Blob([JSON.stringify(sponsorAdRequest)], { type: 'application/json' }));

    // Only append image if one is selected (making it truly optional)
    if (this.selectedImageFile) {
      formData.append('images', this.selectedImageFile);
    }

    const url = `${this.apiUrl}/sponsor-offers/chooseSponsorOffer/${offerId}`;
    this.http.post<number>(url, formData).subscribe({
      next: (sponsorshipId: number) => {
        console.log('Sponsorship created with id', sponsorshipId);
        this.createdSponsorshipId = sponsorshipId;
        this.currentSponsorshipOfferId = offerId;
        this.selectedOfferId = null;
        this.showToastMessage('Success', 'Sponsorship created successfully!', 'success');
        // Reload offers to reflect any changes
        this.loadOffers();
      },
      error: (err) => {
        console.error('Error creating sponsorship:', err);
        this.showToastMessage('Error', 'Failed to create sponsorship.', 'error');
      }
    });
  }
*/
  // Undo the sponsorship on the current card
  // Update the onUndo method to remove from the map
  onUndo(offerId: number, sponsorshipId: number): void {
    this.sponsorshipService.deleteSponsorship({sponsorshipId: sponsorshipId})
      .pipe(finalize(() => {
        // Remove from the map
        this.userSponsorships.delete(offerId);

        // For backward compatibility
        if (offerId === this.currentSponsorshipOfferId) {
          this.createdSponsorshipId = null;
          this.currentSponsorshipOfferId = null;
        }
      }))
      .subscribe({
        // Existing success and error handling...
        next: () => {
          console.log('Sponsorship undone successfully.');
          this.showToastMessage('Success', 'Sponsorship undone successfully!', 'success');
          // Reload offers to reflect any changes
          this.loadOffers();
        },
        error: (err) => {
          console.error('Failed to undo sponsorship:', err);

          // Check if the error is a 404 (not found), which could mean it was already deleted
          if (err.status === 404) {
            // Treat as success since the sponsorship no longer exists
            this.showToastMessage('Success', 'Sponsorship undone successfully!', 'success');
            this.loadOffers();
          } else {
            // For other errors, show the error message but still reset the state
            this.showToastMessage('Warning', 'The sponsorship may have been removed, but there was an error in the response.', 'success');
            this.loadOffers();
          }
        }
      });
  }

  /*onUndo(): void {
    if (!this.createdSponsorshipId) return;

    this.sponsorshipService.deleteSponsorship({ sponsorshipId: this.createdSponsorshipId })
      .pipe(finalize(() => {
        this.createdSponsorshipId = null;
        this.currentSponsorshipOfferId = null;
      }))
      .subscribe({
        next: () => {
          console.log('Sponsorship undone successfully.');
          this.showToastMessage('Success', 'Sponsorship undone successfully!', 'success');
          // Reload offers to reflect any changes
          this.loadOffers();
        },
        error: (err) => {
          console.error('Failed to undo sponsorship:', err);

          // Check if the error is a 404 (not found), which could mean it was already deleted
          if (err.status === 404) {
            // Treat as success since the sponsorship no longer exists
            this.showToastMessage('Success', 'Sponsorship undone successfully!', 'success');
            this.loadOffers();
          } else {
            // For other errors, show the error message but still reset the state
            this.showToastMessage('Warning', 'The sponsorship may have been removed, but there was an error in the response.', 'success');
            this.loadOffers();
          }
        }
      });
  }
*/

  // Cancel the ad form on the active card
  onCancelForm(): void {
    this.selectedOfferId = null;
    this.adForm.reset();
    this.selectedImageFile = null;
  }

  // Handle page change
  onPageChange(page: number): void {
    this.page = page;
    this.loadOffers();
  }

  // Show toast message
  showToastMessage(title: string, message: string, type: 'success' | 'error'): void {
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;

    // Auto-hide toast after 5 seconds
    if (this.toastTimeout) {
      clearTimeout(this.toastTimeout);
    }

    this.toastTimeout = setTimeout(() => {
      this.hideToast();
    }, 5000);
  }

  // Hide toast message
  hideToast(): void {
    this.showToast = false;
  }

  // Add this method to your component class
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


  // Check if the user has any active sponsorships
// Update the checkUserActiveSponsorship method to handle multiple sponsorships
  checkUserActiveSponsorship(): void {
    // Get all sponsorships for the current user
    this.sponsorshipService.getAllSponsorships({page: 0, size: 100}).subscribe({
      next: (response: PageResponseSponsorshipResponse) => {
        // Clear existing sponsorships
        this.userSponsorships.clear();

        if (response.content && response.content.length > 0) {
          // Find all active sponsorships
          const activeSponshorships = response.content.filter(s =>
            s.status?.toUpperCase() === 'PENDING' ||
            s.status?.toUpperCase() === 'APPROVED'
          );

          // Store all active sponsorships in the map
          activeSponshorships.forEach(sponsorship => {
            if (sponsorship.sponsorOffer?.id) {
              this.userSponsorships.set(sponsorship.sponsorOffer.id, sponsorship.id || 0);
              console.log('Found active sponsorship:', sponsorship.id, 'for offer:', sponsorship.sponsorOffer.id);
            }
          });

          // For backward compatibility, set the most recent one as current if any exist
          if (activeSponshorships.length > 0 && activeSponshorships[0].sponsorOffer?.id) {
            this.createdSponsorshipId = activeSponshorships[0].id || null;
            this.currentSponsorshipOfferId = activeSponshorships[0].sponsorOffer.id;
          } else {
            this.createdSponsorshipId = null;
            this.currentSponsorshipOfferId = null;
          }
        } else {
          // No sponsorships at all, reset state
          this.createdSponsorshipId = null;
          this.currentSponsorshipOfferId = null;
        }
      },
      error: (err) => {
        console.error('Failed to check user sponsorships:', err);
        // On error, reset state to be safe
        this.userSponsorships.clear();
        this.createdSponsorshipId = null;
        this.currentSponsorshipOfferId = null;
      }
    });
  }

  // Add a helper method to check if an offer has an active sponsorship
  hasActiveSponsorship(offerId: number): boolean {
    return this.userSponsorships.has(offerId);
  }

  // Add a helper method to get the sponsorship ID for an offer
  getSponsorshipId(offerId: number): number {
    return this.userSponsorships.get(offerId) || 0;
  }
}




