import { Component } from '@angular/core';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';
import {Notification} from './notification';
import {ToastrService} from 'ngx-toastr';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Component({
  selector: 'app-provider-menu',
  standalone: false,
  templateUrl: './provider-menu.component.html',
  styleUrl: './provider-menu.component.scss'
})
export class ProviderMenuComponent {
  private socketClient: any = null;
  private userNotificationSubscription: any;
  private broadcastSubscription: any;
  unreadNotificationsCount = 0;
  notifications: Array<Notification> = [];

  constructor(
    private keycloakService: KeycloakService,
    private toastService: ToastrService
  ) {
  }

  ngOnInit(): void {
    const userId = this.keycloakService.keycloak.tokenParsed?.sub;
    const token = this.keycloakService.keycloak.token;

    if (userId && token) {
      const ws = new SockJS('http://localhost:8088/api/v1/ws');
      this.socketClient = Stomp.over(ws);

      this.socketClient.connect(
        {Authorization: 'Bearer ' + token},
        () => {
          // Subscribe to personal user notifications
          this.userNotificationSubscription = this.socketClient.subscribe(
            `/user/${userId}/notifications`,
            (message: any) => this.handleNotification(message)
          );

          // Optionally: Subscribe to role-based broadcast for Admins
          this.broadcastSubscription = this.socketClient.subscribe(
            `/topic/Provider`,
            (message: any) => this.handleNotification(message)
          );
        }
      );
    }
  }

  private handleNotification(message: any): void {
    const notification: Notification = JSON.parse(message.body);
    if (notification) {
      this.notifications.unshift(notification);
      this.unreadNotificationsCount++;

      switch (notification.status) {
        case 'PROVIDERSHIP_UPDATED':
          this.toastService.info(notification.message, ' Providership Updated');
          break;
        case 'PROVIDERSHIP_DELETED':
          this.toastService.info(notification.message, 'Providership Deleted');
          break;
        case 'SUPPLIER_TRANSACTION_CREATED':
          this.toastService.info(notification.message, 'Supplier transaction Created');
          break;
        case 'SUPPLIER_TRANSACTION_UPDATED':
          this.toastService.success(notification.message, 'Supplier Transaction Updated');
          break;
        case 'SUPPLIER_TRANSACTION_DELETED':
          this.toastService.info(notification.message, 'Supplier Transaction  Deleted');
          break;
        default:
          this.toastService.info(notification.message, 'Notification');
      }
    }
  }
  logout() {
    this.keycloakService.logout();
  }

}
