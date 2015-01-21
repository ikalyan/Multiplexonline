'use strict'; 

var apiModule = angular.module('ApiModule', []);
apiModule.factory('apiService', ['$http', function($http) {
	var basePath="http://localhost:8080/navyaentertainment-client/";
	var _httpMethod="POST";
	var caching=false;
	$http.defaults.withCredentials = true;
	//TODO: <IE10 will require specific XDomainRequest headers for CORS requests

	return{

		/**
		function request: sends a request to api

		bundle object params:

		apiMethod: the api method to call (should be e)
		params: an object representing the key/value pairs that will be passed along with call
		httpMethod: does the call use "GET" or "POST"? Should be set to one of two

		Returns an object from which promises can be attached
		**/
		request: function(bundle){
			if(typeof bundle.apiMethod == "undefined"){
				throw "apiService.request requires an apiMethod parameter in its params object";
				return -1;
			}

			if(typeof bundle.transformResponse == "undefined"){
				bundle.transformResponse=function(data, headersGetter){
					if(typeof data == 'string') {
						try{
							return	JSON.parse(data);
						}
						catch(e){
							return data;
						}
					}
					else
						return data;
				};
			}			

			if(typeof bundle.httpMethod == "undefined")
				bundle.httpMethod=_httpMethod;

			var dataStr="";
			var contentType = "application/json";
			var firstIteration=true;

			//assemble params into data string format
			if(typeof bundle.params != "undefined"){
				for (var key in bundle.params) {
					if (bundle.params.hasOwnProperty(key)) {
						if(firstIteration)
							firstIteration=false;
						else
							dataStr+="&";

						//accepts an array and assigns all values to key
						if(Object.prototype.toString.call( bundle.params[key] ) === '[object Array]'){
							for(var i=0;i<bundle.params[key].length;i++){
								if(i>0)
									dataStr+="&";	
								dataStr+= key+"="+bundle.params[key][i];
							}
						}else if(Object.prototype.toString.call( bundle.params[key] ) === '[object Object]'){	
							dataStr+= key+"="+JSON.stringify(bundle.params[key]);
						}
						else{
							dataStr+= key+"="+bundle.params[key];							
						}
					}
				}
			}
			if(typeof bundle.formData != "undefined"){
				dataStr = bundle.formData;
				contentType = "application/json";
			}
			if(typeof bundle.fileData != "undefined"){
				dataStr = bundle.fileData;
				contentType = undefined;
			}
			if(bundle.httpMethod=='GET'){
				bundle.apiMethod = bundle.apiMethod + "?" + dataStr;
			}
			if(bundle.httpMethod=='POST'){
				dataStr = bundle.params;
			}
			if(typeof bundle.fileData != "undefined"){
				return $http({
					cache: caching,
					method: bundle.httpMethod,
					headers:{"Content-Type":contentType},
					url:basePath+bundle.apiMethod, 
					data:dataStr,
					transformRequest: angular.identity,
					transformResponse:bundle.transformResponse
				});
			} else {
				return $http({
					cache: caching,
					method: bundle.httpMethod,
					headers:{"Content-Type":contentType},
					url:basePath+bundle.apiMethod, 
					data:dataStr,					
					transformResponse:bundle.transformResponse
				});
			}
			//TODO: attach default error handlers
		},
		getBasePath: function(){
			return basePath;
		},
		setBasePath: function(bp){
			basePath=bp;
		},
		getHttpMethod: function(){
			return _httpMethod;
		},
		setHttpMethod: function(mthd){
			_httpMethod=mthd;
		}
	}
}]);