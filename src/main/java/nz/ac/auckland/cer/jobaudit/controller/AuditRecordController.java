package nz.ac.auckland.cer.jobaudit.controller;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import nz.ac.auckland.cer.jobaudit.dao.AuditDatabaseDao;
import nz.ac.auckland.cer.jobaudit.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.jobaudit.pojo.AuditRecord;
import nz.ac.auckland.cer.jobaudit.pojo.AuditRecordFormData;
import nz.ac.auckland.cer.jobaudit.pojo.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

// FIXME: Set up safety net for unexpected exceptions
@Controller
public class AuditRecordController {

	private AuditDatabaseDao auditDatabaseDao;
	private ProjectDatabaseDao projectDatabaseDao;
	private long maxJobRecordsPerPage;
	private List<String> allowedOrderBys;
	
	public AuditRecordController() {
		this.allowedOrderBys = new LinkedList<String>();
		Field[] declaredFields = AuditRecord.class.getDeclaredFields();
        for (Field f: declaredFields) {
        	if (!f.getName().equals("id")) {
        		allowedOrderBys.add(f.getName());
        	}
        }
		Collections.sort(allowedOrderBys);
	}
	
	@RequestMapping(value = "auditrecords", method = RequestMethod.GET)
	public ModelAndView doGet(HttpServletRequest request) throws Exception {
        AuditRecordFormData formData = new AuditRecordFormData();
		String sharedToken = (String)request.getAttribute("shared-token");
		// FIXME: handle situation when requester doesn't have a cluster account
        List<String> accountNames = projectDatabaseDao.getResearcherOrAdviserAccountNamesForSharedToken(sharedToken);
        formData.setUser(accountNames.get(0));
        formData.setSortOrder("desc");
        formData.setOrderBy("qtime");
        return this.handleRequest(request, formData);
	}

	@RequestMapping(value = "auditrecords", method = RequestMethod.POST)
	public ModelAndView doPost(HttpServletRequest request, AuditRecordFormData formData) throws Exception {
    	return this.handleRequest(request, formData);
	}

    private ModelAndView handleRequest(HttpServletRequest request, AuditRecordFormData formData) throws Exception {
    	ModelAndView mav = new ModelAndView("auditrecords");
    	Future<List<User>> fUsers = null;
		Future<Integer> fnr = this.auditDatabaseDao.getNumberRecords(formData.getUser());
    	if ((Boolean)request.getAttribute("showAdminView")) {
    		fUsers = this.auditDatabaseDao.getUsers();
    		mav.addObject("researchersInDropDown", this.createResearcherDropDownMap(fUsers.get()));
    	} else {
    		String sharedToken = (String)request.getAttribute("shared-token");
    		List<String> accountNames = projectDatabaseDao.getResearcherOrAdviserAccountNamesForSharedToken(sharedToken);
    		List<User> users = this.auditDatabaseDao.getUsersForAccountNames(accountNames);
			mav.addObject("researchersInDropDown", this.createResearcherDropDownMap(users));
    	}
	    mav.addObject("totalNumberRecords", fnr.get());
        mav.addObject("auditRecordRequest", formData);
        mav.addObject("orderBys", this.allowedOrderBys);
        mav.addObject("sortOrders", this.createSortOrderDropDownMap());
        mav.addObject("maxJobRecordsPerPage", this.maxJobRecordsPerPage);
		return mav;
    }
    
	/**
	 * Request to get a bunch of audit record.
	 * Returns a json-formatted list of audit records.
	 */
	@RequestMapping(value = "auditrecords/rest/{accountName}/{orderby}/{sortorder}/{offset}", method = RequestMethod.GET)
	public @ResponseBody List<AuditRecord> doGetRest(@PathVariable("accountName") String accountName,
	    @PathVariable("orderby") String orderby, @PathVariable("sortorder") String sortorder, 
	    @PathVariable("offset") long offset, HttpServletRequest request) throws Exception {
		/* The following 2 checks are necessary, because the value of "order by"
         * and the sort order (ascending, descending) are text placement in the query, 
		 * and can be abused by SQL injections
		 */
		if (!(sortorder.equalsIgnoreCase("asc") || sortorder.equalsIgnoreCase("desc"))) {
			throw new Exception("Invalid sort order: " + sortorder);
		}
		if (!allowedOrderBys.contains(orderby)) {
			throw new Exception("Invalid order request: " + orderby);
		}
		// FIXME: verify requester doesn't ask for records for any other than his own account, unless admin
        return this.auditDatabaseDao.getRecords(accountName, orderby, sortorder, offset, maxJobRecordsPerPage).get();
	}
	
	private Map<String,String> createResearcherDropDownMap(List<User> users) throws Exception {
		Map<String,String> tmp = new LinkedHashMap<String, String>();
		for (User u: users) {
			tmp.put(u.getId(), u.getName());
		}
		return tmp;
	}

	private Map<String,String> createSortOrderDropDownMap() {
		Map<String,String> tmp = new LinkedHashMap<String, String>();
		tmp.put("asc", "ascending");
		tmp.put("desc", "descending");
		return tmp;
	}

	public void setAuditDatabaseDao(AuditDatabaseDao auditDatabaseDao) {
		this.auditDatabaseDao = auditDatabaseDao;
	}

	public void setMaxJobRecordsPerPage(Long maxJobRecordsPerPage) {
		this.maxJobRecordsPerPage = maxJobRecordsPerPage;
	}

	public void setProjectDatabaseDao(ProjectDatabaseDao projectDatabaseDao) {
		this.projectDatabaseDao = projectDatabaseDao;
	}

}
