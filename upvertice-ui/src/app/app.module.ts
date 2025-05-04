import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule, provideClientHydration, withEventReplay} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import { CommonModule } from '@angular/common';
import { ToastrModule } from 'ngx-toastr';
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


import {AdminRoutingModule} from './modules/admin/admin-routing.module';
import { RoleRedirectComponent } from './modules/role-redirect/role-redirect.component';
import {AdminModule} from './modules/admin/admin.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {SharedModule} from './modules/shared/shared.module';
import {ProviderModule} from './modules/provider/provider.module';
import {SupplierModule} from './modules/supplier/supplier.module';

export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}

@NgModule({
  declarations: [
    AppComponent,
    RoleRedirectComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule, // Add this for Angular Material
    AppRoutingModule,
    HttpClientModule,
    AdvertiserModule,
    MatDialogModule, // Use the full module instead of individual components
    BaseChartDirective,// Use NgChartsModule instead of BaseChartDirective
    AdminRoutingModule,
    CommonModule,
    AdminModule,
    SharedModule,
    ProviderModule,
    ToastrModule.forRoot({
      positionClass: 'toast-bottom-right',
      timeOut: 3000,
      progressBar: true,
    }),
    SupplierModule


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
    },

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
