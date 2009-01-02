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

    public int getLoopCount(CONTEXT context)
    {
      return loopCount;
    }

    public double getLoopWork(CONTEXT context)
    {
      return loopWork;
    }
  }
}
