import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import {SponsorAdControllerService} from '../../../../services/services/sponsor-ad-controller.service';
import {SponsorAdResponse} from '../../../../services/models/sponsor-ad-response';
import {SponsorAdMultipartRequest} from '../../../../services/models/sponsor-ad-multipart-request';


@Component({
  selector: 'app-sponsor-ad-dialog',
  standalone : false,
  templateUrl: './sponsor-ad-dialog.component.html',
  //styleUrls: ['./sponsor-ad-dialog.component.scss'] //todo
})
export class SponsorAdDialogComponent {
  form: FormGroup;
  selectedImage?: File;

  constructor(
    private fb: FormBuilder,
    private sponsorAdService: SponsorAdControllerService,
    private dialogRef: MatDialogRef<SponsorAdDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SponsorAdResponse
  ) {
    this.form = this.fb.group({
      title: [data?.title || '', Validators.required],
      content: [data?.content || '', Validators.required],
      designColors: this.fb.array(data?.designColors || [''], Validators.required)
    });
  }

  get designColors(): FormArray {
    return this.form.get('designColors') as FormArray;
  }

  addColor(): void {
    this.designColors.push(this.fb.control('', Validators.required));
  }

  removeColor(index: number): void {
    this.designColors.removeAt(index);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedImage = file;
    }
  }

  submit(): void {
    if (this.form.invalid) return;

    const multipartRequest: SponsorAdMultipartRequest = {
      request: JSON.stringify(this.form.value),
      image: this.selectedImage
    };

    this.sponsorAdService.updateSponsorAd({
      adId: this.data?.id as number,
      body: multipartRequest
    }).subscribe({
      next: res => this.dialogRef.close(res),
      error: err => console.error('Update failed', err)
    });
  }
}
