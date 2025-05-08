import {Component, OnInit} from '@angular/core';
import {UserResponse} from '../../../services/models/user-response';
import {UserControllerService} from '../../../services/services/user-controller.service';

import {UserUpdateRequest} from '../../../services/models/user-update-request';
import {PageResponseUserResponse} from '../../../services/models/page-response-user-response';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-user-management',
  standalone: false,
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {
  users: UserResponse[] = [];
  selectedUser: UserResponse | null = null;
  profilePhoto: File | null = null;  // Variable to hold the selected image file

  constructor(
    private userService: UserControllerService,
    private toastService: ToastrService // Inject toastr service
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(page: number = 0, size: number = 10): void {
    this.userService.getUsers({ page, size }).subscribe({
      next: (response: PageResponseUserResponse) => {
        this.users = response.content || [];
        this.toastService.success('Users loaded successfully!'); // Success toast
      },
      error: (error) => {
        console.error('Error loading users', error);
        this.toastService.error('Failed to load users. Please try again later.'); // Error toast
      }
    });
  }

  updateUserInKeycloak(user: UserResponse, newRole: string): void {
    if (this.selectedUser) {
      const updatedUser = { ...this.selectedUser, role: newRole };

      this.userService.updateUserInKeycloak({
        body: {
          userUpdateRequest: updatedUser,
          profilePhoto: this.profilePhoto ?? new Blob() // If no photo, send an empty blob
        }
      }).subscribe({
        next: (response) => {
          console.log('User updated successfully', response);
          this.toastService.success('User updated successfully!'); // Success toast
          this.loadUsers(); // Reload users after update
        },
        error: (error) => {
          console.error('Error updating user', error);
          this.toastService.error('Failed to update user. Please try again later.'); // Error toast
        }
      });
    }
  }

  deleteUserFromKeycloak(userId: string): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUserFromKeycloak({ userId }).subscribe({
        next: () => {
          console.log('User deleted successfully');
          this.toastService.success('User deleted successfully!'); // Success toast
          this.loadUsers(); // Reload users after deletion
        },
        error: (error) => {
          console.error('Error deleting user', error);
          this.toastService.error('Failed to delete user. Please try again later.'); // Error toast
        }
      });
    }
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.profilePhoto = file;
    }
  }

}
