package org.eclipse.net4j.internal.util.om.monitor;

/**
 * @author Eike Stepper
 */
public final class LegacySubMonitor extends SubMonitor
{
  public LegacySubMonitor(Monitor parent, int workFromParent)
  {
    super(parent, workFromParent);
  }

  @Override
  public SubMonitor newSubMonitor(int workFromParent)
  {
    return new LegacySubMonitor(this, workFromParent);
  }
}