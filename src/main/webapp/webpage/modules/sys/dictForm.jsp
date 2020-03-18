<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>字典管理</title>
    <JavaScripts>
        <script type="text/javascript">
            $(document).ready(function () {
                $("#value").focus();
            });
        </script>
    </JavaScripts>
</head>
<body class="hideScroll">
<form:form id="inputForm" modelAttribute="dict" action="${ctx}/sys/dict/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <table class="table table-bordered table-hover">
        <tbody>
        <tr>
            <td class="width-15 active"><label class="pull-right">键值:</label></td>
            <td class="width-35">
                <form:input path="value" htmlEscape="false" maxlength="50" class="form-control"/>
            </td>
            <td class="width-15 active"><label class="pull-right">标签:</label></td>
            <td class="width-35">
                <form:input path="label" htmlEscape="false" maxlength="50" class="form-control required"/>
            </td>
        </tr>
        <tr>
            <td class="width-15 active"><label class="pull-right">类型:</label></td>
            <td class="width-35"><form:input path="type" htmlEscape="false" maxlength="50"
                    class="form-control required abc"/></td>
            <td class="width-15 active"><label class="pull-right">描述:</label></td>
            <td class="width-35"><form:input path="description" htmlEscape="false" maxlength="50"
                    class="form-control required"/></td>
        </tr>
        <tr>
            <td class="width-15 active"><label class="pull-right">排序:</label></td>
            <td class="width-35"><form:input path="sort" htmlEscape="false" maxlength="11"
                    class="form-control required digits"/></td>
            <td class="width-15 active"><label class="pull-right">备注:</label></td>
            <td class="width-35"><form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200"
                    class="form-control "/></td>
        </tr>
        </tbody>
    </table>
</form:form>
</body>
</html>