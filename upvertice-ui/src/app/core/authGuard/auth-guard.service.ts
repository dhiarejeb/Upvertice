import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from '../keycloak/keycloak.service';

export const authGuard: CanActivateFn = (): boolean => {
  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  if (typeof window === 'undefined') {
    console.warn('Auth guard triggered during SSR. Skipping login.');
    return false;
  }

  if (keycloakService.isTokenExpired()) {
    keycloakService.login(); // triggers login flow
    return false;
  }

  return true;
};




  /*const roles = keycloakService.getUserRoles();

  if (roles.includes('Admin')) {
    router.navigate(['/admin']);
    return false; // We redirect manually
  }

  if (roles.includes('Advertiser')) {
    router.navigate(['/advertiser']);
    return false;
  }

  // fallback if no known role
  return false;
*/




 /*// Redirect to the appropriate route based on roles
  if (roles.includes('Advertiser')) {
    router.navigate(['advertiser']);
    //return false;  // Allow access
    return true;
  }

  // 🔒 Future roles (once routes/pages exist)
  if (roles.includes('Admin')) {
    router.navigate(['/admin']);
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
*/

