<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>文件列表</title>
  <link href="${rc.contextPath}/webjars/bootstrap/4.5.0/css/bootstrap.css" rel="stylesheet"/>
  <script src="${rc.contextPath}/webjars/jquery/3.3.1/jquery.js"></script>
  <script src="${rc.contextPath}/webjars/bootstrap/4.5.0/js/bootstrap.bundle.min.js"></script>
  <script src="${rc.contextPath}/js/common.js"></script>
</head>
<body>
<#include "../tip.ftl"/>
<div class="container">
  <div class="row mt-3">
    <section class="col-sm-12 mb-2">
      <div class="d-flex justify-content-start align-items-center">
        <a href="#" class="btn btn-primary mr-3" onclick="showAddDia()">上传文件</a>
        <a href="#" class="btn btn-primary mr-3" onclick="doDelete()">删除</a>
        <form class="form-inline mb-0" id="searchForm">
          <input type="hidden" name="pageNow" id="pageNow"  value="1"/>
        </form>
      </div>
    </section>
    <!--查询出的学生表格-->
    <section class="col-sm-12">
      <table class="table table-bordered table-hover table-striped">
        <thead>
        <tr>
          <th>选择</th>
          <th>系统标识</th>
          <th>原始文件名</th>
          <th>文件大小(字节)</th>
          <th>存储路径</th>
        </tr>
        </thead>
        <tbody id="tbody"></tbody>
      </table>
    </section>
    <!--分页-->
    <section class="col-sm-12">
      <ul class='pagination'></ul>
    </section>
  </div>
</div>
<!--新增文件模态框开始-->
<div class="modal fade" data-backdrop="static"
     tabindex="-1"  id="addModal">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">上传文件</h5>
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="text-danger text-center mb-3">支持pdf、xml、txt文件</div>
        <form id="addForm">
          <input type="hidden" name="originalFileNames"/>
          <input type="hidden" name="sizes"/>
          <input type="button" class="btn btn-primary btn-block" value="点击上传"
                 onclick="document.getElementById('file-input').click()"/>
          <div id="fileNameHelp" class="form-text text-muted small"></div>
          <small id="requiredTip" class="form-text text-danger"></small>
          <!--弹出上传文件框-->
          <input type="file" name="file" id="file-input"
                 oninput="filePreview()" multiple
                 class="d-none">
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" onclick="trueAddFile()" class="btn btn-primary">确认</button>
      </div>
    </div>
  </div>
