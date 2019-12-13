/*
 * Copyright (c) 2008, 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306
 */
package org.eclipse.net4j.util.ui.security;

import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider2;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdateProvider;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class InteractiveCredentialsProvider implements IPasswordCredentialsProvider2, IPasswordCredentialsUpdateProvider
{
  public InteractiveCredentialsProvider()
  {
  }

  @Override
  public boolean isInteractive()
  {
    return true;
  }

  @Override
  public IPasswordCredentials getCredentials()
  {
    return getCredentials(null);
  }

  /**
   * @since 3.3
   */
  @Override
  public IPasswordCredentials getCredentials(final String realm)
  {
    final IPasswordCredentials[] credentials = new IPasswordCredentials[1];
    final Display display = UIUtil.getDisplay();
    display.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        CredentialsDialog dialog = new CredentialsDialog(UIUtil.getShell(), realm);
        if (dialog.open() == CredentialsDialog.OK)
        {
          credentials[0] = dialog.getCredentials();
        }
      }
    });

    return credentials[0];
  }

  /**
   * @since 3.4
   */
  @Override
  public IPasswordCredentialsUpdate getCredentialsUpdate(String userID, CredentialsUpdateOperation operation)
  {
    return getCredentialsUpdate(null, userID, operation);
  }

  /**
   * @since 3.4
   */
  @Override
  public IPasswordCredentialsUpdate getCredentialsUpdate(final String realm, final String userID, final CredentialsUpdateOperation operation)
  {
    final IPasswordCredentialsUpdate[] update = { null };
    final Display display = UIUtil.getDisplay();
    display.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        Shell shell = UIUtil.getShell();

        if (operation == CredentialsUpdateOperation.CHANGE_PASSWORD)
        {
          CredentialsUpdateDialog dialog = new CredentialsUpdateDialog(shell, realm, userID);
          if (dialog.open() == Window.OK)
          {
            update[0] = dialog.getCredentials();
          }
        }
        else
        {
          CredentialsResetDialog dialog = new CredentialsResetDialog(shell, realm, userID);
          if (dialog.open() == Window.OK)
          {
            update[0] = dialog.getCredentials();
            final String newPassword = new String(update[0].getNewPassword());

            MessageDialog msg = new MessageDialog(shell, Messages.getString("InteractiveCredentialsProvider.0"), null, //$NON-NLS-1$
                MessageFormat.format(Messages.getString("InteractiveCredentialsProvider.1"), //$NON-NLS-1$
                    userID, newPassword),
                MessageDialog.INFORMATION, new String[] { Messages.getString("InteractiveCredentialsProvider.2"), //$NON-NLS-1$
                    IDialogConstants.OK_LABEL },
                0)
            {

              @Override
              protected void buttonPressed(int buttonId)
              {
                if (buttonId == 0)
                {
                  copyToClipboard();
                  // Don't close the dialog
                }
                else
                {
                  // Close the dialog in the usual way
                  super.buttonPressed(IDialogConstants.OK_ID);
                }
              }

              private void copyToClipboard()
              {
                Clipboard clipboard = new Clipboard(getShell().getDisplay());

                try
                {
                  clipboard.setContents(new Object[] { newPassword }, new Transfer[] { TextTransfer.getInstance() });
                }
                finally
                {
                  clipboard.dispose();
                }
              }
            };

            msg.open();
          }
        }
      }
    });

    return update[0];
  }
}
