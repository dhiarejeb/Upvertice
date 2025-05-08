import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-delete-confirmation-modal',
  standalone: false,
  templateUrl: './delete-confirmation-modal.component.html',
  styleUrl: './delete-confirmation-modal.component.scss'
})
export class DeleteConfirmationModalComponent {  constructor(
  public dialogRef: MatDialogRef<DeleteConfirmationModalComponent>,
  @Inject(MAT_DIALOG_DATA) public data: { sponsorshipId: number },
  private toastService: ToastrService
) {}

  onCancel(): void {
    this.toastService.success('Deletion cancelled');
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.toastService.success(`Sponsorship #${this.data.sponsorshipId} marked for deletion`);
    this.dialogRef.close(true);
  }
}
