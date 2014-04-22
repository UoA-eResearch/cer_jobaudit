<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.min.js"></script>
  <!--[if lte IE 8]><script type="text/javascript" src="<%=request.getContextPath()%>/js/excanvas.min.js"></script><![endif]-->
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.flot.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.flot.stack.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablesorter.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.blockUI.2.39.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/util.js"></script>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/style/tablesorter/blue/style.css" type="text/css"/>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css"/>
  
  <script type="text/javascript">
    // arrays for the data to be plotted 
    var monthly_serial_jobs_data = new Array();
    var monthly_serial_core_hours_data = new Array();
    var monthly_parallel_jobs_data = new Array();
    var monthly_parallel_core_hours_data = new Array();
    var monthly_avg_waiting_hours_data = new Array();
    var usermap = new Array();
    
    // set up structure to map account names to real names 
    <c:forEach items="${researchersForDropDown}" var="tmp">
	  usermap["${tmp.id}"] = "${tmp.name}";
	</c:forEach>
    <c:forEach items="${researcherList}" var="tmp">
	  usermap["${tmp.id}"] = "${tmp.name}";
	</c:forEach>

    // set up options in the drop down menues for the different categories 
    var options = { 'researcher': '', 'project': '', 'affiliation': '' };
    <c:forEach items="${researchersForDropDown}" var="tmp">
        if ("${tmp.id}" == "${formData.categoryChoice}") {
      	  options['researcher'] += "<option value='${tmp.id}' selected=\"selected\">${tmp.name}</option>";
        } else {
      	  options['researcher'] += "<option value='${tmp.id}'>${tmp.name}</option>";        	
        }
	</c:forEach>	
    <c:forEach items="${projectCodesForDropDown}" var="tmp">
      if ("${tmp}" == "${formData.categoryChoice}") {
    	options['project'] += "<option value='${tmp}' selected=\"selected\">${tmp}</option>";
      } else {
        options['project'] += "<option value='${tmp}'>${tmp}</option>";
      }
	</c:forEach>
    <c:forEach items="${affiliationsForDropDown}" var="tmp">
      if ("${tmp}" == "${formData.categoryChoice}") {
  	    options['affiliation'] += "<option value='${tmp}' selected=\"selected\">${tmp}</option>";    	  
      } else {
  	    options['affiliation'] += "<option value='${tmp}'>${tmp}</option>";
      }
	</c:forEach>
        
    $(document).ready(function() {
    	
    	// set the right choices depending on the selection of the category select 
        $('#categoryChoice').empty().append(options[$('#category').val().toLowerCase()]);
    	// when the category select changes value, adjust the values in the categoryChoice select 
    	$('#category').change(function() {
    	    $('#categoryChoice').empty().append(options[this.value.toLowerCase()]);
    	});
    		
        $("#statistics").tablesorter({widgets:['zebra'], sortList:[[6,1]], headers: {0: {sorter:false}}});
        var table = $("#statistics"); 
        table.bind("sortEnd",function() { 
            var i = 1;
            table.find("tr:gt(0)").each(function(){
                $(this).find("td:eq(0)").text(i);
                i++;
            });
        });
    });
  </script>
</head>

