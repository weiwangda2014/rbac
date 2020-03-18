<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>日志管理</title>
    <JavaScripts>
        <script type="text/javascript">
            function page(n, s) {
                $("#pageNo").val(n);
                $("#pageSize").val(s);
                $("#searchForm").submit();
                return false;
            }

            function empty() {
                top.layer.confirm('确认要清空数据吗?', {icon: 3, title: '系统提示'}, function (index) {
                    var layerIndex = top.layer.load(0, {shade: [0.5, '#393D49']});
                    $.post("${ctx}/sys/log/empty", function (res) {
                        if (res.type == "success") {
                            top.layer.close(layerIndex);
                            top.layer.alert("清空成功", {icon: 0, title: '警告'});
                            top.getActiveTab()[0].contentDocument.forms[0].submit();
                        }
                    }, "json");
                });
            }

        </script>

        <script type="text/javascript">
            $(function () {
                layui.laydate.render({
                    type: 'datetime',
                    elem: '#beginDate',
                    format: 'yyyy-MM-dd HH:mm:ss'
                });
                layui.laydate.render({
                    type: 'datetime',
                    elem: '#endDate',
                    format: 'yyyy-MM-dd HH:mm:ss'
                });
            });
        </script>
    </JavaScripts>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>日志列表 </h5>
        </div>

        <div class="ibox-content">
            <sys:message content="${message}"/>

            <!-- 查询条件 -->
            <div class="row">
                <div class="col-sm-12">
                    <form:form id="searchForm" action="${ctx}/sys/log/" method="post" class="form-inline">
                        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
                        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                        <table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}"
                                          callback="sortOrRefresh();"/><!-- 支持排序 -->
                        <div class="form-group">
                            <label for="title">操作菜单：</label>
                            <input id="title" name="title" type="text" maxlength="50" class="form-control input-sm"
                                   value="${log.title}"/>
                            <label for="createBy.id">用户ID：</label>
                            <input id="createBy.id" name="createBy.id" type="text" maxlength="50"
                                   class="form-control input-sm" value="${log.createBy.id}"/>
                            <label for="requestUri">URI：</label>
                            <input id="requestUri" name="requestUri" type="text" maxlength="50"
                                   class="form-control input-sm" value="${log.requestUri}"/>
                            <label for="beginDate">日期范围：&nbsp;</label>
                            <input id="beginDate" name="beginDate" type="text" maxlength="20"
                                   class="form-control input-sm"
                                   value="<fmt:formatDate value="${log.beginDate}" pattern="yyyy-MM-dd"/>"/>
                            <label for="endDate">&nbsp;--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                            <input id="endDate"
                                   name="endDate"
                                   type="text"
                                   maxlength="20"
                                   class="form-control input-sm"
                                   value="<fmt:formatDate value="${log.endDate}" pattern="yyyy-MM-dd"/>"/>&nbsp;&nbsp;
                            &nbsp;<label for="exception">
                            <input id="exception" name="exception" class="i-checks"
                                   type="checkbox"${log.exception eq '1'?' checked':''}
                                   value="1"/>只查询异常信息</label>
                        </div>
                    </form:form>
                </div>
            </div>


            <!-- 工具栏 -->
            <div class="row">
                <div class="col-sm-12">
                    <div class="pull-left">
                        <shiro:hasPermission name="sys:log:del">
                            <table:delRow url="${ctx}/sys/log/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
                            <button class="btn btn-sm " onclick="empty()"
                                    title="清空"><i class="fa fa-trash"></i>清空
                            </button>
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
            </div>

            <table id="contentTable"
                    class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th><input type="checkbox" class="i-checks"></th>
                    <th>操作菜单</th>
                    <th>操作用户</th>
                    <th>所在公司</th>
                    <th>所在部门</th>
                    <th>URI</th>
                    <th>提交方式</th>
                    <th>操作者IP</th>
                    <th>操作时间</th>
                </thead>
                <tbody><%
                    request.setAttribute("strEnter", "\n");
                    request.setAttribute("strTab", "\t");
                %>
                <c:forEach items="${page.list}" var="log">
                    <tr>
                        <td><input type="checkbox" id="${log.id}" class="i-checks"></td>
                        <td>${log.title}</td>
                        <td>${log.createBy.name}</td>
                        <td>${log.createBy.company.name}</td>
                        <td>${log.createBy.office.name}</td>
                        <td><strong>${log.requestUri}</strong></td>
                        <td>${log.method}</td>
                        <td>${log.remoteAddr}</td>
                        <td><fmt:formatDate value="${log.createDate}" type="both"/></td>
                    </tr>
                    <c:if test="${not empty log.exception}">
                        <tr>
                            <td colspan="8" style="word-wrap:break-word;word-break:break-all;">
                                    <%-- 					用户代理: ${log.userAgent}<br/> --%>
                                    <%-- 					提交参数: ${fns:escapeHtml(log.params)} <br/> --%>
                                异常信息: <br/>
                                    ${fn:replace(fn:replace(fns:escapeHtml(log.exception), strEnter, '<br/>'), strTab, '&nbsp; &nbsp; ')}
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>

            <!-- 分页代码 -->
            <table:page page="${page}"></table:page>
        </div>
    </div>
</div>
</body>
</html>