import {Component, OnInit} from '@angular/core';
import {ProvidershipResponse} from '../../../../services/models/providership-response';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProvidershipControllerService} from '../../../../services/services/providership-controller.service';
import {Chart, ChartConfiguration} from 'chart.js';
import {ProvidershipMultipartRequest} from '../../../../services/models/providership-multipart-request';
import { Router } from '@angular/router';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';
import {ToastrService} from 'ngx-toastr';
@Component({
  selector: 'app-my-providership',
  standalone: false,
  templateUrl: './my-providership.component.html',
  styleUrls: ['./my-providership.component.scss']
})
export class MyProvidershipProviderComponent implements OnInit {
  providerships: ProvidershipResponse[] = [];
  form: FormGroup;
  showModal = false;
  isEdit = false;
  selectedId: number | null = null;
  proofFiles: File[] = [];

  constructor(
    private service: ProvidershipControllerService,
    private router: Router,
    private fb: FormBuilder,
    private toastService: ToastrService // Injecting ToastrService
  ) {
    this.form = this.fb.group({
      sponsorshipId: [null, Validators.required],
      status: ['PENDING', Validators.required],
      producedProduct: [0, [Validators.required, Validators.min(0)]],
      totalProduct: [1, [Validators.required, Validators.min(1)]],
      location: ['', Validators.required],
      hasPrintMachine: [false, Validators.required],
      providedProductTypes: [[], Validators.required]
    });
  }

  ngOnInit() {
    this.getAll();
  }

  getAll() {
    this.service.getAllProviderships({pageable: {page: 0, size: 10}}).subscribe({
      next: (res) => {
        this.providerships = res.content || [];
        setTimeout(() => this.renderChart(), 0);
      },
      error: (err) => {
        console.error('Error fetching providerships:', err);
        this.toastService.error('Failed to load providerships. Please try again.', 'Error');
      }
    });
  }

  openCreate() {
    this.isEdit = false;
    this.selectedId = null;
    this.form.reset({
      status: 'PENDING',
      producedProduct: 0,
      totalProduct: 1,
      hasPrintMachine: false,
      providedProductTypes: []
    });
    this.proofFiles = [];
    this.showModal = true;
  }

  openUpdate(p: ProvidershipResponse) {
    this.isEdit = true;
    this.selectedId = p.id!;
    this.form.patchValue({
      sponsorshipId: p.sponsorship?.id,
      status: p.status,
      producedProduct: p.producedProduct,
      totalProduct: p.totalProduct,
      location: p.location,
      hasPrintMachine: p.hasPrintMachine,
      providedProductTypes: p.sponsorship?.supplierTransactions?.map(t => t.supplierOffer?.title) || []
    });
    this.proofFiles = [];
    this.showModal = true;
    this.toastService.info('Editing providership', 'Info');
  }

  onFileChange(event: any) {
    this.proofFiles = Array.from(event.target.files) as File[];
  }

  onSubmit() {
    const payload = this.form.value;
    const requestJson = JSON.stringify(payload);

    // Build multipart request object matching the generated DTO
    const multipartRequest: ProvidershipMultipartRequest = {
      request: requestJson,
      images: this.proofFiles as Blob[]
    };

    if (this.isEdit) {
      this.service.updateProvidership({id: this.selectedId!, body: multipartRequest})
        .subscribe({
          next: () => {
            this.getAll();
            this.closeModal();
            this.toastService.success('Providership updated successfully!', 'Success');
          },
          error: (err) => {
            console.error('Error updating providership:', err);
            this.toastService.error('Failed to update providership. Please try again.', 'Error');
          }
        });
    } else {
      this.service.createProvidership({body: multipartRequest})
        .subscribe({
          next: () => {
            this.getAll();
            this.closeModal();
            this.toastService.success('Providership created successfully!', 'Success');
          },
          error: (err) => {
            console.error('Error creating providership:', err);
            this.toastService.error('Failed to create providership. Please try again.', 'Error');
          }
        });
    }
  }

  delete(id: number) {
    if (confirm('Are you sure you want to delete this providership?')) {
      this.service.deleteProvidership({id})
        .subscribe({
          next: () => {
            this.getAll();
            this.toastService.success('Providership deleted successfully!', 'Success');
          },
          error: (err) => {
            console.error('Error deleting providership:', err);
            this.toastService.error('Failed to delete providership. Please try again.', 'Error');
          }
        });
    }
  }

  closeModal() {
    this.showModal = false;
  }

  renderChart() {
    const ctx = (document.getElementById('progressChart') as HTMLCanvasElement)
      .getContext('2d');
    if (!ctx) return;
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.providerships.map(p => `#${p.id}`),
        datasets: [{
          label: 'Completion %',
          data: this.providerships.map(p => (p.producedProduct! / p.totalProduct!) * 100)
        }]
      },
      options: {scales: {y: {beginAtZero: true, max: 100}}}
    } as ChartConfiguration);
  }

  /**
   * Parses comma-separated product types and updates the form control.
   */
  onProvidedProductTypesChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.value !== null) {
      const values = input.value
        .split(',')
        .map(v => v.trim())
        .filter(v => v);
      this.form.get('providedProductTypes')!.setValue(values);
    }
  }

  goToDetails(id: number | undefined): void {
    if (id != null) {
      this.router.navigate(['/provider/providership-details', id]);
    }
  }


}
