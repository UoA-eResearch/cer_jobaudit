<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page session="false"%>

<html>
  <head>
    <script type="text/javascript" src="../js/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="../js/jquery.tablesorter.min.js"></script>
    <script type="text/javascript" src="../js/jquery.paging.min.js"></script>
    <script type="text/javascript" src="../js/TableRenderer.js"></script>
    <script type="text/javascript" src="../js/Paginator.js"></script>
    <link rel="stylesheet" href="../style/common.css" type="text/css"/>
    <link rel="stylesheet" href="../style/recordtable.css" type="text/css"/>
    <link rel="stylesheet" href="../style/pagination.css" type="text/css"/>

    <script type="text/javascript">
      var offset = 0;
      var maxJobRecordsPerPage = '${maxJobRecordsPerPage}';
      var totalNumberRecords = '${totalNumberRecords}';
      var contextPath = '';
      var pageCount = Math.ceil(totalNumberRecords / maxJobRecordsPerPage);
      var columnNames = new Array("jobid", "queue", "jobgroup", "jobtype", "account", "appstatus", "status",
        "cores", "nodes", "memrequested", "walltime", "mem", "vmem", "qtime", "start", "done", "coretime",
        "efficacy", "shared", "workingdir", "executable", "jobname");

      $(document).ready(function() {      
        var tableR = new TableRenderer("#records", columnNames);

        // Load records from via REST call to database service 
        function loadRecords() {
          $.ajax({
            url: '../html/auditrecords/rest/' + $('#accountName').val() + '/' + $('#orderBy').val() + '/' + $('#sortOrder').val() + '/' + offset,
            cache: false,
            success: renderTable
          });
        }
    
        // Render the loaded data in a table 
        function renderTable(data, textStatus, xhr) {
          jQuery.parseJSON(data);
          tableR.render(data, $('#orderBy').val());
        }

        // Called when the page changes 
        function onPageChange(newPage) {
          offset = (newPage-1) * maxJobRecordsPerPage;
          loadRecords();
        }

        // Records are loaded in the paginator 
        new Paginator(".pagination", pageCount, onPageChange).render();      
      });
    </script>
  </head>

  <body>
    <div id="body">
      <div id="summary">
        <form:form id="form" method="post" modelAttribute="auditRecordRequest">
          <b>Cluster job audit records of</b> 
          <form:select id="accountName" path="user" items="${researchersInDropDown}" />
          sorted by 
          <form:select id="orderBy" path="orderBy" items="${orderBys}" />
          <form:select id="sortOrder" path="sortOrder" items="${sortOrders}"/>
          <input type="submit" value="Submit">
        </form:form>  

        <table id="bordered">
          <tr>
            <td>Total number of job records</td>
            <td><b>${totalNumberRecords}</b></td>
          </tr>
          <tr>
            <td>Max job records per page</td>
            <td><b>${maxJobRecordsPerPage}</b></td>
          </tr>
        </table>
      </div>
      
      <div class="pagination"></div>
      <div id="records"></div>
      <div class="pagination"></div>
      
    </div>
  </body>

</html>
