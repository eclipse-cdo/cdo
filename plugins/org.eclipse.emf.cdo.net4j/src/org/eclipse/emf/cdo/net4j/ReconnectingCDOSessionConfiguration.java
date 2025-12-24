/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.net4j;

/**
 * A {@link RecoveringCDOSessionConfiguration session configuration} that recovers from network problems by attempting
 * to reconnect to the same repository in specific intervals.
 *
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ReconnectingCDOSessionConfiguration extends RecoveringCDOSessionConfiguration
{
  public long getReconnectInterval();

  public void setReconnectInterval(long interval);

  public int getMaxReconnectAttempts();

  public void setMaxReconnectAttempts(int attempts);
}
