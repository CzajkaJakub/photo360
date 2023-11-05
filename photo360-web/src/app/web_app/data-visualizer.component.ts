import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-data-visualizer',
  templateUrl: './data-visualizer.component.html',
  styleUrls: ['./data-visualizer.component.css']
})
export class DataVisualizerComponent implements OnInit, OnDestroy {

  constructor(private router: Router) {}

  ngOnInit() {
  }

  /**
   *            Function clears subscription, after panel switch.
   */
  ngOnDestroy(): void {
  }

}


