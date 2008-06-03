/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.PreferenceHistory;
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

  private HistoryText connectorText;

  private Label exampleLabel;

  private HistoryText repositoryText;

  private PreferenceButton automaticButton;

  private PreferenceButton legacyButton;

  private String serverDescription;

  private String repositoryName;

  private boolean automaticPackageRegistry;

  private boolean legacyModelSupport;

  static
  {
    OM.PREF_LEGACY_MODEL_SUPPORT.setValue(FSMUtil.isLegacySystemAvailable());
  }

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

  public boolean isLegacyModelSupport()
  {
    return legacyModelSupport;
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
    connectorText = new HistoryText(composite, SWT.BORDER | SWT.SINGLE, connectorHistory);
    connectorText.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

    if (connectorHistory.isEmpty())
    {
      new Label(composite, SWT.NONE);
      exampleLabel = new Label(composite, SWT.NONE);
      exampleLabel.setText("for example 'tcp://estepper@dev.eclipse.org:2036'");
      exampleLabel.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    }

    new Label(composite, SWT.NONE).setText("Repository Name:");
    repositoryText = new HistoryText(composite, SWT.BORDER | SWT.SINGLE, repositoryHistory);
    repositoryText.getCombo().setLayoutData(new GridData(150, SWT.DEFAULT));

    new Label(composite, SWT.NONE);
    automaticButton = new PreferenceButton(composite, SWT.CHECK, "Automatic Package Registry",
        OM.PREF_AUTOMATIC_PACKAGE_REGISTY);

    new Label(composite, SWT.NONE);
    legacyButton = new PreferenceButton(composite, SWT.CHECK, "Legacy Model Support", OM.PREF_LEGACY_MODEL_SUPPORT);
    legacyButton.getButton().setEnabled(FSMUtil.isLegacySystemAvailable());

    connectorText.setFocus();
    connectorText.getCombo().addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent e)
      {
        if (exampleLabel != null)
        {
          exampleLabel.setVisible(true);
        }
      }

      public void focusLost(FocusEvent e)
      {
        if (exampleLabel != null)
        {
          exampleLabel.setVisible(false);
        }
      }
    });

    return composite;
  }

  @Override
  protected void okPressed()
  {
    serverDescription = connectorText.getText(true);
    repositoryName = repositoryText.getText(true);
    automaticPackageRegistry = automaticButton.getSelection(true);
    legacyModelSupport = legacyButton.getSelection(true);
    super.okPressed();
  }

  public void closeWithSuccess()
  {
  }
}
