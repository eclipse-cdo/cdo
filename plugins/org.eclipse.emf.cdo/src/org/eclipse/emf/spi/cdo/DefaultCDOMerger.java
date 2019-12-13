/*
 * Copyright (c) 2010-2013, 2015-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSingleValueFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListIndexAffecting;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.WithIndex;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 */
public class DefaultCDOMerger implements CDOMerger
{
  private final ResolutionPreference resolutionPreference;

  private final ListOrdering listOrdering;

  private CDOChangeSetData result;

  private Map<CDOID, Conflict> conflicts;

  private Map<CDOID, Object> targetMap;

  private Map<CDOID, Object> sourceMap;

  public DefaultCDOMerger()
  {
    this(ResolutionPreference.NONE);
  }

  /**
   * @since 4.6
   */
  public DefaultCDOMerger(ListOrdering listOrdering)
  {
    this(ResolutionPreference.NONE, listOrdering);
  }

  /**
   * @since 4.2
   */
  public DefaultCDOMerger(ResolutionPreference resolutionPreference)
  {
    this(resolutionPreference, ListOrdering.ORDERED);
  }

  /**
   * @since 4.6
   */
  public DefaultCDOMerger(ResolutionPreference resolutionPreference, ListOrdering listOrdering)
  {
    CheckUtil.checkArg(resolutionPreference, "resolutionPreference");
    this.resolutionPreference = resolutionPreference;

    CheckUtil.checkArg(listOrdering, "listOrdering");
    this.listOrdering = listOrdering;
  }

  /**
   * @since 4.2
   */
  public final ResolutionPreference getResolutionPreference()
  {
    return resolutionPreference;
  }

  /**
   * @since 4.6
   */
  public ListOrdering getListOrdering()
  {
    return listOrdering;
  }

  public CDOChangeSetData getResult()
  {
    return result;
  }

  public Map<CDOID, Conflict> getConflicts()
  {
    return conflicts;
  }

  @Override
  public synchronized CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws ConflictException
  {
    result = new CDOChangeSetDataImpl();
    conflicts = CDOIDUtil.createMap();

    targetMap = createMap(target);
    sourceMap = createMap(source);

    Set<CDOID> taken = new HashSet<CDOID>();
    for (Entry<CDOID, Object> entry : targetMap.entrySet())
    {
      CDOID id = entry.getKey();
      Object targetData = entry.getValue();
      Object sourceData = sourceMap.get(id);

      if (merge(targetData, sourceData))
      {
        taken.add(id);
      }
    }

    for (Entry<CDOID, Object> entry : sourceMap.entrySet())
    {
      CDOID id = entry.getKey();
      if (taken.add(id))
      {
        Object sourceData = entry.getValue();
        Object targetData = targetMap.get(id);
        merge(targetData, sourceData);
      }
    }

    if (!conflicts.isEmpty())
    {
      throw new ConflictException("Merger could not resolve all conflicts: " + conflicts, this, result);
    }

    return result;
  }

  protected boolean merge(Object targetData, Object sourceData)
  {
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
      data = changedInSourceAndTarget((CDORevisionDelta)targetData, (CDORevisionDelta)sourceData);
    }
    else if (sourceData instanceof CDORevision && targetData instanceof CDORevision)
    {
      data = addedInSourceAndTarget((CDORevision)targetData, (CDORevision)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDOID)
    {
      data = changedInSourceAndDetachedInTarget((CDORevisionDelta)sourceData);
    }
    else if (targetData instanceof CDORevisionDelta && sourceData instanceof CDOID)
    {
      data = changedInTargetAndDetachedInSource((CDORevisionDelta)targetData);
    }

    return take(data);
  }

  protected Object addedInTarget(CDORevision revision)
  {
    return revision;
  }

  protected Object addedInSource(CDORevision revision)
  {
    return revision;
  }

  protected Object addedInSourceAndTarget(CDORevision targetRevision, CDORevision sourceRevision)
  {
    return targetRevision;
  }

  protected Object changedInTarget(CDORevisionDelta delta)
  {
    return delta;
  }

  protected Object detachedInTarget(CDOID id)
  {
    return id;
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
    switch (resolutionPreference)
    {
    case SOURCE_OVER_TARGET:
      return sourceDelta;

    case TARGET_OVER_SOURCE:
      return targetDelta;

    case NONE:
      return new ChangedInSourceAndTargetConflict(targetDelta, sourceDelta);

    default:
      throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
    }
  }

  protected Object changedInSourceAndDetachedInTarget(CDORevisionDelta sourceDelta)
  {
    switch (resolutionPreference)
    {
    case SOURCE_OVER_TARGET:
      return sourceDelta; // TODO Do we need to "recreate" the source revision as NEW?

    case TARGET_OVER_SOURCE:
      return sourceDelta.getID(); // Indicate detachment

    case NONE:
      return new ChangedInSourceAndDetachedInTargetConflict(sourceDelta);

    default:
      throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
    }
  }

