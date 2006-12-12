<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <title>Insert title here</title>
  <style type="text/css">
<!--
.style3 {
	color: #FF0000;
	font-size: x-small;
}
img.hide{
  display: none;
}

img.show{
  display: inline;
}

-->
    </style>
</head>
<body>

	<script>
	<!--

var req;

function loadXml( url )
{
    // branch for native XMLHttpRequest object
    if ( window.XMLHttpRequest )
    {
        req = new XMLHttpRequest();
        req.onreadystatechange = processReqChange;
        req.open( "GET", url, true );
        req.send( null );
    }
    // branch for IE/Windows ActiveX version
    else if ( window.ActiveXObject )
    {
        req = new ActiveXObject( "Microsoft.XMLHTTP" );
        if ( req )
        {
            req.onreadystatechange = processReqChange;
            req.open( "GET", url, true );
            req.send();
        }
    }
}

function processReqChange()
{
    // only if req shows "complete"
    if ( req.readyState == 4 )
	{
        // only if "OK"
        if ( req.status == 200 )
		{
            // ...processing statements go here...
			var text = req.responseText;
			if ( text.indexOf( "true" ) != -1 )
			{
				document.getElementById( "nameCheckFailed" ).className = 'show';
				document.getElementById( "nameCheckPassed" ).className = 'hide';
				alert( "User with name " + document.getElementById( "username" ).value + " already exists!" );
			}
			else
			{
				document.getElementById( "nameCheckFailed" ).className = 'hide';
				document.getElementById( "nameCheckPassed" ).className = 'show';
			}
        }
		else
		{
            alert( "There was a problem retrieving the XML data:\n" + req.statusText );
        }
    }
}

function checkName( username )
{
    if ( username == '' )
	{
		document.getElementById( "nameCheckFailed" ).className = 'hide';
		document.getElementById( "nameCheckPassed" ).className = 'hide';
		return;
	}

	url = '<%=config.getServletContext().getInitParameter( "userExistsServiceBase" )%>?username=' + username;
	loadXml( url );
}

// --------------------------------------------------------------------------------------
// Global pin length constraints
// --------------------------------------------------------------------------------------

pinMin = 4;
pinMax = 6;

// --------------------------------------------------------------------------------------
// Checks to make sure a both pins are numeric and comply with length constraints
// then checks to make sure that both the pin and pinConfirm input values are equal.
// --------------------------------------------------------------------------------------

function checkPinConfirm( pin, pinConfirm )
{
	if ( !checkPin( pinConfirm ) )
	{
		return;
	}

	if ( pin.value != pinConfirm.value )
	{
		pinConfirm.value = "";
		document.getElementById( "pinConfirmImgNo" ).className = "show";
		document.getElementById( "pinConfirmImgOk" ).className = "hide";
		alert( pinConfirm.name + " is not the same as the " + pin.name + "!" );
		return;
	}

	document.getElementById( "pinConfirmImgNo" ).className = "hide";
	document.getElementById( "pinConfirmImgOk" ).className = "show";
}

// --------------------------------------------------------------------------------------
// Checks to make sure a pin is numeric and falls in length constraints.
// --------------------------------------------------------------------------------------

function checkPin( pin )
{
    var okImgId = pin.name + "ImgOk";
	var noImgId = pin.name + "ImgNo";
    var temp = pin.value;

	if ( pin == '' )
	{
		document.getElementById( noImgId ).className = "show";
		document.getElementById( okImgId ).className = "hide";
	}

	if ( temp.length > pinMax )
	{
		alert( pin.name + " too long! Enter a numeric value " + pinMin
		+ "-" + pinMax + " characters in length!" );
		pin.value = "";
		document.getElementById( noImgId ).className = "show";
		document.getElementById( okImgId ).className = "hide";
		return false;
	}

	if ( temp.length < pinMin )
	{
		alert( pin.name + " too short! Enter a numeric value " + pinMin
		+ "-" + pinMax + " characters in length!" );
		pin.value = "";
		document.getElementById( noImgId ).className = "show";
		document.getElementById( okImgId ).className = "hide";
		return false;
	}

	for ( var ii = 0; ii < temp.length; ii++ )
	{
		if ( ! isDigit( temp.charAt( ii ) ) )
		{
			alert( pin.name + " contains non-numeric characters! Enter a numeric 4-6 digit value." );
			pin.value = "";
			document.getElementById( noImgId ).className = "show";
			document.getElementById( okImgId ).className = "hide";
			return false;
		}
	}

	document.getElementById( noImgId ).className = "hide";
	document.getElementById( okImgId ).className = "show";
	return true;
}

