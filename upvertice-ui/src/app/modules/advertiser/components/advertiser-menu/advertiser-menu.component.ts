import { Component } from '@angular/core';
import { KeycloakService } from '../../../../core/keycloak/keycloak.service';

@Component({
  selector: 'app-advertiser-menu',
  standalone: false,
  templateUrl: './advertiser-menu.component.html',
  styleUrls: ['./advertiser-menu.component.scss']
})
export class AdvertiserMenuComponent {
  constructor(private keycloakService: KeycloakService) {}

  logout() {
    this.keycloakService.logout();
  }

}
