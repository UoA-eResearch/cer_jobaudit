package nz.ac.auckland.cer.jobaudit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import nz.ac.auckland.cer.jobaudit.dao.AuditDatabaseDao;
import nz.ac.auckland.cer.jobaudit.dao.ProjectDatabaseDao;

public class AdminFilter implements Filter {

    private AuditDatabaseDao auditDatabaseDao;
    private ProjectDatabaseDao projectDatabaseDao;
    private Logger log = Logger.getLogger("AdminFilter.class");

    public void doFilter(
            ServletRequest req,
            ServletResponse resp,
            FilterChain fc) throws IOException, ServletException {

        try {
            String sharedToken = (String) req.getAttribute("shared-token");
            String tuakiriUniqueId = (String) req.getAttribute("eppn");
            boolean isUserAdviser = this.projectDatabaseDao.isCurrentUserAdviser(sharedToken);
            boolean isUserAdmin = this.auditDatabaseDao.isCurrentUserAdmin(tuakiriUniqueId);
            req.setAttribute("showAdminView", isUserAdviser || isUserAdmin);
        } catch (final Exception e) {
            log.error(e);
            return;
        }
        fc.doFilter(req, resp);
    }

    public void init(
            FilterConfig fc) throws ServletException {

    }

    public void destroy() {

    }

    public void setAuditDatabaseDao(
            AuditDatabaseDao auditDatabaseDao) {

        this.auditDatabaseDao = auditDatabaseDao;
    }

    public void setProjectDatabaseDao(
            ProjectDatabaseDao projectDatabaseDao) {

        this.projectDatabaseDao = projectDatabaseDao;
    }

}
