(function(window) {
    var rober = window.rober||{};
    window.rober = rober;

    rober.contextPath = rober.contextPath||"";

    /**
     * URL地址特殊处理，如果有ctxPath，则自动添加上去
     * @param path
     * @returns {string}
     */
    rober.url = function(path) {
        var paths = [];
        paths.push(rober.contextPath);
        if (!path.startWith("/")) {
            paths.push("/");
        }
        paths.push(path);
        return paths.join("");
    };

    /**
     * 把请求地址中的参数转为JSON对象
     * @param url
     * @returns {{}|*}
     */
    rober.parseURL = function(url){
        url = url || window.location.href;

        var regUrl = /^([^\?]+)\?([\w\W]+)$/,regPara = /([^&=]+)=([\w\W]*?)(&|$|#)/g,
            arrUrl = regUrl.exec(url);
        var paramObject = {};
        if (arrUrl && arrUrl[2]) {
            var strPara = arrUrl[2], result;
            while ((result = regPara.exec(strPara)) != null) {
                var k = result[1],v = result[2];
                if(!paramObject[k]){
                    paramObject[k]=v;
                }else if(Object.prototype.toString.call(paramObject[k])=='[object Array]'){
                    paramObject[k].push(v); //如果是数组，则直接把参值放进去
                }else{
                    paramObject[k] = [paramObject[k]];  //如果之前的值，则转换成数组，把值放进去
                    paramObject[k].push(v);
                }
            }
            return {
                path:arrUrl[1],
                param:paramObject
            };
        }else{
            return {
                path:url,
                param:{}
            };
        }
    };

    rober.param = $.param;



    return rober;
})(window);