/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class RegisterPackagesAction extends SessionAction
{
  private List<EPackage> ePackages;

  private int errors;

  public RegisterPackagesAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOSession session)
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
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
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
          OM.LOG.error(MessageFormat.format(Messages.getString("RegisterPackagesAction.0"), uri), ex); //$NON-NLS-1$
        }
      }
    }

    postRegistration(ePackages);
    if (errors != 0)
    {
      final String label = errors == 1 ? MessageFormat.format(Messages.getString("RegisterPackagesAction.1"), errors) //$NON-NLS-1$
          : MessageFormat.format(Messages.getString("RegisterPackagesAction.2"), errors); //$NON-NLS-1$
      getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          Shell shell = getShell();
          if (!shell.isDisposed())
          {
            MessageDialog.openError(shell, getText(), label);
          }
        }
      });
    }
  }

  protected void postRegistration(List<EPackage> ePackages)
  {
  }

  protected abstract List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session);
}
