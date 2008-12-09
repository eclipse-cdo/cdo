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
  private static final double ZERO = 0.0d;

  private OMMonitor parent;

  private int parentWork;

  private double sentToParent;

  private double scale;

  private boolean usedUp;

  public NestedMonitor(OMMonitor parent, int parentWork)
  {
    this.parent = parent;
    this.parentWork = parentWork > 0 ? parentWork : 0;
  }

  public OMMonitor getParent()
  {
    return parent;
  }

  public int getParentWork()
  {
    return parentWork;
  }

  @Override
  public synchronized void begin(int totalWork) throws MonitorCanceledException
  {
    super.begin(totalWork);
    scale = totalWork > ZERO ? (double)parentWork / (double)totalWork : ZERO;
  }

  @Override
  public synchronized void worked(double work) throws MonitorCanceledException
  {
    if (!usedUp)
    {
      super.worked(work);
      double realWork = work > ZERO ? scale * work : ZERO;
      parent.worked(realWork);
      sentToParent += realWork;
      if (sentToParent >= parentWork)
      {
        usedUp = true;
      }
    }
  }

  @Override
  public synchronized void done()
  {
    super.done();
    sentToParent = ZERO;
  }
}