// --------------------------------------------------------------------------------------
// Checks to see if a number is a digit.
// --------------------------------------------------------------------------------------

function isDigit( num )
{
	if ( num.length > 1 )
	{
		return false;
	}
	var digits = "0123456789";
	if ( digits.indexOf( num ) != -1 )
	{
		return true;
	}
	else
	{
		return false;
	}
}

// ---------------------------------------------------------------------------------------
// This function capitalizes proper names; it also capitalizes the
// letter after the apostrophe, if one is present
// ---------------------------------------------------------------------------------------

function capitalizeName( Obj )
{
   checkNotNull( Obj );

   if ( Obj.value == '' )
   {
      return;
   }

   var temp = new String( Obj.value )
   var first = temp.substring( 0, 1 )
   temp = first.toUpperCase() + temp.substring( 1, temp.length )
   var apnum = temp.indexOf( "'" )

   if ( apnum > -1 )
   {
      var aplet = temp.substring( apnum+1, apnum+2 )
      temp  = temp.substring( 0, apnum ) + "'" +
              aplet.toUpperCase() +
              temp.substring( apnum+2, temp.length )
   }

   Obj.value = temp
}


// Declaring required variables
var digits = "0123456789";

// non-digit characters which are allowed in phone numbers
var phoneNumberDelimiters = "()- ";

// characters which are allowed in international phone numbers
// (a leading + is OK)
var validWorldPhoneChars = phoneNumberDelimiters + "+";

// Minimum no of digits in an international phone no.
var minDigitsInIPhoneNumber = 10;

function isInteger( s )
{
	var i;

	for ( i = 0; i < s.length; i++ )
    {
        // Check that current character is number.
        var c = s.charAt( i );
        if ( ( ( c < "0" ) || ( c > "9" ) ) )
		{
			return false;
		}
    }

    // All characters are numbers.
    return true;
}


function stripCharsInBag( s, bag )
{
    var i;
    var returnString = "";

	// Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for ( i = 0; i < s.length; i++ )
    {
        // Check that current character isn't whitespace.
        var c = s.charAt( i );

        if ( bag.indexOf( c ) == -1 )
		{
			returnString += c;
		}
    }

    return returnString;
}

function checkInternationalPhone( strPhone )
{
	s = stripCharsInBag( strPhone,validWorldPhoneChars );
	return ( isInteger( s ) && s.length >= minDigitsInIPhoneNumber );
}

function checkMobile( someField )
{
    var okImgId = someField.name + "ImgOk";
	var noImgId = someField.name + "ImgNo";

	if ( someField.value == '' )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		return;
	}

	if ( checkInternationalPhone( someField.value ) )
	{
		document.getElementById( okImgId ).className = "show";
		document.getElementById( noImgId ).className = "hide";
	}
	else
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
	}
}


function checkNotNull( someField )
{
    var okImgId = someField.name + "ImgOk";
	var noImgId = someField.name + "ImgNo";

	if ( someField.value == '' )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		return;
	}

	document.getElementById( okImgId ).className = "show";
	document.getElementById( noImgId ).className = "hide";
}


