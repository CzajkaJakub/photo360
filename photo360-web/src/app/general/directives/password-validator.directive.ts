import {AbstractControl, ValidationErrors, ValidatorFn} from "@angular/forms";

export const passwordMatchingValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('password');
  const confirmPassword = control.get('passwordRepeat');

  return (confirmPassword?.value === password?.value || confirmPassword?.value === '') ? null : {notMatched: true};
};
