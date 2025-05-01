import {Component, OnInit} from '@angular/core';
import {UserResponse} from '../../../services/models/user-response';
import {UserControllerService} from '../../../services/services/user-controller.service';

import {UserUpdateRequest} from '../../../services/models/user-update-request';
import {PageResponseUserResponse} from '../../../services/models/page-response-user-response';

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

  constructor(private userService: UserControllerService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(page: number = 0, size: number = 10): void {
    this.userService.getUsers({ page, size }).subscribe(
      (response: PageResponseUserResponse) => {
        this.users = response.content || [];
      },
      (error) => {
        console.error('Error loading users', error);
      }
    );
  }

  /*updateUserInKeycloak(user: UserResponse, newRole: string): void {
    if (this.selectedUser) {
      const updatedUser = { ...this.selectedUser, role: newRole };
      this.userService.updateUserInKeycloak({
        body: {
          userUpdateRequest: updatedUser,
          profilePhoto: new Blob() // Replace with actual profile photo logic if needed
        }
      }).subscribe(
        (response) => {
          console.log('User updated successfully', response);
          this.loadUsers();
        },
        (error) => {
          console.error('Error updating user', error);
        }
      );
    }
  }

  deleteUserFromKeycloak(userId: string): void {
    this.userService.deleteUserFromKeycloak({ userId }).subscribe(
      () => {
        console.log('User deleted successfully');
        this.loadUsers();
      },
      (error) => {
        console.error('Error deleting user', error);
      }
    );
  }*/


  updateUserInKeycloak(user: UserResponse, newRole: string): void {
    if (this.selectedUser) {
      const updatedUser = { ...this.selectedUser, role: newRole };

      this.userService.updateUserInKeycloak({
        body: {
          userUpdateRequest: updatedUser,
          profilePhoto: this.profilePhoto ?? new Blob()
        }
      }).subscribe(
        (response) => {
          console.log('User updated successfully', response);
          this.loadUsers();
        },
        (error) => {
          console.error('Error updating user', error);
        }
      );
    }
  }


  deleteUserFromKeycloak(userId: string): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUserFromKeycloak({ userId }).subscribe(
        () => {
          console.log('User deleted successfully');
          this.loadUsers();
        },
        (error) => {
          console.error('Error deleting user', error);
        }
      );
    }
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.profilePhoto = file;
    }
  }

}
