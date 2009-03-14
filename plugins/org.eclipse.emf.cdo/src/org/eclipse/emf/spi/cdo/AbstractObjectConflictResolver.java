/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.internal.cdo.CDOObjectMerger;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractObjectConflictResolver implements CDOConflictResolver
{
  private CDOTransaction transaction;

  public AbstractObjectConflictResolver()
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
    Map<CDOID, CDORevisionDelta> revisionDeltas = transaction.getRevisionDeltas();
    for (CDOObject conflict : conflicts)
    {
      CDORevisionDelta revisionDelta = revisionDeltas.get(conflict.cdoID());
      resolveConflict(conflict, revisionDelta);
    }
  }

  /**
   * Resolves the conflict of a single object in the current transaction.
   */
  protected abstract void resolveConflict(CDOObject conflict, CDORevisionDelta revisionDelta);

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
    int originVersion = revision.getVersion();
    revision.setTransactional();

    ((InternalCDORevisionDelta)revisionDelta).setOriginVersion(originVersion);
    ((InternalCDORevisionDelta)revisionDelta).setDirtyVersion(revision.getVersion());

    CDORevisionMerger merger = new CDORevisionMerger();
    merger.merge(revision, revisionDelta);
    ((InternalCDOObject)object).cdoInternalSetRevision(revision);
    ((InternalCDOObject)object).cdoInternalSetState(CDOState.DIRTY);
  }

  /**
   * A conflict resolver implementation that takes all the new remote state of the conflicting objects and then applies
   * the locally existing changes of the current transaction.
   * 
   * @author Eike Stepper
   * @since 2.0
   */
  public static class TakeRemoteChangesThenApplyLocalChanges extends AbstractObjectConflictResolver
  {
    public TakeRemoteChangesThenApplyLocalChanges()
    {
    }

    @Override
    protected void resolveConflict(CDOObject conflict, CDORevisionDelta revisionDelta)
    {
      rollbackObject(conflict);
      changeObject(conflict, revisionDelta);
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static abstract class ThreeWayMerge extends AbstractObjectConflictResolver implements CDOAdapterPolicy
  {
    private ChangeSubscriptionAdapter adapter = new ChangeSubscriptionAdapter();

    private CDOTransactionHandler handler = new CDODefaultTransactionHandler()
    {
      @Override
      public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta ignored)
      {
        if (getTransaction() == transaction)
        {
          adapter.attach(object);
        }
      }

      @Override
      public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
        if (getTransaction() == transaction)
        {
          adapter.reset();
        }
      }

      @Override
      public void rolledBackTransaction(CDOTransaction transaction)
      {
        // Reset the accumulation only if it rolled back the transaction completely
        if (getTransaction() == transaction && transaction.getLastSavepoint().getPreviousSavepoint() == null)
        {
          adapter.reset();
        }
      }
    };

    public ThreeWayMerge()
    {
    }

    public boolean isValid(EObject object, Adapter adapter)
    {
      return adapter instanceof ChangeSubscriptionAdapter;
    }

    @Override
    protected void hookTransaction(CDOTransaction transaction)
    {
      transaction.options().addChangeSubscriptionPolicy(this);
      transaction.addHandler(handler);
    }

    @Override
    protected void unhookTransaction(CDOTransaction transaction)
    {
      transaction.removeHandler(handler);
      transaction.options().removeChangeSubscriptionPolicy(this);
    }

    @Override
    protected void resolveConflict(CDOObject conflict, CDORevisionDelta revisionDelta)
    {
      resolveConflict(conflict, revisionDelta, adapter.getRevisionDeltas(conflict));
    }

    protected abstract void resolveConflict(CDOObject conflict, CDORevisionDelta localDelta,
        List<CDORevisionDelta> remoteDeltas);

    /**
     * @author Eike Stepper
     * @since 2.0
     */
    public static class ChangeSubscriptionAdapter extends AdapterImpl
    {
      private Set<CDOObject> notifiers = new HashSet<CDOObject>();

      private Map<CDOObject, List<CDORevisionDelta>> deltas = new HashMap<CDOObject, List<CDORevisionDelta>>();

      public ChangeSubscriptionAdapter()
      {
      }

      public List<CDORevisionDelta> getRevisionDeltas(CDOObject notifier)
      {
        List<CDORevisionDelta> list = deltas.get(notifier);
        if (list == null)
        {
          return Collections.emptyList();
        }

        return list;
      }

      public Set<CDOObject> getNotifiers()
      {
        return notifiers;
      }

      public Map<CDOObject, List<CDORevisionDelta>> getDeltas()
      {
        return deltas;
      }

      public void attach(CDOObject notifier)
      {
        if (notifiers.add(notifier))
        {
          notifier.eAdapters().add(this);
        }
      }

      public void reset()
      {
        for (CDOObject notifier : notifiers)
        {
          notifier.eAdapters().remove(this);
        }

        notifiers.clear();
        deltas.clear();
      }

      @Override
      public void notifyChanged(Notification msg)
      {
        try
        {
          if (msg instanceof CDODeltaNotification)
          {
            CDODeltaNotification deltaNotification = (CDODeltaNotification)msg;
            Object notifier = deltaNotification.getNotifier();
            if (!deltaNotification.hasNext() && notifiers.contains(notifier))
            {
              CDORevisionDelta revisionDelta = deltaNotification.getRevisionDelta();
              List<CDORevisionDelta> list = deltas.get(notifier);
              if (list == null)
              {
                list = new ArrayList<CDORevisionDelta>(1);
                deltas.put((CDOObject)notifier, list);
              }

              list.add(revisionDelta);
            }
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
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
        throw new CDOException("Object has feature-level conflicts");
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
