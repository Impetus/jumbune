                                                                           
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


Running Jumbune Sample Jobs
--------------------------

    1) Using the browser navigate to 'localhost:8080' if Jumbune is deployed on the same machine or navigate to '<Jumbune machine IP>:8080', Jumbune page would appear.

    2) Upload one of the JSON found in the '<Jumbune home>/examples/resources/sample_json/' directory.
    
    3) The sample job jars are found in the '<Jumbune home>/examples/example-distribution/' directory.
    
    4) Select the appropriate JSON file from the sample JSON directory, following are the steps for doing so.
    
    Word Count:
    ----------
    
        1) Upload sample input file in HDFS by using the following command
          bin/hadoop fs -put <Jumbune Home>/examples/resources/data/PREPROCESSED/data1 /Jumbune/Demo/input/PREPROCESSED/data1
    
        2) Upload the sample wordcount JSON (<Jumbune home>/examples/resources/sample_json/WordCountSample.json).
        
        3) Edit the Name-node and Data-node information.
        
        4) In the 'M/R Jobs' tab select the WordCount sample jar, either by mentioning the path on the Jumbune machine or by uploading from the local machine.
        
        5) Validate and Run the job.
        
    Reg-ex Validator for profiling:
    -----------------------------
        
        1) Upload sample input file in HDFS by using the following command
          bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/u.data /Jumbune/example/regex
          
        2) In the 'M/R Jobs' tab select the Reg-ex Validator sample jar, either by mentioning the path on the Jumbune machine or by uploading from the local machine.
        
        3) Edit the Name-node and Data-node information.
        
        4) Validate and Run the job.
        
    Defaulter list validation for debugging:
    ---------------------------------------
        
        1) Upload sample input file in HDFS by using the following command
        bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/defaulterlistdata.txt /Jumbune/example/defaulter
          
        2) In the 'M/R Jobs' tab select the Defaulter List sample jar, either by mentioning the path on the Jumbune machine or by uploading from the local machine.
        
        3) Edit the Name-node and Data-node information.
        
        4) Validate and Run the job.
        
    US region portout:
    --------------------
        
        1) Upload sample input file in HDFS by using the following command
        bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/PREPROCESSED/data1 /Jumbune/Demo/input/PREPROCESSED/data1
        bin/hadoop fs -put <Jumbune_Home>/examples/resources/data/PREPROCESSED/data2 /Jumbune/Demo/input/PREPROCESSED/data2
          
        2) In the 'M/R Jobs' tab select the US region portout sample jar, either by mentioning the path on the Jumbune machine or by uploading from the local machine.
        
        3) Edit the Name-node and Data-node information.
        
        4) Validate and Run the job.
        
        
    NOTE: We have used GenericOptionsParser for the examples, so do not provide class name information, just select 'Job Class defined in the Jar Manifest' option instead.    
    




        
        
        
    
        
        
        
        
        
        
        
        
        
        
        
    
    
    
    
    
    
    
    

