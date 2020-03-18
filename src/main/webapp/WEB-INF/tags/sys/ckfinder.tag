<%@ tag language="java" trimDirectiveWhitespaces="true" pageEncoding="UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<%@ attribute name="input" type="java.lang.String" required="true" description="输入框" %>
<%@ attribute name="type" type="java.lang.String" required="true" description="files、images、flash、thumb" %>
<%@ attribute name="uploadPath" type="java.lang.String" required="true" description="打开文件管理的上传路径" %>
<%@ attribute name="selectMultiple" type="java.lang.Boolean" required="false" description="是否允许多选" %>
<%@ attribute name="readonly" type="java.lang.Boolean" required="false" description="是否查看模式" %>
<%@ attribute name="maxWidth" type="java.lang.String" required="false" description="最大宽度" %>
<%@ attribute name="maxHeight" type="java.lang.String" required="false" description="最大高度" %>
<ol>
    <img alt="image" width="64px" height="64px" class="img-circle" id="photo"
         src="${fns:getUser().photo }"/>
</ol>
<c:if test="${!readonly}"><a href="javascript:" onclick="${input}FinderOpen();"
                             class="btn btn-primary">${selectMultiple?'添加':'选择'}</a>&nbsp;<a href="javascript:" onclick="${input}DelAll();" class="btn btn-default">清除</a></c:if>
<script src="${base}/ckfinder/static/ckfinder.js"></script>
<script type="text/javascript">
    function ${input}FinderOpen() {
        selectFileWithCKFinder('${input}');
    }

    function selectFileWithCKFinder(elementId) {
        CKFinder.popup({
            language: 'zh-cn',
            chooseFiles: true,
            width: 800,
            height: 600,
            onInit: function (finder) {
                finder.on('files:choose', function (evt) {
                    var file = evt.data.files.first();
                    var output = document.getElementById(elementId);
                    output.value = file.getUrl();
                    $("#photo").attr('src', file.getUrl());
                });

                finder.on('file:choose:resizedImage', function (evt) {
                    var output = document.getElementById(elementId);
                    output.value = evt.data.resizedUrl;
                    $("#photo").attr('src', evt.data.resizedUrl);
                });

            }
        });
    }
</script>
