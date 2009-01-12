/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.net4j.util.ui.widgets.PreferenceButton;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class OpenSessionDialog extends TitleAreaDialog
{
  public static final String TITLE = "Open Session";

  private IWorkbenchPage page;

  private PreferenceButton automaticButton;

  private SessionComposite composite;

  private boolean automaticPackageRegistry;

  private String serverDescription;

  private String repositoryName;

  public OpenSessionDialog(IWorkbenchPage page)
  {
    super(new Shell(page.getWorkbenchWindow().getShell()));
    this.page = page;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public IWorkbenchPage getPage()
  {
    return page;
  }

  public String getServerDescription()
  {
    return serverDescription;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public boolean isAutomaticPackageRegistry()
  {
    return automaticPackageRegistry;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(TITLE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    composite = new SessionComposite(parent, SWT.NONE);

    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    new Label(composite, SWT.NONE);
    automaticButton = new PreferenceButton(composite, SWT.CHECK, "Automatic Package Registry",
        OM.PREF_AUTOMATIC_PACKAGE_REGISTRY);

    return composite;
  }

  @Override
  protected void okPressed()
  {
    serverDescription = composite.getServerDescription();
    repositoryName = composite.getRepositoryName();
    automaticPackageRegistry = automaticButton.getSelection(true);
    super.okPressed();
  }

  public void closeWithSuccess()
  {
  }
}
