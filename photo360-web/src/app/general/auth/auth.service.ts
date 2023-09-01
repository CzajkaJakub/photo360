import {ToastComponent} from "../toast/toast.component";
import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {User} from "./user-mode";
import {BehaviorSubject, catchError, tap} from "rxjs";
import {ConnectionConstants} from "../properties/properties";
import {ResponseStatusHandler} from "../response-status/response-status.service";
import {environment} from "src/environments/environment";
import {AuthResponseData, RequestResponse} from "../interface/interface";

/**
 *    Authorization service which are triggered after switch to protected route.
 */
@Injectable()
export class AuthService {

  user = new BehaviorSubject<User>(null!);
  private tokenExpirationTimer: any;

  constructor(private toast: ToastComponent,
              private router: Router,
              private translate: TranslateService,
              private httpClient: HttpClient,
              private responseStatusHandler: ResponseStatusHandler) {
  }

  /**
   *                Function checks and returns a {@link boolean} if user is authorized to get
   *                into any protected route.
   */
  isAuthenticated(): Promise<boolean> {
    return new Promise((resolve, reject) => {
      resolve(this.user.getValue() != null && this.user.getValue().token != null);
    });
  }

  /**
   *                  Request sender to authorize a user.
   * @param login     Typed login
   * @param password  Typed password
   */
  authorize(login: string, password: string) {
    return this.httpClient.post<AuthResponseData>(environment.REST_APP_HOST.concat(ConnectionConstants.authorizationUrl), {
      login: login,
      password: password,
    }).pipe(catchError((resultData) => this.responseStatusHandler.handleRequestError(resultData)), tap(resData => {
      console.log(resData)
      this.handleAuthentication(resData.email, resData._token, +resData._tokenExpirationDate, +resData._lastLoggedDatetime)
    }));
  }

  createNewUser(login: string, password: string, email: string) {
    password.normalize("NFKC");
    return this.httpClient.post<RequestResponse>(environment.REST_APP_HOST.concat(ConnectionConstants.createNewUserUrl), {
      login: login,
      password: password,
      email: email
    }).pipe(catchError((resultData) => this.responseStatusHandler.handleRequestError(resultData)),
      tap(resData => {
        this.responseStatusHandler.handleStatusMessage(resData)
      }));
  }

  /**
   *                            Set auto logout on token expiration time.
   * @param expirationDuration  Token expiration time.
   */
  autoLogout(expirationDuration: number) {
    this.tokenExpirationTimer = setTimeout(() => {
      this.toast.openSnackBar(this.translate.instant('toast.loggedOut'))
      this.clearCache()
    }, expirationDuration)
  }

  /**
   *   Read authorization token from local memory, it passes when token is still valid,
   *   if not we have to log in again.
   */
  autoLogin() {
    const userData: AuthResponseData = JSON.parse(localStorage.getItem('userData')!)
    if (!userData) {
      return
    }
    const loadedUser = new User(userData.email, userData._token, new Date(userData._tokenExpirationDate), +userData._lastLoggedDatetime)
    if (loadedUser.token) {
      const expirationDuration = new Date(userData._tokenExpirationDate).getTime() - new Date().getTime()
      this.autoLogout(expirationDuration)
      this.user.next(loadedUser)
    }
  }

  /**
   *                        Function which logout the user from the system, after confirmation we won't be able
   *                        to get into protected router.
   *                        After logout we will see notification, navigated to logging panel
   *                        and our data which were saved (time series ids, ranges will be cleared).
   */
  logoutConfirmation() {
    if (this.showConfirmToast(this.translate.instant('toast.logOutConfirm'))) {
      this.clearCache()
      this.toast.openSnackBar(this.translate.instant('toast.loggedOut'))
    }
  }

  forceLogoutConfirmation() {
    this.clearCache()
  }

  /**
   *                    Function normalizes and changes password users password)
   * @param email       Users email
   * @param oldPassword Users old password which is going to be changed
   * @param newPassword Users new password
   */
  changePassword(email: string, oldPassword: string, newPassword: string) {
    oldPassword.normalize("NFKC");
    newPassword.normalize("NFKC");
    return this.httpClient.put<RequestResponse>(environment.REST_APP_HOST.concat(ConnectionConstants.changePasswordUrl), {
      email: email,
      oldPassword: oldPassword,
      newPassword: newPassword,

    });
  }

  /**
   *                  Function creates a user login data,
   *                  and calculates expiration date, based on expiration time from server.
   * @param email     User's email.
   * @param token     Personal user's token.
   * @param expiresIn Expiration time
   * @param _lastLoggedDatetime Last user's logged date
   * @param activeDirectory User logged by active directory
   * @private
   */
  private handleAuthentication(email: string, token: string, expiresIn: number, _lastLoggedDatetime: number) {
    const expirationDate = new Date(new Date().getTime() + expiresIn);
    const user = new User(email, token, expirationDate, _lastLoggedDatetime)
    this.user.next(user);
    localStorage.setItem('userData', JSON.stringify(user))
    this.autoLogout(expiresIn)
    this.router.navigate(["/"])
    this.toast.openSnackBar(this.translate.instant('toast.loggedIn'))
  }

  /**
   *                        Confirmation panel with message. Return boolean based on user choice.
   * @param message         Message
   * @private
   */
  private showConfirmToast(message: string) {
    return confirm(message);
  }

  /**
   *               Function clears our choices (saved time series ids, ranges, directions etc.)
   * @private
   */
  private clearCache() {
    this.user.next(null!);
    this.router.navigate(['/auth'])
    this.tokenExpirationTimer = null
    localStorage.clear()
  }
}
