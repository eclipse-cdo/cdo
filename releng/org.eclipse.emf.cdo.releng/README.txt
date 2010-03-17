- Install Eclipse 3.6 from http://download.eclipse.org/eclipse/downloads (archive)
- Add to the installation from http://download.eclipse.org/releases/helios (p2)
		+ EMF 2.6 SDK
		+ Buckminster 3.6 Core
		+ Buckminster 3.6 CVS Support
		+ Buckminster 3.6 PDE Support

- Create empty project "_target"

- Create empty target platform,
	Add directory location $workspace_loc\_target,
	Activate target platform

- Bootstrap the releng component: http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.emf/org.eclipse.emf.cdo/releng/org.eclipse.emf.cdo.releng/psf/pserver/_bootstrap.psf?root=Modeling_Project&view=co
	Commiters use: http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.emf/org.eclipse.emf.cdo/releng/org.eclipse.emf.cdo.releng/psf/extssh/_bootstrap.psf?root=Modeling_Project&view=co

- Only for CDO 2.0: Team|Switch to "R2_0_maintenance" branch (not tag!)

- Right-click local.mspec, Buckminster|Import..., wait, press Finish

BUILD p2 repository:
- Right-click org.eclipse.emf.cdo.site-feature, Buckminster|Invoke action...|site.p2(.zip).
  DO NOT FORGET to point to the properties file, e.g. /org.eclipse.emf.cdo.releng/local.properties !!!!

PROMOTE:
- Copy update site to /home/data/httpd/download.eclipse.org/modeling/emf/cdo/updates/integration-
- Test installation from http://download.eclipse.org/modeling/emf/cdo/updates/integration-
- Contribute to Helios by updating the feature versions in /org.eclipse.helios.build/emf-cdo.build
- Kick Helios aggregation: https://build.eclipse.org/hudson/view/Repository Aggregation/job/helios.runBuckyBuild
- Test installation from http://download.eclipse.org/releases/staging

Structure of site feature:
- Edit category.xml in the site project (--> http://lenettoyeur-on-eclipse.blogspot.com/2009/11/nesting-categories.html )
