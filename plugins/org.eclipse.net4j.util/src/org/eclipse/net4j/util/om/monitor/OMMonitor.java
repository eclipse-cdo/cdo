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
  public static final int UNKNOWN = -1;

  public String getTask();

  public void setTask(String task);

  public int getTotalWork();

  public boolean hasBegun();

  public void message(String msg);

  public void worked(int work, String msg);

  public void worked(int work);

  public void worked(String msg);

  public void worked();

  public void fork(int workFromParent, Runnable runnable, String msg);

  public void fork(int workFromParent, Runnable runnable);

  public void fork(Runnable runnable, String msg);

  public void fork(Runnable runnable);

  public OMSubMonitor fork(int workFromParent);

  public OMSubMonitor fork();
}
