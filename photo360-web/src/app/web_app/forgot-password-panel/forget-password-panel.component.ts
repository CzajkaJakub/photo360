import {Component, HostListener, OnInit} from "@angular/core";
import {
  ChangePasswordRequestDto,
  RegisterRequestDto,
  ResetPasswordConfirmationRequestDto,
  ResetPasswordRequestDto
} from "../../general/interface/interface";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../general/auth/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {emailValidator} from "../../general/directives/email-regex-validator.directive";
import {passwordMatchingValidator} from "../../general/directives/password-validator.directive";
import {emailInputValidator} from "../../general/directives/email-input-validator.directive";
import {ToastComponent} from "../../general/toast/toast.component";
import {TranslateService} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {User} from "../../general/auth/user-mode";

@Component({
  selector: 'forgot-password-panel',
  templateUrl: './forget-password-panel.component.html',
  styleUrls: ['./forget-password-panel.component.css']
})
export class ForgetPasswordPanel implements OnInit {

  registrationForm!: FormGroup;
  forgetPasswordForm!: FormGroup;
  forgetFormTurn = false;
  isLoading = false;
  error: string = "";
  hidePassword = true;
  info: string = "";
  password = "";
  rectangleWidth = '100%';
  rectangleHeight = '100%';
  containerHeight = window.innerHeight * 0.90 + 'px';
  containerWidth = window.innerWidth * 0.35 + 'px';

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute, private toast: ToastComponent, private translate: TranslateService) {
  }

  ngOnInit(): void {
    this.initForm();
    this.initForgotForm();
  }

  onSubmit() {
    if (this.registrationForm.valid) {
      const email = this.registrationForm.get('email')?.value;

      let resetPasswordRequest: ResetPasswordRequestDto = {
        email: email
      };

      this.authService.resetUserPassword(resetPasswordRequest)
        .subscribe(
          () => {
            this.toast.openSnackBar(this.translate.instant('toast.mailSent'));
            this.forgetFormTurn = true;
          },
          (error) => {
            console.error('Error resetting password:', error);
            this.toast.openSnackBar(this.translate.instant('toast.mailSentFailed'));
          }
        );
    }
  }

  onSubmitForgetForm() {
    if (this.forgetPasswordForm.valid) {
      const email = this.forgetPasswordForm.get('forgetEmail')?.value;
      const newPassword = this.forgetPasswordForm.get('password')?.value;
      const resetPasswordToken = this.forgetPasswordForm.get('code')?.value;

      let forgetPasswordRequest: ResetPasswordConfirmationRequestDto = {
        email: email,
        newPassword: newPassword,
        resetPasswordToken: resetPasswordToken
      };

      this.authService.resetUserPasswordConfirmation(forgetPasswordRequest)
        .subscribe(
          (result) => {
            this.toast.openSnackBar(this.translate.instant('forgotPasswordPanel.changePasswordSuccess'));
          },
          (error) => {
            this.toast.openSnackBar(this.translate.instant('forgotPasswordPanel.changePasswordError'));
          }
        );
    }
  }

  private initForm() {
    let email = '';

    this.registrationForm = new FormGroup({
      'email': new FormControl(email, [Validators.required, emailValidator()])
    }, {validators: [emailInputValidator]});
  }

  private initForgotForm() {
    let email = '';
    let newPassword = '';
    let resetPasswordToken = '';

    this.forgetPasswordForm = new FormGroup({
      'forgetEmail': new FormControl(email, [Validators.required, emailValidator()]),
      'password': new FormControl(newPassword, [Validators.required]),
      'code': new FormControl(resetPasswordToken, [Validators.required])
    }, {validators: []});
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    const newContainerWidth = window.innerWidth * 0.35;
    const newContainerHeight = window.innerHeight * 0.9;
    const newRectangleWidth = newContainerWidth;
    const newRectangleHeight = newContainerHeight;

    this.containerWidth = newContainerWidth + 'px';
    this.containerHeight = newContainerHeight + 'px';
    this.rectangleWidth = newRectangleWidth + 'px';
    this.rectangleHeight = newRectangleHeight + 'px';
  }
}


// export class ChangePasswordPanelComponent implements OnInit {
//
//   changePasswordForm!: FormGroup;
//   activateKeyForm!: FormGroup;
//   isLoading = false;
//   error: string = "";
//   info: string = "";
//   password: string = "";
//   hideOldPass = true;
//   hideNewPass = true;
//   hideRepeatedPass = true;
//   containerHeight = '200px';
//   containerWidth = '500px';
//
//   protected isAuthenticated = false;
//   private userSubscription!: Subscription;
//   protected currentlyLoggedUser!: User
//
//
//   constructor(private authService: AuthService) {
//   }
//
//   ngOnInit(): void {
//     this.initForm();
//     this.userSubscription = this.authService.user.subscribe(user => {
//       this.isAuthenticated = !!user;
//       if (this.isAuthenticated) {
//         this.currentlyLoggedUser = user
//       }
//     });
//   }
//
//   onSubmit() {
//     if (this.changePasswordForm.invalid) return
//     this.info = "";
//     this.error = "";
//     const email = this.changePasswordForm.value.email;
//     const oldPassword = this.changePasswordForm.value.oldPassword;
//     const newPassword = this.changePasswordForm.value.password;
//     this.isLoading = true;
//
//     let requestDto: ChangePasswordRequestDto = {
//       newPassword: newPassword,
//       oldPassword: oldPassword
//     }
//
//     this.authService.changePassword(requestDto).subscribe(
//       (resultMessage) => {
//         this.isLoading = false;
//         this.info = resultMessage.responseMessage;
//       }, errorMessage => {
//         this.isLoading = false;
//         this.error = errorMessage
//       })
//   }
//
//   private initForm() {
//     let email = '';
//     let oldPassword = '';
//     let password = '';
//     let passwordRepeat = '';
//
//     this.changePasswordForm = new FormGroup({
//       'email': new FormControl(email, [Validators.required, emailValidator()]),
//       'oldPassword': new FormControl(oldPassword, [Validators.required]),
//       'password': new FormControl(password, [Validators.required]),
//       'passwordRepeat': new FormControl(passwordRepeat, [Validators.required]),
//     }, {validators: [passwordMatchingValidator, emailInputValidator]});
//   }
// }
