import {AfterViewInit, Component, ElementRef, Renderer2, ViewChild} from '@angular/core';

/**
 *  Value of time [ms], used to skip loading panel if time of request is very short.
 */

const timeOfDataRequestWithoutShowingPanel = 2000

@Component({
  selector: 'app-loading-panel',
  templateUrl: './loading-panel.component.html',
  styleUrls: ['./loading-panel.component.css']
})
export class LoadingPanelComponent implements AfterViewInit {

  @ViewChild("componentContainers") componentContainers!: ElementRef;

  constructor(private renderer: Renderer2) {
  }

  /**
   *          Init basics panel functions.
   */
  ngAfterViewInit(): void {
    this.showDelayedLoadingPanel()
  }

  /**
   *          Function shows panel with label, after {@link timeOfDataRequestWithoutShowingPanel} time
   *          to avoid flashed of label when request time is short.
   * @private
   */
  private showDelayedLoadingPanel() {
    setTimeout(() => {
      this.renderer.setStyle(this.componentContainers.nativeElement, "visibility", "visible")
    }, timeOfDataRequestWithoutShowingPanel)
  }
}
