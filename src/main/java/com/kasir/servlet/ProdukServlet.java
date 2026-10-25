package com.kasir.servlet;

import com.kasir.model.Produk;
import com.kasir.util.Db;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/produk")
public class ProdukServlet extends HttpServlet {

    static Produk map(ResultSet rs) throws SQLException {
        Produk p = new Produk();
        p.setId(rs.getInt("id")); p.setKode(rs.getString("kode")); p.setNama(rs.getString("nama"));
        p.setHarga(rs.getLong("harga")); p.setStok(rs.getInt("stok"));
        try { p.setKategori(rs.getString("kategori")); } catch (Exception ignored) { p.setKategori("Umum"); }
        return p;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filterKategori = req.getParameter("kategori");
        filterKategori = (filterKategori == null || filterKategori.trim().isEmpty()) ? "" : filterKategori.trim();

        List<Produk> list = new ArrayList<>();
        try (Connection c = Db.get()) {
            String sql = filterKategori.isEmpty()
                    ? "SELECT * FROM produk ORDER BY kategori, nama"
                    : "SELECT * FROM produk WHERE kategori=? ORDER BY nama";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                if (!filterKategori.isEmpty()) ps.setString(1, filterKategori);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(map(rs));
                }
            }
            String edit = req.getParameter("edit");
            if (edit != null && !edit.isEmpty()) {
                try (PreparedStatement ps = c.prepareStatement("SELECT * FROM produk WHERE id=?")) {
                    ps.setInt(1, (int) Db.toLong(edit));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) req.setAttribute("edit", map(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("produk", list);
        req.setAttribute("kategoriList", Db.KATEGORI);
        req.setAttribute("filterKategori", filterKategori);
        req.getRequestDispatcher("/WEB-INF/views/produk.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession s = req.getSession();
        try (Connection c = Db.get()) {
            if ("hapus".equals(action)) {
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM produk WHERE id=?")) {
                    ps.setInt(1, (int) Db.toLong(req.getParameter("id")));
                    ps.executeUpdate();
                }
                s.setAttribute("flash", "Produk dihapus");
            } else {
                String id = req.getParameter("id");
                String kode = req.getParameter("kode").trim(), nama = req.getParameter("nama").trim();
                long harga = Db.toLong(req.getParameter("harga"));
                int stok = (int) Db.toLong(req.getParameter("stok"));
                String kategori = req.getParameter("kategori");
                if (kategori == null || kategori.isEmpty()) kategori = "Umum";

                if (kode.isEmpty() || nama.isEmpty()) {
                    s.setAttribute("flash", "Kode dan nama wajib diisi");
                } else if (id == null || id.isEmpty()) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO produk(kode,nama,harga,stok,kategori) VALUES(?,?,?,?,?)")) {
                        ps.setString(1, kode); ps.setString(2, nama); ps.setLong(3, harga);
                        ps.setInt(4, stok); ps.setString(5, kategori);
                        ps.executeUpdate();
                    }
                    s.setAttribute("flash", "✅ Produk ditambahkan");
                } else {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE produk SET kode=?,nama=?,harga=?,stok=?,kategori=? WHERE id=?")) {
                        ps.setString(1, kode); ps.setString(2, nama); ps.setLong(3, harga);
                        ps.setInt(4, stok); ps.setString(5, kategori);
                        ps.setInt(6, (int) Db.toLong(id));
                        ps.executeUpdate();
                    }
                    s.setAttribute("flash", "✅ Produk diperbarui");
                }
            }
        } catch (SQLException e) {
            s.setAttribute("flash", "Gagal menyimpan (kode produk mungkin sudah dipakai): " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/produk");
    }
}
