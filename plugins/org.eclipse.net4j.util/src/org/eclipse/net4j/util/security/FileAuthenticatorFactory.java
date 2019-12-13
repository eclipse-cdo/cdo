/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

/**
 * TODO Consider file attributes when creating initially empty file
 *
 * @author Eike Stepper
 * @since 3.3
 */
public class FileAuthenticatorFactory extends AuthenticatorFactory
{
  public static final String TYPE = "file"; //$NON-NLS-1$

  public FileAuthenticatorFactory()
  {
    super(TYPE);
  }

  @Override
  public FileUserManager create(String description)
  {
    FileUserManager userManager = new FileUserManager();
    userManager.setFileName(description);
    return userManager;
  }
}
