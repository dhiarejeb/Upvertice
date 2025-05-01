import { Component } from '@angular/core';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';

@Component({
  selector: 'app-provider-menu',
  standalone: false,
  templateUrl: './provider-menu.component.html',
  styleUrl: './provider-menu.component.scss'
})
export class ProviderMenuComponent {
  constructor(private keycloakService: KeycloakService) {}

  logout() {
    this.keycloakService.logout();
  }

}
