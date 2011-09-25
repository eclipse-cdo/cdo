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
package org.eclipse.emf.cdo.dawn.emf.notifications.impl;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.common.ui.viewer.IViewerProvider;

/**
 * @author Martin Fluegge
 */
public class DawnEMFLockingHandler extends BasicDawnListener
{
  public DawnEMFLockingHandler(IDawnEditor editor)
  {
    super(editor);
  }

  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {
    System.out.println("DawnEMFLockingHandler.handleViewInvalidationEvent()");
  }

  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event)
  {
    System.out.println("DawnEMFLockingHandler.handleTransactionConflictEvent()");
  }

  public void handleEvent(IEvent event)
  {
    if (event instanceof CDOViewLocksChangedEvent)
    {
      refreshEditor();
    }
  }

  private void refreshEditor()
  {
    editor.getSite().getShell().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        ((IViewerProvider)editor).getViewer().refresh();
      }
    });
  }
}
