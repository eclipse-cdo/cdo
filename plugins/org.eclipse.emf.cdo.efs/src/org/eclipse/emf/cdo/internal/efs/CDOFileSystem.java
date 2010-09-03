/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * Martin Oberhuber (Wind River) - [170317] add symbolic link support to API
 * Martin Oberhuber (Wind River) - [183137] liblocalfile for solaris-sparc
 * Martin Oberhuber (Wind River) - [184433] liblocalfile for Linux x86_64
 * Martin Oberhuber (Wind River) - [184534] get attributes from native lib
 *******************************************************************************/
package org.eclipse.emf.cdo.internal.efs;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDOFileSystem extends FileSystem
{
  private Map<URI, IProjectDescription> projectDescriptions = new HashMap<URI, IProjectDescription>();

  protected CDOFileSystem()
  {
  }

  @Override
  public int attributes()
  {
    return /* EFS.ATTRIBUTE_READ_ONLY | */EFS.ATTRIBUTE_OTHER_READ | EFS.ATTRIBUTE_OTHER_WRITE;
  }

  @Override
  public boolean isCaseSensitive()
  {
    return true;
  }

  @Override
  public IFileStore getStore(URI uri)
  {
    String authority = uri.getAuthority();
    IPath path = new Path(uri.getPath());
    String repositoryName = path.segment(0);
    path = path.removeFirstSegments(1);

    IPath branchPath = Path.EMPTY;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

    while (path.segmentCount() != 0)
    {
      String segment = path.segment(0);
      path = path.removeFirstSegments(1);

      if (segment.startsWith("@"))
      {
        if (segment.length() != 1)
        {
          if (!segment.equals("@HEAD"))
          {
            timeStamp = Long.parseLong(segment.substring(1));
          }
        }

        break;
      }

      branchPath = branchPath.append(segment);
    }

    int segments = branchPath.segmentCount();
    if (segments == 0 || segments == 1 && !branchPath.segment(0).equals(CDOBranch.MAIN_BRANCH_NAME))
    {
      branchPath = new Path(CDOBranch.MAIN_BRANCH_NAME).append(branchPath);
    }

    CDORootStore rootStore = new CDORootStore(this, authority, repositoryName, branchPath, timeStamp);
    System.out.println(rootStore);
    if (path.isEmpty())
    {
      return rootStore;
    }

    return rootStore.getFileStore(path);
  }

  public void putProjectDescription(CDORootStore rootStore, IProjectDescription description) throws IOException
  {
    URI uri = rootStore.toURI();
    // TODO Remove the following. Seems to be checked by workspace and currently prevents projects from re-creation.
    // if (projectDescriptions.containsKey(uri))
    // {
    // throw new IOException("Location " + uri + " is already linked to project " + description.getName());
    // }

    projectDescriptions.put(uri, description);
  }

  public IProjectDescription getProjectDescription(CDORootStore rootStore)
  {
    return projectDescriptions.get(rootStore.toURI());
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Net4j extends CDOFileSystem
  {
    protected Net4j()
    {
    }

    /**
     * @author Eike Stepper
     */
    public static class TCP extends Net4j
    {
      /*
       * Must be public to be instantiatable by the extension registry.
       */
      public TCP()
      {
      }
    }
  }
}
