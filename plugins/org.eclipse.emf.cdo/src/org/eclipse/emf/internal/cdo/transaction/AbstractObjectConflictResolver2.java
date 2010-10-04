/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.CDOObjectMerger;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractObjectConflictResolver2 implements CDOConflictResolver2
{
  private CDOTransaction transaction;

  public AbstractObjectConflictResolver2()
  {
  }

  public CDOTransaction getTransaction()
  {
    return transaction;
  }

  public void setTransaction(CDOTransaction transaction)
  {
    if (this.transaction != transaction)
    {
      if (this.transaction != null)
      {
        unhookTransaction(this.transaction);
      }

      this.transaction = transaction;

      if (this.transaction != null)
      {
        hookTransaction(this.transaction);
      }
    }
  }

  public void resolveConflicts(Set<CDOObject> conflicts)
  {
    // Do nothing
  }

  /**
   * Resolves the conflicts in the current transaction. Depending on the decision taken to resolve the conflicts, it may
   * be necessary to adjust the notifications that will be sent to the adapters in the current transaction. This can be
   * achieved by adjusting the {@link CDORevisionDelta} in <code>deltas</code>.
   * 
   * @param deltas
   */
  public void resolveConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> deltas)
  {
    Map<CDOID, CDORevisionDelta> localDeltas = transaction.getRevisionDeltas();
    for (Entry<CDOObject, Pair<CDORevision, CDORevisionDelta>> entry : conflicts.entrySet())
    {
      CDOObject conflict = entry.getKey();
      CDORevision oldRevision = entry.getValue().getElement1();
      CDORevisionDelta remoteDelta = entry.getValue().getElement2();
      CDORevisionDelta localDelta = localDeltas.get(conflict.cdoID());
      resolveConflict(conflict, oldRevision, localDelta, remoteDelta, deltas);
    }
  }

  /**
   * Resolves the conflict of a single object in the current transaction. Depending on the decision taken to resolve the
   * conflict, it may be necessary to adjust the notification that will be sent to the adapters in the current
   * transaction. This can be achieved by adjusting the {@link CDORevisionDelta} in <code>deltas</code>.
   * 
   * @param deltas
   */
  protected abstract void resolveConflict(CDOObject conflict, CDORevision oldRevision, CDORevisionDelta localDelta,
      CDORevisionDelta remoteDelta, List<CDORevisionDelta> deltas);

  protected void hookTransaction(CDOTransaction transaction)
  {
  }

  protected void unhookTransaction(CDOTransaction transaction)
  {
  }

  public static void rollbackObject(CDOObject object)
  {
    CDOStateMachine.INSTANCE.rollback((InternalCDOObject)object);
  }

  public static void readObject(CDOObject object)
  {
    CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
  }

  /**
   * TODO See {@link CDOObjectMerger}!!!
   */
  public static void changeObject(CDOObject object, CDORevisionDelta revisionDelta)
  {
    readObject(object);

    InternalCDORevision revision = (InternalCDORevision)object.cdoRevision().copy();
    ((InternalCDORevisionDelta)revisionDelta).setVersion(revision.getVersion());

    CDORevisionMerger merger = new CDORevisionMerger();
    merger.merge(revision, revisionDelta);
    ((InternalCDOObject)object).cdoInternalSetRevision(revision);
    ((InternalCDOObject)object).cdoInternalSetState(CDOState.DIRTY);
    ((InternalCDOObject)object).cdoInternalPostLoad();
  }

  /**
   * A conflict resolver implementation that takes all the new remote state of the conflicting objects and then applies
   * the locally existing changes of the current transaction.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public static class TakeRemoteChangesThenApplyLocalChanges extends AbstractObjectConflictResolver2
  {
    public TakeRemoteChangesThenApplyLocalChanges()
    {
    }

    @Override
    protected void resolveConflict(CDOObject conflict, CDORevision oldRevision, CDORevisionDelta localDelta,
        CDORevisionDelta remoteDelta, List<CDORevisionDelta> deltas)
    {
      rollbackObject(conflict);
      changeObject(conflict, localDelta);
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static abstract class ThreeWayMerge extends AbstractObjectConflictResolver2
  {
    @Override
    protected void resolveConflict(CDOObject conflict, CDORevision oldRevision, CDORevisionDelta localDelta,
        CDORevisionDelta remoteDelta, List<CDORevisionDelta> deltas)
    {
      resolveConflict(conflict, localDelta, Collections.singletonList(remoteDelta));
    }

    protected abstract void resolveConflict(CDOObject conflict, CDORevisionDelta localDelta,
        List<CDORevisionDelta> remoteDeltas);
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class MergeLocalChangesPerFeature extends ThreeWayMerge
  {
    public MergeLocalChangesPerFeature()
    {
    }

    @Override
    protected void resolveConflict(CDOObject conflict, CDORevisionDelta localDelta, List<CDORevisionDelta> remoteDeltas)
    {
      if (hasFeatureConflicts(localDelta, remoteDeltas))
      {
        // TODO localDelta may be corrupt already and the transaction will not be able to restore it!!!
        throw new CDOException(Messages.getString("AbstractObjectConflictResolver.0")); //$NON-NLS-1$
      }

      rollbackObject(conflict);

      // Add remote deltas to local delta
      for (CDORevisionDelta remoteDelta : remoteDeltas)
      {
        for (CDOFeatureDelta remoteFeatureDelta : remoteDelta.getFeatureDeltas())
        {
          // TODO Add public API for this:
          ((InternalCDORevisionDelta)localDelta).addFeatureDelta(remoteFeatureDelta);
        }
      }

      changeObject(conflict, localDelta);
    }

    protected boolean hasFeatureConflicts(CDORevisionDelta localDelta, List<CDORevisionDelta> remoteDeltas)
    {
      Set<EStructuralFeature> features = new HashSet<EStructuralFeature>();
      for (CDOFeatureDelta localFeatureDelta : localDelta.getFeatureDeltas())
      {
        features.add(localFeatureDelta.getFeature());
      }

      for (CDORevisionDelta remoteDelta : remoteDeltas)
      {
        for (CDOFeatureDelta remoteFeatureDelta : remoteDelta.getFeatureDeltas())
        {
          EStructuralFeature feature = remoteFeatureDelta.getFeature();
          if (features.contains(feature))
          {
            return true;
          }
        }
      }

      return false;
    }
  }
}
