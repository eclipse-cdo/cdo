/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.transaction.TX;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class Store implements IStore
{
  public static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, Store.class);

  private String type;

  private Repository repository;

  public Store(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }

  public Repository getRepository()
  {
    return repository;
  }

  public void setRepository(Repository repository)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting repository: {0} ({1})", repository.getName(), repository.getUUID());
    }

    this.repository = repository;
  }

  public void registerResource(CDOID id, String path, Map<CDOID, String> idToPathMap, Map<String, CDOID> pathToIDMap)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering resource: {0}", id);
    }

    TX.execute(createRegisterResourceOperation(id, path, idToPathMap, pathToIDMap));
  }

  public CDOID loadResourceID(String path)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Loading resource ID: {0}", path);
    }

    return (CDOID)TX.execute(createLoadResourceIDOperation(path));
  }

  public String loadResourcePath(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Loading resource path: {0}", id);
    }

    return (String)TX.execute(createLoadResourcePathOperation(id));
  }

  public void addRevision(RevisionManager revisionManager, CDORevisionImpl revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding revision: {0}", revision);
    }

    TX.execute(createAddRevisionOperation(revisionManager, revision));
  }

  public CDORevisionImpl loadRevision(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Loading revision: {0}", id);
    }

    return (CDORevisionImpl)TX.execute(createLoadRevisionOperation(id));
  }

  public CDORevisionImpl loadHistoricalRevision(CDOID id, long timeStamp)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Loading historical revision: {0} ({1,date} {1,time})", id, timeStamp);
    }

    return (CDORevisionImpl)TX.execute(createLoadHistoricalRevisionOperation(id, timeStamp));
  }

  public CDOClassRefImpl queryObjectType(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Querying object type: {0}", id);
    }

    // TODO Implement method Store.queryObjectType()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  protected abstract RegisterResourceOperation createRegisterResourceOperation(CDOID id, String path,
      Map<CDOID, String> idToPathMap, Map<String, CDOID> pathToIDMap);

  protected abstract LoadResourceIDOperation createLoadResourceIDOperation(String path);

  protected abstract LoadResourcePathOperation createLoadResourcePathOperation(CDOID id);

  protected abstract AddRevisionOperation createAddRevisionOperation(RevisionManager revisionManager,
      CDORevisionImpl revision);

  protected abstract LoadRevisionOperation createLoadRevisionOperation(CDOID id);

  protected abstract LoadHistoricalRevisionOperation createLoadHistoricalRevisionOperation(CDOID id, long timeStamp);
}
