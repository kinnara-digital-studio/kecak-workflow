package org.joget.apps.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HSTSFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res,
        FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) res;

        if (req.isSecure())
            resp.setHeader("Strict-Transport-Security", "max-age=31622400; includeSubDomains");

        chain.doFilter(req, resp);
    }

	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	public void destroy() {		
	}
}
