import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';
import { ImageViewerComponent } from './image-viewer/image-viewer.component';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import {MatButtonModule} from '@angular/material/button';

@NgModule({
  declarations: [
    ImageViewerComponent,
    ConfirmationDialogComponent
  ],
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule
  ],
  exports: [
    ImageViewerComponent,
    ConfirmationDialogComponent,
    MatDialogModule,
    MatButtonModule
  ]
})
export class SharedModule { }
