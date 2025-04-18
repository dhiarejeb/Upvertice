import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';

import { Observable, from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import {KeycloakService} from '../keycloak/keycloak.service';


@Injectable({
  providedIn: 'root'
})
export class HttpTokenInterceptorService implements HttpInterceptor {
  constructor(private keycloakService: KeycloakService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const keycloak = this.keycloakService.keycloak;

    return from(keycloak.updateToken(5)).pipe( // Try to refresh token if it expires in < 5s
      switchMap(() => {
        const token = keycloak.token;

        if (token) {
          const authReq = request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          });
          return next.handle(authReq);

        }

        return next.handle(request);
      })
    );


  }

  /*constructor(
    private keycloakService: KeycloakService

  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.keycloakService.keycloak.token;
    if (token) {
      const authReq = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next.handle(authReq);
    }
    return next.handle(request);
  }*/
}
