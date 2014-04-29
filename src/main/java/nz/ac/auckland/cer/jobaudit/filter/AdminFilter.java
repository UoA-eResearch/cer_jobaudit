package nz.ac.auckland.cer.jobaudit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import nz.ac.auckland.cer.jobaudit.util.AuditUtil;
import nz.ac.auckland.cer.jobaudit.dao.AuditDatabaseDao;
import nz.ac.auckland.cer.jobaudit.dao.ProjectDatabaseDao;

/*
 * TODO: Log error if expected request attributes are not there
 * TODO: Send e-mail if expected request attributes are not there
 * TODO: Log each request to tomcat-central logfile (file appender): path, method, cn, shared-token
 */
public class AdminFilter implements Filter {

    private AuditDatabaseDao auditDatabaseDao;
    private ProjectDatabaseDao projectDatabaseDao;
    @Autowired private AuditUtil auditUtil;
    private Logger log = Logger.getLogger(AdminFilter.class.getName());
    private Logger flog = Logger.getLogger("file." + AdminFilter.class.getName());

    public void doFilter(
            ServletRequest req,
            ServletResponse resp,
            FilterChain fc) throws IOException, ServletException {

        try {
            HttpServletRequest request = (HttpServletRequest) req;
            String sharedToken = (String) req.getAttribute("shared-token");
            String eppn = (String) req.getAttribute("eppn");
            flog.info(auditUtil.createAuditLogMessage(request, "eppn=\"" + eppn +"\" shared-token=" + sharedToken));
            if (eppn == null || sharedToken == null) {
                log.error("At least one required Tuakiri attribute is null: eppn=" + eppn + ", shared-token=" + sharedToken);
            }
            boolean isUserAdviser = this.projectDatabaseDao.isCurrentUserAdviser(sharedToken);
            boolean isUserAdmin = this.auditDatabaseDao.isCurrentUserAdmin(eppn);
            req.setAttribute("showAdminView", isUserAdviser || isUserAdmin);
        } catch (final Exception e) {
            log.error("Unexpected error", e);
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
