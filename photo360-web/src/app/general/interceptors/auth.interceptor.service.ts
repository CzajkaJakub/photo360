import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, exhaustMap, Observable, take, tap, throwError} from "rxjs";
import {AuthService} from "../auth/auth.service";
import {Injectable} from "@angular/core";
import {Constants} from "../properties/properties";
import {TranslateService} from "@ngx-translate/core";
import {ToastComponent} from "../toast/toast.component";
import {RequestResponse} from "../interface/interface";

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService, private translate: TranslateService, private toast: ToastComponent) {
  }

  // Request handler to transform output data to add api key or personal data token based on uri.
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authService.user.pipe(
      take(1),
      exhaustMap(user => {

        const modifiedRequest = req.clone({
          setHeaders: {
            'publicApiKey': Constants.publicApiKey,
            'Authorization': user?.token ?? ""
          }
        });

        return next.handle(modifiedRequest).pipe(
          catchError((error: any) => {

            if (!error.status) {
              let errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(Constants.STATUS_SERVER_UNREACHABLE));
              this.toast.openSnackBar(errorMessage)
              this.authService.logout()
            } else if (this.isRequestResponse(error.error) && error.error.responseMessage == Constants.STATUS_AUTH_TOKEN_NOT_VALID) {
              error.error.responseMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(Constants.STATUS_AUTH_TOKEN_NOT_VALID))
              this.authService.logout()
            } else {
              error.error.responseMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(error.error.responseMessage));
            }

            this.toast.openSnackBar(error.error.responseMessage)
            return throwError(error);
          }),

          tap((response) => {
            if (this.isRequestResponse(response)) {
              this.toast.openSnackBar(this.translate.instant(Constants.serverStatusTranslatePrefix.concat(response.responseMessage)))
            }
          })
        );
      })
    );
  }

  /**
   * Check that response from server is typed as RequestResponse interface
   * @param response server response
   */
  isRequestResponse(response: any): response is RequestResponse {
    return response && response.responseMessage !== undefined && response.statusType !== undefined && response.statusCode !== undefined;
  }
}
