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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;

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
  private CDOMerger merger;

  public CDOMergingConflictResolver()
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued());
  }

  public CDOMergingConflictResolver(CDOMerger merger)
  {
    this.merger = merger;
  }

  public CDOMerger getMerger()
  {
    return merger;
  }

  public void resolveConflicts(Set<CDOObject> conflicts)
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
    Map<InternalCDOObject, InternalCDORevision> cleanRevisions = transaction.getCleanRevisions();

    Map<CDOID, CDORevisionDelta> localDeltas = transaction.getLastSavepoint().getRevisionDeltas2();
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
        InternalCDORevision newLocalRevision = cleanRevision.copy();
        newLocalRevision.setVersion(newVersion);
        resultDelta.apply(newLocalRevision);

        object.cdoInternalSetRevision(newLocalRevision);
        object.cdoInternalSetState(CDOState.DIRTY);

        // Compute new clean revision
        CDORevisionDelta remoteDelta = remoteDeltas.get(id);
        InternalCDORevision newCleanRevision = cleanRevision.copy();
        newCleanRevision.setVersion(newVersion);
        remoteDelta.apply(newCleanRevision);
        cleanRevisions.put(object, newCleanRevision);

        // Compute new local delta
        InternalCDORevisionDelta newLocalDelta = newLocalRevision.compare(newCleanRevision);
        newLocalDelta.setTarget(null);
        localDeltas.put(id, newLocalDelta);
      }
    }
  }

  private Map<CDOID, CDORevisionDelta> getRemoteDeltas(CDOChangeSet remoteChangeSet)
  {
    Map<CDOID, CDORevisionDelta> remoteDeltas = CDOIDUtil.createMap();
    for (CDORevisionKey key : remoteChangeSet.getChangedObjects())
    {
      remoteDeltas.put(key.getID(), (CDORevisionDelta)key);
    }

    return remoteDeltas;
  }
}
