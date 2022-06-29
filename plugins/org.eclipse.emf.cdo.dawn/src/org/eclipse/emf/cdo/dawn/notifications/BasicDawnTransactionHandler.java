/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class BasicDawnTransactionHandler extends BasicDawnListener implements IDawnTransactionHandler
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, BasicDawnTransactionHandler.class);

  public BasicDawnTransactionHandler(IDawnEditor editor)
  {
    super(editor);
  }

  @Override
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("attachingObject {0}", object); //$NON-NLS-1$
    }
    editor.setDirty();
  }

  @Override
  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("detachingObject {0}", object); //$NON-NLS-1$
    }

    editor.setDirty();
  }

  @Override
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("modifyingObject {0}", object); //$NON-NLS-1$
    }

    editor.setDirty();
  }

  @Override
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  { // This method can be overwritten be subclasses
  }

  @Override
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  { // This method can be overwritten be subclasses
  }

  @Override
  public void rolledBackTransaction(CDOTransaction transaction)
  { // This method can be overwritten be subclasses
  }

  @Override
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  { // This method can be overwritten be subclasses
  }

  @Override
  public void handleTransactionConflictEvent(@SuppressWarnings("deprecation") org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent event)
  { // This method can be overwritten be subclasses
  }

  @Override
  public void handleEvent(IEvent event)
  {
  }
}
