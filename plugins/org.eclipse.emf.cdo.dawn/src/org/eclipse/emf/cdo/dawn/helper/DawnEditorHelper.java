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
package org.eclipse.emf.cdo.dawn.helper;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * TODO move this one to the dawn.util bundle
 * 
 * @author Martin Fluegge
 */
public class DawnEditorHelper
{
  public static Shell getActiveShell()
  {
    Shell shell = Display.getCurrent().getActiveShell();
    return shell;
  }

  public static IEditorPart getActiveEditor()
  {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IEditorPart editor = null;
    if (window != null)
    {
      IWorkbenchPage page = window.getActivePage();
      if (page != null)
      {
        editor = page.getActiveEditor();
      }
    }
    return editor;
  }

  /**
   * This method tries to retrieve a Display. First it tries to get the current display. If this fails it will return
   * the default display.
   * 
   * @return the current display, if not null. If the current Display is null then the default Display.
   * @since 2.0
   */
  public static Display getDisplay()
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      display = Display.getDefault();
    }
    return display;
  }
}
