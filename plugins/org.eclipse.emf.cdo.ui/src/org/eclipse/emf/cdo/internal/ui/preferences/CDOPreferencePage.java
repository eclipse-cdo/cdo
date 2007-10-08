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
package org.eclipse.emf.cdo.internal.ui.preferences;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class CDOPreferencePage extends OMPreferencePage
{
  private Text repositoryName;

  private Text userName;

  private Text connectorDescription;

  private TextAndDisable referenceChunkSize;

  private TextAndDisable preloadChunkSize;

  private Button invalidationNotifications;

  public CDOPreferencePage()
  {
    super(org.eclipse.emf.internal.cdo.bundle.OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    GridLayout grid = new GridLayout(1, false);
    grid.marginHeight = 0;
    grid.marginWidth = 0;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(grid);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    Group sessionGroup = new Group(composite, SWT.NONE);
    sessionGroup.setText("Session Defaults");
    sessionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    sessionGroup.setLayout(new GridLayout(2, false));

    new Label(sessionGroup, SWT.NONE).setText("Repository name:");
    repositoryName = new Text(sessionGroup, SWT.BORDER);
    repositoryName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    new Label(sessionGroup, SWT.NONE).setText("User name:");
    userName = new Text(sessionGroup, SWT.BORDER);
    userName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    new Label(sessionGroup, SWT.NONE).setText("Connector description:");
    connectorDescription = new Text(sessionGroup, SWT.BORDER);
    connectorDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    new Label(sessionGroup, SWT.NONE).setText("Reference chunk size:");
    referenceChunkSize = new TextAndDisable(sessionGroup, SWT.BORDER, String.valueOf(CDORevision.UNCHUNKED));
    referenceChunkSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Group viewGroup = new Group(composite, SWT.NONE);
    viewGroup.setText("View Defaults");
    viewGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    viewGroup.setLayout(new GridLayout(2, false));

    new Label(viewGroup, SWT.NONE).setText("Preload chunk size:");
    preloadChunkSize = new TextAndDisable(viewGroup, SWT.BORDER, String.valueOf(CDOView.NO_PRELOAD));
    preloadChunkSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    new Label(viewGroup, SWT.NONE).setText("EMF invalidation notifications:");
    invalidationNotifications = new Button(viewGroup, SWT.CHECK);

    initValues();
    return composite;
  }

  protected void initValues()
  {
    repositoryName.setText(org.eclipse.emf.internal.cdo.bundle.OM.PREF_REPOSITORY_NAME.getValue());
    userName.setText(org.eclipse.emf.internal.cdo.bundle.OM.PREF_USER_NAME.getValue());
    connectorDescription.setText(org.eclipse.emf.internal.cdo.bundle.OM.PREF_CONNECTOR_DESCRIPTION.getValue());
    referenceChunkSize.setValue(String.valueOf(org.eclipse.emf.internal.cdo.bundle.OM.PREF_REFERENCE_CHUNK_SIZE
        .getValue()));
    preloadChunkSize.setValue(String
        .valueOf(org.eclipse.emf.internal.cdo.bundle.OM.PREF_LOAD_REVISION_COLLECTION_CHUNK_SIZE.getValue()));
    invalidationNotifications
        .setSelection(org.eclipse.emf.internal.cdo.bundle.OM.PREF_ENABLE_INVALIDATION_NOTIFICATIONS.getValue());
  }

  @Override
  public boolean performOk()
  {
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_REPOSITORY_NAME.setValue(repositoryName.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_USER_NAME.setValue(userName.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_CONNECTOR_DESCRIPTION.setValue(connectorDescription.getText());

    int v1 = Integer.parseInt(referenceChunkSize.getText().getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_REFERENCE_CHUNK_SIZE.setValue(v1);

    int v2 = Integer.parseInt(preloadChunkSize.getText().getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_LOAD_REVISION_COLLECTION_CHUNK_SIZE.setValue(v2);

    boolean v3 = invalidationNotifications.getSelection();
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_ENABLE_INVALIDATION_NOTIFICATIONS.setValue(v3);

    return super.performOk();
  }

  private static final class TextAndDisable extends Composite implements SelectionListener, ModifyListener
  {
    private Text text;

    private Button disabled;

    private String disabledValue;

    public TextAndDisable(Composite parent, int textStyle, String disabledValue)
    {
      super(parent, SWT.NONE);
      this.disabledValue = disabledValue;

      GridLayout grid = new GridLayout(2, false);
      grid.marginHeight = 0;
      grid.marginWidth = 0;
      setLayout(grid);

      GridData gd = new GridData();
      gd.widthHint = 32;

      text = new Text(this, textStyle);
      text.addModifyListener(this);
      text.setLayoutData(gd);

      disabled = new Button(this, SWT.CHECK);
      disabled.setText("Disabled");
      disabled.addSelectionListener(this);
      disabled.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    }

    public Text getText()
    {
      return text;
    }

    public Button getButton()
    {
      return disabled;
    }

    public boolean isDisabled()
    {
      return disabled.getSelection();
    }

    public void setDisabled(boolean disabled)
    {
      this.disabled.setSelection(disabled);
      widgetSelected(null);
    }

    public void setValue(String value)
    {
      text.setText(value);
      setDisabled(ObjectUtil.equals(value, disabledValue));
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
      widgetSelected(e);
    }

    public void widgetSelected(SelectionEvent e)
    {
      if (isDisabled())
      {
        text.setText(disabledValue);
        text.setEnabled(false);
      }
      else
      {
        text.setEnabled(true);
      }
    }

    public void modifyText(ModifyEvent e)
    {
    }
  }
}