/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;

/**
 * @author Martin Fluegge
 */
public class DawnEditorInput extends URIEditorInput implements IDawnEditorInput
{
  private CDOResource resource = null;

  public DawnEditorInput(URI uri)
  {
    super(uri);
  }

  @Override
  public CDOView getView()
  {
    return getResource().cdoView();
  }

  @Override
  public boolean isViewOwned()
  {
    return false;
  }

  @Override
  public String getResourcePath()
  {
    return getResource().getPath();
  }

  public void setResource(CDOResource resource)
  {
    this.resource = resource;
  }

  public CDOResource getResource()
  {
    return resource;
  }
}
