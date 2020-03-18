<%@ tag language="java" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<%@ attribute name="id" type="java.lang.String" required="true" %>
<%@ attribute name="url" type="java.lang.String" required="true" %>
<%@ attribute name="label" type="java.lang.String" required="false" %>
<%@ attribute name="methodName" type="java.lang.String" required="true" %>
<button class="btn btn-sm" onclick="${methodName}()" data-toggle="tooltip" data-placement="top"><i
        class="fa fa-trash-o"> ${label==null?'删除':label}</i>
</button>
<batchUpdateRow>
    <%-- 使用方法： 1.将本tag写在查询的form之前；2.传入table的id和controller的url --%>
    <script type="text/javascript">
        function ${methodName}() {
            top.layer.confirm('确认要${label}数据吗?', {icon: 3, title: '系统提示'}, function (index) {
                var layerIndex = top.layer.load(0, {shade: [0.5, '#393D49']});
                $.post("${url}", function (res) {
                    if (res.type == "success") {
                        top.layer.close(layerIndex);
                        top.layer.alert("${label}成功", {icon: 0, title: '警告'});
                        top.getActiveTab()[0].contentDocument.forms[0].submit();
                    }
                }, "json");
            });
        }
    </script>
</batchUpdateRow>