<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page session="false"%>

<html>
  <head>
    <script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
    <!--[if lte IE 8]><script type="text/javascript" src="../js/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="../js/jquery.flot.min.js"></script>
    <script type="text/javascript" src="../js/jquery.flot.stack.min.js"></script>
    <script type="text/javascript" src="../js/jquery.tablesorter.min.js"></script>
    <script type="text/javascript" src="../js/util.js"></script>
    <link rel="stylesheet" href="../style/tablesorter/theme.default.css" type="text/css"/>
    <link rel="stylesheet" href="../style/common.css" type="text/css"/>
  
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
      <c:choose>
        <c:when test="${empty researchersForDropDown}">
          options['researcher'] += "<option value='__dummy__'>${cn}</option>";          
        </c:when>
        <c:otherwise>
          <c:forEach items="${researchersForDropDown}" var="tmp">
            if ("${tmp.id}" == "${formData.categoryChoice}") {
              options['researcher'] += "<option value='${tmp.id}' selected=\"selected\">${tmp.name}</option>";
            } else {
              options['researcher'] += "<option value='${tmp.id}'>${tmp.name}</option>";
            }
          </c:forEach>  
        </c:otherwise>
      </c:choose>
      
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

        $("#statistics").tablesorter({sortList:[[3,1]]});
      });
    </script>
  </head>

  <body>
    <div id="body">

      <!-- define the request -->
      <form:form id="form" method="get" modelAttribute="formData" commandName="formData">
        <b>Get statistics for</b>
        <form:select id="category" path="category" items="${categoriesForDropDown}" />
        <form:select id="categoryChoice" path="categoryChoice" />
        <b>from</b>
        <form:select path="firstMonth" items="${monthsForDropDown}" />
        <form:select path="firstYear" items="${yearsForDropDown}" />
        <b>to</b>
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
          <c:forEach items="${userStatistics}" var="statistics">
            <c:set var="totalNumberJobs" value="${totalNumberJobs + statistics.jobs}" />
            <c:set var="totalCoreHours" value="${totalCoreHours + statistics.total_core_hours}" />
          </c:forEach>
  
          <table>
            <tr>
              <td>Total number of jobs: <b>
                <script type="text/javascript">
                document.write(parseInt("${totalNumberJobs}").toLocaleString());
                </script></b></td>
            </tr>
            <tr>
              <td>Total number of core hours: <b>
                <script type="text/javascript">
                    document.write(parseFloat("${totalCoreHours}").toLocaleString());
                </script></b>
              </td>
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

          <!-- plot bar diagrams -->
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
  
          <!-- user overview -->
          <b>By user:</b>
          <table id="statistics" class="tablesorter">
            <thead>
              <tr>
                <th>User Name</th>
                <!--<th>User ID</th>-->
                <th>Jobs</th>
                <th>Total Cores</th>
                <th class="{sorter: 'digit'}">Total Core Hours</th>
                <th>Total Waiting Time</th>
                <th>Average Waiting Time</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${userStatistics}" var="statistics">
                <c:set var="avgWaitingTime" value="${statistics.total_waiting_time/statistics.jobs}" />
                <tr>
                  <td>
                    <div id="name-${statistics.user}">
                      <script type="text/javascript">
                        // Special chars like '.' have to be escaped with with two backslashes in jquery. 
                        // All users that don't have upis have usernames like <firstname>.<lastname> 
                        var selector = '#name-' + '${statistics.user}'.replace(/\./g,'\\.');
                        if (usermap['${statistics.user}'] === undefined) {
                            $(selector).html("" + '${statistics.user}');
                        } else {
                            $(selector).html("" + usermap['${statistics.user}']);
                        }
                      </script>
                    </div>
                  </td> 
                  <!--<td>${statistics.user}</td>-->
                  <td align="right">${statistics.jobs}</td>
                  <td align="right">${statistics.total_cores}</td> 
                  <td align="right">${statistics.total_core_hours}</td>
                  <td align="right">${statistics.total_waiting_time}</td>
                  <td align="right"><fmt:formatNumber value="${avgWaitingTime}" type="Number" maxFractionDigits="2"/></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:when>
 
        <c:otherwise>
          <br>
          <div class="infoblock">No job history data available for current selection</div>
        </c:otherwise>
 
      </c:choose>
      
    </div>
  </body>

</html>
