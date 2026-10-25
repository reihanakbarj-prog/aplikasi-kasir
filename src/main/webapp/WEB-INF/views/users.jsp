<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">Manajemen User</div>

<div class="grid2">
  <div class="card">
    <h3>Tambah User Baru</h3>
    <form method="post" action="${ctx}/users">
      <input type="hidden" name="action" value="tambah">
      <label>Username</label>
      <input name="username" placeholder="contoh: kasir01" required>
      <label>Nama Lengkap</label>
      <input name="nama" placeholder="contoh: Budi Santoso" required>
      <label>Password</label>
      <input name="password" type="password" placeholder="Minimal 4 karakter" required>
      <label>Role</label>
      <select name="role">
        <option value="kasir">Kasir</option>
        <option value="admin">Admin</option>
      </select>
      <button class="btn success" type="submit" style="margin-top:18px;width:100%">Tambah User</button>
    </form>
  </div>

  <div class="card">
    <div class="section-header">
      <h3>Daftar User</h3>
    </div>
    <table>
      <thead>
        <tr><th>User</th><th>Role</th><th>Aksi</th></tr>
      </thead>
      <tbody>
        <c:forEach var="u" items="${users}">
          <tr>
            <td>
              <div style="display:flex;align-items:center;gap:10px">
                <div class="avatar">${fn:toUpperCase(fn:substring(u.nama,0,1))}</div>
                <div>
                  <div style="font-weight:600"><c:out value="${u.nama}"/></div>
                  <div style="font-size:12px;color:var(--text-sub)">@<c:out value="${u.username}"/></div>
                </div>
              </div>
            </td>
            <td>
              <c:choose>
                <c:when test="${u.role == 'admin'}">
                  <span class="badge badge-warning">Admin</span>
                </c:when>
                <c:otherwise>
                  <span class="badge badge-info">Kasir</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <div style="display:flex;gap:6px">
                <button class="btn sm outline" onclick="gantiPw(${u.id},'${u.username}')">Ganti PW</button>
                <c:if test="${u.username != sessionScope.username}">
                  <form method="post" action="${ctx}/users"
                        onsubmit="return confirm('Hapus user ${u.username}?')">
                    <input type="hidden" name="action" value="hapus">
                    <input type="hidden" name="id" value="${u.id}">
                    <button class="btn sm danger" type="submit">Hapus</button>
                  </form>
                </c:if>
              </div>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty users}">
          <tr><td colspan="3" class="muted">Belum ada user</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>

<div id="modalPw" class="modal-overlay">
  <div class="modal">
    <h3>Ganti Password</h3>
    <p id="modalLabel">Masukkan password baru.</p>
    <form method="post" action="${ctx}/users" id="formGantiPw">
      <input type="hidden" name="action" value="gantipw">
      <input type="hidden" name="id" id="modalUserId">
      <label>Password Baru</label>
      <input type="password" name="newpw" id="modalNewPw" placeholder="Minimal 4 karakter" required>
      <div class="modal-actions">
        <button class="btn success" type="submit">Simpan</button>
        <button type="button" class="btn gray" onclick="tutupModal()">Batal</button>
      </div>
    </form>
  </div>
</div>

<script>
function gantiPw(id, username) {
  document.getElementById('modalUserId').value = id;
  document.getElementById('modalLabel').textContent = 'User: @' + username;
  document.getElementById('modalNewPw').value = '';
  document.getElementById('modalPw').classList.add('open');
}
function tutupModal() {
  document.getElementById('modalPw').classList.remove('open');
}
document.getElementById('modalPw').addEventListener('click', function(e) {
  if (e.target === this) tutupModal();
});
</script>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
