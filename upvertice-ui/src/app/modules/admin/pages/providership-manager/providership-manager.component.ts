import {Component} from '@angular/core';
import {ProvidershipControllerService} from '../../../../services/services';
import {
  PageResponseProvidershipResponse,
  ProvidershipMultipartRequest,
  ProvidershipResponse
} from '../../../../services/models';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {finalize} from 'rxjs';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-providership-manager',
  standalone: false,
  templateUrl: './providership-manager.component.html',
  styleUrls: ['./providership-manager.component.scss']
})
export class ProvidershipManagerComponent {
  providerships: ProvidershipResponse[] = [];
  selectedProvidership: ProvidershipResponse | null = null;
  updateForm: FormGroup;

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Loading states
  loading = false;
  updating = false;

  // Approval status options for admin update
  approvalStatusOptions: Array<'PENDING' | 'APPROVED' | 'REJECTED'> = ['PENDING', 'APPROVED', 'REJECTED'];

  constructor(
    private providershipService: ProvidershipControllerService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private router: Router,
    private toastService: ToastrService
  ) {
    this.updateForm = this.fb.group({
      sponsorshipId: [null],
      providershipApprovalStatus: [null],
      bonusEarned: [0, [Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    this.loadProviderships();
  }

  loadProviderships(): void {
    this.loading = true;
    const params = { pageable: { page: this.currentPage, size: this.pageSize, sort: ['id,desc'] } };

    this.providershipService.getAllProviderships(params)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (resp: PageResponseProvidershipResponse) => {
          this.providerships = resp.content || [];
          this.totalElements = resp.totalElements || 0;
          this.totalPages = resp.totalPages || 0;
          this.toastService.success('Providerships loaded successfully');
        },
        error: () => this.toastService.error('Failed to load providerships')
      });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadProviderships();
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProviderships();
    }
  }

  nextPage(): void {
    if (this.currentPage + 1 < this.totalPages) {
      this.currentPage++;
      this.loadProviderships();
    }
  }

  selectProvidership(item: ProvidershipResponse): void {
    this.selectedProvidership = item;
    this.updateForm.patchValue({
      sponsorshipId: item.sponsorship?.id || null,
      providershipApprovalStatus: item.providershipApprovalStatus || null,
      bonusEarned: item.bonusEarned || 0
    });
  }

  updateProvidership(): void {
    if (!this.selectedProvidership || this.updateForm.invalid) {
      this.updateForm.markAllAsTouched();
      return;
    }

    this.updating = true;

    const payload: ProvidershipMultipartRequest = {
      request: JSON.stringify({
        sponsorshipId: this.updateForm.value.sponsorshipId,
        providershipApprovalStatus: this.updateForm.value.providershipApprovalStatus,
        bonusEarned: this.updateForm.value.bonusEarned
      })
    };

    const params = { id: this.selectedProvidership.id!, body: payload };

    this.providershipService.updateProvidership(params)
      .pipe(finalize(() => this.updating = false))
      .subscribe({
        next: (resp: ProvidershipResponse) => {
          const idx = this.providerships.findIndex(p => p.id === resp.id);
          if (idx > -1) this.providerships[idx] = resp;
          this.toastService.success('Providership updated successfully');
          this.cancelEdit();
        },
        error: () => this.toastService.error('Failed to update providership')
      });
  }

  confirmDelete(item: ProvidershipResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Deletion',
        message: `Delete providership #${item.id}?`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    ref.afterClosed().subscribe(ok => ok && this.deleteProvidership(item));
  }

  deleteProvidership(item: ProvidershipResponse): void {
    if (!item.id) return;

    this.providershipService.deleteProvidership({ id: item.id }).subscribe({
      next: () => {
        this.providerships = this.providerships.filter(p => p.id !== item.id);
        this.toastService.success('Providership deleted');
        if (this.selectedProvidership?.id === item.id) this.cancelEdit();
      },
      error: () => this.toastService.error('Delete failed')
    });
  }

  cancelEdit(): void {
    this.selectedProvidership = null;
    this.updateForm.reset({ sponsorshipId: null, providershipApprovalStatus: null, bonusEarned: 0 });
  }

  isFieldInvalid(name: string): boolean {
    const c = this.updateForm.get(name);
    return !!(c && c.touched && c.invalid);
  }

  atLeastOneFieldFilled(): boolean {
    const value = this.updateForm.value;
    return value.sponsorshipId !== null ||
      value.providershipApprovalStatus !== null ||
      (value.bonusEarned !== null && value.bonusEarned !== 0);
  }

  goToDetails(id: number | undefined): void {
    if (id != null) {
      this.router.navigate(['/admin/providership-details', id]);
    }
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'APPROVED':
        return 'bg-success';
      case 'REJECTED':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

}
