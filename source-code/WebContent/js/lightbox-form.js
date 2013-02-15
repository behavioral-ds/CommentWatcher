/* #-------------------------------------------------------------------------------
# Copyright (c) 2013 Marian-Andrei RIZOIU.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU Public License v3.0
# which accompanies this distribution, and is available at
# http://www.gnu.org/licenses/gpl.html
# 
# Contributors:
#     Marian-Andrei RIZOIU - initial API and implementation
#-------------------------------------------------------------------------------  */
function gradient(id, level)
{
	var box = document.getElementById(id);
	box.style.opacity = level;
	box.style.MozOpacity = level;
	box.style.KhtmlOpacity = level;
	box.style.filter = "alpha(opacity=" + level * 100 + ")";
	box.style.display="block";
	return;
}


function fadein(id) 
{
	var level = 0;
	while(level <= 1)
	{
		setTimeout( "gradient('" + id + "'," + level + ")", (level* 1000) + 10);
		level += 0.01;
	}
}


/**
 * 
 * Open the lightbox
 * 
 * @param formtitle
 * @param fadin
 * @return
 */
function openbox(formtitle, fadin)
{
	System.out.println("In the box!");
	alert('Welcome!');
	
	var box = document.getElementById('box'); 
	document.getElementById('filter').style.display='block';

	var btitle = document.getElementById('boxtitle');
	btitle.innerHTML = formtitle;

	if(fadin)
	{
		gradient("box", 0);
		fadein("box");
	}
	else
	{ 	
		box.style.display='block';
	}  	
}

/**
 * Close the lightbox
 * 
 * @return
 */
function closebox()
{
	document.getElementById('box').style.display='none';
	document.getElementById('filter').style.display='none';
}

////ajax
function alert1(){
	if (confirm("Etes vous sûr??")) 
	{
		return true;
	}
	else {
		return false;
	}
}

function ajax()
{
	var xhr=null;

	if (window.XMLHttpRequest) { 
		xhr = new XMLHttpRequest();
	}
	else if (window.ActiveXObject) 
	{
		xhr = new ActiveXObject("Microsoft.XMLHTTP");
	}
	// on définit l'appel de la fonction au retour serveur
	xhr.onreadystatechange = function() { alert_ajax(xhr); };

	// on appelle le fichier reponse.txt
	xhr.open("GET","reponse5.jsp?value=" 
			+ document.getElementById('search-text1').options[document.getElementById('search-text1').selectedIndex].text
			+ "&lien=" 
			+ document.getElementById('search-text1').options[document.getElementById('search-text1').selectedIndex].value, 
			true);
	xhr.send(null);
}

function alert_ajax(xhr)
{
	var txt =xhr.responseText;
	var mySplitResult = txt.split("*");
	document.forms["form"].elements["t1"].value=mySplitResult[0];
	document.forms["form"].elements["t2"].value=mySplitResult[1];
	document.forms["form1"].elements["inputlib"].value=document.getElementById('search-text1').options[document.getElementById('search-text1').selectedIndex].text;
	document.forms["form1"].elements["texturl"].value=document.getElementById('search-text1').options[document.getElementById('search-text1').selectedIndex].value;
}
//code pour la modification

function champs()
{
//	alert(document.getElementById('extra').options[document.getElementById('extra').selectedIndex].text);
	var number=document.getElementById('extra').options[document.getElementById('extra').selectedIndex].text;
	document.forms["form"].elements["number"].value=number;
	document.getElementById('dynamique').innerHTML="";
	for( i=0; i<number ;i++){
		document.getElementById('dynamique').innerHTML += '<td><select name=' + i + ' id=sel' + i 
				+ ' onchange="deletef(' + i 
				+ ')"> <OPTION value="SOURCE"> SOURCE </OPTION> <option value="DATE_SYSTEME">DATE_SYSTEME</option><option value="DATE">DATE</option><option value="URL">URL</option><option value="AUTEUR">AUTEUR</option></select></td><br/>';
	}

}

function deletef(num)
{
	var number=document.forms["form"].elements["number"].value ;
	var text=document.getElementById('sel'+num).options[document.getElementById('sel'+num).selectedIndex].text;

	alert(text);
	for(i=0;i<number;i++)
	{
		if(i!=num){
			for(j=0 ; j<document.getElementById('sel'+i).length ;j++)
			{
				if(document.getElementById('sel'+i).options[j].text==text){document.getElementById('sel'+i).options[j]=null;}
			}
		}
	}
}

/**
 * affichage des sources par langue
 */

/** rend invisible l'objet passé en paramétre, mais il garde sa place */
function cacher(lobjet)
{
	document.getElementById(lobjet).style.visibility = 'hidden';
}

/** rend Visible l'objet passé en paramétre */
function montrer(lobjet)
{
	document.getElementById(lobjet).style.visibility = 'visible';
}

/** rend invisible Sans prendre de place l'objet passé en paramétre */
function disparaitre(lobjet)
{
	document.getElementById(lobjet).style.display = 'none';
}

/** rend Visible l'objet pass� en param�tre */
function apparaitre(lobjet)
{
	document.getElementById(lobjet).style.display = 'block';
}


/** coche la checkbox pass�e en param�tre */
function cocher(lobjet)
{
	document.getElementById(lobjet).checked = true;
}

/** d�coche la checkbox pass�e en param�tre */
function decocher(lobjet)
{
	document.getElementById(lobjet).checked = false;
}

/** rend enabled l'objet pass� en param�tre */
function enable(lobjet)
{
	document.getElementById(lobjet).disabled = false;
}

/** rend disabled l'objet pass� en param�tre */
function disable(lobjet)
{
	document.getElementById(lobjet).disabled = true;
}

function changeDiv()
{
	if(document.getElementById('frenchDisc').style.visibility == 'visible')
	{
		montrer('"englishDisc"');
		cacher('frenchDisc');
	}
	else
	{
		cacher('"englishDisc"');
		montrer('frenchDisc');
	}
}
/** ******************************************************************************* */
function test()
{
	for(i=1;i<=document.form.source.length-1;i++)
	{
		alert(document.form.source.checked);
	}

}

function fonction1(form){
	var nom = form.le.value;
	var txt = form.li.value;
	var them = form.th.value;    
	document.forms["form1"].elements["theme"].value = them;
	document.forms["form1"].elements["nom"].value = nom;
	document.forms["form1"].elements["texturl"].value = txt;
	document.forms["form1"].elements["th"].value = them;
	document.forms["form1"].elements["le"].value = nom;
	document.forms["form1"].elements["li"].value = txt;
}
