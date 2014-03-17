var __hook = null;
var __hook_init = function(hook){
	if(hook){
		__hook = hook;
	}
};

var hook_apply=function(obj){
	if(__hook){
		__hook(obj);
	}
};