function checkEmail( someField )
{
	if ( someField.value == '' )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		return false;
	}

    var okImgId = someField.name + "ImgOk";
	var noImgId = someField.name + "ImgNo";
    var str = someField.value;
	var at = "@";
	var dot = ".";
	var lat = str.indexOf( at );
	var lstr = str.length;
	var ldot = str.indexOf( dot );

	if ( str.indexOf( at ) == -1 )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
	    alert( "Invalid E-mail ID" );
	    return false;
	}

	if ( str.indexOf( at ) == -1 || str.indexOf( at ) == 0 || str.indexOf( at ) == lstr )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
	    alert( "Invalid E-mail ID" );
	    return false;
	}

	if ( str.indexOf( dot ) ==-1 || str.indexOf( dot ) == 0 || str.indexOf( dot ) == lstr )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		alert( "Invalid E-mail ID" );
		return false;
	}

	if ( str.indexOf( at, ( lat + 1 ) ) != -1 )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		alert( "Invalid E-mail ID" );
		return false;
	}

	if ( str.substring( lat - 1, lat ) == dot || str.substring( lat + 1, lat + 2 ) == dot )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		alert( "Invalid E-mail ID" );
		return false;
	}

	if ( str.indexOf( dot, ( lat + 2 ) ) == -1 )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		alert( "Invalid E-mail ID" );
		return false;
	}

	if ( str.indexOf( " " ) != -1 )
	{
		document.getElementById( okImgId ).className = "hide";
		document.getElementById( noImgId ).className = "show";
		alert( "Invalid E-mail ID" );
		return false;
	}

	document.getElementById( okImgId ).className = "show";
	document.getElementById( noImgId ).className = "hide";
	return true;
}
//-->
	</script>


  <table width="800" border="0">
  <tr>
    <td colspan="2"><img src="images/safehaus-banner.png" width="1000" height="75" /></td>
    <td width="1">&nbsp;</td>
  </tr>
  <tr>
    <td width="160"><img src="images/lhs_spacer.png" width="160" height="500" /></td>
    <td width="836">  <form action="registerUserService" name="registrationForm" method="post">
    <table border="0" cellpadding="2" cellspacing="2" width="100%">
      <tr><td width="21%"><span style="color:#cc0000;">&nbsp;</span></td>
      <td></td></tr>

<tr>
    <td align="right" nowrap="nowrap" ><b>Username: <img id="nameCheckFailed" src="images/no_16x16.png" class="show" width="16" height="16" />
	<img id="nameCheckPassed" src="images/ok_16x16.png" class="hide" width="16" height="16" /></b></td>
    <td width="79%"><input name="username" type="text" id="username" value="" size="50" maxlength="60" onblur="checkName( this.value )" /></td>
</tr>

<tr>
  <td align="right" nowrap="nowrap" ><b>Pin: <img id="pinImgNo" src="images/no_16x16.png" class="show" width="16" height="16" />
    <img id="pinImgOk" src="images/ok_16x16.png" class="hide" width="16" height="16" />
  </b></td>
  <td><label for="textfield"></label>
    <input name="pin" type="password" id="pin" size="12" maxlength="6" onchange="checkPin(document.registrationForm.pin)"/></td>
</tr>

<tr>
  <td align="right" nowrap="nowrap" ><b>Pin Confirm: <img id="pinConfirmImgNo" src="images/no_16x16.png" class="show" width="16" height="16" />
  <img id="pinConfirmImgOk" src="images/ok_16x16.png" class="hide" width="16" height="16" /></b></td>
  <td><label for="textfield"></label>
    <input name="pinConfirm" type="password" id="pinConfirm" size="12" maxlength="6" onchange="checkPinConfirm(document.registrationForm.pin, document.registrationForm.pinConfirm)"/></td>
</tr>
<tr>
    <td align="right" nowrap="nowrap" ><b>First Name: <img id="firstNameImgNo" src="images/no_16x16.png" class="show" width="16" height="16" />
	<img id="firstNameImgOk" src="images/ok_16x16.png" class="hide" width="16" height="16" /> </b></td>
    <td width="79%"><input type="text" name="firstName" value="" size="25" maxlength="30"
	onblur="capitalizeName(document.registrationForm.firstName)"/></td>
</tr>

<tr>
    <td align="right" nowrap="nowrap" ><b>Last Name: <img id="lastNameImgNo" src="images/no_16x16.png" class="show" width="16" height="16" />
	<img id="lastNameImgOk" src="images/ok_16x16.png" class="hide" width="16" height="16" /></b></td>
    <td width="79%"><input type="text" name="lastName" value="" size="25" maxlength="30"
	onblur="capitalizeName(document.registrationForm.lastName)"/></td>
