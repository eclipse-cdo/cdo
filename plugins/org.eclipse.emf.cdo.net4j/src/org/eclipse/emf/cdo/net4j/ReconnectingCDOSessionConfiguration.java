/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.net4j;

/**
 * @author Caspar De Groot
 * @since 4.0
 */
public interface ReconnectingCDOSessionConfiguration extends RecoveringCDOSessionConfiguration
{
  public long getReconnectInterval();

  public void setReconnectInterval(long interval);

  public int getMaxReconnectAttempts();

  public void setMaxReconnectAttempts(int attempts);
}
