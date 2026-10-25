<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">Dashboard</div>

<div class="grid4">
  <div class="stat-card green">
    <div class="stat-icon">Rp</div>
    <div class="stat-value rp"><fmt:formatNumber value="${totalHariIni}" pattern="#,##0"/></div>
    <div class="stat-label">Penjualan Hari Ini</div>
  </div>
  <div class="stat-card blue">
    <div class="stat-icon">#</div>
    <div class="stat-value">${transaksiHariIni}</div>
    <div class="stat-label">Transaksi Hari Ini</div>
  </div>
  <div class="stat-card orange">
    <div class="stat-icon">Prd</div>
    <div class="stat-value">${jumlahProduk}</div>
    <div class="stat-label">Total Produk</div>
  </div>
  <div class="stat-card red">
    <div class="stat-icon">!</div>
    <div class="stat-value">${stokMenipis}</div>
    <div class="stat-label">Stok Menipis</div>
  </div>
</div>

<div class="grid2">
  <div class="card">
    <div class="section-header">
      <h3>Penjualan 7 Hari Terakhir</h3>
      <span class="badge badge-info">Rp <fmt:formatNumber value="${totalBulanIni}" pattern="#,##0"/> bulan ini</span>
    </div>
    <div class="chart-wrap">
      <canvas id="chartPenjualan"></canvas>
    </div>
  </div>

  <div class="card">
    <div class="section-header">
      <h3>Transaksi Terakhir</h3>
      <a href="${ctx}/riwayat" class="btn sm outline">Lihat Semua</a>
    </div>
    <table>
      <thead>
        <tr><th>#</th><th>Tanggal</th><th>Kasir</th><th class="r">Total</th></tr>
      </thead>
      <tbody>
        <c:forEach var="t" items="${recent}">
          <tr>
            <td><a href="${ctx}/struk?id=${t.id}" style="color:var(--yellow);font-weight:600">#${t.id}</a></td>
            <td><fmt:formatDate value="${t.tanggal}" pattern="dd/MM HH:mm"/></td>
            <td><c:out value="${t.kasir}"/></td>
            <td class="r"><b>Rp <fmt:formatNumber value="${t.total}" pattern="#,##0"/></b></td>
          </tr>
        </c:forEach>
        <c:if test="${empty recent}">
          <tr><td colspan="4" class="muted">Belum ada transaksi</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<div class="card">
  <h3>Akses Cepat</h3>
  <div style="display:flex;gap:12px;flex-wrap:wrap">
    <a href="${ctx}/kasir" class="btn success">Buka Kasir</a>
    <a href="${ctx}/produk" class="btn">Kelola Produk</a>
    <a href="${ctx}/riwayat" class="btn gray">Riwayat Transaksi</a>
    <c:if test="${sessionScope.role == 'admin'}">
      <a href="${ctx}/users" class="btn outline">Kelola User</a>
    </c:if>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
(function() {
  var labels = ${chartLabels};
  var data   = ${chartData};
  var ctx = document.getElementById('chartPenjualan').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Penjualan (Rp)',
        data: data,
        backgroundColor: 'rgba(245,158,11,0.75)',
        borderColor: 'rgba(245,158,11,1)',
        borderWidth: 0,
        borderRadius: 6
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: {
          beginAtZero: true,
          grid: { color: 'rgba(255,255,255,0.05)' },
          ticks: {
            color: '#6b6b6b',
            callback: function(v) {
              if (v >= 1000000) return 'Rp ' + (v/1000000).toFixed(1) + 'jt';
              if (v >= 1000) return 'Rp ' + (v/1000).toFixed(0) + 'rb';
              return 'Rp ' + v;
            },
            font: { size: 11 }
          }
        },
        x: {
          grid: { color: 'rgba(255,255,255,0.03)' },
          ticks: { color: '#6b6b6b', font: { size: 11 } }
        }
      }
    }
  });
})();
</script>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
