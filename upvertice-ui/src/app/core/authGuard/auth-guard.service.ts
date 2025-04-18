import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from '../keycloak/keycloak.service';

export const authGuard: CanActivateFn = () => {
  const keycloakService = inject(KeycloakService);

  const router = inject(Router);


  if (typeof window === 'undefined') {
    // We are not in the browser (SSR), skip any redirects or login
    console.warn('Auth guard triggered during SSR. Skipping login.');
    return false;
  }
  // Check if token is expired using the service method
  if (keycloakService.isTokenExpired()) {
     keycloakService.login();
    return false;
  }


  const roles = keycloakService.getUserRoles();


 // Redirect to the appropriate route based on roles
  if (roles.includes('Advertiser')) {
    //router.navigate(['advertiser']);
    //return false;  // Allow access
    return true;
  }

  // 🔒 Future roles (once routes/pages exist)
  if (roles.includes('Admin')) {
    //router.navigate(['/admin/dashboard']);
    return true;

  }

  if (roles.includes('Supplier')) {
    // router.navigate(['/supplier/landing']);
    //return false;

  }

  if (roles.includes('Provider')) {
    // router.navigate(['/provider/workspace']);
    return false;

  }

  // 🚫 Unknown role or unhandled
  router.navigate(['/unauthorized']);
  return true

};
