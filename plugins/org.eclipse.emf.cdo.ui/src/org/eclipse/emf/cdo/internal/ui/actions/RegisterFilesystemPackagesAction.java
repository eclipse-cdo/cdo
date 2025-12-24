/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RegisterFilesystemPackagesAction extends RegisterPackagesAction
{
  private static final String TITLE = Messages.getString("RegisterFilesystemPackagesAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("RegisterFilesystemPackagesAction.1"); //$NON-NLS-1$

  private static final String[] FILTER_NAMES = { Messages.getString("RegisterFilesystemPackagesAction.2"), //$NON-NLS-1$
      Messages.getString("RegisterFilesystemPackagesAction.3"), //$NON-NLS-1$
      Messages.getString("RegisterFilesystemPackagesAction.4"), //$NON-NLS-1$
      Messages.getString("RegisterFilesystemPackagesAction.5") }; //$NON-NLS-1$

  private static final String[] FILTER_EXTENSIONS = { "ecore", "xmi", "xml", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public RegisterFilesystemPackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
    dialog.setFileName("*.ecore"); //$NON-NLS-1$
    dialog.setFilterNames(FILTER_NAMES);
    dialog.setFilterExtensions(FILTER_EXTENSIONS);
    if (dialog.open() != null)
    {
      String filterPath = dialog.getFilterPath();
      String[] fileNames = dialog.getFileNames();
      if (fileNames != null && fileNames.length != 0)
      {
        ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();
        List<EPackage> ePackages = new ArrayList<>(fileNames.length);
        for (String fileName : fileNames)
        {
          String path = filterPath + File.separator + fileName;
          URI uri = URI.createFileURI(path);
          Resource resource = resourceSet.getResource(uri, true);
          EPackage ePackage = (EPackage)resource.getContents().get(0);
          ePackages.add(ePackage);
        }

        return ePackages;
      }
    }

    return null;
  }
}
