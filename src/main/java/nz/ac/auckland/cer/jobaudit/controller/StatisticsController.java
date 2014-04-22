package nz.ac.auckland.cer.jobaudit.controller;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.ac.auckland.cer.jobaudit.db.AuditDatabaseDao;
import nz.ac.auckland.cer.jobaudit.db.ProjectDatabaseDao;
import nz.ac.auckland.cer.jobaudit.pojo.BarDiagramStatistics;
import nz.ac.auckland.cer.jobaudit.pojo.StatisticsFormData;
import nz.ac.auckland.cer.jobaudit.pojo.User;
import nz.ac.auckland.cer.jobaudit.pojo.UserStatistics;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


//FIXME: Set up safety net for unexpected exceptions
@Controller
public class StatisticsController {

	private Logger log = Logger.getLogger("StatisticsController.class");
	private Map<Integer, String> MONTHS;
	private AuditDatabaseDao auditDatabaseDao;
	private ProjectDatabaseDao projectDatabaseDao;
	private Integer historyFirstYear;
	private Integer historyFirstMonth;

	public StatisticsController() {
		// initialize months
		this.MONTHS = new HashMap<Integer,String>();
	    String[] tmp = new DateFormatSymbols().getMonths();
		for (int i=0; i<12; i++) {
			MONTHS.put(i, tmp[i]);
		}
	}
	
