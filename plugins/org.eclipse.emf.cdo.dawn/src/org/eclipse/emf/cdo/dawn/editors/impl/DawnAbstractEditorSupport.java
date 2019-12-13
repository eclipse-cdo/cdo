/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.editors.impl;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.List;

/**
 * @author Martin Fluegge
 */
public abstract class DawnAbstractEditorSupport implements IDawnEditorSupport
{
  private final IDawnEditor editor;

  private CDOView view;

  private boolean dirty;

  @Override
  public CDOView getView()
  {
    return view;
  }

  @Override
  public void setView(CDOView view)
  {
    this.view = view;
  }

  public DawnAbstractEditorSupport(IDawnEditor editor)
  {
    this.editor = editor;
  }

  @Override
  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }

  @Override
  public boolean isDirty()
  {
    return dirty;
  }

  public IDawnEditor getEditor()
  {
    return editor;
  }

  /**
   * This method registeres the listeners for the DawnEditorSupport. Concrete implementaions can influence the
   * registered listeneres by implementing the methods
   * <ul>
   * <li>getBasicHandler()</li>
   * <li>getLockingHandler()</li>
   * </ul>
   * If one of these methods returns null the specific handler will not be registered and activated.
   *
   * @see #getBasicHandler()
   * @see #getLockingHandler()
   * @since 2.0
   */
  @Override
  public void registerListeners()
  {
    BasicDawnListener listener = getBasicHandler();
    CDOView view = getView();

    if (listener != null)
    {
      view.addListener(listener);
    }

    BasicDawnListener lockingHandler = getLockingHandler();
    if (lockingHandler != null)
    {
      view.addListener(lockingHandler);
      view.options().setLockNotificationEnabled(true);
    }

    if (view instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)view;
      CDOTransactionHandlerBase transactionHandler = getTransactionHandler();
      if (transactionHandler != null)
      {
        transaction.addTransactionHandler(transactionHandler);
      }
      transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);
      transaction.options().setAutoReleaseLocksEnabled(false);
    }
  }

  /**
   * Subclasses must implement this method to deliver a IDawnListener that implements the behavior for Session
   * invalidations. If the method returns null, the handler will not be registered.
   *
   * @since 2.0
   */
  protected abstract BasicDawnListener getBasicHandler();

  /**
   * Subclasses must implement this method to deliver a IDawnListener that implements the behavior for remote locking
   * notifications. If the method returns null, the handler will not be registered.
   *
   * @since 2.0
   */
  protected abstract BasicDawnListener getLockingHandler();

  /**
   * Subclasses must implement this method to deliver a CDOTransactionHandlerBase that implements the behavior for
   * remote changes notifications. If the method returns null, the handler will not be registered.
   *
   * @since 2.0
   */
  protected abstract CDOTransactionHandlerBase getTransactionHandler();

  /**
   * @since 1.0
   */
  @Override
  public void rollback()
  {
    CDOView view = getEditor().getView();

    if (view != null && view instanceof CDOTransaction)
    {
      ((CDOTransaction)view).rollback();
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void lockObjects(List<Object> objectsToBeLocked)
  {
    for (Object objectToBeLocked : objectsToBeLocked)
    {
      lockObject(objectToBeLocked);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void unlockObjects(List<Object> objectsToBeLocked)
  {
    for (Object objectToBeUnlocked : objectsToBeLocked)
    {
      unlockObject(objectToBeUnlocked);
    }
  }
}
