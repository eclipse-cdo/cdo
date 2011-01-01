/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Martin Fluegge
 */
public abstract class BasicDawnListener implements IDawnListener// implements IListener
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, BasicDawnListener.class);

  protected IDawnEditor editor;

  /**
   * @since 1.0
   */
  public void setEditor(IDawnEditor editor)
  {
    this.editor = editor;
  }

  /**
   * @since 1.0
   */
  public BasicDawnListener()
  {
  }

  /**
   * @since 1.0
   */
  public BasicDawnListener(IDawnEditor editor)
  {
    this.editor = editor;
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewInvalidationEvent)
    {
      handleViewInvalidationEvent((CDOViewInvalidationEvent)event);
    }
    else if (event instanceof CDOTransactionConflictEvent)
    {
      handleTransactionConflictEvent((CDOTransactionConflictEvent)event);
    }
  }

  /**
   * @since 1.0
   */
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("attachingObject {0}", object); //$NON-NLS-1$
    }
    editor.setDirty();
  }

  /**
   * @since 1.0
   */
  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("detachingObject {0}", object); //$NON-NLS-1$
    }

    editor.setDirty();
  }

  /**
   * @since 1.0
   */
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
  { // This method can be overwritten be subclasses
  }

  /**
   * @since 1.0
   */
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  { // This method can be overwritten be subclasses
  }

  /**
   * @since 1.0
   */
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  { // This method can be overwritten be subclasses
  }

  /**
   * @since 1.0
   */
  public void rolledBackTransaction(CDOTransaction transaction)
  { // This method can be overwritten be subclasses
  }

  /**
   * @since 1.0
   */
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  { // This method can be overwritten be subclasses
  }

  /**
   * @since 1.0
   */
  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event)
  { // This method can be overwritten be subclasses
  }
}