</tr>

<tr>
    <td align="right" nowrap="nowrap" >

        <b>Address Line1: </b><img src="images/spacer_16x16.png" width="16" height="16" />      </td>
    <td width="79%"><input type="text" name="address1" value="" size="50" maxlength="60"  /></td>
</tr>

<tr>

    <td align="right" nowrap="nowrap">
        <b>Address Line2:</b><b> </b><img src="images/spacer_16x16.png" width="16" height="16" /> </td>
    <td width="79%"><input type="text" name="address2" value="" size="50" maxlength="60"  /></td>
</tr>

<tr>
    <td align="right" nowrap="nowrap" ><b>City:</b><b> </b><img src="images/spacer_16x16.png" width="16" height="16" /></td>
    <td width="79%">
       <input type="text" name="city" value="" size="25"   />    </td>
</tr>



<tr>
    <td align="right" nowrap="nowrap" >

<b>State/Prov/Region:</b><b> </b><img src="images/spacer_16x16.png" width="16" height="16" /> </td>
    <td width="79%">
       <input type="text" name="state" value="" size="15"   />    </td>
</tr>

<tr>
    <td align="right" nowrap="nowrap" ><b>ZIP/Postal Code: </b><img src="images/spacer_16x16.png" width="16" height="16" /><b> </b></td>
    <td width="79%"><input type="text" name="zip" value="" size="20"   /></td>
</tr>

