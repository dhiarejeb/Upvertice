import {Component, OnInit} from '@angular/core';
import {SupplierOfferResponse} from '../../../../services/models/supplier-offer-response';
import {SupplierOfferControllerService} from '../../../../services/services/supplier-offer-controller.service';
import {
  SupplierTransactionControllerService
} from '../../../../services/services/supplier-transaction-controller.service';
import {SupplierTransactionResponse} from '../../../../services/models/supplier-transaction-response';
import {
  PageResponseSupplierTransactionResponse
} from '../../../../services/models/page-response-supplier-transaction-response';
import {PageResponseSupplierOfferResponse} from '../../../../services/models/page-response-supplier-offer-response';
import {finalize} from 'rxjs';

@Component({
  selector: 'app-supplier-supplier-offers',
  standalone: false,
  templateUrl: './supplier-supplier-offers.component.html',
  styleUrl: './supplier-supplier-offers.component.scss'
})
export class SupplierSupplierOffersComponent implements OnInit{
  supplierOffers: SupplierOfferResponse[] = [];
  chosenOffers: Map<number, number> = new Map(); // offerId → transactionId
  loading = false;
  page = 0;
  size = 12;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private offerService: SupplierOfferControllerService,
    private transactionService: SupplierTransactionControllerService
  ) {}

  ngOnInit(): void {
    this.loadChosenOffers();
    this.loadSupplierOffers();
  }

  loadSupplierOffers(): void {
    this.loading = true;
    this.offerService.getAllSupplierOffers({ page: this.page, size: this.size }).subscribe({
      next: (res: PageResponseSupplierOfferResponse) => {
        this.supplierOffers = res.content || [];
        this.totalPages = res.totalPages || 0;
        this.totalElements = res.totalElements || 0;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load supplier offers', err);
        this.loading = false;
        this.showToastMessage('Error', 'Failed to load supplier offers.', 'error');
      }
    });
  }

  loadChosenOffers(): void {
    this.transactionService.getSupplierTransactions({ page: 0, size: 100 }).subscribe({
      next: (res: PageResponseSupplierTransactionResponse) => {
        this.chosenOffers.clear();
        const activeTxs = res.content?.filter(tx =>
          tx.supplierTransactionStatus === 'PENDING' ||
          tx.supplierTransactionStatus === 'APPROVED'
        ) || [];
        activeTxs.forEach(tx => {
          const offerId = tx.supplierOffer?.id;
          if (offerId && tx.id) {
            this.chosenOffers.set(offerId, tx.id);
          }
        });
      },
      error: (err) => {
        console.error('Failed to load supplier transactions', err);
        this.chosenOffers.clear();
      }
    });
  }

  onChoose(offer: SupplierOfferResponse): void {
    if (!offer.id) return;

    this.offerService.chooseSupplierOffer({ supplierOfferId: offer.id }).subscribe({
      next: (tx: SupplierTransactionResponse) => {
        if (tx.id && tx.supplierOffer?.id) {
          this.chosenOffers.set(tx.supplierOffer.id, tx.id);
          this.showToastMessage('Success', 'Offer chosen successfully.', 'success');
        }
      },
      error: (err) => {
        console.error('Failed to choose supplier offer', err);
        this.showToastMessage('Error', 'Failed to choose offer.', 'error');
      }
    });
  }

  onUndo(offerId: number): void {
    const transactionId = this.chosenOffers.get(offerId);
    if (!transactionId) return;

    this.transactionService.deleteSupplierTransaction({ transactionId })
      .pipe(finalize(() => this.chosenOffers.delete(offerId)))
      .subscribe({
        next: () => {
          this.showToastMessage('Success', 'Offer undo successful.', 'success');
        },
        error: (err) => {
          console.error('Failed to undo offer', err);
          if (err.status === 404) {
            this.showToastMessage('Success', 'Offer undo successful (already deleted).', 'success');
          } else {
            this.showToastMessage('Warning', 'Offer may have been removed, but undo failed.', 'warning');
          }
        }
      });
  }

  showToastMessage(title: string, message: string, type: 'success' | 'error' | 'warning'): void {
    // Replace with actual toast library if needed
    alert(`${title}: ${message}`);
  }

  isOfferChosen(offerId: number): boolean {
    return this.chosenOffers.has(offerId);
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadSupplierOffers();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadSupplierOffers();
    }
  }
}
