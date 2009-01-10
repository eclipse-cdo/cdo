/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.dialogs.ImportResourceDialog;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ImportResourceAction extends ViewAction
{
  public static final String ID = "import-resource";

  private static final String TITLE = "Import Resource";

  private URI sourceURI;

  private String targetPath;

  public ImportResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, "Import a CDO resource", null, view);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    ImportResourceDialog dialog = new ImportResourceDialog(getShell(), TITLE, SWT.OPEN);
    if (dialog.open() == ImportResourceDialog.OK)
    {
      List<URI> uris = dialog.getURIs();
      if (uris.size() == 1)
      {
        sourceURI = uris.get(0);
        targetPath = dialog.getTargetPath();
      }
      else
      {
        MessageDialog.openError(getShell(), TITLE, "A single URI must be entered!");
        cancel();
      }
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOTransaction transaction = getTransaction();

    // Source ResourceSet
    ResourceSet sourceSet = new ResourceSetImpl();
    Map<String, Object> map = sourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("*", new XMIResourceFactoryImpl());
    sourceSet.setPackageRegistry(transaction.getSession().getPackageRegistry());

    // Source Resource
    Resource source = sourceSet.getResource(sourceURI, true);
    List<EObject> sourceContents = new ArrayList<EObject>(source.getContents());

    // Target Resource
    Resource target = transaction.createResource(targetPath);
    EList<EObject> targetContents = target.getContents();

    // Move contents over
    for (EObject root : sourceContents)
    {
      targetContents.add(root);
    }
  }
}
