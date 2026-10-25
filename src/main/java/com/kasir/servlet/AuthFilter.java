package com.kasir.servlet;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse p = (HttpServletResponse) res;
        r.setCharacterEncoding("UTF-8");
        String path = r.getRequestURI().substring(r.getContextPath().length());
        boolean open = path.equals("/login") || path.startsWith("/css/");
        HttpSession s = r.getSession(false);
        if (open || (s != null && s.getAttribute("user") != null)) {
            chain.doFilter(req, res);
        } else {
            p.sendRedirect(r.getContextPath() + "/login");
        }
    }
}
