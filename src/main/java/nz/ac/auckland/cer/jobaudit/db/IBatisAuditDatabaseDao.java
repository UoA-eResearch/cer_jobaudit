package nz.ac.auckland.cer.jobaudit.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import nz.ac.auckland.cer.jobaudit.pojo.Affiliation;
import nz.ac.auckland.cer.jobaudit.pojo.AuditRecord;
import nz.ac.auckland.cer.jobaudit.pojo.BarDiagramStatistics;
import nz.ac.auckland.cer.jobaudit.pojo.User;
import nz.ac.auckland.cer.jobaudit.pojo.UserStatistics;
import nz.ac.auckland.cer.jobaudit.util.UserComparator;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;


public class IBatisAuditDatabaseDao extends SqlSessionDaoSupport implements AuditDatabaseDao {

	private ExecutorService executorService;
	Logger log = Logger.getLogger(Thread.currentThread().getClass());

	public Future<User> getUser(final String accountName) throws Exception {
		return this.executorService.submit(
			new Callable<User>() {
				public User call() throws Exception {
					return (User) getSqlSession().selectOne("getUser", accountName);
				}
			}
		);
	}

	public Future<List<User>> getUsers() throws Exception {
		return this.executorService.submit(
			new Callable<List<User>>() {
				public List<User> call() throws Exception {
					List<String> accountNames = getSqlSession().selectList("getUsersWithAtLeastOneJob");
					List<User> users = new LinkedList<User>();
					if (accountNames != null) {
						for (String accountName: accountNames) {
							User u = (User) getSqlSession().selectOne("getUser", accountName);
							if (u != null) {
								users.add(u);
							}
						}
					}
					Collections.sort(users, new UserComparator());
					return users;
				}
			}
		);
	}

	public List<User> getUsersForAccountNames(final List<String> accountNames) throws Exception {
		List<User> users = new LinkedList<User>();
		if (accountNames != null) {
			for (String accountName: accountNames) {
				User u = (User) getSqlSession().selectOne("getUser", accountName);
				if (u != null) {
					users.add(u);
				}
			}
		}
		Collections.sort(users, new UserComparator());
		return users;
	}

	public Future<List<String>> getAccountNames(final String bottom, final String top) throws Exception {
		return this.executorService.submit(
			new Callable<List<String>>() {
				public List<String> call() throws Exception {
					Map<String,Object> params=new HashMap<String, Object>();
					params.put("top", top);
					params.put("bottom", bottom);
					return getSqlSession().selectList("getAccountNamesWithAtLeastOneJobInterval", params);
				}
			}
		);
	}

	public Future<List<String>> getAccountNamesForProject(final String project) throws Exception {
		return this.executorService.submit(
			new Callable<List<String>>() {
				public List<String> call() throws Exception {
					return getSqlSession().selectList("getAccountNamesForProject", project);
				}
			}
		);
	}

	public Future<List<String>> getAccountNamesForAffiliation(final String code) throws Exception {
		return this.executorService.submit(
			new Callable<List<String>>() {
				public List<String> call() throws Exception {
					return getSqlSession().selectList("getAccountNamesForAffiliationCode", code);
				}
			}
		);
	}

	public Future<List<String>> getAccountNamesForAffiliation(final String code,
		final String dept1) throws Exception {
		return this.executorService.submit(
			new Callable<List<String>>() {
				public List<String> call() throws Exception {
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("code", code);
					params.put("dept1", dept1);
					return getSqlSession().selectList("getAccountNamesForAffiliationCodeAndDept1", params);
				}
			}
		);
	}

	public Future<List<String>> getAccountNamesForAffiliation(final String code, final String dept1,
		final String dept2) throws Exception {
		return this.executorService.submit(
			new Callable<List<String>>() {
				public List<String> call() throws Exception {
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("code", code);
					params.put("dept1", dept1);
					params.put("dept2", dept2);
					return getSqlSession().selectList("getAccountNamesForAffiliationCodeAndDept1AndDept2",
						params);
				}
			}
		);
	}

