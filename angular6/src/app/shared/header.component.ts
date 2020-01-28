import { Component, OnInit , Input} from "@angular/core";
import { Router} from "@angular/router";

@Component({
	selector: "app-header",
	template: `
		<mat-toolbar class="jumbune-header">
			<a routerLink="/clusters" class="jumbune-logo"> Jumbune </a>
			<span class="app-spacer"></span>
			<nav class="navbar navbar-expand-sm">		
			
		</nav>
		</mat-toolbar>
	`,
	styles: [
		`
			.jumbune-header {
				background-color: #1d78cd;
				color: white;
			}
			a {
				color: #fff;
				font-size: 26px;
				text-decoration: none;
				font-family: sans-serif;
			}
			.text-hidden {
				display:block;
			}
			.text-show {
				display:none;
			}

		`
	]
})
export class headerComponent implements OnInit {

	LOGO = "assets/images/logo.png";

	loginUser = null;
	successMessage;
	
	constructor(private router:Router) {

	}

	ngOnInit() {
		
		
	}

	
}
