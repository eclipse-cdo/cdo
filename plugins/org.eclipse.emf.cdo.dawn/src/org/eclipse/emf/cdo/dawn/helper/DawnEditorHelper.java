/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
  /**
   * Delivers the active shell from the current display.
   *
   * @return the active shell on the current display
   */
  public static Shell getActiveShell()
  {
    Shell shell = Display.getCurrent().getActiveShell();
    return shell;
  }

  /**
   * This method returns the active editor from the active workbench page
   *
   * @return the active editor from the active workbench page or null if there is none
   */
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
