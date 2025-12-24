/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

/**
 * TODO Consider file attributes when creating initially empty file
 *
 * @author Eike Stepper
 */
public class FileUserManagerFactory extends UserManagerFactory
{
  public static final String TYPE = "file"; //$NON-NLS-1$

  public FileUserManagerFactory()
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
