export class Cluster {

  constructor(
		public clusterName: any,
		public realm:any,
    public enableHadoopUser: any,
		public hostMNArr:any,
		public zkHostPortArr:{
			 zkHost :any,
			 zkport:any
		},
		public taskHostArr:any,
		public enableHA:any,
		public enableRMHA:any,
		public jmxPluginEnabled:any,
		public enableAgentHA:any,
		public enableDataProfiling:any,
		public agentInfoPaswd :any,
		public taskManagerPaswd:any,
		public agentUserMN:any,
		public nodeHostArrCopy:any,
		public fileAgentTypeMN:any,
		public selectedDN:any,
		public hostRangeFrom :any,
		public hostRangeTo :any
  ) {  }

}
