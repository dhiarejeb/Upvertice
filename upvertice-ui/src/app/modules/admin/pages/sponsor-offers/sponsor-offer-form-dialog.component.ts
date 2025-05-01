import {
  Component,
  Inject,
} from '@angular/core';

import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SponsorOfferControllerService } from '../../../../services/services/sponsor-offer-controller.service';
import { SponsorOfferResponse } from '../../../../services/models/sponsor-offer-response';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {SponsorOfferMultipartRequest} from '../../../../services/models/sponsor-offer-multipart-request';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-sponsor-offer-form-dialog',
  standalone: false,
  templateUrl: './sponsor-offer-form-dialog.component.html',
})
export class SponsorOfferFormDialogComponent {
  form: FormGroup;
  files: File[] = [];
  oldImageUrls: string[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SponsorOfferFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SponsorOfferResponse | null,
    private sponsorOfferService: SponsorOfferControllerService
  ) {
    this.form = this.fb.group({
      title: [data?.title || '', Validators.required],
      description: [data?.description || '', Validators.required],
      category: [data?.category || '', Validators.required],
      numberAds: [data?.numberAds || '', [Validators.required, Validators.min(1)]],
      price: [data?.price || '', [Validators.required, Validators.min(0)]],
      productQuantity: [data?.productQuantity || '', [Validators.required, Validators.min(1)]],
      productType: [data?.productType || '', Validators.required],
      salesArea: [data?.salesArea || '', Validators.required],
      status: [data?.status || '', Validators.required],
    });

    if (data?.explainImages && data.explainImages.length > 0) {
      this.oldImageUrls = data.explainImages;
    }
  }

  imagePreviews: string[] = [];

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files) {
      this.files = Array.from(input.files);
      this.imagePreviews = [];

      this.files.forEach(file => {
        const reader = new FileReader();
        reader.onload = e => this.imagePreviews.push(e.target?.result as string);
        reader.readAsDataURL(file);
      });
    }
  }

  cancel(): void {
    this.dialogRef.close();
  }

  submit(): void {
    if (this.form.invalid) return;

    const multipartRequest: SponsorOfferMultipartRequest = {
      request: JSON.stringify(this.form.value),
      explainImages: this.files
    };

    const isEdit = !!this.data?.id;

    const request$: Observable<SponsorOfferResponse> = isEdit
      ? this.sponsorOfferService.updateSponsorOffer({
        offerId: this.data?.id as number,
        body: multipartRequest
      })
      : this.sponsorOfferService.createSponsorOffer({
        body: multipartRequest
      }).pipe(
        map((res: any) => res as SponsorOfferResponse)
      );

    request$.subscribe({
      next: (res: SponsorOfferResponse) => this.dialogRef.close(res),
      error: (err: any) => {
        console.error(`${isEdit ? 'Update' : 'Create'} failed`, err);
      }
    });
  }
}
