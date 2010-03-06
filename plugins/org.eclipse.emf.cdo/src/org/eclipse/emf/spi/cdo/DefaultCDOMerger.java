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
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class DefaultCDOMerger implements CDOMerger
{
  private CDOChangeSetData result = new CDOChangeSetDataImpl();

  private Map<CDOID, Conflict> conflicts = new HashMap<CDOID, Conflict>();

  public DefaultCDOMerger()
  {
  }

  public CDOChangeSetData getResult()
  {
    return result;
  }

  public Map<CDOID, Conflict> getConflicts()
  {
    return conflicts;
  }

  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws ConflictException
  {
    Map<CDOID, Object> targetMap = createMap(target);
    Map<CDOID, Object> sourceMap = createMap(source);

    for (Entry<CDOID, Object> entry : targetMap.entrySet())
    {
      CDOID id = entry.getKey();
      Object targetData = entry.getValue();
      Object sourceData = sourceMap.get(id);

      Object data = null;
      if (sourceData == null)
      {
        if (targetData instanceof CDORevision)
        {
          data = addedInTarget((CDORevision)targetData);
        }
        else if (targetData instanceof CDORevisionDelta)
        {
          data = changedInTarget((CDORevisionDelta)targetData);
        }
        else if (targetData instanceof CDOID)
        {
          data = detachedInTarget((CDOID)targetData);
        }
      }
      else if (targetData == null)
      {
        if (sourceData instanceof CDORevision)
        {
          data = addedInSource((CDORevision)sourceData);
        }
        else if (sourceData instanceof CDORevisionDelta)
        {
          data = changedInSource((CDORevisionDelta)sourceData);
        }
        else if (sourceData instanceof CDOID)
        {
          data = detachedInSource((CDOID)sourceData);
        }
      }
      else if (sourceData instanceof CDOID && targetData instanceof CDOID)
      {
        data = detachedInSourceAndTarget((CDOID)sourceData);
      }
      else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDORevisionDelta)
      {
        data = changedInSourceAndTarget((CDORevisionDelta)sourceData, (CDORevisionDelta)targetData);
      }
      else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDOID)
      {
        data = changedInSourceAndDetachedInTarget((CDORevisionDelta)sourceData);
      }

      take(data);
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
        take(data);
      }
    }

    if (!conflicts.isEmpty())
    {
      throw new ConflictException(this);
    }

    return result;
  }

  protected Object addedInTarget(CDORevision revision)
  {
    return revision;
  }

  protected Object changedInTarget(CDORevisionDelta delta)
  {
    return delta;
  }

  protected Object detachedInTarget(CDOID id)
  {
    return id;
  }

  protected Object addedInSource(CDORevision revision)
  {
    return revision;
  }

  protected Object changedInSource(CDORevisionDelta delta)
  {
    return delta;
  }

  protected Object detachedInSource(CDOID id)
  {
    return id;
  }

  protected Object detachedInSourceAndTarget(CDOID id)
  {
    return id;
  }

  protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
  {
    return new ChangedInSourceAndTargetConflict(targetDelta, sourceDelta);
  }

  protected Object changedInSourceAndDetachedInTarget(CDORevisionDelta sourceDelta)
  {
    return new ChangedInSourceAndDetachedInTargetConflict(sourceDelta);
  }

  protected Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta)
  {
    return new ChangedInTargetAndDetachedInSourceConflict(targetDelta);
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

  private void take(Object data)
  {
    if (data instanceof Pair<?, ?>)
    {
      Pair<?, ?> pair = (Pair<?, ?>)data;
      takeNoPair(pair.getElement1());
      takeNoPair(pair.getElement2());
    }
    else
    {
      takeNoPair(data);
    }
  }

  private void takeNoPair(Object data)
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
    else if (data instanceof Conflict)
    {
      Conflict conflict = (Conflict)data;
      conflicts.put(conflict.getID(), conflict);
    }
    else if (data != null)
    {
      throw new IllegalArgumentException("Must be a CDORevision, a CDORevisionDelta, a CDOID, a Conflict or null: "
          + data);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ConflictException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    private DefaultCDOMerger merger;

    public ConflictException(DefaultCDOMerger merger)
    {
      super("Merger could not resolve all conflicts: " + merger.getConflicts());
      this.merger = merger;
    }

    public DefaultCDOMerger getMerger()
    {
      return merger;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Conflict
  {
    public abstract CDOID getID();
  }

  /**
   * @author Eike Stepper
   */
  public static class ChangedInSourceAndTargetConflict extends Conflict
  {
    private CDORevisionDelta targetDelta;

    private CDORevisionDelta sourceDelta;

    public ChangedInSourceAndTargetConflict(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
    {
      this.targetDelta = targetDelta;
      this.sourceDelta = sourceDelta;
    }

    @Override
    public CDOID getID()
    {
      return targetDelta.getID();
    }

    public CDORevisionDelta getTargetDelta()
    {
      return targetDelta;
    }

    public CDORevisionDelta getSourceDelta()
    {
      return sourceDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInSourceAndTarget[target={0}, source={1}]", targetDelta, sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ChangedInSourceAndDetachedInTargetConflict extends Conflict
  {
    private CDORevisionDelta sourceDelta;

    public ChangedInSourceAndDetachedInTargetConflict(CDORevisionDelta sourceDelta)
    {
      this.sourceDelta = sourceDelta;
    }

    @Override
    public CDOID getID()
    {
      return sourceDelta.getID();
    }

    public CDORevisionDelta getSourceDelta()
    {
      return sourceDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInSourceAndDetachedInTarget[source={1}]", sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ChangedInTargetAndDetachedInSourceConflict extends Conflict
  {
    private CDORevisionDelta targetDelta;

    public ChangedInTargetAndDetachedInSourceConflict(CDORevisionDelta targetDelta)
    {
      this.targetDelta = targetDelta;
    }

    @Override
    public CDOID getID()
    {
      return targetDelta.getID();
    }

    public CDORevisionDelta getTargetDelta()
    {
      return targetDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInTargetAndDetachedInSource[target={0}]", targetDelta); //$NON-NLS-1$
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
      InternalCDORevisionDelta result = new CDORevisionDeltaImpl(targetDelta, false);
      ChangedInSourceAndTargetConflict conflict = null;

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
          else
          {
            if (conflict == null)
            {
              conflict = new ChangedInSourceAndTargetConflict(new CDORevisionDeltaImpl(targetDelta, false),
                  new CDORevisionDeltaImpl(sourceDelta, false));
            }

            ((InternalCDORevisionDelta)conflict.getTargetDelta()).addFeatureDelta(targetFeatureDelta);
            ((InternalCDORevisionDelta)conflict.getSourceDelta()).addFeatureDelta(sourceFeatureDelta);
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

      if (result.isEmpty())
      {
        return conflict;
      }

      if (conflict != null)
      {
        return new Pair<InternalCDORevisionDelta, ChangedInSourceAndTargetConflict>(result, conflict);
      }

      return result;
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta)
    {
      return null;
    }

    /**
     * @author Eike Stepper
     */
    public static class ManyValued extends PerFeature
    {
      public ManyValued()
      {
      }

      @Override
      protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta,
          CDOFeatureDelta sourceFeatureDelta)
      {
        EStructuralFeature feature = targetFeatureDelta.getFeature();
        if (feature.isMany())
        {
          if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
          {
            CDOListFeatureDelta targetListDelta = (CDOListFeatureDelta)targetFeatureDelta;
            CDOListFeatureDelta sourceListDelta = (CDOListFeatureDelta)((InternalCDOFeatureDelta)sourceFeatureDelta)
                .copy();

            CDOListFeatureDelta result = createResult(feature);
            handleListDelta(result.getListChanges(), targetListDelta.getListChanges(), sourceListDelta.getListChanges());
            handleListDelta(result.getListChanges(), sourceListDelta.getListChanges(), null);
            return result;
          }
        }

        return null;
      }

      protected CDOListFeatureDelta createResult(EStructuralFeature feature)
      {
        return new CDOListFeatureDeltaImpl(feature);
      }

      protected void handleListDelta(List<CDOFeatureDelta> resultList, List<CDOFeatureDelta> listToHandle,
          List<CDOFeatureDelta> listToAdjust)
      {
        for (CDOFeatureDelta deltaToHandle : listToHandle)
        {
          if (deltaToHandle instanceof CDOAddFeatureDelta)
          {
            handleListDeltaAdd(resultList, (CDOAddFeatureDelta)deltaToHandle, listToAdjust);
          }
          else if (deltaToHandle instanceof CDORemoveFeatureDelta)
          {
            handleListDeltaRemove(resultList, (CDORemoveFeatureDelta)deltaToHandle, listToAdjust);
          }
          else if (deltaToHandle instanceof CDOMoveFeatureDelta)
          {
            handleListDeltaMove(resultList, (CDOMoveFeatureDelta)deltaToHandle, listToAdjust);
          }
          else
          {
            throw new UnsupportedOperationException("Unable to handle list feature conflict: " + deltaToHandle);
          }
        }
      }

      protected void handleListDeltaAdd(List<CDOFeatureDelta> resultList, CDOAddFeatureDelta addDelta,
          List<CDOFeatureDelta> listToAdjust)
      {
        resultList.add(addDelta);
        if (listToAdjust != null)
        {
          int index = addDelta.getIndex();
          for (CDOFeatureDelta deltaToAdjust : listToAdjust)
          {
            if (deltaToAdjust instanceof InternalCDOFeatureDelta.WithIndex)
            {
              InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)deltaToAdjust;
              withIndex.adjustAfterAddition(index);
            }
          }
        }
      }

      protected void handleListDeltaRemove(List<CDOFeatureDelta> resultList, CDORemoveFeatureDelta removeDelta,
          List<CDOFeatureDelta> listToAdjust)
      {
        resultList.add(removeDelta);
        if (listToAdjust != null)
        {
          int index = removeDelta.getIndex();
          for (CDOFeatureDelta deltaToAdjust : listToAdjust)
          {
            if (deltaToAdjust instanceof InternalCDOFeatureDelta.WithIndex)
            {
              InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)deltaToAdjust;
              withIndex.adjustAfterRemoval(index);
            }
          }
        }
      }

      protected void handleListDeltaMove(List<CDOFeatureDelta> resultList, CDOMoveFeatureDelta moveDelta,
          List<CDOFeatureDelta> listToAdjust)
      {
        resultList.add(moveDelta);
        if (listToAdjust != null)
        {
          int oldPosition = moveDelta.getOldPosition();
          int newPosition = moveDelta.getNewPosition();
          for (CDOFeatureDelta deltaToAdjust : listToAdjust)
          {
            if (deltaToAdjust instanceof InternalCDOFeatureDelta.WithIndex)
            {
              InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)deltaToAdjust;
              withIndex.adjustAfterRemoval(oldPosition);
              withIndex.adjustAfterAddition(newPosition);
            }
          }
        }
      }
    }
  }
}
