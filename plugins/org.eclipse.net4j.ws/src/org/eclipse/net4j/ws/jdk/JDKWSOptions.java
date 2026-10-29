/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.ws.jdk;

import org.eclipse.net4j.buffer.IBuffer;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable connection settings for a JDK WebSocket provider factory.
 * <p>
 * The defaults are a ten-second opening-handshake timeout, a twenty-second heartbeat interval, and a maximum complete
 * WebSocket message size of {@link Short#MAX_VALUE} bytes. The message-size limit includes the Net4j frame header.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public final class JDKWSOptions
{
  private static final JDKWSOptions DEFAULTS = new Builder().build();

  private final Duration webSocketConnectTimeout;

  private final Duration heartbeatInterval;

  private final int maxMessageSize;

  private JDKWSOptions(Builder builder)
  {
    webSocketConnectTimeout = builder.webSocketConnectTimeout;
    heartbeatInterval = builder.heartbeatInterval;
    maxMessageSize = builder.maxMessageSize;
  }

  /**
   * Returns the defaults used by the original JDK provider overloads.
   *
   * @return immutable default options
   */
  public static JDKWSOptions defaults()
  {
    return DEFAULTS;
  }

  /**
   * Creates a builder initialized with provider defaults.
   *
   * @return a new options builder
   */
  public static Builder builder()
  {
    return new Builder();
  }

  /**
   * Returns the maximum time allowed for a WebSocket opening handshake.
   *
   * @return opening-handshake timeout
   */
  public Duration getWebSocketConnectTimeout()
  {
    return webSocketConnectTimeout;
  }

  /**
   * Returns the interval between unsolicited heartbeat Pong frames.
   *
   * @return heartbeat interval
   */
  public Duration getHeartbeatInterval()
  {
    return heartbeatInterval;
  }

  /**
   * Returns the maximum complete WebSocket message size, including the Net4j frame header.
   *
   * @return message-size limit in bytes
   */
  public int getMaxMessageSize()
  {
    return maxMessageSize;
  }

  /**
   * Builds immutable JDK WebSocket provider options.
   *
   * @author Eike Stepper
   */
  public static final class Builder
  {
    private Duration webSocketConnectTimeout = Duration.ofSeconds(10);

    private Duration heartbeatInterval = Duration.ofSeconds(20);

    private int maxMessageSize = Short.MAX_VALUE;

    private Builder()
    {
    }

    /**
     * Sets the maximum time allowed for an opening handshake.
     *
     * @param timeout positive timeout that converts to at least one millisecond
     * @return this builder
     */
    public Builder webSocketConnectTimeout(Duration timeout)
    {
      webSocketConnectTimeout = timeout;
      return this;
    }

    /**
     * Sets the interval between unsolicited heartbeat Pong frames.
     *
     * @param interval positive interval that converts to at least one millisecond
     * @return this builder
     */
    public Builder heartbeatInterval(Duration interval)
    {
      heartbeatInterval = interval;
      return this;
    }

    /**
     * Sets the maximum complete WebSocket message size, including its Net4j header.
     *
     * @param size message size from the Net4j header size through {@link Short#MAX_VALUE}
     * @return this builder
     */
    public Builder maxMessageSize(int size)
    {
      maxMessageSize = size;
      return this;
    }

    /**
     * Validates and creates immutable options.
     *
     * @return the immutable settings
     * @throws NullPointerException if either duration is {@code null}
     * @throws IllegalArgumentException if a duration does not convert to positive milliseconds or the message size is outside
     *           the Net4j frame limits
     */
    public JDKWSOptions build()
    {
      validateDuration(webSocketConnectTimeout, "webSocketConnectTimeout"); //$NON-NLS-1$
      validateDuration(heartbeatInterval, "heartbeatInterval"); //$NON-NLS-1$

      if (maxMessageSize < IBuffer.HEADER_SIZE || maxMessageSize > Short.MAX_VALUE)
      {
        throw new IllegalArgumentException("maxMessageSize must be between " + IBuffer.HEADER_SIZE + " and " + Short.MAX_VALUE); //$NON-NLS-1$
      }

      return new JDKWSOptions(this);
    }

    private static void validateDuration(Duration duration, String name)
    {
      Objects.requireNonNull(duration, name);
      long millis;

      try
      {
        millis = duration.toMillis();
      }
      catch (ArithmeticException ex)
      {
        throw new IllegalArgumentException(name + " must be representable in milliseconds", ex); //$NON-NLS-1$
      }

      if (duration.isZero() || duration.isNegative() || millis <= 0)
      {
        throw new IllegalArgumentException(name + " must convert to a positive millisecond value"); //$NON-NLS-1$
      }
    }
  }
}
