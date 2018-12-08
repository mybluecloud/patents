<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>专利智能分析</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <link rel="stylesheet" href="${request.contextPath}/css/base.css"/>
  <link rel="stylesheet" href="${request.contextPath}/css/style.css">
  <link href="${request.contextPath}/css/font-awesome/css/font-awesome.min.css"
        rel="stylesheet" type="text/css"/>
  <link href="https://cdn.bootcss.com/jqueryui/1.12.1/jquery-ui.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${request.contextPath}/css/jquery.fileupload.css">
  <script src="https://cdn.bootcss.com/jquery/1.7.2/jquery.min.js"></script>
</head>
<body style="background: url(${request.contextPath}/img/bg.jpg) top center no-repeat;background-size:cover">
<div class="w1200">
  <div class="head clearfix">
    <div class="logo fl"><img src="${request.contextPath}/img/logo.png"></div>
    <div class="fr photonav">
      <a>
        <img src="${request.contextPath}/img/photo.jpg">
        <em>Mr.Li</em>
      </a>
    </div>
  </div>
  <div class="m_content">
    <h3 class="title">专利智能分析</h3>
    <div class="featureBox">
      <ul class="tabNav clearfix">
        <li class="cur">关键词分析<i></i></li>
        <li>文件分析<i></i></li>
        <li>地区分析<i></i></li>
      </ul>
      <div class="features">
        <!-- start -->
        <div class="item" style="display: block;">
          <div class="searchBox">
            <input id="queryKey" name="queryKey" type="text" name="" placeholder="输入关键词分析" class="fl editText">
            <a class="fl voicebtn"><img src="${request.contextPath}/img/voice.png"></a>
            <button id="search" class="fr searchBtn">分析</button>
          </div>
        </div>
        <!-- end -->
        <!-- start -->
        <div class="item" style="display: none">
          <div class="fileBtn">
            <img src="${request.contextPath}/img/file-btn.png">上传文件
            <input id="fileupload" type="file" name="files[]" class="unfile">
            <div id="progress" class="progress">
              <div class="progress-bar progress-bar-success"></div>
            </div>
          </div>
        </div>
        <!-- end -->
        <!-- start -->
        <div class="item" style="display: none">
          <form type="get" action="${request.contextPath}/analyze">
            <div class="fileBtn">
              <select class="fl selectText" name="province" id="province"  onchange="getAreaInfo(this,1)">
                <option value="0">请选择省</option>
                <#if provinces?exists>
                  <#list provinces as province>
                  <option value="${province.id}">${province.extName}</option>
                  </#list>
                </#if>
              </select>
              <select class="fl selectText" name="city" id="city" onchange="getAreaInfo(this,2)">
                <option value="0">请选择市</option>
                <#if cities?exists>
                  <#list cities as city>
                  <option value="${city.id}">${city.extName}</option>
                  </#list>
                </#if>
              </select>
              <select class="fl selectText" name="county" id="county">
                <option value="0">请选择区</option>
                <#if counties?exists>
                  <#list counties as county>
                  <option value="${county.id}">${county.extName}</option>
                  </#list>
                </#if>
              </select>
              <button id="analyze" class="fr searchBtn" type="submit">分析</button>
            </div>
          </form>
        </div>
        <!-- end -->
      </div>
      <div class="results">
        <h3>分析历史</h3>
        <ul class="resultList" id="history">
        <#if histories?exists>
          <#list histories as history>
            <#if history.status == 0>
          <li class="overflow">
            <div class="fl blue-color">${history.name}</div>
            <em class="fr">分析中...</em>
          </li>
            </#if>
            <#if history.status == 1>
          <li class="overflow">
            <a href="${request.contextPath}/result?id=${history.id}" class="fl blue-color"
               target="_blank">${history.name}</a>
            <button onclick="del(${history.id})" class="fr" style="border-style: none;background: transparent;"><i
                class="fa fa-close" style="color:#f00;"></i></button>
          </li>

            </#if>
            <#if history.status == 2>
          <li class="overflow">
            <div class="fl">${history.name}</div>
            <em class="fr">解析失败，请检查文件
              <button onclick="del(${history.id})" style="border-style: none;background: transparent;"><i class="fa fa-close" style="color:#f00;"></i>
              </button>
            </em>

          </li>
            </#if>
          </#list>
        </#if>
        </ul>
      </div>
    </div>
  </div>
  <!-- 底部 -->
  <div class="foot">
    <p>北京资境科技有限公司</p>
  </div>
  <!-- 底部 end-->