</div>
<!--新增文件模态框结束-->
<script>
  // 加载可分页的表格
  function loadPageableTable() {
    $.ajax({
      type:"post",
      url:'${rc.contextPath}/manage/findByPage',
      data: formDataObj("#searchForm"),
      success:function (result) {
        if (result.success) {
          var tableResult = result.data;
          showTbodyHtml(tableResult);
          showPageHtml(tableResult);
        }else {
          $("#tipCont").text(result.errMsg);
          $("#tipModal").modal("show");
        }
      }
    });
  }
  /*
  组装tbody里面的html
   */
  function showTbodyHtml(tableResult) {
    var rows = tableResult.rows;
    var len = rows.length;
    var tbodyHtml = '';
    for (var i =0;i<len;i++) {
      var oneRow = rows[i];
      tbodyHtml += '<tr>\n' +
        '                <td>\n' +
        '                  <div class="form-check">\n' +
        '                    <input type="checkbox" class="form-check-input" name="pathsToDelete" value="'+oneRow["storeRelativePath"]+'"/>\n' +
        '                  </div>\n' +
        '                </td>\n' +
        '                <td>'+oneRow["id"]+'</td>\n' +
        '                <td>'+oneRow["originalFileName"]+'</td>\n' +
        '                <td>'+oneRow["size"]+'</td>\n' +
        '                <td><a href="${rc.contextPath}/'+oneRow["storeRelativePath"]+'" download="'+oneRow["originalFileName"]+'">'+oneRow["storeAbsPath"]+'</a></td>\n' +
        '              </tr>';
    }
    $("#tbody").html(tbodyHtml);
  }
  function showPageHtml(tableResult) {
    var pageHtml = '';
    var pageNow = parseInt($("#pageNow").val());
    var pageCount = tableResult["pageCount"];
    if (pageNow !== 1) {
      pageHtml += '<li class="page-item"><a class="page-link" href="#" onclick="goFirst()">首页</a></li>\n' +
        '            <li class="page-item"><a class="page-link" href="#"  onclick="goPre()">上一页</a></li>';
    }
    if (pageNow !== pageCount && pageCount !==0) {
      pageHtml += '<li class="page-item"><a class="page-link" href="#"  onclick="goNext()">下一页</a></li>\n' +
        '            <li class="page-item"><a class="page-link" href="#"  onclick="goLast('+pageCount+')">尾页</a></li>';
    }
    pageHtml += '<li class="page-item"><span class="page-link">共'+pageCount+'页</span></li>\n' +
      '            <li class="page-item"><span class="page-link">共'+tableResult["totalCount"]+'条</span></li>\n' +
      '            <li class="page-item"><span class="page-link">当前是第'+pageNow+'页</span></li>';
    $(".pagination").html(pageHtml);
  }
  // 查询按钮点击事件
  function reloadTable() {
    $("#pageNow").val(1);
    loadPageableTable();
  }
  // 首页
  function goFirst() {
    $("#pageNow").val(1);
    loadPageableTable();
  }
  // 上一页
  function goPre() {
    var crtPageNow = $("#pageNow").val();
    var prePage = parseInt(crtPageNow) - 1;
    $("#pageNow").val(prePage);
    loadPageableTable();
  }
  // 下一页
  function goNext() {
    var crtPageNow = $("#pageNow").val();
    var nextPage = parseInt(crtPageNow) + 1;
    $("#pageNow").val(nextPage);
    loadPageableTable();
  }
  // 尾页
  function goLast(pageCount) {
    $("#pageNow").val(pageCount);
    loadPageableTable();
  }
  // 删除
  function doDelete() {
    // 判断选中了几行
    var checkedInputs = $("input[name=pathsToDelete]:checked");
    if (!checkedInputs || checkedInputs.length === 0) {
      $("#tipCont").text("请选择要删除的行");
      $("#tipModal").modal("show");
      return;
    }
    // 获取到选择行的id
    var pathsToDelete = [];
    $.each(checkedInputs,function (i,ele) {
      pathsToDelete.push($(ele).val());
    });
    // 向后台发送删除请求
    $.ajax({
      type:"post",
      url:'${rc.contextPath}/manage/deleteByIds',
      contentType:"application/json",
      data:JSON.stringify({"pathsToDelete":pathsToDelete}),
      success:function (result) {
        if (result.success) {
          reloadTable();
        }else {
          $("#tipCont").text(result.errMsg);
          $("#tipModal").modal("show");
        }
      }
    });
  }
  // 弹出新增文件对话框
  function showAddDia() {
    document.getElementById("addForm").reset();
    $("#fileNameHelp").html("");
    $("#requiredTip").text("");
    $("#addModal").modal("show");
  }
  // 前端文件预览
  function filePreview() {
    //获取文件
    var files = document.getElementById("file-input").files;
    var selectedCnt = files.length;
    var fileNameArr = [],fileSizeArr = [];
    for (var i =0;i<selectedCnt;i++) {
      var file = files[i];
      //文件大小
      var size = (file.size / 1024) / 1024;
      if (size > 2048) {
        alert("文件\""+file.name+"\"大小不能大于2048m!");
        document.getElementById("file-input").files = [];
        break;
      } else if (size <= 0) {
        alert("文件\""+file.name+"\"大小不能为0!");
        document.getElementById("file-input").files = [];
        break;
      }
      fileNameArr.push(file.name);
      fileSizeArr.push(file.size);
    }
    // 满足条件的数目等于选择的数目时再赋值
    if (fileNameArr.length === selectedCnt) {
      $("#addForm input[name=originalFileNames]").val(fileNameArr.join(","));
      $("#addForm input[name=sizes]").val(fileSizeArr.join(","));
      var fileNameHelpHtml = '';
      for (var j =0;j<fileNameArr.length;j++) {
        var oneFileName = fileNameArr[j];
        fileNameHelpHtml += '<div>'+oneFileName+'</div>';
      }
      $("#fileNameHelp").html(fileNameHelpHtml);
      // 去掉校验提示
      $("#requiredTip").text("");
    }
  }
  // 新增文件信息
  function trueAddFile() {
    var fileArr = document.getElementById("file-input").files;
    // 有文件时再进行文件上传
    if (fileArr && fileArr.length > 0) {
      $("#addModal").modal("hide");
      var formData = new FormData();
      for (var i = 0;i < fileArr.length;i++) {
        //  如果该键已经存在，则将值附加到该键的原始值,不能直接放入一个数组
        formData.append("files",fileArr[i]);
      }
      formData.append("originalFileNames",$("#addForm input[name=originalFileNames]").val());
      formData.append("sizes",$("#addForm input[name=sizes]").val());
      // 执行添加
      $.ajax({
        type:"post",
        url:'${rc.contextPath}/manage/addFile',
        data:formData,
        contentType: false,
        processData: false,
        cache: false,
        success:function (result) {
          if (result.success) {
           reloadTable();
          }else {
            $("#tipCont").text(result.errMsg);
            $("#tipModal").modal("show");
          }
        },
        // 只要状态码不是200就会进入到这里,如:
        /*
        error: "Bad Request"
        message: ""
        path: "/manage/addFile"
        status: 400
        timestamp: "2021-08-03T18:00:00.214+00:00"
         */
        error:function (err,errorStr) { // errorStr:一个"error"字符串
          $("#tipCont").text(err.responseText);
          $("#tipModal").modal("show");
        }
      });
    }else {
      $("#requiredTip").text("请先选择要上传的文件");
    }
  }
  // 执行
  loadPageableTable();
</script>
</body>
</html>