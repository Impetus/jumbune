//import { ApplicationService } from './../../applications/application.service';
import { FormControl,FormGroup,Validators,FormGroupDirective } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ClusterService } from "./../cluster.service";
import { OnInit, OnDestroy, DebugElement,ViewChild } from "@angular/core";
import { Component, Inject } from "@angular/core";
import { Router, ActivatedRoute } from "@angular/router";
import "rxjs/add/operator/map";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material";
import {map, startWith} from 'rxjs/operators';
import {
	ToastyService,
	ToastyConfig,
	ToastOptions,
	ToastData
} from "ngx-toasty";


@Component({
	selector: "manage-cluster",
	templateUrl: "./manage-cluster.component.html",
	styleUrls: ["./manage-cluster.component.scss"]
})
export class ManageClusterComponent implements OnInit, OnDestroy {
	private subscription : Subscription = new Subscription();
	private queueSubscription : Subscription = new Subscription();
	private queueListSubscription: Subscription = new Subscription();
	private filterSubscription : Subscription = new Subscription();
	activeClusterName: any;
	emailConfiguration = {};
	haConfiguration = {};
	influxDBConfiguration = {};
	alertConfiguration = {};
	ticketConfiguration = {};
	alertActionConfiguration:any = {};
	backgroundProcessConfiguration = {};
	chargeBackConfigurations = {};
	RUMQueue = { queueName: "", executionEngine: "", vCore: "", memory: "" };
	RUMQueuesList:any = [];
	clusterQueuesList:any = [];
	ObjectQueue = { selectedQueueName: "", warningLevel: 60, criticalLevel: 80 };
	toastOptions: ToastOptions;
	selectedIndex: number = 0;
	numberErrorMsg = false;
	alertAction = {
		snmpTraps: {
			trapOID: "",
			ipAddress: "",
			port: ""
		}
	};

	isShowLoader: boolean = true;
	showQueue: boolean = false;
	loadQueueData: boolean = false;

	toAddSLAActionData: boolean = true;
	toEditSLAActionData: boolean = false;

	toAddQueueConfData: boolean = true;
	toEditQueueConfData: boolean = false;

	toEditAlertActionData: boolean = false;
	toAddAlertActionData: boolean = true;

	displaySnmpErrorMessage: boolean = false;
	queueAction = {};

	dISK_SPACE_UTILIZATIONFalse: boolean = false;
	uNDER_REPLICATED_BLOCKSFalse: boolean = false;
	hDFS_UTILIZATIONFalse: boolean = false;

	showWarningCriticalErrorMessage: boolean = false;
	toEditQueueUtilizationData: boolean = false;
	toEditGenericQueueUtilizationData: boolean = false;

	username: any;
	//isSecurityEnabled:boolean =  false;

	hideManageClusterButtons = true;
	isSecurityEnabled = false;
	inefficienciesEmailsArr = [];
	inefficienciesEmailAddress;
	selectableEmail = true;
  removableEmail = true;




	jdbcUserName = "";
	showJdbcUserNameInput = false;

	tempChargebackQueueList;
	tempExecutionEngineList;

	//filteredQueueName =  new FormControl();//filter queue
	//filteredQueueList;// list of queues

	//filteredUserName = new FormControl();//filter queue
	//;//list of users

	exclusionUserNameList;
	exclusionQueueNameList;
	exclusionTableArr = [];
	exclusionDataSource;
	showExclutionWarningMsg =  true;
	exclusionUserName  = new FormControl();
	exclusionQueueName =  new FormControl();
	exclusionAppName = new FormControl();