<tr>
    <td align="right" nowrap="nowrap" ><b>Country:</b><b> </b><img src="images/spacer_16x16.png" width="16" height="16" /></td>
    <td width="79%">
        <select name="country">
    <option>United States</option>

    <option>Afghanistan</option>
    <option>Albania</option>
    <option>Algeria</option>
    <option>American Samoa</option>
    <option>Andorra</option>
    <option>Anguilla</option>

    <option>Antarctica</option>
    <option>Antigua And Barbuda</option>
    <option>Argentina</option>
    <option>Armenia</option>
    <option>Aruba</option>
    <option>Australia</option>

    <option>Austria</option>
    <option>Azerbaijan</option>
    <option>Bahamas</option>
    <option>Bahrain</option>
    <option>Bangladesh</option>
    <option>Barbados</option>

    <option>Belarus</option>
    <option>Belgium</option>
    <option>Belize</option>
    <option>Benin</option>
    <option>Bermuda</option>
    <option>Bhutan</option>

    <option>Bolivia</option>
    <option>Bosnia and Herzegovina</option>
    <option>Botswana</option>
    <option>Bouvet Island</option>
    <option>Brazil</option>
    <option>British Indian Ocean Territory</option>

    <option>Brunei Darussalam</option>
    <option>Bulgaria</option>
    <option>Burkina Faso</option>
    <option>Burundi</option>
    <option>Cambodia</option>
    <option>Cameroon</option>

    <option>Canada</option>
    <option>Cape Verde</option>
    <option>Cayman Islands</option>
    <option>Central African Republic</option>
    <option>Chad</option>
    <option>Chile</option>

    <option>China</option>
    <option>Christmas Island</option>
    <option>Cocos (Keeling) Islands</option>
    <option>Colombia</option>
    <option>Comoros</option>
    <option>Congo</option>

    <option>Congo, the Democratic Republic of the</option>
    <option>Cook Islands</option>
    <option>Costa Rica</option>
    <option>Cote d'Ivoire</option>
    <option>Croatia</option>
    <option>Cyprus</option>

    <option>Czech Republic</option>
    <option>Denmark</option>
    <option>Djibouti</option>
    <option>Dominica</option>
    <option>Dominican Republic</option>
    <option>East Timor</option>

    <option>Ecuador</option>
    <option>Egypt</option>
    <option>El Salvador</option>
    <option>England</option>
    <option>Equatorial Guinea</option>
    <option>Eritrea</option>

    <option>Espana</option>
    <option>Estonia</option>
    <option>Ethiopia</option>
    <option>Falkland Islands</option>
    <option>Faroe Islands</option>
    <option>Fiji</option>

    <option>Finland</option>
    <option>France</option>
    <option>French Guiana</option>
    <option>French Polynesia</option>
    <option>French Southern Territories</option>
    <option>Gabon</option>

    <option>Gambia</option>
    <option>Georgia</option>
    <option>Germany</option>
    <option>Ghana</option>
    <option>Gibraltar</option>
    <option>Great Britain</option>

    <option>Greece</option>
    <option>Greenland</option>
    <option>Grenada</option>
    <option>Guadeloupe</option>
    <option>Guam</option>
    <option>Guatemala</option>

    <option>Guinea</option>
    <option>Guinea-Bissau</option>
    <option>Guyana</option>
    <option>Haiti</option>
    <option>Heard and Mc Donald Islands</option>
    <option>Honduras</option>

    <option>Hong Kong</option>
    <option>Hungary</option>
    <option>Iceland</option>
    <option>India</option>
    <option>Indonesia</option>
    <option>Ireland</option>

    <option>Israel</option>
    <option>Italy</option>
    <option>Jamaica</option>
    <option>Japan</option>
    <option>Jordan</option>
    <option>Kazakhstan</option>

    <option>Kenya</option>
    <option>Kiribati</option>
    <option>Korea, Republic of</option>
    <option>Korea (South)</option>
    <option>Kuwait</option>
    <option>Kyrgyzstan</option>

    <option>Lao People's Democratic Republic</option>
    <option>Latvia</option>
    <option>Lebanon</option>
    <option>Lesotho</option>
    <option>Liberia</option>
    <option>Libya</option>

    <option>Liechtenstein</option>
    <option>Lithuania</option>
    <option>Luxembourg</option>
    <option>Macau</option>
    <option>Macedonia</option>
    <option>Madagascar</option>

    <option>Malawi</option>
    <option>Malaysia</option>
    <option>Maldives</option>
    <option>Mali</option>
    <option>Malta</option>
    <option>Marshall Islands</option>

    <option>Martinique</option>
    <option>Mauritania</option>
    <option>Mauritius</option>
    <option>Mayotte</option>
    <option>Mexico</option>
    <option>Micronesia, Federated States of</option>

    <option>Moldova, Republic of</option>
    <option>Monaco</option>
    <option>Mongolia</option>
    <option>Montserrat</option>
    <option>Morocco</option>
    <option>Mozambique</option>

    <option>Myanmar</option>
    <option>Namibia</option>
    <option>Nauru</option>
    <option>Nepal</option>
    <option>Netherlands</option>
    <option>Netherlands Antilles</option>

    <option>New Caledonia</option>
    <option>New Zealand</option>
    <option>Nicaragua</option>
    <option>Niger</option>
    <option>Nigeria</option>
    <option>Niue</option>

    <option>Norfolk Island</option>
    <option>Northern Ireland</option>
    <option>Northern Mariana Islands</option>
    <option>Norway</option>
    <option>Oman</option>
    <option>Pakistan</option>

    <option>Palau</option>
    <option>Panama</option>
    <option>Papua New Guinea</option>
    <option>Paraguay</option>
    <option>Peru</option>
    <option>Philippines</option>

    <option>Pitcairn</option>
    <option>Poland</option>
    <option>Portugal</option>
    <option>Puerto Rico</option>
    <option>Qatar</option>
    <option>Reunion</option>

    <option>Romania</option>
    <option>Russia</option>
    <option>Russian Federation</option>
    <option>Rwanda</option>
    <option>Saint Kitts and Nevis</option>
    <option>Saint Lucia</option>

    <option>Saint Vincent and the Grenadines</option>
    <option>Samoa (Independent)</option>
    <option>San Marino</option>
    <option>Sao Tome and Principe</option>
    <option>Saudi Arabia</option>
    <option>Scotland</option>

    <option>Senegal</option>
    <option>Serbia and Montenegro</option>
    <option>Seychelles</option>
    <option>Sierra Leone</option>
    <option>Singapore</option>
    <option>Slovakia</option>

    <option>Slovenia</option>
    <option>Solomon Islands</option>
    <option>Somalia</option>
    <option>South Africa</option>
    <option>South Georgia and the South Sandwich Islands</option>
    <option>South Korea</option>

    <option>Spain</option>
    <option>Sri Lanka</option>
    <option>St. Helena</option>
    <option>St. Pierre and Miquelon</option>
    <option>Suriname</option>
    <option>Svalbard and Jan Mayen Islands</option>

    <option>Swaziland</option>
    <option>Sweden</option>
    <option>Switzerland</option>
    <option>Taiwan</option>
    <option>Tajikistan</option>
    <option>Tanzania</option>

    <option>Thailand</option>
    <option>Togo</option>
    <option>Tokelau</option>
    <option>Tonga</option>
    <option>Trinidad</option>
    <option>Trinidad and Tobago</option>

    <option>Tunisia</option>
    <option>Turkey</option>
    <option>Turkmenistan</option>
    <option>Turks and Caicos Islands</option>
    <option>Tuvalu</option>
    <option>Uganda</option>

    <option>Ukraine</option>
    <option>United Arab Emirates</option>
    <option>United Kingdom</option>
    <option>United States</option>
    <option>United States Minor Outlying Islands</option>
    <option>Uruguay</option>

    <option>USA</option>
    <option>Uzbekistan</option>
    <option>Vanuatu</option>
    <option>Vatican City State (Holy See)</option>
    <option>Venezuela</option>
    <option>Viet Nam</option>

    <option>Virgin Islands (British)</option>
    <option>Virgin Islands (U.S.)</option>
    <option>Wales</option>
    <option>Wallis and Futuna Islands</option>
    <option>Western Sahara</option>
    <option>Yemen</option>

    <option>Zambia</option>
    <option>Zimbabwe</option>
