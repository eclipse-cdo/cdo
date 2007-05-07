/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class SteppingWorkbenchWizard extends SteppingWizard implements IWorkbenchWizard
{
  public static final String KEY_WORKBENCH = "WORKBENCH";

  public static final String KEY_WORKBENCH_SELECTION = "WORKBENCH_SELECTION";

  public SteppingWorkbenchWizard(Map<String, Object> context)
  {
    super(context);
  }

  public SteppingWorkbenchWizard()
  {
  }

  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    setContextValue(KEY_WORKBENCH, workbench);
    setContextValue(KEY_WORKBENCH_SELECTION, selection);
  }
}
