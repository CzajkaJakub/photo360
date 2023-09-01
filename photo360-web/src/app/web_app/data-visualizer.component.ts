import {Component, OnDestroy, OnInit} from '@angular/core';

@Component({
  selector: 'app-data-visualizer',
  templateUrl: './data-visualizer.component.html',
  styleUrls: ['./data-visualizer.component.css']
})
export class DataVisualizerComponent implements OnInit, OnDestroy {

  ngOnInit(): void {

  }

  /**
   *            Function clears subscription, after panel switch.
   */
  ngOnDestroy(): void {

  }
}


