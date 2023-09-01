import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, exhaustMap, Observable, take, throwError} from "rxjs";
import {AuthService} from "../auth/auth.service";
import {Injectable} from "@angular/core";
import {ResponseStatus} from "../response-status/response-codes";
import {ToastComponent} from "../toast/toast.component";
import {TranslateService} from "@ngx-translate/core";
import {Constants} from "../properties/properties";
import {mod} from "@zxcvbn-ts/core/dist/helper";

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService, private toast: ToastComponent, private translate: TranslateService) {
  }

  // Request handler to transform output data to add api key or personal data token based on uri.
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authService.user.pipe(
      take(1),
      exhaustMap(user => {

        const modifiedRequest = req.clone({
          setHeaders: {
            "Content-Type": "application/json",
            'publicApiKey': Constants.publicApiKey,
            'Authorization': user?.token ?? ""
          }
        });

        return next.handle(modifiedRequest).pipe(
          catchError((error: any) => {
            if (error instanceof HttpErrorResponse && error.error.responseMessage == ResponseStatus.STATUS_AUTH_TOKEN_NOT_VALID) {
              this.authService.forceLogoutConfirmation()
              this.toast.openSnackBar(this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_AUTH_TOKEN_NOT_VALID)))
            } else if (error instanceof HttpErrorResponse && error.status == 0) {
              this.toast.openSnackBar(this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_SERVER_UNREACHABLE)))
              this.authService.forceLogoutConfirmation()
            }
            return throwError(error);
          })
        );
      })
    );
  }
}
