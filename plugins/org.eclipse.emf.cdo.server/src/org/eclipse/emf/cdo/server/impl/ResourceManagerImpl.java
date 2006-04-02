/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.net4j.spring.impl.ServiceImpl;

import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ResourceInfo;
import org.eclipse.emf.cdo.server.ResourceManager;

import java.util.HashMap;
import java.util.Map;


public class ResourceManagerImpl extends ServiceImpl implements ResourceManager
{
  private Map<Integer, ResourceInfo> ridToResourceMap = new HashMap<Integer, ResourceInfo>();

  private Map<String, ResourceInfo> pathToResourceMap = new HashMap<String, ResourceInfo>();

  public void registerResourceInfo(ResourceInfo resourceInfo)
  {
    if (isDebugEnabled()) debug("Registering " + resourceInfo);
    ridToResourceMap.put(resourceInfo.getRID(), resourceInfo);
    pathToResourceMap.put(resourceInfo.getPath(), resourceInfo);
  }

  public ResourceInfo registerResourceInfo(String resourcePath, int rid, long nextOIDFragment)
  {
    ResourceInfo resourceInfo = new ResourceInfoImpl(resourcePath, rid, nextOIDFragment);
    registerResourceInfo(resourceInfo);
    return resourceInfo;
  }

  public ResourceInfo getResourceInfo(String path, Mapper mapper)
  {
    ResourceInfo resourceInfo = pathToResourceMap.get(path);

    if (resourceInfo == null)
    {
      resourceInfo = mapper.selectResourceInfo(path);

      if (resourceInfo == null)
      {
        return null;
      }

      registerResourceInfo(resourceInfo);
    }

    return resourceInfo;
  }

  public ResourceInfo getResourceInfo(int rid, Mapper mapper)
  {
    ResourceInfo resourceInfo = ridToResourceMap.get(new Integer(rid));

    if (resourceInfo == null)
    {
      resourceInfo = mapper.selectResourceInfo(rid);

      if (resourceInfo == null)
      {
        return null;
      }

      registerResourceInfo(resourceInfo);
    }

    return resourceInfo;
  }
}
