import {Component} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {Constants} from "../properties/properties";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-toast',
  template: ``
})
export class ToastComponent {

  constructor(private _snackBar: MatSnackBar,
              private translate: TranslateService) {
  }

  /**
   *                Function opens a notification (toast) at the bottom of the page,
   *                with specific message. {@link setTimeout} tells us about time of display.
   * @param message Message which will be displayed on toast.
   */

  openSnackBar(message: string) {
    this._snackBar.open(message, this.translate.instant('toast.close'));
    setTimeout(() => this._snackBar.dismiss(), Constants.toastDisplayTime)
  }
}
