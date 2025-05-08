import { Component } from '@angular/core';
import { KeycloakService } from '../../../../core/keycloak/keycloak.service';
import {Notification} from './notification';
import {ToastrService} from 'ngx-toastr';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Component({
  selector: 'app-advertiser-menu',
  standalone: false,
  templateUrl: './advertiser-menu.component.html',
  styleUrls: ['./advertiser-menu.component.scss']
})
export class AdvertiserMenuComponent {
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
            `/topic/Advertiser`,
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
        case 'SPONSOR_OFFER_CREATED':
          this.toastService.info(notification.message, 'New Sponsor Offer CREATED');
          break;
        case 'SPONSOR_OFFER_UPDATED':
          this.toastService.info(notification.message, 'A Sponsor Offer has been UPDATED');
          break;
        case 'SPONSOR_OFFER_DELETED':
          this.toastService.info(notification.message, 'A Sponsor Offer has been DELETED');
          break;
        case 'SPONSORSHIP_UPDATED':
          this.toastService.success(notification.message, 'Sponsorship Created');
          break;
        case 'SPONSORSHIP_DELETED':
          this.toastService.info(notification.message, 'Sponsorship Deleted');
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
