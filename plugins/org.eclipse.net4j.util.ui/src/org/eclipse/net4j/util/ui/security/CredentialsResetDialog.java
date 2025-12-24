/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.PasswordCredentialsUpdate;

import org.eclipse.swt.widgets.Shell;

import java.security.SecureRandom;
import java.text.MessageFormat;

/**
 * @since 3.4
 * @author Christian W. Damus (CEA LIST)
 */
public class CredentialsResetDialog extends CredentialsDialog
{
  private static final String TITLE = Messages.getString("CredentialsResetDialog_0"); //$NON-NLS-1$

  private static final String MESSAGE = Messages.getString("CredentialsResetDialog_1"); //$NON-NLS-1$

  private static final SecureRandom RANDOM = new SecureRandom();

  public CredentialsResetDialog(Shell shell, String userID)
  {
    this(shell, null, userID);
  }

  public CredentialsResetDialog(Shell shell, String realm, String userID)
  {
    super(shell, realm, TITLE, MessageFormat.format(MESSAGE, userID));
  }

  @Override
  public IPasswordCredentialsUpdate getCredentials()
  {
    return (IPasswordCredentialsUpdate)super.getCredentials();
  }

  @Override
  protected IPasswordCredentials createCredentials(String userID, char[] password)
  {
    char[] newPassword = generatePassword();
    return new PasswordCredentialsUpdate(userID, password, newPassword);
  }

  char[] generatePassword()
  {
    char[] result = new char[8];

    int base = Character.valueOf('!');
    int max = Character.valueOf('~') - base;

    for (int i = 0; i < result.length; i++)
    {
      result[i] = (char)(base + RANDOM.nextInt(max));
    }

    return result;
  }
}
