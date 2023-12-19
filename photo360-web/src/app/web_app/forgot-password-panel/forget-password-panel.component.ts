import {Component, HostListener, OnInit} from "@angular/core";
import {RegisterRequestDto, ResetPasswordRequestDto} from "../../general/interface/interface";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../general/auth/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {emailValidator} from "../../general/directives/email-regex-validator.directive";
import {passwordMatchingValidator} from "../../general/directives/password-validator.directive";
import {emailInputValidator} from "../../general/directives/email-input-validator.directive";
import {ToastComponent} from "../../general/toast/toast.component";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'forgot-password-panel',
  templateUrl: './forget-password-panel.component.html',
  styleUrls: ['./forget-password-panel.component.css']
})
export class ForgetPasswordPanel implements OnInit {

  registrationForm!: FormGroup;
  isLoading = false;
  error: string = "";
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
          },
          (error) => {
            console.error('Error resetting password:', error);
            this.toast.openSnackBar(this.translate.instant('toast.mailSentFailed'));
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
