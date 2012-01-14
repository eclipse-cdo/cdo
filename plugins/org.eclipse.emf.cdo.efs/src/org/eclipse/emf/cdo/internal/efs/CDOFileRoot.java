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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOFileRoot extends AbstractFileStore
{
  private CDOFileSystem fileSystem;

  private String authority;

  private String repositoryName;

  private IPath branchPath;

  private long timeStamp;

  private transient CDOView view;

  public CDOFileRoot(CDOFileSystem fileSystem, String authority, String repositoryName, IPath branchPath, long timeStamp)
  {
    this.fileSystem = fileSystem;
    this.authority = authority;
    this.repositoryName = repositoryName;
    this.branchPath = branchPath;
    this.timeStamp = timeStamp;
  }

  @Override
  public CDOFileSystem getFileSystem()
  {
    return fileSystem;
  }

  public String getAuthority()
  {
    return authority;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public IPath getBranchPath()
  {
    return branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public IPath getPath()
  {
    return Path.EMPTY;
  }

  @Override
  public CDOView getView(IProgressMonitor monitor)
  {
    if (view == null)
    {
      view = fileSystem.getView(this, monitor);
    }

    return view;
  }

  @Override
  protected CDOResourceNode doGetResourceNode(IProgressMonitor monitor)
  {
    return getView(monitor).getRootResource();
  }

  @Override
  public IFileStore getParent()
  {
    return null;
  }

  @Override
  public String getName()
  {
    return "";
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor)
  {
    FileInfo info = new FileInfo(getName());
    info.setLastModified(EFS.NONE);
    info.setExists(true); // Root resource is always present
    info.setLength(EFS.NONE);
    info.setDirectory(true);
    info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
    return info;
  }

  @Deprecated
  @Override
  public IFileStore getChild(IPath path)
  {
    return new CDOFileStore(this, path);
  }

  @Override
  public IFileStore getChild(String name)
  {
    return getChild(new Path(name));
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    List<String> result = new ArrayList<String>();

    for (EObject object : getView(monitor).getRootResource().getContents())
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        result.add(node.getName());
      }
    }

    return result.toArray(new String[result.size()]);
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj.getClass() == CDOFileRoot.class)
    {
      CDOFileRoot that = (CDOFileRoot)obj;
      if (view != null && view == that.view)
      {
        // Optimization
        return true;
      }

      return authority.equals(that.authority) && repositoryName.equals(that.repositoryName)
          && branchPath.equals(that.branchPath) && timeStamp == that.timeStamp;
    }

    return false;
  }

  @Override
  protected int createHashCode()
  {
    return authority.hashCode() ^ repositoryName.hashCode() ^ branchPath.hashCode() ^ ObjectUtil.hashCode(timeStamp);
  }

  @Override
  public void appendURI(StringBuilder builder)
  {
    builder.append(fileSystem.getScheme());
    builder.append("://");
    builder.append(authority);
    builder.append("/");
    builder.append(repositoryName);
    builder.append("/");
    builder.append(branchPath.toPortableString());
    builder.append("/@");
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(timeStamp);
    }
  }
}
