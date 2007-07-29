package org.eclipse.net4j.internal.util.om.progress;

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
  public void onSuccess(String success)
  {
    parent.onSuccess(success);
  }

  @Override
  public String getTask()
  {
    return parent.getTask();
  }

  @Override
  public void setTask(String task)
  {
    parent.setTask(task);
  }
}