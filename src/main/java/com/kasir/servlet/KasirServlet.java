package com.kasir.servlet;

import com.kasir.model.CartItem;
import com.kasir.model.Produk;
import com.kasir.util.Db;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/kasir")
public class KasirServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> cart(HttpSession s) {
        Map<Integer, CartItem> c = (Map<Integer, CartItem>) s.getAttribute("cart");
        if (c == null) { c = new LinkedHashMap<>(); s.setAttribute("cart", c); }
        return c;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q");
        q = q == null ? "" : q.trim();
        List<Produk> list = new ArrayList<>();
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM produk WHERE LOWER(nama) LIKE ? OR LOWER(kode) LIKE ? ORDER BY nama")) {
            String like = "%" + q.toLowerCase() + "%";
            ps.setString(1, like); ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(ProdukServlet.map(rs));
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        Map<Integer, CartItem> cart = cart(req.getSession());
        long total = 0;
        for (CartItem i : cart.values()) total += i.getSubtotal();
        req.setAttribute("produk", list);
        req.setAttribute("q", q);
        req.setAttribute("items", cart.values());
        req.setAttribute("total", total);
        req.getRequestDispatcher("/WEB-INF/views/kasir.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        Map<Integer, CartItem> cart = cart(s);
        String action = req.getParameter("action");
        String back = req.getContextPath() + "/kasir";
        try {
            if ("tambah".equals(action)) {
                int id = (int) Db.toLong(req.getParameter("id"));
                int qty = Math.max(1, (int) Db.toLong(req.getParameter("qty")));
                try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM produk WHERE id=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            Produk p = ProdukServlet.map(rs);
                            CartItem ex = cart.get(id);
                            int baru = (ex == null ? 0 : ex.getQty()) + qty;
                            if (baru > p.getStok()) {
                                s.setAttribute("flash", "Stok " + p.getNama() + " tidak cukup (sisa " + p.getStok() + ")");
                            } else if (ex == null) {
                                cart.put(id, new CartItem(id, p.getNama(), p.getHarga(), qty));
                            } else {
                                ex.setQty(baru);
                            }
                        }
                    }
                }
            } else if ("hapus".equals(action)) {
                cart.remove((int) Db.toLong(req.getParameter("id")));
            } else if ("kosongkan".equals(action)) {
                cart.clear();
            } else if ("bayar".equals(action)) {
                int idTrx = checkout(s, cart, Db.toLong(req.getParameter("bayar")));
                if (idTrx > 0) back = req.getContextPath() + "/struk?id=" + idTrx;
            }
        } catch (SQLException e) {
            s.setAttribute("flash", "Terjadi kesalahan: " + e.getMessage());
        }
        resp.sendRedirect(back);
    }

    private int checkout(HttpSession s, Map<Integer, CartItem> cart, long bayar) throws SQLException {
        if (cart.isEmpty()) { s.setAttribute("flash", "Keranjang masih kosong"); return 0; }
        long total = 0;
        for (CartItem i : cart.values()) total += i.getSubtotal();
        if (bayar < total) { s.setAttribute("flash", "Uang bayar kurang dari total"); return 0; }

        try (Connection c = Db.get()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement("UPDATE produk SET stok=stok-? WHERE id=? AND stok>=?")) {
                    for (CartItem i : cart.values()) {
                        ps.setInt(1, i.getQty()); ps.setInt(2, i.getProdukId()); ps.setInt(3, i.getQty());
                        if (ps.executeUpdate() == 0) throw new SQLException("Stok tidak cukup: " + i.getNama());
                    }
                }
                int idTrx;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO transaksi(tanggal,total,bayar,kembali,kasir) VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setLong(2, total); ps.setLong(3, bayar); ps.setLong(4, bayar - total);
                    ps.setString(5, String.valueOf(s.getAttribute("user")));
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); idTrx = k.getInt(1); }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO detail(transaksi_id,produk_id,nama,harga,qty,subtotal) VALUES(?,?,?,?,?,?)")) {
                    for (CartItem i : cart.values()) {
                        ps.setInt(1, idTrx); ps.setInt(2, i.getProdukId()); ps.setString(3, i.getNama());
                        ps.setLong(4, i.getHarga()); ps.setInt(5, i.getQty()); ps.setLong(6, i.getSubtotal());
                        ps.executeUpdate();
                    }
                }
                c.commit();
                cart.clear();
                return idTrx;
            } catch (SQLException e) {
                c.rollback();
                s.setAttribute("flash", e.getMessage());
                return 0;
            }
        }
    }
}
