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
  public boolean isCanceled();

  /**
   * @since 2.0
   */
  public void checkCanceled() throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public void begin(int totalWork) throws MonitorCanceledException;

  public void worked(int work) throws MonitorCanceledException;

  /**
   * @since 2.0
   */
  public void done();

  public int getTotalWork();

  /**
   * @since 2.0
   */
  public int getWork();

  /**
   * @since 2.0
   */
  public OMMonitor fork(int work);
}
