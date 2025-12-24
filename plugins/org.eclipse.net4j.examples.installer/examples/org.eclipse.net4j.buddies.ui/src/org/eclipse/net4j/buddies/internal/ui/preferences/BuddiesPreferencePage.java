/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.preferences;

import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.internal.buddies.bundle.OM;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class BuddiesPreferencePage extends OMPreferencePage
{
  private Text connectorDescription;

  private Text userID;

  private Text password;

  private Button autoConnect;

  public BuddiesPreferencePage()
  {
    super(OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Composite composite = UIUtil.createGridComposite(parent, 2);
    composite.setLayoutData(UIUtil.createGridData());

    new Label(composite, SWT.NONE).setText(Messages.getString("BuddiesPreferencePage_0")); //$NON-NLS-1$
    connectorDescription = new Text(composite, SWT.BORDER);
    connectorDescription.setLayoutData(UIUtil.createGridData(true, false));

    new Label(composite, SWT.NONE).setText(Messages.getString("BuddiesPreferencePage_1")); //$NON-NLS-1$
    userID = new Text(composite, SWT.BORDER);
    userID.setLayoutData(new GridData(100, SWT.DEFAULT));

    new Label(composite, SWT.NONE).setText(Messages.getString("BuddiesPreferencePage_2")); //$NON-NLS-1$
    password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
    password.setLayoutData(new GridData(100, SWT.DEFAULT));

    new Label(composite, SWT.NONE);
    autoConnect = new Button(composite, SWT.CHECK);
    autoConnect.setText(Messages.getString("BuddiesPreferencePage_3")); //$NON-NLS-1$

    initValues();
    return composite;
  }

  protected void initValues()
  {
    connectorDescription.setText(OM.PREF_CONNECTOR_DESCRIPTION.getValue());
    userID.setText(OM.PREF_USER_ID.getValue());
    password.setText(OM.PREF_PASSWORD.getValue());
    autoConnect.setSelection(OM.PREF_AUTO_CONNECT.getValue());
  }

  @Override
  public boolean performOk()
  {
    OM.PREF_CONNECTOR_DESCRIPTION.setValue(connectorDescription.getText());
    OM.PREF_USER_ID.setValue(userID.getText());
    OM.PREF_PASSWORD.setValue(password.getText());
    OM.PREF_AUTO_CONNECT.setValue(autoConnect.getSelection());
    return super.performOk();
  }
}
