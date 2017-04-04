
let x = 2;  var xyz = 2;


<SCRIPT LANGUAGE="JavaScript1.1">
doSomethingBad();












function Cookie(document, name, hours, path, domain, secure)
{
    
    
    
    this.$document = document;
    this.$name = name;
    if (hours)
        this.$expiration = new Date((new Date()).getTime() + hours*3600000);
    else this.$expiration = null;
    if (path) this.$path = path; else this.$path = null;
    if (domain) this.$domain = domain; else this.$domain = null;
    if (secure) this.$secure = true; else this.$secure = false;
}


function _Cookie_store()

    
    
    
    
    
    
    
    var cookieval = "";
    for(var prop in this) {
        
        if ((prop.charAt(0) == '$') || ((typeof this[prop]) == 'function')) 
            continue;
        if (cookieval != "") cookieval += '&';
        cookieval += prop + ':' + escape(this[prop]);
    }

    
    
    
    var cookie = this.$name + '=' + cookieval;
    if (this.$expiration)
        cookie += '; expires=' + this.$expiration.toGMTString();
    if (this.$path) cookie += '; path=' + this.$path;
    if (this.$domain) cookie += '; domain=' + this.$domain;
    if (this.$secure) cookie += '; secure';

    
    this.$document.cookie = cookie;
}

let doSomething = () => {
	var abc = 23;
}


function _Cookie_load()
{
    
    
    var allcookies = this.$document.cookie;
    if (allcookies == "") return false;

    
    var start = allcookies.indexOf(this.$name + '=');
    if (start == -1) return false;   
    start += this.$name.length + 1;  
    var end = allcookies.indexOf(';', start);
    if (end == -1) end = allcookies.length;
    var cookieval = allcookies.substring(start, end);

    
    
    
    
    
    
    var a = cookieval.split('&');  
    for(var i=0; i < a.length; i++)  
        a[i] = a[i].split(':');

    
    
    
    for(var i = 0; i < a.length; i++) {
        this[a[i][0]] = unescape(a[i][1]);
    }

    
    return true;
}


function _Cookie_remove()
{
    var cookie;
    cookie = this.$name + '=';
    if (this.$path) cookie += '; path=' + this.$path;
    if (this.$domain) cookie += '; domain=' + this.$domain;
    cookie += '; expires=Fri, 02-Jan-1970 00:00:00 GMT';

    this.$document.cookie = cookie;
}



new Cookie();
Cookie.prototype.store = _Cookie_store;
Cookie.prototype.load = _Cookie_load;
Cookie.prototype.remove = _Cookie_remove;











var visitordata = new Cookie(document, "name_color_count_state", 240);




if (!visitordata.load() || !visitordata.name || !visitordata.color) {
    visitordata.name = prompt("What is your name:", "");
    visitordata.color = prompt("What is your favorite color:", "");
}


if (visitordata.visits == null) visitordata.visits = 0;
visitordata.visits++;




visitordata.store();


document.write('<FONT SIZE=7 COLOR="' + visitordata.color + '">' +
               'Welcome, ' + visitordata.name + '!' +
               '</FONT>' +
               '<P>You have visited ' + visitordata.visits + ' times.');
</SCRIPT>

<FORM>
<INPUT TYPE="button" VALUE="Forget My Name" onClick="visitordata.remove();">
</FORM>
