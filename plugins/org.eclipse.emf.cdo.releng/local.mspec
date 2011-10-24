<?xml version="1.0" encoding="UTF-8"?>
<mspec:mspec xmlns:mspec="http://www.eclipse.org/buckminster/MetaData-1.0" installLocation="" materializer="p2" name="local.mspec" url="build.cquery">
  <mspec:property key="target.os" value="*"/>
  <mspec:property key="target.ws" value="*"/>
  <mspec:property key="target.arch" value="*"/>
  <mspec:property key="resolve.target.platform" value="true"/>

  <mspec:property key="eclipse.downloads" value="http://download.eclipse.org"/>
  <mspec:property key="buckminster.download.source" value="true"/>
  
  <mspec:mspecNode materializer="workspace" filter="(buckminster.source=true)"/>
</mspec:mspec>