	public Future<List<String>> getAffiliations() throws Exception {
		return this.executorService.submit(
			new Callable<List<String>>() {
				public List<String> call() throws Exception {
					List<Affiliation> tmp = getSqlSession().selectList("getAffiliations");
					Set <String> affiliations = new HashSet<String>();
					for (Affiliation a: tmp) {
						StringBuffer affil = new StringBuffer("");
						String sep = "/";
						String code = (a.getCode() == null || a.getCode().trim().length() == 0) ? " " : a.getCode();
						String dept1 = (a.getDept1() == null || a.getDept1().trim().length() == 0) ? " " : a.getDept1();
						String dept2 = (a.getDept2() == null || a.getDept2().trim().length() == 0) ? " " : a.getDept2();
						affil.append(sep).append(code).append(sep).append(dept1).append(sep).append(dept2);
						affiliations.add(affil.toString().replaceAll("/ $", "").replaceAll("/ $", ""));
						affiliations.add(new StringBuffer("/").append(code).toString());
						if (dept1 != " ") {
							affiliations.add(new StringBuffer("/").append(code).append(sep).append(dept1).toString());
						}			
					}
					List<String> list = new ArrayList<String>(affiliations);
					Collections.sort(list);
					return list;
				}
			}
		);
	}
	
	public boolean isCurrentUserAdmin(String tuakiriUniqueId) throws Exception {
        boolean returnVal = false;
		Integer count = (Integer) getSqlSession().selectOne("isCurrentUserAdmin", tuakiriUniqueId);
		if (count > 0) {
			returnVal = true;
		}
		return returnVal;
	}	

	
	public Future<Integer> getNumberRecords(final String accountName) throws Exception {
		return this.executorService.submit(
			new Callable<Integer>() {
				public Integer call() throws Exception {
					return (Integer) getSqlSession().selectOne("getNumberAuditRecordsOfAccountName", accountName);
				}
			}
		);
	}

	public Future<List<AuditRecord>> getRecords(final String accountName, final String orderby,
		final String sortorder, final long offset, final long amount) throws Exception {
		return this.executorService.submit(
			new Callable<List<AuditRecord>>() {
				public List<AuditRecord> call() throws Exception {
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("accountName", accountName);
					params.put("orderby", orderby);
					params.put("sortorder", sortorder);
					params.put("offset", offset);
					params.put("amount", amount);
					return getSqlSession().selectList("getAuditRecordsOfUser", params);
				}
			}
		);
	}
	
	public List<UserStatistics> getStatisticsForAccountNames(final List<String> accountNames,
		final Calendar from, final Calendar to) throws Exception {		
		String high = ""+(to.getTimeInMillis()/1000);
		String mid = null;
		
		Calendar now = Calendar.getInstance();
		int currMonth = now.get(Calendar.MONTH);
		int currYear = now.get(Calendar.YEAR);
		String mmFrom=null;
		String mmTo=null;
		int tempFrom;
		int tempTo;
		
		// the selected date range overlaps with past 24 hours' time span
		if ((to.getTimeInMillis())>(System.currentTimeMillis()-86400000)) {
			mid=""+((System.currentTimeMillis()-86400000)/1000);
			Calendar newTo = Calendar.getInstance();
			newTo.set(currYear, currMonth-1, 1, 0, 0, 0);
			Calendar curr=Calendar.getInstance();
			curr.set(currYear, currMonth, 1, 0, 0, 0);
			mmFrom=""+(from.get(Calendar.MONTH)+1);
			mmTo=""+(newTo.get(Calendar.MONTH)+1);
			return getStatisticsForAccountNames(accountNames, ""+(curr.getTimeInMillis()/1000), mid,
				high,""+from.get(Calendar.YEAR)+(mmFrom.length()==1?"0"+mmFrom:mmFrom),
				""+newTo.get(Calendar.YEAR)+(mmTo.length()==1?"0"+mmTo:mmTo));
		} else {
			tempFrom=(from.get(Calendar.MONTH)+1);
			tempTo=(to.get(Calendar.MONTH));
			mmFrom = tempFrom+"";
			mmTo = tempTo+"";
			return getStatisticsForAccountNames(accountNames,
				""+from.get(Calendar.YEAR)+(mmFrom.length()==1?"0"+mmFrom:mmFrom),
				""+to.get(Calendar.YEAR)+(mmTo.length()==1?"0"+mmTo:mmTo));
		}
	}	

	// get statistics for data older than 24 hours
	private List<UserStatistics> getStatisticsForAccountNames(final List<String> accountNames,
		final String bottom, final String top) throws Exception {	
		List<UserStatistics> list = null;
		if (accountNames == null || accountNames.size() == 0) {
			list = new LinkedList<UserStatistics>();
		} else {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("bottom", bottom);
			params.put("top", top);
			params.put("accountNames", accountNames);
			list = getSqlSession().selectList("getStatisticsForAccountNames", params);
		}
		return list;
	}
	
