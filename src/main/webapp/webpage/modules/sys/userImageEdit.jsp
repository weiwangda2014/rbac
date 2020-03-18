<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html>
<head>
    <title>个人信息</title>
    <Styles>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/cropper@4.1.0/dist/cropper.min.css">
    </Styles>
    <JavaScripts>
        <script src="https://cdn.jsdelivr.net/npm/cropper@4.1.0/dist/cropper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/jquery-cropper@1.0.1/dist/jquery-cropper.min.js"></script>
        <script type="text/javascript" src="${ctxStatic}/js/plugins/jquery-webcam/jquery.webcam.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function () {
                var $image = $("#image")
                $($image).cropper({
                    aspectRatio: 1,
                    preview: ".img-preview",
                    done: function (data) {

                    }
                });

                var $inputImage = $("#inputImage");
                if (window.FileReader) {
                    $inputImage.change(function () {
                        var fileReader = new FileReader(),
                            files = this.files,
                            file;
                        if (!files.length) {
                            return;
                        }
                        file = files[0];

                        if (/^image\/\w+$/.test(file.type)) {
                            fileReader.readAsDataURL(file);
                            fileReader.onload = function () {
                                $inputImage.val("");
                                $image.cropper("reset", true).cropper("replace", this.result);
                            };
                        } else {
                            $.message("warn", "文件格式错误");
                        }
                    });
                } else {
                    $inputImage.addClass("hide");
                }

                $("#download").click(function () {
                    window.open($image.cropper("getDataURL"));
                });

                $("#zoomIn").click(function () {
                    $image.cropper("zoom", 0.1);
                });

                $("#zoomOut").click(function () {
                    $image.cropper("zoom", -0.1);
                });

                $("#rotateLeft").click(function () {
                    $image.cropper("rotate", 45);
                });

                $("#rotateRight").click(function () {
                    $image.cropper("rotate", -45);
                });

                $("#setDrag").click(function () {
                    $image.cropper("setDragMode", "crop");
                    sendPhoto();
                });
            });
            var sendPhoto = function () {
                // 得到PNG格式的dataURL
                var photo = $('#image').cropper('getCroppedCanvas', {
                    width: 300,
                    height: 300
                }).toDataURL('image/png');
                $.ajax({
                    url: '${ctx}/sys/user/uploadPhotoByBASE64', // 要上传的地址
                    type: 'post',
                    data: {
                        type: "data",
                        'image': photo
                    },
                    dataType: 'json',
                    success: function (data) {
                        $.message(data);
                    }
                });
            }
        </script>
        <script type="text/javascript">
            $(function () {
                var pos = 0, ctx = null, saveCB, image = [];
                var canvas = document.createElement("canvas");
                canvas.setAttribute('width', 320);
                canvas.setAttribute('height', 240);
                if (canvas.toDataURL) {
                    ctx = canvas.getContext("2d");
                    image = ctx.getImageData(0, 0, 320, 240);
                    saveCB = function (data) {
                        var col = data.split(";");
                        var img = image;
                        for (var i = 0; i < 320; i++) {
                            var tmp = parseInt(col[i]);
                            img.data[pos + 0] = (tmp >> 16) & 0xff;
                            img.data[pos + 1] = (tmp >> 8) & 0xff;
                            img.data[pos + 2] = tmp & 0xff;
                            img.data[pos + 3] = 0xff;
                            pos += 4;
                        }
                        if (pos >= 4 * 320 * 240) {
                            ctx.putImageData(img, 0, 0);
                            $.post("${ctx}/sys/user/uploadPhotoByBASE64",
                                {
                                    type: "data",
                                    image: canvas.toDataURL("image/png")
                                },
                                function (result) {
                                    $.message(result);
                                });
                            var base64image = document.getElementById('base64image');
                            base64image.setAttribute('src', '');
                            base64image.setAttribute('src', canvas.toDataURL("image/png"));
                            pos = 0;
                        }
                    };
                } else {
                    saveCB = function (data) {
                        var base64image = document.getElementById('base64image');
                        base64image.setAttribute('src', '');
                        base64image.setAttribute('src', data.join('|'));
                        image.push(data);
                        pos += 4 * 320;
                        if (pos >= 4 * 320 * 240) {
                            $.post("${ctx}/sys/user/uploadPhotoByBASE64",
                                {
                                    type: "pixel",
                                    image: image.join('|')
                                },
                                function (result) {
                                    $.message(result);
                                });
                            pos = 0;
                        }
                    };
                }

                $("#webcam").webcam({
                    width: 320,
                    height: 240,
                    mode: "callback",
                    swffile: "${ctxStatic}/js/plugins/jquery-webcam/jscam_canvas_only.swf",
                    onSave: saveCB,
                    onCapture: function () {
                        webcam.save();
                    },
                    debug: function (type, string) {
                        console.log(type + ": " + string);
                    }
                });
                $('#capture').on('click', function () {
                    webcam.capture();
                });
            });
        </script>
    </JavaScripts>

