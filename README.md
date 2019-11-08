**Title:** CommentWatcher installation guide:

**Author:** Marian-Andrei RIZOIU (Marian-Andrei@rizoiu.eu)

Description:
---
CommentWatcher is an open source tool aimed at analyzing discussions on web forums.

Constructed as a web platform, CommentWatcher features automatic mass fetching of user posts from forum on multiple sites, extracting topics, visualizing the topics as an expression cloud and exploring their temporal evolution. The underlying social network of users is simultaneously constructed using the citation relations between users and visualized as a graph structure.

Our platform addresses the issues of the diversity and dynamics of structures of webpages hosting the forums by implementing a parser architecture that is independent of the HTML structure of webpages. This allows easy on-the-fly adding of new websites.

Two types of users are targeted: end users who seek to study the discussed topics and their temporal evolution, and researchers in need of establishing a forum benchmark dataset and comparing the performances of analysis tools.

Installation:
---
Steps to install CommentWatcher (on a unix machine):
- Use Tomcat Manager to deploy the "CommentWatcher.war" web archive ;
- Unzip the content of the "analyse.tar.bz2" in the folder "/opt" ;
- execute by typing in your browser the address indicated by Tomcat Manager for the installed application.

Notes: 
---
1) the WAR archive is optimized for Apache Tomcat 7.
2) Public Mercurial repository address: http://eric.univ-lyon2.fr/~commentwatcher/cgi-bin/CommentWatcher.cgi/CommentWatcher/
3) License: GNU General Public License (GPL) v3.