	selectedAppSla: string = 'job';

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private clusterService: ClusterService,
		private toastyService: ToastyService,
		private toastyConfig: ToastyConfig,
		public dialog: MatDialog
	) {
		this.route.params.subscribe(res => {
			this.activeClusterName = res.id;
		});
	}

	ngOnInit() {
		this.subscription = this.clusterService
			.getClusterConfiguration(this.activeClusterName)
			.subscribe(res => {
				this.isShowLoader = false;
				this.fillData(res);
			},
			err=> {
				this.isShowLoader = false;
				console.log(err);
			});

		this.queueSubscription = this.clusterService
			.getRUMQueuesList(this.activeClusterName)
			.subscribe(data => {
				this.loadQueueData = true;
				this.RUMQueuesList = data;
				this.tempChargebackQueueList = this.getComputedChargebackQueueList();
		    this.tempExecutionEngineList = this.getComputedQueueTypeList();
			},
			err=>{
				console.log(err);
			});

		this.clusterService
			.getRUMQueuesList(this.activeClusterName)
			.subscribe(data => {
				this.clusterQueuesList = data;
			},
			err=> {
				console.log(err);
			});

		//	this.fetchApplicationsFilterOptions();

		this.toastyConfig.theme = "bootstrap";

		this.toastOptions = {
			title: "",
			msg: "",
			showClose: true,
			timeout: 3000
		};

	//	this.tempChargebackQueueList = this.getComputedChargebackQueueList();
		//this.tempExecutionEngineList = this.getComputedQueueTypeList();
	}

	fillData(data) {
		this.emailConfiguration = data.emailConfiguration;
		this.haConfiguration = data.haConfiguration;
		this.influxDBConfiguration = data.influxDBConfiguration;
	
		this.alertConfiguration = data.alertConfiguration;
		this.ticketConfiguration = data.ticketConfiguration;
	
		this.backgroundProcessConfiguration = data.backgroundProcessConfiguration;

		if (data.backgroundProcessConfiguration == null) {
			this.backgroundProcessConfiguration = {
				processMap: {
					SYSTEM_METRICS: false,
					QUEUE_UTILIZATION: false,
					WORKER_NODES_UPDATER: false,
					//APPS_PERSISTER_PROCESS: false,
					///CONF_RECOMMENDER: false,
					//APPS_QUERY_USER_UPDTER:false,
					//WORKLOAD_COMPARISON:false
				},
				//eagerExecutions: {
					//MAPREDUCE : false,
					//TEZ : false,
					//SPARK : false
				//}
			};
		}

	

		this.alertActionConfiguration = data.alertActionConfiguration;
		// if (this.alertActionConfiguration == null) {
		// 	this.alertActionConfiguration = {
		// 		alertActions: [],
		// 		exclusionConditions:[],
		// 		inefficiencyEmailTo:[]
		// 	};
		// }
		//this.tempChargebackQueueList = this.getComputedChargebackQueueList();
		//this.tempExecutionEngineList = this.getComputedQueueTypeList();
	}

	showDiskSpaceUtlizationError = function() {
		if (
			Number(
				this.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION
					.warningLevel
			) >=
			Number(
				this.alertConfiguration.configurableAlerts.DISK_SPACE_UTILIZATION
					.criticalLevel
			)
		) {
			this.dISK_SPACE_UTILIZATIONFalse = true;
		} else {
			this.dISK_SPACE_UTILIZATIONFalse = false;
		}
	};

	showUnderReplicatedBlockError = function() {
		if (
			Number(
				this.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS
					.warningLevel
			) >=
			Number(
				this.alertConfiguration.configurableAlerts.UNDER_REPLICATED_BLOCKS
					.criticalLevel
			)
		) {
			this.uNDER_REPLICATED_BLOCKSFalse = true;
		} else {
			this.uNDER_REPLICATED_BLOCKSFalse = false;
		}
	};

	showHDFSUtilizationError = function() {
		if (
			Number(
				this.alertConfiguration.configurableAlerts.HDFS_UTILIZATION.warningLevel
			) >=
			Number(
				this.alertConfiguration.configurableAlerts.HDFS_UTILIZATION
					.criticalLevel
			)
		) {
			this.hDFS_UTILIZATIONFalse = true;
		} else {
			this.hDFS_UTILIZATIONFalse = false;
		}
	};



	showSLAActionData = function(index) {
		this.slaActionIndex = index;
		console.log(this.slaConfigurations.slaConfList[index])
		if (this.slaConfigurations.slaConfList[index].queue === '') {
			this.selectedAppSla = 'job';
		} else if (this.slaConfigurations.slaConfList[index].applicationName === '') {
			this.selectedAppSla = 'queue';
		}
		this.slaAction = Object.assign(
			{},
			this.slaConfigurations.slaConfList[index]
		);
		this.toAddSLAActionData = false;
		this.toEditSLAActionData = true;
	};

	updateSLAAction = function() {
		console.log(this.slaAction)
		if (this.selectedAppSla === 'job') {
			this.slaAction.queue = '';
		}
		if (this.selectedAppSla === 'queue') {
			this.slaAction.applicationName = '';
		}
		this.slaConfigurations.slaConfList[this.slaActionIndex] = this.slaAction;
		this.toEditSLAActionData = false;
		this.toAddSLAActionData = true;
		this.slaAction = {};
	};

	/** delete sla action configuration */
	deleteSLAAction = function(index) {
		this.slaConfigurations.slaConfList.splice(index, 1);
		this.slaAction = {};
		if (this.slaActionIndex == index) {
			this.toEditSLAActionData = false;
			this.toAddSLAActionData = false;
		}
	};



	isNumber(evt) {
		evt = evt ? evt : window.event;
		var charCode = evt.which ? evt.which : evt.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
			return false;
		}
		return true;
	}