<body>
  <div id="body">
  
  <h3>Job Submission Statistics</h3>

   <!-- define the request -->
   <form:form id="form" method="post" modelAttribute="formData" commandName="formData">
    Get statistics for 
    <form:select id="category" path="category" items="${categoriesForDropDown}" />
    <form:select id="categoryChoice" path="categoryChoice" />
    from
    <form:select path="firstMonth" items="${monthsForDropDown}" />
    <form:select path="firstYear" items="${yearsForDropDown}" />
    to
    <form:select path="lastMonth" items="${monthsForDropDown}" />
    <form:select path="lastYear" items="${yearsForDropDown}" />
    <input type="submit" value="Submit">
  </form:form>
 
  <!-- display the statistics -->
  <c:choose>
    <c:when test="${not empty userStatistics}">
      <br>  
      <c:set var="totalNumberJobs" value="0" />
      <c:set var="totalCoreHours" value="0" />
      <c:set var="totalNumberGridJobs" value="0" />
      <c:set var="totalGridCoreHours" value="0" />
      <c:forEach items="${userStatistics}" var="statistics">
        <c:set var="totalNumberJobs" value="${totalNumberJobs + statistics.jobs}" />
        <c:set var="totalCoreHours" value="${totalCoreHours + statistics.total_core_hours}" />
        <c:set var="totalNumberGridJobs" value="${totalNumberGridJobs + statistics.grid_jobs}" />
        <c:set var="totalGridCoreHours" value="${totalGridCoreHours + statistics.total_grid_core_hours}" />
      </c:forEach>
  
    <c:set var="gridJobsPercentage" value="${(100*totalNumberGridJobs)/totalNumberJobs}" />
    <c:set var="gridTotalHoursPercentage" value="${(100*totalGridCoreHours)/totalCoreHours}" />

    <b>Total number of jobs</b>
    <table id="bordered">
      <tr>
  	    <td>&nbsp;</td>
  	    <td align="right">Total</td>
  	    <td align="right">Via Grid</td>
  	  </tr>
      <tr>
        <td>Number of jobs</td>
        <td align="right"><b>${totalNumberJobs}</b></td>
        <td align="right"><b>${totalNumberGridJobs}</b> (<script type="text/javascript">document.write(parseFloat("${gridJobsPercentage}").toFixed(2))</script> %)</td>
      </tr>
      <tr>
        <td>Core hours</td>
        <td align="right"><b><script type="text/javascript">document.write(parseFloat("${totalCoreHours}").toFixed(2))</script></b></td>
        <td align="right"><b><script type="text/javascript">document.write(parseFloat("${totalGridCoreHours}").toFixed(2))</script></b> (<script type="text/javascript">document.write(parseFloat("${gridTotalHoursPercentage}").toFixed(2))</script> %)</td>
      </tr>
    </table>

    <!-- areas where the diagrams are plotted -->
    <table border="0" cellpadding="10">
      <tr>
        <th>Number Jobs (that actually started)</th>
        <th>Core Hours</th>
        <th>Average Waiting Hours</th>
      </tr>
      <tr>
        <td><div id="monthly_jobs_plot" style="width:380px;height:350px;"></div></td>
        <td><div id="monthly_core_hours_plot" style="width:380px;height:350px;"></div></td>
        <td><div id="monthly_avg_waiting_hours_plot" style="width:380px;height:350px;"></div></td>
      </tr>
    </table>
    <br>
  
    <!-- prepare the data to be plotted -->
    <c:forEach items="${jobStatistics}" var="statistics">
      <c:if test="${not empty statistics.serial_jobs}">
        <script type="text/javascript">
          var arr = new Array("${statistics.serial_jobs}", "${statistics.bottom}"*1000);
          monthly_serial_jobs_data.push(arr);
        </script>      
      </c:if>
      <c:if test="${not empty statistics.parallel_jobs}">
        <script type="text/javascript">
          var arr = new Array("${statistics.parallel_jobs}", "${statistics.bottom}"*1000);
          monthly_parallel_jobs_data.push(arr);
        </script>      
      </c:if>
      <c:if test="${not empty statistics.serial_core_hours}">
        <script type="text/javascript">
          var arr = new Array("${statistics.serial_core_hours}", "${statistics.bottom}"*1000);
          monthly_serial_core_hours_data.push(arr);
        </script>
      </c:if>
      <c:if test="${not empty statistics.parallel_core_hours}">
        <script type="text/javascript">
          var arr = new Array("${statistics.parallel_core_hours}", "${statistics.bottom}"*1000);
          monthly_parallel_core_hours_data.push(arr);
        </script>
      </c:if>
      <c:if test="${not empty statistics.avg_waiting_hours}">
        <script type="text/javascript">
          var arr = new Array("${statistics.avg_waiting_hours}", "${statistics.bottom}"*1000);
          monthly_avg_waiting_hours_data.push(arr);
        </script>
      </c:if>
    </c:forEach>

    <!-- plot the bar diagrams -->
    <script type="text/javascript">
      draw_stacked_bar_diagram(monthly_jobs_plot, [ 
          { "label": "Serial Jobs", "data": monthly_serial_jobs_data, "color": "#06c" },
          { "label": "Parallel Jobs", "data": monthly_parallel_jobs_data, "color": "#d70" }
      ]);
      draw_stacked_bar_diagram(monthly_core_hours_plot, [
          { "label": "Serial Core Hours", "data": monthly_serial_core_hours_data, "color": "#060" },
          { "label": "Parallel Core Hours", "data": monthly_parallel_core_hours_data, "color": "#d70" }
      ]);
      draw_stacked_bar_diagram(monthly_avg_waiting_hours_plot, [
          { "label": "Avg. Waiting Hours", "data": monthly_avg_waiting_hours_data, "color": "#b00" }
      ]);
    </script>
    <br>
  
    <!-- User overview -->
    <b>Per user:</b>
    <table id="statistics" class="tablesorter"><thead>
      <tr>
        <th>#</th>
        <th>User Name</th>
        <th>User ID</th>
        <th>Jobs</th>
        <th>Jobs (Grid)</th>
        <th>Total Cores</th>
        <th class="{sorter: 'digit'}">Total Core Hours</th>
        <th>Total Core Hours (Grid)</th>
        <th>Total Waiting Time</th>
        <th>Average Waiting Time</th>
      </tr>
      </thead><tbody>
  
    <c:forEach items="${userStatistics}" var="statistics">
  	  <c:set var="avgWaitingTime" value="${statistics.total_waiting_time/statistics.jobs}" />
      <tr>
        <td>&nbsp;</td>
        <td><a href="<%=request.getContextPath()%>/html/auditrecords?accountName=${statistics.user}">
            <script type="text/javascript">document.write(usermap["${statistics.user}"]);</script></a></td> 
        <td><a href="<%=request.getContextPath()%>/html/auditrecords?accountName=${statistics.user}">${statistics.user}</a></td> 
        <td align="right">${statistics.jobs}</td> 
        <td align="right">${statistics.grid_jobs}</td> 
        <td align="right">${statistics.total_cores}</td> 
        <td align="right">${statistics.total_core_hours}</td>
        <td align="right">${statistics.total_grid_core_hours}</td> 
	    <td align="right">${statistics.total_waiting_time}</td>
        <td align="right"><fmt:formatNumber value="${avgWaitingTime}" type="Number" maxFractionDigits="2"/></td>
      </tr>
    </c:forEach>
    </tbody>
    </table>
  </c:when>
  <c:otherwise>
    <br><br>
    <b>No audit data available for current selection</b>
  </c:otherwise>
  </c:choose>
   
  </div>
</body>

</html>
