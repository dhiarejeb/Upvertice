import {Component, Inject, OnInit, PLATFORM_ID} from '@angular/core';

import {KeycloakService} from './core/keycloak/keycloak.service';
import {isPlatformBrowser} from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrls: ['./app.component.scss']
})
export class AppComponent{
  title = 'upvertice-ui';
  constructor(
    private keycloakService: KeycloakService,
    @Inject(PLATFORM_ID) private platformId: object
  ) {}


}
