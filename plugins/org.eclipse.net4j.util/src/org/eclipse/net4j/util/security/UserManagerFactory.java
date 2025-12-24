/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.factory.Factory;

/**
 * @author Eike Stepper
 */
public abstract class UserManagerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.userManagers"; //$NON-NLS-1$

  public UserManagerFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }
}
