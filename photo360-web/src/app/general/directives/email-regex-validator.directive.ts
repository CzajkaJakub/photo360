import {AbstractControl, ValidatorFn} from "@angular/forms";

export function emailValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const valid = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(control.value);
    return valid ? null : {'email': true};
  };
}
