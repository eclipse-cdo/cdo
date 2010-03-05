/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class DefaultCDOMerger implements CDOMerger
{
  public DefaultCDOMerger()
  {
  }

  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source)
  {
    CDOChangeSetData result = new CDOChangeSetDataImpl();
    Map<CDOID, Object> targetMap = createMap(target);
    Map<CDOID, Object> sourceMap = createMap(source);

    for (Entry<CDOID, Object> entry : targetMap.entrySet())
    {
      CDOID id = entry.getKey();
      Object targetData = entry.getValue();
      Object sourceData = sourceMap.get(id);

      if (sourceData == null)
      {
        take(targetData, result);
      }
      else if (targetData == null)
      {
        take(sourceData, result);
      }
      else if (sourceData instanceof CDOID && targetData instanceof CDOID)
      {
        // Detached in both source and target
        take(sourceData, result);
      }
      else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDORevisionDelta)
      {
        // Changed in both source and target
        Object data = changedInSourceAndTarget((CDORevisionDelta)sourceData, (CDORevisionDelta)targetData);
        take(data, result);
      }
      else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDOID)
      {
        // Changed in source and detached in target
        Object data = changedInSourceAndDetachedInTarget((CDORevisionDelta)sourceData);
        take(data, result);
      }
    }

    for (Entry<CDOID, Object> entry : sourceMap.entrySet())
    {
      CDOID id = entry.getKey();
      Object sourceData = entry.getValue();
      Object targetData = targetMap.get(id);

      if (targetData instanceof CDORevisionDelta && sourceData instanceof CDOID)
      {
        // Changed in target and detached in source
        Object data = changedInTargetAndDetachedInSource((CDORevisionDelta)targetData);
        take(data, result);
      }
    }

    return result;
  }

  protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
      throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  protected Object changedInSourceAndDetachedInTarget(CDORevisionDelta sourceDelta)
      throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  protected Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta)
      throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  private Map<CDOID, Object> createMap(CDOChangeSetData changeSetData)
  {
    Map<CDOID, Object> map = new HashMap<CDOID, Object>();
    for (CDOIDAndVersion data : changeSetData.getNewObjects())
    {
      map.put(data.getID(), data);
    }

    for (CDORevisionKey data : changeSetData.getChangedObjects())
    {
      map.put(data.getID(), data);
    }

    for (CDOIDAndVersion data : changeSetData.getDetachedObjects())
    {
      map.put(data.getID(), data.getID());
    }

    return map;
  }

  private void take(Object data, CDOChangeSetData result)
  {
    if (data instanceof CDORevision)
    {
      result.getNewObjects().add((CDORevision)data);
    }
    else if (data instanceof CDORevisionDelta)
    {
      result.getChangedObjects().add((CDORevisionDelta)data);
    }
    else if (data instanceof CDOID)
    {
      result.getDetachedObjects().add(CDOIDUtil.createIDAndVersion((CDOID)data, CDOBranchVersion.UNSPECIFIED_VERSION));
    }
    else if (data != null)
    {
      throw new IllegalArgumentException("Must be a CDORevision, a CDORevisionDelta or a CDOID: " + data);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class PerFeature extends DefaultCDOMerger
  {
    public PerFeature()
    {
    }

    @Override
    protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
    {
      CDORevisionDeltaImpl result = new CDORevisionDeltaImpl(targetDelta, false);

      Map<EStructuralFeature, CDOFeatureDelta> targetMap = ((InternalCDORevisionDelta)targetDelta).getFeatureDeltaMap();
      Map<EStructuralFeature, CDOFeatureDelta> sourceMap = ((InternalCDORevisionDelta)sourceDelta).getFeatureDeltaMap();

      for (CDOFeatureDelta targetFeatureDelta : targetMap.values())
      {
        EStructuralFeature feature = targetFeatureDelta.getFeature();
        CDOFeatureDelta sourceFeatureDelta = sourceMap.get(feature);

        if (sourceFeatureDelta == null)
        {
          result.addFeatureDelta(targetFeatureDelta);
        }
        else
        {
          CDOFeatureDelta featureDelta = changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta);
          }
        }
      }

      for (CDOFeatureDelta sourceFeatureDelta : sourceMap.values())
      {
        EStructuralFeature feature = sourceFeatureDelta.getFeature();
        CDOFeatureDelta targetFeatureDelta = targetMap.get(feature);

        if (targetFeatureDelta == null)
        {
          result.addFeatureDelta(sourceFeatureDelta);
        }
      }

      return result;
    }

    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta) throws UnsupportedOperationException
    {
      throw new UnsupportedOperationException();
    }
  }
}