</head>
<body class="gray-bg">
<div class="wrapper wrapper-content">

    <div class="row">
        <div class="col-sm-12">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>富头像上传编辑器</h5>
                    <div class="ibox-tools">
                        <a class="collapse-link">
                            <i class="fa fa-chevron-up"></i>
                        </a>
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                            <i class="fa fa-wrench"></i>
                        </a>
                        <ul class="dropdown-menu dropdown-user">
                            <li><a href="#">选项1</a>
                            </li>
                            <li><a href="#">选项2</a>
                            </li>
                        </ul>
                        <a class="close-link">
                            <i class="fa fa-times"></i>
                        </a>
                    </div>
                </div>
                <div class="ibox-content">
                    <ul class="nav nav-tabs">
                        <li class="active"><a href="#1" data-toggle="tab">本地上传</a></li>
                        <li class=""><a href="#2" data-toggle="tab">视频拍照</a></li>
                        <li class=""><a href="#3" data-toggle="tab">相册选取</a></li>
                    </ul>
                    <div class="tab-content">
                        <div class="tab-pane active" id="1">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="image-crop">
                                        <c:choose>
                                            <c:when test="${user!=null&&user.photo!=null}">
                                                <img src='${user.photo}'
                                                     id="image">
                                            </c:when>
                                            <c:otherwise>
                                                <img src='${ctxStatic}/images/default.jpg'
                                                     id="image">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <h4>图片预览：</h4>
                                    <div class="img-preview img-preview-sm"></div>
                                    <h4>说明：</h4>
                                    <p>
                                        你可以选择新图片上传，然后下载裁剪后的图片
                                    </p>
                                    <div class="btn-group">
                                        <label title="上传图片" for="inputImage" class="btn btn-primary">
                                            <input type="file" accept="image/*" name="file" id="inputImage"
                                                   class="hide"> 上传新图片
                                        </label>
                                        <label title="下载图片" id="download" class="btn btn-primary">下载</label>
                                    </div>
                                    <h4>其他说明：</h4>
                                    <p>
                                        你可以使用<code>$({image}).cropper(options)</code>来配置插件
                                    </p>
                                    <div class="btn-group">
                                        <button class="btn" id="zoomIn" type="button">放大</button>
                                        <button class="btn" id="zoomOut" type="button">缩小</button>
                                        <button class="btn" id="rotateLeft" type="button">左旋转</button>
                                        <button class="btn" id="rotateRight" type="button">右旋转</button>
                                        <button class="btn btn-warning" id="setDrag" onclick="sendPhoto()"
                                                type="button">裁剪
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="tab-pane" id="2">
                            <div class="row">
                                <div class="col-md-6">
                                    <div id="webcam"></div>
                                    <canvas id="canvas" width="300" height="240"></canvas>
                                </div>
                                <div class="col-md-6">
                                    <h4>图片预览：</h4>
                                    <div class="img-preview">
                                        <img id="base64image" src='${ctxStatic}/images/default.jpg'/>
                                    </div>
                                    <h4>说明：</h4>
                                    <p>
                                        你可以选择拍照并自动上传
                                    </p>
                                    <div class="btn-group">
                                        <button id="capture" class="btn btn-primary" type="button">拍照并上传</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="tab-pane" id="3">
                            <div id="userAlbums">
                                <a href="${ctxStatic}/images/a1.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a1.jpg" alt="示例图片1"/>
                                </a>
                                <a href="${ctxStatic}/images/a2.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a2.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a3.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a3.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a4.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a4.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a5.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a5.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a6.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a6.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a7.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a7.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a8.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a8.jpg" alt="示例图片2"/>
                                </a>
                                <a href="${ctxStatic}/images/a9.jpg" class="fancybox" title="选取该照片">
                                    <img src="${ctxStatic}/images/a9.jpg" alt="示例图片2"/>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</body>
</html>