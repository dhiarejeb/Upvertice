import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {SponsorshipResponse} from '../../../../../services/models/sponsorship-response';
import {SponsorAdResponse} from '../../../../../services/models/sponsor-ad-response';
import {ToastrService} from 'ngx-toastr';
@Component({
  selector: 'app-edit-sponsorship-modal',
  standalone: false,
  templateUrl: './edit-sponsorship-modal.component.html',
  styleUrl: './edit-sponsorship-modal.component.scss'
})
export class EditSponsorshipModalComponent implements OnInit {
  sponsorship: SponsorshipResponse;
  activeTab = 'status';
  statusForm: FormGroup;
  adForm: FormGroup;

  previewUrl: string | null = null;
  selectedImage: File | null = null;
  selectedColors: string[] = [];

  constructor(
    private fb: FormBuilder,
    private toastService: ToastrService,
    public dialogRef: MatDialogRef<EditSponsorshipModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { sponsorship: SponsorshipResponse }
  ) {
    this.sponsorship = data.sponsorship;

    this.statusForm = this.fb.group({
      status: [this.sponsorship.status || 'PENDING', Validators.required]
    });

    const sponsorAd = this.sponsorship.sponsorAds?.[0];
    this.adForm = this.fb.group({
      title: [sponsorAd?.title || '', Validators.required],
      content: [sponsorAd?.content || ''],
      designColors: [sponsorAd?.designColors?.join(', ') || '']
    });

    this.previewUrl = sponsorAd?.design || null;
  }

  ngOnInit(): void {
    const sponsorAd = this.sponsorship.sponsorAds?.[0];
    this.selectedColors = sponsorAd?.designColors || ['#ffffff'];
  }

  onTabChange(tab: string): void {
    this.activeTab = tab;
  }

  onStatusSubmit(): void {
    if (this.statusForm.valid) {
      this.toastService.success('Status form submitted successfully');
      this.dialogRef.close({
        type: 'status',
        status: this.statusForm.value.status
      });
    } else {
      this.toastService.error('Status form is invalid');
    }
  }

  onAdSubmit(): void {
    if (this.adForm.valid) {
      const sponsorAd: Partial<SponsorAdResponse> = {
        id: this.sponsorship.sponsorAds?.[0]?.id,
        title: this.adForm.value.title,
        content: this.adForm.value.content,
        designColors: this.selectedColors
      };

      this.toastService.success('Sponsor ad form submitted successfully');
      this.dialogRef.close({
        type: 'ad',
        sponsorAd,
        image: this.selectedImage
      });
    } else {
      this.toastService.error('Ad form is invalid');
    }
  }

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.selectedImage = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onCancel(): void {
    this.toastService.success('Edit cancelled');
    this.dialogRef.close();
  }

  addColor(): void {
    this.selectedColors.push('#000000');
  }

  removeColor(index: number): void {
    this.selectedColors.splice(index, 1);
  }

  onColorChange(newColor: string, index: number): void {
    this.selectedColors[index] = newColor;
  }
}
