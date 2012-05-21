/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.tasks.table;

import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Eike Stepper
 */
public class VersionTask
{
  private LogicalTask logicalTask;

  private ITask task;

  private Version version;

  private String severity;

  private String status;

  private String resolution;

  public VersionTask(LogicalTask logicalTask, ITask task, Version version, String severity, String status,
      String resolution)
  {
    this.logicalTask = logicalTask;
    this.task = task;
    this.version = version;
    this.severity = severity;
    this.status = status;
    this.resolution = resolution;
  }

  public LogicalTask getLogicalTask()
  {
    return logicalTask;
  }

  public ITask getTask()
  {
    return task;
  }

  public String getID()
  {
    return task.getTaskId();
  }

  public String getSummary()
  {
    return task.getSummary();
  }

  public Version getVersion()
  {
    return version;
  }

  public String getSeverity()
  {
    return severity;
  }

  public String getStatus()
  {
    return status;
  }

  public String getResolution()
  {
    return resolution;
  }

  @Override
  public String toString()
  {
    return getID() + "v" + getVersion();
  }
}
