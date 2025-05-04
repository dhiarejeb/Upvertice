import { Component } from '@angular/core';
import {KeycloakService} from '../../../../core/keycloak/keycloak.service';

@Component({
  selector: 'app-supplier-menu',
  standalone: false,
  templateUrl: './supplier-menu.component.html',
  styleUrl: './supplier-menu.component.scss'
})
export class SupplierMenuComponent {
  constructor(private keycloakService: KeycloakService) {}

  logout() {
    this.keycloakService.logout();
  }

}
