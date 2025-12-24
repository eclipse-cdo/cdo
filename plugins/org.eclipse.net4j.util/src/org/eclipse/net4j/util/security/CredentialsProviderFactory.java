/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class CredentialsProviderFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.security.credentialsProviders"; //$NON-NLS-1$

  public CredentialsProviderFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public abstract ICredentialsProvider create(String description) throws ProductCreationException;
}
