/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ImportRootsAction extends EditingDomainAction
{
  public static final String ID = "import-roots"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("ImportRootsAction.1"); //$NON-NLS-1$

  private CDOResource targetResource;

  private transient List<URI> uris;

  public ImportRootsAction()
  {
    super(TITLE + INTERACTIVE);
    setId(ID);
  }

  public CDOResource getTargetResource()
  {
    return targetResource;
  }

  public void setTargetResource(CDOResource targetResource)
  {
    this.targetResource = targetResource;
  }

  @Override
  public boolean isEnabled()
  {
    return targetResource != null && super.isEnabled();
  }

  @Override
  protected void preRun() throws Exception
  {
    ResourceDialog dialog = new ResourceDialog(getShell(), TITLE, SWT.OPEN | SWT.MULTI)
    {
      @Override
      protected boolean processResources()
      {
        return true;
      }
    };

    if (dialog.open() == ResourceDialog.OK)
    {
      uris = dialog.getURIs();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    EList<EObject> targetContents = targetResource.getContents();
    List<Resource> resources = getSourceResources();
    for (Resource resource : resources)
    {
      List<EObject> contents = new ArrayList<>(resource.getContents());
      for (EObject root : contents)
      {
        targetContents.add(root);
      }
    }
  }

  protected List<Resource> getSourceResources()
  {
    ResourceSetImpl resourceSet = createSourceResourceSet();
    List<Resource> resources = new ArrayList<>(uris.size());
    for (URI uri : uris)
    {
      Resource resource = resourceSet.getResource(uri, true);
      resources.add(resource);
    }

    return resources;
  }

  protected ResourceSetImpl createSourceResourceSet()
  {
    CDOView view = targetResource.cdoView();
    CDOSession session = view.getSession();
    CDOPackageRegistry packageRegistry = session.getPackageRegistry();

    ResourceSetImpl resourceSet = new ResourceSetImpl();
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }
}
