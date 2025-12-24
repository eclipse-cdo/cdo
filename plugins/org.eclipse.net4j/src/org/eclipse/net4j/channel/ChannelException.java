/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.channel;

/**
 * Thrown by an {@link IChannel} to indicate channel management problems.
 *
 * @see IChannelMultiplexer
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 */
public class ChannelException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ChannelException()
  {
  }

  public ChannelException(String message)
  {
    super(message);
  }

  public ChannelException(Throwable cause)
  {
    super(cause);
  }

  public ChannelException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
