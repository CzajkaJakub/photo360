import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../general/auth/auth.service";
import {passwordMatchingValidator} from "../../general/directives/password-validator.directive";
import {emailValidator} from "../../general/directives/email-regex-validator.directive";
import {emailInputValidator} from "../../general/directives/email-input-validator.directive";
import {ChangePasswordRequestDto} from "../../general/interface/interface";

@Component({
  selector: 'app-change-password-panel',
  templateUrl: './change-password-panel.component.html',
  styleUrls: ['./change-password-panel.component.css']
})
export class ChangePasswordPanelComponent implements OnInit {

  changePasswordForm!: FormGroup;
  isLoading = false;
  error: string = "";
  info: string = "";
  password: string = "";
  hideOldPass = true;
  hideNewPass = true;
  hideRepeatedPass = true;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.initForm();

  }

  onSubmit() {
    if (this.changePasswordForm.invalid) return
    this.info = "";
    this.error = "";
    const email = this.changePasswordForm.value.email;
    const oldPassword = this.changePasswordForm.value.oldPassword;
    const newPassword = this.changePasswordForm.value.password;
    this.isLoading = true;

    let requestDto: ChangePasswordRequestDto = {
      newPassword: newPassword,
      oldPassword: oldPassword
    }

    this.authService.changePassword(requestDto).subscribe(
      (resultMessage) => {
        this.isLoading = false;
        this.info = resultMessage.responseMessage;
      }, errorMessage => {
        this.isLoading = false;
        this.error = errorMessage
      })
  }

  private initForm() {
    let email = '';
    let oldPassword = '';
    let password = '';
    let passwordRepeat = '';

    this.changePasswordForm = new FormGroup({
      'email': new FormControl(email, [Validators.required, emailValidator()]),
      'oldPassword': new FormControl(oldPassword, [Validators.required]),
      'password': new FormControl(password, [Validators.required]),
      'passwordRepeat': new FormControl(passwordRepeat, [Validators.required]),
    }, {validators: [passwordMatchingValidator, emailInputValidator]});
  }
}
