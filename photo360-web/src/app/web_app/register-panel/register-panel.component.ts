import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../general/auth/auth.service";
import {passwordMatchingValidator} from "../../general/directives/password-validator.directive";
import {emailValidator} from "../../general/directives/email-regex-validator.directive";
import {emailInputValidator} from "../../general/directives/email-input-validator.directive";
import {ActivatedRoute, Router} from "@angular/router";
import {RegisterRequestDto} from "../../general/interface/interface";

@Component({
  selector: 'app-register-panel',
  templateUrl: './register-panel.component.html',
  styleUrls: ['./register-panel.component.css']
})
export class RegisterPanelComponent implements OnInit {

  registrationForm!: FormGroup;
  isLoading = false;
  error: string = "";
  info: string = "";
  password = "";
  hidePassword = true;

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  onSubmit() {
    if (this.registrationForm.invalid) return
    this.info = ""
    this.error = ""
    const login = this.registrationForm.value.login;
    const password = this.registrationForm.value.password;
    const email = this.registrationForm.value.email;
    this.isLoading = true;

    let requestDto: RegisterRequestDto = {
      email: email,
      login: login,
      password: password
    }

    this.authService.createNewUser(requestDto).subscribe(
      (result) => {
        this.isLoading = false;
        this.router.navigate(['/auth'], {
          relativeTo: this.route,
          queryParams: {registrationMessage: result.responseMessage, userEmail: email},
          skipLocationChange: true
        })
      }, errorMessage => {
        this.isLoading = false;
        this.error = errorMessage
      })
  }

  private initForm() {
    let login = '';
    let password = '';
    let email = '';
    let passwordRepeat = '';

    this.registrationForm = new FormGroup({
      'login': new FormControl(login, [Validators.required]),
      'email': new FormControl(email, [Validators.required, emailValidator()]),
      'password': new FormControl(password, [Validators.required]),
      'passwordRepeat': new FormControl(passwordRepeat, [Validators.required]),
    }, {validators: [passwordMatchingValidator, emailInputValidator]});
  }
}
