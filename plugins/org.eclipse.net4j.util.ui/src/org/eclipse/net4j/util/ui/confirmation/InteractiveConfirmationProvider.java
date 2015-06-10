/*
 * Copyright (c) 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.ui.confirmation;

import org.eclipse.net4j.util.confirmation.Confirmation;
import org.eclipse.net4j.util.confirmation.IConfirmationProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import java.util.Set;

/**
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 3.4
 */
public class InteractiveConfirmationProvider implements IConfirmationProvider
{
  public InteractiveConfirmationProvider()
  {
  }

  public boolean isInteractive()
  {
    return true;
  }

  public Confirmation confirm(final String subject, final String message, final Set<Confirmation> acceptable,
      final Confirmation suggestion)
  {
    final Confirmation[] confirmation = new Confirmation[1];
    final Display display = UIUtil.getDisplay();
    display.syncExec(new Runnable()
    {
      public void run()
      {
        Shell shell;

        try
        {
          IWorkbenchWindow window = UIUtil.getActiveWorkbenchWindow();
          shell = window.getShell();
        }
        catch (Exception ex)
        {
          shell = new Shell(display);
        }

        confirmation[0] = ConfirmationDialog.openConfirm(shell, subject, message, acceptable, suggestion);
      }
    });

    return confirmation[0];
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   *
   * @since 3.4
   */
  public static class Factory extends IConfirmationProvider.Factory
  {
    public Factory()
    {
      super(INTERACTIVE_TYPE);
    }

    public Object create(String description) throws ProductCreationException
    {
      return new InteractiveConfirmationProvider();
    }
  }
}
