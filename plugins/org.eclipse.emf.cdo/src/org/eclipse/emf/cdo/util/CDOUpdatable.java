/*
 * Copyright (c) 2010-2014, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

/**
 * Represents facilities that can receive
 * {@link org.eclipse.emf.cdo.session.CDOSession.Options#setPassiveUpdateEnabled(boolean) passive updates}.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOUpdatable
{
  public static final long NO_TIMEOUT = -1;

  /**
   * Returns the time stamp of the last commit operation. May not be accurate if
   * {@link org.eclipse.emf.cdo.session.CDOSession.Options#isPassiveUpdateEnabled() passive updates} are disabled.
   */
  public long getLastUpdateTime();

  /**
   * Blocks the calling thread until a commit operation with the given time stamp (or higher) has occurred.
   *
   * @param updateTime the time stamp of the update to wait for in milliseconds since Unix epoch.
   */
  public void waitForUpdate(long updateTime);

  /**
   * Blocks the calling thread until a commit operation with the given time stamp (or higher) has occurred or the given
   * timeout has expired.
   *
   * @param updateTime the time stamp of the update to wait for in milliseconds since Unix epoch.
   * @param timeoutMillis the maximum number of milliseconds to wait for the update to occur,
   *        or {@link CDOUpdatable#NO_TIMEOUT} to wait indefinitely.
   * @return <code>true</code> if the specified commit operation has occurred within the given timeout period,
   *         <code>false</code> otherwise.
   */
  public boolean waitForUpdate(long updateTime, long timeoutMillis);

  /**
   * @since 4.3
   */
  public boolean runAfterUpdate(long updateTime, Runnable runnable);
}
