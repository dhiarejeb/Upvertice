import {Component, OnInit} from '@angular/core';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';

import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {Notification} from './notification'
import {ToastrService} from 'ngx-toastr';
@Component({
  selector: 'app-admin-menu',
  standalone: false,
  templateUrl: './admin-menu.component.html',
  styleUrls: ['./admin-menu.component.scss']
})
export class AdminMenuComponent implements OnInit {

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
            `/topic/Admin`,
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
        case 'SPONSORSHIP_CREATED':
          this.toastService.info(notification.message, 'New Sponsorship CREATED');
          break;
        case 'SPONSORSHIP_UPDATED':
          this.toastService.info(notification.message, 'A Sponsorship has been UPDATED');
          break;
        case 'SPONSORSHIP_DELETED':
          this.toastService.info(notification.message, 'A Sponsorship has been DELETED');
          break;
        case 'SUPPLIER_OFFER_CREATED':
          this.toastService.success(notification.message, 'New Supplier Offer Created');
          break;
        case 'SUPPLIER_OFFER_UPDATED':
          this.toastService.info(notification.message, 'A Supplier Offer has been Updated');
          break;
        case 'SUPPLIER_OFFER_DELETED':
          this.toastService.warning(notification.message, 'Supplier Offer Deleted');
          break;
        case 'SUPPLIER_TRANSACTION_CREATED':
          this.toastService.warning(notification.message, 'Supplier Transaction CREATED');
          break;
        case 'SUPPLIER_TRANSACTION_UPDATED':
          this.toastService.warning(notification.message, 'Supplier Transaction UPDATED');
          break;
        case 'SUPPLIER_TRANSACTION_DELETED':
          this.toastService.warning(notification.message, 'Supplier Transaction Deleted');
          break;
        case 'PROVIDERSHIP_CREATED':
          this.toastService.warning(notification.message, 'PROVIDERSHIP CREATED');
          break;
        case 'PROVIDERSHIP_UPDATED':
          this.toastService.warning(notification.message, 'PROVIDERSHIP UPDATED');
          break;
        case 'PROVIDERSHIP_DELETED':
          this.toastService.warning(notification.message, 'PROVIDERSHIP Deleted');
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




  /*socketClient : any = null;
  private notificationSubscription : any;
  unreadNotificationsCount = 0;
  notifications : Array<Notification> = [];


  constructor(
    private keycloakService: KeycloakService,
    private toastService : ToastrService) {}

  ngOnInit(): void {
        if (this.keycloakService.keycloak.tokenParsed?.sub){
          // /user/123568/notification (destination)
          let ws = new SockJs("http://localhost:8088/api/v1/ws");
          this.socketClient = Stomp.over(ws);
          this.socketClient.connect({'Authorization:' : 'Bearer' + this.keycloakService.keycloak.token},()=>{
            this.notificationSubscription = this.socketClient.subscribe(
              `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/notifications`,
              (message : any)=>{
                const notification: Notification = JSON.parse(message.body);
                if(notification){
                  this.notifications.unshift(notification);

                  switch(notification.status){
                    case'SPONSORSHIP_CREATED':
                      this.toastService.info(notification.message);
                      break;
                  }
                  this.unreadNotificationsCount++;

                }

            }
            ) // subscribe to a topic
          });
        }
    }*/






