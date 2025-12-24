/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.signal.security;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * An abstract signal protocol that supports authentication.
 *
 * @author Christian W. Damus (CEA LIST)
 *
 * @since 4.3
 */
public class AuthenticatingSignalProtocol<INFRA_STRUCTURE extends IPasswordCredentialsProvider.Provider> extends SignalProtocol<INFRA_STRUCTURE>
    implements IPasswordCredentialsProvider.Provider
{
  public AuthenticatingSignalProtocol(String type)
  {
    super(type);
  }

  @Override
  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    return getInfraStructure().getCredentialsProvider();
  }
}
