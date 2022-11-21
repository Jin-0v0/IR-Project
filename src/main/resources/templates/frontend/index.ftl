<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>主页面</title>
  <link href="${rc.contextPath}/webjars/bootstrap/4.5.0/css/bootstrap.css" rel="stylesheet"/>
  <script src="${rc.contextPath}/webjars/jquery/3.3.1/jquery.js"></script>
  <script src="${rc.contextPath}/webjars/bootstrap/4.5.0/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<div class="container">
  <div class="row" style="margin-top: 200px">
    <div class="col-sm-12">
      <form action="${rc.contextPath}/toSearch" method="post">
        <div class="form-row">
          <div class="form-group col-sm-2">
            <select class="form-control" name="searchField">
              <option value="fileName" selected>标题</option>
              <option value="cont">全文</option>
              <option value="cont">作者</option>
              <option value="cont">日期</option>
              <option value="cont">隶属关系</option>
              <option value="cont">地址</option>
            </select>
          </div>
          <div class="form-group col-sm-8">
            <input type="text" name="searchWord" class="form-control"
                   placeholder="请输入检索内容" required/>
          </div>
          <div class="form-group col-sm-2">
            <input type="submit" class="btn btn-primary btn-block" value="检索"/>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>
</body>
</html>