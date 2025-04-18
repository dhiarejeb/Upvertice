/*
import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule, provideClientHydration, withEventReplay} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BaseChartDirective} from 'ng2-charts';

import {
  HTTP_INTERCEPTORS,
  HttpClientModule,
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi
} from '@angular/common/http';

import {KeycloakService} from './core/keycloak/keycloak.service';
import {AdvertiserModule} from './modules/advertiser/advertiser.module';
import {HttpTokenInterceptorService} from './core/interceptor/http-token-interceptor.service';

import {MatDialogActions, MatDialogClose, MatDialogContent} from '@angular/material/dialog';



export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}
@NgModule({
  declarations: [
    AppComponent,



  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    AdvertiserModule,
    MatDialogClose,
    MatDialogContent,
    MatDialogActions,
    BaseChartDirective


  ],
  providers: [
    provideClientHydration(withEventReplay()),
    provideHttpClient(
      withInterceptorsFromDi(), // Permet d'utiliser les intercepteurs DI
      withFetch() // Active le fetch() natif (remplace XMLHttpRequest)
    ),
    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: (keycloak: KeycloakService) => () => keycloak.init(),
      deps: [KeycloakService],
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpTokenInterceptorService,
      multi: true
    }


  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
*/

import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule, provideClientHydration, withEventReplay} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BaseChartDirective} from 'ng2-charts'; // Import NgChartsModule instead of BaseChartDirective
import {
  HTTP_INTERCEPTORS,
  HttpClientModule,
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi
} from '@angular/common/http';

import {KeycloakService} from './core/keycloak/keycloak.service';
import {AdvertiserModule} from './modules/advertiser/advertiser.module';
import {HttpTokenInterceptorService} from './core/interceptor/http-token-interceptor.service';

import {MatDialogModule} from '@angular/material/dialog'; // Import the full module
//import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {SharedModule} from './modules/shared/shared.module';

export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    //BrowserAnimationsModule, // Add this for Angular Material
    AppRoutingModule,
    HttpClientModule,
    AdvertiserModule,
    MatDialogModule, // Use the full module instead of individual components
    BaseChartDirective,// Use NgChartsModule instead of BaseChartDirective
    SharedModule,


  ],
  providers: [
    provideClientHydration(withEventReplay()),
    provideHttpClient(
      withInterceptorsFromDi(),
      withFetch()
    ),
    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: kcFactory,
      deps: [KeycloakService],
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpTokenInterceptorService,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
