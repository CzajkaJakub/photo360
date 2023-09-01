import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../general/auth/auth.service";
import {ToastComponent} from "../../general/toast/toast.component";
import {TranslateService} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {Constants} from '../../general/properties/properties';
import {User} from "../../general/auth/user-mode";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  protected isAuthenticated = false;
  protected currentlyLoggedUser!: User
  private userSubscription!: Subscription;
  private translationSubscription!: Subscription;

  constructor(public authService: AuthService,
              private toast: ToastComponent,
              public translateService: TranslateService) {

  }

  /**
   *    Init basic panel functions like create subscriptions to dynamic information switch, load project version and build date.
   */

  ngOnInit(): void {
    this.userSubscription = this.authService.user.subscribe(user => {
      this.isAuthenticated = !!user;
      if (this.isAuthenticated) {
        this.currentlyLoggedUser = user
      }
    });
  }

  /**
   *              Function which logs out from our current logged account.
   *              After that we will not be able to get main functions like get data
   *              or create charts. After all we get notification at bottom of site by {@link ToastComponent}
   *              with given message in current displayed language, which is located in {@link assets/i18n} folder.
   *              For now there is only simple authorization without tokens etc.
   */

  logout() {
    if (this.isAuthenticated) {
      this.authService.logoutConfirmation();
    } else {
      this.toast.openSnackBar(this.translateService.instant("toast.notLogged"))
    }
  }

  /**
   *              Function changes current language to the given one {@link lang},
   *              after all there will be also displayed notification at bottom of site by {@link ToastComponent}
   *              with given message in current displayed language, which is located in {@link assets/i18n} folder.
   *
   * @param lang  Language which will be used to translate all text on website.
   */

  changeLanguage(lang: string) {
    this.translationSubscription = this.translateService.use(lang).subscribe(() => {
      this.toast.openSnackBar(this.translateService.instant("toast.languageSwitch"));
    });
  }

  /**
   *              Returns a class for specific spans to create a flags next to language picker in header.
   *              All classes are stored in {@link flagsDictionary}.
   *
   * @param lang  Language which define what flag will be displayed.
   */
  getFlagClass(lang: string) {
    return Constants.flagsDictionary.get(lang);
  }

  /**
   *            Function clears subscription, after panel switch.
   */
  ngOnDestroy() {
    this.userSubscription.unsubscribe();
    this.translationSubscription.unsubscribe();
  }
}
