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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectPackageDialog;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RegisterGeneratedPackagesAction extends RegisterPackagesAction
{
  private static final String TITLE = "Register Generated Packages";

  private static final String TOOL_TIP = "Register native, legacy or converted packages";

  private EPackage.Registry registry = EPackage.Registry.INSTANCE;

  public RegisterGeneratedPackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    SelectPackageDialog dialog = new SelectPackageDialog(shell, "Generated Packages",
        "Select one or more packages for registration with the CDO package registry", session.getPackageRegistry()
            .keySet());

    if (dialog.open() == SelectPackageDialog.OK)
    {
      Set<String> checkedURIs = dialog.getCheckedURIs();
      List<EPackage> ePackages = new ArrayList<EPackage>(checkedURIs.size());
      for (String uri : checkedURIs)
      {
        try
        {
          EPackage ePackage = registry.getEPackage(uri);
          ePackages.add(ePackage);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return ePackages;
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
      if (uri == null || uri.length() == 0)
      {
        return "";
      }

      return registry.containsKey(uri) ? null : "Package " + uri + " not found.";
    }
  }
}
