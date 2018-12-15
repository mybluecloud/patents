<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>专利智能分析</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <link rel="stylesheet" href="${request.contextPath}/css/base.css"/>
  <link rel="stylesheet" href="${request.contextPath}/css/style.css">
  <link rel="stylesheet" href="${request.contextPath}/js/amcharts/plugins/export/export.css" type="text/css" media="all"/>
  <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css"
        integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
  <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.slim.min.js"
          integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
          crossorigin="anonymous"></script>
  <script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js"
          integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
          crossorigin="anonymous"></script>
</head>
<style>
  .chart {
    width: 75%;
    height: 600px;
    padding: 30px 0;
    margin: 0 auto;
  }
</style>
<body>
<div class="w1200 m_head overflow">
  <div class="fl"><img src="${request.contextPath}/img/logo1.png"></div>
  <div class="fr photonav">
    <a>
      <img src="${request.contextPath}/img/photo.jpg">
      <em>Mr.Li</em>
    </a>
  </div>
</div>

<div class="m_desc">
  <div class="w1200 overflow">
    <#if query?exists>
      <h3 class="fl">${query}</h3>
    </#if>
  </div>
</div>

<div class="w1200 mBox">
  <div class="focus_box">
    <table class="table table-striped table-bordered">
      <thead>
      <tr>
        <th scope="col">申请号</th>
        <th scope="col">专利名称</th>
        <th scope="col">公开号</th>
        <th scope="col">公开日</th>
        <th scope="col">申请日</th>
        <th scope="col">申请(专利权)人</th>
        <th scope="col">发明人</th>
      </tr>
      </thead>
      <tbody>
  <#if content.cubePatentSearchResponse.documents??>
    <#list content.cubePatentSearchResponse.documents as document>
    <tr>
      <#if document.field_values.an??>
      <th scope="row">${document.field_values.an}</th>
      </#if>
      <#if document.field_values.ti??>
      <td>${document.field_values.ti}</td>
      </#if>
      <#if document.field_values.pn??>
      <td>${document.field_values.pn}</td>
      </#if>
      <#if document.field_values.pd??>
      <td>${document.field_values.pd}</td>
      </#if>
      <#if document.field_values.ad??>
      <td>${document.field_values.ad}</td>
      </#if>
      <#if document.field_values.pa??>
      <td>
        <#list document.field_values.pa as sqr>
          <a href="${request.contextPath}/inventor?name=${sqr}" class="fl blue-color"
             target="_blank">${sqr}</a>&nbsp;
        </#list>
      </td>
      </#if>
      <#if document.field_values.in??>
      <td>
        <#list document.field_values.in as fmr>
          <a href="${request.contextPath}/inventor?name=${fmr}" class="fl blue-color"
             target="_blank">${fmr}</a>&nbsp;
        </#list>
      </td>
      </#if>
    </tr>
    </#list>
  </#if>

      </tbody>
    </table>
  </div>
</div>
<!-- chart -->
<div class="focus_box">
  <div class="overflow focustop" style="padding:10px 5px 15px 120px;">
    <h3 class="f1">申请人指数图</h3>
  </div>
</div>
<div id="chart_pa" class="chart"></div>
<div class="focus_box">
  <div class="overflow focustop" style="padding:10px 5px 15px 120px;">
    <h3 class="f1">分类号（大类）指数图</h3>
  </div>
</div>
<div id="chart_ic12" class="chart"></div>
<div class="focus_box">
  <div class="overflow focustop" style="padding:10px 5px 15px 120px;">
    <h3 class="f1">申请日指数图</h3>
  </div>
</div>
<div id="chart_ad" class="chart"></div>
<!-- chart end -->
<!-- 底部 -->
<div class="foot pd40">
  <p class="em3">北京资境科技有限公司</p>
</div>
<!-- 底部 end-->
<script src="${request.contextPath}/js/amcharts/amcharts.js" type="text/javascript"></script>
<script src="${request.contextPath}/js/amcharts/serial.js" type="text/javascript"></script>
<script src="${request.contextPath}/js/amcharts/plugins/export/export.min.js" type="text/javascript"></script>
<script src="${request.contextPath}/js/amcharts/themes/light.js"></script>
<script src="${request.contextPath}/js/dict.js"></script>
<script>
  var http_request = "${request.contextPath}";

  var pa, ic12, ad;
  <#if pa??>
    pa = ${pa}.pa_ana;
  </#if>
  <#if ad??>
    ad = ${ad}.ad_ana;
  </#if>

  <#if ic12??>
   ic12 = ${ic12}.ic1_ana;
  </#if>
  for (var i in ic12) {
    ic12[i].alias = IC12_DICT[ic12[i].name];
  }

  AmCharts.makeChart("chart_pa", {
    "type": "serial",
    "theme": "light",
    "dataProvider": pa,
    "valueAxes": [{
      "gridColor": "#FFFFFF",
      "gridAlpha": 0.2,
      "dashLength": 0
    }],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [{
      "balloonText": "[[category]]: <b>[[value]]</b>",
      "fillAlphas": 0.8,
      "lineAlpha": 0.2,
      "type": "column",
      "valueField": "count"
    }],
    "chartCursor": {
      "categoryBalloonEnabled": false,
      "cursorAlpha": 0,
      "zoomable": false
    },
    "categoryField": "name",
    "categoryAxis": {
      "gridPosition": "start",
      "gridAlpha": 0,
      "tickPosition": "start",
      "tickLength": 20,
      "labelRotation": 30
    },
    "export": {
      "enabled": true
    }

  });

  AmCharts.makeChart("chart_ic12", {
    "type": "serial",
    "theme": "light",
    "dataProvider": ic12,
    "valueAxes": [{
      "gridColor": "#FFFFFF",
      "gridAlpha": 0.2,
      "dashLength": 0
    }],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [{
      "balloonText": "[[category]]: <b>[[value]]</b>",
      "fillAlphas": 0.8,
      "lineAlpha": 0.2,
      "type": "column",
      "valueField": "count"
    }],
    "chartCursor": {
      "categoryBalloonEnabled": false,
      "cursorAlpha": 0,
      "zoomable": false
    },
    "categoryField": "alias",
    "categoryAxis": {
      "gridPosition": "start",
      "gridAlpha": 0,
      "tickPosition": "start",
      "tickLength": 20,
      "labelRotation": 30
    },
    "export": {
      "enabled": true
    }

  });

  AmCharts.makeChart("chart_ad", {
    "type": "serial",
    "theme": "light",
    "dataProvider": ad,
    "valueAxes": [{
      "gridColor": "#FFFFFF",
      "gridAlpha": 0.2,
      "dashLength": 0
    }],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [{
      "balloonText": "[[category]]: <b>[[value]]</b>",
      "fillAlphas": 0.8,
      "lineAlpha": 0.2,
      "type": "column",
      "valueField": "count"
    }],
    "chartCursor": {
      "categoryBalloonEnabled": false,
      "cursorAlpha": 0,
      "zoomable": false
    },
    "categoryField": "name",
    "categoryAxis": {
      "gridPosition": "start",
      "gridAlpha": 0,
      "tickPosition": "start",
      "tickLength": 0,
      "labelRotation": 0
    },
    "export": {
      "enabled": true
    }

  });


</script>
</body>
</html>

