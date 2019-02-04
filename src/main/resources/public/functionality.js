var xhr = new XMLHttpRequest();
function xhrWithBody(method,url,callback,sendObject){
  var params = "";
  for(key in sendObject)
      params += encodeURIComponent(key)+"="+encodeURIComponent(sendObject[key])+"&";
  xhr.open(method,url+"?"+params,true);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  
  xhr.onreadystatechange = function(){
      if(this.readyState == 4 && this.status == 200){
          if(callback)
              callback(JSON.parse(this.responseText));
          else
              console.error(this.responseText);
      }
  }
  xhr.send(params);
}
function $(queryString){
  return document.querySelector(queryString);
}