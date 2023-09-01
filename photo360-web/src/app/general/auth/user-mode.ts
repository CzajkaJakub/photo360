/**
 * User data model.
 */

export class User {

  constructor(public email: string, private _token: string, private _tokenExpirationDate: Date, private _lastLoggedDatetime: number) {
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

  get lastLoggedDatetime(): number {
    return this._lastLoggedDatetime;
  }
}
