/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.HistoryText;
import org.eclipse.net4j.util.ui.widgets.PreferenceButton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public class SessionComposite extends Composite
{
  private IHistory<String> connectorHistory = new PreferenceHistory(OM.PREF_HISTORY_CONNECTORS);

  private IHistory<String> repositoryHistory = new PreferenceHistory(OM.PREF_HISTORY_REPOSITORIES);

  private HistoryText connectorText;

  private Label exampleLabel;

  private HistoryText repositoryText;

  private PreferenceButton automaticButton;

  private String connectorDescription;

  private String repositoryName;

  private boolean automaticRegistry;

  public SessionComposite(Composite parent, int style)
  {
    super(parent, style);
    setLayoutData(UIUtil.createGridData());
    setLayout(new GridLayout(2, false));

    new Label(this, SWT.NONE).setText("Server Description:");
    connectorText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, connectorHistory);
    connectorText.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    connectorText.getCombo().addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        connectorDescription = connectorText.getText();
      }
    });

    if (connectorHistory.isEmpty())
    {
      new Label(this, SWT.NONE);
      exampleLabel = new Label(this, SWT.NONE);
      exampleLabel.setText("for example 'tcp://dev.eclipse.org:2036'");
      exampleLabel.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    }

    new Label(this, SWT.NONE).setText("Repository Name:");
    repositoryText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, repositoryHistory);
    repositoryText.getCombo().setLayoutData(new GridData(150, SWT.DEFAULT));
    repositoryText.getCombo().addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        repositoryName = repositoryText.getText();
      }
    });

    new Label(this, SWT.NONE);
    automaticButton = new PreferenceButton(this, SWT.CHECK, "Automatic Package Registry",
        OM.PREF_AUTOMATIC_PACKAGE_REGISTRY);
    automaticButton.getButton().addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        automaticRegistry = automaticButton.getSelection();
      }
    });

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

    connectorDescription = connectorText.getText();
    repositoryName = repositoryText.getText();
    automaticRegistry = automaticButton.getSelection();
  }

  public IHistory<String> getConnectorHistory()
  {
    return connectorHistory;
  }

  public IHistory<String> getRepositoryHistory()
  {
    return repositoryHistory;
  }

  public HistoryText getConnectorText()
  {
    return connectorText;
  }

  public Label getExampleLabel()
  {
    return exampleLabel;
  }

  public HistoryText getRepositoryText()
  {
    return repositoryText;
  }

  public PreferenceButton getAutomaticButton()
  {
    return automaticButton;
  }

  public boolean isAutomaticRegistry()
  {
    return automaticRegistry;
  }

  public String getSessionDescription()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(connectorDescription);
    builder.append("?repositoryName=");
    builder.append(repositoryName);
    if (automaticRegistry)
    {
      builder.append("&automaticPackageRegistry=true");
    }

    return builder.toString();
  }

  public void rememberSettings()
  {
    connectorText.getHistory().add(connectorDescription);
    repositoryText.getHistory().add(repositoryName);
    automaticButton.getPreference().setValue(automaticRegistry);
  }
}
