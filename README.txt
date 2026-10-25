SISTEM KASIR WEB (Java Servlet + JSP + H2)

CARA MENJALANKAN DI NETBEANS
1. Ekstrak zip ini.
2. NetBeans > File > Open Project > pilih folder "kasir-web".
3. Klik kanan project > Properties > Run > Server: pilih Apache Tomcat 9.x
   (JANGAN Tomcat 10+, karena memakai paket javax.servlet).
4. Klik kanan project > Clean and Build (Maven mengunduh dependency, butuh internet pertama kali).
5. Klik Run. Buka http://localhost:8080/kasir-web/
6. Login: admin / admin123

CATATAN
- Database otomatis dibuat di folder: <home user>/kasirdb (tidak perlu install MySQL).
- Butuh JDK 11 atau lebih baru.
- Fitur: login, kelola produk (CRUD), kasir + keranjang, stok berkurang otomatis,
  kembalian otomatis, struk cetak, riwayat transaksi.
