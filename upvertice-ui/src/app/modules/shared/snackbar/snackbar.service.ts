import { Injectable } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  private readonly defaultDuration = 3000;

  constructor(private snackBar: MatSnackBar) { }

  success(message: string, duration: number = this.defaultDuration): void {
    this.showSnackbar(message, 'success-snackbar', duration);
  }

  error(message: string, duration: number = this.defaultDuration): void {
    this.showSnackbar(message, 'error-snackbar', duration);
  }

  info(message: string, duration: number = this.defaultDuration): void {
    this.showSnackbar(message, 'info-snackbar', duration);
  }

  private showSnackbar(message: string, panelClass: string, duration: number): void {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: [panelClass, 'app-snackbar'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }
}
