/*
 * Copyright (c) 2013, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.ui.security;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.PasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.SecurityUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.4
 * @author Christian W. Damus (CEA LIST)
 */
public class CredentialsUpdateDialog extends CredentialsDialog
{
  private static final String TITLE = Messages.getString("CredentialsUpdateDialog_0"); //$NON-NLS-1$

  private static final String MESSAGE = Messages.getString("CredentialsUpdateDialog_1"); //$NON-NLS-1$

  private static final int HEIGHT = 275;

  private String userID;

  private Text newPasswordControl;

  private Text repeatNewPasswordControl;

  public CredentialsUpdateDialog(Shell shell)
  {
    this(shell, null, null);
  }

  public CredentialsUpdateDialog(Shell shell, String realm, String userID)
  {
    super(shell, realm, TITLE, MESSAGE);
    this.userID = userID;
  }

  @Override
  protected void configureShell(Shell newShell, int width, int height)
  {
    super.configureShell(newShell, width, HEIGHT);
  }

  @Override
  public IPasswordCredentialsUpdate getCredentials()
  {
    return (IPasswordCredentialsUpdate)super.getCredentials();
  }

  @Override
  protected IPasswordCredentials createCredentials(String userID, char[] password)
  {
    String newPassword = newPasswordControl.getText();
    return new PasswordCredentialsUpdate(userID, password, SecurityUtil.toCharArray(newPassword));
  }

  @Override
  protected Composite createCredentialsArea(Composite parent)
  {
    Composite result = super.createCredentialsArea(parent);

    ModifyListener newPasswordListener = new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        validateNewPassword();
      }
    };

    new Label(result, SWT.NONE).setText(Messages.getString("CredentialsUpdateDialog_2")); //$NON-NLS-1$
    newPasswordControl = new Text(result, SWT.BORDER | SWT.PASSWORD);
    newPasswordControl.setLayoutData(UIUtil.createGridData(true, false));
    newPasswordControl.addModifyListener(newPasswordListener);

    new Label(result, SWT.NONE).setText(Messages.getString("CredentialsUpdateDialog_3")); //$NON-NLS-1$
    repeatNewPasswordControl = new Text(result, SWT.BORDER | SWT.PASSWORD);
    repeatNewPasswordControl.setLayoutData(UIUtil.createGridData(true, false));
    repeatNewPasswordControl.addModifyListener(newPasswordListener);

    return result;
  }

  @Override
  protected Control createUserIDControl(Composite composite)
  {
    Text text = new Text(composite, SWT.BORDER);
    text.setText(StringUtil.safe(userID));
    text.setEnabled(false);
    return text;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    super.createButtonsForButtonBar(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(false);
  }

  void validateNewPassword()
  {
    String newPassword = newPasswordControl.getText().trim();
    if (newPassword.length() == 0)
    {
      error(Messages.getString("CredentialsUpdateDialog_4")); //$NON-NLS-1$
      return;
    }

    String verify = repeatNewPasswordControl.getText().trim();
    if (verify.length() == 0)
    {
      error(null);
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return;
    }

    if (!verify.equals(newPassword))
    {
      error(Messages.getString("CredentialsUpdateDialog_5")); //$NON-NLS-1$
      return;
    }

    error(null);
  }

  void error(String message)
  {
    setErrorMessage(message);
    getButton(IDialogConstants.OK_ID).setEnabled(message == null);
  }
}
