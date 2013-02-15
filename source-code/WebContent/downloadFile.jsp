<!-- #-------------------------------------------------------------------------------
# Copyright (c) 2013 Marian-Andrei RIZOIU.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU Public License v3.0
# which accompanies this distribution, and is available at
# http://www.gnu.org/licenses/gpl.html
# 
# Contributors:
#     Marian-Andrei RIZOIU - initial API and implementation
#------------------------------------------------------------------------------- -->
<!--  %@ page import="obsolete.Configuration"%-->
<%@ page import="java.util.*,java.io.*"%><%
	// attention : ne pas mettre d'espace, de saut
	// de ligne ou de html à l'extérieur des balises jsp!

	// lien vers le fichier dont le nom est passé en paramètre. Remplacez "chemin du fichier" par
	// le chemin du répertoire dans lequel se trouve le fichier. Exemple : "C:/Fichiers/" sous Windows,
	// ou "/Fichiers/" sous Unix.
	File f = new File(request.getParameter("file"));

	// on renseigne le type de contenu. J'ai mis "unknown/unknown", ce qui permet de downloader
	// n'importe quel type de fichier, mais on peut aussi mettre "application/pdf" pour downloader
	// uniquement des pdf, ou "application/msword" pour des fichiers Microsoft Word, etc.
	response.setContentType("unknown/unknown");

	// on renseigne l'entete, en précisant quel sera le nom de fichier proposé à l'utilisateur pour
	// l'enregistrement sur son disque dur. Ici je lui propose le nom d'origine du fichier.
	response.setHeader("Content-Disposition", "attachment; filename=\""	+ request.getParameter("file") + "\"");

	// On ouvre un inputStream sur le fichier pour récupérer son contenu, et on remplit un servletOutputStream avec ce contenu.
	InputStream in = new FileInputStream(f);
	ServletOutputStream outs = response.getOutputStream();
	try {
		int bit = in.read();
		while ((bit) >= 0) {
			outs.write(bit);
			bit = in.read();
		}
	} catch (Exception e) {
		e.printStackTrace(System.out);
	}
	outs.flush();
	outs.close();
	in.close();

	//attention : ne pas mettre d'espace, de saut de ligne ou de html après la fermeture de la balise jsp!
%>
