/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.emf.common.ui.viewer.IViewerProvider;

/**
 * @author Martin Fluegge
 */
public class DawnEMFHandler extends BasicDawnTransactionHandler
{
  /**
   * @since 1.0
   */
  public DawnEMFHandler(IDawnEditor editor)
  {
    super(editor);
  }

  @Override
  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event)
  {
    super.handleTransactionConflictEvent(event);
    refreshEditor();
  }

  @Override
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {
    super.handleViewInvalidationEvent(event);
    refreshEditor();
  }

  private void refreshEditor()
  {
    editor.getSite().getShell().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        ((IViewerProvider)editor).getViewer().refresh();
      }
    });
  }
}
