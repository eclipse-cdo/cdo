/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * Martin Oberhuber (Wind River) - [294429] Avoid substring baggage in FileInfo
 *******************************************************************************/
package org.eclipse.emf.cdo.internal.efs;

import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.net.URI;

/**
 * @author Eike Stepper
 */
public abstract class AbstractFileStore extends FileStore
{
  private transient int hashCode;

  public AbstractFileStore()
  {
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public IFileStore getChild(String name)
  {
    return getChild(new Path(name));
  }

  @Override
  public int hashCode()
  {
    if (hashCode == 0)
    {
      hashCode = createHashCode();
      if (hashCode == 0)
      {
        hashCode = 1;
      }
    }

    return hashCode;
  }

  protected abstract int createHashCode();

  @Override
  public final URI toURI()
  {
    StringBuilder builder = new StringBuilder();
    appendURI(builder);
    return URI.create(builder.toString());
  }

  public abstract void appendURI(StringBuilder builder);

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
