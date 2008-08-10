/*************************************************************************** 
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 * Contributors: 
 *    Simon McDuff - initial API and implementation 
 *    Eike Stepper - maintenance         
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOSavepointImpl implements CDOSavepoint
{
  private CDOTransactionImpl transaction;

  private Map<CDOID, CDOResource> newResources = new HashMap<CDOID, CDOResource>();

  private Map<CDOID, CDOObject> newObjects = new HashMap<CDOID, CDOObject>();

  private Map<CDOID, CDORevision> baseNewObjects = new HashMap<CDOID, CDORevision>();

  private Map<CDOID, CDOObject> dirtyObjects = new HashMap<CDOID, CDOObject>();

  private ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = new ConcurrentHashMap<CDOID, CDORevisionDelta>();

  private CDOSavepointImpl previousSavepoint;

  private CDOSavepointImpl nextSavepoint;

  private boolean isDirty;

  public CDOSavepointImpl(CDOTransactionImpl transaction, CDOSavepointImpl lastSavepoint)
  {
    this.transaction = transaction;
    isDirty = transaction.isDirty();
    previousSavepoint = lastSavepoint;
    if (previousSavepoint != null)
    {
      previousSavepoint.setNextSavepoint(this);
    }
  }

  public void clear()
  {
    newResources.clear();
    newObjects.clear();
    dirtyObjects.clear();
    revisionDeltas.clear();
    baseNewObjects.clear();
  }

  public boolean isDirty()
  {
    return isDirty;
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    return newResources;
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return newObjects;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return dirtyObjects;
  }

  public ConcurrentMap<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return revisionDeltas;
  }

  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    return baseNewObjects;
  }

  public CDOSavepointImpl getPreviousSavepoint()
  {
    return previousSavepoint;
  }

  public CDOSavepointImpl getNextSavepoint()
  {
    return nextSavepoint;
  }

  public void setPreviousSavepoint(CDOSavepointImpl previousSavepoint)
  {
    this.previousSavepoint = previousSavepoint;
  }

  public void setNextSavepoint(CDOSavepointImpl nextSavepoint)
  {
    this.nextSavepoint = nextSavepoint;
  }

  public CDOTransaction getTransaction()
  {
    return transaction;
  }

  public void rollback(boolean remote)
  {
    getTransaction().rollback(this, remote);
  }

  public void rollback()
  {
    getTransaction().rollback(this);
  }

  public boolean isValid()
  {
    CDOSavepoint lastSavepoint = getTransaction().getLastSavepoint();
    for (CDOSavepoint savepoint = lastSavepoint; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
    {
      if (savepoint == this)
      {
        return true;
      }
    }

    return false;
  }
}
