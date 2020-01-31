import { Subscription } from 'rxjs';
import { Component, OnInit, OnDestroy } from "@angular/core";
import { Router, ActivatedRoute } from "@angular/router";
import { ClusterService } from "../cluster.service";

import {
	ToastyService,
	ToastyConfig,
	ToastOptions,
	ToastData
} from "ngx-toasty";

@Component({
	selector: "add-cluster",
	templateUrl: "./add-cluster.component.html",
	styleUrls: ["./add-cluster.component.scss"]
})
export class AddClusterComponent implements OnInit, OnDestroy {
	private subscription : Subscription = new Subscription();
	private maprSubscription: Subscription = new Subscription();
	private secureSubscription : Subscription = new Subscription();
	clusterObj: any;
	submitted = false;

	editMode = false;	
	isClusterNameRight: boolean = true;
	isMapr: boolean = false;
	isEmr: boolean = false;
	isSecurityEnabled:boolean =false;	
	routeClusterName: any;
		
	toastOptions: ToastOptions;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private clusterService: ClusterService,
		private toastyService: ToastyService,
		private toastyConfig: ToastyConfig,
	) {}

	ngOnInit() {
		window.scrollTo(0, 0);
		this.route.params.subscribe(res => {
			this.routeClusterName = res.action;
			this.getClusterDetails(this.routeClusterName);
		});

	

		this.maprSubscription = this.clusterService.getIsMaprDistribution().subscribe(
			res => {
				this.isMapr = res.isMapr;
				this.isEmr = res.isEmr;
			},
			err => {
				console.log(err);
			}
		);

		
		this.toastOptions = {
			title: "",
			msg: "",
			showClose: true,
			timeout: 3000
		};

	}

	onSubmit() {
		this.submitted = true;
	}

	goToClusterList() {
		this.router.navigate(["/clusters"]);
	}

	getClusterDetails(id) {
		if (id === "new") {
			this.editMode = false;
			this.clusterObj = {
				agents: {
					agents: [
						{
							host: "",
							port: ""
						}
					],
					haEnabled: false,
					hasPasswordlessAccess: false,
					password: "",
					sshAuthKeysFile: "",
					user: ""
				},
				clusterName: "",
				enableHostRange:false,
				hadoopUsers: {
					hasSingleUser: true
				},
				hostRangeFromValue: "",
				hostRangeToValue: "",
				jmxPluginEnabled: false,
				nameNodes: {
					haEnabled: false,
					hosts: [""],
					nameNodeJmxPort: ""
				},
				realm: "",
				taskManagers: {
					hosts: [""],
					rmHaEnabled: false,
					taskManagerJmxPort: ""
				},
				workers: {
					dataNodeJmxPort: "",
					hosts: [""],
					spotInstances: false,
					taskExecutorJmxPort: ""
				// },
				// authenticationMechanism: {
				// 	authType:"NONE",
				// 	username:"",
				// 	password:""
				// },
				}, zks: [
					{
						host: "",
						port: "2181"
					}
				]
			};
		} else {
			this.editMode = true;
			this.subscription = this.clusterService.getEditClusterDetails(id).subscribe(
				data => {
					this.setEditData(Object.assign({}, data));
				},
				error => {
					console.error(error);
				}
			);
		}
	}

	setEditData = function(data) {	
		this.clusterObj = data;
		if(this.clusterObj.enableHostRange == "FALSE" || this.clusterObj.enableHostRange == "false"){
			this.clusterObj.enableHostRange = false;
		}
		else{
			this.clusterObj.enableHostRange = true;
		}
		// if(!this.clusterObj.authenticationMechanism) {
		// 	this.clusterObj.authenticationMechanism = {
		// 		authType: "NONE",
		// 		username:"",
		// 		password:""

		// 	}
		// }
	
	};

	addZkHostPort = function() {
		this.clusterObj.zks.push({
			host: "",
			port: "2181"
		});
	};
	removeZkHostPort = function(index) {
		this.clusterObj.zks.splice(index, 1);
	};

	addRMHost = function() {
		this.clusterObj.taskManagers.hosts.push("");
	};

	removeRMHost = function(index) {
		this.clusterObj.taskManagers.hosts.splice(index, 1);		
	};

	/** create name node hosts when HA is enable */
	addNameNodeHost = function() {
		this.clusterObj.nameNodes.hosts.push("");		
	};
	/** remove  name node hosts when HA is enable */
	removeNameNodeHost = function(index) {
		this.clusterObj.nameNodes.hosts.splice(index, 1);
		
	};

	/** create hosts fields in worker node */
	addWorkerNodeHost = function() {
		this.clusterObj.workers.hosts.push("");
	};

	/** remove hosts fields in worker node */
	removeWorkerNodeHost = function(index) {
		this.clusterObj.workers.hosts.splice(index, 1);
	};

	/** Function to generate port fields in jumbune agent  */
	addAgentHostPort = function() {	
		this.clusterObj.agents.agents.push({
			host: "",
			port: ""
		});
	};
	/** Function to remove port fields in jumbune agent  */
	removeAgentHostPort = function(index) {
		this.clusterObj.agents.agents.splice(index, 1);		
	};

	analyzeClusterName(value) {
		let strongRegex = new RegExp("^[a-zA-Z0-9_]*$");
		this.isClusterNameRight = strongRegex.test(value);
	}

	refreshZookeeperArray(event) {
		if (event === true) {
			this.clusterObj.zks = [{ host: "", port: "2181" }];	
			this.clusterObj.nameNodes.hosts.splice(1);		
		}
	}

	refreshAgentArray(event){
		if (event === false) {
			this.clusterObj.agents.agents = [{ host: "", port: ""}];			
		}
	}

	refreshTaskManagerArray(event){
		if (event === false) {
			this.clusterObj.taskManagers.hosts.splice(1);
		}		
	}

	isNumber(evt) {
		evt = evt ? evt : window.event;
		var charCode = evt.which ? evt.which : evt.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
			return false;
		}
		return true;
	}

	

	trackByFn(index, item) {
    return index; // or item.id
  }

	/** Function to save cluster information */
	save() {
		for (let i = 0; i < this.clusterObj.agents.agents.length ; i++) {
			if ((!this.clusterObj.agents.agents[i]['host']) || (!this.clusterObj.agents.agents[i]['port']) ){
				this.clusterObj.agents.agents.splice(i, 1);
				i--;			
			}
		}
		if (this.clusterObj.nameNodes.haEnabled == false) {
			this.clusterObj.zks = [{ "host": "", "port": "2181" }];
		}
		else {
			for(let j=0; j < this.clusterObj.zks.length; j++){
				if( (!this.clusterObj.zks[j]['host']) || (!this.clusterObj.zks[j]['port'])){
					this.clusterObj.zks.splice(j, 1);
					j--;
				}
			}
		}

		if (this.clusterObj.nameNodes.haEnabled == true) {
			this.clusterObj.nameNodes.hosts = [];
		}
		else {
			for(let k=0; k < this.clusterObj.nameNodes.hosts.length; k++){
				if( (!this.clusterObj.nameNodes.hosts[k])){
					this.clusterObj.nameNodes.hosts.splice(k, 1);
					k--;
				}
			}
		}
		

		for(let m=0; m < this.clusterObj.taskManagers.hosts.length; m++){
			if( (!this.clusterObj.taskManagers.hosts[m])){
				this.clusterObj.taskManagers.hosts.splice(m, 1);
				m--;
			}
		}

		for(let n=0; n < this.clusterObj.workers.hosts.length; n++){
			if( (!this.clusterObj.workers.hosts[n])){
				this.clusterObj.workers.hosts.splice(n, 1);
				n--;
			}
		}		
		if (this.routeClusterName == "new" && !this.editMode) {			

			this.clusterService.addClusterConfiguration(this.clusterObj).subscribe(
				res => {		
					this.toastOptions.msg = "Cluster saved successfully!!!";
					this.toastyService.success(this.toastOptions);			
					setTimeout(() => {
						this.router.navigate(['/clusters']);
					}, 3000);	
				},
				err => {
					console.log(err);
				}
			);
		} else {

			this.editMode = true;
			if(this.clusterObj.enableHostRange === "true"){
				this.clusterObj.enableHostRange = "TRUE";
			}
			else{
				this.clusterObj.enableHostRange = "FALSE";
			}

			if(this.clusterObj.agents.hasPasswordlessAccess) {				
				this.clusterObj.agents.password = null;
			}
			if(!this.clusterObj.agents.hasPasswordlessAccess) {
				this.clusterObj.agents.sshAuthKeysFile = null;
			}

			if (this.clusterObj.nameNodes.haEnabled == true) {
				this.clusterObj.nameNodes.hosts = [];
			}
			if (this.clusterObj.nameNodes.haEnabled == false) {
				this.clusterObj.zks = [{ "host": "", "port": "2181" }];
			}
			this.clusterService
				.updateClusterConfiguration(this.routeClusterName, this.clusterObj)
				.subscribe(
					res => {
					this.toastOptions.msg = "Cluster updated successfully!!!";
					this.toastyService.success(this.toastOptions);		
						setTimeout(() => {
							this.router.navigate(['/clusters']);
						}, 3000);	
					},
					err => {
						console.log(err);
					}
				);
		}
	};

	//function to save and go cluster configuartion 
	saveAndGoConfiguration() {
		for (let i = 0; i < this.clusterObj.agents.agents.length ; i++) {
			if ((!this.clusterObj.agents.agents[i]['host']) || (!this.clusterObj.agents.agents[i]['port']) ){
				this.clusterObj.agents.agents.splice(i, 1);
				i--;			
			}
		}

		if (this.clusterObj.nameNodes.haEnabled == false) {
			this.clusterObj.zks = [{ "host": "", "port": "2181" }];
		} 
		else {
			for(let j=0; j < this.clusterObj.zks.length; j++){
				if( (!this.clusterObj.zks[j]['host']) || (!this.clusterObj.zks[j]['port'])){
					this.clusterObj.zks.splice(j, 1);
					j--;
				}
			}
		}
	

		if (this.clusterObj.nameNodes.haEnabled == true) {
			this.clusterObj.nameNodes.hosts = [];
		}
		else {
			for(let k=0; k < this.clusterObj.nameNodes.hosts.length; k++){
				if( (!this.clusterObj.nameNodes.hosts[k])){
					this.clusterObj.nameNodes.hosts.splice(k, 1);
					k--;
				}
			}
		}
		

		for(let m=0; m < this.clusterObj.taskManagers.hosts.length; m++){
			if( (!this.clusterObj.taskManagers.hosts[m])){
				this.clusterObj.taskManagers.hosts.splice(m, 1);
				m--;
			}
		}

		for(let n=0; n < this.clusterObj.workers.hosts.length; n++){
			if( (!this.clusterObj.workers.hosts[n])){
				this.clusterObj.workers.hosts.splice(n, 1);
				n--;
			}
		}		
		if (this.routeClusterName == "new" && !this.editMode) {			

			this.clusterService.addClusterConfiguration(this.clusterObj).subscribe(
				res => {		
					this.toastOptions.msg = "Cluster saved successfully!!!";
					this.toastyService.success(this.toastOptions);			
					setTimeout(() => {
						this.router.navigate(['/manage-cluster/'+this.clusterObj.clusterName]);
					}, 3000);	
				},
				err => {
					console.log(err);
				}
			);
		} else {

			this.editMode = true;
			if(this.clusterObj.enableHostRange === "true"){
				this.clusterObj.enableHostRange = "TRUE";
			}
			else{
				this.clusterObj.enableHostRange = "FALSE";
			}

			if (this.clusterObj.nameNodes.haEnabled == true) {
				this.clusterObj.nameNodes.hosts = [];
			}
			if (this.clusterObj.nameNodes.haEnabled == false) {
				this.clusterObj.zks = [{ "host": "", "port": "2181" }];
			}
			this.clusterService
				.updateClusterConfiguration(this.routeClusterName, this.clusterObj)
				.subscribe(
					res => {
					this.toastOptions.msg = "Cluster updated successfully!!!";
					this.toastyService.success(this.toastOptions);		
						setTimeout(() => {
							this.router.navigate(['/manage-cluster/'+this.clusterObj.clusterName]);
						}, 3000);	
					},
					err => {
						console.log(err);
					}
				);
		}
	};

 ngOnDestroy() {
	this.subscription.unsubscribe();
	this.maprSubscription.unsubscribe();
	this.secureSubscription.unsubscribe();
 }
	
}
