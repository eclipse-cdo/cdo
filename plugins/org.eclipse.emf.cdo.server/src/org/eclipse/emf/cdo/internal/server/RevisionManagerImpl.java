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

import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOPathFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.server.RevisionManager;

/**
 * @author Eike Stepper
 */
public class RevisionManagerImpl extends CDORevisionResolverImpl implements RevisionManager
{
  private RepositoryImpl repository;

  public RevisionManagerImpl(RepositoryImpl repository)
  {
    this.repository = repository;
  }

  public RepositoryImpl getRepository()
  {
    return repository;
  }

  @Override
  public void addRevision(CDORevisionImpl revision)
  {
    super.addRevision(revision);
    if (revision.isResource())
    {
      String path = (String)revision.getData().get(CDOPathFeatureImpl.INSTANCE, -1);
      repository.getResourceManager().registerResource(revision.getID(), path);
    }
  }

  @Override
  protected CDORevisionImpl loadActual(CDOID id)
  {
    // TODO Implement method RevisionManagerImpl.loadActual()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  protected CDORevisionImpl loadHistorical(CDOID id, long timeStamp)
  {
    // TODO Implement method RevisionManagerImpl.loadHistorical()
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
