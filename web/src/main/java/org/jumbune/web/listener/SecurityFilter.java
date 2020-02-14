package org.jumbune.web.listener;



import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jumbune.common.beans.JumbuneInfo;

import org.jumbune.web.utils.WebConstants;

public class SecurityFilter implements javax.servlet.Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		/*if (JumbuneInfo.isSecured()) {
			HttpSession session = httpServletRequest.getSession(false);
			if (session == null) {
				httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				String user = (String) httpServletRequest.getSession().getAttribute(WebConstants.SESSION_USER_NAME);
				if (user != null) {
					filter.doFilter(request, response);
				} else {
					httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
		}*/
			filter.doFilter(request, response);
		
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	@Override
	public void destroy() {

	}

}
