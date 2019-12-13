/*
 * Copyright (c) 2010-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionValueVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Map;
import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOMergingConflictResolver extends AbstractChangeSetsConflictResolver
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, CDOViewImpl.class);

  private CDOMerger merger;

  private long lastNonConflictTimeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

  private boolean conflict;

  public CDOMergingConflictResolver(CDOMerger merger)
  {
    this.merger = merger;
  }

  /**
   * @param ensureRemoteNotifications boolean to disable the use of {@link CDOAdapterPolicy} to ensure remote changes reception for conflict resolution, true by default. Can be disabled to limit network traffic when {@link PassiveUpdateMode} is enabled and in {@link PassiveUpdateMode#CHANGES} or {@link PassiveUpdateMode#ADDITIONS}
   * @since 4.4
   */
  public CDOMergingConflictResolver(CDOMerger merger, boolean ensureRemoteNotifications)
  {
    super(ensureRemoteNotifications);
    this.merger = merger;
  }

  /**
   * @since 4.2
   */
  public CDOMergingConflictResolver(DefaultCDOMerger.ResolutionPreference resolutionPreference)
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued(resolutionPreference));
  }

  /**
   * @param ensureRemoteNotifications boolean to disable the use of {@link CDOAdapterPolicy} to ensure remote changes reception for conflict resolution, true by default. Can be disabled to limit network traffic when {@link PassiveUpdateMode} is enabled and in {@link PassiveUpdateMode#CHANGES} or {@link PassiveUpdateMode#ADDITIONS}
   * @since 4.4
   */
  public CDOMergingConflictResolver(DefaultCDOMerger.ResolutionPreference resolutionPreference, boolean ensureRemoteNotifications)
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued(resolutionPreference), ensureRemoteNotifications);
  }

  /**
   * @since 4.4
   */
  public CDOMergingConflictResolver()
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued());
  }

  /**
   * @param ensureRemoteNotifications boolean to disable the use of {@link CDOAdapterPolicy} to ensure remote changes reception for conflict resolution, true by default. Can be disabled to limit network traffic when {@link PassiveUpdateMode} is enabled and in {@link PassiveUpdateMode#CHANGES} or {@link PassiveUpdateMode#ADDITIONS}
   * @since 4.4
   */
  public CDOMergingConflictResolver(boolean ensureRemoteNotifications)
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued(), ensureRemoteNotifications);
  }

  public CDOMerger getMerger()
  {
    return merger;
  }

  /**
   * @since 4.4
   */
  public long getLastNonConflictTimeStamp()
  {
    return lastNonConflictTimeStamp;
  }

  /**
   * @since 4.4
   */
  public boolean isConflict()
  {
    return conflict;
  }

  @Override
  public void resolveConflicts(Set<CDOObject> conflicts)
  {
    CDOChangeSet remoteChangeSet = getRemoteChangeSet();
    while (remoteChangeSet != null)
    {
      resolveConflicts(conflicts, remoteChangeSet);
      remoteChangeSet = getRemoteChangeSet();
    }
  }

  /**
   * @since 4.4
   */
  protected void resolveConflicts(Set<CDOObject> conflicts, CDOChangeSet remoteChangeSet)
  {
    CDOChangeSet localChangeSet = getLocalChangeSet();
    CDOChangeSetData result;

    try
    {
      result = merger.merge(localChangeSet, remoteChangeSet);

      if (!conflict)
      {
        lastNonConflictTimeStamp = getRemoteTimeStamp();
      }
    }
    catch (ConflictException ex)
    {
      result = handleConflict(ex.getResult());
      if (result == null)
      {
        conflict = true;
        return;
      }
    }

    updateTransactionWithResult(conflicts, remoteChangeSet, result);
  }

  /**
   * @since 4.4
   */
  protected CDOChangeSetData handleConflict(CDOChangeSetData result)
  {
    return null;
  }

  @Override
  protected void hookTransaction(CDOTransaction transaction)
  {
    lastNonConflictTimeStamp = transaction.getSession().getLastUpdateTime();
    conflict = false;

    super.hookTransaction(transaction);
  }

  @Override
  protected void transactionCommitted(CDOCommitContext commitContext)
  {
    super.transactionCommitted(commitContext);
    resetConflict();
  }

  @Override
  protected void transactionRolledBack()
  {
    super.transactionRolledBack();
    resetConflict();
  }

  private void resetConflict()
  {
    lastNonConflictTimeStamp = getTransaction().getLastUpdateTime();
    conflict = false;
  }

  private void updateTransactionWithResult(Set<CDOObject> conflicts, CDOChangeSet remoteChangeSet, CDOChangeSetData result)
  {
    InternalCDOTransaction transaction = (InternalCDOTransaction)getTransaction();
    InternalCDOSavepoint savepoint = transaction.getLastSavepoint();

    Map<InternalCDOObject, InternalCDORevision> cleanRevisions = transaction.getCleanRevisions();
    final ObjectsMapUpdater detachedObjectsUpdater = new ObjectsMapUpdater(savepoint.getDetachedObjects());

    Map<CDOID, CDORevisionDelta> remoteDeltas = getRemoteDeltas(remoteChangeSet);

    for (CDORevisionKey key : result.getChangedObjects())
    {
      InternalCDORevisionDelta resultDelta = (InternalCDORevisionDelta)key;
      CDOID id = resultDelta.getID();

      InternalCDOObject object = (InternalCDOObject)transaction.getObject(id, false);
      if (object != null && conflicts.contains(object))
      {
        // TODO Should the merge result be compared to non-conflicting revisions, too?

        int newVersion = computeNewVersion(object);
        InternalCDORevision cleanRevision = cleanRevisions.get(object);
        InternalCDORevision newLocalRevision = computeNewLocalRevision(resultDelta, newVersion, cleanRevision);

        // Adjust local object
        object.cdoInternalSetRevision(newLocalRevision);

        final InternalCDORevision newCleanRevision = computeNewCleanRevision(remoteDeltas, id, newVersion, cleanRevision);

        // Compute new local delta
        InternalCDORevisionDelta newLocalDelta = newLocalRevision.compare(newCleanRevision);
        if (newLocalDelta.isEmpty())
        {
          CDOSavepoint currentCDOSavePoint = savepoint;
          while (currentCDOSavePoint != null)
          {
            currentCDOSavePoint.getRevisionDeltas2().remove(id);
            currentCDOSavePoint.getDirtyObjects().remove(id);
            currentCDOSavePoint = currentCDOSavePoint.getPreviousSavepoint();
          }

          object.cdoInternalSetState(CDOState.CLEAN);
        }
        else
        {
          newLocalDelta.setTarget(null);
          CDOSavepoint currentCDOSavePoint = savepoint;
          while (currentCDOSavePoint != null)
          {
            currentCDOSavePoint.getRevisionDeltas2().put(id, newLocalDelta);
            currentCDOSavePoint.getDirtyObjects().put(id, object);
            currentCDOSavePoint = currentCDOSavePoint.getPreviousSavepoint();
          }

          object.cdoInternalSetState(CDOState.DIRTY);
          cleanRevisions.put(object, newCleanRevision);
          updateObjects(newCleanRevision, newLocalDelta, detachedObjectsUpdater);
        }
        object.cdoInternalPostLoad();
      }
    }
  }

  private int computeNewVersion(InternalCDOObject object)
  {
    InternalCDORevision localRevision = object.cdoRevision();
    int newVersion = localRevision.getVersion() + 1;
    return newVersion;
  }

  private InternalCDORevision computeNewLocalRevision(InternalCDORevisionDelta resultDelta, int newVersion, InternalCDORevision cleanRevision)
  {
    InternalCDORevision newLocalRevision = cleanRevision.copy();
    newLocalRevision.setVersion(newVersion);
    resultDelta.applyTo(newLocalRevision);
    return newLocalRevision;
  }

  private InternalCDORevision computeNewCleanRevision(Map<CDOID, CDORevisionDelta> remoteDeltas, CDOID id, int newVersion, InternalCDORevision cleanRevision)
  {
    CDORevisionDelta remoteDelta = remoteDeltas.get(id);
    if (remoteDelta != null)
    {
      InternalCDORevision newCleanRevision = cleanRevision.copy();
      newCleanRevision.setVersion(newVersion);
      remoteDelta.applyTo(newCleanRevision);
      return newCleanRevision;
    }

    return cleanRevision;
  }

  private void updateObjects(final InternalCDORevision newCleanRevision, InternalCDORevisionDelta newLocalDelta, final ObjectsMapUpdater detachedObjectsUpdater)
  {
    newLocalDelta.accept(new CDOFeatureDeltaVisitorImpl()
    {
      @Override
      public void visit(CDOAddFeatureDelta delta)
      {
        // recurse(newObjectsUpdater, (CDOID)delta.getValue());
      }

      @Override
      public void visit(CDOClearFeatureDelta delta)
      {
        // TODO Only for reference features?
        CDOList list = newCleanRevision.getListOrNull(delta.getFeature());
        if (list != null)
        {
          for (Object id : list)
          {
            recurse(detachedObjectsUpdater, (CDOID)id);
          }
        }
      }

      @Override
      public void visit(CDORemoveFeatureDelta delta)
      {
        // TODO Only for reference features?
        recurse(detachedObjectsUpdater, (CDOID)delta.getValue());
      }

      @Override
      public void visit(CDOSetFeatureDelta delta)
      {
        // recurse(detachedObjectsUpdater, (CDOID)delta.getOldValue());
        // recurse(newObjectsUpdater, (CDOID)delta.getValue());
      }

      @Override
      public void visit(CDOUnsetFeatureDelta delta)
      {
        // TODO: implement CDOMergingConflictResolver.resolveConflicts(...).new CDOFeatureDeltaVisitorImpl()
      }

      private void recurse(final ObjectsMapUpdater objectsUpdater, CDOID id)
      {
        CDOObject object = objectsUpdater.update(id);
        if (object != null)
        {
          InternalCDORevision revision = (InternalCDORevision)object.cdoRevision();
          if (revision != null)
          {
            revision.accept(new CDORevisionValueVisitor()
            {
              @Override
              public void visit(EStructuralFeature feature, Object value, int index)
              {
                recurse(objectsUpdater, (CDOID)value);
              }
            }, EMFUtil.CONTAINMENT_REFERENCES);
          }
        }
      }
    }, EMFUtil.CONTAINMENT_REFERENCES);
  }

  private Map<CDOID, CDORevisionDelta> getRemoteDeltas(CDOChangeSet remoteChangeSet)
  {
    Map<CDOID, CDORevisionDelta> remoteDeltas = CDOIDUtil.createMap();
    for (CDORevisionKey key : remoteChangeSet.getChangedObjects())
    {
      if (key instanceof CDORevisionDelta)
      {
        CDORevisionDelta delta = (CDORevisionDelta)key;
        remoteDeltas.put(key.getID(), delta);
      }
      else if (TRACER.isEnabled())
      {
        TRACER.format("Not a CDORevisionDelta: {0}", key); //$NON-NLS-1$
      }
    }

    return remoteDeltas;
  }

  /**
   * @author Eike Stepper
   */
  private final class ObjectsMapUpdater
  {
    private final Map<CDOID, CDOObject> map;

    private final Map<CDOID, CDOObject> mapCopy;

    public ObjectsMapUpdater(Map<CDOID, CDOObject> map)
    {
      mapCopy = CDOIDUtil.createMap(map);
      map.clear();

      this.map = map;
    }

    public CDOObject update(CDOID id)
    {
      CDOObject object = mapCopy.get(id);
      if (object == null)
      {
        object = getTransaction().getObject(id, true);
      }

      if (object != null)
      {
        map.put(id, object);
        return object;
      }

      return null;
    }
  }
}
