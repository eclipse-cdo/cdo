/*
 * Copyright (c) 2009-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.HistoryText;
import org.eclipse.net4j.util.ui.widgets.PreferenceButton;

import org.eclipse.emf.common.util.URI;

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
import org.eclipse.swt.widgets.Listener;

/**
 * Simple {@link org.eclipse.swt.widgets.Composite composite} allowing users to introduce connection information with a
 * repository. The widget recalls connection history and provides some additional functionality tweakers, as automatic
 * EPackage registration.
 *
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

    new Label(this, SWT.NONE).setText(Messages.getString("SessionComposite.0")); //$NON-NLS-1$
    connectorText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, connectorHistory);
    connectorText.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    connectorText.getCombo().addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        connectorDescription = connectorText.getText();
      }
    });

    if (connectorHistory.isEmpty())
    {
      new Label(this, SWT.NONE);
      exampleLabel = new Label(this, SWT.NONE);
      exampleLabel.setText(Messages.getString("SessionComposite.1")); //$NON-NLS-1$
      exampleLabel.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    }

    new Label(this, SWT.NONE).setText(Messages.getString("SessionComposite.2")); //$NON-NLS-1$
    repositoryText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, repositoryHistory);
    repositoryText.getCombo().setLayoutData(new GridData(150, SWT.DEFAULT));
    repositoryText.getCombo().addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        repositoryName = repositoryText.getText();
      }
    });

    new Label(this, SWT.NONE);
    automaticButton = new PreferenceButton(this, SWT.CHECK, Messages.getString("SessionComposite.3"), //$NON-NLS-1$
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
      @Override
      public void focusGained(FocusEvent e)
      {
        if (exampleLabel != null)
        {
          exampleLabel.setVisible(true);
        }
      }

      @Override
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
    if (connectorDescription.contains("?")) //$NON-NLS-1$
    {
      builder.append("&"); //$NON-NLS-1$
    }
    else
    {
      builder.append("?"); //$NON-NLS-1$
    }

    builder.append("repositoryName="); //$NON-NLS-1$
    builder.append(repositoryName);
    if (automaticRegistry)
    {
      builder.append("&automaticPackageRegistry=true"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  /**
   * @since 3.0
   */
  public boolean isDescriptionValid()
  {
    URI uri = URI.createURI(getSessionDescription());
    return uri.hasAuthority() && uri.host() != null && !StringUtil.isEmpty(uri.host()) && !StringUtil.isEmpty(repositoryName);
  }

  public void rememberSettings()
  {
    connectorText.getHistory().add(connectorDescription);
    repositoryText.getHistory().add(repositoryName);
    automaticButton.getPreference().setValue(automaticRegistry);
  }

  @Override
  public void addListener(int eventType, Listener listener)
  {
    super.addListener(eventType, listener);
    connectorText.addListener(eventType, listener);
    repositoryText.addListener(eventType, listener);
    automaticButton.addListener(eventType, listener);
  }

  @Override
  public void removeListener(int eventType, Listener listener)
  {
    super.removeListener(eventType, listener);
    connectorText.removeListener(eventType, listener);
    repositoryText.removeListener(eventType, listener);
    automaticButton.removeListener(eventType, listener);
  }
}
