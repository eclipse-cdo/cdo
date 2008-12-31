/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.filters.CDOObjectFilter;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Victor Roldan Betancort
 */
public class CDOObjectFilterAction extends LongRunningAction
{
  private StructuredViewer viewer;

  private CDOObjectFilter filter;

  public CDOObjectFilterAction(StructuredViewer viewer, CDOObjectFilter filter)
  {
    this.viewer = viewer;
    this.filter = filter;
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    viewer.getControl().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        Shell shell = viewer.getControl().getShell();
        InputDialog dialog = new InputDialog(shell, filter.getTitle(), filter.getDescription(), filter.getPattern(),
            null);

        if (dialog.open() == InputDialog.OK)
        {
          filter.setPattern(dialog.getValue().trim());
        }
      }
    });
  }
}
