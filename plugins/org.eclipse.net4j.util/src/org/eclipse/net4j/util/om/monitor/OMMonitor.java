/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.monitor;

/**
 * @author Eike Stepper
 */
public interface OMMonitor
{
  /**
   * @since 2.0
   */
  public static final double ZERO = 0;

  /**
   * @since 2.0
   */
  public static final double ONE = 1;

  /**
   * @since 2.0
   */
  public static final double TEN = 10;

  /**
   * @since 2.0
   */
  public static final double HUNDRED = 100;

  /**
   * @since 2.0
   */
  public static final int THOUSAND = 1000;

  /**
   * @since 2.0
   */
  public static final double DEFAULT_TIME_FACTOR = THOUSAND;

  /**
   * @since 2.0
   */
  public boolean isCanceled();

  /**
   * @since 2.0
   */
  public void checkCanceled() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public boolean hasBegun() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public OMMonitor begin(double totalWork) throws MonitorCanceledException;

  /**
   * Same as calling <code>begin(ONE)</code>.
   * 
   * @since 2.0
   */
  public OMMonitor begin() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public void worked(double work) throws MonitorCanceledException;

  /**
   * Same as calling <code>worked(ONE)</code>.
   * 
   * @since 2.0
   */
  public void worked() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public OMMonitor fork(double work);

  /**
   * Same as calling <code>fork(ONE)</code>.
   * 
   * @since 2.0
   */
  public OMMonitor fork();

  /**
   * @since 2.0
   */
  public Async forkAsync(double work);

  /**
   * Same as calling <code>forkAsync(ONE)</code>.
   * 
   * @since 2.0
   */
  public Async forkAsync();

  /**
   * @since 2.0
   */
  public void done();

  /**
   * @since 2.0
   */
  public double getTotalWork();

  /**
   * @since 2.0
   */
  public double getWork();

  /**
   * @since 2.0
   */
  public double getWorkPercent();

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface Async
  {
    public void stop();
  }
}
