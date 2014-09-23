/*
 * Render a table
 */
function TableRenderer(divId, columnNames) {

    /* Id of the div where the table is rendered */
    this.divId = divId;
    /* Column names */
    this.columnNames = columnNames;
   
    TableRenderer.prototype.pad2 = function(number) {
        return (number < 10 ? '0' : '') + number;
    };

    /* Render the table */
    TableRenderer.prototype.render = function(rowList, highlightCol) {
        var tmp='<table class="recordtable"><tr>';
    	for (var i=0; i<columnNames.length; i++) {
    		if (columnNames[i] == highlightCol) {
    		    tmp += '<th class="highlight">';
    		} else {
    		    tmp += '<th>';
    		}
    		tmp += columnNames[i] + '</th>';
    	}
    	tmp += '</tr>';
    	for (var i=0; i<rowList.length; i++) {
    		if (i%2 == 0) {
    			tmp += '<tr class="odd">';
    		} else {
    			tmp += '<tr>';    			
    		}
    		for (var j=0; j<this.columnNames.length; j++) {
    		    if (columnNames[j] == "coretime" || columnNames[j] == "walltime") {
                    // core hours
    		    	var seconds = rowList[i][columnNames[j]];
                    var hours = this.pad2(Math.floor(seconds/3600));
                    seconds %= 3600;
                    var minutes = this.pad2(Math.floor(seconds/60));
                    seconds = this.pad2(seconds % 60);
    		        tmp += '<td>' + hours + ":" + minutes + ":" + seconds + '</td>';
    		    } else if (columnNames[j] == "efficacy") {
    		        var efficacy = rowList[i][columnNames[j]];
    		        if (efficacy == null || efficacy == "null") {
    		    	    efficacy = 0;
    		        }
    		        tmp += '<td>' + efficacy + '</td>';
    		    } else if (columnNames[j] == "nodes") {
    		    	var nodes = rowList[i][columnNames[j]];
    		        var machine_string = rowList[i]["processors"];
    		        var machines = machine_string.split(",c").sort();
    		        var MAX_MACHINES = 8;
    		        var tooltip = '';
    		        for (var k=0; k<machines.length; k++) {
                            if (machines[k].charAt(0) != 'c') {
                                machines[k] = 'c' + machines[k];
                            }    
    		            tooltip += machines[k] + '\n';
    		            if (k >= MAX_MACHINES-1) {
    		                break;
    		            }
    		        }
    		        if (machines.length > MAX_MACHINES) {
    		        	tooltip += "...\n";
    		        }
    		        tmp += '<td><div title="' + tooltip + '">' + nodes + '</div></td>';    		    
    		    } else {
    			    tmp += '<td>' + rowList[i][columnNames[j]] + '</td>';
    		    }
    		}
			tmp += '</tr>';
    	}
    	tmp += '</table>';
        $(this.divId).html(tmp);
    };

    
}

