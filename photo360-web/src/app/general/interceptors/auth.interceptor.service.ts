import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {exhaustMap, Observable, take} from "rxjs";
import {AuthService} from "../auth/auth.service";
import {Injectable} from "@angular/core";
import {Constants} from "../properties/properties";

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService) {
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

        return next.handle(modifiedRequest);
      })
    );
  }
}
