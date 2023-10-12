import {AfterViewInit, Component, ElementRef, Renderer2, ViewChild} from '@angular/core';
import {Constants} from "../../properties/properties";

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
   *          Function shows panel with label, after {@link Constants.timeOfDataRequestWithoutShowingPanel} time
   *          to avoid flashed of label when request time is short.
   * @private
   */
  private showDelayedLoadingPanel() {
    setTimeout(() => {
      this.renderer.setStyle(this.componentContainers.nativeElement, "visibility", "visible")
    }, Constants.timeOfDataRequestWithoutShowingPanel)
  }
}