	// get statistics including the last 24 hours
	private List<UserStatistics> getStatisticsForAccountNames(final List<String> accountNames, final String bottom,
		final String mid, final String top, final String start, final String end) throws Exception {
		List<UserStatistics> list = null;
		if (accountNames == null || accountNames.size() == 0) {
			list = new LinkedList<UserStatistics>();
		} else {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("bottom", bottom);
			params.put("mid", mid);
			params.put("top", top);
			params.put("accountNames", accountNames);
			params.put("start", start);
			params.put("end", end);
			list = getSqlSession().selectList("getStatisticsForAccountNamesLatest", params);
		}
		return list;
	}
	
	public List<UserStatistics> getStatisticsForProject(final String project, final Calendar from,
		final Calendar to) throws Exception {
		String high = ""+(to.getTimeInMillis()/1000);
		String mid = null;		
		Calendar now = Calendar.getInstance();
		int currMonth = now.get(Calendar.MONTH);
		int currYear = now.get(Calendar.YEAR);
		String mmFrom=null;
		String mmTo=null;
		
		// the selected date range overlaps with past 24 hours' time span
		if ((to.getTimeInMillis())>(System.currentTimeMillis()-86400000)) {
			mid=""+((System.currentTimeMillis()-86400000)/1000);
			Calendar newTo = Calendar.getInstance();
			newTo.set(currYear, currMonth-1, 1, 0, 0, 0);
			Calendar curr=Calendar.getInstance();
			curr.set(currYear, currMonth, 1, 0, 0, 0);
			mmFrom=""+(from.get(Calendar.MONTH)+1);
			mmTo=""+(newTo.get(Calendar.MONTH)+1);
			return getStatisticsForProject(project, ""+(curr.getTimeInMillis()/1000), mid, high,
				""+from.get(Calendar.YEAR)+(mmFrom.length()==1?"0"+mmFrom:mmFrom),
				""+newTo.get(Calendar.YEAR)+(mmTo.length()==1?"0"+mmTo:mmTo));			
		} else {
			mmFrom=""+(from.get(Calendar.MONTH)+1);
			mmTo=""+(to.get(Calendar.MONTH));
			return getStatisticsForProject(project, "" + from.get(Calendar.YEAR)+(mmFrom.length()==1?"0"+mmFrom:mmFrom),
				""+to.get(Calendar.YEAR)+(mmTo.length()==1?"0"+mmTo:mmTo));
		}
	}	
	
	// get statistics for data older than 24 hours
	private List<UserStatistics> getStatisticsForProject(final String project, final String bottom,
		final String top) throws Exception {	
		List<UserStatistics> list = null;
		if (project == null) {
			list = new LinkedList<UserStatistics>();
		} else {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("bottom", bottom);
			params.put("top", top);
			params.put("project", project);
			list = getSqlSession().selectList("getStatisticsForProject", params);
		}
		return list;
	}

	// get statistics including the last 24 hours
	private List<UserStatistics> getStatisticsForProject(final String project, final String bottom,
		final String mid, final String top, final String start, final String end) throws Exception {	
		List<UserStatistics> list = null;
		if (project == null) {
			list = new LinkedList<UserStatistics>();
		} else {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("bottom", bottom);
			params.put("mid", mid);
			params.put("top", top);
			params.put("project", project);
			params.put("start", start);
			params.put("end", end);
			list = getSqlSession().selectList("getStatisticsForProjectLatest", params);
		}
		return list;
	}
	
