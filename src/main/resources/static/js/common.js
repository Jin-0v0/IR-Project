/*
获取form表单项的值并封装为json对象返回:
{"no":66,"realName":"李四","birthDay":"2021-07-01","phone":"123456"
,"email":"zhao@163.com","love":"football,basketball"}
serializeArray返回的结果:
(7) [{…}, {…}, {…}, {…}, {…}, {…}, {…}]
        0: {name: "no", value: "66"}
        1: {name: "realName", value: "李四"}
        2: {name: "birthDay", value: "2021-07-01"}
        3: {name: "phone", value: "123456"}
        4: {name: "email", value: "zhao@163.com"}
        5: {name: "love", value: "football"}
        6: {name: "love", value: "basketball"}
        length: 7
  jq ajax请求的两种常用方式的区别:
        contentType:"application/x-www-form-urlencoded"---data:json对象--req.getParameter("")
        contentType:"application/json"---data:JSON.stringify(json对象)
                    ---req.getInputStream(@RequestBody)
selector : 选择器,如#addForm
 */
function formDataObj(selector) {
  var arr = $(selector).serializeArray();
  var retObj ={};
  $.each(arr,function (i,ele) {
    if (retObj[ele["name"]]) {
      retObj[ele["name"]] += "," + ele["value"];
    } else{
      retObj[ele["name"]] =  ele["value"];
    }
  });
  return retObj;
}