/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public abstract class SafeAction extends Action
{
  public static final String INTERACTIVE = Messages.getString("SafeAction_0"); //$NON-NLS-1$

  private static final ThreadLocal<Event> EVENT = new ThreadLocal<>();

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

  /**
   * @since 3.5
   */
  public Shell getShell()
  {
    return UIUtil.getShell();
  }

  @Override
  public final void run()
  {
    runWithEvent(null);
  }

  @Override
  public final void runWithEvent(Event event)
  {
    if (event != null)
    {
      EVENT.set(event);
    }

    try
    {
      safeRun();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      MessageDialog.openError(null, getText(), ex.getLocalizedMessage() + "\n" + Messages.getString("SafeAction_1")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    finally
    {
      if (event != null)
      {
        EVENT.remove();
      }
    }
  }

  /**
   * @since 3.21
   */
  protected final Event getEvent()
  {
    return EVENT.get();
  }

  protected abstract void safeRun() throws Exception;
}
