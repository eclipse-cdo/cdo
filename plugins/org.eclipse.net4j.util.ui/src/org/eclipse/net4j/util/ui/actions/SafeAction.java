/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Eike Stepper
 */
public abstract class SafeAction extends Action
{
  public static final String INTERACTIVE = "...";

  public SafeAction()
  {
  }

  public SafeAction(String text, String toolTipText, ImageDescriptor image)
  {
    super(text, image);
    setToolTipText(toolTipText);
  }

  public SafeAction(String text, String toolTipText)
  {
    super(text, null);
    setToolTipText(toolTipText);
  }

  public SafeAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public SafeAction(String text, int style)
  {
    super(text, style);
  }

  public SafeAction(String text)
  {
    super(text);
  }

  @Override
  public final void run()
  {
    try
    {
      safeRun();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      MessageDialog.openError(null, getText(), ex.getMessage() + "\nSee the Error log for details.");
    }
  }

  protected abstract void safeRun() throws Exception;
}
