<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>区域管理</title>
    <JavaScripts>
        <%@include file="/webpage/include/treetable.jsp" %>
        <script type="text/javascript">
            $(document).ready(function () {
                var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, "");
                var data = ${fns:toJson(page)}, rootId = "0";
                addRow("#treeTableList", tpl, data, rootId, true);

                $("#treeTable").treetable({
                    expandable: true,
                    onNodeInitialized: nodeInitialized,
                    onInitialized: initialized,
                    onNodeExpand: nodeExpand,
                    onNodeCollapse: nodeCollapse
                });

                $("#treeTable tbody").on("mousedown", "tr", function () {
                    $(".selected").not(this).removeClass("selected");
                    $(this).toggleClass("selected");
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

            function nodeExpand() {
                getNodeViaAjax(this.id);
            }


            function nodeCollapse() {

            }

            function getNodeViaAjax(parentNodeID) {
                $.ajax({
                    type: 'POST',
                    url: '${ctx}/sys/area/data/',
                    data: {
                        parentid: parentNodeID
                    },
                    success: function (data) {
                        var childNodes = data;
                        if (childNodes) {
                            var parentNode = $("#treeTable").treetable("node", parentNodeID);
                            for (var i = 0; i < childNodes.length; i++) {
                                var node = childNodes[i];
                                var nodeToAdd = $("#treeTable").treetable("node", node.id);
                                // check if node already exists. If not add row to parent node
                                if (!nodeToAdd) {
                                    var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, "");
                                    var row = Mustache.render(tpl, {
                                        dict: {
                                            type: getDictLabel(${fns:toJson(fns:getDictList('sys_area_type'))}, node.type)
                                        }, pid: parentNodeID, row: node
                                    })
                                    $("#treeTable").treetable("loadBranch", parentNode, row);
                                }
                            }
                        }
                    },
                    error: function (error) {
                    },
                    dataType: 'json'
                });
            }


            function addRow(list, tpl, data, pid, root) {
                for (var i = 0; i < data.length; i++) {
                    var row = data[i];
                    if ((${fns:jsGetVal('row.parentId')}) == pid) {
                        $(list).append(Mustache.render(tpl, {
                            dict: {
                                type: getDictLabel(${fns:toJson(fns:getDictList('sys_area_type'))}, row.type)
                            }, pid: (root ? 0 : pid), row: row
                        }));

                        addRow(list, tpl, data, row.id);
                    }
                }
            }

            function refresh() {//刷新

                window.location = "${ctx}/sys/area/";
            }
        </script>
    </JavaScripts>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>区域列表 </h5>
        </div>

        <div class="ibox-content">
            <sys:message content="${message}"/>


            <!-- 工具栏 -->
            <div class="row">
                <div class="col-sm-12">
                    <div class="pull-left">
                        <shiro:hasPermission name="sys:area:add">
                            <table:addRow url="${ctx}/sys/area/form" title="区域"></table:addRow><!-- 增加按钮 -->
                        </shiro:hasPermission>
                        <shiro:hasPermission name="sys:user:edit">
                            <table:editRow url="${ctx}/sys/area/form" id="treeTable" title="区域"
                                           target="officeContent"></table:editRow><!-- 编辑按钮 -->
                        </shiro:hasPermission>
                        <shiro:hasPermission name="sys:area:del">
                            <table:delRow url="${ctx}/sys/area/deleteAll" id="treeTable"></table:delRow><!-- 删除按钮 -->
                        </shiro:hasPermission>
                        <button class="btn btn-sm " data-toggle="tooltip" data-placement="left"
                                onclick="refresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新
                        </button>

                    </div>
                </div>
            </div>

            <table id="treeTable"
                   class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th><input type="checkbox" class="i-checks"></th>
                    <th>区域名称</th>
                    <th>区域编码</th>
                    <th>区域类型</th>
                    <th>备注</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="treeTableList">

                </tbody>
            </table>
            <br/>
        </div>
    </div>
</div>
<script type="text/template" id="treeTableTpl">
    <tr data-tt-id="{{row.id}}" data-tt-parent-id="{{pid}}"
        data-tt-branch={{#row.hasChildren}}'true'{{/row.hasChildren}} {{^row.hasChildren}}'false'{{/row.hasChildren}}>
        <td><input type="checkbox" id="{{row.id}}" class="i-checks"></td>
        <td>{{row.name}}
        </td>
        <td>{{row.code}}</td>
        <td>{{dict.type}}</td>
        <td>{{row.remarks}}</td>
        <td>
            <shiro:hasPermission name="sys:area:view">
                <a href="#" onclick="openDialogView('查看区域', '${ctx}/sys/area/form?id={{row.id}}','800px', '500px')"
                   class="btn btn-info btn-xs"><i class="fa fa-search-plus"></i> 查看</a>
            </shiro:hasPermission>
            <shiro:hasPermission name="sys:area:edit">
                <a href="#" onclick="openDialog('修改区域', '${ctx}/sys/area/form?id={{row.id}}','800px', '500px')"
                   class="btn btn-success btn-xs"><i class="fa fa-edit"></i> 修改</a>
            </shiro:hasPermission>
            <shiro:hasPermission name="sys:area:del">
                <a onclick="doDelete('{{row.id}}')"
                   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
            </shiro:hasPermission>
            <shiro:hasPermission name="sys:area:add">
                <a href="#" onclick="openDialog('添加下级区域', '${ctx}/sys/area/form?parent.id={{row.id}}','800px', '500px')"
                   class="btn btn-primary btn-xs"><i class="fa fa-plus"></i> 添加下级区域</a>
            </shiro:hasPermission>
        </td>
    </tr>
</script>
</body>
</html>