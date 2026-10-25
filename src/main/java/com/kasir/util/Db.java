package com.kasir.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

/** Koneksi H2 (file di folder home user: ~/kasirdb) + pembuatan tabel otomatis. */
public class Db {
    private static final String URL =
            "jdbc:h2:" + System.getProperty("user.home").replace('\\', '/') + "/kasirdb/kasir;AUTO_SERVER=TRUE";

    static {
        try {
            Class.forName("org.h2.Driver");
            init();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    private static void init() throws SQLException {
        try (Connection c = get(); Statement st = c.createStatement()) {
            // Tabel users
            st.execute("CREATE TABLE IF NOT EXISTS users(id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(100) NOT NULL, "
                    + "nama VARCHAR(100), role VARCHAR(20) DEFAULT 'kasir')");
            // Tambah kolom role jika belum ada (migrasi)
            try { st.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'kasir'"); } catch (Exception ignored) {}

            // Tabel produk
            st.execute("CREATE TABLE IF NOT EXISTS produk(id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "kode VARCHAR(30) UNIQUE NOT NULL, nama VARCHAR(150) NOT NULL, "
                    + "harga BIGINT NOT NULL, stok INT NOT NULL, kategori VARCHAR(50) DEFAULT 'Umum')");
            // Tambah kolom kategori jika belum ada (migrasi)
            try { st.execute("ALTER TABLE produk ADD COLUMN IF NOT EXISTS kategori VARCHAR(50) DEFAULT 'Umum'"); } catch (Exception ignored) {}

            // Tabel transaksi
            st.execute("CREATE TABLE IF NOT EXISTS transaksi(id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "tanggal TIMESTAMP NOT NULL, total BIGINT, bayar BIGINT, kembali BIGINT, kasir VARCHAR(100))");

            // Tabel detail
            st.execute("CREATE TABLE IF NOT EXISTS detail(id INT AUTO_INCREMENT PRIMARY KEY, transaksi_id INT NOT NULL, "
                    + "produk_id INT, nama VARCHAR(150), harga BIGINT, qty INT, subtotal BIGINT)");

            // Seed admin user
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO users(username,password,nama,role) VALUES(?,?,?,?)")) {
                        ps.setString(1, "admin"); ps.setString(2, sha256("admin123"));
                        ps.setString(3, "Administrator"); ps.setString(4, "admin");
                        ps.executeUpdate();
                    }
                }
            }
            // Pastikan admin punya role admin (migrasi)
            try (PreparedStatement ps = c.prepareStatement("UPDATE users SET role='admin' WHERE username='admin' AND (role IS NULL OR role='kasir')")) {
                ps.executeUpdate();
            }

            // Seed produk awal
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM produk")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    Object[][] seed = {
                        {"P001", "Indomie Goreng",    3500,  100, "Makanan"},
                        {"P002", "Aqua 600ml",        4000,   80, "Minuman"},
                        {"P003", "Teh Botol Sosro",   5000,   60, "Minuman"},
                        {"P004", "Roti Tawar",       15000,   25, "Makanan"},
                        {"P005", "Gula Pasir 1kg",   17000,   40, "Sembako"},
                        {"P006", "Minyak Goreng 1L", 20000,   30, "Sembako"},
                        {"P007", "Chiki Snack",       3000,    5, "Snack"},
                        {"P008", "Kopi Sachet",       2000,   50, "Minuman"}
                    };
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO produk(kode,nama,harga,stok,kategori) VALUES(?,?,?,?,?)")) {
                        for (Object[] r : seed) {
                            ps.setString(1, (String) r[0]); ps.setString(2, (String) r[1]);
                            ps.setLong(3, ((Number) r[2]).longValue()); ps.setInt(4, ((Number) r[3]).intValue());
                            ps.setString(5, (String) r[4]);
                            ps.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    public static String sha256(String s) {
        try {
            byte[] h = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static long toLong(String s) {
        try { return Long.parseLong(s.trim().replace(".", "")); } catch (Exception e) { return 0; }
    }

    /** Daftar kategori produk yang tersedia */
    public static final String[] KATEGORI = {"Makanan", "Minuman", "Snack", "Sembako", "Kebersihan", "Lainnya", "Umum"};
}
