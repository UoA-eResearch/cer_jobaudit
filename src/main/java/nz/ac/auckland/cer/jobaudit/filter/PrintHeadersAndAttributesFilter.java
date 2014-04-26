package nz.ac.auckland.cer.jobaudit.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class PrintHeadersAndAttributesFilter implements Filter {

    private Logger log = Logger.getLogger("PrintHeadersAndAttributesFilter.class");

    public void doFilter(
            ServletRequest req,
            ServletResponse resp,
            FilterChain fc) throws IOException, ServletException {

        try {
            final HttpServletRequest request = (HttpServletRequest) req;
            System.err.println("HTTP Request Headers:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                System.err.println(headerName + ": " + request.getHeader(headerName));
            }
            System.err.println("");
            System.err.println("Request Attributes:");
            Enumeration<String> attribNames = request.getAttributeNames();
            while (headerNames.hasMoreElements()) {
                String attribName = attribNames.nextElement();
                System.err.println(attribName + ": " + request.getAttribute(attribName));
            }
            System.err.println("");
            System.err.println("Hidden Request Attributes:");
            System.err.println("shared-token: " + request.getAttribute("shared-token"));
            System.err.println("cn: " + request.getAttribute("cn"));
            System.err.println("Shib-Identity-Provider: " + request.getAttribute("Shib-Identity-Provider"));
            System.err.println("Tuakiri Unique ID (eppn): " + request.getAttribute("eppn"));
            System.err.println("mail: " + request.getAttribute("mail"));
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

}
