// Object.prototype.assertSimpleValue = function(){
//     if(jQuery.type(this)==="function"){
//         throw new Error("value["+this+"] is function,can not convert to simple value"); 
//     }else if(jQuery.type(this)==="regexp"){
//         throw new Error("value["+this+"] is regexp,can not convert to simple value"); 
//     }
// };
// Object.prototype.isJSONObject = function(){
//     var v = this;
//     return typeof(v) == "object" 
//     && Object.prototype.toString.call(v).toLowerCase() == "[object object]" 
//     && !v.length; 
// };

// Object.prototype.strVal = function () {
//     this.assertSimpleValue();
//     var v = this;
//     var r = v;
//     if (v.isJSONObject()||jQuery.type(v)==="array") {
//         //return JSON.stringify(content, null, 2);
//         r = JSON.stringify(v);
//     }else if(jQuery.type(v)==="boolean"||jQuery.type(v)==="number"||jQuery.type(v)==="date"){
//         r = ""+v;
//     }
//     return r;
// };
// Object.prototype.doubleVal = function () {
//     this.assertSimpleValue();
//     var v = this;
//     var r = v;
//     if (v.isJSONObject()|| jQuery.type(v) === "array") {
//         r = undefined;
//     } else if (jQuery.type(v) === "string" && jQuery.isNumeric(v)) {
//         r = parseFloat(v);
//         if (isNaN(r)) r = undefined;
//     } else if (jQuery.type(v) === "number") {
//         r = v;
//     }
//     return r;
// };
// Object.prototype.intVal = function () {
//     this.assertSimpleValue();
//     var v = this;
//     var r = v;
//     if (v.isJSONObject()|| jQuery.type(v) === "array") {
//         r = undefined;
//     } else if (jQuery.type(v) === "string" && jQuery.isNumeric(v)) {
//         r = parseInt(v);
//         if (isNaN(r)) r = undefined;
//     } else if (jQuery.type(v) === "number") {
//         r = parseInt(""+v);
//     }
//     return r;
// };
// Object.prototype.boolVal = function(){
//     this.assertSimpleValue();
//     var v = this;
//     var r = v;
//     if (v.isJSONObject()|| jQuery.type(v) === "array") {
//         r = undefined;
//     }else if (jQuery.type(v) === "boolean") {
//         return r;
//     }else{
// 		if(jQuery.type(v.strVal())=="undefined")return;
//         return true==v||"true"==v||"TRUE"==v;
//     }
//     return r;
// };

// Object.prototype.jsonVal = function(){
//     this.assertSimpleValue();
//     var v = this;
//     var r = v;
//     if(v.isJSONObject()||jQuery.type(v) === "array"){
//         return r;
//     }else if (jQuery.type(v) === "string") {
//         r = jQuery.parseJSON(v);
//     }
//     return r;
// };