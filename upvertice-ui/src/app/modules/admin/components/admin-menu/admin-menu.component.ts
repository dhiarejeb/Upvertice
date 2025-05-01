import { Component } from '@angular/core';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';

@Component({
  selector: 'app-admin-menu',
  standalone: false,
  templateUrl: './admin-menu.component.html',
  styleUrls: ['./admin-menu.component.scss']
})
export class AdminMenuComponent {
  constructor(private keycloakService: KeycloakService) {}

  logout() {
    this.keycloakService.logout();
  }

}
