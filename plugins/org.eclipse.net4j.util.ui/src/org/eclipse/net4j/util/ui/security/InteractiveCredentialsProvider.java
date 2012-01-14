/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.security;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class InteractiveCredentialsProvider implements IPasswordCredentialsProvider
{
  public InteractiveCredentialsProvider()
  {
  }

  public boolean isInteractive()
  {
    return true;
  }

  public IPasswordCredentials getCredentials()
  {
    final IPasswordCredentials[] credentials = new IPasswordCredentials[1];
    final Display display = UIUtil.getDisplay();
    display.syncExec(new Runnable()
    {
      public void run()
      {
        CredentialsDialog dialog = new CredentialsDialog(new Shell(display));
        if (dialog.open() == CredentialsDialog.OK)
        {
          credentials[0] = dialog.getCredentials();
        }
      }
    });

    return credentials[0];
  }
}
