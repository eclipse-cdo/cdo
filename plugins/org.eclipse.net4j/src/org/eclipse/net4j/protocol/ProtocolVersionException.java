/*
 * Copyright (c) 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.protocol;

import org.eclipse.net4j.Net4jUtil;

/**
 * An exception that indicates mismatch between the versions of two protocol peer implementations.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class ProtocolVersionException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public ProtocolVersionException()
  {
  }

  public ProtocolVersionException(String s)
  {
    super(s);
  }

  public ProtocolVersionException(Throwable cause)
  {
    super(cause);
  }

  public ProtocolVersionException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public static void checkVersion(IProtocol<?> protocol, int expectedVersion) throws ProtocolVersionException
  {
    int actualVersion = Net4jUtil.getProtocolVersion(protocol);
    if (actualVersion != expectedVersion)
    {
      throw new ProtocolVersionException("Protocol version " + actualVersion + " does not match expected version " + expectedVersion);
    }
  }
}
