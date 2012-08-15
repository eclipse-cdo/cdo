/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
      throw new ProtocolVersionException("Protocol version " + actualVersion + " does not match expected version "
          + expectedVersion);
    }
  }
}
