import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule, provideClientHydration, withEventReplay} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule, provideHttpClient, withFetch, withInterceptorsFromDi} from '@angular/common/http';
import {KeycloakService} from './services/keycloak/keycloak.service'; // <-- Nouvelle importation


export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}
@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule


  ],
  providers: [
    provideClientHydration(withEventReplay()),
    provideHttpClient(
      withInterceptorsFromDi(), // Permet d'utiliser les intercepteurs DI
      withFetch() // Active le fetch() natif (remplace XMLHttpRequest)
    ),
    {
      provide: APP_INITIALIZER,
      useFactory: kcFactory,
      multi: true,
      deps: [KeycloakService] // Explicit dependency

    }


  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
