/*
 * Copyright (c) 2012, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterPackagesAction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RegisterWorkspacePackagesAction extends RegisterPackagesAction
{
  private static final String TITLE = Messages.getString("RegisterWorkspacePackagesAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("RegisterWorkspacePackagesAction.1"); //$NON-NLS-1$

  public RegisterWorkspacePackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    OpenResourcesDialog dialog = new OpenResourcesDialog(shell);
    if (dialog.open() == OpenResourcesDialog.OK)
    {
      Object[] result = dialog.getResult();
      if (result != null && result.length != 0)
      {
        ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();
        resourceSet.getURIConverter().getURIMap().putAll(computePlatformURIMap());

        List<EPackage> ePackages = new ArrayList<EPackage>(result.length);
        for (Object object : result)
        {
          if (object instanceof IFile)
          {
            IFile file = (IFile)object;

            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
            Resource resource = resourceSet.getResource(uri, true);

            EPackage ePackage = (EPackage)resource.getContents().get(0);
            ePackages.add(ePackage);
          }
        }

        return ePackages;
      }
    }

    return null;
  }

  @SuppressWarnings("deprecation")
  private static Map<URI, URI> computePlatformURIMap()
  {
    try
    {
      return EcorePlugin.computePlatformURIMap(true);
    }
    catch (Throwable ex)
    {
      return EcorePlugin.computePlatformURIMap();
    }
  }
}
