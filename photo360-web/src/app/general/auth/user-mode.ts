/**
 * User data model.
 */

export class User {

  constructor(public email: string, private _token: string, private _tokenExpirationDate: Date, private _lastLoggedDatetime: Date | null) {
  }

  /**
   *  Returns a personal token only if it is still valid, otherwise null.
   */
  get token(): string | null {
    if (!this.tokenExpirationDate || new Date() > this.tokenExpirationDate) {
      return null;
    }
    return this._token;
  }

  get tokenExpirationDate(): Date {
    return this._tokenExpirationDate;
  }

  get lastLoggedDatetime(): Date | number {
    return this._lastLoggedDatetime != null ? this._lastLoggedDatetime : 0;
  }
}
