/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;

/**
 * @author Eike Stepper
 */
public abstract class SafeActionDelegate implements IActionDelegate
{
  /**
   * @since 3.5
   */
  public static final String INTERACTIVE = SafeAction.INTERACTIVE;

  private IAction action;

  private ISelection selection;

  public SafeActionDelegate()
  {
  }

  /**
   * @since 3.5
   */
  public Shell getShell()
  {
    return UIUtil.getShell();
  }

  public IAction getAction()
  {
    return action;
  }

  public ISelection getSelection()
  {
    return selection;
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
    this.action = action;
    this.selection = selection;
  }

  @Override
  public void run(IAction action)
  {
    this.action = action;

    try
    {
      safeRun();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);

      Shell shell = getShell();
      String text = getText();
      String message = ex.getLocalizedMessage() + "\n" + Messages.getString("SafeActionDelegate_0"); //$NON-NLS-1$ //$NON-NLS-2$
      MessageDialog.openError(shell, text, message);
    }
  }

  protected abstract void safeRun() throws Exception;

  protected String getText()
  {
    return action == null ? Messages.getString("SafeActionDelegate_1") : action.getText(); //$NON-NLS-1$
  }
}
