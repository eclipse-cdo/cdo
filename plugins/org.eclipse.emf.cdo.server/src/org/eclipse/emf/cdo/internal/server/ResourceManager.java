/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.server.IResourceManager;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.StoreUtil;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ResourceManager extends Lifecycle implements IResourceManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, SessionManager.class);

  private Repository repository;

  private Map<CDOID, String> idToPathMap = new HashMap<CDOID, String>();

  private Map<String, CDOID> pathToIDMap = new HashMap<String, CDOID>();

  public ResourceManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
  }

  public CDOID getResourceID(String path)
  {
    CDOID id = pathToIDMap.get(path);
    if (id == null)
    {
      id = loadID(path);
      if (id != null)
      {
        registerResource(id, path);
      }
    }

    return id;
  }

  public String getResourcePath(CDOID id)
  {
    String path = idToPathMap.get(id);
    if (path == null)
    {
      path = loadPath(id);
      if (path != null)
      {
        registerResource(id, path);
      }
    }

    return path;
  }

  public void registerResource(CDOID id, String path)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering resource: {0} <--> {1}", id, path);
    }

    idToPathMap.put(id, path);
    pathToIDMap.put(path, id);
  }

  private CDOID loadID(String path)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    return storeReader.readResourceID(path);
  }

  private String loadPath(CDOID id)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    return storeReader.readResourcePath(id);
  }
}
