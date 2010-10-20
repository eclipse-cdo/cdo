/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace.internal.efs;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOResourceNodeStore extends AbstractFileStore
{
  private CDOWorkspaceStore workspaceStore;

  private IFileStore parent;

  private String name;

  public CDOResourceNodeStore(CDOWorkspaceStore workspaceStore, IFileStore parent, String name)
  {
    this.workspaceStore = workspaceStore;
    this.parent = parent;
    this.name = name;
  }

  public CDOWorkspaceStore getWorkspaceStore()
  {
    return workspaceStore;
  }

  @Override
  public IFileStore getParent()
  {
    return parent;
  }

  @Override
  public String getName()
  {
    return name;
  }

  public String getPath()
  {
    if (parent instanceof CDOResourceNodeStore)
    {
      return ((CDOResourceNodeStore)parent).getPath() + "/" + name;
    }

    return name;
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    List<String> childNames = new ArrayList<String>();
    CDOView view = null;

    try
    {
      view = workspaceStore.getWorkspace().openView();
      CDOResourceNode node = view.getResourceNode(getPath());
      if (node instanceof CDOResourceFolder)
      {
        CDOResourceFolder folder = (CDOResourceFolder)node;

        for (CDOResourceNode child : folder.getNodes())
        {
          childNames.add(child.getName());
        }
      }
    }
    finally
    {
      IOUtil.close(view);
    }

    return childNames.toArray(new String[childNames.size()]);
  }

  @Override
  public IFileStore getChild(String name)
  {
    return new CDOResourceNodeStore(workspaceStore, this, name);
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  {
    CDOView view = null;

    try
    {
      view = workspaceStore.getWorkspace().openView();
      CDOResourceNode node = view.getResourceNode(getPath());

      FileInfo info = new FileInfo(getName());
      info.setExists(true);
      info.setLength(EFS.NONE);
      info.setLastModified(EFS.NONE);
      info.setDirectory(node instanceof CDOResourceFolder);
      info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
      info.setAttribute(EFS.ATTRIBUTE_HIDDEN, false);
      return info;
    }
    finally
    {
      IOUtil.close(view);
    }
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    // TODO: implement CDOWorkspaceNodeStore.openInputStream(options, monitor)
    throw new UnsupportedOperationException();
  }
}
