package com.kasir.servlet;

import com.kasir.model.Transaksi;
import com.kasir.util.Db;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection c = Db.get()) {
            // Statistik hari ini
            long totalHariIni = 0;
            int transaksiHariIni = 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*), COALESCE(SUM(total),0) FROM transaksi WHERE CAST(tanggal AS DATE) = CURRENT_DATE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { transaksiHariIni = rs.getInt(1); totalHariIni = rs.getLong(2); }
                }
            }

            // Total penjualan bulan ini
            long totalBulanIni = 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COALESCE(SUM(total),0) FROM transaksi WHERE MONTH(tanggal)=MONTH(CURRENT_DATE) AND YEAR(tanggal)=YEAR(CURRENT_DATE)")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalBulanIni = rs.getLong(1);
                }
            }

            // Jumlah produk
            int jumlahProduk = 0;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM produk")) {
                if (rs.next()) jumlahProduk = rs.getInt(1);
            }

            // Stok menipis (<= 5)
            int stokMenipis = 0;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM produk WHERE stok <= 5")) {
                if (rs.next()) stokMenipis = rs.getInt(1);
            }

            // Jumlah user
            int jumlahUser = 0;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next()) jumlahUser = rs.getInt(1);
            }

            // 5 transaksi terakhir
            List<Transaksi> recent = new ArrayList<>();
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM transaksi ORDER BY id DESC LIMIT 5")) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setId(rs.getInt("id"));
                    t.setTanggal(rs.getTimestamp("tanggal"));
                    t.setTotal(rs.getLong("total"));
                    t.setKasir(rs.getString("kasir"));
                    recent.add(t);
                }
            }

            // Data 7 hari terakhir untuk grafik
            String[] chartLabels = new String[7];
            long[] chartData = new long[7];
            Calendar cal = Calendar.getInstance();
            for (int i = 6; i >= 0; i--) {
                Calendar day = Calendar.getInstance();
                day.add(Calendar.DAY_OF_MONTH, -i);
                int idx = 6 - i;
                chartLabels[idx] = String.format("%02d/%02d",
                        day.get(Calendar.DAY_OF_MONTH), day.get(Calendar.MONTH) + 1);
                java.sql.Date sqlDate = new java.sql.Date(day.getTime().getTime());
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT COALESCE(SUM(total),0) FROM transaksi WHERE CAST(tanggal AS DATE)=?")) {
                    ps.setDate(1, sqlDate);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) chartData[idx] = rs.getLong(1);
                    }
                }
            }

            // Build JSON arrays for Chart.js
            StringBuilder labels = new StringBuilder("[");
            StringBuilder data = new StringBuilder("[");
            for (int i = 0; i < 7; i++) {
                if (i > 0) { labels.append(","); data.append(","); }
                labels.append("'").append(chartLabels[i]).append("'");
                data.append(chartData[i]);
            }
            labels.append("]"); data.append("]");

            req.setAttribute("totalHariIni", totalHariIni);
            req.setAttribute("transaksiHariIni", transaksiHariIni);
            req.setAttribute("totalBulanIni", totalBulanIni);
            req.setAttribute("jumlahProduk", jumlahProduk);
            req.setAttribute("stokMenipis", stokMenipis);
            req.setAttribute("jumlahUser", jumlahUser);
            req.setAttribute("recent", recent);
            req.setAttribute("chartLabels", labels.toString());
            req.setAttribute("chartData", data.toString());

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
