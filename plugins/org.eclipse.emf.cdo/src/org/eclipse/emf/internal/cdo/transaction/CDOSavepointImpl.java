/*
 * Copyright (c) 2009-2013, 2016, 2017, 2019, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

  private Map<CDOID, CDORevision> baseNewObjects = CDOIDUtil.createMap();

  private Map<CDOID, CDOObject> newObjects = CDOIDUtil.createMap();

  // Bug 283985 (Re-attachment)
  private Map<CDOID, CDOObject> reattachedObjects = CDOIDUtil.createMap();

  private Map<CDOID, CDOObject> detachedObjects = new HashMap<CDOID, CDOObject>()
  {
    private static final long serialVersionUID = 1L;

    @Override
    public CDOObject put(CDOID key, CDOObject object)
    {
      return sync().supply(() -> {
        baseNewObjects.remove(key);
        newObjects.remove(key);
        reattachedObjects.remove(key);
        dirtyObjects.remove(key);
        revisionDeltas.remove(key);
        return super.put(key, object);
      });
    }
  };

  private Map<CDOID, CDOObject> dirtyObjects = CDOIDUtil.createMap();

  private Map<CDOID, CDORevisionDelta> revisionDeltas = new HashMap<CDOID, CDORevisionDelta>()
  {
    private static final long serialVersionUID = 1L;

    @Override
    public CDORevisionDelta put(CDOID id, CDORevisionDelta delta)
    {
      transaction.clearResourcePathCacheIfNecessary(delta);
      return super.put(id, delta);
    }

    @Override
    public void putAll(Map<? extends CDOID, ? extends CDORevisionDelta> m)
    {
      throw new UnsupportedOperationException();
    }
  };

  private boolean wasDirty;

  public CDOSavepointImpl(InternalCDOTransaction transaction, InternalCDOSavepoint lastSavepoint)
  {
    super(transaction, lastSavepoint);
    this.transaction = transaction;
    wasDirty = transaction.isDirty();
  }

  @Override
  public InternalCDOTransaction getTransaction()
  {
    return (InternalCDOTransaction)super.getTransaction();
  }

  @Override
  public InternalCDOSavepoint getFirstSavePoint()
  {
    return sync().supply(() -> (InternalCDOSavepoint)super.getFirstSavePoint());
  }

  @Override
  public InternalCDOSavepoint getPreviousSavepoint()
  {
    return sync().supply(() -> (InternalCDOSavepoint)super.getPreviousSavepoint());
  }

  @Override
  public InternalCDOSavepoint getNextSavepoint()
  {
    return sync().supply(() -> (InternalCDOSavepoint)super.getNextSavepoint());
  }

  @Override
  public void clear()
  {
    sync().run(() -> {
      newObjects.clear();
      dirtyObjects.clear();
      revisionDeltas.clear();
      baseNewObjects.clear();
      detachedObjects.clear();
      reattachedObjects.clear();
    });
  }

  @Override
  public boolean wasDirty()
  {
    return wasDirty;
  }

  @Override
  public Map<CDOID, CDOObject> getNewObjects()
  {
    return newObjects;
  }

  @Override
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return detachedObjects;
  }

  // Bug 283985 (Re-attachment)
  @Override
  public Map<CDOID, CDOObject> getReattachedObjects()
  {
    return reattachedObjects;
  }

  @Override
  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return dirtyObjects;
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
    return new ConcurrentMap<CDOID, CDORevisionDelta>()
    {
      @Override
      public int size()
      {
        return revisionDeltas.size();
      }

      @Override
      public boolean isEmpty()
      {
        return revisionDeltas.isEmpty();
      }

      @Override
      public boolean containsKey(Object key)
      {
        return revisionDeltas.containsKey(key);
      }

      @Override
      public boolean containsValue(Object value)
      {
        return revisionDeltas.containsValue(value);
      }

      @Override
      public CDORevisionDelta get(Object key)
      {
        return revisionDeltas.get(key);
      }

      @Override
      public CDORevisionDelta put(CDOID key, CDORevisionDelta value)
      {
        return revisionDeltas.put(key, value);
      }

      @Override
      public CDORevisionDelta remove(Object key)
      {
        return revisionDeltas.remove(key);
      }

      @Override
      public void putAll(Map<? extends CDOID, ? extends CDORevisionDelta> m)
      {
        revisionDeltas.putAll(m);
      }

      @Override
      public void clear()
      {
        revisionDeltas.clear();
      }

      @Override
      public Set<CDOID> keySet()
      {
        return revisionDeltas.keySet();
      }

      @Override
      public Collection<CDORevisionDelta> values()
      {
        return revisionDeltas.values();
      }

      @Override
      public Set<Map.Entry<CDOID, CDORevisionDelta>> entrySet()
      {
        return revisionDeltas.entrySet();
      }

      @Override
      public boolean equals(Object o)
      {
        return revisionDeltas.equals(o);
      }

      @Override
      public int hashCode()
      {
        return revisionDeltas.hashCode();
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

  @Override
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas2()
  {
    return revisionDeltas;
  }

  @Override
  public CDOChangeSetData getChangeSetData()
  {
    return sync().supply(() -> createChangeSetData(newObjects, revisionDeltas, detachedObjects));
  }

  @Override
  public CDOChangeSetData getAllChangeSetData()
  {
    return sync().supply(() -> createChangeSetData(getAllNewObjects(), getAllRevisionDeltas(), getAllDetachedObjects()));
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

  @Override
  public Map<CDOID, CDORevision> getBaseNewObjects()
  {
    return baseNewObjects;
  }

  /**
   * Return the list of new objects from this point.
   */
  @Override
  public Map<CDOID, CDOObject> getAllDirtyObjects()
  {
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
    return sync().supply(() -> {
      if (getPreviousSavepoint() == null)
      {
        return Collections.unmodifiableMap(getNewObjects());
      }

      Map<CDOID, CDOObject> newObjects = CDOIDUtil.createMap();
      for (InternalCDOSavepoint savepoint = getFirstSavePoint(); savepoint != null; savepoint = savepoint.getNextSavepoint())
      {
        newObjects.putAll(savepoint.getNewObjects());

        for (CDOID removedID : savepoint.getDetachedObjects().keySet())
        {
          newObjects.remove(removedID);
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
    return sync().supply(() -> {
      if (getPreviousSavepoint() == null)
      {
        return Collections.unmodifiableMap(getRevisionDeltas2());
      }

      InternalCDOSavepoint firstSavePoint = getFirstSavePoint();
      boolean multiSavepoint = firstSavePoint.getNextSavepoint() != null;
      Set<CDOFeatureDelta> originalFeatureDeltas = multiSavepoint ? new HashSet<>() : null;

      // We need to combine the results for all deltas in different savepoints.
      Map<CDOID, CDORevisionDelta> allRevisionDeltas = CDOIDUtil.createMap();

      for (InternalCDOSavepoint savepoint = firstSavePoint; savepoint != null; savepoint = savepoint.getNextSavepoint())
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
      }

      return Collections.unmodifiableMap(allRevisionDeltas);
    });
  }

  @Override
  public Map<CDOID, CDOObject> getAllDetachedObjects()
  {
    return sync().supply(() -> {
      if (getPreviousSavepoint() == null && getReattachedObjects().isEmpty())
      {
        return Collections.unmodifiableMap(getDetachedObjects());
      }

      Map<CDOID, CDOObject> detachedObjects = CDOIDUtil.createMap();
      for (InternalCDOSavepoint savepoint = getFirstSavePoint(); savepoint != null; savepoint = savepoint.getNextSavepoint())
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

  // TODO Not sure if this new implementation is needed. The existing one passes all tests.
  // public boolean isNewObject(CDOID id)
  // {
  // if (id.isTemporary())
  // {
  // return true;
  // }
  //
  // boolean isNew = false;
  // boolean wasNew = false;
  // synchronized (transaction) // TODO synchronized -> transaction.getViewLock().lock()
  // {
  // for (InternalCDOSavepoint savepoint = this; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
  // {
  // if (savepoint.getNewObjects().containsKey(id))
  // {
  // isNew = true;
  // wasNew = true;
  // }
  //
  // if (isNew && savepoint.getDetachedObjects().containsKey(id))
  // {
  // isNew = false;
  // }
  //
  // if (!isNew && wasNew && savepoint.getReattachedObjects().containsKey(id))
  // {
  // isNew = true;
  // }
  // }
  // }
  //
  // return isNew;
  // }

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

      CDOTransactionStrategy transactionStrategy = transaction.getTransactionStrategy();
      transactionStrategy.rollback(transaction, this);
    });
  }

  private CriticalSection sync()
  {
    return transaction.sync();
  }
}
