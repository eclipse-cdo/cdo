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

import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public class CDOWorkspaceFileSystem extends FileSystem implements IResourceChangeListener
{
  public static final String SCHEME = "cdo.workspace";

  public static final String[] NO_CHILD_NAMES = {};

  private static CDOWorkspaceFileSystem instance;

  private Map<String, CDOWorkspaceStore> workspaceStores = new HashMap<String, CDOWorkspaceStore>();

  private boolean workspaceListenerRegistered;

  public CDOWorkspaceFileSystem()
  {
    instance = this;
  }

  public static CDOWorkspaceFileSystem getInstance()
  {
    return instance;
  }

  @Override
  public int attributes()
  {
    return EFS.ATTRIBUTE_OTHER_READ | EFS.ATTRIBUTE_OTHER_WRITE;
  }

  @Override
  public boolean isCaseSensitive()
  {
    return true;
  }

  @Override
  public IFileStore getStore(URI uri)
  {
    String path = uri.getSchemeSpecificPart();
    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    String name = tokenizer.nextToken();

    IFileStore store = getWorkspaceStore(name);

    while (tokenizer.hasMoreTokens())
    {
      name = tokenizer.nextToken();
      store = store.getChild(name);
    }

    return store;
  }

  public void resourceChanged(IResourceChangeEvent event)
  {
    IResourceDelta delta = event.getDelta();
    if (delta != null)
    {
      for (IResourceDelta projectDelta : delta.getAffectedChildren())
      {
        if (projectDelta.getKind() == IResourceDelta.REMOVED)
        {
          String name = projectDelta.getFullPath().segment(0);

          CDOWorkspaceStore store;
          synchronized (workspaceStores)
          {
            store = workspaceStores.remove(name);

            if (workspaceStores.isEmpty())
            {
              ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
              workspaceListenerRegistered = false;
            }
          }

          if (store != null)
          {
            store.dispose();
          }
        }
      }
    }
  }

  public CDOWorkspaceStore addWorkspaceStore(String name, CDOWorkspace workspace)
  {
    synchronized (workspaceStores)
    {
      CDOWorkspaceStore store = createWorkspaceStore(name);
      store.setWorkspace(workspace);
      addWorkspaceStore(store);
      return store;
    }
  }

  private void addWorkspaceStore(IFileStore store)
  {
    workspaceStores.put(store.getName(), (CDOWorkspaceStore)store);

    if (!workspaceListenerRegistered)
    {
      ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
      workspaceListenerRegistered = true;
    }
  }

  private IFileStore getWorkspaceStore(String name)
  {
    synchronized (workspaceStores)
    {
      IFileStore store = workspaceStores.get(name);
      if (store == null)
      {
        store = createWorkspaceStore(name);
        addWorkspaceStore(store);
      }

      return store;
    }
  }

  private CDOWorkspaceStore createWorkspaceStore(String name)
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IPath location = root.getLocation().append(name);
    return new CDOWorkspaceStore(name, new File(location.toOSString()));
  }
}
