/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace.efs;

import org.eclipse.emf.cdo.location.ICheckoutSource;
import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.location.IRepositoryLocationManager;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceConfiguration;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;
import org.eclipse.emf.cdo.workspace.internal.efs.CDOWorkspaceFileSystem;
import org.eclipse.emf.cdo.workspace.internal.efs.CDOWorkspaceStore;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * This class is the main entry point to the {@link org.eclipse.core.filesystem.EFS Eclipse File System (EFS)} implementation
 * for CDO {@link org.eclipse.emf.cdo.workspace.CDOWorkspace workspaces}.
 * @author Eike Stepper
 */
public final class CDOFS
{
  private CDOFS()
  {
  }

  public static CDOWorkspace open(String projectName, File projectFolder) throws Exception
  {
    IDBStore local = createLocalStore(projectFolder);
    CDOWorkspaceBase base = createWorkspaceBase(new File(projectFolder, "base"));
    IRepositoryLocation remote = readRepositoryLocation(projectFolder);

    CDOWorkspaceConfiguration config = CDOWorkspaceUtil.createWorkspaceConfiguration();
    config.setStore(local);
    config.setBase(base);
    config.setRemote(remote);

    CDOWorkspace workspace = config.open();
    return workspace;
  }

  public static void checkout(ICheckoutSource checkoutSource, String projectName, IProgressMonitor monitor)
      throws Exception
  {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IWorkspaceRoot root = workspace.getRoot();
    IProject project = root.getProject(projectName);
    if (project.exists())
    {
      throw new IOException("Project " + projectName + " already exists");
    }

    File projectFolder = root.getLocation().append(projectName).toFile();
    URI uri = checkout(checkoutSource, projectName, projectFolder);

    IProjectDescription description = workspace.newProjectDescription(projectName);
    description.setLocationURI(uri);

    project.create(description, new NullProgressMonitor());
    if (!project.isOpen())
    {
      project.open(new NullProgressMonitor());
    }
  }

  private static URI checkout(ICheckoutSource checkoutSource, String projectName, File projectFolder) throws Exception
  {
    IDBStore local = createLocalStore(projectFolder);
    CDOWorkspaceBase base = createWorkspaceBase(new File(projectFolder, "base"));

    IRepositoryLocation remote = checkoutSource.getRepositoryLocation();
    writeRepositoryLocation(projectFolder, remote);

    String branchPath = checkoutSource.getBranchPath();
    long timeStamp = checkoutSource.getTimeStamp();

    CDOWorkspaceConfiguration config = CDOWorkspaceUtil.createWorkspaceConfiguration();
    config.setStore(local);
    config.setBase(base);
    config.setRemote(remote);
    config.setBranchPath(branchPath);
    config.setTimeStamp(timeStamp);

    CDOWorkspace workspace = config.checkout();
    CDOWorkspaceStore store = getFileSystem().addWorkspaceStore(projectName, workspace);
    return store.toURI();
  }

  private static CDOWorkspaceFileSystem getFileSystem() throws CoreException
  {
    return (CDOWorkspaceFileSystem)EFS.getFileSystem(CDOWorkspaceFileSystem.SCHEME);
  }

  private static IDBStore createLocalStore(File projectFolder)
  {
    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(false);
    IDBAdapter dbAdapter = createLocalAdapter();
    IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(createLocalDataSource(new File(
        projectFolder, "local")));
    IDBStore local = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
    return local;
  }

  private static IDBAdapter createLocalAdapter()
  {
    return new H2Adapter();
  }

  private static DataSource createLocalDataSource(File folder)
  {
    folder.mkdirs();
    String path = folder.getAbsolutePath().replace('\\', '/');

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + path + "/db");
    return dataSource;
  }

  private static CDOWorkspaceBase createWorkspaceBase(File folder)
  {
    folder.mkdirs();
    return CDOWorkspaceUtil.createFolderWorkspaceBase(folder);
  }

  private static File getRemotePropertiesFile(File projectFolder)
  {
    return new File(projectFolder, "remote.properties");
  }

  private static void writeRepositoryLocation(File projectFolder, IRepositoryLocation remote) throws IOException
  {
    FileOutputStream out = null;

    try
    {
      out = new FileOutputStream(getRemotePropertiesFile(projectFolder));
      remote.write(out);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  private static IRepositoryLocation readRepositoryLocation(File projectFolder) throws IOException
  {
    FileInputStream in = null;

    try
    {
      in = new FileInputStream(getRemotePropertiesFile(projectFolder));
      return IRepositoryLocationManager.INSTANCE.addRepositoryLocation(in);
    }
    finally
    {
      IOUtil.close(in);
    }
  }
}
