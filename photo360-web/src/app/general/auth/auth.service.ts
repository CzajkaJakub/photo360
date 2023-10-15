import {ToastComponent} from "../toast/toast.component";
import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";
import {User} from "./user-mode";
import {BehaviorSubject} from "rxjs";
import {ConnectionConstants} from "../properties/properties";
import {
  AuthResponseDataDto,
  ChangePasswordRequestDto,
  LoginRequestDto,
  RegisterRequestDto,
  RequestResponse,
  ResetPasswordConfirmationRequestDto,
  ResetPasswordRequestDto,
  UserEmailVerificationDto
} from "../interface/interface";
import {NetworkService} from "../network/network-server.service";

/**
 *    Authorization service which are triggered after switch to protected route.
 */
@Injectable()
export class AuthService {

  user = new BehaviorSubject<User>(null!);
  private tokenExpirationTimer: any;

  constructor(private router: Router,
              private toast: ToastComponent,
              private translate: TranslateService,
              private networkService: NetworkService) {
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
   * @param loginCredentials user data to authorize
   */
  authorize(loginCredentials: LoginRequestDto) {
    this.networkService.sendPostRequest<AuthResponseDataDto>(ConnectionConstants.authorizationUrl, loginCredentials, null, null)
      .subscribe(authorizeResponseData => {
        this.handleAuthentication(authorizeResponseData)
      });
  }

  createNewUser(registerRequestData: RegisterRequestDto) {
    registerRequestData.password.normalize("NFKC");
    return this.networkService.sendPostRequest<RequestResponse>(ConnectionConstants.createNewUserUrl, registerRequestData, null, null);
  }

  resetUserPassword(resetPasswordRequestDto: ResetPasswordRequestDto) {
    this.networkService.sendPostRequest<RequestResponse>(ConnectionConstants.resetPasswordRequestUrl, resetPasswordRequestDto, null, null);
  }

  resetUserPasswordConfirmation(resetPasswordConfirmationRequestDto: ResetPasswordConfirmationRequestDto) {
    this.networkService.sendPostRequest<RequestResponse>(ConnectionConstants.resetPasswordConfirmationUrl, resetPasswordConfirmationRequestDto, null, null);
  }

  sendEmailConfirmation() {
    this.networkService.sendGetRequest<RequestResponse>(ConnectionConstants.emailConfirmationRequestUrl, null, null);
  }

  confirmUserEmail(userEmailVerificationData: UserEmailVerificationDto) {
    this.networkService.sendGetRequest<RequestResponse>(ConnectionConstants.confirmEmailWithCodeRequestUrl, null, [userEmailVerificationData.emailVerificationToken]);
  }

  /**
   *                    Function normalizes and changes password users password
   * @param userData
   */
  changePassword(userData: ChangePasswordRequestDto) {
    userData.oldPassword.normalize("NFKC");
    userData.newPassword.normalize("NFKC");
    return this.networkService.sendPutRequest<RequestResponse>(ConnectionConstants.createNewUserUrl, userData, null, null);
  }

  /**
   *                            Set auto logout on token expiration time.
   * @param expirationDuration  Token expiration time.
   */
  autoLogout(expirationDuration: number) {
    this.tokenExpirationTimer = setTimeout(() => {
      this.toast.openSnackBar(this.translate.instant('toast.loggedOut'))
      this.logout()
    }, expirationDuration)
  }

  /**
   *   Read authorization token from local memory, it passes when token is still valid,
   *   if not we have to log in again.
   */
  autoLogin() {
    const userData: AuthResponseDataDto = JSON.parse(localStorage.getItem('userData')!)
    if (!userData) {
      return
    }

    const loadedUser = new User(userData.email, userData._token, new Date(userData._tokenExpirationDate), new Date(userData._lastLoggedDatetime), userData.roles)
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
      this.logout()
      this.toast.openSnackBar(this.translate.instant('toast.loggedOut'))
    }
  }

  /**
   *               Function clears our choices (saved time series ids, ranges, directions etc.)
   */
  logout() {
    this.user.next(null!);
    this.router.navigate(['/auth'])
    this.tokenExpirationTimer = null
    localStorage.clear()
  }

  /**
   *                  Function creates a user login data,
   *                  and calculates expiration date, based on expiration time from server.
   * @private
   * @param userData Currently logged user data, contains information like email, jwt token, roles, last logged datetime and token expiration date
   */
  private handleAuthentication(userData: AuthResponseDataDto) {
    const expirationDate = new Date(new Date().getTime() + userData._tokenExpirationDate);
    const user = new User(userData.email, userData._token, expirationDate, userData._lastLoggedDatetime, userData.roles)
    this.user.next(user);
    localStorage.setItem('userData', JSON.stringify(user))
    this.autoLogout(new Date(userData._tokenExpirationDate).getTime())
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
}