	public List<Future<BarDiagramStatistics>> getBarDiagramAccountNamesStatistics(final List<String> accountNames,
			Integer startYear, Integer startMonth, 
			Integer endYear, Integer endMonth) throws Exception{
		List<Future<BarDiagramStatistics>> fbdslist = new LinkedList<Future<BarDiagramStatistics>>();
		Calendar from = Calendar.getInstance();
		Calendar to= Calendar.getInstance();
		int currMonth = from.get(Calendar.MONTH);
		int currYear = from.get(Calendar.YEAR);
		
		//get the bar diagram statistics
		to.set(endYear, endMonth, 1,0,0,0);
		int month = startMonth;
		
		boolean currMonthInRange=false; 
		//if current month lies in the range of the selected time period
		if ((to.get(Calendar.YEAR)>currYear) || 
			(to.get(Calendar.MONTH) >= currMonth) && (to.get(Calendar.YEAR) ==currYear)) {
			currMonthInRange=true;
			to.set(currYear, currMonth-1,1,0,0,0);
		}
		
		from.set(startYear, month, 1, 0, 0, 0);
		while ((from.get(Calendar.YEAR) <= to.get(Calendar.YEAR) && 
			   !(from.get(Calendar.YEAR) == to.get(Calendar.YEAR) && 
			     from.get(Calendar.MONTH) > to.get(Calendar.MONTH)))) {
			if (accountNames.size() < 1) {
				fbdslist.add(getBarDiagramStatisticsForAllAccountNames(
					"" + (from.get(Calendar.MONTH) + 1),"" + from.get(Calendar.YEAR)));
			} else {
				fbdslist.add(getBarDiagramStatisticsForAccountNameSet(
					accountNames, "" + (from.get(Calendar.MONTH) + 1), ""+ from.get(Calendar.YEAR)));
			}
		    month += 1;
		    from.set(startYear, month, 1, 0, 0, 0);
		}

		if(currMonthInRange) { //get the data for the current month
			from.set(currYear, currMonth, 1, 0, 0, 0);
			long bottom = from.getTimeInMillis()/1000;
            from.set(currYear, currMonth+1, 1, 0, 0, 0);
		    long top = from.getTimeInMillis()/1000;
		    fbdslist.add(getBarDiagramStatisticsForAccountNameSetCurr(accountNames,"" + bottom,
		    	""+((System.currentTimeMillis()-86400000)/1000), ""+top));
		}
		
		return fbdslist;		
	}

	//get bar diagram statistics for data up to previous month
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForAllAccountNames(final String bottom,
		final String top) throws Exception {
		return this.executorService.submit(
			new Callable<BarDiagramStatistics>() {
				public BarDiagramStatistics call() throws Exception {
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("bottom", bottom);
					params.put("top", top);
					BarDiagramStatistics bds = (BarDiagramStatistics) getSqlSession().selectOne(
						"getBarDiagramStatisticsForAllAccountNamesForInterval", params);
					if (bds == null) {
						bds = new BarDiagramStatistics();
					}
					bds.setBottom(Integer.parseInt(bottom));
					bds.setTop(Integer.parseInt(top));
					return bds;
				}
			}
		);
	}
	
	//get bar diagram statistics for current month's data
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForAccountNameSetCurr(
		final List<String> accountNames, final String bottom, final String mid, 
		final String top) throws Exception {
		return this.executorService.submit(
			new Callable<BarDiagramStatistics>() {
				public BarDiagramStatistics call() throws Exception {
					BarDiagramStatistics bds = null;
					if (accountNames != null && accountNames.size() > 0) {
						Map<String,Object> params = new HashMap<String,Object>();
						params.put("bottom", bottom);
						params.put("top", top);
						params.put("mid", mid);
						params.put("accountNames", accountNames);
						bds = (BarDiagramStatistics) getSqlSession().selectOne(
							"getBarDiagramStatisticsForAccountNameSetForIntervalCurr", params);
					}
					if (bds == null) {
						bds = new BarDiagramStatistics();							
					}
					bds.setBottom(Integer.parseInt(bottom));
					bds.setTop(Integer.parseInt(top));
					return bds;
				}
			}
		);
	}
	
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForAccountNameSet(
		final List<String> accountNames, final String bottom, final String top) throws Exception {
		return  this.executorService.submit(
			new Callable<BarDiagramStatistics>() {
				public BarDiagramStatistics call() throws Exception {
					BarDiagramStatistics bds = null;
					if (accountNames != null && accountNames.size() > 0) {
						Map<String,Object> params = new HashMap<String,Object>();
						params.put("bottom", (bottom.length()==1?"0"+bottom:bottom));
						params.put("top", (top.length()==1?"0"+top:top));
						params.put("accountNames", accountNames);
						bds = (BarDiagramStatistics) getSqlSession().selectOne(
							"getBarDiagramStatisticsForAccountNameSetForInterval", params);
					}
					if (bds == null) {
						bds = new BarDiagramStatistics();
					}
					int month = Integer.parseInt(bottom);
					int year = Integer.parseInt(top);
					Calendar cal = Calendar.getInstance();
					cal.set(year, month-1, 1, 0, 0, 0);
					bds.setBottom(Integer.parseInt(""+(cal.getTimeInMillis()/1000)));
					cal.set(year, month, 1, 0, 0, 0);
					bds.setTop(Integer.parseInt(""+(cal.getTimeInMillis()/1000)));
					return bds;
				}
			}
		);
	}
		
