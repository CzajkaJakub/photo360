import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AuthInterceptorService} from "./general/interceptors/auth.interceptor.service";
import {InstantDatePipe} from "./general/pipes/instant-date.pipe";
import {DropdownDirective} from "./general/directives/dropdown.directive";
import {ToastComponent} from "./general/toast/toast.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppRoutingModule} from "./general/routes/app-routing.module";
import {I18nModule} from "./general/internationalisation/i18n.module";
import {PasswordStrengthMeterModule} from "angular-password-strength-meter";
import {HeaderComponent} from "./web_app/header/header.component";
import {PageNotFoundComponent} from "./web_app/page-not-found/page-not-found.component";
import {HomePageComponent} from "./web_app/home-page/home-page.component";
import {LoggingPanelComponent} from "./web_app/logging-panel/logging-panel.component";
import {
  LoggingPanelSpinnerComponent
} from "./general/common-panels/logging-panel-spinner/logging-panel-spinner.component";
import {RegisterPanelComponent} from "./web_app/register-panel/register-panel.component";
import {ChangePasswordPanelComponent} from "./web_app/change-password-panel/change-password-panel.component";
import {DataVisualizerComponent} from "./web_app/data-visualizer.component";
import {AuthService} from "./general/auth/auth.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {AuthGuard} from "./general/auth/auth-guard.service";
import {NotLoggedInGuard} from "./general/auth/not.logged-guard.service";
import {ResponseStatusHandler} from "./general/response-status/response-status.service";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS} from "@angular/material-moment-adapter";
import {ImageUploaderService} from "./general/data/image.uploader.service";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {MatNativeDateModule} from "@angular/material/core";


@NgModule({
  declarations: [
    AppComponent,
    DropdownDirective,
    ToastComponent,
    InstantDatePipe,

    HeaderComponent,
    PageNotFoundComponent,
    DataVisualizerComponent,
    HomePageComponent,
    LoggingPanelComponent,
    LoggingPanelSpinnerComponent,
    RegisterPanelComponent,
    ChangePasswordPanelComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    I18nModule,
    MatDatepickerModule,
    I18nModule,
    MatDialogModule,
    PasswordStrengthMeterModule.forRoot(),
  ],
  providers: [MatSnackBar, ToastComponent, AuthService, AuthGuard, NotLoggedInGuard, ResponseStatusHandler, ImageUploaderService, MatDialog,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true
    },
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
      useValue: {
        useCest: true
      }
    },
  ],
  exports: [
    InstantDatePipe
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
