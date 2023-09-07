import {Injectable} from "@angular/core";
import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {ResponseStatus} from "./response-codes";
import {RequestResponse} from "../interface/interface";
import {Constants} from "../properties/properties";

@Injectable()
export class ResponseStatusHandler {

  constructor(private translate: TranslateService) {
  }

  public handleStatusMessage(status: RequestResponse) {
    let message;
    switch (status.responseMessage) {
      case ResponseStatus.STATUS_USER_CREATED:
        message = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_USER_CREATED));
        break;
      case ResponseStatus.STATUS_PASSWORD_CHANGED:
        message = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_PASSWORD_CHANGED));
        break;
    }
    status.responseMessage = message;
  }


  /**
   *                    Error handler, to handle information from the server.
   * @param errorResult Error message
   * @private
   */
  public handleRequestError(errorResult: HttpErrorResponse) {
    console.log(errorResult)
    let errorMessage = this.translate.instant('error.unknownErrorOccurred');
    if (!errorResult.status) {
      errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_SERVER_UNREACHABLE));
    } else {
      switch (errorResult.error.responseMessage) {
        case ResponseStatus.STATUS_ACCOUNT_LOCKED:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_ACCOUNT_LOCKED));
          break;
        case ResponseStatus.STATUS_USER_NOT_FOUND_BY_LOGIN:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_USER_NOT_FOUND_BY_LOGIN));
          break;
        case ResponseStatus.STATUS_WRONG_CREDENTIALS:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_WRONG_CREDENTIALS));
          break;
        case ResponseStatus.STATUS_USER_NOT_FOUND_BY_EMAIL:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_USER_NOT_FOUND_BY_EMAIL));
          break;
        case ResponseStatus.STATUS_WRONG_PASSWORD:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_WRONG_PASSWORD));
          break;
        case ResponseStatus.STATUS_AUTH_TOKEN_EXPIRED:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_AUTH_TOKEN_EXPIRED));
          break;
        case ResponseStatus.STATUS_AUTH_TOKEN_NOT_VALID:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_AUTH_TOKEN_NOT_VALID));
          break;
        case ResponseStatus.STATUS_WRONG_PUBLIC_API_KEY:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_WRONG_PUBLIC_API_KEY));
          break;
        case ResponseStatus.STATUS_FIELD_CONTAINS_WHITESPACES:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_FIELD_CONTAINS_WHITESPACES));
          break;
        case ResponseStatus.STATUS_EMAIL_WRONG_FORMAT:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_EMAIL_WRONG_FORMAT));
          break;
        case ResponseStatus.STATUS_EMAIL_ALREADY_EXISTS:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_EMAIL_ALREADY_EXISTS));
          break;
        case ResponseStatus.STATUS_LOGIN_ALREADY_EXISTS:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_LOGIN_ALREADY_EXISTS));
          break;
        case ResponseStatus.STATUS_WRONG_FIELD_SIZE:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_WRONG_FIELD_SIZE));
          break;
        case ResponseStatus.STATUS_UNAUTHORIZED_ROLE:
          errorMessage = this.translate.instant(Constants.serverStatusTranslatePrefix.concat(ResponseStatus.STATUS_UNAUTHORIZED_ROLE));
          break;
      }
    }

    return throwError(errorMessage);
  }
}
