import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../general/auth/auth.service";
import {ToastComponent} from "../../general/toast/toast.component";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {Constants} from '../../general/properties/properties';
import {User} from "../../general/auth/user-mode";
import {ImageUploaderService} from "../../general/data/image.uploader.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogModule} from "@angular/material/dialog";
import {UploadImagesConfig} from "../../general/interface/interface";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {AsyncPipe, NgForOf, SlicePipe} from "@angular/common";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatIconModule} from "@angular/material/icon";
import {MatCheckboxModule} from "@angular/material/checkbox";

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

  private uploadedImagesConfig: UploadImagesConfig = {
    zipFile: null!,
    isPublic: false,
    description: null!,
    title: null!
  };

  constructor(public authService: AuthService,
              private toast: ToastComponent,
              public translateService: TranslateService,
              private imageUploaderService: ImageUploaderService,
              private dialog: MatDialog) {

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

  openSavePortfolioDialog() {
    const dialogRef = this.dialog.open(UploadImagesDialogConfig, {
      disableClose: true,
      data: this.uploadedImagesConfig,
      autoFocus: false,
      width: "450px"
    });

    dialogRef.afterClosed().subscribe(dialogConfig => {
      if (dialogConfig !== null) {
        if (this.uploadedImagesConfig.zipFile != null && this.uploadedImagesConfig.description != null && this.uploadedImagesConfig.title != null) {
          this.imageUploaderService.uploadFilesWithImages(this.uploadedImagesConfig);
        }
      }
    });
  }
}


@Component({
  selector: 'upload-images-config-dialog',
  templateUrl: 'upload-images-config-dialog-content.html',
  styles:
    [`
      .checkbox-section {
        display: flex;
        align-content: center;
        align-items: center;
        justify-content: center;
        height: 60px;
      }

      .margin {
        margin: 20px; /* Specify the actual margin value */
      }

      .file-input {
        display: none;
      }
    `],
  imports: [
    MatDialogModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDatepickerModule,
    MatOptionModule,
    MatSelectModule,
    NgForOf,
    TranslateModule,
    ReactiveFormsModule,
    AsyncPipe,
    MatAutocompleteModule,
    SlicePipe,
    MatIconModule,
    MatCheckboxModule
  ],
  standalone: true
})
export class UploadImagesDialogConfig {

  inputConfig = new FormControl('');

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  uploadFilesWithImages(e: Event) {
    let uploadedFile = (e.target as HTMLInputElement).files?.item(0)
    if (uploadedFile != null) this.data.zipFile = uploadedFile;
    (e.target as HTMLInputElement).value = ''
  }
}


