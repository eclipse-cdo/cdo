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
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class RegisterPackagesAction extends SessionAction
{
  private List<EPackage> ePackages;

  private int errors;

  public RegisterPackagesAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOSession session)
  {
    super(page, text, toolTipText, image, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    ePackages = getEPackages(getPage(), getSession());
    if (ePackages == null)
    {
      cancel();
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    errors = 0;
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    for (EPackage ePackage : ePackages)
    {
      EcoreUtil.freeze(ePackage);
      Resource resource = ePackage.eResource();
      URI uri = resource == null ? null : resource.getURI();

      try
      {
        packageRegistry.putEPackage(ePackage);
      }
      catch (RuntimeException ex)
      {
        ++errors;
        if (uri == null)
        {
          OM.LOG.error(ex);
        }
        else
        {
          OM.LOG.error("Failed to register package " + uri, ex);
        }
      }
    }

    postRegistration(ePackages);
    if (errors != 0)
    {
      final String label = String.valueOf(errors) + (errors == 1 ? " package has" : " packages have");
      final Shell shell = getShell();
      shell.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          MessageDialog.openError(shell, getText(), label
              + " not been registered due to errors.\nSee the log for details.");
        }
      });
    }
  }

  protected void postRegistration(List<EPackage> ePackages)
  {
  }

  protected abstract List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session);
}
