/**
 * 对象常见操作工具类
 */
var objectkit = {
    isJSON: function(obj) {
        var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
        return isjson;
    },
    deepClone: function(obj) {
        return JSON.parse(JSON.stringify(obj));
    },
    equals: function(v1, v2) {
        if (typeof(v1) === "object" && objectkit.isJSON(v1) && typeof(v2) === "object" && objectkit.isJSON(v2)) {
            return JSON.stringify(v1) == JSON.stringify(v2);
        } else {
            return v1 == v2;
        }

    }
};

/**
 * 数组API扩展
 */
Array.prototype.getIndex = function(arg, n) {
    var i = isNaN(n) || n < 0 ? 0 : n;
    for (; i < this.length; i++) {
        if (this[i] == arg) {
            return i;
        } else if (typeof(this[i]) === "object" && objectkit.equals(this[i], arg)) {
            return i;
        }
    }
    return -1;
};
Array.prototype.each = function(fn) {
    fn = fn || Function.K;
    var a = [];
    var args = Array.prototype.slice.call(arguments, 1);
    for (var i = 0; i < this.length; i++) {
        var res = fn.apply(this, [this[i], i].concat(args));
        if (res != null) a.push(res);
    }
    return a;
};
Array.prototype.contains = function(obj) {
    return this.getIndex(obj) >= 0;
};
Array.prototype.clear = function() {
    this.length = 0;
    return this;
};
Array.prototype.append = function(obj) {
    this.push(obj);
    return this;
};
Array.prototype.insertAt = function(index, obj) {
    this.splice(index, 0, obj);
    return this;
};
Array.prototype.removeAt = function(index) {
    this.splice(index, 1);
    return this;
};
Array.prototype.remove = function(obj) {
    if (obj instanceof Array) {
        for (var i = 0; i < obj.length; i++) {
            this.remove(obj[i]);
        }
    }
    var index = this.getIndex(obj);
    if (index >= 0) {
        this.removeAt(index);
    }
    return this;
};
Array.prototype.size = function() {
    return this.length;
};
Array.prototype.clone = function() {
    var cloneList = Array();
    for (var i = 0, a = 0; i < this.length; i++) {
        cloneList.push(this[i]);
    }
    return cloneList;
};

Array.prototype.clone = function() {
    var arr = [];
    for (var i = 0; i < this.length; i++) {
        if (typeof(this[i]) !== 'object') {
            arr.push(this[i]);
        } else if (objectkit.isJSON(this[i])) {
            arr.push(objectkit.deepClone(this[i]));
        } else if (typeof(this[i].clone) === "function") {
            arr.push(this[i].clone());
        }
    }
    return arr;
};

/* variable number of arrays */
Array.prototype.merge = function() {
    for (var i = 0; i < arguments.length; i++) {
        var array = arguments[i];
        if (array instanceof Array) {
            for (var j = 0; j < array.length; j++) {
                if (this.getIndex(array[j]) === -1) {
                    this.push(array[j]);
                }
            }
        } else {
            this.push(array);
        }
    }
    return this;
};
Array.prototype.swap = function(i, j) {
    var temp = this[i];
    this[i] = this[j];
    this[j] = temp;
    return this;
};
/**  
 * 得到一个数组不重复的元素集合<br/>  
 * 唯一化一个数组  
 * @returns {Array} 由不重复元素构成的数组  
 */
Array.prototype.uniquelize = function() {
    var copy = this.clone();
    this.clear();
    for (var i = 0; i < copy.length; i++) {
        if (!this.contains(copy[i])) {
            this.push(copy[i]);
        }
    }
    return this;
};
/**
 * 两个集合的差集
 */
Array.prototype.minus = function(b) {
    var copy = this.clone();
    this.clear();
    var r = copy.uniquelize().each(function(o) { return b.contains(o) ? null : o });
    return this.concat(r);
};

/**
 * 求两个集合的并集
 */
Array.prototype.union = function(b) {
    var copy = this.clone();
    this.clear();
    var r = copy.concat(b).uniquelize();;
    return this.concat(r);
};

/**
 * 两个集合的交集  
 */
Array.prototype.intersect = function(b) {
    // return a.uniquelize().each(function(o) { return b.contains(o) ? o : null });

    var copy = this.clone();
    this.clear();
    var r = copy.uniquelize().each(function(o) { return b.contains(o) ? o : null });
    return this.concat(r);
};