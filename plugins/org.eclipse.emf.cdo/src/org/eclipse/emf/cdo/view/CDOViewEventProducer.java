/*
 * Copyright (c) 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler1;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;

import org.eclipse.net4j.util.container.SelfAttachingContainerListener;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * Produces {@link IEvent events} for calls to various {@link CDOObjectHandler view object handler}
 * and {@link CDOTransactionHandlerBase transaction handler} methods and delivers these events to a
 * {@link org.eclipse.net4j.util.container.SelfAttachingContainerListener.Delegating delegate} {@link IListener listener}.
 *
 * @author Eike Stepper
 * @since 4.5
 */
public class CDOViewEventProducer extends SelfAttachingContainerListener.Delegating
{
  private final CDOObjectHandler objectHandler = new CDOObjectHandler()
  {
    @Override
    public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      notifyOtherEvent(new CDOObjectStateChangedEvent(view, object, oldState, newState));
    }
  };

  private final CDOTransactionHandler1 transactionHandler1 = new CDOTransactionHandler1()
  {
    @Override
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      notifyOtherEvent(new CDOAttachingObjectEvent(transaction, object));
    }

    @Override
    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      notifyOtherEvent(new CDODetachingObjectEvent(transaction, object));
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      notifyOtherEvent(new CDOModifyingObjectEvent(transaction, object, featureDelta));
    }
  };

  private final CDOTransactionHandler2 transactionHandler2 = new CDOTransactionHandler2()
  {
    @Override
    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      notifyOtherEvent(new CDOCommittingTransactionEvent(transaction, commitContext));
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      notifyOtherEvent(new CDOCommittedTransactionEvent(transaction, commitContext));
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
      notifyOtherEvent(new CDORolledBackTransactionEvent(transaction));
    }
  };

  public CDOViewEventProducer(IListener delegate, boolean delegateContainerEvents)
  {
    super(delegate, delegateContainerEvents);
  }

  public CDOViewEventProducer(IListener delegate)
  {
    super(delegate);
  }

  @Override
  public void attach(Object element)
  {
    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      if (produceObjectStateChangedEvents())
      {
        view.addObjectHandler(objectHandler);
      }

      if (view instanceof CDOTransaction)
      {
        CDOTransaction transaction = (CDOTransaction)view;
        if (produceObjectModificationEvents())
        {
          transaction.addTransactionHandler(transactionHandler1);
        }

        if (produceTransactionDemarcationEvents())
        {
          transaction.addTransactionHandler(transactionHandler2);
        }
      }
    }

    super.attach(element);
  }

  @Override
  public void detach(Object element)
  {
    super.detach(element);

    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      if (produceObjectStateChangedEvents())
      {
        view.removeObjectHandler(objectHandler);
      }

      if (view instanceof CDOTransaction)
      {
        CDOTransaction transaction = (CDOTransaction)view;
        if (produceObjectModificationEvents())
        {
          transaction.removeTransactionHandler(transactionHandler1);
        }

        if (produceTransactionDemarcationEvents())
        {
          transaction.removeTransactionHandler(transactionHandler2);
        }
      }
    }
  }

  protected boolean produceObjectStateChangedEvents()
  {
    return true;
  }

  protected boolean produceObjectModificationEvents()
  {
    return true;
  }

  protected boolean produceTransactionDemarcationEvents()
  {
    return true;
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOObjectHandler#objectStateChanged(CDOView, CDOObject, CDOState, CDOState) CDOObjectHandler.objectStateChanged()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDOObjectStateChangedEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOObject object;

    private final CDOState oldState;

    private final CDOState newState;

    private CDOObjectStateChangedEvent(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      super(view);
      this.object = object;
      this.oldState = oldState;
      this.newState = newState;
    }

    @Override
    public CDOView getSource()
    {
      return (CDOView)super.getSource();
    }

    public CDOObject getObject()
    {
      return object;
    }

    public CDOState getOldState()
    {
      return oldState;
    }

    public CDOState getNewState()
    {
      return newState;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "object=" + object + ", oldState=" + oldState + ", newState=" + newState;
    }
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOTransactionHandler1#attachingObject(CDOTransaction, CDOObject) CDOTransactionHandler1.attachingObject()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDOAttachingObjectEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOObject object;

    private CDOAttachingObjectEvent(CDOTransaction transaction, CDOObject object)
    {
      super(transaction);
      this.object = object;
    }

    @Override
    public CDOTransaction getSource()
    {
      return (CDOTransaction)super.getSource();
    }

    public CDOObject getObject()
    {
      return object;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "object=" + object;
    }
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOTransactionHandler1#detachingObject(CDOTransaction, CDOObject) CDOTransactionHandler1.detachingObject()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDODetachingObjectEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOObject object;

    private CDODetachingObjectEvent(CDOTransaction transaction, CDOObject object)
    {
      super(transaction);
      this.object = object;
    }

    @Override
    public CDOTransaction getSource()
    {
      return (CDOTransaction)super.getSource();
    }

    public CDOObject getObject()
    {
      return object;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "object=" + object;
    }
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOTransactionHandler1#modifyingObject(CDOTransaction, CDOObject, CDOFeatureDelta) CDOTransactionHandler1.modifyingObject()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDOModifyingObjectEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOObject object;

    private final CDOFeatureDelta featureDelta;

    private CDOModifyingObjectEvent(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      super(transaction);
      this.object = object;
      this.featureDelta = featureDelta;
    }

    @Override
    public CDOTransaction getSource()
    {
      return (CDOTransaction)super.getSource();
    }

    public CDOObject getObject()
    {
      return object;
    }

    public CDOFeatureDelta getFeatureDelta()
    {
      return featureDelta;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "object=" + object + ", featureDelta=" + featureDelta;
    }
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOTransactionHandler2#committingTransaction(CDOTransaction, CDOCommitContext) CDOTransactionHandler2.committingTransaction()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDOCommittingTransactionEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOCommitContext commitContext;

    private CDOCommittingTransactionEvent(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      super(transaction);
      this.commitContext = commitContext;
    }

    @Override
    public CDOTransaction getSource()
    {
      return (CDOTransaction)super.getSource();
    }

    public CDOCommitContext getCommitContext()
    {
      return commitContext;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "commitContext=" + commitContext;
    }
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOTransactionHandler2#committedTransaction(CDOTransaction, CDOCommitContext) CDOTransactionHandler2.committedTransaction()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDOCommittedTransactionEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOCommitContext commitContext;

    private CDOCommittedTransactionEvent(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      super(transaction);
      this.commitContext = commitContext;
    }

    @Override
    public CDOTransaction getSource()
    {
      return (CDOTransaction)super.getSource();
    }

    public CDOCommitContext getCommitContext()
    {
      return commitContext;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "commitContext=" + commitContext;
    }
  }

  /**
   * An {@link CDOViewEvent event} fired from a {@link CDOViewEventProducer view event producer} when
   * {@link CDOTransactionHandler2#rolledBackTransaction(CDOTransaction) CDOTransactionHandler2.rolledBackTransaction()}
   * was called.
   *
   * @author Eike Stepper
   */
  public static final class CDORolledBackTransactionEvent extends Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    private CDORolledBackTransactionEvent(CDOTransaction transaction)
    {
      super(transaction);
    }

    @Override
    public CDOTransaction getSource()
    {
      return (CDOTransaction)super.getSource();
    }
  }
}
