/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - http://bugs.eclipse.org/244801
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.ui.dialogs.ExportResourceDialog;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ExportResourceAction extends ViewAction
{
  public static final String ID = "export-resource";

  private static final String TITLE = "Export Resource";

  public ExportResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, "Export a CDO resource", null, view);
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
    CDOView view = getView();

    // Source Resource
    Resource source = view.getResource(targetPath);
    List<EObject> sourceContents = new ArrayList<EObject>(source.getContents());

    // Target Resource
    EMFUtil.saveXMI(sourceURI.toString(), sourceContents);
  }
}
