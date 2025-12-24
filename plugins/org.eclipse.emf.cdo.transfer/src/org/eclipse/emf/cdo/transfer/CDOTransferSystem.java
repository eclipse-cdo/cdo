/*
 * Copyright (c) 2012, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.IOException;
import java.io.InputStream;

/**
 * An abstraction of an end point system of a {@link CDOTransfer transfer}.
 * <p>
 * It's used for example in {@link CDOTransfer#getSourceSystem()} and {@link CDOTransfer#getTargetSystem()}.
 * The arguments of the map() methods in {@link CDOTransfer} are treated as {@link IPath paths} relative to the source transfer system.
 * The {@link CDOTransferMapping#getTarget() targets} of the created mappings are determined by paths relative to the target transfer system.
 *
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

    return null;
  }

  public abstract URI getURI(IPath path);

  public CDOTransferElement getElement(String path)
  {
    return getElement(new Path(path));
  }

  public abstract CDOTransferElement getElement(IPath path);

  public abstract CDOTransferElement getElement(URI uri);

  public abstract void createFolder(IPath path);

  public Resource createModel(ResourceSet resourceSet, IPath path)
  {
    URI uri = getURI(path);
    return resourceSet.createResource(uri);
  }

  public abstract void createBinary(IPath path, InputStream source, IProgressMonitor monitor);

  public abstract void createText(IPath path, InputStream source, String encoding, IProgressMonitor monitor);

  public void saveModels(EList<Resource> resources, IProgressMonitor monitor)
  {
    try
    {
      monitor.beginTask("", resources.size());

      for (Resource resource : resources)
      {
        ConcurrencyUtil.checkCancelation(monitor);
        monitor.subTask("Saving " + resource.getURI());
        resource.save(null);
        monitor.worked(1);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      monitor.done();
    }
  }
}
