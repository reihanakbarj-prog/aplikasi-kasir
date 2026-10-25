package com.kasir.servlet;

import com.kasir.model.User;
import com.kasir.util.Db;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && "admin".equals(s.getAttribute("role"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            req.getSession().setAttribute("flash", "Akses ditolak. Hanya admin yang bisa mengelola user.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        List<User> list = new ArrayList<>();
        try (Connection c = Db.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, username, nama, role FROM users ORDER BY id")) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setNama(rs.getString("nama"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("users", list);
        req.setAttribute("kategoriList", Db.KATEGORI);
        req.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        String action = req.getParameter("action");
        HttpSession s = req.getSession();
        try (Connection c = Db.get()) {
            if ("tambah".equals(action)) {
                String username = req.getParameter("username").trim();
                String nama = req.getParameter("nama").trim();
                String password = req.getParameter("password");
                String role = req.getParameter("role");
                if (username.isEmpty() || nama.isEmpty() || password == null || password.isEmpty()) {
                    s.setAttribute("flash", "Semua field wajib diisi");
                } else if (password.length() < 4) {
                    s.setAttribute("flash", "Password minimal 4 karakter");
                } else {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO users(username,password,nama,role) VALUES(?,?,?,?)")) {
                        ps.setString(1, username);
                        ps.setString(2, Db.sha256(password));
                        ps.setString(3, nama);
                        ps.setString(4, (role == null || role.isEmpty()) ? "kasir" : role);
                        ps.executeUpdate();
                        s.setAttribute("flash", "✅ User " + username + " berhasil ditambahkan");
                    }
                }
            } else if ("hapus".equals(action)) {
                int id = (int) Db.toLong(req.getParameter("id"));
                String myUsername = (String) s.getAttribute("username");
                try (PreparedStatement ps = c.prepareStatement("SELECT username FROM users WHERE id=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String targetUsername = rs.getString("username");
                            if (targetUsername.equals(myUsername)) {
                                s.setAttribute("flash", "Tidak bisa menghapus akun sendiri");
                            } else {
                                try (PreparedStatement del = c.prepareStatement("DELETE FROM users WHERE id=?")) {
                                    del.setInt(1, id);
                                    del.executeUpdate();
                                    s.setAttribute("flash", "✅ User berhasil dihapus");
                                }
                            }
                        }
                    }
                }
            } else if ("gantipw".equals(action)) {
                int id = (int) Db.toLong(req.getParameter("id"));
                String newpw = req.getParameter("newpw");
                if (newpw == null || newpw.length() < 4) {
                    s.setAttribute("flash", "Password minimal 4 karakter");
                } else {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE users SET password=? WHERE id=?")) {
                        ps.setString(1, Db.sha256(newpw));
                        ps.setInt(2, id);
                        ps.executeUpdate();
                        s.setAttribute("flash", "✅ Password berhasil diubah");
                    }
                }
            }
        } catch (SQLException e) {
            s.setAttribute("flash", "Error: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
