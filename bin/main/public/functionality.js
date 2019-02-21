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
function all(queryString){
  return document.querySelectorAll(queryString);
}
myFilter = function(id, sel, filter) {
  var a, b, c, i, ii, iii, hit;
  a = w3.getElements(id);
  for (i = 0; i < a.length; i++) {
    b = w3.getElements(sel);
    for (ii = 0; ii < b.length; ii++) {
      hit = 0;
      if (b[ii].innerHTML.toUpperCase().indexOf(filter.toUpperCase()) > -1) {
        hit = 1;
      }
      c = b[ii].getElementsByTagName("*");
      for (iii = 0; iii < c.length; iii++) {
        if (c[iii].innerHTML.toUpperCase().indexOf(filter.toUpperCase()) > -1) {
          hit = 1;
        }
      }
      if (hit == 1) {
        b[ii].parentElement.style.display = "";
      } else {
        b[ii].parentElement.style.display = "none";
      }
    }
  }
};
