import {AfterViewInit, Component, OnDestroy} from '@angular/core';
import {ActivatedRoute, Data} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.css']
})
export class PageNotFoundComponent implements AfterViewInit, OnDestroy {

  errorDataSubscription!: Subscription;
  errorMessage!: string;

  constructor(private route: ActivatedRoute) {
  }

  /**
   *            Function which gets error by given in param,
   *            and displays it to main panel. To trigger that for test,
   *            we can pass fake url at search bar on the browser.
   */
  ngAfterViewInit(): void {
    this.errorMessage = this.route.snapshot.data['errorMessage']
    this.errorDataSubscription = this.route.data.subscribe((data: Data) => {
      this.errorMessage = data['errorMessage']
    })
  }

  /**
   *            Function clears subscription, after panel switch.
   */
  ngOnDestroy(): void {
    this.errorDataSubscription.unsubscribe();
  }
}
