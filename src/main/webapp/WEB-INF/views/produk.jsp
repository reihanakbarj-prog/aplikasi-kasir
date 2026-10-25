<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">Manajemen Produk</div>

<div class="grid2">
  <div class="card">
    <h3>${empty edit ? 'Tambah Produk' : 'Edit Produk'}</h3>
    <form method="post" action="${ctx}/produk" class="row" style="flex-wrap:wrap;gap:10px">
      <input type="hidden" name="action" value="simpan">
      <input type="hidden" name="id" value="${edit.id}">
      <input name="kode" placeholder="Kode (cth: P007)" value="<c:out value='${edit.kode}'/>" required style="flex:1;min-width:110px">
      <input name="nama" placeholder="Nama produk" value="<c:out value='${edit.nama}'/>" required style="flex:2;min-width:200px">
      <input name="harga" type="number" min="0" placeholder="Harga (Rp)" value="${edit.harga}" required style="flex:1;min-width:110px">
      <input name="stok" type="number" min="0" placeholder="Stok" value="${edit.stok}" required style="flex:1;min-width:90px">
      <select name="kategori" style="flex:1;min-width:130px">
        <c:forEach var="kat" items="${kategoriList}">
          <option value="${kat}" ${edit.kategori == kat || (empty edit.kategori && kat == 'Umum') ? 'selected' : ''}>${kat}</option>
        </c:forEach>
      </select>
      <button class="btn success" type="submit">Simpan</button>
      <c:if test="${not empty edit}">
        <a class="btn gray" href="${ctx}/produk">Batal</a>
      </c:if>
    </form>
  </div>

  <div class="card">
    <h3>Filter Produk</h3>
    <form method="get" action="${ctx}/produk" class="filter-bar" style="margin:0">
      <span class="filter-label">Kategori:</span>
      <select name="kategori" onchange="this.form.submit()">
        <option value="">Semua Kategori</option>
        <c:forEach var="kat" items="${kategoriList}">
          <option value="${kat}" ${filterKategori == kat ? 'selected' : ''}>${kat}</option>
        </c:forEach>
      </select>
      <c:if test="${not empty filterKategori}">
        <a href="${ctx}/produk" class="btn sm gray">Reset</a>
      </c:if>
    </form>
  </div>
</div>

<div class="card">
  <div class="section-header">
    <h3>Daftar Produk
      <c:if test="${not empty filterKategori}">
        &mdash; <span class="badge badge-info">${filterKategori}</span>
      </c:if>
    </h3>
  </div>
  <table>
    <thead>
      <tr>
        <th>Kode</th><th>Nama</th><th>Kategori</th>
        <th class="r">Harga</th><th class="r">Stok</th><th></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="p" items="${produk}">
        <tr>
          <td><span class="badge badge-gray"><c:out value="${p.kode}"/></span></td>
          <td><b><c:out value="${p.nama}"/></b></td>
          <td><span class="badge badge-info"><c:out value="${p.kategori}"/></span></td>
          <td class="r">Rp <fmt:formatNumber value="${p.harga}" pattern="#,##0"/></td>
          <td class="r">
            <c:choose>
              <c:when test="${p.stok le 0}"><span class="badge badge-danger">Habis</span></c:when>
              <c:when test="${p.stok le 5}"><span class="badge badge-warning warn">${p.stok}</span></c:when>
              <c:otherwise><span class="badge badge-success">${p.stok}</span></c:otherwise>
            </c:choose>
          </td>
          <td>
            <div style="display:flex;gap:6px">
              <a class="btn sm outline" href="${ctx}/produk?edit=${p.id}">Edit</a>
              <form method="post" action="${ctx}/produk"
                    onsubmit="return confirm('Hapus produk ini?')">
                <input type="hidden" name="action" value="hapus">
                <input type="hidden" name="id" value="${p.id}">
                <button class="btn sm danger" type="submit">Hapus</button>
              </form>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty produk}">
        <tr><td colspan="6" class="muted">Belum ada produk</td></tr>
      </c:if>
    </tbody>
  </table>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
