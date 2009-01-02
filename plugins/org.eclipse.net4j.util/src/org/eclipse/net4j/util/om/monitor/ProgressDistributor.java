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

import org.eclipse.net4j.util.CheckUtil;

import java.util.Arrays;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ProgressDistributor<CONTEXT>
{
  private ProgressDistributable<CONTEXT>[] distributables;

  private double[] distribution;

  public ProgressDistributor(ProgressDistributable<CONTEXT>[] distributables, double[] distribution)
  {
    CheckUtil.checkArg(distributables, "distributables");
    CheckUtil.checkArg(distribution, "distribution");
    if (distributables.length == 0)
    {
      throw new IllegalArgumentException("distributables.length == 0");
    }

    if (distributables.length != distribution.length)
    {
      throw new IllegalArgumentException("distributables.length != distribution.length");
    }

    this.distributables = distributables;
    this.distribution = distribution;
  }

  public ProgressDistributor(ProgressDistributable<CONTEXT>[] distributables)
  {
    this(distributables, createDefaultDistribution(distributables.length));
  }

  public final void run(CONTEXT context, OMMonitor monitor) throws Exception
  {
    double[] distributionCopy;
    synchronized (this)
    {
      distributionCopy = new double[distribution.length];
      System.arraycopy(distribution, 0, distributionCopy, 0, distribution.length);
    }

    double total = OMMonitor.ZERO;
    for (int i = 0; i < distributionCopy.length; i++)
    {
      total += distributionCopy[i];
    }

    monitor.begin(total);

    try
    {
      double[] times = new double[distributables.length];
      for (int i = 0; i < distributables.length; i++)
      {
        ProgressDistributable<CONTEXT> distributable = distributables[i];
        int count = distributable.getLoopCount(context);
        double work = distributable.getLoopWork(context);

        OMMonitor distributableMonitor = monitor.fork(distributionCopy[i]);
        distributableMonitor.begin(work * count);

        try
        {
          long start = System.currentTimeMillis();
          for (int loop = 0; loop < count; loop++)
          {
            distributable.runLoop(loop, context, distributableMonitor);
          }

          times[i] = (double)(System.currentTimeMillis() - start) / count;
        }
        finally
        {
          distributableMonitor.done();
        }
      }

      synchronized (this)
      {
        distribute(distribution, times);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected abstract void distribute(double[] distribution, double[] times);

  private static double[] createDefaultDistribution(int count)
  {
    double[] distribution = new double[count];
    Arrays.fill(distribution, OMMonitor.ONE);
    return distribution;
  }

  /**
   * @author Eike Stepper
   */
  public static class Arithmetic<CONTEXT> extends ProgressDistributor<CONTEXT>
  {
    private long count;

    private double[] times;

    public Arithmetic(ProgressDistributable<CONTEXT>[] distributables, double[] distribution)
    {
      super(distributables, distribution);
    }

    public Arithmetic(ProgressDistributable<CONTEXT>[] distributables)
    {
      super(distributables);
    }

    @Override
    protected void distribute(double[] distribution, double[] times)
    {
      ++count;
      for (int i = 0; i < times.length; i++)
      {
        this.times[i] += times[i];
        distribution[i] = this.times[i] / count;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Geometric<CONTEXT> extends ProgressDistributor<CONTEXT>
  {
    public Geometric(ProgressDistributable<CONTEXT>[] distributables, double[] distribution)
    {
      super(distributables, distribution);
    }

    public Geometric(ProgressDistributable<CONTEXT>[] distributables)
    {
      super(distributables);
    }

    @Override
    protected void distribute(double[] distribution, double[] times)
    {
      for (int i = 0; i < times.length; i++)
      {
        distribution[i] = (distribution[i] + times[i]) / 2;
      }
    }
  }
}
