/*
 * Copyright (c) 2010-2016, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeKindCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.StringUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class CDOChangeSetDataImpl implements CDOChangeSetData
{
  private List<CDOIDAndVersion> newObjects;

  private List<CDORevisionKey> changedObjects;

  private List<CDOIDAndVersion> detachedObjects;

  private CDOChangeKindCache changeKindCache;

  public CDOChangeSetDataImpl(List<CDOIDAndVersion> newObjects, List<CDORevisionKey> changedObjects, List<CDOIDAndVersion> detachedObjects)
  {
    this.newObjects = Objects.requireNonNullElseGet(newObjects, () -> new ArrayList<CDOIDAndVersion>());
    this.changedObjects = Objects.requireNonNullElseGet(changedObjects, () -> new ArrayList<CDORevisionKey>());
    this.detachedObjects = Objects.requireNonNullElseGet(detachedObjects, () -> new ArrayList<CDOIDAndVersion>());
  }

  public CDOChangeSetDataImpl()
  {
    this(null, null, null);
  }

  @Override
  public boolean isEmpty()
  {
    if (newObjects != null && !newObjects.isEmpty())
    {
      return false;
    }

    if (changedObjects != null && !changedObjects.isEmpty())
    {
      return false;
    }

    if (detachedObjects != null && !detachedObjects.isEmpty())
    {
      return false;
    }

    return true;
  }

  @Override
  public CDOChangeSetData copy()
  {
    List<CDOIDAndVersion> newObjectsCopy = new ArrayList<>(newObjects.size());
    for (CDOIDAndVersion key : newObjects)
    {
      if (key instanceof CDORevision)
      {
        CDORevision revision = (CDORevision)key;
        newObjectsCopy.add(revision.copy());
      }
      else
      {
        newObjectsCopy.add(key);
      }
    }

    List<CDORevisionKey> changedObjectsCopy = new ArrayList<>(changedObjects.size());
    for (CDORevisionKey key : changedObjects)
    {
      if (key instanceof CDORevisionDelta)
      {
        CDORevisionDelta delta = (CDORevisionDelta)key;
        changedObjectsCopy.add(delta.copy());
      }
      else
      {
        changedObjectsCopy.add(key);
      }
    }

    List<CDOIDAndVersion> detachedObjectsCopy = new ArrayList<>(detachedObjects.size());
    for (CDOIDAndVersion key : detachedObjects)
    {
      detachedObjectsCopy.add(key);
    }

    return new CDOChangeSetDataImpl(newObjectsCopy, changedObjectsCopy, detachedObjectsCopy);
  }

  @Override
  public void merge(CDOChangeSetData changeSetData)
  {
    Map<CDOID, CDOIDAndVersion> newMap = CDOIDUtil.createMap();
    fillMap(newMap, newObjects);
    fillMap(newMap, changeSetData.getNewObjects());

    Map<CDOID, CDORevisionKey> changedMap = CDOIDUtil.createMap();
    fillMap(changedMap, changedObjects);
    for (CDORevisionKey key : changeSetData.getChangedObjects())
    {
      mergeChangedObject(key, newMap, changedMap);
    }

    Map<CDOID, CDOIDAndVersion> detachedMap = CDOIDUtil.createMap();
    fillMap(detachedMap, detachedObjects);
    for (CDOIDAndVersion key : changeSetData.getDetachedObjects())
    {
      CDOID id = key.getID();
      if (newMap.remove(id) == null)
      {
        detachedMap.put(id, key);
      }
    }

    newObjects = new ArrayList<>(newMap.values());
    changedObjects = new ArrayList<>(changedMap.values());
    detachedObjects = new ArrayList<>(detachedMap.values());
  }

  private void mergeChangedObject(CDORevisionKey key, Map<CDOID, CDOIDAndVersion> newMap, Map<CDOID, CDORevisionKey> changedMap)
  {
    CDOID id = key.getID();
    if (key instanceof CDORevisionDelta)
    {
      CDORevisionDelta delta = (CDORevisionDelta)key;

      // Try to add the delta to existing new revision
      CDOIDAndVersion oldRevision = newMap.get(id);
      if (oldRevision instanceof CDORevision)
      {
        CDORevision newRevision = (CDORevision)oldRevision;
        delta.applyTo(newRevision);
        return;
      }

      // Try to add the delta to existing delta
      CDORevisionKey oldDelta = changedMap.get(id);
      if (oldDelta instanceof CDORevisionDelta)
      {
        InternalCDORevisionDelta newDelta = (InternalCDORevisionDelta)oldDelta;
        for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
        {
          newDelta.addFeatureDelta(featureDelta, null);
        }

        return;
      }
    }

    // Fall back
    changedMap.put(id, key);
  }

  @Override
  public List<CDOIDAndVersion> getNewObjects()
  {
    return newObjects;
  }

  @Override
  public List<CDORevisionKey> getChangedObjects()
  {
    return changedObjects;
  }

  @Override
  public List<CDOIDAndVersion> getDetachedObjects()
  {
    return detachedObjects;
  }

  @Override
  public List<CDOID> getAffectedIDs()
  {
    return getAffectedIDs(this);
  }

  @Override
  public synchronized Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    if (changeKindCache == null)
    {
      changeKindCache = new CDOChangeKindCache(this);
    }

    return changeKindCache;
  }

  @Override
  public CDOChangeKind getChangeKind(CDOID id)
  {
    return getChangeKinds().get(id);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("ChangeSetData[newObjects={0}, changedObjects={1}, detachedObjects={2}]", //$NON-NLS-1$
        newObjects.size(), changedObjects.size(), detachedObjects.size());
  }

  public static List<CDOID> getAffectedIDs(CDOChangeSetData changeSetData)
  {
    List<CDOID> ids = new ArrayList<>();
    for (CDOIDAndVersion object : changeSetData.getNewObjects())
    {
      ids.add(object.getID());
    }

    for (CDORevisionKey object : changeSetData.getChangedObjects())
    {
      ids.add(object.getID());
    }

    for (CDOIDAndVersion object : changeSetData.getDetachedObjects())
    {
      ids.add(object.getID());
    }

    return ids;
  }

  public static String format(CDOChangeSetData changeSetData)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(changeSetData);
    builder.append(StringUtil.NL);

    format(builder, "  New Objects:", changeSetData.getNewObjects());
    format(builder, "  Changed Objects:", changeSetData.getChangedObjects());
    format(builder, "  Detached Objects:", changeSetData.getDetachedObjects());
    return builder.toString();
  }

  private static void format(StringBuilder builder, String label, List<?> objects)
  {
    builder.append(label);
    builder.append(StringUtil.NL);

    for (Object object : objects)
    {
      builder.append("    ");
      builder.append(object);
      builder.append(StringUtil.NL);
    }
  }

  private static <T extends CDOIDAndVersion> void fillMap(Map<CDOID, T> map, Collection<T> c)
  {
    for (T key : c)
    {
      map.put(key.getID(), key);
    }
  }
}
