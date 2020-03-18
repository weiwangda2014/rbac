<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp" %>
<html lang="en">
<head>
    <JavaScripts>
        <script src="https://cdn.jsdelivr.net/npm/echarts@4.4.0/dist/echarts.min.js"></script>
        <script>
            $(function () {
                var myChart = echarts.init(document.getElementById('main'));
                var now = new Date();
                var res = [];
                var len = 20;
                while (len--) {
                    var time = now.toLocaleTimeString().replace(/^\D*/, '');
                    time = time.substr(time.indexOf(":") + 1);
                    res.unshift(time);
                    now = new Date(now - 1000);
                }
                option = {
                    legend: {
                        data: ['jvm内存使用率', '物理内存使用率', 'cpu使用率']
                    },
                    grid: {
                        x: 40,
                        y: 30,
                        x2: 10,
                        y2: 35,
                        borderWidth: 0,
                        borderColor: "#FFFFFF"
                    },
                    xAxis: [{
                        axisLabel: {
                            rotate: 40,
                        },
                        type: 'category',// 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
                        data: res
                    }],
                    yAxis: [{
                        min: 0,
                        max: 100,
                        axisLabel: {
                            formatter: '{value}%'
                        }
                    }],
                    series: [
                        {
                            name: 'jvm内存使用率',
                            type: 'line',
                            data: [12, 25, 31, 11, 45, 50, 11, 09, 28, 35, 40, 24,
                                28, 39, 23, 31, 14, 18, 08, 36]
                        },
                        {
                            name: '物理内存使用率',
                            type: 'line',
                            data: [55, 41, 10, 23, 15, 44, 29, 41, 27, 05, 12, 25,
                                31, 11, 45, 50, 11, 09, 28, 35]
                        },
                        {
                            name: 'cpu使用率',
                            type: 'line',
                            data: [40, 24, 28, 39, 23, 31, 14, 18, 08, 36, 55, 41,
                                10, 23, 15, 44, 29, 41, 27, 05]
                        }]
                };
                myChart.setOption(option);
                var main_one = echarts.init(document.getElementById('main_one'));
                var main_two = echarts.init(document.getElementById('main_two'));
                var main_three = echarts.init(document.getElementById('main_three'));
                one_option = {
                    tooltip: {
                        formatter: "{a} <br/>{b} : {c}%"
                    },
                    series: [
                        {

                            title:{
                                show : true,
                                offsetCenter: [0, "95%"],
                            },
                            pointer: {
                                color: '#FF0000'
                            },
                            name:'监控指标',
                            radius:'70%',
                            axisLine: {            // 坐标轴线
                                lineStyle: {       // 属性lineStyle控制线条样式
                                    width: 20
                                }
                            },
                            detail : {formatter:'{value}%'},
                            type:'gauge',
                            data:[{value: 50, name: 'JVM使用率'}]
                        }
                    ]
                };
                two_option = {
                    tooltip: {
                        formatter: "{a} <br/>{b} : {c}%"
                    },
                    series: [
                        {
                            name: '监控指标',
                            type: 'gauge',
                            startAngle: 180,
                            endAngle: 0,
                            center: ['50%', '90%'],    // 默认全局居中
                            radius: 180,
                            axisLine: {            // 坐标轴线
                                lineStyle: {       // 属性lineStyle控制线条样式
                                    width: 140
                                }
                            },
                            axisTick: {            // 坐标轴小标记
                                splitNumber: 10,   // 每份split细分多少段
                                length: 12,        // 属性length控制线长
                            },
                            axisLabel: {           // 坐标轴文本标签，详见axis.axisLabel

                                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                                    color: '#fff',
                                    fontSize: 15,
                                    fontWeight: 'bolder'
                                }
                            },

                            pointer: {
                                width: 10,
                                length: '80%',
                                color: 'rgba(255, 255, 255, 0.8)'
                            },
                            title: {
                                show: true,
                                offsetCenter: [0, 15],       // x, y，单位px
                                /* textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                                     color: '#fff',
                                     fontSize: 25
                                 }*/
                            },
                            detail: {
                                show: true,
                                backgroundColor: 'rgba(0,0,0,0)',
                                borderWidth: 0,
                                borderColor: '#ccc',
                                offsetCenter: [0, -40],       // x, y，单位px
                                formatter: '{value}%',
                                textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                                    fontSize: 20
                                }
                            },
                            data: [{value: 50, name: 'cpu使用率'}]
                        }
                    ]
                };

                main_one.setOption(one_option);
                main_two.setOption(two_option);
                one_option.series[0].data[0].name = '内存使用率';
                one_option.series[0].pointer.color = '#428bca'
                main_three.setOption(one_option);
                var axisData;
                clearInterval(timeTicket);
                var timeTicket = setInterval(function () {
                    axisData = (new Date()).toLocaleTimeString().replace(/^\D*/, '');
                    axisData = axisData.substr(axisData.indexOf(":") + 1);
                    var jvm = [];
                    var ram = [];
                    var cpu = [];
                    $.ajax({
                        type: "POST",
                        url: '${ctx}/monitor/usage',
                        async: false,
                        dataType: 'json',
                        success: function (json) {
                            $("#td_jvmUsage").html(json.jvmUsage);
                            $("#td_serverUsage").html(json.ramUsage);
                            $("#td_cpuUsage").html(json.cpuUsage);


                            jvm.push(json.jvmUsage);
                            ram.push(json.ramUsage);
                            cpu.push(json.cpuUsage);
                            // 动态数据接口 addData
                            myChart.appendData({
                                seriesIndex: 0,
                                data: [[0, // 系列索引
                                    jvm, // 新增数据
                                    false, // 新增数据是否从队列头部插入
                                    false, // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
                                ], [1, // 系列索引
                                    ram, // 新增数据
                                    false, // 新增数据是否从队列头部插入
                                    false, // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
                                ], [2, // 系列索引
                                    cpu, // 新增数据
                                    false, // 新增数据是否从队列头部插入
                                    false, // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
                                    axisData // 坐标轴标签
                                ]]
                            });

                            one_option.series[0].data[0].value = json.jvmUsage;
                            one_option.series[0].data[0].name = 'JVM使用率';
                            one_option.series[0].pointer.color = '#FF0000'
                            main_one.setOption(one_option, true);

                            two_option.series[0].data[0].value = json.cpuUsage;
                            main_two.setOption(two_option, true);

                            one_option.series[0].data[0].value = json.ramUsage;
                            one_option.series[0].data[0].name = '内存使用率';
                            one_option.series[0].pointer.color = '#428bca'
                            main_three.setOption(one_option, true);

                        }
                    });
                }, 3000);

            });

            function modifySer(key) {
                $.ajax({
                    async: false,
                    url: "${ctx}/monitor/modifySetting?" + key + "=" + $("#" + key).val(),
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            alert("更新成功！");
                        } else {
                            alert("更新失败！");
                        }
                    }
                });
            }
        </script>

    </JavaScripts>
