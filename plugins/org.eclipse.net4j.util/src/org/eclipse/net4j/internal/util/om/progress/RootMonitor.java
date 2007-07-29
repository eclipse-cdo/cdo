/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.om.progress;

/**
 * @author Eike Stepper
 */
public abstract class RootMonitor extends Monitor
{
  private transient String task;

  public RootMonitor()
  {
  }

  @Override
  public void onSuccess(String success)
  {
    System.out.println(success);
  }

  @Override
  public String getTask()
  {
    return task;
  }

  @Override
  public void setTask(String task)
  {
    this.task = task;
    System.out.println(task);
  }
}
