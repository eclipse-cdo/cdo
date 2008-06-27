/*************************************************************************** 
 * Copyright (c) 2004 - 2008 Simon McDuff, Canada. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 * Contributors: 
 *    Simon McDuff - initial API and implementation 
 *         
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSavePoint;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 */
public class CDOSavePointImpl implements CDOSavePoint
{
  private CDOTransactionImpl transaction = null;

  private Map<CDOID, CDOResource> newResources = new HashMap<CDOID, CDOResource>();

  private Map<CDOID, CDOObject> newObjects = new HashMap<CDOID, CDOObject>();

  private Map<CDOID, CDORevisionImpl> baseNewObjects = new HashMap<CDOID, CDORevisionImpl>();

  private Map<CDOID, CDOObject> dirtyObjects = new HashMap<CDOID, CDOObject>();

  private ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = new ConcurrentHashMap<CDOID, CDORevisionDelta>();

  private CDOSavePointImpl previousSavePoint = null;

  private CDOSavePointImpl nextSavePoint = null;

  private boolean isDirty = false;

  public CDOSavePointImpl(CDOTransactionImpl transaction, CDOSavePointImpl lastSavePoint)
  {
    this.transaction = transaction;
    this.isDirty = transaction.isDirty();
    this.previousSavePoint = lastSavePoint;
    if (this.previousSavePoint != null) this.previousSavePoint.setNextSavePoint(this);
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

  public Map<CDOID, CDORevisionImpl> getBaseNewObjects()
  {
    return baseNewObjects;
  }

  public CDOSavePointImpl getPreviousSavePoint()
  {
    return previousSavePoint;
  }

  public CDOSavePointImpl getNextSavePoint()
  {
    return nextSavePoint;
  }

  public void setPreviousSavePoint(CDOSavePointImpl previousSavePoint)
  {
    this.previousSavePoint = previousSavePoint;
  }

  public void setNextSavePoint(CDOSavePointImpl nextSavePoint)
  {
    this.nextSavePoint = nextSavePoint;
  }

  public CDOTransaction getTransaction()
  {
    return transaction;
  }

}
