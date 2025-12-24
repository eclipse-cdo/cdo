/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface ProgressDistributable<CONTEXT>
{
  public int getLoopCount(CONTEXT context);

  public double getLoopWork(CONTEXT context);

  public void runLoop(int index, CONTEXT context, OMMonitor monitor) throws Exception;

  /**
   * @author Eike Stepper
   */
  public static abstract class Default<CONTEXT> implements ProgressDistributable<CONTEXT>
  {
    private int loopCount = 1;

    private double loopWork = OMMonitor.ONE;

    public Default()
    {
    }

    public Default(int loopCount, double loopWork)
    {
      this.loopCount = loopCount;
      this.loopWork = loopWork;
    }

    @Override
    public int getLoopCount(CONTEXT context)
    {
      return loopCount;
    }

    @Override
    public double getLoopWork(CONTEXT context)
    {
      return loopWork;
    }
  }
}
