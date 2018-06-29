function Map() {
	this.keys = new Array();
	this.data = new Object();

	this.Map = function() {
		//Constructor
		if (!(this instanceof Map))
			return new Map();
	};

	this.put = function(key, value) {
		if (this.data[key] == null) {
			this.keys.push(key);
		}
		this.data[key] = value;
	};
	
	this.insert = function(key,value,index) {
		if (this.data[key] == null) {
			this.keys.splice(index, 0, key);
		}
		this.data[key] = value;
	};

	this.get = function(key) {
		return this.data[key];
	};
	this.removeAll = function() {
		var len = this.keys.length;
		for ( var i = len - 1; i >= 0; i--) {
			this.remove(this.keys[i]);
		}
	};
	this.remove = function(key) {
		var index = this.keys.indexOf(key);
		if (index > -1) {
			this.keys.splice(index,1);
			this.data[key] = null;
		}
	};

	this.each = function(fn) {
		if (typeof fn != 'function') {
			return;
		}
		var len = this.keys.length;
		for ( var i = 0; i < len; i++) {
			var k = this.keys[i];
			fn(k, this.data[k], i);
		}
	};

	this.entrys = function() {
		var len = this.keys.length;
		var entrys = new Array(len);
		for ( var i = 0; i < len; i++) {
			entrys[i] = {
					key : this.keys[i],
					value : this.data[i]
			};
		}
		return entrys;
	};
	this.getKeys = function() {
		return this.keys;
	};
	this.isEmpty = function() {
		return this.keys.length == 0;
	};

	this.size = function() {
		return this.keys.length;
	};
	
	this.toJson = function (){
		return JSON.stringify(this);
	};
	
	this.setMap = function(array, object){
		if(array != null && array != undefined && object != null && object != undefined){
			this.data = object;
			this.keys = array;
		}
		else{
			this.keys = new Array();
			this.data = new Object();
		}
	}
	
	
};

Map.fromJson = function(jsonString){
	var obj = JSON.parse(jsonString);
	var instance = new Map();
	instance.keys = obj.keys;
	instance.data = obj.data;
    return instance;
};