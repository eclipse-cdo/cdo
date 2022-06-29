/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Martin Fluegge
 */
public abstract class BasicDawnListener implements IDawnListener// implements IListener
{
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

  @Override
  @SuppressWarnings("deprecation")
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewInvalidationEvent)
    {
      handleViewInvalidationEvent((CDOViewInvalidationEvent)event);
    }
    else if (event instanceof org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent)
    {
      handleTransactionConflictEvent((org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent)event);
    }
    else if (event instanceof CDOViewLocksChangedEvent)
    {
      handleLocksChangedEvent((CDOViewLocksChangedEvent)event);
    }
    else
    {
      handleEvent(event);
    }
  }

  @Override
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {
  }

  @Override
  public void handleTransactionConflictEvent(@SuppressWarnings("deprecation") org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent event)
  {
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleLocksChangedEvent(CDOViewLocksChangedEvent event)
  {
  }

  /**
   * @since 2.0
   */
  @Override
  public void handleEvent(IEvent event)
  {
  }
}
