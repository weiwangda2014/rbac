<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">
    <title>${fns:getConfig('productName')}</title>
    <meta name="keywords" content="${fns:getConfig('productName')}">
    <meta name="description" content="${fns:getConfig('productName')}">
    <%@include file="/webpage/include/styles.jsp" %>
</head>

<body class="fixed-sidebar full-height-layout gray-bg" style="overflow:hidden">
<div id="wrapper">
    <!--左侧导航开始-->
    <nav class="navbar-default navbar-static-side" role="navigation">
        <div class="nav-close"><i class="fa fa-times-circle"></i>
        </div>
        <div class="sidebar-collapse">
            <ul class="nav" id="side-menu">
                <li class="nav-header">
                    <div class="dropdown profile-element">
                        <span><img alt="image" width="64px" height="64px" class="img-circle"
                                   src="${fns:getUser().photo }"/></span>
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                                <span class="clear">
                                    <span class="block m-t-xs"><strong class="font-bold">${fns:getUser().name}</strong></span>
                                    <span class="text-muted text-xs block">${fns:getUser().roleNames}<b
                                            class="caret"></b></span>
                                </span>
                        </a>
                        <ul class="dropdown-menu animated fadeInRight m-t-xs">
                            <li><a class="J_menuItem" href="${ctx}/sys/user/imageEdit">修改头像</a>
                            </li>
                            <li><a class="J_menuItem" href="${ctx }/sys/user/info">个人资料</a>
                            </li>
                            <li><a class="J_menuItem" href="${ctx }/iim/contact/index">联系我们</a>
                            </li>
                            <li><a class="J_menuItem" href="${ctx }/iim/mailBox/list">信箱</a>
                            </li>
                            <li class="divider"></li>
                            <li><a href="${ctx}/logout">安全退出</a>
                            </li>
                        </ul>
                    </div>
                    <div class="logo-element">${fns:getConfig('productLogo')}</div>
                </li>
                <t:menu menu="${fns:getTopMenu()}"></t:menu>
            </ul>
        </div>
    </nav>
    <!--左侧导航结束-->
    <!--右侧部分开始-->
    <div id="page-wrapper" class="gray-bg">
        <div class="row border-bottom">
            <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header"><a class="navbar-minimalize minimalize-styl-2 btn btn-primary "
                                              href="#"><i class="fa fa-bars"></i> </a>
                    <form role="search" class="navbar-form-custom" method="post"
                          action="#">
                        <div class="form-group">
                            <input type="text" placeholder="请输入您需要查找的内容 …" class="form-control" name="top-search"
                                   id="top-search">
                        </div>
                    </form>
                </div>
                <ul class="nav navbar-top-links navbar-right">
                    <li class="dropdown">
                        <a class="dropdown-toggle count-info" data-toggle="dropdown" href="#">
                            <i class="fa fa-envelope"></i> <span class="label label-warning">${noReadCount}</span>
                        </a>
                        <ul class="dropdown-menu dropdown-messages">
                            <c:forEach items="${mailPage.list}" var="mailBox">
                                <li class="m-t-xs">
                                    <div class="dropdown-messages-box">

                                        <a href="#"
                                           onclick='top.openTab("${ctx}/iim/contact/index?name=${mailBox.sender.name }","通讯录", false)'
                                           class="pull-left">
                                            <img alt="image" class="img-circle" src="${mailBox.sender.photo }">
                                        </a>
                                        <div class="media-body">
                                            <small class="pull-right">${fns:getTime(mailBox.sendtime)}前</small>
                                            <strong>${mailBox.sender.name }</strong>
                                            <a class="J_menuItem"
                                               href="${ctx}/iim/mailBox/detail?id=${mailBox.id}"> ${fns:abbr(mailBox.mail.title,50)}</a>
                                            <br>
                                            <a class="J_menuItem" href="${ctx}/iim/mailBox/detail?id=${mailBox.id}">
                                                    ${mailBox.mail.overview}
                                            </a>
                                            <br>
                                            <small class="text-muted">
                                                <fmt:formatDate value="${mailBox.sendtime}"
                                                                pattern="yyyy-MM-dd HH:mm:ss"/></small>
                                        </div>
                                    </div>
                                </li>
                                <li class="divider"></li>
                            </c:forEach>
                            <li>
                                <div class="text-center link-block">
                                    <a class="J_menuItem" href="${ctx}/iim/mailBox/list?orderBy=sendtime desc">
                                        <i class="fa fa-envelope"></i> <strong> 查看所有邮件</strong>
                                    </a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown">
                        <a class="dropdown-toggle count-info" data-toggle="dropdown" href="#">
                            <i class="fa fa-bell"></i> <span class="label label-primary">${count }</span>
                        </a>
                        <ul class="dropdown-menu dropdown-alerts">
                            <li>

                                <c:forEach items="${page.list}" var="oaNotify">

                                    <div>
                                        <a class="J_menuItem" href="${ctx}/oa/oaNotify/view?id=${oaNotify.id}&">
                                            <i class="fa fa-envelope fa-fw"></i> ${fns:abbr(oaNotify.title,50)}
                                        </a>
                                        <span class="pull-right text-muted small">${fns:getTime(oaNotify.updateDate)}前</span>
                                    </div>

                                </c:forEach>

                            </li>
                            <li class="divider"></li>
                            <li>
                                <div class="text-center link-block">
                                    您有${count }条未读消息 <a class="J_menuItem" href="${ctx }/oa/oaNotify/self ">
                                    <strong>查看所有 </strong>
                                    <i class="fa fa-angle-right"></i>
                                </a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown hidden-xs">
                        <a class="right-sidebar-toggle" aria-expanded="false">
                            <i class="fa fa-tasks"></i> 主题
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="row content-tabs">
            <button class="roll-nav roll-left J_tabLeft"><i class="fa fa-backward"></i>
            </button>
            <nav class="page-tabs J_menuTabs">
                <div class="page-tabs-content">
                    <a href="javascript:;" class="active J_menuTab" data-id="index_v1.html">首页</a>
                </div>
            </nav>
            <button class="roll-nav roll-right J_tabRight"><i class="fa fa-forward"></i>
            </button>
            <div class="btn-group roll-nav roll-right">
                <button class="dropdown J_tabClose" data-toggle="dropdown">关闭操作<span class="caret"></span>

                </button>
                <ul role="menu" class="dropdown-menu dropdown-menu-right">
                    <li class="J_tabShowActive"><a>定位当前选项卡</a>
                    </li>
                    <li class="divider"></li>
                    <li class="J_tabCloseAll"><a>关闭全部选项卡</a>
                    </li>
                    <li class="J_tabCloseOther"><a>关闭其他选项卡</a>
                    </li>
                </ul>
            </div>
            <a href="${ctx}/logout" class="roll-nav roll-right J_tabExit"><i class="fa fa fa-sign-out"></i> 退出</a>
        </div>
        <div class="row J_mainContent" id="content-main">
            <iframe class="J_iframe" name="iframe0" width="100%" height="100%" src="${ctx}/home" frameborder="0"
                    data-id="index_v1.html" seamless></iframe>
        </div>
        <div class="footer">
            <div class="pull-right">&copy; ${fns:getConfig('copyrightYear')} <a href="${fns:getConfig('productSite')}"
                                                                                target="_blank">${fns:getConfig('productName')}</a>
            </div>
        </div>
    </div>
    <!--右侧部分结束-->
    <!--右侧边栏开始-->
    <div id="right-sidebar">
        <div class="sidebar-container">

            <ul class="nav nav-tabs navs-3">

                <li class="active">
                    <a data-toggle="tab" href="#tab-1">
                        <i class="fa fa-gear"></i> 主题
                    </a>
                </li>
            </ul>

            <div class="tab-content">
                <div id="tab-1" class="tab-pane active">
                    <div class="sidebar-title">
                        <h3><i class="fa fa-comments-o"></i> 主题设置</h3>
                        <small><i class="fa fa-tim"></i> 你可以从这里选择和预览主题的布局和样式，这些设置会被保存在本地，下次打开的时候会直接应用这些设置。</small>
                    </div>
                    <div class="skin-setttings">
                        <div class="title">主题设置</div>
                        <div class="setings-item">
                            <span>收起左侧菜单</span>
                            <div class="switch">
                                <div class="onoffswitch">
                                    <input type="checkbox" name="collapsemenu" class="onoffswitch-checkbox"
                                           id="collapsemenu">
                                    <label class="onoffswitch-label" for="collapsemenu">
                                        <span class="onoffswitch-inner"></span>
                                        <span class="onoffswitch-switch"></span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="setings-item">
                            <span>固定顶部</span>

                            <div class="switch">
                                <div class="onoffswitch">
                                    <input type="checkbox" name="fixednavbar" class="onoffswitch-checkbox"
                                           id="fixednavbar">
                                    <label class="onoffswitch-label" for="fixednavbar">
                                        <span class="onoffswitch-inner"></span>
                                        <span class="onoffswitch-switch"></span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="setings-item">
                                <span>
                                    固定宽度
                                </span>

                            <div class="switch">
                                <div class="onoffswitch">
                                    <input type="checkbox" name="boxedlayout" class="onoffswitch-checkbox"
                                           id="boxedlayout">
                                    <label class="onoffswitch-label" for="boxedlayout">
                                        <span class="onoffswitch-inner"></span>
                                        <span class="onoffswitch-switch"></span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="title">皮肤选择</div>
                        <div class="setings-item default-skin nb" id="skin-2">
                            <span class="skin-name">默认皮肤</span>
                        </div>
                        <div class="setings-item blue-skin nb" id="skin-1">
                            <span class="skin-name">蓝色主题</span>
                        </div>
                        <div class="setings-item yellow-skin nb" id="skin-3">
                            <span class="skin-name"> 黄色/紫色主题</span>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
    <!--右侧边栏结束-->
</div>
<%@include file="/webpage/include/scripts.jsp" %>

</body>
</html>