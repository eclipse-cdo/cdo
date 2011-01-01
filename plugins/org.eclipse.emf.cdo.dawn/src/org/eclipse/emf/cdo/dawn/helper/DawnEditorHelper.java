/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.helper;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
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
}
