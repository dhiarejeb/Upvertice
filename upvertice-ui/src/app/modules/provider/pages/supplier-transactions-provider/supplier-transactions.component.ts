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
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-supplier-transactions',
  standalone: false,
  templateUrl: './supplier-transactions.component.html',
  styleUrls: ['./supplier-transactions.component.scss']
})
export class SupplierTransactionsProviderComponent {
  transactions: SupplierTransactionResponse[] = [];
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;
  statusOptions = ['PENDING', 'APPROVED', 'REJECTED', 'FINISHED', 'COMPLETED'];

  constructor(
    private service: SupplierTransactionControllerService,
    private router: Router,
    private toastService: ToastrService // Inject toastr service
  ) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.service.getSupplierTransactions({ page: this.page, size: this.size }).subscribe({
      next: (resp: PageResponseSupplierTransactionResponse) => {
        this.transactions = resp.content || [];
        this.totalPages = resp.totalPages || 0;
        this.totalElements = resp.totalElements || 0;
        this.toastService.success('Transactions loaded successfully!'); // Success message on successful load
      },
      error: (err) => {
        console.error('Error loading transactions', err);
        this.toastService.error('Failed to load transactions. Please try again later.'); // Error message on failure
      }
    });
  }

  update(transaction: SupplierTransactionResponse): void {
    if (!transaction.id) return;

    // Build the request payload with only status and discount
    const updatePayload = {
      status: transaction.supplierTransactionStatus,
      discount: transaction.discount
    };

    // Create the multipart request object
    const body: SupplierTransactionMultipartRequest = {
      request: JSON.stringify(updatePayload)
      // images not provided for admin update
    };

    this.service.updateSupplierTransaction({ transactionId: transaction.id, body }).subscribe({
      next: () => {
        this.toastService.success('Transaction updated successfully!'); // Success message on successful update
        this.loadTransactions(); // Reload transactions after update
      },
      error: (err) => {
        console.error('Error updating transaction', err);
        this.toastService.error('Failed to update transaction. Please try again later.'); // Error message on failure
      }
    });
  }

  delete(transaction: SupplierTransactionResponse): void {
    if (!transaction.id) return;
    if (!confirm('Are you sure you want to delete this transaction?')) return;

    this.service.deleteSupplierTransaction({ transactionId: transaction.id }).subscribe({
      next: () => {
        this.toastService.success('Transaction deleted successfully!'); // Success message on successful deletion
        this.loadTransactions(); // Reload transactions after deletion
      },
      error: (err) => {
        console.error('Error deleting transaction', err);
        this.toastService.error('Failed to delete transaction. Please try again later.'); // Error message on failure
      }
    });
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

  goToDetails(id: number | undefined): void {
    if (id != null) {
      this.router.navigate(['/provider/supplier-transaction-details', id]);
    }
  }
}
