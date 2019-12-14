/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - bug 244801
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.dialogs.ExportResourceDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ExportResourceAction extends AbstractViewAction
{
  public static final String ID = "export-resource"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("ExportResourceAction.1"); //$NON-NLS-1$

  public ExportResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, Messages.getString("ExportResourceAction.2"), //$NON-NLS-1$
        SharedIcons.getDescriptor(SharedIcons.ETOOL_EXPORT), view);
    setId(ID);
  }

  private URI sourceURI;

  private String targetPath;

  @Override
  protected void preRun() throws Exception
  {
    ExportResourceDialog dialog = new ExportResourceDialog(getShell(), TITLE, SWT.OPEN);
    if (dialog.open() == ExportResourceDialog.OK)
    {
      List<URI> uris = dialog.getURIs();
      if (uris.size() == 1)
      {
        sourceURI = uris.get(0);
        targetPath = dialog.getTargetPath();
      }
      else
      {
        MessageDialog.openError(getShell(), TITLE, Messages.getString("ExportResourceAction.3")); //$NON-NLS-1$
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
    CDOView view = getView();

    // Source Resource
    Resource source = view.getResource(targetPath);
    List<EObject> sourceContents = new ArrayList<>(source.getContents());
    exportObjects(sourceContents);
  }

  private void exportObjects(List<EObject> sourceContents)
  {
    // Target Resource
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    Resource resource = resourceSet.createResource(sourceURI);

    Collection<EObject> copiedRoots = EcoreUtil.copyAll(sourceContents);
    resource.getContents().addAll(copiedRoots);

    try
    {
      resource.save(null);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
