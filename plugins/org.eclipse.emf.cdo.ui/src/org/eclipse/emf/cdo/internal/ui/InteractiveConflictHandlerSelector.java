/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.internal.ui.dialogs.RollbackTransactionDialog;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.emf.internal.cdo.transaction.CDOHandlingConflictResolver.ConflictHandler;
import org.eclipse.emf.internal.cdo.transaction.CDOHandlingConflictResolver.ConflictHandlerSelector;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class InteractiveConflictHandlerSelector implements ConflictHandlerSelector
{
  public InteractiveConflictHandlerSelector()
  {
  }

  @Override
  public ConflictHandler selectConflictHandler(final CDOTransaction transaction, final List<ConflictHandler> choices)
  {
    final ConflictHandler[] result = { null };

    UIUtil.getDisplay().syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        IWorkbenchPage workbenchPage = UIUtil.getActiveWorkbenchPage();
        String title = "Conflict Error";

        new RollbackTransactionDialog(workbenchPage, title, title, transaction)
        {
          @Override
          protected String getQuestion()
          {
            return null;
          }

          @Override
          protected Control createDialogArea(Composite parent)
          {
            Control control = super.createDialogArea(parent);
            setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_CONFLICT));
            setMessage("The transaction contains unresolved conflicts.");
            return control;
          }

          @Override
          protected void createButtonsForButtonBar(Composite parent)
          {
            boolean defaultButton = true;

            for (final ConflictHandler conflictHandler : choices)
            {
              Button button = createButton(parent, IDialogConstants.OK_ID, conflictHandler.getLabel(), defaultButton);
              button.addSelectionListener(new SelectionAdapter()
              {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                  result[0] = conflictHandler;
                }
              });

              defaultButton = false;
            }

            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
          }
        }.open();
      }
    });

    return result[0];
  }
}
