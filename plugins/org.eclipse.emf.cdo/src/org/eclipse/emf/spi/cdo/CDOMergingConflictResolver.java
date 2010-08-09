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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOMerger;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
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
    CDOChangeSet target = getLocalChangeSet();
    CDOChangeSet source = getRemoteChangeSet();
    CDOChangeSetData result = merger.merge(target, source);

    // Map<CDOID, InternalCDORevisionDelta> deltas = new HashMap<CDOID, InternalCDORevisionDelta>();
    // for (CDORevisionKey key : result.getChangedObjects())
    // {
    // deltas.put(key.getID(), (InternalCDORevisionDelta)key);
    // }
    //
    // for (CDORevisionKey key : source.getChangedObjects())
    // {
    // CDOID id = key.getID();
    // InternalCDORevisionDelta delta = deltas.get(id);
    // if (delta != null)
    // {
    // CDOBranchVersion branchVersion = ((CDORevisionDelta)key).getTarget();
    // delta.setTarget(branchVersion.getBranch().getVersion(branchVersion.getVersion()));
    // }
    // }

    final InternalCDOTransaction transaction = (InternalCDOTransaction)getTransaction();
    final InternalCDOSession session = transaction.getSession();
    final InternalCDORevisionManager revisionManager = session.getRevisionManager();

    final CDORevisionProvider ancestorProvider = new CDORevisionProvider()
    {
      public CDORevision getRevision(CDOID id)
      {
        return revisionManager.getRevision(id, transaction, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
      }
    };

    final CDORevisionProvider targetProvider = new CDORevisionProvider()
    {
      public CDORevision getRevision(CDOID id)
      {
        CDOObject object = transaction.getObject(id, false);
        if (object != null)
        {
          return object.cdoRevision();
        }

        return ancestorProvider.getRevision(id);
      }
    };

    CDOChangeSetData applied = transaction.applyChangeSetData(result, ancestorProvider, targetProvider, transaction);

    ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = transaction.getLastSavepoint().getRevisionDeltas();
    for (CDORevisionKey key : applied.getChangedObjects())
    {
      CDOID id = key.getID();
      InternalCDOObject object = (InternalCDOObject)transaction.getObject(id, false);
      if (object != null)
      {
        object.cdoInternalSetState(CDOState.DIRTY);
        InternalCDORevision revision = object.cdoRevision();
        int version = revision.getVersion() + 1;
        revision.setVersion(version);

        InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)revisionDeltas.get(id);
        if (revisionDelta != null)
        {
          revisionDelta.setVersion(version);
        }
      }
    }
  }
}
