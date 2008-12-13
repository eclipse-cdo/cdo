/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOConflictResolver;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;

import org.eclipse.emf.internal.cdo.CDOObjectMerger;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ObjectConflictResolver implements CDOConflictResolver
{
  public static final CDORevisionMerger REVISION_MERGER = new CDORevisionMerger();

  public ObjectConflictResolver()
  {
  }

  public void resolveConflicts(CDOTransaction transaction, Set<CDOObject> conflicts)
  {
    Map<CDOID, CDORevisionDelta> revisionDeltas = transaction.getRevisionDeltas();
    for (CDOObject conflict : conflicts)
    {
      CDORevisionDelta revisionDelta = revisionDeltas.get(conflict.cdoID());
      resolveConflict(transaction, conflict, revisionDelta);
    }
  }

  /**
   * Resolves the conflict of a single object in the current transaction.
   */
  protected abstract void resolveConflict(CDOTransaction transaction, CDOObject conflict, CDORevisionDelta revisionDelta);

  public static void rollbackObject(CDOObject object)
  {
    CDOStateMachine.INSTANCE.rollback((InternalCDOObject)object);
  }

  /**
   * TODO See {@link CDOObjectMerger}!!!
   */
  public static void changeObject(CDOObject object, CDORevisionDelta revisionDelta)
  {
    CDOStateMachine.INSTANCE.read((InternalCDOObject)object);

    InternalCDORevision revision = (InternalCDORevision)object.cdoRevision().copy();
    int originVersion = revision.getVersion();
    revision.setTransactional();

    ((InternalCDORevisionDelta)revisionDelta).setOriginVersion(originVersion);
    ((InternalCDORevisionDelta)revisionDelta).setDirtyVersion(revision.getVersion());

    REVISION_MERGER.merge(revision, revisionDelta);
    ((InternalCDOObject)object).cdoInternalSetRevision(revision);
  }

  /**
   * A conflict resolver implementation that takes all the new remote state of the conflicting objects and then applies
   * the locally existing changes of the current transaction.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public static class TakeRemoteChangesThenApplyLocalChanges extends ObjectConflictResolver
  {
    public TakeRemoteChangesThenApplyLocalChanges()
    {
    }

    @Override
    protected void resolveConflict(CDOTransaction transaction, CDOObject conflict, CDORevisionDelta revisionDelta)
    {
      rollbackObject(conflict);
      changeObject(conflict, revisionDelta);
    }
  }
}
