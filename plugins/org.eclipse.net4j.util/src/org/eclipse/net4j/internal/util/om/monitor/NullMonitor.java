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
package org.eclipse.net4j.internal.util.om.monitor;

/**
 * @author Eike Stepper
 */
public class NullMonitor extends Monitor
{
  public NullMonitor()
  {
    super(null, 0);
  }

  private NullMonitor(NullMonitor parent, int workFromParent)
  {
    super(parent, workFromParent);
  }

  @Override
  protected Monitor subMonitor(int workFromParent)
  {
    return new NullMonitor(this, workFromParent);
  }

  @Override
  protected void checkWork(int work)
  {
  }
}
