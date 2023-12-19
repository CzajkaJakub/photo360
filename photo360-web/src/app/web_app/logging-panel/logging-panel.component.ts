import {Component, OnInit, HostListener} from '@angular/core';
import {AuthService} from "../../general/auth/auth.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthResponseDataDto, ChangePasswordRequestDto, LoginRequestDto} from "../../general/interface/interface";
import {tap} from "rxjs";

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
  rectangleWidth = '100%';
  rectangleHeight = '100%';
  containerHeight = window.innerHeight * 0.90 + 'px';
  containerWidth = window.innerWidth * 0.35 + 'px';

  constructor(private authService: AuthService, private activatedRoute: ActivatedRoute, private router: Router) {
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


    let requestDto: LoginRequestDto = {
      login: login,
      password: password
    }

    this.authService.authorize(requestDto).subscribe(
      (authorizeResponseData: AuthResponseDataDto) => {
        this.authService.handleAuthentication(authorizeResponseData);
      },
      error => {
        this.error = error.error.responseMessage
        this.isLoading = false;
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  private initForm() {
    let login = '';
    let password = '';

    this.loginForm = new FormGroup({
      'login': new FormControl(login, [Validators.required]),
      'password': new FormControl(password, [Validators.required]),
    });
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
