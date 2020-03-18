<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>用户管理</title>
    <JavaScripts>
        <%@include file="/webpage/include/treeview.jsp" %>
        <script type="text/javascript">
            function refresh() {//刷新

                window.location = "${ctx}/sys/user/index";
            }
        </script>

        <script type="text/javascript">
            var setting = {
                data: {simpleData: {enable: true, idKey: "id", pIdKey: "pId", rootPId: '0'}},
                callback: {
                    onClick: function (event, treeId, treeNode) {
                        var id = treeNode.id == '0' ? '' : treeNode.id;
                        $('#officeContent').attr("src", "${ctx}/sys/user/list?office.id=" + id + "&office.name=" + treeNode.name);
                    }
                }
            };

            function refreshTree() {
                $.getJSON("${ctx}/sys/office/treeData", function (data) {
                    $.fn.zTree.init($("#ztree"), setting, data).expandAll(true);
                });
            }

            refreshTree();

            var leftWidth = 180; // 左侧窗口大小
            var htmlObj = $("html"), mainObj = $("#main");
            var frameObj = $("#left, #openClose, #right, #right iframe");

            function wSize() {
                var strs = getWindowSize().toString().split(",");
                htmlObj.css({"overflow-x": "hidden", "overflow-y": "hidden"});
                mainObj.css("width", "auto");
                frameObj.height(strs[0] - 120);
                var leftWidth = ($("#left").width() < 0 ? 0 : $("#left").width());
                $(".ztree").width(leftWidth - 10).height(frameObj.height() - 46);

            }
            window.onload = function(){
                var ifm= document.getElementById("officeContent");

                ifm.height=document.documentElement.clientHeight;
            }
        </script>
        <script src="${ctxStatic}/js/wsize.min.js" type="text/javascript"></script>
    </JavaScripts>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content">
    <div class="ibox float-e-margins">

        <div id="content" class="row">
            <div id="left" class="col-xs-2 col-sm-2 col-md-2">
                <a onclick="refresh()" class="pull-right">
                    <i class="fa fa-refresh"></i>
                </a>
                <div id="ztree" class="ztree leftBox-content"></div>
            </div>
            <div id="right" class="col-xs-10 col-sm-10 col-md-10">
                <iframe id="officeContent" name="officeContent" src="${ctx}/sys/user/list" width="100%" height="91%"
                        frameborder="0" style="position: relative;"></iframe>
            </div>
        </div>

    </div>
</div>
</body>
</html>