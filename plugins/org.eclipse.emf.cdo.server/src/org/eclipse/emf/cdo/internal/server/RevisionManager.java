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
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.StoreUtil;

/**
 * @author Eike Stepper
 */
public class RevisionManager extends CDORevisionResolverImpl implements IRevisionManager
{
  private Repository repository;

  public RevisionManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
  }

  @Override
  public void addRevision(CDORevisionImpl revision)
  {
    StoreUtil.getTransaction().addRevision(revision);
    super.addRevision(revision);
    if (revision.isResource())
    {
      String path = (String)revision.getData().get(CDOPathFeatureImpl.INSTANCE, -1);
      repository.getResourceManager().registerResource(revision.getID(), path);
    }
  }

  @Override
  protected CDORevisionImpl loadRevision(CDOID id)
  {
    return StoreUtil.getTransaction().getRevision(id);
  }

  @Override
  protected CDORevisionImpl loadRevision(CDOID id, long timeStamp)
  {
    return StoreUtil.getTransaction().getRevision(id, timeStamp);
  }
}
