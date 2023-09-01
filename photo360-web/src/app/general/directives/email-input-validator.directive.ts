import {AbstractControl, ValidationErrors, ValidatorFn} from "@angular/forms";

export const emailInputValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const email = control.get('email');

  return (email?.valid || (!email?.dirty && email?.untouched)) ? null : {emailNotValid: true};
};
