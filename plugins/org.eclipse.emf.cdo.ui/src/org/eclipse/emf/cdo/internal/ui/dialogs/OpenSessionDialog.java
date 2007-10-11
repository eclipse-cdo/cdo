/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.internal.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.HistoryText;
import org.eclipse.net4j.util.ui.widgets.PreferenceButton;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

  private IHistory<String> connectorHistory = new PreferenceHistory(OM.PREF_HISTORY_CONNECTORS);

  private IHistory<String> repositoryHistory = new PreferenceHistory(OM.PREF_HISTORY_REPOSITORIES);

  private HistoryText connector;

  private Label example;

  private HistoryText repository;

  private PreferenceButton legacy;

  private String serverDescription;

  private String repositoryName;

  private boolean legacySupport;

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

  public boolean isLegacySupport()
  {
    return legacySupport;
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
    Composite composite = new Composite((Composite)super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(2, false));

    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    new Label(composite, SWT.NONE).setText("Server Description:");
    connector = new HistoryText(composite, SWT.BORDER | SWT.SINGLE, connectorHistory);
    connector.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

    if (connectorHistory.isEmpty())
    {
      new Label(composite, SWT.NONE);
      example = new Label(composite, SWT.NONE);
      example.setText("for example 'tcp://estepper@dev.eclipse.org:2036'");
      example.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    }

    new Label(composite, SWT.NONE).setText("Repository Name:");
    repository = new HistoryText(composite, SWT.BORDER | SWT.SINGLE, repositoryHistory);
    repository.getCombo().setLayoutData(new GridData(150, SWT.DEFAULT));

    new Label(composite, SWT.NONE);
    legacy = new PreferenceButton(composite, SWT.CHECK, "Legacy Support", OM.PREF_LEGACY_SUPPORT);

    connector.setFocus();
    connector.getCombo().addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
        if (example != null)
        {
          example.setVisible(true);
        }
      }

      public void focusLost(FocusEvent e)
      {
        if (example != null)
        {
          example.setVisible(false);
        }
      }
    });

    return composite;
  }

  @Override
  protected void okPressed()
  {
    serverDescription = connector.getText();
    repositoryName = repository.getText();
    legacySupport = legacy.getSelection();

    connectorHistory.add(serverDescription);
    repositoryHistory.add(repositoryName);
    OM.PREF_LEGACY_SUPPORT.setValue(legacySupport);
    super.okPressed();
  }

  public void closeWithSuccess()
  {
  }
}
