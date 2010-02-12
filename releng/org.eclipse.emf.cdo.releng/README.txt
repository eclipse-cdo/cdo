- Install IDE:
	Platform 3.6
	Buckminster 3.6
		core
		cvs
		pde

- Create empty project _target

- Create empty target platform (TP),
	add directory location $workspace_loc\_target,
	activate TP

- Bootstrap the releng component: http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.emf/org.eclipse.emf.cdo/releng/org.eclipse.emf.cdo.releng/psf/pserver/_bootstrap.psf?root=Modeling_Project&view=co
	Commiters use: http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.emf/org.eclipse.emf.cdo/releng/org.eclipse.emf.cdo.releng/psf/extssh/_bootstrap.psf?root=Modeling_Project&view=co

- Team|Switch to BRANCH (not tag!) R2_0_maintenance for CDO 2.0

- Right-click build.mspec, Buckminster|Import..., wait, press Finish

BUILD p2 repository:

- Right-click org.eclipse.emf.cdo.site-feature, Buckminster|Invoke action...|site.p2(.zip).
  DO NOT FORGET to point to the properties file, e.g. /org.eclipse.emf.cdo.releng/build-local.properties !!!!

PROMOTE:

- Edit category.xml in the site project (--> http://lenettoyeur-on-eclipse.blogspot.com/2009/11/nesting-categories.html )

- Copy update site to /home/data/httpd/download.eclipse.org/modeling/emf/cdo/updates/3.0
