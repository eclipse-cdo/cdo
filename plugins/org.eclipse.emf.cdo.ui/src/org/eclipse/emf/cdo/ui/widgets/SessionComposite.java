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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionProvider;

import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.HistoryText;
import org.eclipse.net4j.util.ui.widgets.PreferenceButton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Victor Roldan Betancort
 */
public class SessionComposite extends Composite implements CDOSessionProvider
{
  private IHistory<String> connectorHistory = new PreferenceHistory(OM.PREF_HISTORY_CONNECTORS);

  private IHistory<String> repositoryHistory = new PreferenceHistory(OM.PREF_HISTORY_REPOSITORIES);

  private HistoryText connectorText;

  private Label exampleLabel;

  private HistoryText repositoryText;

  private PreferenceButton automaticButton;

  private String sessionDescription;

  public SessionComposite(Composite parent, int style)
  {
    super(parent, style);
    setLayoutData(UIUtil.createGridData());
    setLayout(new GridLayout(2, false));

    new Label(this, SWT.NONE).setText("Server Description:");
    connectorText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, connectorHistory);
    connectorText.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

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

    new Label(this, SWT.NONE);
    automaticButton = new PreferenceButton(this, SWT.CHECK, "Automatic Package Registry",
        OM.PREF_AUTOMATIC_PACKAGE_REGISTRY);
  }

  @Override
  public void dispose()
  {
    assembleSessionDescription();
    super.dispose();
  }

  public CDOSession getSession()
  {
    String description = getSessionDescription();
    return (CDOSession)getContainer().getElement("org.eclipse.emf.cdo.sessions", "cdo", description);
  }

  public String getSessionDescription()
  {
    try
    {
      String description = sessionDescription;
      if (description == null)
      {
        description = assembleSessionDescription();
      }

      return description;
    }
    finally
    {
      sessionDescription = null;
    }
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected String assembleSessionDescription()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(connectorText.getText(true));
    builder.append("?repositoryName=");
    builder.append(repositoryText.getText(true));
    if (automaticButton.getSelection(true))
    {
      builder.append("&automaticPackageRegistry=true");
    }

    return builder.toString();
  }
}
