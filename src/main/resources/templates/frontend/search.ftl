<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>主页面</title>
  <link href="${rc.contextPath}/webjars/bootstrap/4.5.0/css/bootstrap.css" rel="stylesheet"/>
  <script src="${rc.contextPath}/webjars/jquery/3.3.1/jquery.js"></script>
  <script src="${rc.contextPath}/webjars/bootstrap/4.5.0/js/bootstrap.bundle.min.js"></script>
  <script src="${rc.contextPath}/js/common.js"></script>
</head>
<body>
<div class="container">
  <div class="row mt-5">
    <div class="col-sm-12">
      <form id="searchForm">
        <div class="form-row">
          <div class="form-group col-sm-2">
            <select class="form-control" name="searchField">
              <#if searchRequest.searchField?? && searchRequest.searchField == "cont">
                <option value="fileName">文档名</option>
                <option value="cont" selected>全文</option>
                <option value="writer">作者</option>
                <option value="date">日期</option>
                <option value="affiliation">隶属关系</option>
                <option value="address">地址</option>
                <option value="title">标题</option>
              <#else>
                  <option value="fileName">文档名</option>
                  <option value="cont">全文</option>
                  <option value="writer">作者</option>
                  <option value="date">日期</option>
                  <option value="affiliation">隶属关系</option>
                  <option value="address">地址</option>
                  <option value="title" selected>标题</option>
                </#if>
            </select>
            <div class="invalid-feedback"></div>
          </div>
          <div class="form-group col-sm-8">
            <input type="text" name="searchWord" class="form-control"
                   value="${(searchRequest.searchWord)!""}"
                  placeholder="请输入检索内容"/>
            <div class="invalid-feedback"></div>
          </div>
          <div class="form-group col-sm-2">
            <input type="button" onclick="doSearch()" class="btn btn-primary btn-block" value="检索"/>
          </div>
        </div>
      </form>
    </div>
  </div>
  <!--条目展示-->
  <div id="searchResult"></div>
  <div class="row d-none" id="noResultTip">
    <div class="col-sm-12">
      <div class="alert alert-danger text-center">
        没有查询结果
      </div>
    </div>
  </div>
</div>
<script>
  function doSearch() {
    // 检验必填:不对整个form加was-validated,不然select会有个校验通过的对号,故
    // 改为单独针对input进行校验
    var $searchWordInput = $("#searchForm input[name=searchWord]");
    var searchWord = $searchWordInput.val();
    if (!searchWord) {
      $searchWordInput.addClass("is-invalid");
      return;
    }else {
      $searchWordInput.removeClass("is-invalid");
    }
    if (!$("#noResultTip").hasClass("d-none")) {
      $("#noResultTip").addClass("d-none");
    }
    $.ajax({
      type:"post",
      url:'${rc.contextPath}/search',
      contentType:"application/json",
      data:JSON.stringify(formDataObj("#searchForm")),
      success:function (result) {
        if (result.success) {
          var vos = result.data;
          showItems(vos);
        }else {
          $("#tipCont").text(result.errMsg);
          $("#tipModal").modal("show");
        }
      }
    });
  }
  function showItems(vos) {
    var resultHtml = '';
    var len = vos.length;
    if (len > 0) {
      for (var i = 0;i < len;i++) {
        var oneItem = vos[i];
        resultHtml += ' <div class="row mt-3">\n' +
          '    <div class="col-sm-12">\n' +
          '      <a href="${rc.contextPath}/'+oneItem["storeRelativePath"]+'" download="'+oneItem["originalFileName"]+'">'+oneItem["originalFileName"]+'</a>\n' +
          '    </div>\n' +
          '    <div class="col-sm-12">'+oneItem["summary"]+'</div>\n' +
          '  </div>';
      }
    }else{
      // 显示没有搜索结果
      $("#noResultTip").removeClass("d-none");
    }

    $("#searchResult").html(resultHtml);
  }
  // 进入本页面后就执行
  doSearch();
</script>
</body>
</html>