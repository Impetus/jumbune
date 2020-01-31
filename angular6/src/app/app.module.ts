import { Router } from '@angular/router';
import { HttpErrorInterceptor } from './shared/http-error.interceptor';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { CommonHttpClientService, applicationHttpClientCreator } from './shared/common-http-client.service';


import 'hammerjs'; 

import { RouterModule } from '@angular/router';
import { AppRoutingModule }     from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule }    from './core/core.module';
import { SharedModule }    from './shared/shared.module';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    CoreModule,
    SharedModule,
    RouterModule,
    AppRoutingModule,
    FormsModule,
    MatProgressBarModule
  ],
  providers: [
    //providing the CommonHttpClientService so it could be used as a service
   {
     provide: CommonHttpClientService,
     useFactory:applicationHttpClientCreator,
     deps: [HttpClient]
   },
   {
    provide: HTTP_INTERCEPTORS,
    useClass: HttpErrorInterceptor,
    multi: true,
    deps: [Router]
  }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
