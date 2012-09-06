/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public abstract class CDOTransferSystem
{
  private boolean readOnly;

  protected CDOTransferSystem(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  public abstract String getType();

  protected ResourceSet provideResourceSet()
  {
    return null;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public CDOTransferType getDefaultTransferType(CDOTransferElement element)
  {
    if (element.isDirectory())
    {
      return CDOTransferType.FOLDER;
    }

    return CDOTransferType.UNKNOWN;
  }

  public abstract URI getURI(IPath path);

  public CDOTransferElement getElement(String path)
  {
    return getElement(new Path(path));
  }

  public abstract CDOTransferElement getElement(IPath path);

  public abstract void createFolder(IPath path) throws IOException;

  public Resource createModel(ResourceSet resourceSet, IPath path) throws IOException
  {
    URI uri = getURI(path);
    return resourceSet.createResource(uri);
  }

  public abstract void createBinary(IPath path, InputStream source) throws IOException;

  public abstract void createText(IPath path, InputStream source, String encoding) throws IOException;
}