  protected Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta)
  {
    switch (resolutionPreference)
    {
    case SOURCE_OVER_TARGET:
      return targetDelta.getID();

    case TARGET_OVER_SOURCE:
      return targetDelta; // TODO Do we need to "recreate" the target revision as NEW?

    case NONE:
      return new ChangedInTargetAndDetachedInSourceConflict(targetDelta);

    default:
      throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
    }
  }

  protected Map<CDOID, Object> getTargetMap()
  {
    return targetMap;
  }

  protected Map<CDOID, Object> getSourceMap()
  {
    return sourceMap;
  }

  private Map<CDOID, Object> createMap(CDOChangeSetData changeSetData)
  {
    Map<CDOID, Object> map = CDOIDUtil.createMap();
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

  private boolean take(Object data)
  {
    if (data instanceof Pair<?, ?>)
    {
      Pair<?, ?> pair = (Pair<?, ?>)data;
      boolean taken = takeNoPair(pair.getElement1());
      taken |= takeNoPair(pair.getElement2());
      return taken;
    }

    return takeNoPair(data);
  }

  private boolean takeNoPair(Object data)
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
      throw new IllegalArgumentException("Must be a CDORevision, a CDORevisionDelta, a CDOID, a Conflict or null: " + data);
    }
    else
    {
      return false;
    }

    return true;
  }

  /**
   * Enumerates the possible resolution preferences that can be used with a {@link DefaultCDOMerger}.
   *
   * @since 4.2
   * @author Eike Stepper
   */
  public static enum ResolutionPreference
  {
    NONE,

    @Deprecated
    SOURCE_OVER_TARGET,

    @Deprecated
    TARGET_OVER_SOURCE,

    @Deprecated
    DETACH_OVER_CHANGE,

    @Deprecated
    CHANGE_OVER_DETACH
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static abstract class Conflict
  {
    public Conflict()
    {
    }

    public abstract CDOID getID();
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
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
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
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
      return MessageFormat.format("ChangedInSourceAndDetachedInTarget[source={0}]", sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
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
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class PerFeature extends DefaultCDOMerger
  {
    public PerFeature()
    {
    }

    /**
     * @since 4.2
     */
    public PerFeature(ResolutionPreference resolutionPreference)
    {
      super(resolutionPreference);
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
          CDOFeatureDelta featureDelta = changedInTarget(targetFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta, null);
          }
        }
        else
        {
          CDOFeatureDelta featureDelta = changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta, null);
          }
          else
          {
            if (conflict == null)
            {
              ResolutionPreference resolutionPreference = getResolutionPreference();
              switch (resolutionPreference)
              {
              case SOURCE_OVER_TARGET:
                // TODO: implement DefaultCDOMerger.PerFeature.changedInSourceAndTarget(targetDelta, sourceDelta)
                throw new UnsupportedOperationException();

              case TARGET_OVER_SOURCE:
                // TODO: implement DefaultCDOMerger.PerFeature.changedInSourceAndTarget(targetDelta, sourceDelta)
                throw new UnsupportedOperationException();

              case NONE:
                conflict = new ChangedInSourceAndTargetConflict(new CDORevisionDeltaImpl(targetDelta, false), new CDORevisionDeltaImpl(sourceDelta, false));
                break;

              default:
                throw new IllegalStateException("Illegal resolution preference: " + resolutionPreference);
              }
            }

            ((InternalCDORevisionDelta)conflict.getTargetDelta()).addFeatureDelta(targetFeatureDelta, null);
            ((InternalCDORevisionDelta)conflict.getSourceDelta()).addFeatureDelta(sourceFeatureDelta, null);
          }
        }
      }

      for (CDOFeatureDelta sourceFeatureDelta : sourceMap.values())
      {
        EStructuralFeature feature = sourceFeatureDelta.getFeature();
        CDOFeatureDelta targetFeatureDelta = targetMap.get(feature);

        if (targetFeatureDelta == null)
        {
          CDOFeatureDelta featureDelta = changedInSource(sourceFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta, null);
          }
        }
      }

      if (result.isEmpty())
      {
        return conflict;
      }

      if (conflict != null)
      {
        return Pair.create(result, conflict);
      }

      return result;
    }

    /**
     * @return the result feature delta, or <code>null</code> to ignore the change.
     */
    protected CDOFeatureDelta changedInTarget(CDOFeatureDelta featureDelta)
    {
      return featureDelta;
    }

    /**
     * @return the result feature delta, or <code>null</code> to ignore the change.
     */
    protected CDOFeatureDelta changedInSource(CDOFeatureDelta featureDelta)
    {
      return featureDelta;
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta)
    {
      EStructuralFeature feature = targetFeatureDelta.getFeature();
      if (feature.isMany())
      {
        return changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      return changedInSourceAndTargetSingleValued(feature, targetFeatureDelta, sourceFeatureDelta);
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta)
    {
      return null;
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTargetSingleValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta)
    {
      if (targetFeatureDelta.isStructurallyEqual(sourceFeatureDelta))
      {
        return targetFeatureDelta;
      }

      return null;
    }

    /**
     * If the meaning of this type isn't clear, there really should be more of a description here...
     *
     * @author Eike Stepper
     */
    public static class ManyValued extends PerFeature
    {
      public ManyValued()
      {
      }

      /**
       * @since 4.2
       */
      public ManyValued(ResolutionPreference resolutionPreference)
      {
        super(resolutionPreference);
      }

      /**
       * @since 4.2
       */
      protected boolean treatAsUnique(EStructuralFeature feature)
      {
        return feature.isUnique();
      }

      @Override
      protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
          CDOFeatureDelta sourceFeatureDelta)
      {
        if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
        {
          List<CDOFeatureDelta> originalSourceDeltas = ((CDOListFeatureDelta)sourceFeatureDelta).getListChanges();
          List<CDOFeatureDelta> originalTargetDeltas = ((CDOListFeatureDelta)targetFeatureDelta).getListChanges();
          int originSize = ((CDOListFeatureDelta)targetFeatureDelta).getOriginSize();
          boolean treatAsUnique = treatAsUnique(feature);

          // Copy the target deltas and build a map from original indices to the relevant target deltas.
          List<CDOFeatureDelta> targetDeltas = new ArrayList<CDOFeatureDelta>();
          TargetCopyProcessor targetProcessor = new TargetCopyProcessor(treatAsUnique);
          copy(originalTargetDeltas, originSize, targetDeltas, targetProcessor);

          // Copy the source deltas and eliminate/convert the corresponding target deltas.
          CDOListFeatureDelta result = new CDOListFeatureDeltaImpl(feature, originSize);
          List<CDOFeatureDelta> sourceDeltas = result.getListChanges();
          Map<Object, Object> targetMap = targetProcessor.getTargetMap();
          SourceCopyProcessor sourceProcessor = new SourceCopyProcessor(targetDeltas, targetMap, targetProcessor.getTargetAdditions());
          copy(originalSourceDeltas, originSize, sourceDeltas, sourceProcessor);

          // Move the remaining target deltas to the end of the source delta list and adjust their indices accordingly.
          for (CDOFeatureDelta targetDelta : targetDeltas)
          {
            if (targetDelta != null)
            {
              int projectedIndex = (Integer)targetMap.get(targetDelta);

              if (targetDelta instanceof CDOSingleValueFeatureDeltaImpl)
              {
                CDOSingleValueFeatureDeltaImpl impl = (CDOSingleValueFeatureDeltaImpl)targetDelta;
                impl.setIndex(projectedIndex);
              }
              else
              {
                CDOMoveFeatureDeltaImpl impl = (CDOMoveFeatureDeltaImpl)targetDelta;
                int moveOffset = impl.getNewPosition() - impl.getOldPosition();
                impl.setNewPosition(projectedIndex + moveOffset);
                impl.setOldPosition(projectedIndex);
              }

              WithIndex targetDeltaWithIndex = (WithIndex)targetDelta;
              int sourceCount = sourceDeltas.size();

              for (int sourcePosition = 0; sourcePosition < sourceCount; sourcePosition++)
              {
                CDOFeatureDelta sourceDelta = sourceDeltas.get(sourcePosition);
                int sourceIndex = ((WithIndex)sourceDelta).getIndex();
                Type sourceType = sourceDelta.getType();

                switch (sourceType)
                {
                case ADD:
                  targetDeltaWithIndex.adjustAfterAddition(sourceIndex);
                  break;

                case REMOVE:
                  targetDeltaWithIndex.adjustAfterRemoval(sourceIndex);
                  break;

                case SET:
                  // Neutral; do nothing.
                  break;

                case MOVE:
                  targetDeltaWithIndex.adjustAfterMove(sourceIndex, ((CDOMoveFeatureDelta)sourceDelta).getNewPosition());
                  break;

                default:
                  throw new IllegalStateException("Illegal source type: " + sourceType);
                }
              }

              sourceDeltas.add(targetDelta);
            }
          }

          return result;
        }

        return super.changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      private static int projectIndex(List<CDOFeatureDelta> changes, int count, int index)
      {
        for (int i = count - 1; i >= 0; --i)
        {
          CDOFeatureDelta projectingDelta = changes.get(i);
          if (projectingDelta instanceof ListIndexAffecting)
          {
            index = ((ListIndexAffecting)projectingDelta).projectIndex(index);
          }
        }

        return index;
      }

      private static void copy(List<CDOFeatureDelta> featureDeltas, int originSize, List<CDOFeatureDelta> copyDeltas, CopyProcessor processor)
      {
        boolean first = true;

        for (CDOFeatureDelta featureDelta : featureDeltas)
        {
          if (first)
          {
            first = false;

            Type type = featureDelta.getType();
            if (type == Type.CLEAR || type == Type.UNSET)
            {
              EStructuralFeature feature = featureDelta.getFeature();
              for (int deltaPosition = 0; deltaPosition < originSize; deltaPosition++)
              {
                CDORemoveFeatureDelta copyDelta = new CDORemoveFeatureDeltaImpl(feature, 0);
                copyDeltas.add(copyDelta);

                processor.processRemoveExpansion(copyDeltas, copyDelta, deltaPosition);
              }

              continue;
            }
          }

          CDOFeatureDelta copyDelta = featureDelta.copy();
          copyDeltas.add(copyDelta);

          int deltaPosition = copyDeltas.size() - 1;
          processor.processDeltaCopy(copyDeltas, copyDelta, deltaPosition);
        }
      }

      /**
       * @author Eike Stepper
       */
      private interface CopyProcessor
      {
        public void processRemoveExpansion(List<CDOFeatureDelta> deltas, CDORemoveFeatureDelta delta, int deltaPosition);

        public void processDeltaCopy(List<CDOFeatureDelta> deltas, CDOFeatureDelta delta, int deltaPosition);
      }

      /**
       * @author Eike Stepper
       */
      private static final class TargetCopyProcessor implements CopyProcessor
      {
        private final Map<Object, Object> targetMap = new HashMap<Object, Object>();

        private final Map<Object, Integer> targetAdditions;

        public TargetCopyProcessor(boolean treatAsUnique)
        {
          targetAdditions = treatAsUnique ? new HashMap<Object, Integer>() : null;
        }

        public Map<Object, Object> getTargetMap()
        {
          return targetMap;
        }

        public Map<Object, Integer> getTargetAdditions()
        {
          return targetAdditions;
        }

        @Override
        public void processRemoveExpansion(List<CDOFeatureDelta> targetDeltas, CDORemoveFeatureDelta targetDelta, int deltaPosition)
        {
          int projectedIndex = deltaPosition;
          targetMap.put(targetDelta, projectedIndex);
          targetMap.put(deltaPosition, projectedIndex);
        }

        @Override
        public void processDeltaCopy(List<CDOFeatureDelta> targetDeltas, CDOFeatureDelta targetDelta, int deltaPosition)
        {
          int deltaIndex = ((WithIndex)targetDelta).getIndex();
          int projectedIndex = projectIndex(targetDeltas, deltaPosition, deltaIndex);

          targetMap.put(targetDelta, projectedIndex);

          Object positions = targetMap.get(projectedIndex);
          if (positions == null)
          {
            targetMap.put(projectedIndex, deltaPosition);
          }
          else if (positions instanceof Integer)
          {
            List<Integer> list = new ArrayList<Integer>(2);
            list.add((Integer)positions);
            list.add(deltaPosition);
            targetMap.put(projectedIndex, list);
          }
          else
          {
            @SuppressWarnings("unchecked")
            List<Integer> list = (List<Integer>)positions;
            list.add(deltaPosition);
          }

          if (targetAdditions != null)
          {
            Type targetType = targetDelta.getType();
            switch (targetType)
            {
            case ADD:
            case SET:
              targetAdditions.put(((CDOSingleValueFeatureDeltaImpl)targetDelta).getValue(), deltaPosition);
              break;

            default:
              break;
            }
          }
        }
      }

      /**
       * @author Eike Stepper
       */
      private static final class SourceCopyProcessor implements CopyProcessor
      {
        private final List<CDOFeatureDelta> targetDeltas;

        private final Map<Object, Object> targetMap;

        private final Map<Object, Integer> targetAdditions;

        public SourceCopyProcessor(List<CDOFeatureDelta> targetDeltas, Map<Object, Object> targetMap, Map<Object, Integer> targetAdditions)
        {
          this.targetDeltas = targetDeltas;
          this.targetMap = targetMap;
          this.targetAdditions = targetAdditions;
        }

        @Override
        public void processRemoveExpansion(List<CDOFeatureDelta> sourceDeltas, CDORemoveFeatureDelta sourceDelta, int deltaPosition)
        {
          processDeltaCopy(sourceDeltas, sourceDelta, deltaPosition);
        }

        @Override
        public void processDeltaCopy(List<CDOFeatureDelta> sourceDeltas, CDOFeatureDelta sourceDelta, int deltaPosition)
        {
          Type sourceType = sourceDelta.getType();

          switch (sourceType)
          {
          case ADD:
            processDuplicateValues(sourceDelta);
            break;

          case REMOVE:
            processSourceRemove(sourceDeltas, sourceDelta, deltaPosition);
            break;

          case SET:
            processSourceRemove(sourceDeltas, sourceDelta, deltaPosition);
            processDuplicateValues(sourceDelta);
            break;

          case MOVE:
            // Neutral; do nothing.
            break;

          default:
            throw new IllegalStateException("Illegal source type: " + sourceType);
          }
        }

        private void processSourceRemove(List<CDOFeatureDelta> sourceDeltas, CDOFeatureDelta sourceDelta, int deltaPosition)
        {
          int projectedIndex = projectIndex(sourceDeltas, deltaPosition, ((WithIndex)sourceDelta).getIndex());
          Object targetPositions = targetMap.get(projectedIndex);
          if (targetPositions != null)
          {
            if (targetPositions instanceof Integer)
            {
              Integer targetPosition = (Integer)targetPositions;
              processSourceRemove(targetPosition);
            }
            else
            {
              @SuppressWarnings("unchecked")
              List<Integer> list = (List<Integer>)targetPositions;
              for (Integer targetPosition : list)
              {
                processSourceRemove(targetPosition);
              }
            }
          }
        }

        private void processSourceRemove(Integer targetPosition)
        {
          CDOFeatureDelta targetDelta = targetDeltas.get(targetPosition);
          Type targetType = targetDelta.getType();

          switch (targetType)
          {
          case ADD:
            // Neutral; do nothing.
            break;

          case REMOVE:
          {
            CDORemoveFeatureDelta targetRemoveDelta = (CDORemoveFeatureDelta)targetDelta;
            int removedIndex = targetRemoveDelta.getIndex();

            targetDeltas.set(targetPosition, null);
            adjustRemainingDeltas(targetPosition, removedIndex, CDOFeatureDelta.NO_INDEX);
            break;
          }

          case SET:
          {
            CDOSetFeatureDelta targetSetDelta = (CDOSetFeatureDelta)targetDelta;
            int removedIndex = targetSetDelta.getIndex();

            targetDelta = new CDOAddFeatureDeltaImpl(targetSetDelta.getFeature(), removedIndex, targetSetDelta.getValue());
            targetDeltas.set(targetPosition, targetDelta);
            adjustRemainingDeltas(targetPosition, removedIndex, CDOFeatureDelta.NO_INDEX);
            break;
          }

          case MOVE:
          {
            CDOMoveFeatureDelta targetMoveDelta = (CDOMoveFeatureDelta)targetDelta;
            int removedIndex = targetMoveDelta.getOldPosition();
            int addedIndex = targetMoveDelta.getNewPosition();

            targetDeltas.set(targetPosition, null);
            adjustRemainingDeltas(targetPosition, removedIndex, addedIndex);
            break;
          }

          default:
            throw new IllegalStateException("Illegal source type: " + targetType);
          }
        }

        private void processDuplicateValues(CDOFeatureDelta sourceDelta)
        {
          if (targetAdditions != null)
          {
            Object sourceValue = ((CDOSingleValueFeatureDeltaImpl)sourceDelta).getValue();
            Integer targetPosition = targetAdditions.get(sourceValue);
            if (targetPosition != null)
            {
              WithIndex targetDelta = (WithIndex)targetDeltas.set(targetPosition, null);
              Object oldMapping = targetMap.remove(targetDelta);

              int addedIndex = targetDelta.getIndex();

              if (targetDelta instanceof CDOSetFeatureDelta)
              {
                CDOSetFeatureDelta targetSetDelta = (CDOSetFeatureDelta)targetDelta;

                CDORemoveFeatureDelta targetRemoveDelta = new CDORemoveFeatureDeltaImpl(targetSetDelta.getFeature(), addedIndex);
                targetDeltas.set(targetPosition, targetRemoveDelta);
                targetMap.put(targetRemoveDelta, oldMapping);
              }

              adjustRemainingDeltas(targetPosition, CDOFeatureDelta.NO_INDEX, addedIndex);
            }
          }
        }

        private void adjustRemainingDeltas(int targetPosition, int removedIndex, int addedIndex)
        {
          for (int i = targetPosition + 1; i < targetDeltas.size(); i++)
          {
            CDOFeatureDelta remainingDelta = targetDeltas.get(i);
            if (remainingDelta != null)
            {
              WithIndex remainingWithIndex = (WithIndex)remainingDelta;

              if (removedIndex != CDOFeatureDelta.NO_INDEX)
              {
                remainingWithIndex.adjustAfterAddition(removedIndex); // Undo the target REMOVE.
              }

              if (addedIndex != CDOFeatureDelta.NO_INDEX)
              {
                remainingWithIndex.adjustAfterRemoval(addedIndex); // Undo the target ADD.
              }
            }
          }
        }
      }
    }

    /**
     * If the meaning of this type isn't clear, there really should be more of a description here...
     *
     * @author Eike Stepper
     * @since 4.6
     */
    public static class ManyValuedOld extends PerFeature
    {
      public ManyValuedOld()
      {
      }

      /**
       * @since 4.2
       */
      public ManyValuedOld(ResolutionPreference resolutionPreference)
      {
        super(resolutionPreference);
      }

      /**
       * @since 4.2
       */
      protected boolean treatAsUnique(EStructuralFeature feature)
      {
        return feature.isUnique();
      }

      @Override
      protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature, CDOFeatureDelta targetFeatureDelta,
          CDOFeatureDelta sourceFeatureDelta)
      {
        if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
        {
          // Initialize work lists with virtual elements
          int originSize = ((CDOListFeatureDelta)sourceFeatureDelta.copy()).getOriginSize();
          BasicEList<Element> ancestorList = new BasicEList<Element>(originSize);
          PerSide<BasicEList<Element>> listPerSide = new PerSide<BasicEList<Element>>();

          initWorkLists(originSize, ancestorList, listPerSide);

          // Apply list changes to source and target work lists
          PerSide<List<CDOFeatureDelta>> changesPerSide = new PerSide<List<CDOFeatureDelta>>(copyListChanges(sourceFeatureDelta),
              copyListChanges(targetFeatureDelta));
          Map<Object, List<Element>> additions = new HashMap<Object, List<Element>>();
          Map<CDOFeatureDelta, Element> allElements = new HashMap<CDOFeatureDelta, Element>();

          applyChangesToWorkList(Side.SOURCE, listPerSide, changesPerSide, allElements, additions);
          applyChangesToWorkList(Side.TARGET, listPerSide, changesPerSide, allElements, additions);

          // Pick changes from source and target sides into the merge result
          CDOListFeatureDelta result = new CDOListFeatureDeltaImpl(feature, originSize);
          List<CDOFeatureDelta> resultChanges = result.getListChanges();

          pickChangesIntoResult(Side.SOURCE, feature, ancestorList, changesPerSide, allElements, additions, resultChanges);
          pickChangesIntoResult(Side.TARGET, feature, ancestorList, changesPerSide, allElements, additions, resultChanges);

          return result;
        }

        return super.changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      private void initWorkLists(int originSize, BasicEList<Element> ancestorList, PerSide<BasicEList<Element>> listPerSide)
      {
        BasicEList<Element> sourceList = new BasicEList<Element>(originSize);
        BasicEList<Element> targetList = new BasicEList<Element>(originSize);

        for (int i = 0; i < originSize; i++)
        {
          Element element = new Element(i);
          ancestorList.add(element);
          sourceList.add(element);
          targetList.add(element);
        }

        listPerSide.set(Side.SOURCE, sourceList);
        listPerSide.set(Side.TARGET, targetList);
      }

      private List<CDOFeatureDelta> copyListChanges(CDOFeatureDelta featureDelta)
      {
        CDOListFeatureDelta listFeatureDelta = (CDOListFeatureDelta)featureDelta.copy();
        List<CDOFeatureDelta> copy = listFeatureDelta.getListChanges();

        if (!copy.isEmpty())
        {
          CDOFeatureDelta.Type firstType = copy.get(0).getType();
          if (firstType == Type.CLEAR || firstType == Type.UNSET)
          {
            copy.remove(0);

            List<CDOFeatureDelta> expandedDeltas = expandClearDelta(listFeatureDelta);
            copy.addAll(0, expandedDeltas);
          }
        }

        return copy;
      }

      private List<CDOFeatureDelta> expandClearDelta(CDOListFeatureDelta listFeatureDelta)
      {
        EStructuralFeature feature = listFeatureDelta.getFeature();
        int originSize = listFeatureDelta.getOriginSize();
        List<CDOFeatureDelta> expandedDeltas = new ArrayList<CDOFeatureDelta>(originSize);

        for (int i = 0; i < originSize; i++)
        {
          expandedDeltas.add(new CDORemoveFeatureDeltaImpl(feature, 0));
        }

        return expandedDeltas;
      }

      private void applyChangesToWorkList(Side side, PerSide<BasicEList<Element>> listPerSide, PerSide<List<CDOFeatureDelta>> changesPerSide,
          Map<CDOFeatureDelta, Element> allElements, Map<Object, List<Element>> additions)
      {
        BasicEList<Element> list = listPerSide.get(side);
        List<CDOFeatureDelta> changes = changesPerSide.get(side);
        for (CDOFeatureDelta change : changes)
        {
          Type changeType = change.getType();
          switch (changeType)
          {
          case ADD:
          {
            CDOAddFeatureDelta addChange = (CDOAddFeatureDelta)change;

            int index = addChange.getIndex();
            if (index > list.size())
            {
              index = list.size();
            }

            Element element = new Element(-1);
            element.set(side, addChange);
            allElements.put(addChange, element);

            list.add(index, element);
            rememberAddition(addChange.getValue(), element, additions);
            break;
          }

          case REMOVE:
          {
            CDORemoveFeatureDelta removeChange = (CDORemoveFeatureDelta)change;

            Element element = list.remove(removeChange.getIndex());
            element.set(side, removeChange);
            allElements.put(removeChange, element);
            break;
          }

          case SET:
          {
            CDOSetFeatureDelta setChange = (CDOSetFeatureDelta)change;

            Element newElement = new Element(-1);
            newElement.set(side, setChange);
            rememberAddition(setChange.getValue(), newElement, additions);

            Element oldElement = list.set(setChange.getIndex(), newElement);
            oldElement.set(side, setChange);
            allElements.put(setChange, oldElement);
            break;
          }

          case MOVE:
          {
            CDOMoveFeatureDelta moveChange = (CDOMoveFeatureDelta)change;

            Element element = list.move(moveChange.getNewPosition(), moveChange.getOldPosition());
            element.set(side, moveChange);
            allElements.put(moveChange, element);
            break;
          }

          case CLEAR:
          case UNSET:
            // These deltas should have been replaced by multiple REMOVE deltas in copyListChanges()
            throw new IllegalStateException("Unhandled change type: " + changeType);

          default:
            throw new IllegalStateException("Illegal change type: " + changeType);
          }
        }
      }

      private void rememberAddition(Object value, Element element, Map<Object, List<Element>> additions)
      {
        List<Element> additionsList = additions.get(value);
        if (additionsList == null)
        {
          additionsList = new ArrayList<Element>(1);
          additions.put(value, additionsList);
        }

        additionsList.add(element);
      }

      private void pickChangesIntoResult(Side side, EStructuralFeature feature, BasicEList<Element> ancestorList, PerSide<List<CDOFeatureDelta>> changesPerSide,
          Map<CDOFeatureDelta, Element> allElements, Map<Object, List<Element>> additions, List<CDOFeatureDelta> result)
      {
        List<CDOFeatureDelta> changes = changesPerSide.get(side);
        for (CDOFeatureDelta change : changes)
        {
          Type changeType = change.getType();
          switch (changeType)
          {
          case ADD:
          {
            CDOAddFeatureDeltaImpl addChange = (CDOAddFeatureDeltaImpl)change;
            result.add(addChange);

            int sideIndex = addChange.getIndex();
            int ancestorIndex = sideIndex;

            int ancestorEnd = ancestorList.size();
            if (ancestorIndex > ancestorEnd)
            {
              // TODO Better way to adjust ancestor indexes?
              ancestorIndex = ancestorEnd;
              addChange.setIndex(ancestorIndex);
            }

            Element newElement = allElements.get(addChange);
            ancestorList.add(ancestorIndex, newElement);

            if (treatAsUnique(feature))
            {
              // Detect and remove corresponding AddDeltas from the other side
              Object value = addChange.getValue();
              List<Element> elementsToAdd = additions.get(value);
              if (elementsToAdd != null)
              {
                for (Element element : elementsToAdd)
                {
                  CDOAddFeatureDelta otherAdd = (CDOAddFeatureDelta)element.get(other(side));
                  if (otherAdd != null)
                  {
                    element.set(other(side), null);

                    // Not taking an AddDelta has the same effect on indexes as a removal of the element
                    List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                    int otherIndex = otherAdd.getIndex();
                    adjustAfterRemoval(otherChanges, otherIndex, addChange);
                  }
                }
              }
            }

            break;
          }

          case REMOVE:
          {
            CDORemoveFeatureDeltaImpl removeChange = (CDORemoveFeatureDeltaImpl)change;
            result.add(removeChange);

            Element removedElement = allElements.get(removeChange);
            int ancestorIndex = ancestorList.indexOf(removedElement);
            removeChange.setIndex(ancestorIndex);
            ancestorList.remove(ancestorIndex);

            // Detect and remove a potential duplicate RemoveDelta from the other side
            CDOFeatureDelta otherChange = removedElement.get(other(side));
            if (otherChange != null)
            {
              Type otherChangeType = otherChange.getType();
              switch (otherChangeType)
              {
              case REMOVE:
              {
                CDORemoveFeatureDelta otherRemove = (CDORemoveFeatureDelta)otherChange;
                removedElement.set(other(side), null);

                // Not taking a RemoveDelta has the same effect on indexes as an addition of the element
                List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                int otherIndex = otherRemove.getIndex();
                adjustAfterAddition(otherChanges, otherIndex, otherRemove);
                break;
              }

              case MOVE:
              {
                CDOMoveFeatureDelta otherMove = (CDOMoveFeatureDelta)otherChange;
                removedElement.set(other(side), null);

                // Not taking a MoveDelta has the same effect on indexes as a reverse move of the element
                List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                int otherOldPosition = otherMove.getOldPosition();
                int otherNewPosition = otherMove.getNewPosition();
                adjustAfterMove(otherChanges, otherOldPosition, otherNewPosition, otherMove);
                break;
              }

              default:
                throw new IllegalStateException("Unexpected change type: " + otherChangeType);
              }
            }

            break;
          }

          case SET:
          {
            throw new IllegalStateException("Unhandled change type: " + changeType);
            // CDOSetFeatureDelta setChange = (CDOSetFeatureDelta)change;
            // break;
          }

          case MOVE:
          {
            CDOMoveFeatureDeltaImpl moveChange = (CDOMoveFeatureDeltaImpl)change;
            int sideOldPosition = moveChange.getOldPosition();
            int sideNewPosition = moveChange.getNewPosition();

            Element movedElement = allElements.get(moveChange);
            CDOFeatureDelta otherChange = movedElement.get(other(side));

            if (otherChange != null)
            {
              Type otherChangeType = otherChange.getType();
              switch (otherChangeType)
              {
              case REMOVE:
              {
                // Prioritize the RemoveDelta of the other side, delete the MoveDelta from this side
                adjustAfterMove(changes, sideOldPosition, sideNewPosition, moveChange);
                movedElement.set(side, null);
                return;
              }

              case MOVE:
              {
                CDOMoveFeatureDelta otherMove = (CDOMoveFeatureDelta)otherChange;
                movedElement.set(other(side), null);

                // Not taking a MoveDelta has the same effect on indexes as a reverse move of the element
                List<CDOFeatureDelta> otherChanges = changesPerSide.get(other(side));
                int otherOldPosition = otherMove.getOldPosition();
                int otherNewPosition = otherMove.getNewPosition();
                adjustAfterMove(otherChanges, otherOldPosition, otherNewPosition, otherMove);
                movedElement.set(other(side), null);
                break;
              }

              default:
                throw new IllegalStateException("Unexpected change type: " + otherChangeType);
              }
            }

            int positionDelta = sideNewPosition - sideOldPosition;
            int ancestorOldPosition = ancestorList.indexOf(movedElement);
            int ancestorNewPosition = ancestorOldPosition + positionDelta;
            if (ancestorNewPosition < 0)
            {
              ancestorNewPosition = 0;
            }

            int ancestorEnd = ancestorList.size() - 1;
            if (ancestorNewPosition > ancestorEnd)
            {
              ancestorNewPosition = ancestorEnd;
            }

            moveChange.setOldPosition(ancestorOldPosition);
            moveChange.setNewPosition(ancestorNewPosition);
            result.add(moveChange);

            ancestorList.move(ancestorNewPosition, ancestorOldPosition);
            break;
          }

          case CLEAR:
          case UNSET:

          default:
            throw new IllegalStateException("Illegal change type: " + changeType);
          }
        }
      }

      private static void adjustAfterAddition(List<CDOFeatureDelta> list, int index, CDOFeatureDelta deltaToRemove)
      {
        for (Iterator<CDOFeatureDelta> it = list.iterator(); it.hasNext();)
        {
          CDOFeatureDelta delta = it.next();
          if (delta == deltaToRemove)
          {
            it.remove();
            continue;
          }

          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterAddition(index);
          }
        }
      }

      private static void adjustAfterRemoval(List<CDOFeatureDelta> list, int index, CDOFeatureDelta deltaToRemove)
      {
        for (Iterator<CDOFeatureDelta> it = list.iterator(); it.hasNext();)
        {
          CDOFeatureDelta delta = it.next();
          if (delta == deltaToRemove)
          {
            it.remove();
            continue;
          }

          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterRemoval(index);
          }
        }
      }

      private static void adjustAfterMove(List<CDOFeatureDelta> list, int oldPosition, int newPosition, CDOFeatureDelta deltaToRemove)
      {
        for (Iterator<CDOFeatureDelta> it = list.iterator(); it.hasNext();)
        {
          CDOFeatureDelta delta = it.next();
          if (delta == deltaToRemove)
          {
            it.remove();
            continue;
          }

          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterRemoval(oldPosition);
            withIndex.adjustAfterAddition(newPosition);
          }
        }
      }

      /**
       * @since 4.2
       */
      protected static Side other(Side side)
      {
        if (side == Side.SOURCE)
        {
          return Side.TARGET;
        }

        return Side.SOURCE;
      }

      /**
       * Enumerates the possible sides of a merge, i.e., {@link #SOURCE} and {@link #TARGET}.
       *
       * @author Eike Stepper
       * @since 4.2
       */
      public static enum Side
      {
        SOURCE, TARGET
      }

      /**
       * Holds data for the source and target sides.
       *
       * @author Eike Stepper
       * @since 4.2
       */
      public static class PerSide<T>
      {
        private T source;

        private T target;

        public PerSide()
        {
        }

        public PerSide(T source, T target)
        {
          this.source = source;
          this.target = target;
        }

        public final T get(Side side)
        {
          if (side == Side.SOURCE)
          {
            return source;
          }

          return target;
        }

        public final void set(Side side, T value)
        {
          if (side == Side.SOURCE)
          {
            this.source = value;
          }
          else
          {
            this.target = value;
          }
        }

        @Override
        public String toString()
        {
          return "source: " + source + "\ntarget: " + target;
        }
      }

      /**
       * A virtual list element to establish unique relations between ancestor, source and target sides.
       *
       * @author Eike Stepper
       * @since 4.2
       */
      public static final class Element extends PerSide<CDOFeatureDelta>
      {
        private final int ancestorIndex;

        public Element(int ancestorIndex)
        {
          this.ancestorIndex = ancestorIndex;
        }

        public int getAncestorIndex()
        {
          return ancestorIndex;
        }

        @Override
        public String toString()
        {
          return String.valueOf(ancestorIndex);
        }
      }
    }
  }
}
