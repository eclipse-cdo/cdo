/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306 - adapted from SafeActionDelegate
 */
package org.eclipse.net4j.util.ui.handlers;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 3.4
 */
public abstract class SafeHandler extends AbstractHandler
{
  private Command command;

  private ISelection selection;

  public SafeHandler()
  {
  }

  public Command getCommand()
  {
    return command;
  }

  public ISelection getSelection()
  {
    return selection;
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    Object result = null;

    try
    {
      extractEventDetails(event);

      result = safeExecute(event);
    }
    catch (ExecutionException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      MessageDialog.openError(null, getText(), ex.getLocalizedMessage() + "\n" + Messages.getString("SafeActionDelegate_0")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return result;
  }

  @Override
  public void setEnabled(Object evaluationContext)
  {
    try
    {
      Object variable = HandlerUtil.getVariable(evaluationContext, ISources.ACTIVE_CURRENT_SELECTION_NAME);
      selection = variable instanceof ISelection ? (ISelection)variable : StructuredSelection.EMPTY;
      setBaseEnabled(updateSelection(selection));
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  /**
   * Extracts details from the {@code event} that we may need later,
   * for example on a background thread when the original execution
   * context is no longer valid.
   */
  protected void extractEventDetails(ExecutionEvent event)
  {
    setEnabled(event.getApplicationContext());
    command = event.getCommand();
  }

  protected abstract Object safeExecute(ExecutionEvent event) throws Exception;

  protected String getText()
  {
    try
    {
      return command == null ? Messages.getString("SafeActionDelegate_1") : command.getName(); //$NON-NLS-1$
    }
    catch (NotDefinedException e)
    {
      return Messages.getString("SafeActionDelegate_1"); //$NON-NLS-1$
    }
  }

  protected boolean updateSelection(ISelection selection)
  {
    return true;
  }
}
