/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoriesView;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoryItemProvider;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutRepositoryPage extends CheckoutWizardPage
{
  private CDORepository repository;

  private boolean skip;

  private CDOSession session;

  private TableViewer tableViewer;

  public CheckoutRepositoryPage()
  {
    super("Repository", "Select the CDO model repository from which to checkout.");
  }

  public final CDORepository getRepository()
  {
    return repository;
  }

  public final void setRepository(CDORepository repository)
  {
    if (this.repository != repository)
    {
      releaseSession();

      log("Setting repository to " + repository);
      this.repository = repository;
      repositoryChanged(repository);
    }
  }

  public final void skip()
  {
    skip = true;
  }

  public CDOSession getSession()
  {
    if (session == null && repository != null)
    {
      log("Acquiring session from " + repository);
      session = repository.acquireSession();
    }

    return session;
  }

  private void releaseSession()
  {
    if (repository != null && session != null)
    {
      log("Releasing session of " + repository);
      repository.releaseSession();
      session = null;
    }
  }

  @Override
  public void dispose()
  {
    releaseSession();
    super.dispose();
  }

  @Override
  protected void createUI(final Composite parent)
  {
    TableColumnLayout tableLayout = new TableColumnLayout();

    Composite tableComposite = new Composite(parent, SWT.NONE);
    tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    tableComposite.setLayout(tableLayout);

    CDORepositoryItemProvider itemProvider = new CDORepositoryItemProvider();

    tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
    tableViewer.setContentProvider(itemProvider);
    tableViewer.setLabelProvider(itemProvider);
    tableViewer.setInput(CDOExplorerUtil.getRepositoryManager());
    tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        if (selection.size() == 1)
        {
          setRepository((CDORepository)selection.getFirstElement());
        }

        validate();
      }
    });

    if (repository == null)
    {
      CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
      CDORepository[] repositories = repositoryManager.getRepositories();
      if (repositories.length != 0)
      {
        setRepository(repositories[0]);
        tableViewer.setSelection(new StructuredSelection(repositories[0]));
      }
    }

    tableViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        showNextPage();
      }
    });

    Table table = tableViewer.getTable();
    table.setHeaderVisible(true);

    TableColumn repositoryColumn = new TableColumn(table, SWT.NONE);
    repositoryColumn.setText("Repository");
    tableLayout.setColumnData(repositoryColumn, new ColumnWeightData(100, 150, true));

    TableColumn modeColumn = new TableColumn(table, SWT.NONE);
    modeColumn.setText("Versioning Mode");
    tableLayout.setColumnData(modeColumn, new ColumnWeightData(0, 120, true));

    TableColumn idColumn = new TableColumn(table, SWT.NONE);
    idColumn.setText("ID Generation");
    tableLayout.setColumnData(idColumn, new ColumnWeightData(0, 100, true));

    GridLayout gridLayout = new GridLayout(1, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;

    Composite buttonComposite = new Composite(parent, SWT.NONE);
    buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
    buttonComposite.setLayout(gridLayout);

    Button newButton = new Button(buttonComposite, SWT.NONE);
    newButton.setText("New Repository...");
    newButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        Shell shell = parent.getShell();
        CDORepositoriesView.newRepository(shell);
      }
    });

    getShell().getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        pageActivated();
      }
    });
  }

  @Override
  protected void pageActivated()
  {
    if (repository != null)
    {
      tableViewer.setSelection(new StructuredSelection(repository));
    }

    if (skip)
    {
      skip = false;
      showNextPage();
    }
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    return repository != null;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty(CDOCheckoutImpl.PROP_REPOSITORY, repository.getID());
  }
}
