import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../general/auth/auth.service";
import {passwordMatchingValidator} from "../../general/directives/password-validator.directive";
import {emailValidator} from "../../general/directives/email-regex-validator.directive";
import {emailInputValidator} from "../../general/directives/email-input-validator.directive";
import {ChangePasswordRequestDto, UserEmailVerificationDto} from "../../general/interface/interface";
import {Subscription} from "rxjs";
import {User} from "../../general/auth/user-mode";
import {ToastComponent} from "../../general/toast/toast.component";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-change-password-panel',
  templateUrl: './change-password-panel.component.html',
  styleUrls: ['./change-password-panel.component.css']
})
export class ChangePasswordPanelComponent implements OnInit {

  changePasswordForm!: FormGroup;
  activateKeyForm!: FormGroup;
  isLoading = false;
  error: string = "";
  info: string = "";
  password: string = "";
  hideOldPass = true;
  containerHeight = '200px';
  containerWidth = '500px';

  protected isAuthenticated = false;
  private userSubscription!: Subscription;
  protected currentlyLoggedUser!: User


  constructor(private toast: ToastComponent, private authService: AuthService, private translate: TranslateService) {
  }

  ngOnInit(): void {
    this.initForm();
    this.userSubscription = this.authService.user.subscribe(user => {
      this.isAuthenticated = !!user;
      if (this.isAuthenticated) {
        this.currentlyLoggedUser = user
      }
    });
  }

  onSubmit() {
    if (this.changePasswordForm.invalid) return
    this.info = "";
    this.error = "";
    const oldPassword = this.changePasswordForm.value.oldPassword;
    const newPassword = this.changePasswordForm.value.newPassword;
    this.isLoading = true;

    let requestDto: ChangePasswordRequestDto = {
      newPassword: newPassword,
      oldPassword: oldPassword
    }

    this.authService.changePassword(requestDto).subscribe(
      (resultMessage) => {
        this.isLoading = false;
        this.info = resultMessage.responseMessage;
        this.changePasswordForm.reset();
        this.password = '';
        this.toast.openSnackBar(this.translate.instant('changePassword.changeSuccess'));
      }, errorMessage => {
        this.isLoading = false;
        this.error = errorMessage
      })
  }

  onSubmitKey(){
    if (this.activateKeyForm.invalid) return
    this.info = "";
    this.error = "";
    const emailVerificationToken = this.activateKeyForm.value.code;
    this.isLoading = true;

    let requestDtoEmail: UserEmailVerificationDto = {
      emailVerificationToken: emailVerificationToken,
    }

    this.authService.confirmUserEmail(requestDtoEmail).subscribe(
      (resultMessage) => {
        this.isLoading = false;
        this.activateKeyForm.reset();
        this.toast.openSnackBar(this.translate.instant('changePassword.keySuccess'));
      }, errorMessage => {
        console.log(errorMessage)
        this.isLoading = false;
        this.error = errorMessage
      })
  }

  private initForm() {
    let oldPassword = '';
    let newPassword = '';
    let emailVerificationToken = '';

    this.changePasswordForm = new FormGroup({
      'oldPassword': new FormControl(oldPassword, [Validators.required]),
      'newPassword': new FormControl(newPassword, [Validators.required]),
    }, {validators: []});

    this.activateKeyForm = new FormGroup({
      'code': new FormControl(emailVerificationToken, [Validators.required]),
    }, {validators: []});
  }
}
