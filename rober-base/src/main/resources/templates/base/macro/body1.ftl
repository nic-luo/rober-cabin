<#-- 页面布局使用到的宏 -->
<#macro body1 title class>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link rel="icon" type="image/x-icon" href="${request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${ctxPath}/plugins/font-awesome/4.6.3/css/font-awesome.min.css">
    <link rel="stylesheet" href="${ctxPath}/plugins/bootstrap/3.3.7/css/bootstrap.css">
    <#-- 系统引入的前端资源 -->
    <script src="${ctxPath}/lang/string.js?${currentTimeMillis}" type="text/javascript"></script>
    <script src="${ctxPath}/lang/array.js?${currentTimeMillis}" type="text/javascript"></script>

    <script src="${ctxPath}/plugins/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
    <script src="${ctxPath}/plugins/bootstrap/3.3.7/js/bootstrap.js" type="text/javascript"></script>

    <script src="${ctxPath}/plugins/doT/1.1.0/doT.min.js" type="text/javascript"></script>
    <script src="${ctxPath}/plugins/vue/2.5.1/vue${jsSuffix}.js" type="text/javascript"></script>


</head>
<body class="${class}">
    <#nested>
</body>
</html>
</#macro>
