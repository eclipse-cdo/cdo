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
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ImportRootsAction extends EditingDomainAction
{
  public static final String ID = "import-roots";

  private static final String TITLE = "Import Roots";

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
  protected void doRun() throws Exception
  {
    EList<EObject> targetContents = targetResource.getContents();
    List<Resource> resources = getSourceResources();
    for (Resource resource : resources)
    {
      List<EObject> contents = new ArrayList<EObject>(resource.getContents());
      for (EObject root : contents)
      {
        targetContents.add(root);
      }
    }
  }

  protected List<Resource> getSourceResources()
  {
    ResourceSetImpl resourceSet = createSourceResourceSet();
    List<Resource> resources = new ArrayList<Resource>(uris.size());
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
