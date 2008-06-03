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
package org.eclipse.emf.cdo.internal.ui.preferences;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

import org.eclipse.swt.SWT;
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

  private TextAndDisable commitTimeout;

  public CDOPreferencePage()
  {
    super(org.eclipse.emf.internal.cdo.bundle.OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Composite composite = UIUtil.createGridComposite(parent, 1);
    ((GridLayout)composite.getLayout()).verticalSpacing = 5;
    composite.setLayoutData(UIUtil.createGridData());

    Group sessionGroup = new Group(composite, SWT.NONE);
    sessionGroup.setLayout(new GridLayout(2, false));
    sessionGroup.setText("Session Defaults");
    sessionGroup.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText("Repository name:");
    repositoryName = new Text(sessionGroup, SWT.BORDER);
    repositoryName.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText("User name:");
    userName = new Text(sessionGroup, SWT.BORDER);
    userName.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText("Connector description:");
    connectorDescription = new Text(sessionGroup, SWT.BORDER);
    connectorDescription.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText("Reference chunk size:");
    referenceChunkSize = new TextAndDisable(sessionGroup, SWT.BORDER, String.valueOf(CDORevision.UNCHUNKED));
    referenceChunkSize.setLayoutData(UIUtil.createGridData(true, false));

    Group viewGroup = new Group(composite, SWT.NONE);
    viewGroup.setLayout(new GridLayout(2, false));
    viewGroup.setText("View Defaults");
    viewGroup.setLayoutData(UIUtil.createGridData(true, false));

    new Label(viewGroup, SWT.NONE).setText("Preload chunk size:");
    preloadChunkSize = new TextAndDisable(viewGroup, SWT.BORDER, String.valueOf(CDOView.NO_PRELOAD));
    preloadChunkSize.setLayoutData(UIUtil.createGridData(true, false));

    new Label(viewGroup, SWT.NONE).setText("EMF invalidation notifications:");
    invalidationNotifications = new Button(viewGroup, SWT.CHECK);

    Group transactionGroup = new Group(composite, SWT.NONE);
    transactionGroup.setLayout(new GridLayout(2, false));
    transactionGroup.setText("Transaction Defaults");
    transactionGroup.setLayoutData(UIUtil.createGridData(true, false));

    new Label(transactionGroup, SWT.NONE).setText("Commit timeout (millis):");
    commitTimeout = new TextAndDisable(transactionGroup, SWT.BORDER, String.valueOf(BufferInputStream.NO_TIMEOUT))
    {
      @Override
      protected GridData createTextLayoutData()
      {
        GridData gd = super.createTextLayoutData();
        gd.widthHint = 64;
        return gd;
      }
    };
    commitTimeout.setLayoutData(UIUtil.createGridData(true, false));

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
    commitTimeout.setValue(String
        .valueOf(org.eclipse.emf.internal.cdo.bundle.OM.PREF_DEFAULT_COMMIT_TIMEOUT.getValue()));
  }

  @Override
  public boolean performOk()
  {
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_REPOSITORY_NAME.setValue(repositoryName.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_USER_NAME.setValue(userName.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_CONNECTOR_DESCRIPTION.setValue(connectorDescription.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_REFERENCE_CHUNK_SIZE.setValue(Integer.parseInt(referenceChunkSize
        .getValue()));
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_LOAD_REVISION_COLLECTION_CHUNK_SIZE.setValue(Integer
        .parseInt(preloadChunkSize.getValue()));
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_ENABLE_INVALIDATION_NOTIFICATIONS.setValue(invalidationNotifications
        .getSelection());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_DEFAULT_COMMIT_TIMEOUT.setValue(Long
        .parseLong(commitTimeout.getValue()));
    return super.performOk();
  }
}
