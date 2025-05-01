import { Component } from '@angular/core';
import {Router} from '@angular/router';
import { KeycloakService } from '../../core/keycloak/keycloak.service';

@Component({
  selector: 'app-role-redirect',
  standalone: false,
  templateUrl: './role-redirect.component.html',
  styleUrls: ['./role-redirect.component.scss']
})
export class RoleRedirectComponent {
  constructor(
    private router: Router,
    private keycloakService: KeycloakService) {}

  ngOnInit(): void {
    const roles = this.keycloakService.getUserRoles();

    if (roles.includes('Admin')) {
      this.router.navigate(['/admin']);
    } else if (roles.includes('Advertiser')) {
      this.router.navigate(['/advertiser']);
    }else if (roles.includes('Provider')) {
      this.router.navigate(['/provider']);
    }else {
      // fallback if role doesn't match
      this.router.navigate(['/unauthorized']);
    }
  }

}