</div>
<script src="https://cdn.bootcss.com/jqueryui/1.12.1/jquery-ui.min.js"></script>
<script src="${request.contextPath}/js/jquery.iframe-transport.js"></script>
<script src="${request.contextPath}/js/jquery.fileupload.js"></script>
<script src="${request.contextPath}/js/jquery.fileupload-process.js"></script>
<script src="${request.contextPath}/js/jquery.fileupload-validate.js"></script>
<script src="${request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript">
  $(".tabNav li").hover(function () {
    var i = $(this).index();

    $(this).addClass('cur').siblings().removeClass('cur');
    $('.features').find(".item").eq(i).show().siblings().hide();
  });

  var http_request = "${request.contextPath}";

  $('#search').click(function () {
    var key = $('#queryKey').val().trim();
    if (key.length == 0) {
      return;
    }
    $.blockUI({message: '<h3><img src="' + http_request + '/img/busy.gif" />    分析中，请稍等……</h3>'});
    $.ajax({
      url: http_request + "/search",
      data: {
        "queryKey": key
      }, success: function (data) {
        window.location.href = http_request + "/result?id=" + data.id;
      }
    });
  })

  // Change this to the location of your server-side upload handler:
  $('#fileupload').fileupload({
    url: http_request + '/searchFile',
    dataType: 'json',
    acceptFileTypes: /(\.|\/)(txt|doc?x|pdf)$/i,
    done: function (e, data) {
      $.unblockUI();
      console.info(data.result);
      if (data.result.status == 0) {
        window.location.href = http_request + "/result?id=" + data.result.id;
      } else {
        alert("解析文件内容失败，请检查文件编码及内容");
      }

    },
    messages: {
      maxFileSize: 'File exceeds maximum allowed size of 99MB',
      acceptFileTypes: 'File type not allowed'
    },
    progressall: function (e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      $('#progress .progress-bar').css(
          'width',
          progress + '%'
      );

      if (progress == 100) {
        $.blockUI({message: '<h3><img src="' + http_request + '/img/busy.gif" />    分析中，请稍等……</h3>'});

      }
    }
  })
  .on('fileuploadfail', function (e, data) {
    console.info(data);
  })
  .prop('disabled', !$.support.fileInput)
  .parent().addClass($.support.fileInput ? undefined : 'disabled');

  function del(id) {
    $.ajax({
      url: http_request + "/delRecord",
      data: {
        "id": id
      }, success: function (data) {
        window.location.reload();
      }
    });
  }

  function getAreaInfo(t,deep) {

    $.ajax({
      url: http_request + "/areaInfo",
      data: {
        "pid": $(t).val()
      }, success: function (data) {

        $('#county').empty();
        $('#county').append('<option value="0">请选择区</option>');
        if (deep == 1) {
          $('#city').empty();
          $('#city').append('<option value="0">请选择市</option>');

          for (var i in data.areas) {
            $('#city').append('<option value="'+data.areas[i].id+'">'+data.areas[i].extName+'</option>');
          }

        } else if (deep == 2) {
          for (var i in data.areas) {
            $('#county').append('<option value="'+data.areas[i].id+'">'+data.areas[i].extName+'</option>');
          }

        }

      }
    });
  }



</script>
</body>
</html>

