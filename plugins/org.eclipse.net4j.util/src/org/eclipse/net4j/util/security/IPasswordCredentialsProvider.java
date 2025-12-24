/*
 * Copyright (c) 2007, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.net4j.util.security;

/**
 * @author Eike Stepper
 */
public interface IPasswordCredentialsProvider extends ICredentialsProvider
{
  @Override
  public IPasswordCredentials getCredentials();

  /**
   * Interface implemented by protocol objects that can supply an
   * {@link IPasswordCredentialsProvider}.
   *
   * @author Christian W. Damus (CEA LIST)
   * @since 3.4
   */
  public static interface Provider
  {
    public IPasswordCredentialsProvider getCredentialsProvider();
  }
}
