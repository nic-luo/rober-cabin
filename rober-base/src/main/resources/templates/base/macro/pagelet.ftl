<#-- 页面布局使用到的宏 -->
<#macro pagelet title class>
<#if pageletMode != "1">
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
    <script src="${ctxPath}/lang/string.js" type="text/javascript"></script>
    <script src="${ctxPath}/lang/array.js" type="text/javascript"></script>
    <script src="${ctxPath}/lang/Math.uuid.js" type="text/javascript"></script>

    <#--<script src="https://cdnjs.cloudflare.com/ajax/libs/sugar/1.4.1/sugar.min.js"></script>-->
    <#--<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>-->
    <#--<script src="https://cdnjs.cloudflare.com/ajax/libs/jQuery.my/1.2.11/jquerymy.min.js"></script>-->

    <script src="${ctxPath}/plugins/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
    <script src="${ctxPath}/plugins/bootstrap/3.3.7/js/bootstrap.js" type="text/javascript"></script>
    <script src="${ctxPath}/plugins/angular/1.4.6/angular.min.js" type="text/javascript"></script>
    <#--<script src="${ctxPath}/plugins/underscore/1.7.0/underscore-min.js" type="text/javascript"></script>-->
    <#--<script src="${ctxPath}/plugins/backbone/1.1.2/backbone-min.js" type="text/javascript"></script>-->
    <script src="${ctxPath}/rober/rober-core.js?${currentTimeMillis}" type="text/javascript"></script>
    <#--<script src="${ctxPath}/plugins/doT/1.1.0/doT.min.js" type="text/javascript"></script>-->

    <script type="text/javascript">
        rober.contextPath = "${ctxPath}";
        requirejs.config({
            baseUrl: '${ctxPath}',
            urlArgs: "cache=${currentTimeMillis}",
            paths: {
                "avalon":"plugins/avalon/2.2.4/avalon",
                "angular":"plugins/angular/1.4.6/angular.min",
                "Vue":"plugins/vue/2.5.1/vue${jsSuffix}",
                "Underscore":"plugins/underscore/1.7.0/underscore-min",
                "Backbone":"plugins/backbone/1.1.2/backbone-min",
                "ko":"plugins/knockout/3.4.2/knockout-min",
                "jquery": "plugins/jquery/1.12.4/jquery.min",
                "way": "plugins/way/way",
                "jquerymy": "plugins/jquerymy/jquerymy-1.2.14",
                "bootstrap": "plugins/bootstrap/3.3.7/js/bootstrap.min",
                "doT": "plugins/doT/1.1.0/doT.min",
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

        function fromAs(name,path,deps){
            var paths = {},shim={};
            paths[name]=path;
            shim[name]={};
            if(deps&&deps instanceof Array){
                shim[name].deps = deps;
                shim[name].exports = name;
            }
            requirejs.config({paths:paths,shim:shim});
        };

        function pagelet(){
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
</head>
<body>
    <#nested>
</body>
</html>
<#else>
    <#nested>
</#if>
</#macro>
