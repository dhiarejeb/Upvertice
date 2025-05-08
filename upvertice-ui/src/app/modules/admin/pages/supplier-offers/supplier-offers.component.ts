import {Component, OnInit} from '@angular/core';
import {SupplierOfferResponse} from '../../../../services/models/supplier-offer-response';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SupplierOfferControllerService} from '../../../../services/services/supplier-offer-controller.service';
import {PageResponseSupplierOfferResponse} from '../../../../services/models/page-response-supplier-offer-response';
import {SupplierOfferMultipartRequest} from '../../../../services/models/supplier-offer-multipart-request';
import {UpdateSupplierOffer$Params} from '../../../../services/fn/supplier-offer-controller/update-supplier-offer';
import {CreateSupplierOffer$Params} from '../../../../services/fn/supplier-offer-controller/create-supplier-offer';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-supplier-offers',
  standalone: false,
  templateUrl: './supplier-offers.component.html',
  styleUrls: ['./supplier-offers.component.scss']
})
export class SupplierOffersComponent implements OnInit {
  supplierOffers: SupplierOfferResponse[] = [];
  currentPage = 0;
  totalPages = 0;

  formVisible = false;
  editing = false;
  editingId: number | null = null;

  offerForm: FormGroup;
  selectedImage: File | null = null;

  constructor(
    private supplierOfferService: SupplierOfferControllerService,
    private fb: FormBuilder,
    private toastService: ToastrService
  ) {
    this.offerForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      quantityAvailable: [0, [Validators.required, Validators.min(0)]],
      price: [0, [Validators.required, Validators.min(0)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      status: ['AVAILABLE', Validators.required],
      sponsorAdIds: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.supplierOfferService.getAllSupplierOffers({ page: this.currentPage, size: 10 })
      .subscribe({
        next: (resp: PageResponseSupplierOfferResponse) => {
          this.supplierOffers = resp.content || [];
          this.totalPages = resp.totalPages || 0;
        },
        error: () => {
          this.toastService.error('Failed to load supplier offers');
        }
      });
  }

  openCreateForm(): void {
    this.resetForm();
    this.editing = false;
    this.editingId = null;
    this.formVisible = true;
  }

  editOffer(offer: SupplierOfferResponse): void {
    this.editing = true;
    this.editingId = offer.id!;
    this.formVisible = true;

    this.offerForm.patchValue({
      title: offer.title,
      description: offer.description,
      quantityAvailable: offer.quantityAvailable,
      price: offer.price,
      startDate: offer.startDate,
      endDate: offer.endDate,
      status: offer.status,
      sponsorAdIds: (offer.sponsorAds || []).map(ad => ad.id).join(',')
    });
    this.selectedImage = null;
  }

  closeForm(): void {
    this.formVisible = false;
    this.resetForm();
  }

  onImageSelected(evt: Event): void {
    const input = evt.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedImage = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.offerForm.invalid) return;

    const fv = this.offerForm.value;
    const sponsorAdIds: string[] = fv.sponsorAdIds
      .split(',')
      .map((s: string) => s.trim())
      .filter((s: string) => s.length > 0);

    const startDate = this.formatDate(fv.startDate);
    const endDate = this.formatDate(fv.endDate);

    const requestPayload = {
      title: fv.title,
      description: fv.description,
      quantityAvailable: fv.quantityAvailable,
      price: fv.price,
      startDate,
      endDate,
      status: fv.status,
      sponsorAdIds
    };

    const multipart: SupplierOfferMultipartRequest = {
      request: JSON.stringify(requestPayload),
      image: this.selectedImage as Blob
    };

    if (this.editingId !== null) {
      const params: UpdateSupplierOffer$Params = {
        id: this.editingId,
        body: multipart
      };
      this.supplierOfferService.updateSupplierOffer(params).subscribe({
        next: () => {
          this.toastService.success('Supplier offer updated successfully');
          this.loadOffers();
          this.closeForm();
        },
        error: () => {
          this.toastService.error('Failed to update supplier offer');
        }
      });
    } else {
      const params: CreateSupplierOffer$Params = { body: multipart };
      this.supplierOfferService.createSupplierOffer(params).subscribe({
        next: () => {
          this.toastService.success('Supplier offer created successfully');
          this.loadOffers();
          this.closeForm();
        },
        error: () => {
          this.toastService.error('Failed to create supplier offer');
        }
      });
    }
  }

  confirmDelete(id: number): void {
    if (confirm('Are you sure you want to delete this offer?')) {
      this.delete(id);
    }
  }

  delete(id: number): void {
    this.supplierOfferService.deleteSupplierOffer({ id }).subscribe({
      next: () => {
        this.toastService.success('Supplier offer deleted successfully');
        this.loadOffers();
      },
      error: () => {
        this.toastService.error('Failed to delete supplier offer');
      }
    });
  }

  private resetForm(): void {
    this.offerForm.reset({
      title: '',
      description: '',
      quantityAvailable: 0,
      price: 0,
      startDate: '',
      endDate: '',
      status: 'AVAILABLE',
      sponsorAdIds: ''
    });
    this.selectedImage = null;
    this.editingId = null;
  }

  private formatDate(input: string): string {
    const d = new Date(input);
    const yy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${yy}-${mm}-${dd}`;
  }
  changePage(offset: number): void {
    const newPage = this.currentPage + offset;

    if (newPage >= 0 && newPage < this.totalPages) {
      this.currentPage = newPage;
      this.loadOffers(); // adjust if your method name differs
    }
  }

}
