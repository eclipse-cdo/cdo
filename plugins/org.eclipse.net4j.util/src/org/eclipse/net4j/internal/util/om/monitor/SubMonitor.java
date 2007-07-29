package org.eclipse.net4j.internal.util.om.monitor;

/**
 * @author Eike Stepper
 */
public class SubMonitor extends Monitor
{
  private Monitor parent;

  private int workFromParent;

  public SubMonitor(Monitor parent, int workFromParent)
  {
    this.parent = parent;
    this.workFromParent = workFromParent;
  }

  public Monitor getParent()
  {
    return parent;
  }

  public int getWorkFromParent()
  {
    return workFromParent;
  }

  @Override
  public void message(String msg, int level)
  {
    parent.message(msg, level + 1);
  }
}