import {Component, OnInit} from '@angular/core';
import {SupplierOfferResponse} from '../../../../services/models/supplier-offer-response';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SupplierOfferControllerService} from '../../../../services/services/supplier-offer-controller.service';
import {PageResponseSupplierOfferResponse} from '../../../../services/models/page-response-supplier-offer-response';
import {SupplierOfferMultipartRequest} from '../../../../services/models/supplier-offer-multipart-request';

@Component({
  selector: 'app-supplier-offers',
  standalone: false,
  templateUrl: './supplier-offers.component.html',
  styleUrls: ['./supplier-offers.component.scss']
})
export class SupplierOffersComponent implements OnInit {
  supplierOffers: SupplierOfferResponse[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  formVisible: boolean = false;
  editing: boolean = false;
  offerForm: FormGroup;
  selectedImage: File | null = null;
  editingId: number | null = null;

  constructor(
    private supplierOfferService: SupplierOfferControllerService,
    private fb: FormBuilder
  ) {
    this.offerForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      quantityAvailable: [0, [Validators.required, Validators.min(0)]],
      price: [0, [Validators.required, Validators.min(0)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      status: ['AVAILABLE', Validators.required],
      sponsorAdIds: [[]]
    });
  }

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.supplierOfferService.getAllSupplierOffers({ page: this.currentPage, size: 10 })
      .subscribe((response: PageResponseSupplierOfferResponse) => {
        this.supplierOffers = response.content || [];
        this.totalPages = response.totalPages || 0;
      });
  }

  loadPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadOffers();
    }
  }

  openCreateForm(): void {
    this.resetForm();
    this.editing = false;
    this.formVisible = true;
  }

  closeForm(): void {
    this.formVisible = false;
    this.resetForm();
  }

  onImageSelected(event: any): void {
    this.selectedImage = event.target.files[0];
  }

  onSubmit(): void {
    const formValue = this.offerForm.value;
    const requestPayload = {
      ...formValue,
      sponsorAdIds: formValue.sponsorAdIds
    };

    const multipartRequest: SupplierOfferMultipartRequest = {
      request: JSON.stringify(requestPayload),
      image: this.selectedImage as Blob
    };

    if (this.editingId !== null) {
      this.supplierOfferService.updateSupplierOffer({ id: this.editingId, body: multipartRequest })
        .subscribe(() => {
          this.loadOffers();
          this.closeForm();
        });
    } else {
      this.supplierOfferService.createSupplierOffer({ body: multipartRequest })
        .subscribe(() => {
          this.loadOffers();
          this.closeForm();
        });
    }
  }

  editOffer(offer: SupplierOfferResponse): void {
    this.editing = true;
    this.editingId = offer.id!;
    this.offerForm.patchValue({
      ...offer,
      sponsorAdIds: offer.sponsorAds?.map(ad => ad.id) || []
    });
    this.selectedImage = null;
    this.formVisible = true;
  }

  confirmDelete(id: number): void {
    if (confirm('Are you sure you want to delete this offer?')) {
      this.delete(id);
    }
  }

  delete(id: number): void {
    this.supplierOfferService.deleteSupplierOffer({ id })
      .subscribe(() => this.loadOffers());
  }

  resetForm(): void {
    this.offerForm.reset({
      title: '',
      description: '',
      quantityAvailable: 0,
      price: 0,
      startDate: '',
      endDate: '',
      status: 'AVAILABLE',
      sponsorAdIds: []
    });
    this.editingId = null;
    this.selectedImage = null;
  }
  changePage(offset: number): void {
    const newPage = this.currentPage + offset;

    if (newPage >= 0 && newPage < this.totalPages) {
      this.currentPage = newPage;
      this.loadOffers(); // adjust if your method name differs
    }
  }

}
