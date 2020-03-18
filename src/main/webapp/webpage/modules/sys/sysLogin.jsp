<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${fns:getConfig('productName')} 登录</title>
    <meta name="keywords" content="${fns:getConfig('productKeywords')}">
    <meta name="description" content="${fns:getConfig('productDescription')}">
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.4.1/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/font-awesome@4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/animate.css@3.7.2/animate.min.css">
    <link href="${ctxStatic}/css/style.min.css?v=4.1.0" rel="stylesheet">
    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->
</head>

<body class="gray-bg">


<div class="middle-box text-center loginscreen  animated fadeInDown">
    <div>
        <div>

            <h1 class="logo-name">${fns:getConfig('productLogo')}</h1>

        </div>
        <h3>欢迎使用 ${fns:getConfig('productLogo')}</h3>

        <form class="form-horizontal" role="form" action="${ctx}/login" method="post">
            <div class="form-group">
                <label for="username" class="col-sm-2 control-label">用户名</label>
                <div class="col-sm-10">
                    <input type="text" id="username" name="username"
                           class="form-control required"
                           placeholder="用户名"/>
                </div>
            </div>
            <div class="form-group">
                <label for="password" class="col-sm-2 control-label">密码</label>
                <div class="col-sm-10">
                    <input type="password" id="password" name="password"
                           class="form-control required"
                           placeholder="密码"/>
                </div>
            </div>
            <c:if test="${isValidateCodeLogin}">
                <div class="form-group">
                    <label class="col-sm-2 control-label" for="validateCode">验证码</label>
                    <div class="col-sm-10">
                        <sys:validateCode name="validateCode" inputCssStyle=""/>
                    </div>
                </div>
            </c:if>
            <div class="checkbox m-l m-r-xs">
                <label for="rememberMe" class="i-checks">
                    <input type="checkbox" id="rememberMe"
                           name="rememberMe" ${rememberMe ? 'checked' : ''}><i></i> 记住我</label>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-primary block full-width m-b">登 录</button>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- 全局js -->
<script src="https://cdn.jsdelivr.net/npm/jquery@2.2.4/dist/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@3.4.1/dist/js/bootstrap.min.js"></script>
<script>
    if (window.top !== window.self) {
        window.top.location = window.location;
    }
</script>
</body>
</html>
