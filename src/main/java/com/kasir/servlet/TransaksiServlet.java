package com.kasir.servlet;

import com.kasir.model.CartItem;
import com.kasir.model.Transaksi;
import com.kasir.util.Db;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/riwayat", "/struk"})
public class TransaksiServlet extends HttpServlet {

    private Transaksi map(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi();
        t.setId(rs.getInt("id")); t.setTanggal(rs.getTimestamp("tanggal")); t.setTotal(rs.getLong("total"));
        t.setBayar(rs.getLong("bayar")); t.setKembali(rs.getLong("kembali")); t.setKasir(rs.getString("kasir"));
        return t;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection c = Db.get()) {
            if (req.getServletPath().equals("/struk")) {
                // Halaman struk
                int id = (int) Db.toLong(req.getParameter("id"));
                Transaksi t = null;
                try (PreparedStatement ps = c.prepareStatement("SELECT * FROM transaksi WHERE id=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) t = map(rs); }
                }
                if (t == null) { resp.sendRedirect(req.getContextPath() + "/riwayat"); return; }
                try (PreparedStatement ps = c.prepareStatement("SELECT * FROM detail WHERE transaksi_id=? ORDER BY id")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next())
                            t.getItems().add(new CartItem(rs.getInt("produk_id"), rs.getString("nama"),
                                    rs.getLong("harga"), rs.getInt("qty")));
                    }
                }
                req.setAttribute("t", t);
                req.getRequestDispatcher("/WEB-INF/views/struk.jsp").forward(req, resp);
            } else {
                // Halaman riwayat dengan filter tanggal
                String dari = req.getParameter("dari");
                String sampai = req.getParameter("sampai");
                boolean hasFilter = dari != null && !dari.isEmpty() && sampai != null && !sampai.isEmpty();

                List<Transaksi> list = new ArrayList<>();
                long sum = 0;

                String sql = hasFilter
                        ? "SELECT * FROM transaksi WHERE CAST(tanggal AS DATE) BETWEEN ? AND ? ORDER BY id DESC LIMIT 500"
                        : "SELECT * FROM transaksi ORDER BY id DESC LIMIT 200";

                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    if (hasFilter) {
                        ps.setDate(1, java.sql.Date.valueOf(dari));
                        ps.setDate(2, java.sql.Date.valueOf(sampai));
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Transaksi t = map(rs);
                            sum += t.getTotal();
                            list.add(t);
                        }
                    }
                }

                req.setAttribute("list", list);
                req.setAttribute("sum", sum);
                req.setAttribute("dari", dari == null ? "" : dari);
                req.setAttribute("sampai", sampai == null ? "" : sampai);
                req.getRequestDispatcher("/WEB-INF/views/riwayat.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
