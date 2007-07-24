/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.internal.util.collection.PreferenceHistory;
import org.eclipse.net4j.ui.widgets.HistoryInputDialog;
import org.eclipse.net4j.util.collection.IHistory;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RegisterCDOPackageAction extends RegisterPackageAction
{
  public static final IHistory<String> HISTORY = new PreferenceHistory(OM.PREF_HISTORY_REGISTER_CDO_PACKAGE);

  private static final String TITLE = "Register CDO Package";

  public RegisterCDOPackageAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, "Register a package generated for CDO", null, session);
  }

  @Override
  protected EPackage getEPackage(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    for (;;)
    {
      InputDialog dialog = new HistoryInputDialog(shell, TITLE, "Enter a package URI:", HISTORY, null);
      if (dialog.open() != InputDialog.OK)
      {
        return null;
      }

      String uri = dialog.getValue();
      EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(uri);
      if (ePackage != null)
      {
        HISTORY.add(uri);
        return ePackage;
      }

      if (!MessageDialog.openQuestion(shell, TITLE, "Package " + uri + " not found.\nTry again?"))
      {
        return null;
      }
    }
  }
}
