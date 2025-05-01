import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserManagementComponent } from './user-management/user-management.component';
import {FormsModule} from "@angular/forms";



@NgModule({
  declarations: [
    UserManagementComponent
  ],
    imports: [
        CommonModule,
        FormsModule
    ]
})
export class SharedModule { }
