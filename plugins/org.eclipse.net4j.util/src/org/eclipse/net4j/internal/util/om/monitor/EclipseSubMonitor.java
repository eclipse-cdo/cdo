package org.eclipse.net4j.internal.util.om.monitor;

import org.eclipse.net4j.util.om.monitor.IEclipseMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @author Eike Stepper
 */
public final class EclipseSubMonitor extends SubMonitor implements IEclipseMonitor
{
  private IProgressMonitor progressMonitor;

  public EclipseSubMonitor(Monitor parent, int workFromParent)
  {
    super(parent, workFromParent);
    progressMonitor = new SubProgressMonitor(((IEclipseMonitor)parent).getProgressMonitor(), workFromParent);
  }

  public IProgressMonitor getProgressMonitor()
  {
    return progressMonitor;
  }

  @Override
  public SubMonitor newSubMonitor(int workFromParent)
  {
    return new EclipseSubMonitor(this, workFromParent);
  }
}