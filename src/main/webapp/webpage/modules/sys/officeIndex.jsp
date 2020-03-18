<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>机构管理</title>
    <JavaScripts>
        <%@include file="/webpage/include/treeview.jsp" %>
        <script type="text/javascript">
            var setting = {
                data: {simpleData: {enable: true, idKey: "id", pIdKey: "pId", rootPId: '0'}},
                callback: {
                    onClick: function (event, treeId, treeNode) {
                        var id = treeNode.pId == '0' ? '' : treeNode.pId;
                        $('#officeContent').attr("src", "${ctx}/sys/office/list?id=" + id + "&parentIds=" + treeNode.pIds);
                    }
                }
            };

            function refreshTree() {
                $.getJSON("${ctx}/sys/office/treeData", function (data) {
                    $.fn.zTree.init($("#ztree"), setting, data).expandAll(true);
                });
            }
            function refresh() {//刷新
                window.location = "${ctx}/sys/office/";
            }
            refreshTree();
        </script>
    </JavaScripts>
</head>
<body class="gray-bg">

<div class="wrapper wrapper-content">
    <div class="ibox float-e-margins">
        <div class="ibox-content">
            <div id="content d-flex justify-content-between" class="row">
                <div class="col-3 col-md-2" style=" overflow:auto;">
                    <a onclick="refresh()" class="pull-right">
                        <i class="fa fa-refresh"></i>
                    </a>
                    <div id="ztree" class="ztree"></div>
                </div>
                <div class="col-9 col-md-10">
                    <iframe id="officeContent" name="officeContent" src="${ctx}/sys/office/list?id=&parentIds="
                            width="100%" height="600" frameborder="0"></iframe>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>