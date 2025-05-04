import { Component } from '@angular/core';
import {SupplierTransactionResponse} from '../../../../services/models/supplier-transaction-response';
import {
  SupplierTransactionControllerService
} from '../../../../services/services/supplier-transaction-controller.service';
import {
  PageResponseSupplierTransactionResponse
} from '../../../../services/models/page-response-supplier-transaction-response';
import {SupplierTransactionMultipartRequest} from '../../../../services/models/supplier-transaction-multipart-request';
import {Router} from '@angular/router';

@Component({
  selector: 'app-supplier-transactions',
  standalone: false,
  templateUrl: './supplier-transactions.component.html',
  styleUrls: ['./supplier-transactions.component.scss']
})
export class SupplierTransactionsSupplierComponent {
  transactions: SupplierTransactionResponse[] = [];
  editingId: number | null = null;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;
  statusOptions: SupplierTransactionResponse['supplierTransactionStatus'][] = [
    'PENDING', 'APPROVED', 'REJECTED', 'FINISHED', 'COMPLETED'
  ];

  updatedLocations: string[] = [];
  updatedProofs: File[] = [];
  updatedQuantitySold: number | null = null;

  constructor(
    private service: SupplierTransactionControllerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.service.getSupplierTransactions({ page: this.page, size: this.size }).subscribe(resp => {
      this.transactions = resp.content || [];
      this.totalPages = resp.totalPages ?? 0;
      this.totalElements = resp.totalElements ?? 0;
    });
  }

  toggleEdit(tx: SupplierTransactionResponse): void {
    if (this.editingId === tx.id) {
      this.editingId = null;
    } else {
      this.editingId = tx.id ?? null;
      this.updatedLocations = [...(tx.locations || [])];
      this.updatedQuantitySold = tx.quantitySold ?? 0;
      this.updatedProofs = [];
    }
  }

  onProofsSelected(event: any): void {
    const files = event.target.files;
    this.updatedProofs = Array.from(files);
  }

  submitEdit(tx: SupplierTransactionResponse): void {
    if (!tx.id) return;

    const requestPayload: any = {};

    if (this.updatedLocations && this.updatedLocations.length > 0) {
      requestPayload.locations = this.updatedLocations.filter((loc: string) => loc.trim().length > 0);
    }

    if (this.updatedQuantitySold !== null && this.updatedQuantitySold !== undefined) {
      requestPayload.quantitySold = this.updatedQuantitySold;
    }

    const multipartRequest = {
      request: JSON.stringify(requestPayload),
      images: this.updatedProofs
    };

    this.service.updateSupplierTransaction({
      transactionId: tx.id,
      body: multipartRequest
    }).subscribe(() => {
      this.loadTransactions();
      this.editingId = null;
      this.updatedLocations = [];
      this.updatedQuantitySold = null;
      this.updatedProofs = [];
    });
  }




  delete(tx: SupplierTransactionResponse): void {
    if (!tx.id) return;
    if (!confirm('Are you sure you want to delete this transaction?')) return;

    this.service.deleteSupplierTransaction({ transactionId: tx.id }).subscribe(() => {
      this.loadTransactions();
    });
  }

  goToDetails(id: number | undefined): void {
    if (id != null) {
      this.router.navigate(['/supplier/supplier-transaction-details', id]);
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadTransactions();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadTransactions();
    }
  }

  onLocationsChange(event: string): void {
    this.updatedLocations = event.split(',').map(location => location.trim());
  }


}
