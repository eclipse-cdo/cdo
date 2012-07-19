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
package org.eclipse.emf.cdo.internal.efs;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.efs.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.net.URI;

/**
 * @author Eike Stepper
 */
public abstract class AbstractFileStore extends FileStore
{
  private transient int hashCode;

  private transient CDOResourceNode resourceNode;

  public AbstractFileStore()
  {
  }

  public abstract IPath getPath();

  public abstract CDOView getView(IProgressMonitor monitor);

  public final CDOTransaction openTransaction(IProgressMonitor monitor)
  {
    CDOSession session = getView(monitor).getSession();
    return session.openTransaction();
  }

  public final CDOResourceNode getResourceNode(IProgressMonitor monitor)
  {
    if (resourceNode == null)
    {
      resourceNode = doGetResourceNode(monitor);
    }

    return resourceNode;
  }

  protected abstract CDOResourceNode doGetResourceNode(IProgressMonitor monitor);

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    CDOTransaction transaction = null;

    try
    {
      transaction = openTransaction(monitor);
      resourceNode = transaction.createResourceFolder(getPath().toPortableString());
      transaction.commit(monitor);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      if (transaction != null)
      {
        transaction.close();
      }
    }

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
}
