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
 * @since 2.0
 */
public class NestedMonitor extends Monitor
{
  private IMonitor parent;

  private int parentWork;

  private float propagateWork;

  public NestedMonitor(IMonitor parent, int parentWork)
  {
    this.parent = parent;
    this.parentWork = parentWork;
  }

  public IMonitor getParent()
  {
    return parent;
  }

  public int getParentWork()
  {
    return parentWork;
  }

  @Override
  public synchronized void worked(int work) throws MonitorCanceledException
  {
    super.worked(work);
    float ratio = getWork();
    ratio /= getTotalWork();
    propagateWork += ratio;

    int parentTicks = (int)Math.floor(propagateWork * getParentWork());
    if (parentTicks > 0)
    {
      parent.worked(parentTicks);
      float rest = parentTicks;
      rest /= getParentWork();
      propagateWork -= rest;
    }
  }
}
