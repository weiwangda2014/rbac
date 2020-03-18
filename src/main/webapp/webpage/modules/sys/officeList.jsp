<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>机构管理</title>
    <JavaScripts>
        <%@include file="/webpage/include/treetable.jsp" %>
        <script type="text/javascript">
            $(document).ready(function () {
                var tpl = $("#treeTableTpl").html();
                var data = ${fns:toJson(list)}, rootId = "${not empty office.id ? office.id : '0'}";
                addRow("#treeTableList", tpl, data, rootId, true);
                $("#treeTable").treetable({
                    expandable: true,
                    onNodeInitialized: nodeInitialized,
                    onInitialized: initialized
                });
            });

            function nodeInitialized() {
                $('input[type="checkbox"].i-checks, input[type="radio"].i-checks').iCheck({
                    handle: 'checkbox',
                    checkboxClass: 'icheckbox_minimal-blue',
                    radioClass: 'iradio_minimal-blue'
                });
            }

            function initialized() {
                $('input[type="checkbox"].i-checks, input[type="radio"].i-checks').iCheck({
                    handle: 'checkbox',
                    checkboxClass: 'icheckbox_minimal-blue',
                    radioClass: 'iradio_minimal-blue'
                });
            }

            function addRow(list, tpl, data, pid, root) {
                for (var i = 0; i < data.length; i++) {
                    var row = data[i];
                    if ((${fns:jsGetVal('row.parentId')}) == pid) {
                        $(list).append(Mustache.render(tpl, {
                            dict: {
                                type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type)
                            }, pid: (root ? 0 : pid), row: row
                        }));
                        addRow(list, tpl, data, row.id);
                    }
                }
            }

            function refresh() {//刷新或者排序，页码不清零
                window.location = "${ctx}/sys/office/list";
            }
        </script>
    </JavaScripts>
</head>
<body>
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-content"> <!-- 查询条件 -->
            <div class="row">
                <div class="col-sm-12">
                    <form:form id="searchForm" modelAttribute="office" action="${ctx}/sys/office/list" method="post"
                               class="form-inline">
                        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
                        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                        <table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}"
                                          callback="sortOrRefresh();"/><!-- 支持排序 -->
                        <div class="form-group">

                            <label class="control-label" for="name">机构名称：</label>
                            <form:input path="name" htmlEscape="false" maxlength="50"
                                        class=" form-control input-sm"/>
                            <label class="control-label" for="type">机构类别：</label>
                            <form:select path="type" class="form-control">
                                <form:options items="${fns:getDictList('sys_office_type')}" itemLabel="label" itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                        <div class="form-group">

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
                        <shiro:hasPermission name="sys:office:add">
                            <table:addRow url="${ctx}/sys/office/form?parent.id=${office.id}" title="机构"
                                          target="officeContent"></table:addRow><!-- 增加按钮 -->
                        </shiro:hasPermission>

                        <shiro:hasPermission name="sys:office:edit">
                            <table:editRow url="${ctx}/sys/office/form" id="treeTable" title="机构"
                                           target="officeContent"></table:editRow><!-- 编辑按钮 -->
                        </shiro:hasPermission>

                        <shiro:hasPermission name="sys:office:del">
                            <table:delRow url="${ctx}/sys/office/deleteAll" id="treeTable"></table:delRow><!-- 删除按钮 -->
                        </shiro:hasPermission>
                        <button class="btn btn-sm " data-toggle="tooltip" data-placement="left" onclick="refresh()"
                                title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新
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
                    <table id="treeTable"
                           class="table table-bordered table-hover">
                        <thead>
                        <tr>
                            <th><input type="checkbox" class="i-checks"></th>
                            <th>机构名称</th>
                            <th>归属区域</th>
                            <th>机构编码</th>
                            <th>机构类型</th>
                            <th>备注</th>
                            <shiro:hasPermission name="sys:office:edit">
                                <th>操作</th>
                            </shiro:hasPermission>
                        </tr>
                        </thead>
                        <tbody id="treeTableList"></tbody>
                    </table>
                </div>
                <table:page page="${page}"></table:page>
            </div>
        </div>
    </div>
</div>
<script type="text/template" id="treeTableTpl">
    <tr data-tt-id="{{row.id}}" data-tt-parent-id="{{pid}}">
        <td><input type="checkbox" id="{{row.id}}" class="i-checks"></td>
        <td><a href="#" onclick="openDialogView('查看机构', '${ctx}/sys/office/form?id={{row.id}}','800px', '620px')">{{row.name}}</a>
        </td>
        <td>{{row.area.name}}</td>
        <td>{{row.code}}</td>
        <td>{{dict.type}}</td>
        <td>{{row.remarks}}</td>
        <td>
            <shiro:hasPermission name="sys:office:view">
                <a href="#" onclick="openDialogView('查看机构', '${ctx}/sys/office/form?id={{row.id}}','800px', '620px')"
                   class="btn btn-info btn-xs"><i class="fa fa-search-plus"></i> 查看</a>
            </shiro:hasPermission>
            <shiro:hasPermission name="sys:office:edit">
                <a href="#"
                   onclick="openDialog('修改机构', '${ctx}/sys/office/form?id={{row.id}}','800px', '620px', 'officeContent')"
                   class="btn btn-success btn-xs"><i class="fa fa-edit"></i> 修改</a>
            </shiro:hasPermission>
            <shiro:hasPermission name="sys:office:del">
                <a onclick="doDelete('{{row.id}}')"
                   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
            </shiro:hasPermission>
            <shiro:hasPermission name="sys:office:add">
                <a href="#"
                   onclick="openDialog('添加下级机构', '${ctx}/sys/office/form?parent.id={{row.id}}','800px', '620px', 'officeContent')"
                   class="btn  btn-primary btn-xs"><i class="fa fa-plus"></i> 添加下级机构</a>
            </shiro:hasPermission>
        </td>
    </tr>
</script>
</body>
</html>