/*
 * Copyright (c) 2009-2013, 2016, 2017, 2019, 2021, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 204890
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;

import org.eclipse.net4j.util.collection.MultiMap;
import org.eclipse.net4j.util.concurrent.CriticalSection;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOSavepointImpl extends CDOUserSavepointImpl implements InternalCDOSavepoint
{
  private final InternalCDOTransaction transaction;

  /**
   * The fixed point represented by this public savepoint handle.
   */
  private final TransactionBoundary boundary;

  private boolean wasDirty;

  public CDOSavepointImpl(InternalCDOTransaction transaction, InternalCDOSavepoint lastSavepoint, TransactionBoundary boundary)
  {
    super(transaction, lastSavepoint);
    this.transaction = transaction;
    this.boundary = boundary;

    boundary.setSavepoint(this);
    wasDirty = boundary.wasDirty();
  }

  @Override
  public InternalCDOTransaction getTransaction()
  {
    return (InternalCDOTransaction)super.getTransaction();
  }

  public TransactionBoundary getBoundary()
  {
    return boundary;
  }

  @Override
  public InternalCDOSavepoint getFirstSavePoint()
  {
    return (InternalCDOSavepoint)super.getFirstSavePoint();
  }

  @Override
  public InternalCDOSavepoint getPreviousSavepoint()
  {
    return (InternalCDOSavepoint)super.getPreviousSavepoint();
  }

  @Override
  public InternalCDOSavepoint getNextSavepoint()
  {
    return (InternalCDOSavepoint)super.getNextSavepoint();
  }

  @Override
  public void clear()
  {
    sync().run(() -> boundary.getSegment().clear());
  }

  @Override
  public boolean wasDirty()
  {
    return wasDirty;
  }

  @Override
  public Map<CDOID, CDOObject> getNewObjects()
  {
    return boundary.getSegment().getNewObjects();
  }

  @Override
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return boundary.getSegment().getDetachedObjects();
  }

  // Bug 283985 (Re-attachment)
  @Override
  public Map<CDOID, CDOObject> getReattachedObjects()
  {
    return boundary.getSegment().getReattachedObjects();
  }

  @Override
  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return boundary.getSegment().getDirtyObjects();
  }

  @Override
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas2()
  {
    return boundary.getSegment().getRevisionDeltas();
  }

  @Override
  public CDOChangeSetData getChangeSetData()
  {
    return sync().supply(() -> createChangeSetData(getNewObjects(), getRevisionDeltas2(), getDetachedObjects()));
  }

  @Override
  public CDOChangeSetData getAllChangeSetData()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> createChangeSetData( //
          history.aggregateNewObjects(boundary, false), //
          history.aggregateRevisionDeltas(boundary, false), //
          history.aggregateDetachedObjects(boundary, false)));
    }

    InternalCDOSavepoint previousSavepoint = getPreviousSavepoint();
    return previousSavepoint == null //
        ? createChangeSetData(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()) //
        : previousSavepoint.getAllChangeSetDataIncludingCurrent();
  }

  @Override
  public CDOChangeSetData getAllChangeSetDataIncludingCurrent()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> createChangeSetData( //
          history.aggregateNewObjects(boundary, true), //
          history.aggregateRevisionDeltas(boundary, true), //
          history.aggregateDetachedObjects(boundary, true)));
    }

    return sync().supply(() -> createChangeSetData( //
        getAllNewObjectsIncludingCurrent(), //
        getAllRevisionDeltasIncludingCurrent(), //
        getAllDetachedObjectsIncludingCurrent()));
  }

  @Override
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    return boundary.getSegment().getBaseNewObjects();
  }

  /**
   * Return the list of new objects from this point.
   */
  @Override
  public Map<CDOID, CDOObject> getAllDirtyObjects()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateDirtyObjects(boundary, false));
    }

    InternalCDOSavepoint previousSavepoint = getPreviousSavepoint();
    return previousSavepoint == null ? Collections.emptyMap() : previousSavepoint.getAllDirtyObjectsIncludingCurrent();
  }

  @Override
  public Map<CDOID, CDOObject> getAllDirtyObjectsIncludingCurrent()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateDirtyObjects(boundary, true));
    }

    return sync().supply(() -> {
      if (getPreviousSavepoint() == null)
      {
        return Collections.unmodifiableMap(getDirtyObjects());
      }

      MultiMap.ListBased<CDOID, CDOObject> dirtyObjects = new MultiMap.ListBased<>();
      for (InternalCDOSavepoint savepoint = this; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
      {
        dirtyObjects.getDelegates().add(savepoint.getDirtyObjects());
      }

      return dirtyObjects;
    });
  }

  /**
   * Return the list of new objects from this point without objects that are removed.
   */
  @Override
  public Map<CDOID, CDOObject> getAllNewObjects()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateNewObjects(boundary, false));
    }

    InternalCDOSavepoint previousSavepoint = getPreviousSavepoint();
    return previousSavepoint == null ? Collections.emptyMap() : previousSavepoint.getAllNewObjectsIncludingCurrent();
  }

  @Override
  public Map<CDOID, CDOObject> getAllNewObjectsIncludingCurrent()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateNewObjects(boundary, true));
    }

    return sync().supply(() -> {
      if (getPreviousSavepoint() == null)
      {
        return Collections.unmodifiableMap(getNewObjects());
      }

      Map<CDOID, CDOObject> newObjects = CDOIDUtil.createMap();

      for (InternalCDOSavepoint savepoint = getFirstSavePoint();; savepoint = savepoint.getNextSavepoint())
      {
        newObjects.putAll(savepoint.getNewObjects());

        for (CDOID removedID : savepoint.getDetachedObjects().keySet())
        {
          newObjects.remove(removedID);
        }

        if (savepoint == this)
        {
          break;
        }
      }

      return newObjects;
    });
  }

  /**
   * @since 2.0
   */
  @Override
  public Map<CDOID, CDORevision> getAllBaseNewObjects()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateBaseNewObjects(boundary, false));
    }

    InternalCDOSavepoint previousSavepoint = getPreviousSavepoint();
    return previousSavepoint == null ? Collections.emptyMap() : previousSavepoint.getAllBaseNewObjectsIncludingCurrent();
  }

  @Override
  public Map<CDOID, CDORevision> getAllBaseNewObjectsIncludingCurrent()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateBaseNewObjects(boundary, true));
    }

    return sync().supply(() -> {
      if (getPreviousSavepoint() == null)
      {
        return Collections.unmodifiableMap(getBaseNewObjects());
      }

      MultiMap.ListBased<CDOID, CDORevision> newObjects = new MultiMap.ListBased<>();
      for (InternalCDOSavepoint savepoint = this; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
      {
        newObjects.getDelegates().add(savepoint.getBaseNewObjects());
      }

      return newObjects;
    });
  }

  /**
   * Return the list of all deltas without objects that are removed.
   */
  @Override
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltas()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateRevisionDeltas(boundary, false));
    }

    InternalCDOSavepoint previousSavepoint = getPreviousSavepoint();
    return previousSavepoint == null ? Collections.emptyMap() : previousSavepoint.getAllRevisionDeltasIncludingCurrent();
  }

  @Override
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltasIncludingCurrent()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateRevisionDeltas(boundary, true));
    }

    return sync().supply(() -> {
      if (getPreviousSavepoint() == null)
      {
        return Collections.unmodifiableMap(getRevisionDeltas2());
      }

      InternalCDOSavepoint firstSavePoint = getFirstSavePoint();
      boolean multiSavepoint = firstSavePoint != this;
      Set<CDOFeatureDelta> originalFeatureDeltas = multiSavepoint ? new HashSet<>() : null;

      // We need to combine the results for all deltas in different savepoints.
      Map<CDOID, CDORevisionDelta> allRevisionDeltas = CDOIDUtil.createMap();

      for (InternalCDOSavepoint savepoint = firstSavePoint;; savepoint = savepoint.getNextSavepoint())
      {
        for (CDORevisionDelta revisionDelta : savepoint.getRevisionDeltas2().values())
        {
          CDOID id = revisionDelta.getID();
          if (!isNewObject(id))
          {
            CDORevisionDeltaImpl oldRevisionDelta = (CDORevisionDeltaImpl)allRevisionDeltas.get(id);
            if (oldRevisionDelta == null)
            {
              if (multiSavepoint)
              {
                for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
                {
                  originalFeatureDeltas.add(featureDelta);
                }
              }

              allRevisionDeltas.put(id, revisionDelta.copy());
            }
            else
            {
              for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas())
              {
                if (!multiSavepoint || originalFeatureDeltas.add(featureDelta))
                {
                  CDOFeatureDelta copy = featureDelta.copy();
                  oldRevisionDelta.addFeatureDelta(copy, null);
                }
              }
            }
          }
        }

        Set<CDOID> reattachedObjects = savepoint.getReattachedObjects().keySet();
        for (CDOID detachedID : savepoint.getDetachedObjects().keySet())
        {
          if (!reattachedObjects.contains(detachedID))
          {
            allRevisionDeltas.remove(detachedID);
          }
        }

        if (savepoint == this)
        {
          break;
        }
      }

      return Collections.unmodifiableMap(allRevisionDeltas);
    });
  }

  @Override
  public Map<CDOID, CDOObject> getAllDetachedObjects()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateDetachedObjects(boundary, false));
    }

    InternalCDOSavepoint previousSavepoint = getPreviousSavepoint();
    return previousSavepoint == null ? Collections.emptyMap() : previousSavepoint.getAllDetachedObjectsIncludingCurrent();
  }

  @Override
  public Map<CDOID, CDOObject> getAllDetachedObjectsIncludingCurrent()
  {
    TransactionHistory history = getTransactionHistory();
    if (history != null)
    {
      return sync().supply(() -> history.aggregateDetachedObjects(boundary, true));
    }

    return sync().supply(() -> {
      if (getPreviousSavepoint() == null && getReattachedObjects().isEmpty())
      {
        return Collections.unmodifiableMap(getDetachedObjects());
      }

      Map<CDOID, CDOObject> detachedObjects = CDOIDUtil.createMap();

      for (InternalCDOSavepoint savepoint = getFirstSavePoint();; savepoint = savepoint.getNextSavepoint())
      {
        for (Map.Entry<CDOID, CDOObject> entry : savepoint.getDetachedObjects().entrySet())
        {
          CDOID detachedID = entry.getKey();
          if (!isNewObject(detachedID))
          {
            CDOObject detachedObject = entry.getValue();
            detachedObjects.put(detachedID, detachedObject);
          }
        }

        Map<CDOID, CDOObject> reattachedObjects = savepoint.getReattachedObjects();

        for (CDOID reattachedID : reattachedObjects.keySet())
        {
          detachedObjects.remove(reattachedID);
        }

        if (savepoint == this)
        {
          break;
        }
      }

      return detachedObjects;
    });
  }

  @Override
  public boolean isNewObject(CDOID id)
  {
    if (id.isTemporary())
    {
      return true;
    }

    return sync().supply(() -> {
      for (InternalCDOSavepoint savepoint = this; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
      {
        if (savepoint.getNewObjects().containsKey(id))
        {
          return true;
        }
      }

      return false;
    });
  }

  @Override
  public CDOObject getDetachedObject(CDOID id)
  {
    return sync().supply(() -> {
      for (InternalCDOSavepoint savepoint = this; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
      {
        Map<CDOID, CDOObject> reattachedObjects = savepoint.getReattachedObjects();
        if (!reattachedObjects.isEmpty())
        {
          CDOObject object = reattachedObjects.get(id);
          if (object != null)
          {
            return null;
          }
        }

        Map<CDOID, CDOObject> detachedObjects = savepoint.getDetachedObjects();
        if (!detachedObjects.isEmpty())
        {
          CDOObject object = detachedObjects.get(id);
          if (object != null)
          {
            return object;
          }
        }
      }

      return null;
    });
  }

  @Override
  public CDOObject getDirtyObject(CDOID id)
  {
    return sync().supply(() -> {
      for (InternalCDOSavepoint savepoint = this; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
      {
        Map<CDOID, CDOObject> dirtyObjects = savepoint.getDirtyObjects();
        if (!dirtyObjects.isEmpty())
        {
          CDOObject object = dirtyObjects.get(id);
          if (object != null)
          {
            return object;
          }
        }
      }

      return null;
    });
  }

  @Override
  public void rollback()
  {
    sync().run(() -> {
      InternalCDOTransaction transaction = getTransaction();
      LifecycleUtil.checkActive(transaction);
      transaction.rollbackToSavepoint(this);
    });
  }

  private TransactionHistory getTransactionHistory()
  {
    return transaction instanceof TransactionHistory ? (TransactionHistory)transaction : null;
  }

  private CDOChangeSetData createChangeSetData(Map<CDOID, CDOObject> newObjects, Map<CDOID, CDORevisionDelta> revisionDeltas,
      Map<CDOID, CDOObject> detachedObjects)
  {
    List<CDOIDAndVersion> newList = new ArrayList<>(newObjects.size());
    for (CDOObject object : newObjects.values())
    {
      newList.add(object.cdoRevision());
    }

    List<CDORevisionKey> changedList = new ArrayList<>(revisionDeltas.size());
    for (CDORevisionDelta delta : revisionDeltas.values())
    {
      changedList.add(delta);
    }

    List<CDOIDAndVersion> detachedList = new ArrayList<>(detachedObjects.size());
    for (CDOID id : detachedObjects.keySet())
    {
      detachedList.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
    }

    return new CDOChangeSetDataImpl(newList, changedList, detachedList);
  }

  private CriticalSection sync()
  {
    return transaction.sync();
  }

  @Override
  @Deprecated
  public Set<CDOID> getSharedDetachedObjects()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void recalculateSharedDetachedObjects()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public ConcurrentMap<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return new ConcurrentMap<>()
    {
      @Override
      public int size()
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().size();
      }

      @Override
      public boolean isEmpty()
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().isEmpty();
      }

      @Override
      public boolean containsKey(Object key)
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().containsKey(key);
      }

      @Override
      public boolean containsValue(Object value)
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().containsValue(value);
      }

      @Override
      public CDORevisionDelta get(Object key)
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().get(key);
      }

      @Override
      public CDORevisionDelta put(CDOID key, CDORevisionDelta value)
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().put(key, value);
      }

      @Override
      public CDORevisionDelta remove(Object key)
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().remove(key);
      }

      @Override
      public void putAll(Map<? extends CDOID, ? extends CDORevisionDelta> m)
      {
        CDOSavepointImpl.this.getRevisionDeltas2().putAll(m);
      }

      @Override
      public void clear()
      {
        CDOSavepointImpl.this.getRevisionDeltas2().clear();
      }

      @Override
      public Set<CDOID> keySet()
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().keySet();
      }

      @Override
      public Collection<CDORevisionDelta> values()
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().values();
      }

      @Override
      public Set<Map.Entry<CDOID, CDORevisionDelta>> entrySet()
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().entrySet();
      }

      @Override
      public boolean equals(Object o)
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().equals(o);
      }

      @Override
      public int hashCode()
      {
        return CDOSavepointImpl.this.getRevisionDeltas2().hashCode();
      }

      @Override
      public CDORevisionDelta putIfAbsent(CDOID key, CDORevisionDelta value)
      {
        return null;
      }

      @Override
      public boolean remove(Object key, Object value)
      {
        return false;
      }

      @Override
      public boolean replace(CDOID key, CDORevisionDelta oldValue, CDORevisionDelta newValue)
      {
        return false;
      }

      @Override
      public CDORevisionDelta replace(CDOID key, CDORevisionDelta value)
      {
        return null;
      }
    };
  }
}
