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

import nz.ac.auckland.cer.jobaudit.dao.AuditDbDao;
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
public class SimpleStatisticsController {

    private Logger log = Logger.getLogger("SimpleStatisticsController.class");
    private Map<Integer, String> MONTHS;
    private AuditDbDao auditDbDao;
    private Integer historyFirstYear;
    private Integer historyFirstMonth;

    public SimpleStatisticsController() {

        // initialize months
        this.MONTHS = new HashMap<Integer, String>();
        String[] tmp = new DateFormatSymbols().getMonths();
        for (int i = 0; i < 12; i++) {
            MONTHS.put(i, tmp[i]);
        }
    }

    @RequestMapping(value = "simplestatistics", method = RequestMethod.GET)
    public ModelAndView onGet(
            HttpServletRequest request,
            HttpServletResponse response,
            StatisticsFormData formData) throws Exception {

        if (formData == null || formData.getFirstMonth() == null) {
            // no choice has been made
            Calendar now = Calendar.getInstance();
            formData = new StatisticsFormData();
            formData.setFirstMonth(this.historyFirstMonth);
            formData.setFirstYear(this.historyFirstYear);
            formData.setLastMonth(now.get(Calendar.MONTH));
            formData.setLastYear(now.get(Calendar.YEAR));
        }
        return this.handleRequest(request, formData);
    }

    private ModelAndView handleRequest(
            HttpServletRequest request,
            StatisticsFormData formData) throws Exception {

        ModelAndView mav = new ModelAndView("simplestatistics");
        Future<List<String>> fAffil = null;
        List<UserStatistics> userstatslist = new LinkedList<UserStatistics>();
        List<Future<BarDiagramStatistics>> fbdslist = new LinkedList<Future<BarDiagramStatistics>>();
        List<BarDiagramStatistics> bdslist = new LinkedList<BarDiagramStatistics>();
        List<User> researcherList = new LinkedList<User>();
        List<String> accountNames = null;
        List<String> affiliations = null;
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        if (formData.getCategoryChoice() == null || formData.getCategoryChoice().equals("All")) {
            accountNames = this.auditDbDao.getAccountNames("" + (from.getTimeInMillis() / 1000),
                    "" + (to.getTimeInMillis() / 1000)).get();
        } else {
            accountNames = this.getAccountNamesForAffiliation(request, formData);    
        }
        researcherList = this.auditDbDao.getUsersForAccountNames(accountNames);

        fAffil = this.auditDbDao.getAuditAffiliations();
        affiliations = fAffil.get();
        affiliations.add(0, "All");

        from.set(formData.getFirstYear(), formData.getFirstMonth(), 1, 0, 0, 0);
        to.set(formData.getLastYear(), formData.getLastMonth() + 1, 1, 0, 0, 0);

        userstatslist = this.auditDbDao.getStatisticsForAccountNames(accountNames, from, to);
        fbdslist = this.auditDbDao.getBarDiagramAccountNamesStatistics(accountNames, formData.getFirstYear(),
                    formData.getFirstMonth(), formData.getLastYear(), formData.getLastMonth());

        for (Future<BarDiagramStatistics> fbds : fbdslist) {
            bdslist.add(fbds.get());
        }

        mav.addObject("affiliationsForDropDown", affiliations);
        mav.addObject("monthsForDropDown", this.MONTHS);
        mav.addObject("yearsForDropDown", this.createYearList());
        mav.addObject("userStatistics", userstatslist);
        mav.addObject("researcherList", researcherList);
        mav.addObject("jobStatistics", bdslist);
        mav.addObject("formData", formData);
        mav.addObject("cn", request.getAttribute("cn"));
        return mav;
    }

    private List<String> getAccountNamesForAffiliation(
            HttpServletRequest req,
            StatisticsFormData formData) throws Exception {

        List<String> users = new LinkedList<String>();
        Future<List<String>> fuserlist = null;
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.set(formData.getFirstYear(), formData.getFirstMonth(), 1, 0, 0, 0);
        to.set(formData.getLastYear(), formData.getLastMonth() + 1, 1, 0, 0, 0);
        String affil = formData.getCategoryChoice();
        String[] subs = affil.split("/");
        List<String> usersWithAtLeastOneJob = this.auditDbDao.getAccountNames(
                "" + (from.getTimeInMillis() / 1000), "" + (to.getTimeInMillis() / 1000)).get();

        if (StringUtils.countMatches(affil, "/") == 1) {
            fuserlist = this.auditDbDao.getAccountNamesForAffiliation(subs[1].trim());
        } else if (StringUtils.countMatches(affil, "/") == 2) {
            fuserlist = this.auditDbDao.getAccountNamesForAffiliation(subs[1].trim(), subs[2].trim());
        } else if (StringUtils.countMatches(affil, "/") == 3) {
            fuserlist = this.auditDbDao.getAccountNamesForAffiliation(subs[1].trim(), subs[2].trim(),
                    subs[3].trim());
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

    public void setAuditDbDao(
            AuditDbDao auditDbDao) {

        this.auditDbDao = auditDbDao;
    }

    public void setHistoryFirstYear(
            Integer historyFirstYear) {

        this.historyFirstYear = historyFirstYear;
    }

    public void setHistoryFirstMonth(
            Integer historyFirstMonth) {

        this.historyFirstMonth = historyFirstMonth;
    }

}
