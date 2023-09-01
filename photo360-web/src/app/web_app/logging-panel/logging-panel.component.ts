import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../general/auth/auth.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-logging-panel',
  templateUrl: './logging-panel.component.html',
  styleUrls: ['./logging-panel.component.css']
})
export class LoggingPanelComponent implements OnInit {

  loginForm!: FormGroup;
  isLoading = false;
  error: string = "";
  info: string = "";
  hidePassword = true;

  constructor(private authService: AuthService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.initForm();
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['registrationMessage'] != undefined && params['userEmail'] != undefined) {
        this.info = `${params['registrationMessage']} (${params['userEmail']})`;
      }
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return
    const login = this.loginForm.value.login;
    const password = this.loginForm.value.password;
    this.isLoading = true;
    this.authService.authorize(login, password).subscribe(
      () => {
        this.isLoading = false;
      }, errorMessage => {
        this.isLoading = false;
        this.error = errorMessage
      })
  }

  private initForm() {
    let login = '';
    let password = '';

    this.loginForm = new FormGroup({
      'login': new FormControl(login, [Validators.required]),
      'password': new FormControl(password, [Validators.required]),
    });
  }
}
