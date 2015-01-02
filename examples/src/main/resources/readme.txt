                                                                           
                                                                     JUMBUNE
                                                                     -------
                                                                    

This READ-ME is intended for Jumbune users to help them start-up the Jumbune Server, create and submit a Jumbune job. For detailed information regarding deployment please refer to the usage document provided with the Jumbune distribution.


Check-list before deployment
----------------------------

Make sure that:

    1)    JUMBUNE_HOME is set as an environment variable on the Jumbune machine.
    
    2)    AGENT_HOME is set as an environment variable on the Name-node of Apache Hadoop cluster.
    
    3)    Hadoop cluster is up.


Brief Deployment Guide
----------------------

    1)  Navigate to the path where the Jumbune Distribution jar is present, un-jar using the command java -jar <Jumbune-Distribution-Name> and input necessary details as it appears on the console.
    
    2)  Navigate to bin directory inside Jumbune home, change the permissions for the contents by using the command 'chmod 700 *' and start the Jumbune server by using './startWeb'.
    
    3)  Copy the agent jar from the 'agent-distribution' directory in Jumbune home, to the Name-node of the Hadoop cluster.
    
    4)  Start the Jumbune agent by using the command 'java -jar <Jumbune-Agent-Name> portNo'.
    
    5)  Please make sure that the port mentioned while starting the Jumbune agent is later entered in the job JSON. 

    
For detailed deployment guide, please refer to the 'Jumbune Usage Document'.


				Running a shipped example
				-------------------------

To execute Jumbune sample examples (shipped along with the distribution), perform the following steps:

	1. Navigate to the example folder you are wishing to run, go through the readme.txt

	2. Upload the JSON from <Jumbune home>/examples/resources/sample JSON/ directory from the Open option on the Jumbune home page.

	3. The sample job jars are found in the <Jumbune home>/examples/example-distribution/ directory.


Running the Word Count example:
-------------------------------

	For running the word count example, execute the following steps:

	1. Upload sample input file in HDFS using the following command:

	bin/hadoop fs -put <Jumbune Home>/examples/resources/data/PREPROCESSED/data1 </Jumbune/data/data1 or any HDFS path>

Note: Ensure that path <HDFS path> is not present on HDFS and user has appropriate permission to put data file on HDFS.
		
	2. Upload sample wordcount JSON (<Jumbune home>/examples/resources/sample JSON/WordCountSample.json).
	
	3. Edit Name-node and Data-node information.
	
	4. In 'M/R Jobs' tab select the WordCount sample jar (examples-wordcount-x.y.z.jar), either by mentioning the path on the Jumbune machine or by uploading from local machine.

	5. Validate and Run the job.

For Running Movie Rating example (for Profiling):
------------------------------------------------

	For movie rating, perform the following steps:
	
	1. Upload sample input file in HDFS by using the following command:
	
	bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/u.data </Jumbune/examples/regex or any HDFS path>
	
	Note: Ensure that path is not present on HDFS and user has appropriate permission to put data file on HDFS.
	
	2. Upload sample JSON (<Jumbune home>/examples/resources/sample JSON/MovieRatingSample.json).
	
	3. Edit the Name-node and Data-node information.
	
	4. In the 'M/R Jobs' tab select movie rating sample jar (examples-movierating-x.y.z.jar), either by mentioning the path on the Jumbune machine or by uploading from local machine.

	5. Validate and run the job.

	
For Running Bank Defaulters example (for Debugging):
----------------------------------------------------

	For bank defaulters, perform the following steps:

	1. Upload sample input file in HDFS by using the following command

	bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/defaulterlistdata.txt	</Jumbune/examples/defaulter or any HDFS path>

	Note: Ensure that path is not present on HDFS and user has appropriate permission to put data file
	on HDFS.

	2. Upload sample JSON (<Jumbune home>/examples/resources/sample	JSON/BankDefaultersSample.json).

	3. In 'M/R Jobs' tab select the bank defaulters sample jar (examples-bankdefaulters-x.y.z.jar), either by mentioning the path on the Jumbune machine or by uploading from local machine.

	4. Edit the Name-node and Data-node information.

	5. Validate and Run the job.


For Running US Region Port Out example (for Debugging):
------------------------------------------------------

	For US Region port out, perform the following steps:

	1. Upload sample input file in HDFS by using the following command:

	bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/PREPROCESSED/data1 /Jumbune/Demo/input/PREPROCESSED/data1

	bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/PREPROCESSED/data2 /Jumbune/Demo/input/PREPROCESSED/data2

	Note: Ensure that path is not present on HDFS and user has appropriate permission to put data file on HDFS.
	
	2. Upload sample JSON (<Jumbune home>/examples/resources/sample JSON/USRegionPortOutSample.json).

	3. In the 'M/R Jobs' tab select the US region portout sample jar (examples-usregionportouts-x.y.z.jar), either by mentioning the path on the Jumbune machine or by uploading from the local machine.

	4. Edit the Name-node and Data-node information.

	5. Validate and Run the job.


For Running Clickstream Analysis example (for Debugging):
---------------------------------------------------------

	1. Upload sample input file in HDFS by using the following command:

	bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/clickstream.tsv /Jumbune/clickstreamdata

	Note: Ensure that path is not present on HDFS and user has appropriate permission to put data file
	on HDFS.

	2. Upload sample JSON (<Jumbune home>/examples/resources/sample JSON/ClickstreamSample.json).

	3. In 'M/R Jobs' tab select the clickstream sample jar (examples-clickstreamanalysis-x.y.z.jar), either by mentioning the path on the Jumbune machine or by uploading from the local machine.

	4. Edit Name-node and Data-node information.

	5. Validate and Run the job.


For Running Sensor data example (for HDFS Validation):
-----------------------------------------------------

	For sensor data, perform the following steps:

	1. Upload sample input file in HDFS by using the following command
	
	bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/sensor_data/Jumbune/sensordata

	Note: Ensure that path is not present on HDFS and user has appropriate permission to put data file on HDFS.

	2. Upload sample JSON (<Jumbune home>/examples/resources/sample JSON/SensorDataSample.json).

	3. Edit Name-node and Data-node information.

	4. Validate and Run the job.

NOTE:
We have used GenericOptionsParser in our examples, so do not provide class name information, just select 'Job Class defined in the Jar Manifest' option on ‘M/R Jobs’ tab on Jumbune UI Wizard.
Ensure that output path provided in JSON file must not exist on HDFS previously
