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

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.util.AuditLog;
import nz.ac.auckland.cer.jobaudit.dao.AuditDbDao;

/*
 * TODO: Send e-mail if expected request attributes are not there
 */
public class AdminFilter implements Filter {

    private AuditDbDao auditDbDao;
    private ProjectDbDao projectDbDao;
    @Autowired private AuditLog auditLog;
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
            String cn = (String) req.getAttribute("cn");
            flog.info(auditLog.createAuditLogMessage(request, "cn=\"" + cn + "\" eppn=" + eppn + " shared-token=" + sharedToken));
            if (cn == null || eppn == null || sharedToken == null) {
                log.error("At least one required Tuakiri attribute is null: cn=" + cn + ", eppn=" + eppn + ", shared-token=" + sharedToken);
            }
            boolean isUserAdviser = this.projectDbDao.isUserAdviser(sharedToken);
            boolean isUserAdmin = this.auditDbDao.isCurrentUserAdmin(eppn);
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

    public void setAuditDbDao(
            AuditDbDao auditDbDao) {

        this.auditDbDao = auditDbDao;
    }

    public void setProjectDbDao(
            ProjectDbDao projectDbDao) {

        this.projectDbDao = projectDbDao;
    }

}
