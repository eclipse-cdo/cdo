/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.server.bundle.CDOServer;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.server.ResourceManager;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ResourceManagerImpl implements ResourceManager
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_SESSION, SessionManagerImpl.class);

  private RepositoryImpl repository;

  private Map<CDOID, String> idToPathMap = new HashMap();

  private Map<String, CDOID> pathToIDMap = new HashMap();

  public ResourceManagerImpl(RepositoryImpl repository)
  {
    this.repository = repository;
  }

  public RepositoryImpl getRepository()
  {
    return repository;
  }

  public CDOID getResourceID(String path)
  {
    CDOID id = pathToIDMap.get(path);
    if (id == null)
    {
      id = loadID(path);
      registerResource(id, path);
    }

    return id;
  }

  public String getResourcePath(CDOID id)
  {
    String path = idToPathMap.get(id);
    if (path == null)
    {
      path = loadPath(id);
      registerResource(id, path);
    }

    return path;
  }

  public void registerResource(CDOID id, String path)
  {
    final String oldPath = idToPathMap.put(id, path);
    if (oldPath != path)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Registering resource: {0} --> {1}", id, path);
      }

      pathToIDMap.put(path, id);
    }
  }

  private CDOID loadID(String path)
  {
    // TODO Implement method ResourceManagerImpl.loadID()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private String loadPath(CDOID id)
  {
    // TODO Implement method ResourceManagerImpl.loadPath()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
