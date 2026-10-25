<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="struk">
  <h3 style="text-align:center;margin:0;font-size:16px">TOKO SAYA</h3>
  <p style="text-align:center;margin:4px 0 12px;font-size:12px;color:var(--text-sub)">Struk Pembayaran</p>
  <hr>
  <div style="font-size:12px;line-height:1.8">
    <div>No Struk : <b>#${t.id}</b></div>
    <div>Tanggal  : <fmt:formatDate value="${t.tanggal}" pattern="dd/MM/yyyy HH:mm"/></div>
    <div>Kasir    : <c:out value="${t.kasir}"/></div>
  </div>
  <hr>
  <c:forEach var="i" items="${t.items}">
    <div><b><c:out value="${i.nama}"/></b></div>
    <div class="line" style="color:var(--text-sub)">
      <span>${i.qty} x Rp <fmt:formatNumber value="${i.harga}" pattern="#,##0"/></span>
      <span><b>Rp <fmt:formatNumber value="${i.subtotal}" pattern="#,##0"/></b></span>
    </div>
  </c:forEach>
  <hr>
  <div class="line"><b>TOTAL</b><b>Rp <fmt:formatNumber value="${t.total}" pattern="#,##0"/></b></div>
  <div class="line"><span>Bayar</span><span>Rp <fmt:formatNumber value="${t.bayar}" pattern="#,##0"/></span></div>
  <div class="line"><span>Kembali</span><span style="color:var(--success);font-weight:700">Rp <fmt:formatNumber value="${t.kembali}" pattern="#,##0"/></span></div>
  <hr>
  <p style="text-align:center;font-size:12px;margin-top:8px">Terima kasih atas kunjungan Anda!</p>
  <div class="no-print" style="text-align:center;margin-top:16px;display:flex;gap:10px;justify-content:center">
    <button class="btn" onclick="window.print()">Cetak</button>
    <a class="btn gray" href="${ctx}/kasir">Transaksi Baru</a>
    <a class="btn outline" href="${ctx}/riwayat">Riwayat</a>
  </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
