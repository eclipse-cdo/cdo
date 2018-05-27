Add the following plugins:

  org.eclipse.net4j.trace
  org.aspectj.runtime
  org.aspectj.weaver
  org.eclipse.equinox.weaving.hook
  org.eclipse.equinox.weaving.aspectj (Auto-Start=true)

Specify the following VM arguments:

  -Dosgi.framework.extensions=org.eclipse.equinox.weaving.hook
  -Dorg.eclipse.net4j.trace.listenerType=logger
  -Dorg.eclipse.net4j.trace.listenerDescription=C:\Develop\cdo-master\trace-file
