<!DOCTYPE html>
<html>
<head>
<script>

graphJson = [
  
    { "key":"clean Tuples",
     "values":""
    },
    { "key":"null Checks",
     "values":""
    },
    { "key":"DataType Checks",
     "values":""
    },  
    { "key":"Regex Check",
     "values":""
    },
    { "key":"Number of Fields",
     "values":""
    }
    
  ];
  
  graphJson2 = [
      
  ];

  noOfDates=[];
  
</script>


    <style>
        text {
            font: 12px sans-serif;
        }
        svg {
            display: block;
        }
        html, body, svg {
            margin: 0px;
            padding: 0px;
            height: 100%;
            width: 100%;
        }
    </style>
</head>
<body class='with-3d-shadow with-transitions'>

<svg id="chart1"></svg>

<script>
  

//function to convert json produced by Jumbune to the json format required by graph. 
 function convertJumbuneJsonToGraphJson(jsonVal) {

            var dirtyValues = [];
            var nullValues = [];
            var dataTypeValues = [];
            var regexValues=[];
            var fieldValues=[];
            var obj = JSON.parse(jsonVal);
            var dates = Object.keys(obj)
             noOfDates=dates;
            for (var i = 0; i < dates.length; i++) {
                var nullTemp = [];
                var dataTypeTemp = [];
                var pureTemp = [];
                var regexTemp=[];
                var fieldTemp=[];

                totalTuples = obj[dates[i]]["totalTupleProcessed"];
                
                var jsonReport = JSON.parse(obj[dates[i]].jsonReport);
                var keysPresent = Object.keys(jsonReport);
                var date=parseInt(dates[i]); 
                
                
                //calculation of pure values
                pureTemp[0] = date;
                pureTuples = obj[dates[i]]["cleanTuple"];
                if (pureTuples == null) {
                    pureTuples = 0;
                }
                pureTemp[1] = pureTuples / 1;
                dirtyValues[i] = pureTemp;

                //calculation of null violations
                nullTemp[0] = date;
                var nullInfected = 0;
                if (keysPresent.indexOf("Null Check") >= 0) {
                    nullInfected = jsonReport["Null Check"]["dirtyTuple"];
                }
                nullTemp[1] = nullInfected; //((nullInfected/totalTuples)*100)//
                nullValues[i] = nullTemp;

                //calculation of datatype violations 
                dataTypeTemp[0] = date;
                var dataTypeInfected = 0;
                if (keysPresent.indexOf("Data Type") >= 0) {
                    dataTypeInfected = jsonReport["Data Type"]["dirtyTuple"];
                }
                dataTypeTemp[1] = dataTypeInfected; //((dataTypeInfected/totalTuples)*100)//
                dataTypeValues[i] = dataTypeTemp;
                
                //calculation of Regex violations 
                regexTemp[0] = date;
                var regexInfected = 0;
                if (keysPresent.indexOf("Regex") >= 0) {
                    regexInfected = jsonReport["Regex"]["dirtyTuple"];
                }
                regexTemp[1] = regexInfected; //((dataTypeInfected/totalTuples)*100)//
                regexValues[i] = regexTemp;

                //calculation of Number of fields violations 
                fieldTemp[0] = date;
                var fieldInfected = 0;
                if (keysPresent.indexOf("Number of Fields") >= 0) {
                    fieldInfected = jsonReport["Number of Fields"]["dirtyTuple"];
                }
                fieldTemp[1] = fieldInfected; //((dataTypeInfected/totalTuples)*100)//
                fieldValues[i] = fieldTemp;


         
         
         
            } // End of for

            var graphData = JSON.stringify(graphJson)
            graphData = JSON.parse(graphData)
            graphData[0]["values"] = dirtyValues;
            graphData[1]["values"] = nullValues;
            graphData[2]["values"] = dataTypeValues;
            graphData[3]["values"] = regexValues;
            graphData[4]["values"] = fieldValues;   
          return graphData;
    
        }

    var colors = d3.scale.category20();
    var keyColor = function(d, i) {return colors(d.key)};

    function makeGraph(graphJsonData) {     
       var chart = nv.models.stackedAreaChart()
            .useInteractiveGuideline(true)
            .x(function(d) { return d[0] })
            .y(function(d) { return d[1] })
            .controlLabels({stacked: "Stacked"})
            .color(keyColor)
            .duration(300);
     chart.xAxis.tickFormat(function(d) { return d3.time.format('%H:%M-%d/%m')(new Date(d)) });
        chart.yAxis.tickFormat(d3.format(',.2f'));



        d3.select('#chart1')
            .datum(graphJsonData)
            .transition().duration(1000)
            .call(chart)
            .each('start', function() {
                setTimeout(function() {
                    d3.selectAll('#chart1 *').each(function() {
                        if(this.__transition__)
                            this.__transition__.duration = 1;
                    })
                }, 0)
            });

        nv.utils.windowResize(chart.update);
        return chart;
    }

function makeGraphSchemaBasedOnKeysPresent(initialJson,finalJson)
{
    var obj = JSON.stringify(initialJson)
            obj = JSON.parse(obj);
            var dates = Object.keys(obj)

     var arrIndex=parseInt(0);
       graphJson2[arrIndex++]=finalJson[0];

var nullFlag=false;
var dataTypeFlag=false;
var regexFlag=false;
var noOfFieldsFlag=false;

for(i=0;i<dates.length; i++)

{
    var jsonReport = JSON.parse(obj[dates[i]].jsonReport);
    var keysPresent = Object.keys(jsonReport);
       
        if (keysPresent.indexOf("Null Check") >= 0 && nullFlag == false) {
                 graphJson2[arrIndex++]=finalJson[1];
                 nullFlag=true;
                }
        if (keysPresent.indexOf("Data Type") >= 0 && dataTypeFlag == false) {
                          graphJson2[arrIndex++]=finalJson[2]
                          dataTypeFlag=true;
                }
        if (keysPresent.indexOf("Regex") >= 0 && regexFlag == false) {
          graphJson2[arrIndex++]=finalJson[3];
          regexFlag=true;
                }
        if (keysPresent.indexOf("Number of Fields") >= 0 && noOfFieldsFlag == false) {
                graphJson2[arrIndex++]=finalJson[4]
                noOfFieldsFlag=true;
                }

}
return graphJson2;	
}


 function makeAndShowGraph(json)
{ 
  graphJson=convertJumbuneJsonToGraphJson(json);
  graphJson2=makeGraphSchemaBasedOnKeysPresent(JSON.parse(json),graphJson);
  var chartData=makeGraph(graphJson2);
  nv.addGraph(chartData);
}



</script>
</body>
</html>