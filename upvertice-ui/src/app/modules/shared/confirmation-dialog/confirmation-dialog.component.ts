// src/app/modules/shared/confirmation-dialog/confirmation-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: false,
  template: `
    <h2 mat-dialog-title>Confirm Action</h2>
    <div class="mat-dialog-content">
      {{ data.message }}
    </div>
    <div class="mat-dialog-actions" align="end">
      <button mat-button (click)="dialogRef.close(false)">Cancel</button>
      <button mat-raised-button color="warn" (click)="dialogRef.close(true)">Confirm</button>
    </div>
  `,
  styles: [`
    .mat-dialog-content {
      padding: 16px 0;
      font-size: 16px;
    }
    .mat-dialog-actions {
      padding: 16px 0 0 0;
      gap: 8px;
      display: flex;
      justify-content: flex-end;
    }
  `]
})
export class ConfirmationDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}
}
