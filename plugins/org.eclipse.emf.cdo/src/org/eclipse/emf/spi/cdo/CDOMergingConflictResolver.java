/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.EMFUtil;
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
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.HashMap;
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

  public CDOMergingConflictResolver(CDOMerger merger)
  {
    this.merger = merger;
  }

  /**
   * @since 4.2
   */
  public CDOMergingConflictResolver(DefaultCDOMerger.ResolutionPreference resolutionPreference)
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued(resolutionPreference));
  }

  public CDOMergingConflictResolver()
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued());
  }

  public CDOMerger getMerger()
  {
    return merger;
  }

  public void resolveConflicts(Set<CDOObject> conflicts)
  {
    try
    {
      CDOChangeSet localChangeSet = getLocalChangeSet();
      CDOChangeSet remoteChangeSet = getRemoteChangeSet();
      CDOChangeSetData result;

      try
      {
        result = merger.merge(localChangeSet, remoteChangeSet);
      }
      catch (ConflictException ex)
      {
        result = ex.getResult();
      }

      InternalCDOTransaction transaction = (InternalCDOTransaction)getTransaction();
      InternalCDOSavepoint savepoint = transaction.getLastSavepoint();

      Map<InternalCDOObject, InternalCDORevision> cleanRevisions = transaction.getCleanRevisions();
      Map<CDOID, CDOObject> dirtyObjects = savepoint.getDirtyObjects();
      // final ObjectsMapUpdater newObjectsUpdater = new ObjectsMapUpdater(savepoint.getNewObjects());
      final ObjectsMapUpdater detachedObjectsUpdater = new ObjectsMapUpdater(savepoint.getDetachedObjects());

      Map<CDOID, CDORevisionDelta> localDeltas = savepoint.getRevisionDeltas2();
      Map<CDOID, CDORevisionDelta> remoteDeltas = getRemoteDeltas(remoteChangeSet);

      for (CDORevisionKey key : result.getChangedObjects())
      {
        InternalCDORevisionDelta resultDelta = (InternalCDORevisionDelta)key;
        CDOID id = resultDelta.getID();
        InternalCDOObject object = (InternalCDOObject)transaction.getObject(id, false);
        if (object != null)
        {
          // Compute new version
          InternalCDORevision localRevision = object.cdoRevision();
          int newVersion = localRevision.getVersion() + 1;

          // Compute new local revision
          InternalCDORevision cleanRevision = cleanRevisions.get(object);
          if (cleanRevision == null)
          {
            // In this case the object revision *is clean*
            cleanRevision = object.cdoRevision();
          }

          InternalCDORevision newLocalRevision = cleanRevision.copy();
          newLocalRevision.setVersion(newVersion);
          resultDelta.apply(newLocalRevision);

          // Adjust local object
          object.cdoInternalSetRevision(newLocalRevision);

          // Compute new clean revision
          CDORevisionDelta remoteDelta = remoteDeltas.get(id);
          InternalCDORevision newCleanRevision = cleanRevision.copy();
          newCleanRevision.setVersion(newVersion);
          remoteDelta.apply(newCleanRevision);

          // Compute new local delta
          InternalCDORevisionDelta newLocalDelta = newLocalRevision.compare(newCleanRevision);
          if (newLocalDelta.isEmpty())
          {
            localDeltas.remove(id);
            object.cdoInternalSetState(CDOState.CLEAN);
            dirtyObjects.remove(id);
          }
          else
          {
            newLocalDelta.setTarget(null);
            localDeltas.put(id, newLocalDelta);
            object.cdoInternalSetState(CDOState.DIRTY);

            cleanRevisions.put(object, newCleanRevision);
            dirtyObjects.put(id, object);

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
                // TODO: implement CDOMergingConflictResolver.resolveConflicts(...).new CDOFeatureDeltaVisitorImpl()
              }

              @Override
              public void visit(CDORemoveFeatureDelta delta)
              {
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
        }
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
  }

  private Map<CDOID, CDORevisionDelta> getRemoteDeltas(CDOChangeSet remoteChangeSet)
  {
    Map<CDOID, CDORevisionDelta> remoteDeltas = new HashMap<CDOID, CDORevisionDelta>();
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
      mapCopy = new HashMap<CDOID, CDOObject>(map);
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
