- install:
	platform 3.5.1 http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/eclipse/downloads/drops/R-3.5.1-200909170800/eclipse-SDK-3.5.1-win32.zip
	(subclipse)
	buckminster http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/tools/buckminster/updates-3.5/ http://download.eclipse.org/tools/buckminster/updates-3.5
		core
		cvs
		(svn)
		pde
		psf

- create a cvs repo location (extssh for committers)

- checkout releng component (http://dev.eclipse.org/svnroot/tools/org.eclipse.buckminster/trunk/testbench/org.eclipse.emf.cdo.releng.buckminster)

- create empty TP, add directory location $workspace_loc\.target, activate TP, create empty project ".target"

- right-click build.mspec, Buckminster|import..., wait, press Finish

- right-click org.eclipse.emf.cdo.site-feature, Buckminster|Invoke action...|site.p2(.zip).
  DO NOT FORGET to point to the properties file, e.g. /org.eclipse.emf.cdo.releng.buckminster/buckminster-local.properties !!!!

- Edit category.xml in the site project (--> http://lenettoyeur-on-eclipse.blogspot.com/2009/11/nesting-categories.html )

- Signing: TBD

- Copy update site to /home/data/httpd/download.eclipse.org/modeling/emf/cdo/updates/3.0
