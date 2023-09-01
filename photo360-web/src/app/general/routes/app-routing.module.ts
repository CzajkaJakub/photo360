import {NgModule} from '@angular/core';
import {NotLoggedInGuard} from "../auth/not.logged-guard.service";
import {RouterModule, Routes} from "@angular/router";
import {AuthGuard} from "../auth/auth-guard.service";
import {HomePageComponent} from "../../web_app/home-page/home-page.component";
import {LoggingPanelComponent} from "../../web_app/logging-panel/logging-panel.component";
import {RegisterPanelComponent} from "../../web_app/register-panel/register-panel.component";
import {ChangePasswordPanelComponent} from "../../web_app/change-password-panel/change-password-panel.component";
import {PageNotFoundComponent} from "../../web_app/page-not-found/page-not-found.component";

/**
 * All current supported routes.
 */
const appRoutes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: HomePageComponent},
  {path: 'auth', canActivate: [NotLoggedInGuard], component: LoggingPanelComponent},
  {path: 'register', canActivate: [NotLoggedInGuard], component: RegisterPanelComponent},
  {path: 'change-password', canActivate: [AuthGuard], component: ChangePasswordPanelComponent},
  {path: '**', redirectTo: '/not-found'},
  {path: 'not-found', component: PageNotFoundComponent, data: {errorMessage: "Page not found!"}},
]

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {
}

