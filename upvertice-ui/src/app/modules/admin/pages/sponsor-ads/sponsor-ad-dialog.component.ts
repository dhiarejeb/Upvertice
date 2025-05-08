import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import {SponsorAdControllerService} from '../../../../services/services/sponsor-ad-controller.service';
import {SponsorAdResponse} from '../../../../services/models/sponsor-ad-response';
import {SponsorAdMultipartRequest} from '../../../../services/models/sponsor-ad-multipart-request';
import {ToastrService} from 'ngx-toastr';


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
    private toastService: ToastrService,
    private dialogRef: MatDialogRef<SponsorAdDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SponsorAdResponse
  ) {
    this.form = this.fb.group({
      title: [data?.title || '', Validators.required],
      content: [data?.content || '', Validators.required],
      designColors: this.fb.array(
        data?.designColors?.length ? data.designColors.map(color => this.fb.control(color, Validators.required)) : [this.fb.control('', Validators.required)],
        Validators.required
      )
    });
  }

  get designColors(): FormArray {
    return this.form.get('designColors') as FormArray;
  }

  addColor(): void {
    this.designColors.push(this.fb.control('', Validators.required));
  }

  removeColor(index: number): void {
    if (this.designColors.length > 1) {
      this.designColors.removeAt(index);
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedImage = file;
      this.toastService.info('Image selected');
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.toastService.error('Please fill in all required fields');
      return;
    }

    const multipartRequest: SponsorAdMultipartRequest = {
      request: JSON.stringify(this.form.value),
      image: this.selectedImage
    };

    this.sponsorAdService.updateSponsorAd({
      adId: this.data?.id as number,
      body: multipartRequest
    }).subscribe({
      next: (res) => {
        this.toastService.success('Sponsor ad updated successfully');
        this.dialogRef.close(res);
      },
      error: (err) => {
        console.error('Update failed', err);
        this.toastService.error('Failed to update sponsor ad');
      }
    });
  }
}
