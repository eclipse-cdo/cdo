<?xml version="1.0" encoding="UTF-8"?>
<mspec:mspec xmlns:mspec="http://www.eclipse.org/buckminster/MetaData-1.0" installLocation="" materializer="p2" name="local.mspec" url="build.cquery">
  <mspec:property key="target.os" value="*"/>
  <mspec:property key="target.ws" value="*"/>
  <mspec:property key="target.arch" value="*"/>
  <mspec:property key="eclipse.downloads" value="http://download.eclipse.org"/>
  <mspec:property key="resolve.target.platform" value="true"/>
  <mspec:mspecNode namePattern="^org\.eclipse\.emf\.cdo\.server\.product$" materializer="workspace" resourcePath="org.eclipse.emf.cdo.server.product-feature"/>
  <mspec:mspecNode materializer="workspace" filter="(buckminster.source=true)"/>
</mspec:mspec>
