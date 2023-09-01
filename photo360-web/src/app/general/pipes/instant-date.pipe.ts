import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "instantDatePipe"
})
export class InstantDatePipe implements PipeTransform {

  transform(value: any): any {
    return value * 1000
  }
}
