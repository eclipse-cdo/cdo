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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RegisterCDOPackageAction extends RegisterPackageAction
{
  public static final IHistory<String> HISTORY = new PreferenceHistory(OM.PREF_HISTORY_REGISTER_CDO_PACKAGE);

  private static final String TITLE = "Register CDO Package";

  private EPackage.Registry registry = EPackage.Registry.INSTANCE;

  public RegisterCDOPackageAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, "Register a package generated for CDO", null, session);
  }

  @Override
  protected EPackage getEPackage(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    IInputValidator validator = new EPackageFactoryValidator();
    InputDialog dialog = new HistoryInputDialog(shell, TITLE, "Enter a package URI:", HISTORY, validator);
    if (dialog.open() == InputDialog.OK)
    {
      String uri = dialog.getValue();
      HISTORY.add(uri);
      return registry.getEPackage(uri);
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public class EPackageFactoryValidator implements IInputValidator
  {
    public String isValid(String uri)
    {
      return registry.containsKey(uri) ? null : "Package " + uri + " not found.";
    }
  }
}
