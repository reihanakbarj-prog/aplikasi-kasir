<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="id">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Login - Sistem Kasir</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=4">
</head>
<body>
<div class="login-page">
  <div class="login-card">
    <div class="login-logo">K</div>
    <h1 class="login-title">Sistem Kasir</h1>
    <p class="login-subtitle">Masuk untuk mengakses dashboard</p>

    <c:if test="${not empty error}">
      <div class="flash" style="border-left-color:#ef4444;background:rgba(239,68,68,.1);color:#f87171">
        <c:out value="${error}"/>
      </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
      <label>Username</label>
      <input name="username" required autofocus placeholder="Masukkan username" autocomplete="username">
      <label style="margin-top:14px">Password</label>
      <input name="password" type="password" required placeholder="Password" autocomplete="current-password">
      <button class="btn big" type="submit" style="margin-top:24px">Masuk Sekarang</button>
    </form>
    <p class="login-hint">Default: <strong>admin</strong> / <strong>admin123</strong></p>
  </div>
</div>
</body>
</html>