</head>
<body class="" style="">
<div class="wrapper wrapper-content">
    <div class="row animated fadeInRight">
        <div class="col-sm-12">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <i class="fa fa-rss-square"></i> 实时监控
                </div>

                <div class="panel-body">
                    <table style="width: 100%;">
                        <tr>
                            <td width="33.3%">
                                <div id="main_one" style="height: 240px;"></div>
                            </td>
                            <td width="33.3%">
                                <div id="main_two" style="height: 240px;"></div>
                            </td>
                            <td width="33.3%">
                                <div id="main_three"
                                     style="height: 240px;"></div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div class="row animated fadeInRight">
        <div class="col-sm-12">
            <div class="panel panel-success">
                <div class="panel-heading">
                    <i class="fa fa-briefcase"></i> 警告设置
                </div>
                <table  class="table table-bordered table-hover"
                       width="100%" style="vertical-align: middle;">
                    <thead>
                    <tr style="background-color: #faebcc; text-align: center;">
                        <td width="100">名称</td>
                        <td width="100">参数</td>
                        <td width="205">预警设置</td>
                        <td width="375">邮箱设置</td>
                    </tr>
                    </thead>
                    <tbody id="tbody">
                    <tr>
                        <td style='padding-left: 10px; text-align: left;vertical-align: middle;'>CPU</td>
                        <td style='padding-left: 10px; text-align: left;vertical-align: middle;'>当前使用率：<span
                                id="td_cpuUsage" style="color: red;">50</span> %
                        </td>
                        <td align="center">
                            <table>
                                <tr>
                                    <td>使用率超出</td>
                                    <td><input class='inputclass' name='cpu' id='cpu'
                                               type='text' value='${cpu}'/> %,
                                    </td>
                                    <td>发送邮箱提示 <a class='btn btn-info'
                                                  href='javascript:void(0)' onclick='modifySer("cpu");'>
                                        修改 </a></td>
                                </tr>
                            </table>
                        </td>
                        <td rowspan='3' align="center" style="vertical-align: middle;"><input
                                class='inputclass' style='width: 250px; height: 32px;'
                                name='toEmail' id='toEmail' type='text'
                                value='${toEmail}'/><a class='btn btn-info'
                                                       href='javascript:void(0)' onclick='modifySer("toEmail");'>
                            修改 </a></td>
                    </tr>
                    <tr>
                        <td style='padding-left: 10px; text-align: left;vertical-align: middle;'>服务器内存</td>
                        <td style='padding-left: 10px; text-align: left;vertical-align: middle;'>当前使用率：<span
                                id="td_serverUsage" style="color: blue;">50</span> %
                        </td>
                        <td align="center">
                            <table>
                                <tr>
                                    <td>使用率超出</td>
                                    <td><input class='inputclass' name='ram' id='ram'
                                               type='text' value='${ram}'/> %,
                                    </td>
                                    <td>发送邮箱提示 <a class='btn btn-info'
                                                  href='javascript:void(0)' onclick='modifySer("ram");'>
                                        修改 </a></td>
                                </tr>
                            </table>

                        </td>
                    </tr>
                    <tr>
                        <td style='padding-left: 10px; text-align: left;vertical-align: middle;'>JVM内存</td>
                        <td style='padding-left: 10px; text-align: left;vertical-align: middle;'>当前使用率：<span
                                id="td_jvmUsage" style="color: green;">50</span> %
                        </td>
                        <td align="center">
                            <table>
                                <tr>
                                    <td>使用率超出</td>
                                    <td><input class='inputclass' name='jvm' id='jvm'
                                               type='text' value='${jvm}'/> %,
                                    </td>
                                    <td>发送邮箱提示 <a class='btn btn-info'
                                                  href='javascript:void(0)' onclick='modifySer("jvm");'>
                                        修改 </a></td>
                                </tr>
                            </table>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="row animated fadeInRight">
        <div class="col-sm-6">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <i class="fa fa-th-list"></i> 服务器信息
                </div>
                <div class="panel-body" style="padding: 0px">
                    <div style="height: 370px;" class="embed-responsive embed-responsive-16by9">
                        <iframe class="embed-responsive-item" src="${ctx}/monitor/systemInfo"></iframe>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6">
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <i class="fa fa-fire"></i> 实时监控
                </div>

                <div class="panel-body">
                    <div id="main" style="height: 370px;"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
