<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>专利智能分析</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <link rel="stylesheet" href="${request.contextPath}/css/base.css" />
  <link rel="stylesheet" href="${request.contextPath}/css/style.css">
  <link rel="stylesheet" href="${request.contextPath}/js/amcharts/plugins/export/export.css" type="text/css" media="all" />
  <script src="https://cdn.bootcss.com/jquery/1.7.2/jquery.min.js"></script>

</head>
<style>
  .chart {
    width		: 75%;
    height		: 600px;
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


<!-- chart -->
<div class="focus_box">
  <div class="overflow focustop" style="padding:10px 5px 15px 120px;">
    <h3 class="f1">申请人指数图</h3>
  </div>
</div>
<div id="chart_pa" class="chart" ></div>
<div class="focus_box">
  <div class="overflow focustop" style="padding:10px 5px 15px 120px;">
    <h3 class="f1">分类号（大类）指数图</h3>
  </div>
</div>
<div id="chart_ic12" class="chart" ></div>
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
<script>
  var http_request = "${request.contextPath}";

  var pa,ic12;
  <#if pa??>
    pa = ${pa}.pa_ana;
  </#if>

  <#if ic12??>
   ic12 = ${ic12}.ic1_ana;
  </#if>
  console.info(pa);
  AmCharts.makeChart( "chart_pa", {
    "type": "serial",
    "theme": "light",
    "dataProvider": pa,
    "valueAxes": [ {
      "gridColor": "#FFFFFF",
      "gridAlpha": 0.2,
      "dashLength": 0
    } ],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [ {
      "balloonText": "[[category]]: <b>[[value]]</b>",
      "fillAlphas": 0.8,
      "lineAlpha": 0.2,
      "type": "column",
      "valueField": "count"
    } ],
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
      "labelRotation":30
    },
    "export": {
      "enabled": true
    }

  } );

  AmCharts.makeChart( "chart_ic12", {
    "type": "serial",
    "theme": "light",
    "dataProvider": ic12,
    "valueAxes": [ {
      "gridColor": "#FFFFFF",
      "gridAlpha": 0.2,
      "dashLength": 0
    } ],
    "gridAboveGraphs": true,
    "startDuration": 1,
    "graphs": [ {
      "balloonText": "[[category]]: <b>[[value]]</b>",
      "fillAlphas": 0.8,
      "lineAlpha": 0.2,
      "type": "column",
      "valueField": "count"
    } ],
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
      "labelRotation":30
    },
    "export": {
      "enabled": true
    }

  } );




</script>
</body>
</html>

