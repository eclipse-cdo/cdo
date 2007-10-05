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
package org.eclipse.net4j.util.internal.ui.security;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * @author Eike Stepper
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
    CredentialsDialog dialog = new CredentialsDialog();
    if (dialog.open() == CredentialsDialog.OK)
    {
      return dialog.getCredentials();
    }

    return null;
  }
}
