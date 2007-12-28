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
import org.eclipse.emf.cdo.util.EMFUtil;

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
  private static final String TITLE = "Register Filesystem Packages";

  private static final String TOOL_TIP = "Register dynamic packages from the filesystem";

  private static final String[] FILTER_NAMES = { "Ecore models (*.ecore)", "XMI files (*.xmi)", "XML files (*.xml)",
      "All files (*.*)" };

  private static final String[] FILTER_EXTENSIONS = { "ecore", "xmi", "xml", null };

  public RegisterFilesystemPackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session)
  {
    Shell shell = page.getWorkbenchWindow().getShell();
    FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
    dialog.setFileName("*.ecore");
    dialog.setFilterNames(FILTER_NAMES);
    dialog.setFilterExtensions(FILTER_EXTENSIONS);
    if (dialog.open() != null)
    {
      String filterPath = dialog.getFilterPath();
      String[] fileNames = dialog.getFileNames();
      if (fileNames != null && fileNames.length != 0)
      {
        ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();
        List<EPackage> ePackages = new ArrayList<EPackage>(fileNames.length);
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
