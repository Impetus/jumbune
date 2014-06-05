var colorCodes = ["#0000ff", "#8a2be2", "#a52a2a", "#5f9ea0", "#7fff00", "#d2691e", "#ff7f50", "#6495ed", "#00ffff", "#00008b", "#008b8b", "#b8860b", "#006400", "#8b008b", "#556b2f", "#ff8c00", "#9932cc", "#8b0000", "#e9967a", "#8fbc8f", "#483d8b", "#2f4f4f", "#00ced1", "#9400d3", "#00bfff", "#1e90ff", "#228b22", "#ff00ff", "#ffd700", "#daa520", "#008000", "#adff2f", "#ff69b4", "#cd5c5c", "#4b0082", "#7cfc00", "#f08080", "#90ee90", "#ffb6c1", "#ffa07a", "#20b2aa", "#87cefa", "#778899", "#b0c4de", "#00ff00", "#32cd32", "#800000", "#66cdaa", "#0000cd", "#ba55d3", "#9370d8", "#3cb371", "#7b68ee", "#00fa9a", "#48d1cc", "#191970", "#808000", "#6b8e23", "#ffa500", "#da70d6", "#98fb98", "#afeeee", "#d87093", "#cd853f", "#ffc0cb", "#dda0dd", "#b0e0e6", "#800080", "#bc8f8f", "#4169e1", "#8b4513", "#fa8072", "#f4a460", "#2e8b57", "#a0522d", "#87ceeb", "#6a5acd", "#708090", "#00ff7f", "#4682b4", "#d2b48c", "#008080", "#d8bfd8", "#ff6347", "#40e0d0", "#ee82ee", "#ffff00", "#9acd32"];

	$(document).ready(function() {

		$("#helpLink").helpfw({ url:"skins/js/plugins/help-framework/helpjson.json", width:700, height:500, theme:"green" ,autolinking:true});
		//yaml form validation code Begin here
		$("#yamlUploadForm").validationEngine('attach', {promptPosition : "centerRight", scroll: false});
		//yaml form validation code End here

		//submit button click code Begin here....
		/*$("#submit").click(function(){
											
			if($("#fileName").val().length==0){
				$("#file-error").text('Please select a file to start execution.');
				return false;
			}
			var ext = $('#fileName').val().split('.').pop().toLowerCase();
			if($.inArray(ext, ['yaml']) == -1) {
				$("#file-error").text('Please select a YAML file');
				return false;
			}
			
		});*/
		//Submit button click code End here....



		//clear-logs button click code Begin here....		
		$("#clear-logs").click(function(){
			$.ajax({
				type: "POST",
		      	 url  	 : "ClearLogs",
		      	 data 	 : "",
		      	 cache 	 : false,
		      	 success : function(data){		      		
		      		if(data.success==true){
		      			alert('Logs Cleared Successfully ');
		      		}
		      	},
				error:function (xhr, ajaxOptions, thrownError){
					alert('There was a problem trying to clear logs');
               	 	alert('value of xhr:'+xhr);
               	 	alert('value of xhr.status:'+xhr.status);
               	 	alert('value of ajaxOptions:'+ajaxOptions);
               		alert('value of thrownError:'+thrownError);
               		
                	
           		 }  
				 
		      });
			
		});
		//clear-logs button click code End here....	



		//info hover button click code Begin here....	
		$('.info-image').hover(
			function() {				
				var tooltip = $(this).closest('.field').find('.info-message');
				var pos = $(this).position(); 
				tooltip.css({ 'top':(pos.top+27)+'px', 'left':(pos.left-90)+'px' });
				tooltip.show();
			}, 
			function() {
				$(this).closest('.field').find('.info-message').hide();
			}
		);
		//info hover button click code End here....			


		 $(".content").hide();

		 $(".heading").click(function()
		  {
		    $(this).next(".content").slideToggle(500);
		  });


		  var ctr=0;
        	$(".modal").click(function()        		
  				  {
  			  		var serializedData=$(this).find('form').serialize();			  		
  			  		var  tempName=serializedData.split('=');
  			  		var elementName=tempName[1];
  			  		
		  			if(ctr!=0){
		  			$("#taskCountersTable").jqGrid('clearGridData');
		  			$("#taskCountersTable").jqGrid('setCaption','Task Counter Statistics- '+elementName);
		  			}
  			  		

  			  		var taskCounters=allTaskCounterData[elementName];
  					  jQuery("#taskCountersTable").jqGrid({ 
							datastr: taskCounters,
						    datatype: "local",
						    colNames:['Counter Name','Filtered In','Context Writes','Unmatched Keys','Unmatched Values','Filtered Out'], 
						    colModel:[ 
										{name:'counterName',index:'counterName', width:400},
										{name:'filteredIn',index:'filteredIn', width:120, align:"right"}, 
										{name:'contextWrites',index:'contextWrites', width:120, align:"right"},
										{name:'unmatchedKeys',index:'unmatchedKeys', width:150, align:"right"},
										{name:'unmatchedValues',index:'unmatchedValues', width:150, align:"right"},
										{name:'filteredOut',index:'filteredOut', width:120, align:"right"},
							], 
							height: 'auto', 
							autowidth:true,
							rowNum:1000, 
							pager: '#taskCountersDiv', 
							viewrecords: true, 
							caption:'Task Counter Statistics- '+elementName
						}); 
						jQuery("#taskCountersTable").jqGrid('navGrid','#taskCountersDiv',{edit:false,add:false,del:false});	
						//jQuery("#taskCountersTable").setGridWidth(500);
						for(var b=0; b<taskCounters.length; b++){
		                    $("#taskCountersTable").jqGrid('addRowData', b+1, taskCounters[b]);
		                
		                }
						jQuery('#taskCountersTable').trigger("reloadGrid");
						//$('#dialog-confirm').html(elementName);
  					   $('#taskCountersTable').dialog('open');
  					    
  			  		
  			  		
  				 	/* traceListArray=traceList.split("[");
  				 	traceListArraytwo=traceListArray[1].split("]");
  				 	traceListArrayThree=traceListArraytwo[0].split(",");
  				 	*/	
  				 	ctr++;
  			});
  		  
  		   $("#taskCountersTable").dialog({
  	        	 resizable: true,
  	        	  autoOpen: false,
  	        	height:140,
  	        	width:1100,
  	        	modal: true,
  	        	buttons: {
  	        	'OK': function() {
  	        	 $(this).dialog('close');
  	        	}
  	        } }); 


			$( "#tabs" ).tabs();
		
	});
	


	
