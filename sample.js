<!-- This example is from the book _JavaScript: The Definitive Guide_.     -->
let x = 2; <!-- Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates. --> var xyz = 2;
<!-- This example is provided WITHOUT WARRANTY either expressed or implied.-->
<!-- You may study, use, modify, and distribute it for any purpose.        -->
<SCRIPT LANGUAGE="JavaScript1.1">

// The constructor function.  Creates a cookie object for the specified
// document,  with a specified name. 
// attributes.  
// Arguments:
//   document: the Document object that the cookie is stored for.  Required.
//   name: a string that specifies a name for the cookie.  Required.
//   hours: an optional number that specifies the number of hours from now
//          that the cookie should expire.
//   path: an optional string that specifies the cookie path attribute.
//   domain: an optional string that specifies the cookie domain attribute.
//   secure: an optional boolean value that, if true, requests a secure cookie.
//
function Cookie(document, name, hours, path, domain, secure)
{
    // All the predefined properties of this object begin with '$'
    // to distinguish them from other properties which are the values to
    // be stored in the cookie.
    this.$document = document;
    this.$name = name;
    if (hours)
        this.$expiration = new Date((new Date()).getTime() + hours*3600000);
    else this.$expiration = null;
    if (path) this.$path = path; else this.$path = null;
    if (domain) this.$domain = domain; else this.$domain = null;
    if (secure) this.$secure = true; else this.$secure = false;
}

// This function is the store() method of the Cookie object
function _Cookie_store()
{
    // First, loop through the properties of the Cookie object and
    // put together the value of the cookie.  Since cookies use the
    // equals sign and semicolons as separators, we'll use colons
    // and ampersands for the individual state variables we store 
    // within a single cookie value.  Note that we escape the value
    // of each state variable, in case it contains punctuation or other
    // illegal characters.
    var cookieval = "";
    for(var prop in this) {
        // ignore properties with names that begin with '$' and also methods
        if ((prop.charAt(0) == '$') || ((typeof this[prop]) == 'function')) 
            continue;
        if (cookieval != "") cookieval += '&';
        cookieval += prop + ':' + escape(this[prop]);
    }

    // Now that we have the value of the cookie, put together the 
    // complete cookie string, which includes the name, and the various
    // attributes specified when the Cookie object was created.
    var cookie = this.$name + '=' + cookieval;
    if (this.$expiration)
        cookie += '; expires=' + this.$expiration.toGMTString();
    if (this.$path) cookie += '; path=' + this.$path;
    if (this.$domain) cookie += '; domain=' + this.$domain;
    if (this.$secure) cookie += '; secure';

    // Now store the cookie by setting the magic Document.cookie property
    this.$document.cookie = cookie;
}

let doSomething = () => {
	var abc = 23;
}

// This function is the load() method of the Cookie object
function _Cookie_load()
{
    // First, get a list of all cookies that pertain to this document.
    // We do this by reading the magic Document.cookie property
    var allcookies = this.$document.cookie;
    if (allcookies == "") return false;

    // Now extract just the named cookie from that list.
    var start = allcookies.indexOf(this.$name + '=');
    if (start == -1) return false;   // cookie not defined for this page.
    start += this.$name.length + 1;  // skip name and equals sign.
    var end = allcookies.indexOf(';', start);
    if (end == -1) end = allcookies.length;
    var cookieval = allcookies.substring(start, end);

    // Now that we've extracted the value of the named cookie, we've
    // got to break that value down into individual state variable 
    // names and values.  The name/value pairs are separated from each
    // other with ampersands, and the individual names and values are
    // separated from each other with colons.  We use the split method
    // to parse everything.
    var a = cookieval.split('&');  // break it into array of name/value pairs
    for(var i=0; i < a.length; i++)  // break each pair into an array
        a[i] = a[i].split(':');

    // Now that we've parsed the cookie value, set all the names and values
    // of the state variables in this Cookie object.  Note that we unescape()
    // the property value, because we called escape() when we stored it.
    for(var i = 0; i < a.length; i++) {
        this[a[i][0]] = unescape(a[i][1]);
    }

    // We're done, so return the success code
    return true;
}

// This function is the remove() method of the Cookie object.
function _Cookie_remove()
{
    var cookie;
    cookie = this.$name + '=';
    if (this.$path) cookie += '; path=' + this.$path;
    if (this.$domain) cookie += '; domain=' + this.$domain;
    cookie += '; expires=Fri, 02-Jan-1970 00:00:00 GMT';

    this.$document.cookie = cookie;
}

// Create a dummy Cookie object, so we can use the prototype object to make
// the functions above into methods.
new Cookie();
Cookie.prototype.store = _Cookie_store;
Cookie.prototype.load = _Cookie_load;
Cookie.prototype.remove = _Cookie_remove;

//===================================================================
//  The code above is the definition of the Cookie class
//  The code below is a sample use of that class.
//===================================================================

// Create the cookie we'll use to save state for this web page.
// Since we're using the default path, this cookie will be accessible
// to all web pages in the same directory as this file or "below" it.
// Therefore, it should have a name that is unique among those pages.
// Not that we set the expiration to 10 days in the future.
var visitordata = new Cookie(document, "name_color_count_state", 240);

// First, try to read data stored in the cookie.  If the cookie is not
// defined, or if it doesn't contain the data we need, then query the
// user for that data.
if (!visitordata.load() || !visitordata.name || !visitordata.color) {
    visitordata.name = prompt("What is your name:", "");
    visitordata.color = prompt("What is your favorite color:", "");
}

// Keep track of how many times this user has visited the page:
if (visitordata.visits == null) visitordata.visits = 0;
visitordata.visits++;

// Store the cookie values, even if they were already stored, so that the 
// expiration date will be reset to 10 days from this most recent visit.
// Also, store them again to save the updated visits state variable.
visitordata.store();

// Now we can use the state variables we read:
document.write('<FONT SIZE=7 COLOR="' + visitordata.color + '">' +
               'Welcome, ' + visitordata.name + '!' +
               '</FONT>' +
               '<P>You have visited ' + visitordata.visits + ' times.');
</SCRIPT>

<FORM>
<INPUT TYPE="button" VALUE="Forget My Name" onClick="visitordata.remove();">
</FORM>