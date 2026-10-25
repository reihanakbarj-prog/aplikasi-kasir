<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">Transaksi Kasir</div>

<div class="grid2">
  <section class="card">
    <div class="section-header">
      <h3>Daftar Produk</h3>
    </div>
    <form method="get" action="${ctx}/kasir" class="filter-bar" style="margin-bottom:14px">
      <input name="q" value="<c:out value='${q}'/>" placeholder="Cari nama / kode produk...">
      <button class="btn" type="submit">Cari</button>
      <c:if test="${not empty q}">
        <a href="${ctx}/kasir" class="btn gray sm">Reset</a>
      </c:if>
    </form>
    <table>
      <thead>
        <tr><th>Kode</th><th>Nama</th><th class="r">Harga</th><th class="r">Stok</th><th>Qty</th><th></th></tr>
      </thead>
      <tbody>
        <c:forEach var="p" items="${produk}">
          <tr>
            <td><span class="badge badge-gray"><c:out value="${p.kode}"/></span></td>
            <td>
              <b><c:out value="${p.nama}"/></b><br>
              <span style="font-size:11px;color:var(--text-sub)"><c:out value="${p.kategori}"/></span>
            </td>
            <td class="r">Rp <fmt:formatNumber value="${p.harga}" pattern="#,##0"/></td>
            <td class="r">
              <c:choose>
                <c:when test="${p.stok le 0}"><span class="badge badge-danger">Habis</span></c:when>
                <c:when test="${p.stok le 5}"><span class="badge badge-warning warn">${p.stok}</span></c:when>
                <c:otherwise><span class="badge badge-success">${p.stok}</span></c:otherwise>
              </c:choose>
            </td>
            <td colspan="2">
              <form method="post" action="${ctx}/kasir" style="display:flex;gap:6px;align-items:center">
                <input type="hidden" name="action" value="tambah">
                <input type="hidden" name="id" value="${p.id}">
                <input class="qty" type="number" name="qty" value="1" min="1" max="${p.stok}" style="width:60px">
                <button class="btn sm success" type="submit" ${p.stok le 0 ? 'disabled' : ''}>+ Tambah</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty produk}">
          <tr><td colspan="6" class="muted">Produk tidak ditemukan</td></tr>
        </c:if>
      </tbody>
    </table>
  </section>

  <section class="card">
    <h3>Keranjang Belanja</h3>
    <table>
      <thead>
        <tr><th>Item</th><th class="r">Harga</th><th class="r">Qty</th><th class="r">Subtotal</th><th></th></tr>
      </thead>
      <tbody>
        <c:forEach var="i" items="${items}">
          <tr>
            <td><b><c:out value="${i.nama}"/></b></td>
            <td class="r">Rp <fmt:formatNumber value="${i.harga}" pattern="#,##0"/></td>
            <td class="r">${i.qty}</td>
            <td class="r"><b>Rp <fmt:formatNumber value="${i.subtotal}" pattern="#,##0"/></b></td>
            <td>
              <form method="post" action="${ctx}/kasir">
                <input type="hidden" name="action" value="hapus">
                <input type="hidden" name="id" value="${i.produkId}">
                <button class="btn sm danger" type="submit">X</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty items}">
          <tr><td colspan="5" class="muted">Keranjang masih kosong</td></tr>
        </c:if>
      </tbody>
    </table>

    <div class="total">Total: Rp <span id="total" data-v="${total}"><fmt:formatNumber value="${total}" pattern="#,##0"/></span></div>

    <form method="post" action="${ctx}/kasir">
      <input type="hidden" name="action" value="bayar">
      <label>Uang Bayar (Rp)</label>
      <input type="number" id="bayar" name="bayar" min="0" placeholder="Masukkan nominal..." required>
      <div class="kembali">Kembalian: Rp <b id="kembali">0</b></div>
      <button class="btn big" type="submit" style="margin-top:12px">Bayar &amp; Cetak Struk</button>
    </form>

    <form method="post" action="${ctx}/kasir" style="margin-top:10px"
          onsubmit="return confirm('Kosongkan semua keranjang?')">
      <input type="hidden" name="action" value="kosongkan">
      <button class="btn danger" type="submit" style="width:100%">Kosongkan Keranjang</button>
    </form>
  </section>
</div>

<script>
var total = Number(document.getElementById('total').dataset.v) || 0;
document.getElementById('bayar').addEventListener('input', function () {
  var k = (Number(this.value) || 0) - total;
  document.getElementById('kembali').textContent = (k < 0 ? 0 : k).toLocaleString('id-ID');
});
</script>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
