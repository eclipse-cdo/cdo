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

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.util.transaction.TX;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class Store implements IStore
{
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
    this.repository = repository;
  }

  public void registerResource(CDOID id, String path, Map<CDOID, String> idToPathMap, Map<String, CDOID> pathToIDMap)
  {
    TX.execute(createRegisterResourceOperation(id, path, idToPathMap, pathToIDMap));
  }

  public CDOID loadResourceID(String path)
  {
    return (CDOID)TX.execute(createLoadResourceIDOperation(path));
  }

  public String loadResourcePath(CDOID id)
  {
    return (String)TX.execute(createLoadResourcePathOperation(id));
  }

  public void addRevision(RevisionManager revisionManager, CDORevisionImpl revision)
  {
    TX.execute(createAddRevisionOperation(revisionManager, revision));
  }

  public CDORevisionImpl loadRevision(CDOID id)
  {
    return (CDORevisionImpl)TX.execute(createLoadRevisionOperation(id));
  }

  public CDORevisionImpl loadHistoricalRevision(CDOID id, long timeStamp)
  {
    return (CDORevisionImpl)TX.execute(createLoadHistoricalRevisionOperation(id, timeStamp));
  }

  public CDOClassRef queryObjectType(CDOID id)
  {
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
