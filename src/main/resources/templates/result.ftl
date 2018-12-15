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
  #chartdiv {
    width		: 50%;
    height		: 600px;
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
    <#if history?exists>
      <h3 class="fl">${history.queryKey}</h3>
      <a id="export" class="fr exportBtn">导出</a>
      <input type="hidden" id="queryID" value="${history.id}">
    </#if>

  </div>
</div>
<div class="w1200 mBox">

<#if patents?exists>
  <#list patents as patent>
  <div class="focus_box">
    <div class="overflow focustop">
      <h3 class="fl">${patent.publicationNumber}   ${patent.title}</h3>
      <a class="fr" target="_blank" href="${request.contextPath}${patent.pdf}"><img src="${request.contextPath}/img/file1.jpg"></a>
    </div>
    <table class="tableList" width="100%" cellpadding="0" cellspacing="0">
      
      <tr>
    <#if patent.applicationNumber??>
        <td width="50px">专利申请号</td>
        <td width="260">${patent.applicationNumber}</td>
    </#if>
      </tr>
      
      <tr>
    <#if patent.assigneeOriginal??>
        <td width="50px">申请人</td>
        <td width="260">${patent.assigneeOriginal}</td>
    </#if>
      </tr>
      <tr>
    <#if patent.assigneeCurrent??>
        <td width="50px">受让人</td>
        <td width="260">${patent.assigneeCurrent}</td>
    </#if>
      </tr>
      <tr>
    <#if patent.inventors??>
        <td width="50px">发明人</td>
        <td width="260">${patent.inventors}</td>
    </#if>
      </tr>
      <tr>
    <#if patent.priorityDate??>
        <td width="50px">优先权日期   </td>
        <td width="260">${patent.priorityDate}</td>
    </#if>
      </tr>
      <tr>
    <#if patent.filingDate??>
        <td width="50px">申请日期</td>
        <td width="260">${patent.filingDate}</td>
    </#if>
      </tr>
      <tr>
    <#if patent.citedBy??>
        <td width="50px">专利AI分析指数</td>
        <td width="260">${patent.citedBy}</td>
    </#if>
      </tr>
      <tr>
    <#if patent.claims??>
        <td width="50px">权利要求总数</td>
        <td width="260">${patent.claims}</td>
    </#if>
      </tr>
    </table>
     <#if patent.summary??>
    <p class="describes">${patent.summary}</p>
    </#if>
  </div>

  </#list>
</#if>
<div class="focus_box">
	<div class="overflow focustop">
		<h3 class="f1">专利AI分析指数图</h3>
	</div>
</div>
</div>
<!-- chart -->

<div id="chartdiv" style="padding: 30px 0;margin: 0 auto;"></div>

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
  var datas = [];
  <#if patents??>
    <#list patents as patent>
    var data = {};
    data.applicationNumber = "${patent.applicationNumber}";
    data.citedBy = parseInt("${patent.citedBy}");
    datas.push(data);
    </#list>
  </#if>

  var chart = AmCharts.makeChart( "chartdiv", {
    "type": "serial",
    "addClassNames": true,
    "theme": "light",
    "depth3D": 50,
    "angle": 50,
    // "autoMargins": false,
    // "marginLeft": 30,
    // "marginRight": 8,
    // "marginTop": 10,
    // "marginBottom": 26,
    "balloon": {
      "adjustBorderColor": false,
      "horizontalPadding": 10,
      "verticalPadding": 8,
      "color": "#ffffff"
    },

    "dataProvider": datas,
    "valueAxes": [ {
      "stackType": "3d",
      "axisAlpha": 0,
      "position": "left",
      "integersOnly":true
    } ],
    "startDuration": 1,
    "graphs": [ {
      "fillAlphas": 1,
      "lineAlpha": 1,
      "colorField": "color",
      "type": "column",
      "labelText": "[[value]]",
      "valueField": "citedBy"

    } ],
    "categoryField": "applicationNumber",
    "categoryAxis": {
      "gridPosition": "start",
      "axisAlpha": 0,
      "tickLength": 0
    },
    "export": {
      "enabled": true
    }
  } );



  $('#export').on('click', function () {

    $.ajax({
      url: http_request + "/export",
      data: {
        "id": $('#queryID').val()
      },
      dataType: 'json',
      success: function (data) {

        $('<form method="get" action="'+http_request +  data + '"></form>').appendTo('body').submit().remove();

      }
    });


  });
</script>
</body>
</html>

