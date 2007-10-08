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
package org.eclipse.net4j.buddies.internal.ui.preferences;

import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

  private ModifyListener modifyListener = new ModifyListener()
  {
    public void modifyText(ModifyEvent e)
    {
      dialogChanged();
    }
  };

  public BuddiesPreferencePage()
  {
    super(OM.PREFS);
  }

  @Override
  protected Control createContents(Composite parent)
  {
    GridLayout grid = new GridLayout(2, false);
    grid.marginHeight = 0;
    grid.marginWidth = 0;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(grid);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    new Label(composite, SWT.NONE).setText("Server:");
    connectorDescription = new Text(composite, SWT.BORDER);
    connectorDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    connectorDescription.addModifyListener(modifyListener);

    new Label(composite, SWT.NONE).setText("User ID:");
    userID = new Text(composite, SWT.BORDER);
    userID.setLayoutData(new GridData(100, SWT.DEFAULT));
    userID.addModifyListener(modifyListener);

    new Label(composite, SWT.NONE).setText("Password:");
    password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
    password.setLayoutData(new GridData(100, SWT.DEFAULT));
    password.addModifyListener(modifyListener);

    new Label(composite, SWT.NONE);
    autoConnect = new Button(composite, SWT.CHECK);

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

  protected void dialogChanged()
  {
  }
}