</select>    </td>
</tr>


<tr>
  <td align="right" nowrap="nowrap" ><strong>Email:<b> <img id="emailImgNo" src="images/no_16x16.png" class="show" width="16" height="16" />
  <img id="emailImgOk" src="images/ok_16x16.png" class="hide" width="16" height="16" /></b></strong></td>
  <td><input name="email" type="text" id="email" value="" size="25" onchange="checkEmail( this )"/></td>
</tr>
<tr>
  <td align="right" nowrap="nowrap" ><strong>Mobile Number:<b> <img id="mobileImgNo" src="images/no_16x16.png" class="show" width="16" height="16" /><img id="mobileImgOk" src="images/ok_16x16.png" class="hide" width="16" height="16" /></b></strong></td>
  <td><input name="mobile" type="text" id="mobile" value="" size="15" onchange="checkMobile(this)"  /></td>
</tr>
<tr>
    <td align="right" nowrap="nowrap" ><strong><b>Mobile Carrier: </b><img src="images/spacer_16x16.png" width="16" height="16" /><b> 
	</b></strong></td>

    <td width="79%"><select name="carrier">
      <option value="T-Mobile" selected="selected">T-Mobile</option>
      <option value="AT&T">AT&amp;T</option>
      <option value="Verizon">Verizon</option>
      <option value="Cingular">Cingular</option>
      <option value="Sprint">Sprint</option>
      <option value="Nextel">Nextel</option>
    </select></td>
</tr>
    </table>
    <table border="0" cellpadding="2" cellspacing="2" width="100%">
<tr>
  <td width="69%" align="right" valign="top">
    <p><b>Account activation URL delivery medium?</b></p>
    <p><em>(With SMS delivery your hauskeys can be provisioned to your handset) </em> </p></td>
  <td width="31%" valign="top" nowrap="nowrap">
<input type="radio" name="urlDeliveryMedium" value="1" checked="true" />
SMS to Mobile Phone
<br />
<input type="radio" name="urlDeliveryMedium" value="0" />
Email </td>
</tr>
<tr><td>&nbsp;</td>
<td nowrap="nowrap"><input type="image" src="images/continue.gif"  width="86" alt="Continue" height="22" border="0" /></td></tr>
    </table>
  </form></td></tr>
</table>


</body>
</html>