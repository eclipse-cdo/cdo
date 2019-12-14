/*
 * Copyright (c) 2007-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectPackageDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RegisterGeneratedPackagesAction extends RegisterPackagesAction
{
  private static final String TITLE = Messages.getString("RegisterGeneratedPackagesAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("RegisterGeneratedPackagesAction.1"); //$NON-NLS-1$

  private EPackage.Registry registry = EPackage.Registry.INSTANCE;

  public RegisterGeneratedPackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    SelectPackageDialog dialog = new SelectPackageDialog(shell, Messages.getString("RegisterGeneratedPackagesAction.2"), //$NON-NLS-1$
        Messages.getString("RegisterGeneratedPackagesAction.3"), session.getPackageRegistry() //$NON-NLS-1$
            .keySet());

    if (dialog.open() == SelectPackageDialog.OK)
    {
      Set<String> checkedURIs = dialog.getCheckedURIs();
      List<EPackage> ePackages = new ArrayList<>(checkedURIs.size());
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
    @Override
    public String isValid(String uri)
    {
      if (uri == null || uri.length() == 0)
      {
        return ""; //$NON-NLS-1$
      }

      return registry.containsKey(uri) ? null : MessageFormat.format(Messages.getString("RegisterGeneratedPackagesAction.5"), uri); //$NON-NLS-1$
    }
  }
}
