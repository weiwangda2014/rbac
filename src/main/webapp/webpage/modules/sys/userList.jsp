<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>用户管理</title>
</head>
<body>
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-content"> <!-- 查询条件 -->
            <div class="row">
                <div class="col-sm-12">
                    <form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/list" method="post"
                            class="form-inline">
                        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
                        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                        <table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}"
                                callback="sortOrRefresh();"/><!-- 支持排序 -->
                        <div class="form-group">
                            <label class="control-label" for="company">归属公司：</label>
                            <sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name"
                                    labelValue="${user.company.name}"
                                    title="公司" url="/sys/office/treeData?type=1" cssClass=" form-control input-sm"
                                    allowClear="true"/>
                            <label class="control-label" for="loginName">登录名：</label>
                            <form:input path="loginName" htmlEscape="false" maxlength="50" class=" form-control input-sm"/>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="office">归属部门：</label>
                            <sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name"
                                    labelValue="${user.office.name}"
                                    title="部门" url="/sys/office/treeData?type=2" cssClass=" form-control input-sm"
                                    allowClear="true" notAllowSelectParent="true"/>
                            <label class="control-label" for="name">姓&nbsp;&nbsp;&nbsp;名：</label>
                            <form:input path="name" htmlEscape="false" maxlength="50" class=" form-control input-sm"/>

                        </div>
                    </form:form>
                </div>
            </div>
        </div>
        <div class="ibox-content">
            <!-- 工具栏 -->
            <div class="row">
                <div class="col-sm-12">
                    <div class="pull-left">
                        <shiro:hasPermission name="sys:user:add">
                            <table:addRow url="${ctx}/sys/user/form" title="用户"
                                    target="officeContent"></table:addRow><!-- 增加按钮 -->
                        </shiro:hasPermission>
                        <shiro:hasPermission name="sys:user:edit">
                            <table:editRow url="${ctx}/sys/user/form" id="contentTable" title="用户"
                                    target="officeContent"></table:editRow><!-- 编辑按钮 -->
                        </shiro:hasPermission>
                        <shiro:hasPermission name="sys:user:del">
                            <table:delRow url="${ctx}/sys/user/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
                        </shiro:hasPermission>
                        <shiro:hasPermission name="sys:user:import">
                            <table:importExcel url="${ctx}/sys/user/import"></table:importExcel><!-- 导入按钮 -->
                        </shiro:hasPermission>
                        <shiro:hasPermission name="sys:user:export">
                            <table:exportExcel url="${ctx}/sys/user/export"></table:exportExcel><!-- 导出按钮 -->
                        </shiro:hasPermission>
                        <button class="btn btn-sm " data-toggle="tooltip" data-placement="left"
                                onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新
                        </button>
                    </div>
                    <div class="pull-right">
                        <button class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="search()"><i
                                class="fa fa-search"></i> 查询
                        </button>
                        <button class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="reset()"><i
                                class="fa fa-refresh"></i> 重置
                        </button>
                    </div>
                </div>
                <div class="col-sm-12" style="overflow: auto; width: 100%;">
                    <table id="contentTable" class="table table-bordered table-hover">
                        <thead>
                        <tr>
                            <th><input type="checkbox" class="i-checks"></th>
                            <th class="sort-column login_name">登录名</th>
                            <th class="sort-column name">姓名</th>
                            <th class="sort-column phone">电话</th>
                            <th class="sort-column c.name">归属公司</th>
                            <th class="sort-column o.name">归属部门</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${page.list}" var="user">
                            <tr>
                                <td><input type="checkbox" id="${user.id}" class="i-checks"></td>
                                <td>${user.loginName}
                                </td>
                                <td>${user.name}</td>
                                <td>${user.phone}</td>
                                <td>${user.company.name}</td>
                                <td>${user.office.name}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <table:page page="${page}"></table:page>
            </div>
        </div>
    </div>
</div>
</body>
</html>