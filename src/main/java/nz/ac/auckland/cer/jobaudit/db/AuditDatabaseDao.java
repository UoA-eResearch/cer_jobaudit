package nz.ac.auckland.cer.jobaudit.db;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

import nz.ac.auckland.cer.jobaudit.pojo.AuditRecord;
import nz.ac.auckland.cer.jobaudit.pojo.BarDiagramStatistics;
import nz.ac.auckland.cer.jobaudit.pojo.User;
import nz.ac.auckland.cer.jobaudit.pojo.UserStatistics;


public interface AuditDatabaseDao {

	public Future<User> getUser(String accountName) throws Exception;
	public Future<List<User>> getUsers() throws Exception;
	public Future<List<String>> getAccountNames(String top, String bottom) throws Exception;
	public Future<List<String>> getAccountNamesForProject(String project) throws Exception;	
	public Future<List<String>> getAccountNamesForAffiliation(String code) throws Exception;
	public Future<List<String>> getAccountNamesForAffiliation(String code, String dept1) throws Exception;
	public Future<List<String>> getAccountNamesForAffiliation(String code, String dept1, String dept2) throws Exception;
	public Future<List<String>> getAffiliations() throws Exception;
	public Future<List<AuditRecord>> getRecords(String accountName, String orderby, String sortorder, long offset, long amount) throws Exception;
	public Future<Integer> getNumberRecords(String accountName) throws Exception;
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForProjectCurr(String project, String bottom, String mid, String top) throws Exception;
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForAllAccountNames(String bottom, String top) throws Exception;
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForAccountNameSet(List<String> accountNames, String bottom, String top) throws Exception;
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForAccountNameSetCurr(List<String> accountNames, String bottom, String mid, String top) throws Exception;
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForProject(String projects, String bottom, String top) throws Exception;
	public List<Future<BarDiagramStatistics>> getProjectStats(String project, Integer startYear, Integer startMonth,Integer endYear, Integer endMonth) throws Exception;
	public List<Future<BarDiagramStatistics>> getBarDiagramAccountNamesStatistics(List<String> accountNames, Integer startYear, Integer startMonth,Integer endYear, Integer endMonth) throws Exception;
	public List<User> getUsersForAccountNames(List<String> accountNames) throws Exception;
	public List<UserStatistics> getStatisticsForAccountNames(List<String> accountNames, Calendar from, Calendar to) throws Exception;	
	public List<UserStatistics> getStatisticsForProject(String projects, Calendar from, Calendar to) throws Exception;
	public boolean isCurrentUserAdmin(String tuakiriUniqueId) throws Exception;	

}
