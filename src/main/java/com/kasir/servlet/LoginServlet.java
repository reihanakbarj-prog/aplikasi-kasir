package com.kasir.servlet;

import com.kasir.util.Db;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().equals("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // Jika sudah login, langsung ke dashboard
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String u = req.getParameter("username"), p = req.getParameter("password");
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT nama, role FROM users WHERE username=? AND password=?")) {
            ps.setString(1, u == null ? "" : u.trim());
            ps.setString(2, Db.sha256(p == null ? "" : p));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession s = req.getSession(true);
                    s.setAttribute("user", rs.getString("nama"));
                    s.setAttribute("username", u == null ? "" : u.trim());
                    s.setAttribute("role", rs.getString("role"));
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    return;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("error", "Username atau password salah");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
