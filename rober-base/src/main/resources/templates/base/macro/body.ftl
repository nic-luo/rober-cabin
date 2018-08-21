<#-- 页面布局使用到的宏 -->
<#macro body title class>
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
    <script src="${ctxPath}/plugins/require/2.3.5/require.min.js" type="text/javascript"></script>
    <script src="${ctxPath}/lang/string.js?${currentTimeMillis}" type="text/javascript"></script>
    <script src="${ctxPath}/lang/array.js?${currentTimeMillis}" type="text/javascript"></script>
    <script src="${ctxPath}/plugins/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
    <script src="${ctxPath}/plugins/bootstrap/3.3.7/js/bootstrap.js" type="text/javascript"></script>
    <script src="${ctxPath}/rober/rober-core.js?${currentTimeMillis}" type="text/javascript"></script>
    <#--<script src="${ctxPath}/plugins/doT/1.1.0/doT.min.js" type="text/javascript"></script>-->
    <#--<script src="${ctxPath}/plugins/vue/2.5.1/vue${jsSuffix}.js" type="text/javascript"></script>-->


    <script type="text/javascript">
        rober.contextPath = "${ctxPath}";
        requirejs.config({
            baseUrl: '${ctxPath}',
            urlArgs: "cache=${currentTimeMillis}",
            paths: {
                "Vue":"plugins/vue/2.5.1/vue${jsSuffix}",
                "jquery": "plugins/jquery/1.12.4/jquery.min",
                "bootstrap": "plugins/bootstrap/3.3.7/js/bootstrap.min",
                "rober":"rober/rober-core"
            },
            shim: {
                "jquery":{
                    exports:"$"
                },
                "bootstrap": {
                    deps: ["jquery"]
                },
                "rober":{
                    deps: ["jquery"],
                    exports:"rober",
                    init:function(){}
                }

            }
        });

        function importModule(name,path,deps){
            var paths = {},shim={};
            paths[name]=path;
            shim[name]={};
            if(deps&&deps instanceof Array){
                shim[name].deps = deps;
                shim[name].exports = name;
            }
            requirejs.config({paths:paths,shim:shim});
        };

        function modulet(){
            var args = arguments;
            var func,funcArgs = [];
            if(!args||args.length==0)return;
            func = args[args.length-1];
            if(func instanceof Function){
                var requireArgs = [];
                for(var i=0;i<args.length-1;i++){
                    var v = args[i];
                    if(v instanceof Array){
                        requireArgs.merge(v);
                    }else{
                        requireArgs.push(v);
                    }
                }
                require(requireArgs, func);
            }
        };

    </script>
    <script type="text/javascript">
//        require(["jquery", "bootstrap","rober"], function ($,bs,rober) {
//            console.log(rober.url("1111"));
//            invoke1();
//        });
//        require(["jquery", "bootstrap","rober"], function () {
//            console.log($);
//            console.log(rober.url("2222"));
//        });
//        console.log(run);
//        require([], function () {
//            console.log($);
//            console.log(rober.url("2222"));
//        });
    </script>

</head>
<body class="${class}">
    <#nested>
</body>
</html>
</#macro>