	@RequestMapping(value = "statistics", method = RequestMethod.GET)
	public ModelAndView onGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Calendar now = Calendar.getInstance();
		StatisticsFormData formData = new StatisticsFormData();
		formData.setFirstMonth(this.historyFirstMonth);
		formData.setFirstYear(this.historyFirstYear);
		formData.setLastMonth(now.get(Calendar.MONTH));
		formData.setLastYear(now.get(Calendar.YEAR));
		formData.setCategory("Researcher");
		return this.handleRequest(request, formData);
	}

	@RequestMapping(value = "statistics", method = RequestMethod.POST)
	public ModelAndView onPost(HttpServletRequest request, HttpServletResponse response,
			StatisticsFormData formData) throws Exception {
	    return this.handleRequest(request, formData);
	}
	
	private ModelAndView handleRequest(HttpServletRequest request, StatisticsFormData formData) throws Exception {
		ModelAndView mav = new ModelAndView("statistics");
		Future<List<User>> fResearchersInDropDown = null;
		Future<List<String>> fAffil = null;
		List<UserStatistics> userstatslist = new LinkedList<UserStatistics>();
		List<Future<BarDiagramStatistics>> fbdslist = new LinkedList<Future<BarDiagramStatistics>>();
		List<BarDiagramStatistics> bdslist = new LinkedList<BarDiagramStatistics>();
		List<User> researcherList = new LinkedList<User>();
		List<User> researchersForDropDown = new LinkedList<User>();
		List<String> projectCodesForDropDown = null;
		List<String> accountNames = null;
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();

		// initialize categories
	    List<String> categories = new LinkedList<String>();
	    categories.add("Researcher");
	    categories.add("Project");
	    if ((Boolean)request.getAttribute("showAdminView")) {
		    categories.add("Affiliation");
		    if (formData.getCategory().equals("Affiliation")) {
			    accountNames = this.getAccountNamesForAffiliation(request, formData);
		    } else {
			    accountNames = this.getAccountNamesForResearcher(request, formData);		    	
		    }
		    projectCodesForDropDown = this.projectDatabaseDao.getProjectCodes();
			fResearchersInDropDown = this.auditDatabaseDao.getUsers();
			fAffil = this.auditDatabaseDao.getAffiliations();
			this.appendDummyUser(researchersForDropDown);
			researchersForDropDown.addAll(fResearchersInDropDown.get());
			mav.addObject("affiliationsForDropDown", fAffil.get());
	    } else {
	    	String sharedToken = (String)request.getAttribute("shared-token");
			accountNames = projectDatabaseDao.getResearcherAccountNamesForSharedToken(sharedToken);
			researchersForDropDown = this.auditDatabaseDao.getUsersForAccountNames(accountNames);
			projectCodesForDropDown = this.projectDatabaseDao.getProjectCodesForSharedToken(sharedToken);
	    }
		
		from.set(formData.getFirstYear(), formData.getFirstMonth(), 1, 0, 0, 0);
		to.set(formData.getLastYear(), formData.getLastMonth() + 1, 1, 0, 0, 0);

		if (formData.getCategory().equals("Project")) {
			String project = formData.getCategoryChoice();
			userstatslist = this.auditDatabaseDao.getStatisticsForProject(project, from, to);
			List<String> tmp = new LinkedList<String>();
			for (UserStatistics us: userstatslist) {
				tmp.add(us.getUser());
			}
			researcherList = this.auditDatabaseDao.getUsersForAccountNames(tmp);
			fbdslist = auditDatabaseDao.getProjectStats(project, formData.getFirstYear(),
				formData.getFirstMonth(), formData.getLastYear(), formData.getLastMonth());
		} else {
			userstatslist = this.auditDatabaseDao.getStatisticsForAccountNames(accountNames, from, to);
			bdslist = new LinkedList<BarDiagramStatistics>();
			fbdslist = this.auditDatabaseDao.getBarDiagramAccountNamesStatistics(accountNames,
				formData.getFirstYear(), formData.getFirstMonth(), formData.getLastYear(), 
				formData.getLastMonth());			
		}
		
		for (Future<BarDiagramStatistics> fbds : fbdslist) {
			bdslist.add(fbds.get());
		}
		
		mav.addObject("researchersForDropDown", researchersForDropDown);
		mav.addObject("projectCodesForDropDown", projectCodesForDropDown);		
		mav.addObject("monthsForDropDown", this.MONTHS);
		mav.addObject("yearsForDropDown", this.createYearList());
		mav.addObject("categoriesForDropDown", categories);
		mav.addObject("researcherList", researcherList);
        mav.addObject("userStatistics", userstatslist);
		mav.addObject("jobStatistics", bdslist);
		mav.addObject("formData", formData);
	    return mav;		
	}
	
	private List<String> getAccountNamesForResearcher(HttpServletRequest req, StatisticsFormData formData) throws Exception {
		List<String> users = new LinkedList<String>();
		String user = formData.getCategoryChoice();
		if (user != null && !(user.equalsIgnoreCase("all"))) {
			users.add(user);
		} else {
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.set(formData.getFirstYear(), formData.getFirstMonth(), 1,0,0,0);
			to.set(formData.getLastYear(), formData.getLastMonth()+1, 1,0,0,0);
			
			// list of all user names
			users.addAll(this.auditDatabaseDao.getAccountNames(
				""+(from.getTimeInMillis()/1000),""+(to.getTimeInMillis()/1000)).get());
		}
		return users;
	}

	private List<String> getAccountNamesForAffiliation(HttpServletRequest req, StatisticsFormData formData) throws Exception {
		List<String> users = new LinkedList<String>();
		Future<List<String>> fuserlist = null;
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.set(formData.getFirstYear(), formData.getFirstMonth(), 1,0,0,0);
		to.set(formData.getLastYear(), formData.getLastMonth()+1, 1,0,0,0);
		String affil = formData.getCategoryChoice();
		String[] subs = affil.split("/");
		List<String> usersWithAtLeastOneJob = this.auditDatabaseDao.getAccountNames(
			"" + (from.getTimeInMillis() / 1000),
			"" + (to.getTimeInMillis() / 1000)).get();

		if (StringUtils.countMatches(affil, "/") == 1) {
			fuserlist = this.auditDatabaseDao.getAccountNamesForAffiliation(subs[1].trim());
		} else if (StringUtils.countMatches(affil, "/") == 2) {
			fuserlist = this.auditDatabaseDao.getAccountNamesForAffiliation(subs[1].trim(),
				subs[2].trim());
		} else if (StringUtils.countMatches(affil, "/") == 3) {
			fuserlist = this.auditDatabaseDao.getAccountNamesForAffiliation(subs[1].trim(),
				subs[2].trim(), subs[3].trim());
		} else {
			throw new Exception("Unexpected affilation string: " + affil);
		}
		List<String> usersForAffil = fuserlist.get();
		for (String u : usersForAffil) {
			if (usersWithAtLeastOneJob.contains(u)) {
				users.add(u);
			}
		}
		return users;
	}

	private List<Integer> createYearList() {
		List<Integer> years = new LinkedList<Integer>();
		for (int i = this.historyFirstYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
			years.add(i);
		}
		return years;
	}

	private void appendDummyUser(List<User> l) {
		User u = new User();
		u.setId("all");
		u.setName("All");
		l.add(0, u);
	}

	public void setProjectDatabaseDao(ProjectDatabaseDao projectDatabaseDao) {
		this.projectDatabaseDao = projectDatabaseDao;
	}

	public void setAuditDatabaseDao(AuditDatabaseDao auditDatabaseDao) {
		this.auditDatabaseDao = auditDatabaseDao;
	}

	public void setHistoryFirstYear(Integer historyFirstYear) {
		this.historyFirstYear = historyFirstYear;
	}

	public void setHistoryFirstMonth(Integer historyFirstMonth) {
		this.historyFirstMonth = historyFirstMonth;
	}
	
}
