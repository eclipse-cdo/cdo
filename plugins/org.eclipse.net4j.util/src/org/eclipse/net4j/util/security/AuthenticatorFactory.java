/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
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
 * @since 3.3
 */
public abstract class AuthenticatorFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.authenticators"; //$NON-NLS-1$

  public AuthenticatorFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }
}
