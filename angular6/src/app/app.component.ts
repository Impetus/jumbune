
import { Component, OnInit, AfterViewInit } from "@angular/core";
import {
  Router,  
  NavigationStart,
  NavigationEnd,
  NavigationCancel
} from '@angular/router';

@Component({
	selector: "app-root",
	templateUrl: "./app.component.html",
	styleUrls: ["./app.component.scss"]
})
export class AppComponent implements OnInit, AfterViewInit {
	loading = true;
	constructor(private router: Router) {

	}

	ngOnInit() {
		this.loading = true;
	}

	ngAfterViewInit() {
		this.router.events
		.subscribe((event) => {
				if(event instanceof NavigationStart) {
						this.loading = true;
				}
				else if (
						event instanceof NavigationEnd || 
						event instanceof NavigationCancel
						) {
						this.loading = false;
				}
		});
  }

}