	public List<Future<BarDiagramStatistics>> getProjectStats(final String project, Integer startYear,
		Integer startMonth, Integer endYear, Integer endMonth) throws Exception {

		List<Future<BarDiagramStatistics>> fbdslist = new LinkedList<Future<BarDiagramStatistics>>();

		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		int currMonth = from.get(Calendar.MONTH);
		int currYear = from.get(Calendar.YEAR);
			
		//get the bar diagram statistics
		to.set(endYear, endMonth, 1,0,0,0);
		int month = startMonth;
			
		boolean currMonthInRange=false; 
		//if current month lies in the range of the selected time period
		//and set the end month/year to the current month/year
		if((to.get(Calendar.YEAR)>currYear) || 
			(to.get(Calendar.MONTH) >= currMonth) && (to.get(Calendar.YEAR) ==currYear)) {
			currMonthInRange=true;
			to.set(currYear, currMonth-1,1,0,0,0);
		}
			
		from.set(startYear, month, 1, 0, 0, 0);

		//for time-span (months) before current month
		while ((from.get(Calendar.YEAR) <= to.get(Calendar.YEAR) && 
				!(from.get(Calendar.YEAR) == to.get(Calendar.YEAR) && 
				  from.get(Calendar.MONTH) > to.get(Calendar.MONTH)))) {
			fbdslist.add(getBarDiagramStatisticsForProject(project, "" + (from.get(Calendar.MONTH) + 1),
				""+ from.get(Calendar.YEAR)));
			month += 1;
			from.set(startYear, month, 1, 0, 0, 0);
		}
		//for current month
		if(currMonthInRange) { //get the data for the current month
			from.set(currYear, currMonth, 1, 0, 0, 0);
			long bottom = from.getTimeInMillis()/1000;
	        from.set(currYear, currMonth+1, 1, 0, 0, 0);
			long top = from.getTimeInMillis()/1000;
			fbdslist.add(getBarDiagramStatisticsForProjectCurr(project,"" + bottom,
				""+((System.currentTimeMillis()-86400000)/1000), ""+top));
		}
			
		return fbdslist;
	}
	
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForProject(final String project,
		final String bottom, final String top) throws Exception {
		
		return this.executorService.submit(
			new Callable<BarDiagramStatistics>() {
				public BarDiagramStatistics call() throws Exception {
					BarDiagramStatistics bds = null;
					if (project != null) {
						Map<String,Object> params = new HashMap<String,Object>();
						params.put("bottom", (bottom.length()==1?"0"+bottom:bottom));
						params.put("top", (top.length()==1?"0"+top:top));
						params.put("project", project);
						bds = (BarDiagramStatistics) getSqlSession().selectOne(
							"getBarDiagramStatisticsForProjectForInterval", params);
					}
				    if (bds == null) {
				    	bds = new BarDiagramStatistics();
				    }
					int month = Integer.parseInt(bottom);
					int year = Integer.parseInt(top);
					Calendar cal= Calendar.getInstance();
					cal.set(year, month-1, 1, 0, 0, 0);
					bds.setBottom(Integer.parseInt(""+(cal.getTimeInMillis()/1000)));
					cal.set(year, month, 1, 0, 0, 0);
					bds.setTop(Integer.parseInt(""+(cal.getTimeInMillis()/1000)));
					return bds;
				}
			}
		);
	}
	
	//get bar diagram statistics for current month's data
	public Future<BarDiagramStatistics> getBarDiagramStatisticsForProjectCurr(final String project,
		final String bottom, final String mid, final String top) throws Exception {
		return this.executorService.submit(
			new Callable<BarDiagramStatistics>() {
				public BarDiagramStatistics call() throws Exception {
					BarDiagramStatistics bds = null;
					if (project != null) {
						Map<String,Object> params = new HashMap<String,Object>();
						params.put("bottom", bottom);
						params.put("top", top);
						params.put("mid", mid);
						params.put("project", project);
						bds = (BarDiagramStatistics) getSqlSession().selectOne(
							"getBarDiagramStatisticsForProjectForIntervalCurr", params);
					}
				    if (bds == null) {
				    	bds = new BarDiagramStatistics();
				    }
					bds.setBottom(Integer.parseInt(bottom));
					bds.setTop(Integer.parseInt(top));
					return bds;
				}
			}
		);
	}	

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

}
