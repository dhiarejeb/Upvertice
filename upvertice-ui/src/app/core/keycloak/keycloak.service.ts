import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {UserProfile} from './UserProfile';
import Keycloak from 'keycloak-js';
import {isPlatformBrowser} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _profile: UserProfile | undefined;
  private _keycloak: Keycloak | undefined;
  private _isBrowser: boolean;


  constructor(@Inject(PLATFORM_ID) private platformId: string) {
    this._isBrowser = isPlatformBrowser(this.platformId);

  }

  get keycloak() {
    if (!this._keycloak && isPlatformBrowser(this.platformId)) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'Upvertice',
        clientId: 'upvertice-frontend'
      });
    }
    return this._keycloak!;
  }

  async init() {
    if (!isPlatformBrowser(this.platformId)) {
      console.warn("Skipping Keycloak init: Not running in the browser.");
      return;
    }

    const authenticated = await this.keycloak.init({
      onLoad: 'login-required',
    });

    if (authenticated) {
      this._profile = (await this.keycloak.loadUserProfile()) as UserProfile;
      this._profile.token = this.keycloak.token || '';
    }
  }

  get profile(): UserProfile | undefined {
    return this._profile;
  }





  login() {
     return this.keycloak.login();
   }


   logout() {
    // this.keycloak.accountManagement();
    return this.keycloak.logout({redirectUri: 'http://localhost:4200'});
   }


  getUserRoles(): string[] {
    return this.keycloak?.realmAccess?.roles || [];
  }

  hasRole(role: string): boolean {
    return this.getUserRoles().includes(role);
  }


  isTokenExpired(): boolean {
    return this.keycloak ? this.keycloak.isTokenExpired() : true;
  }





}
