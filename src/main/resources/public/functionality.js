var xhr = new XMLHttpRequest();
function xhrWithBody(method,url,callback,sendObject){
  var params = "";
  for(key in sendObject)
  {
    params += encodeURIComponent(key)+"="+encodeURIComponent(sendObject[key])+"&";
  }
  params = params.substr(0,params.length-1);
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
  xhr.send();
  // xhr.send(params);
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
numericalSort = function(id, sel, sortvalue) {
  var a, b, i, ii, y, bytt, v1, v2, cc, j;
  a = w3.getElements(id);
  for (i = 0; i < a.length; i++) {
    for (j = 0; j < 2; j++) {
      cc = 0;
      y = 1;
      while (y == 1) {
        y = 0;
        b = a[i].querySelectorAll(sel);
        for (ii = 0; ii < (b.length - 1); ii++) {
          bytt = 0;
          if (sortvalue) {
            v1 = Number(b[ii].querySelector(sortvalue).innerHTML.toLowerCase());
            v2 = Number(b[ii + 1].querySelector(sortvalue).innerHTML.toLowerCase());
          } else {
            v1 = Number(b[ii].innerHTML.toLowerCase());
            v2 = Number(b[ii + 1].innerHTML.toLowerCase());
          }
          if ((j == 0 && (v1 > v2)) || (j == 1 && (v1 < v2))) {
            bytt = 1;
            break;
          }
        }
        if (bytt == 1) {
          b[ii].parentNode.insertBefore(b[ii + 1], b[ii]);
          y = 1;
          cc++;
        }
      }
      if (cc > 0) {break;}
    }
  }
};