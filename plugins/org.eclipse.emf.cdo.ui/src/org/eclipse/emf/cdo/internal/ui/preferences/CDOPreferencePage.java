/*
 * Copyright (c) 2007-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.preferences;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

import org.eclipse.swt.SWT;
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
    Composite composite = UIUtil.createGridComposite(parent, 1);
    ((GridLayout)composite.getLayout()).verticalSpacing = 5;
    composite.setLayoutData(UIUtil.createGridData());

    Group sessionGroup = new Group(composite, SWT.NONE);
    sessionGroup.setLayout(new GridLayout(2, false));
    sessionGroup.setText(Messages.getString("CDOPreferencePage.0")); //$NON-NLS-1$
    sessionGroup.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText(Messages.getString("CDOPreferencePage.1")); //$NON-NLS-1$
    repositoryName = new Text(sessionGroup, SWT.BORDER);
    repositoryName.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText(Messages.getString("CDOPreferencePage.2")); //$NON-NLS-1$
    userName = new Text(sessionGroup, SWT.BORDER);
    userName.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText(Messages.getString("CDOPreferencePage.3")); //$NON-NLS-1$
    connectorDescription = new Text(sessionGroup, SWT.BORDER);
    connectorDescription.setLayoutData(UIUtil.createGridData(true, false));

    new Label(sessionGroup, SWT.NONE).setText(Messages.getString("CDOPreferencePage.4")); //$NON-NLS-1$
    referenceChunkSize = new TextAndDisable(sessionGroup, SWT.BORDER, String.valueOf(CDORevision.UNCHUNKED));
    referenceChunkSize.setLayoutData(UIUtil.createGridData(true, false));

    Group viewGroup = new Group(composite, SWT.NONE);
    viewGroup.setLayout(new GridLayout(2, false));
    viewGroup.setText(Messages.getString("CDOPreferencePage.5")); //$NON-NLS-1$
    viewGroup.setLayoutData(UIUtil.createGridData(true, false));

    new Label(viewGroup, SWT.NONE).setText(Messages.getString("CDOPreferencePage.6")); //$NON-NLS-1$
    preloadChunkSize = new TextAndDisable(viewGroup, SWT.BORDER, String.valueOf(CDOView.Options.NO_REVISION_PREFETCHING));
    preloadChunkSize.setLayoutData(UIUtil.createGridData(true, false));

    new Label(viewGroup, SWT.NONE).setText(Messages.getString("CDOPreferencePage.7")); //$NON-NLS-1$
    invalidationNotifications = new Button(viewGroup, SWT.CHECK);

    initValues();
    return composite;
  }

  protected void initValues()
  {
    // TODO Move all preferences from core to UI!
    repositoryName.setText(org.eclipse.emf.internal.cdo.bundle.OM.PREF_REPOSITORY_NAME.getValue());
    userName.setText(org.eclipse.emf.internal.cdo.bundle.OM.PREF_USER_NAME.getValue());
    connectorDescription.setText(org.eclipse.emf.internal.cdo.bundle.OM.PREF_CONNECTOR_DESCRIPTION.getValue());
    referenceChunkSize.setValue(String.valueOf(org.eclipse.emf.internal.cdo.bundle.OM.PREF_COLLECTION_LOADING_CHUNK_SIZE.getValue()));
    preloadChunkSize.setValue(String.valueOf(org.eclipse.emf.internal.cdo.bundle.OM.PREF_REVISION_LOADING_CHUNK_SIZE.getValue()));
    invalidationNotifications.setSelection(org.eclipse.emf.internal.cdo.bundle.OM.PREF_ENABLE_INVALIDATION_NOTIFICATION.getValue());
  }

  @Override
  public boolean performOk()
  {
    // TODO Move all preferences from core to UI!
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_REPOSITORY_NAME.setValue(repositoryName.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_USER_NAME.setValue(userName.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_CONNECTOR_DESCRIPTION.setValue(connectorDescription.getText());
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_COLLECTION_LOADING_CHUNK_SIZE.setValue(Integer.parseInt(referenceChunkSize.getValue()));
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_REVISION_LOADING_CHUNK_SIZE.setValue(Integer.parseInt(preloadChunkSize.getValue()));
    org.eclipse.emf.internal.cdo.bundle.OM.PREF_ENABLE_INVALIDATION_NOTIFICATION.setValue(invalidationNotifications.getSelection());
    return super.performOk();
  }
}
