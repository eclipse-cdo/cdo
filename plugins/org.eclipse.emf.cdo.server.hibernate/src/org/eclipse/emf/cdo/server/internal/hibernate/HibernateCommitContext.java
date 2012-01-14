/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import java.util.HashMap;

/**
 * A HibernateCommitContext contains the commitcontext as well as support for direct (hashmap) based search for a new or
 * changed object using the id.
 * 
 * @author Martin Taal
 */
public class HibernateCommitContext
{
  private InternalCommitContext commitContext;

  private HashMap<CDOID, InternalCDORevision> dirtyObjects;

  private HashMap<CDOID, InternalCDORevision> newObjects;

  public InternalCommitContext getCommitContext()
  {
    return commitContext;
  }

  public void setCommitContext(InternalCommitContext commitContext)
  {
    this.commitContext = commitContext;
  }

  // initialize is not done when the commitContext is set because it appeared
  // that at that moment the temp id's are not repaired. The initialize method
  // is called on demand.
  protected void initialize()
  {
    if (dirtyObjects != null)
    {
      return;
    }

    dirtyObjects = new HashMap<CDOID, InternalCDORevision>();
    for (InternalCDORevision cdoRevision : commitContext.getDirtyObjects())
    {
      dirtyObjects.put(cdoRevision.getID(), cdoRevision);
    }

    newObjects = new HashMap<CDOID, InternalCDORevision>();
    for (InternalCDORevision cdoRevision : commitContext.getNewObjects())
    {
      newObjects.put(cdoRevision.getID(), cdoRevision);
    }
  }

  public InternalCDORevision getDirtyObject(CDOID id)
  {
    initialize();
    return dirtyObjects.get(id);
  }

  public InternalCDORevision getNewObject(CDOID id)
  {
    initialize();
    return newObjects.get(id);
  }

  public void setNewID(CDOID oldId, CDOID newId)
  {
    initialize();
    InternalCDORevision cdoRevision;
    if ((cdoRevision = dirtyObjects.get(oldId)) != null)
    {
      dirtyObjects.remove(oldId);
      dirtyObjects.put(newId, cdoRevision);
      return;
    }

    if ((cdoRevision = newObjects.get(oldId)) != null)
    {
      newObjects.remove(oldId);
      newObjects.put(newId, cdoRevision);
      return;
    }
  }
}
