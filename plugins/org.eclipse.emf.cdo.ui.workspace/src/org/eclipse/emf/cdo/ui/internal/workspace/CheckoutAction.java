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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
        CheckoutDialog dialog = new CheckoutDialog(part.getSite().getShell());
        if (dialog.open() == CheckoutDialog.OK)
        {
          final String projectName = dialog.getProjectName();
          new Job("Checking out...")
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              try
              {
                CheckoutAction.this.run((ICheckoutSource)element, projectName, monitor);
                return Status.OK_STATUS;
              }
              catch (Exception ex)
              {
                return new Status(IStatus.ERROR, OM.BUNDLE_ID, "Problem during check out: " + ex.getLocalizedMessage(),
                    ex);
              }
            }
          }.schedule();
        }
      }
    }
  }

  protected void run(ICheckoutSource checkoutSource, String projectName, IProgressMonitor monitor)
  {
    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(false);
    IDBAdapter dbAdapter = createLocalAdapter(projectName);
    IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(createLocalDataSource(projectName));
    IDBStore local = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);

    CDOWorkspaceBase base = createWorkspaceBase(projectName);
    CDOSessionConfigurationFactory remote = checkoutSource.getRepositoryLocation();
    CDOWorkspaceUtil.checkout(local, base, remote, checkoutSource.getBranchPath(), checkoutSource.getTimeStamp());
  }

  protected IDBAdapter createLocalAdapter(String projectName)
  {
    return new H2Adapter();
  }

  protected DataSource createLocalDataSource(String projectName)
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:_database/repo1");
    return dataSource;
  }

  protected CDOWorkspaceBase createWorkspaceBase(String projectName)
  {
    return CDOWorkspaceUtil.createFolderWorkspaceBase(new File(projectName));
  }
}
