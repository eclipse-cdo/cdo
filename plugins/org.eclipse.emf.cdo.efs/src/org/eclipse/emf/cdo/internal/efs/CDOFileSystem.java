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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDOFileSystem extends FileSystem
{
  private Map<Pair<String, String>, CDOSession> sessions = new HashMap<Pair<String, String>, CDOSession>();

  private Map<URI, CDOView> views = new HashMap<URI, CDOView>();

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

    CDOFileRoot root = new CDOFileRoot(this, authority, repositoryName, branchPath, timeStamp);
    if (path.isEmpty())
    {
      return root;
    }

    return root.getFileStore(path);
  }

  public CDOView getView(CDOFileRoot root)
  {
    URI uri = root.toURI();
    CDOView view = views.get(uri);
    if (view == null)
    {
      String authority = root.getAuthority();
      String repositoryName = root.getRepositoryName();
      String branchPath = root.getBranchPath().toPortableString();
      long timeStamp = root.getTimeStamp();

      CDOSession session = getSession(authority, repositoryName);
      CDOBranchManager branchManager = session.getBranchManager();
      CDOBranch branch = branchManager.getBranch(branchPath);

      view = session.openView(branch, timeStamp);
      views.put(uri, view);
    }

    return view;
  }

  protected CDOSession getSession(String authority, String repositoryName)
  {
    Pair<String, String> sessionKey = new Pair<String, String>(authority, repositoryName);
    CDOSession session = sessions.get(sessionKey);
    if (session == null)
    {
      CDOSessionConfiguration configuration = createSessionConfiguration(authority, repositoryName);
      session = configuration.openSession();
      sessions.put(sessionKey, session);
    }
    return session;
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected abstract CDOSessionConfiguration createSessionConfiguration(String authority, String repositoryName);

  /**
   * @author Eike Stepper
   */
  public static abstract class Net4j extends CDOFileSystem
  {
    private String connectorType;

    protected Net4j(String connectorType)
    {
      this.connectorType = connectorType;
    }

    protected IConnector getConnector(String authority)
    {
      return (IConnector)getContainer().getElement("org.eclipse.net4j.connectors", connectorType, authority);
    }

    @Override
    protected CDOSessionConfiguration createSessionConfiguration(String authority, String repositoryName)
    {
      org.eclipse.emf.cdo.net4j.CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
      configuration.setConnector(getConnector(authority));
      configuration.setRepositoryName(repositoryName);
      return configuration;
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
        super("tcp");
      }
    }
  }
}
