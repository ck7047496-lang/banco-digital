import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { importProvidersFrom } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './app/auth.interceptor';
import { provideRouter } from '@angular/router';
import { routes } from './app/app-routing.module'; // Assumindo que as rotas são exportadas como 'routes'

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      BrowserAnimationsModule,
      HttpClientModule
    ),
    provideRouter(routes), // Fornece as rotas para a aplicação
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ]
}).catch(err => console.error(err));