/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
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
    else if (event instanceof CDOViewLocksChangedEvent)
    {
      handleLocksChangedEvent((CDOViewLocksChangedEvent)event);
    }
    else
    {
      handleEvent(event);
    }
  }

  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {
  }

  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event)
  {
  }

  /**
   * @since 2.0
   */
  public void handleLocksChangedEvent(CDOViewLocksChangedEvent event)
  {
  }

  /**
   * @since 2.0
   */
  public void handleEvent(IEvent event)
  {
  }
}
