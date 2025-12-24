/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.security.CredentialsProviderFactory;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.ui.UIUtil;

/**
 * @author Eike Stepper
 */
public class InteractiveCredentialsProviderFactory extends CredentialsProviderFactory
{
  public static final String TYPE = "interactive"; //$NON-NLS-1$

  public InteractiveCredentialsProviderFactory()
  {
    super(TYPE);
  }

  @Override
  public IPasswordCredentialsProvider create(String description) throws ProductCreationException
  {
    return UIUtil.createInteractiveCredentialsProvider();
  }
}
