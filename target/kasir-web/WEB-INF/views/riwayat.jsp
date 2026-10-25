<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">Riwayat Transaksi</div>

<div class="card" style="margin-bottom:16px">
  <form method="get" action="${ctx}/riwayat" class="filter-bar" style="margin:0">
    <span class="filter-label">Filter:</span>
    <input type="date" name="dari" value="${dari}" style="flex:none;width:160px">
    <span class="filter-label">s/d</span>
    <input type="date" name="sampai" value="${sampai}" style="flex:none;width:160px">
    <button class="btn" type="submit">Tampilkan</button>
    <c:if test="${not empty dari}">
      <a href="${ctx}/riwayat" class="btn gray">Reset</a>
    </c:if>
  </form>
</div>

<div class="card">
  <div class="section-header">
    <h3>
      <c:choose>
        <c:when test="${not empty dari}">Transaksi: ${dari} s/d ${sampai}</c:when>
        <c:otherwise>200 Transaksi Terakhir</c:otherwise>
      </c:choose>
    </h3>
    <div style="text-align:right">
      <div style="font-size:12px;color:var(--text-sub)">Total Penjualan</div>
      <div style="font-size:20px;font-weight:800;color:var(--yellow)">
        Rp <fmt:formatNumber value="${sum}" pattern="#,##0"/>
      </div>
    </div>
  </div>
  <table>
    <thead>
      <tr>
        <th>No</th><th>Tanggal</th><th>Kasir</th>
        <th class="r">Total</th><th class="r">Bayar</th><th class="r">Kembali</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="t" items="${list}">
        <tr>
          <td><a href="${ctx}/struk?id=${t.id}" style="color:var(--yellow);font-weight:600">#${t.id}</a></td>
          <td><fmt:formatDate value="${t.tanggal}" pattern="dd/MM/yyyy HH:mm"/></td>
          <td><c:out value="${t.kasir}"/></td>
          <td class="r"><b>Rp <fmt:formatNumber value="${t.total}" pattern="#,##0"/></b></td>
          <td class="r">Rp <fmt:formatNumber value="${t.bayar}" pattern="#,##0"/></td>
          <td class="r" style="color:var(--success)">Rp <fmt:formatNumber value="${t.kembali}" pattern="#,##0"/></td>
          <td><a class="btn sm outline" href="${ctx}/struk?id=${t.id}">Struk</a></td>
        </tr>
      </c:forEach>
      <c:if test="${empty list}">
        <tr><td colspan="7" class="muted">Belum ada transaksi pada periode ini</td></tr>
      </c:if>
    </tbody>
  </table>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
