
import { Component, OnInit, OnDestroy } from "@angular/core";
import { ClusterService } from "../cluster.service";
import { Router } from "@angular/router";
import { Subscription } from "rxjs";

@Component({
	selector: "app-cluster-list",
	templateUrl: "./cluster-list.component.html",
	styleUrls: ["./cluster-list.component.scss"]
})
export class ClusterListComponent implements OnInit,OnDestroy {

	private subscription : Subscription = new Subscription();



	constructor(
		private clusterService: ClusterService,	
		private router: Router
	) {
		
	
	}

	clusterList: { [key: string]: any } = {};

	ngOnInit() {
		window.scrollTo(0, 0);
		this.fetchClustersList();
	}

	fetchClustersList() {
		this.subscription = this.clusterService.getClusterList().subscribe(
			data => {
				this.clusterList = data;
			},
			error => {
				console.error(error);
			}
		);
	}



	editCluster(id) {
		this.router.navigate(["/add-cluster/", id]);
	}

	deleteClusterName(clusterName) {
		this.clusterService.deleteCluster(clusterName).subscribe(
			res => {
				this.fetchClustersList();
			},
			err => {
				console.log(err);
			}
		);
	}



 ngOnDestroy() {
	 this.subscription.unsubscribe();	
 }
	
}
