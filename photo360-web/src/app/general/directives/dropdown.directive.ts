import {Directive, ElementRef, HostBinding, HostListener} from '@angular/core';

@Directive({
  selector: '[appDropdown]'
})
export class DropdownDirective {

  @HostBinding('class.open') isOpen = false;

  constructor(private elRef: ElementRef) {
  }

  /**
   *              Function which close dropdown menu after click outside it.
   * @param event Event listener
   */
  @HostListener('document:click', ['$event']) toggleOpen(event: Event) {
    const clickedElement = event.target as HTMLElement;
    const isInputElement = clickedElement.tagName.toLowerCase() === 'input';
    if (!isInputElement) {
      this.isOpen = this.elRef.nativeElement.contains(event.target) ? !this.isOpen : false;
    }
  }
}
