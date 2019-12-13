/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Eike Stepper
 */
public class RepositoryTypePage extends WizardPage
{
  private AbstractRepositoryPage nextPage;

  public RepositoryTypePage()
  {
    super("wizardPage");
    setImageDescriptor(OM.getImageDescriptor("icons/wiz/new_repo.gif"));
    setTitle("New Repository");
    setMessage("Select the type of the new repository.");
  }

  @Override
  public void createControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    container.setLayout(containerGridLayout);
    setControl(container);

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
    composite.setLayout(new GridLayout(1, false));

    NewRepositoryWizard wizard = getWizard();

    RepositoryRemotePage remotePage = wizard.getRemotePage();
    addChoice(composite, "Connect to an existing remote repository.", "icons/wiz/new_repo_remote.gif", remotePage, true);

    RepositoryClonePage clonePage = wizard.getClonePage();
    addChoice(composite, "Clone an existing remote repository.", "icons/wiz/new_repo_clone.gif", clonePage, false);

    RepositoryLocalPage localPage = wizard.getLocalPage();
    addChoice(composite, "Create a new local repository.", "icons/wiz/new_repo_local.gif", localPage, true);

    nextPage = remotePage;
    setPageComplete(true);
  }

  private Button addChoice(Composite composite, String text, String imagePath, final AbstractRepositoryPage nextPage, boolean enabled)
  {
    this.nextPage = nextPage;

    final SelectionListener listener = new SelectionListener()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        RepositoryTypePage.this.nextPage = nextPage;
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e)
      {
        widgetSelected(e);
        getContainer().showPage(nextPage);
      }
    };

    Button button = new Button(composite, SWT.RADIO);
    button.setText(text);
    button.setEnabled(enabled);
    button.addSelectionListener(listener);
    button.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseDoubleClick(MouseEvent e)
      {
        listener.widgetDefaultSelected(null);
      }
    });

    Label imageLabel = new Label(composite, SWT.NONE);
    imageLabel.setImage(OM.getImage(imagePath));
    imageLabel.setEnabled(enabled);

    new Label(composite, SWT.NONE);
    return button;
  }

  @Override
  public NewRepositoryWizard getWizard()
  {
    return (NewRepositoryWizard)super.getWizard();
  }

  @Override
  public AbstractRepositoryPage getNextPage()
  {
    return nextPage;
  }
}
