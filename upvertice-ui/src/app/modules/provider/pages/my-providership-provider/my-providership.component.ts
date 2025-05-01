import {Component, OnInit} from '@angular/core';
import {ProvidershipResponse} from '../../../../services/models/providership-response';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProvidershipControllerService} from '../../../../services/services/providership-controller.service';
import {Chart, ChartConfiguration} from 'chart.js';
import {ProvidershipMultipartRequest} from '../../../../services/models/providership-multipart-request';
import { Router } from '@angular/router';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';
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

    private fb: FormBuilder
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
    this.service.getAllProviderships({pageable: {page: 0, size: 10}}).subscribe(res => {
      this.providerships = res.content || [];
      setTimeout(() => this.renderChart(), 0);
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
        .subscribe(() => {
          this.getAll();
          this.closeModal();
        });
    } else {
      this.service.createProvidership({body: multipartRequest})
        .subscribe(() => {
          this.getAll();
          this.closeModal();
        });
    }
  }

  delete(id: number) {
    if (confirm('Are you sure you want to delete this providership?')) {
      this.service.deleteProvidership({id})
        .subscribe(() => this.getAll());
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
