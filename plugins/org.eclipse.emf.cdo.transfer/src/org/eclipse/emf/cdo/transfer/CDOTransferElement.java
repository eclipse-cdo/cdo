/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.InputStream;

/**
 * An abstraction of the elements (such as files or folders) of a {@link CDOTransferSystem transfer system}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public abstract class CDOTransferElement
{
  public static final CDOTransferElement[] NO_CHILDREN = {};

  private final CDOTransferSystem system;

  protected CDOTransferElement(CDOTransferSystem system)
  {
    this.system = system;
  }

  public final CDOTransferSystem getSystem()
  {
    return system;
  }

  public abstract Object getNativeObject();

  public abstract boolean isDirectory();

  public abstract IPath getPath();

  public final String getName()
  {
    return getPath().lastSegment();
  }

  public final URI getURI()
  {
    IPath path = getPath();
    return system.getURI(path);
  }

  public final boolean isRoot()
  {
    return getParent() == null;
  }

  public final CDOTransferElement getParent()
  {
    IPath path = getPath();
    if (path.isRoot())
    {
      return null;
    }

    return system.getElement(path.removeLastSegments(1));
  }

  public final CDOTransferElement[] getChildren()
  {
    if (isDirectory())
    {
      return doGetChildren();
    }

    return NO_CHILDREN;
  }

  public final CDOTransferElement getChild(IPath path)
  {
    IPath childPath = getPath().append(path.makeRelative());
    return system.getElement(childPath);
  }

  public final CDOTransferElement getChild(String path)
  {
    return getChild(new Path(path));
  }

  public final InputStream openInputStream()
  {
    checkNotDirectory();
    return doOpenInputStream();
  }

  @Override
  public int hashCode()
  {
    String path = getPath().toString();
    final int prime = 31;
    int result = 1;
    result = prime * result + (system == null ? 0 : system.hashCode());
    result = prime * result + (path == null ? 0 : path.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (!(obj instanceof CDOTransferElement))
    {
      return false;
    }

    CDOTransferElement other = (CDOTransferElement)obj;
    if (system != other.system)
    {
      return false;
    }

    String path = getPath().toString();
    String otherPath = other.getPath().toString();
    if (path == null)
    {
      if (otherPath != null)
      {
        return false;
      }
    }
    else if (!path.equals(otherPath))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return getPath().toString();
  }

  protected abstract CDOTransferElement[] doGetChildren();

  protected abstract InputStream doOpenInputStream();

  private void checkNotDirectory()
  {
    if (isDirectory())
    {
      throw new IORuntimeException("Not supported for directories");
    }
  }
}
