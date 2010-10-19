/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.workspace;

import org.eclipse.emf.cdo.location.ICheckoutSource;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.ui.internal.workspace.bundle.OM;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class CheckoutAction implements IObjectActionDelegate
{
  private IWorkbenchPart part;

  private ISelection selection;

  public CheckoutAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart part)
  {
    this.part = part;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  public void run(IAction action)
  {
    if (selection instanceof IStructuredSelection)
    {
      final Object element = ((IStructuredSelection)selection).getFirstElement();
      if (element instanceof ICheckoutSource)
      {
        final ICheckoutSource checkoutSource = (ICheckoutSource)element;
        String projectNameDefault = checkoutSource.getRepositoryLocation().getRepositoryName();

        Shell shell = part.getSite().getShell();
        CheckoutDialog dialog = new CheckoutDialog(shell, projectNameDefault);

        if (dialog.open() == CheckoutDialog.OK)
        {
          IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
          final IProject project = root.getProject(dialog.getProjectName());

          new Job("Checking out...")
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              try
              {
                CheckoutAction.this.run(checkoutSource, project, monitor);
                return Status.OK_STATUS;
              }
              catch (CoreException ex)
              {
                ex.printStackTrace();
                return ex.getStatus();
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
                return new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getLocalizedMessage(), ex);
              }
            }
          }.schedule();
        }
      }
    }
  }

  protected void run(ICheckoutSource checkoutSource, IProject project, IProgressMonitor monitor) throws Exception
  {
    project.create(new NullProgressMonitor());
    project.open(new NullProgressMonitor());

    IFolder cdoFolder = project.getFolder(".cdo");
    cdoFolder.create(true, true, new NullProgressMonitor());
    cdoFolder.setTeamPrivateMember(true);

    IFolder baseFolder = cdoFolder.getFolder("base");
    baseFolder.create(true, true, new NullProgressMonitor());

    IFolder localFolder = cdoFolder.getFolder("local");
    localFolder.create(true, true, new NullProgressMonitor());

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(false);
    IDBAdapter dbAdapter = createLocalAdapter();
    IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(createLocalDataSource(localFolder));
    IDBStore local = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);

    CDOWorkspaceBase base = createWorkspaceBase(baseFolder);
    CDOSessionConfigurationFactory remote = checkoutSource.getRepositoryLocation();
    CDOWorkspaceUtil.checkout(local, base, remote, checkoutSource.getBranchPath(), checkoutSource.getTimeStamp());
  }

  protected IDBAdapter createLocalAdapter()
  {
    return new H2Adapter();
  }

  protected DataSource createLocalDataSource(IFolder folder)
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder.getLocation().toString().replace('\\', '/'));
    return dataSource;
  }

  protected CDOWorkspaceBase createWorkspaceBase(IFolder folder)
  {
    return CDOWorkspaceUtil.createFolderWorkspaceBase(new File(folder.getLocation().toString()));
  }
}