isNumberKey(evt)

       {
				 evt = evt ? evt : window.event;
          var charCode = (evt.which) ? evt.which : evt.keyCode;
          if (charCode != 46 && charCode > 31
            && (charCode < 48 || charCode > 57))
             return false;

          return true;
       }



	addQueueAction = function() {

		this.queueAction = {
			queueName: this.RUMQueue.queueName,
			executionEngine: this.RUMQueue.executionEngine,
			vCore: this.RUMQueue.vCore,
			memory: this.RUMQueue.memory
		};
		this.chargeBackConfigurations.chargeBackConfList.push(this.queueAction);
		this.RUMQueue = {};
		this.tempChargebackQueueList = this.getComputedChargebackQueueList();


	};

	showQueueActionData = function(index) {
		this.queueActionIndex = index;
		this.RUMQueue = Object.assign(
			{},
			this.chargeBackConfigurations.chargeBackConfList[index]
		);
		this.toAddQueueConfData = false;
		this.toEditQueueConfData = true;
	};

	updateQueueAction = function() {
		this.chargeBackConfigurations.chargeBackConfList[
			this.queueActionIndex
		] = this.RUMQueue;
		this.toEditQueueConfData = false;
		this.RUMQueue = {};
		//this.tempChargebackQueueList = this.getComputedChargebackQueueList();
		//this.tempExecutionEngineList = this.getComputedQueueTypeList();
	};

	deleteQueueAction = function(index) {
		this.chargeBackConfigurations.chargeBackConfList.splice(index, 1);
		this.RUMQueue = {};
		if (this.queueActionIndex == index) {
			this.toEditQueueConfData = false;
			this.toAddQueueConfData = false;
		}
		this.tempChargebackQueueList = this.getComputedChargebackQueueList();
		this.tempExecutionEngineList = this.getComputedQueueTypeList();
	};

	/** Function disable Add and update button in RUM module */
	isDisabledQueue = function() {
		if (
			!this.RUMQueue.queueName ||
			!this.RUMQueue.executionEngine ||
			(!this.RUMQueue.vCore && !this.RUMQueue.memory)
		) {
			return true;
		}
		return false;
	};

	addAlertAction = function() {
		if (!this.isSNMPCorrect(this.alertAction)) {
			this.displaySnmpErrorMessage = true;
			return;
		}
		let copy = Object.assign({}, this.alertAction);

		this.alertActionConfiguration.alertActions.push(copy);
		this.toAddAlertActionData = false;
		this.alertAction = {};
		this.alertAction.snmpTraps = {};
		this.displaySnmpErrorMessage = false;
	};

	updateAlertAction = function() {
		if (!this.isSNMPCorrect(this.alertAction)) {
			this.displaySnmpErrorMessage = true;
			return;
		}
		let copy = Object.assign({}, this.checkSNMP(this.alertAction));
		this.alertActionConfiguration.alertActions[this.alertActionIndex] = copy;
		this.toEditAlertActionData = false;
		this.alertAction = {};
		this.alertAction.snmpTraps = {};
		this.displaySnmpErrorMessage = false;
	};

	showAlertActionData = function(index) {
		this.alertActionIndex = index;
		//	this.alertAction = this.alertActionConfiguration.alertActions[index];
		this.alertAction = Object.assign(
			{},
			this.alertActionConfiguration.alertActions[index]
		);
		console.log(this.alertAction);
		this.toAddAlertActionData = false;
		this.toEditAlertActionData = true;
	};

	/** delete alert action configuration */
	deleteAlertAction = function(index) {
		this.alertActionConfiguration.alertActions.splice(index, 1);
		this.alertAction = {};
		this.alertAction.snmpTraps = {};
		if (this.alertActionIndex == index) {
			this.toEditAlertActionData = false;
			this.toAddAlertActionData = false;
		}
	};

	/** Function disable Add and update button in escalation module */
	isDisabled = function() {
		if (!this.alertAction.alertLevel || !this.alertAction.occuringSinceHours) {
			return true;
		}

		if (this.alertAction.enableTicket == true) {
			if (
				!(
					this.ticketConfiguration.host &&
					this.ticketConfiguration.port &&
					this.ticketConfiguration.username &&
					this.ticketConfiguration.password &&
					this.ticketConfiguration.formName
				)
			) {
				return true;
			}
		}
		if (
			!(
				this.alertAction.emailTo ||
				(this.alertAction.snmpTraps &&
					this.alertAction.snmpTraps.trapOID &&
					this.alertAction.snmpTraps.ipAddress &&
					this.alertAction.snmpTraps.port)
			)
		) {
			return true;
		}
		return false;
	};

	checkSNMP(alertAction) {
		var snmpTraps = alertAction.snmpTraps;
		if (snmpTraps == null) {
			return alertAction;
		}
		if (
			this.isStringNullOrEmpty(snmpTraps.trapOID) ||
			this.isStringNullOrEmpty(snmpTraps.ipAddress) ||
			this.isStringNullOrEmpty(snmpTraps.port)
		) {
			alertAction.snmpTraps = null;
		}
		return alertAction;
	}

	/** Check if String null or empty */
	isStringNullOrEmpty(x) {
		if (x == null || x == undefined || x.length == 0) {
			return true;
		}
	}
	/** check SNMP traps null or empty in escalation module */
	isSNMPCorrect(alertAction) {
		var snmpTraps = alertAction.snmpTraps;
		if (snmpTraps.trapOID || snmpTraps.ipAddress || snmpTraps.port) {
			if (this.isStringNullOrEmpty(snmpTraps.trapOID)) {
				return false;
			}
			if (this.isStringNullOrEmpty(snmpTraps.ipAddress)) {
				return false;
			}
			if (this.isStringNullOrEmpty(snmpTraps.port)) {
				return false;
			}
		}
		return true;
	}

	addQueue = function() {
		this.showQueue = true;
		this.toEditQueueUtilizationData = false;
		this.QueueUtilization = false;
		this.toEditGenericQueueUtilizationData = false;
		this.ObjectQueue.selectedQueueName = "";
		this.showWarningCriticalErrorMessage = false;
		this.ObjectQueue = {
			selectedQueueName: "",
			warningLevel: 60,
			criticalLevel: 80
		};
	};

	addQueueUtilization() {
		if (
			this.alertConfiguration["individualQueueAlerts"] == null ||
			this.alertConfiguration["individualQueueAlerts"] == undefined
		) {
			this.alertConfiguration["individualQueueAlerts"] = {};
		}

		if (
			Number(this.ObjectQueue.warningLevel) >=
			Number(this.ObjectQueue.criticalLevel)
		) {
			this.showWarningCriticalErrorMessage = true;
			return;
		} else {
			this.showWarningCriticalErrorMessage = false;
		}
		var value = {
			queueName: this.ObjectQueue.selectedQueueName,
			warningLevel: this.ObjectQueue.warningLevel,
			criticalLevel: this.ObjectQueue.criticalLevel
		};
		this.alertConfiguration["individualQueueAlerts"][value.queueName] = {
			warningLevel: this.ObjectQueue.warningLevel,
			criticalLevel: this.ObjectQueue.criticalLevel
		};
		this.toEditQueueUtilizationData = false;
		this.toEditGenericQueueUtilizationData = false;
		//this.QueueUtilization = false;
		this.showQueue = false;
	}

	editGenericQueueUtilizationData = function(key) {
		this.GenericQueueUtilizationIndex = key;
		this.ObjectQueue.selectedQueueName = key;
		this.ObjectQueue.warningLevel = this.alertConfiguration.configurableAlerts[
			"QUEUE_UTILIZATION"
		].warningLevel;
		this.ObjectQueue.criticalLevel = this.alertConfiguration.configurableAlerts[
			"QUEUE_UTILIZATION"
		].criticalLevel;
		this.toEditQueueUtilizationData = true;
		this.editingIndividualQueue = false;
		this.toEditGenericQueueUtilizationData = true;
		this.QueueUtilization = false;
		this.showQueue = false;
		this.showWarningCriticalErrorMessage = false;
	};

	updateQueueUtilizationData = function() {
		if (
			Number(this.ObjectQueue.warningLevel) >=
			Number(this.ObjectQueue.criticalLevel)
		) {
			this.showWarningCriticalErrorMessage = true;
			return;
		} else {
			this.showWarningCriticalErrorMessage = false;
		}
		if (this.toEditGenericQueueUtilizationData) {
			this.alertConfiguration.configurableAlerts[
				"QUEUE_UTILIZATION"
			].warningLevel = this.ObjectQueue.warningLevel;
			this.alertConfiguration.configurableAlerts[
				"QUEUE_UTILIZATION"
			].criticalLevel = this.ObjectQueue.criticalLevel;
		} else {
			this.alertConfiguration.individualQueueAlerts[
				this.QueueUtilizationIndex
			].warningLevel = this.ObjectQueue.warningLevel;
			this.alertConfiguration.individualQueueAlerts[
				this.QueueUtilizationIndex
			].criticalLevel = this.ObjectQueue.criticalLevel;
		}
		this.toEditQueueUtilizationData = false;
		this.showQueue = false;
		this.toEditGenericQueueUtilizationData = false;
		this.QueueUtilization = false;
	};

	showWarningCriticalError = function() {
		if (
			Number(this.ObjectQueue.warningLevel) >=
			Number(this.ObjectQueue.criticalLevel)
		) {
			this.showWarningCriticalErrorMessage = true;
		} else {
			this.showWarningCriticalErrorMessage = false;
		}
	};

	deleteQueueUtilizationData = function(key) {
		delete this.alertConfiguration.individualQueueAlerts[key];
		this.showQueue = false;
	};

	CancelQueue = function() {
		this.QueueUtilizationIndex = "";
		this.showQueue = false;
		this.toEditQueueUtilizationData = false;
		this.QueueUtilization = false;
		this.toEditGenericQueueUtilizationData = false;
	};

	isNullOrEmpty = function(obj) {
		if (obj == null || obj == undefined || Object.keys(obj).length == 0) {
			return true;
		} else {
			return false;
		}
	};

	/** Function to save cluster configuration data */
	saveConfigData() {
		var adminConfiguration = {};
		adminConfiguration["clusterName"] = this.activeClusterName;
		this.dISK_SPACE_UTILIZATIONFalse = false;
		this.uNDER_REPLICATED_BLOCKSFalse = false;
		this.hDFS_UTILIZATIONFalse = false;
		//this.qUEUE_UTILIZATIONFalse = false;

		if (
			Number(
				this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
						.criticalLevel
				) &&
			Number(
				this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
						.criticalLevel
				) &&
			Number(
				this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
						.criticalLevel
				)
		) {
			//this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.dISK_SPACE_UTILIZATIONFalse = true;
			this.uNDER_REPLICATED_BLOCKSFalse = true;
			this.hDFS_UTILIZATIONFalse = true;
			return;
		}
		if (
			Number(
				this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
						.criticalLevel
				) &&
			Number(
				this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
						.criticalLevel
				)
		) {
			//this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.dISK_SPACE_UTILIZATIONFalse = true;
			this.uNDER_REPLICATED_BLOCKSFalse = true;
			return;
		}
		if (
			Number(
				this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
						.criticalLevel
				) &&
			Number(
				this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
						.criticalLevel
				)
		) {
			//	this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.dISK_SPACE_UTILIZATIONFalse = true;
			this.hDFS_UTILIZATIONFalse = true;
			return;
		}
		if (
			Number(
				this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
						.criticalLevel
				) &&
			Number(
				this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
					.warningLevel
			) >=
				Number(
					this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
						.criticalLevel
				)
		) {
			//	this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.uNDER_REPLICATED_BLOCKSFalse = true;
			this.hDFS_UTILIZATIONFalse = true;
			return;
		}
		// adminConfiguration['ticketConfiguration'] = { 'enable' : false }
		if (
			Number(
				this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
					.warningLevel
			) >=
			Number(
				this.alertConfiguration["configurableAlerts"].DISK_SPACE_UTILIZATION
					.criticalLevel
			)
		) {
			//this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.dISK_SPACE_UTILIZATIONFalse = true;
			return;
		}
		if (
			Number(
				this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
					.warningLevel
			) >=
			Number(
				this.alertConfiguration["configurableAlerts"].UNDER_REPLICATED_BLOCKS
					.criticalLevel
			)
		) {
			//this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.uNDER_REPLICATED_BLOCKSFalse = true;
			return;
		}
		if (
			Number(
				this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
					.warningLevel
			) >=
			Number(
				this.alertConfiguration["configurableAlerts"].HDFS_UTILIZATION
					.criticalLevel
			)
		) {
			//this.displayMsgBox('Failure', "Please check Cluster Configurations!");
			this.toastOptions.msg = "problem while saving the cluster!!!";
			this.toastyService.error(this.toastOptions);
			this.selectedIndex = 4;
			this.hDFS_UTILIZATIONFalse = true;
			return;
		}

		this.emailConfiguration = this.setEmailAuthReverse(this.emailConfiguration);

		adminConfiguration["emailConfiguration"] = this.emailConfiguration;
		adminConfiguration["haConfiguration"] = this.haConfiguration;
		adminConfiguration["influxDBConfiguration"] = this.influxDBConfiguration;
	//	adminConfiguration["hiveConfiguration"] = this.hiveConfiguration;
		//adminConfiguration["dlcConfiguration"] = this.dlcConfiguration;

		if (
			this.isStringNullOrEmpty(this.ticketConfiguration["host"]) ||
			this.isStringNullOrEmpty(this.ticketConfiguration["port"]) ||
			this.isStringNullOrEmpty(this.ticketConfiguration["username"]) ||
			this.isStringNullOrEmpty(this.ticketConfiguration["password"]) ||
			this.isStringNullOrEmpty(this.ticketConfiguration["formName"])
		) {
			adminConfiguration["ticketConfiguration"] = { enable: false };
		} else {
			adminConfiguration["ticketConfiguration"] = this.ticketConfiguration;
			adminConfiguration["ticketConfiguration"].enable = true;
		}

		var str = this.alertConfiguration["hdfsDirPaths"];
		if (Array.isArray(str)) {
			this.alertConfiguration["hdfsDirPaths"] = str;
		} else {
			if (str != undefined) {
				var res = str.split(",");
				this.alertConfiguration["hdfsDirPaths"] = res;
			} else {
				this.alertConfiguration["hdfsDirPaths"] = [];
			}
		}

		adminConfiguration["alertConfiguration"] = this.alertConfiguration;
		adminConfiguration[
			"alertActionConfiguration"
		] = this.alertActionConfiguration;
		//adminConfiguration["slaConfigurations"] = this.slaConfigurations;
		// adminConfiguration[
		// 	"chargeBackConfigurations"
		// ] = this.chargeBackConfigurations;
		adminConfiguration[
			"backgroundProcessConfiguration"
		] = this.backgroundProcessConfiguration;
		//adminConfiguration["cboConfiguration"] = this.cboConfiguration;
		// adminConfiguration[
		// 	"scriptMappingConfiguration"
		// ] = this.scriptMappingConfiguration;

		this.clusterService.saveConfigurationData(adminConfiguration).subscribe(
			(responseData: any) => {
				this.toastOptions.msg = "Cluster Configurations saved successfully!!!";
				this.toastyService.success(this.toastOptions);
				setTimeout(() => {
					this.router.navigate(["/clusters"]);
				}, 3000);
			},
			err => {
				console.log(err);
			}
		);
	}

	/** set email authentication */
	setEmailAuth(conf) {
		if (conf.authentication == true) {
			conf.authentication = "true";
		}
		if (conf.authentication == false) {
			conf.authentication = "false";
		}
		return conf;
	}
	/***/
	setEmailAuthReverse(conf) {
		if (conf.authentication == "true") {
			conf.authentication = true;
		}
		if (conf.authentication == "false") {
			conf.authentication = false;
		}
		return conf;
	}





	queueFilter(queueName) {
		if (this.alertConfiguration == null) {
			return;
		}
		if (
			this.alertConfiguration["individualQueueAlerts"] == undefined ||
			this.alertConfiguration["individualQueueAlerts"] == null
		) {
			this.alertConfiguration["individualQueueAlerts"] = {};
		}
		for (var queue in this.alertConfiguration["individualQueueAlerts"]) {
			if (queueName == queue) {
				return;
			}
		}
		return queueName;
	}

	editQueueUtilizationData = function(key) {
		this.QueueUtilizationIndex = key;
		this.ObjectQueue.warningLevel = this.alertConfiguration.individualQueueAlerts[
			key
		]["warningLevel"];
		this.ObjectQueue.selectedQueueName = key;
		this.ObjectQueue.criticalLevel = this.alertConfiguration.individualQueueAlerts[
			key
		]["criticalLevel"];
		this.toEditQueueUtilizationData = true;
		this.editingIndividualQueue = true;
		this.toEditGenericQueueUtilizationData = false;
		this.QueueUtilization = false;
		this.showQueue = false;
		this.showWarningCriticalErrorMessage = false;
	};

	goToClusterList() {
		this.router.navigate(["/clusters"]);
	}

	cancelAlertAction = function() {
		this.alertAction.snmpTraps = {};
		this.alertAction.occuringSinceHours = "";
		this.alertAction.emailTo = "";
		this.alertAction.alertLevel = "";
		this.toAddAlertActionData = true;
		this.toEditAlertActionData = false;
	};
	cancelSLAAction = function() {
		// this.slaAction.user = "";
		this.slaAction.applicationName = "";
		this.slaAction.queue = "";
		this.slaAction.maximumDuration = "";
		this.toAddSLAActionData = true;
		this.toEditSLAActionData = false;
	};
	cancelRumAction = function() {
		this.RUMQueue = {};
		this.toAddQueueConfData = true;
		this.toEditQueueConfData = false;
	};

	editCluster = function(clusterName) {
		this.mode = "edit";
		this.router.navigate(["/add-cluster"]);
	};



	checkisNumberLength(value: Number) {
		if (value < 5) {
			this.numberErrorMsg = true;
		} else {
			this.numberErrorMsg = false;
		}
	}

	// AddScriptUser() {
	// 	if (
	// 		this.users.kerberosPrincipal == "" ||
	// 		this.users.kerberosPrincipal == undefined
	// 	) {
	// 		return;
	// 	}
	// 	if (this.users.keyTabPath == "" || this.users.keyTabPath == undefined) {
	// 		return;
	// 	}
	// 	if (this.users.usernameOnHdfs == "" || this.users.usernameOnHdfs == undefined) {
	// 		return;
	// 	}
	// 	this.scriptMappingConfiguration["users"].push(this.users);
	// 	this.users = {
	// 		kerberosPrincipal: "",
	// 		keyTabPath: "",
	// 		usernameOnHdfs:""
	// 	};
	// }

	// deleteScriptUser(index) {
	// 	this.scriptMappingConfiguration.users.splice(index, 1);
	// }

	queueRUMFilter(queueName) {
		var mapReduce = false,
			spark = false;
		if (
			this.chargeBackConfigurations == null ||
			this.chargeBackConfigurations == undefined
		) {
			this.chargeBackConfigurations = { chargeBackConfList: [] };
		}
		for (
			var i = 0;
			i < this.chargeBackConfigurations["chargeBackConfList"].length;
			i++
		) {
			if (
				queueName ==
					this.chargeBackConfigurations["chargeBackConfList"][i].queueName &&
				this.chargeBackConfigurations["chargeBackConfList"][i]
					.executionEngine == "ALL"
			) {
				return;
			} else if (
				queueName ==
					this.chargeBackConfigurations["chargeBackConfList"][i].queueName &&
				this.chargeBackConfigurations["chargeBackConfList"][i]
					.executionEngine == "MAPREDUCE"
			) {
				mapReduce = true;
			} else if (
				queueName ==
					this.chargeBackConfigurations["chargeBackConfList"][i].queueName &&
				this.chargeBackConfigurations["chargeBackConfList"][i]
					.executionEngine == "SPARK"
			) {
				spark = true;
			} else {
			}
		}
		if (mapReduce && spark) {
			return;
		}

		return queueName;
	}

	getComputedChargebackQueueList() {
		var listToShow = [];
		if (!this.RUMQueuesList || this.RUMQueuesList.length == 0) {
			return listToShow;
		}
		if (!this.chargeBackConfigurations) {
			return this.RUMQueuesList;
		}
		var confList = this.chargeBackConfigurations["chargeBackConfList"];
		if (!confList || confList.length == 0) {
			return this.RUMQueuesList;
		}
		for (var queue of this.RUMQueuesList) {
			if (this.canShowQueueInChargeBackSelectList(queue)) {
				listToShow.push(queue);
			}
		}
		return listToShow;
	}



	getComputedQueueTypeList() {
	var	queueTypeList1 = [
			{ label: "All", value: "ALL" },
			{ label: "MapReduce", value: "MAPREDUCE" },
			{ label: "Spark", value: "SPARK" },
			{ label: "Tez", value: "TEZ" }
		];
		var confList = this.chargeBackConfigurations["chargeBackConfList"];
		if (!confList || confList.length == 0) {
			return queueTypeList1;
		}

		var addedEE = [];

		for (var conf of confList) {
			if (this.RUMQueue.queueName == conf.queueName) {
				addedEE.push(conf['executionEngine']);
			}
		}
		for (var ee of addedEE) {
			this.removeFromExecEngines(queueTypeList1, ee);
		}

		if (addedEE.length > 0) {
			this.removeFromExecEngines(queueTypeList1, 'ALL');
		}
		return queueTypeList1;
	}

	removeFromExecEngines(queueTypeList, execEngine) {
		var i = queueTypeList.length;
		while (i--) {
			if (queueTypeList[i].value == execEngine) {
				queueTypeList.splice(i, 1);
				return;
			}
		}
	}

	canShowQueueInChargeBackSelectList(queue) {
		var confList = this.chargeBackConfigurations["chargeBackConfList"];
		var eeAdded = [];
		for (var conf of confList) {
			if (conf['queueName'] == queue) {
				eeAdded.push(conf['executionEngine']);
			}
		}
		if (eeAdded.indexOf('ALL') != -1) {
			return false;
		}
		if (eeAdded.indexOf('MAPREDUCE') != -1 && eeAdded.indexOf('SPARK') != -1 && eeAdded.indexOf('TEZ') != -1 ) {
			return false;
		}
		return true;
	}

	checkQueueName(queueName) {
		if(queueName) {
			this.tempExecutionEngineList = this.getComputedQueueTypeList();
		}
	}

	userList= [];
	queuesList =[];
		/**Function for get filter options */
	// fetchApplicationsFilterOptions() {
	// 	this.filterSubscription = this.applicationService.getApplicationsFilterOptions(this.activeClusterName).subscribe(res => {
	// 		let filterOptions = res;
	// 		// filter list of user names
	// 		this.userList = filterOptions['users'];
	// 		this.userList.sort(function (a, b) {
	// 			return a.toLowerCase().localeCompare(b.toLowerCase());
	// 		});


	// 		this.exclusionUserNameList = this.exclusionUserName.valueChanges
	// 		.pipe(
	// 			startWith(''),
	// 			map(value => this._filterUserNames(value))
	// 		);



	// 		//filter queue names
	// 		this.queuesList = filterOptions['queues'];
	// 		this.queuesList.sort(function (a, b) {
	// 			return a.toLowerCase().localeCompare(b.toLowerCase());
	// 		});

	// 		this.exclusionQueueNameList =  this.exclusionQueueName.valueChanges
	// 		.pipe(
	// 			startWith(''),
	// 			map(value => this._filterQueueNames(value))
	// 		);
	// 	},
	// 		err => {
	// 			console.log(err);
	// 		})
	// }

	onExclusionSubmit() {
		if ((this.exclusionUserName.value != "" && this.exclusionUserName.value != null) || (this.exclusionQueueName.value != '' &&  this.exclusionQueueName.value != null) || (this.exclusionAppName.value != "" && this.exclusionAppName.value != null)) {
			this.alertActionConfiguration.exclusionConditions.push({
				"user":this.exclusionUserName.value,
				"queue":this.exclusionQueueName.value,
				"appName":this.exclusionAppName.value,
			})
		}



		if (this.alertActionConfiguration.exclusionConditions.length == 0) {
			this.showExclutionWarningMsg =  true;
		}
		else {
			this.showExclutionWarningMsg = false;
		}

		this.resetExclusionForm();
	}

	resetExclusionForm() {
		this.exclusionUserName.setValue('');
		this.exclusionQueueName.setValue('');
		this.exclusionAppName.setValue('');


		this.exclusionUserNameList = this.exclusionUserName.valueChanges
		.pipe(
			startWith(''),
			map(value => this._filterUserNames(value))
		);

		this.exclusionQueueNameList =  this.exclusionQueueName.valueChanges
		.pipe(
			startWith(''),
			map(value => this._filterQueueNames(value))
		);
	}

		/** delete sla action configuration */
		deleteExclusion(index) {
			this.alertActionConfiguration.exclusionConditions.splice(index, 1);
			this.resetExclusionForm();
			this.isEditExclusionData = false;
		};

		isEditExclusionData:boolean = false;
		exclusionIndexNumber:number;

		showExclusion(index) {
			this.exclusionUserName.setValue(this.alertActionConfiguration.exclusionConditions[index].user);
			this.exclusionQueueName.setValue(this.alertActionConfiguration.exclusionConditions[index].queue);
			this.exclusionAppName.setValue(this.alertActionConfiguration.exclusionConditions[index].appName);

			this.isEditExclusionData = true;
			this.exclusionIndexNumber = index;
		};

		updateExclusionData() {
			if (this.isEditExclusionData) {
				let copy =  {
					"user":this.exclusionUserName.value,
					"queue":this.exclusionQueueName.value,
					"appName":this.exclusionAppName.value,
				}
				this.alertActionConfiguration.exclusionConditions[this.exclusionIndexNumber] = Object.assign({},copy);
				this.resetExclusionForm();
				this.isEditExclusionData = false;
				if (this.alertActionConfiguration.exclusionConditions.length == 0) {
					this.showExclutionWarningMsg =  true;
				}
				else {
					this.showExclutionWarningMsg = false;
				}
			}
		}



	/**
	 *
	 * @param value queue name for filter
	 */
	private _filterQueueNames(value) {
		if (value != null) {
			const filterQueueValue = value.toLowerCase();
			return this.queuesList.filter(option => option.toLowerCase().includes(filterQueueValue));
		}

	}

	/**
	 *
	 * @param value user name for filter
	 */
	private _filterUserNames(value) {
		if (value != null) {
			const filterUserValue = value.toLowerCase();
			return this.userList.filter(option => option.toLowerCase().includes(filterUserValue));
		}

	}

	AddEmailForInefficiencies() {
		if (this.inefficienciesEmailAddress) {
			this.alertActionConfiguration.inefficiencyEmailTo.push(this.inefficienciesEmailAddress);
			this.inefficienciesEmailAddress = '';
		}

	}

	removeEmail(email: string): void {
		if (email) {
			const index = this.alertActionConfiguration.inefficiencyEmailTo.indexOf(email);

			if (index >= 0) {
				this.alertActionConfiguration.inefficiencyEmailTo.splice(index, 1);
			}
		}

	}
	
	onAppSlaChange(event) {
		this.selectedAppSla = event.value;
	};

	ngOnDestroy() {
		this.subscription.unsubscribe();
		this.queueSubscription.unsubscribe();
		this.queueListSubscription.unsubscribe();
		this.filterSubscription.unsubscribe();
	}

}




