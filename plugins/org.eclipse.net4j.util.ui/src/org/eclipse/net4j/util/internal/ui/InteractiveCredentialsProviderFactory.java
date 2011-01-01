/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
