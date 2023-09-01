import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {AuthService} from "./auth.service";

@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(private authService: AuthService, private router: Router) {
  }

  /**
   *                Function checks authorization after each page switch,
   *                when any of given routes is protected by {@link AuthService}.
   * @param route   Given url route
   * @param state   Router snapshot
   */

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.authService.isAuthenticated()
      .then(
        (authenticated: boolean) => {
          if (authenticated) {
            return true;
          } else {
            this.router.navigate(['/auth']);
            return false;
          }
        }
      );
  }

  /**
   *                    Function which protects also child routes of given one by {@link AuthService}.
   * @param childRoute  Given url route
   * @param state       Router snapshot
   */

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.canActivate(childRoute, state);
  }
}
