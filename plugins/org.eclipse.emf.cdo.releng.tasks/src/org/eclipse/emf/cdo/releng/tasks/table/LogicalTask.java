/**
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LogicalTask implements Comparable<LogicalTask>
{
  private List<VersionTask> versionTasks = new ArrayList<VersionTask>();

  private TaskModel taskModel;

  private String summary;

  private Version maxVersion;

  private int unresolvedCount = -1;

  public LogicalTask(TaskModel taskModel, String summary)
  {
    this.taskModel = taskModel;
    this.summary = summary;
  }

  public TaskModel getTaskModel()
  {
    return taskModel;
  }

  public String getSummary()
  {
    return summary;
  }

  public List<VersionTask> getVersionTasks()
  {
    return versionTasks;
  }

  public VersionTask getVersionTask(Version version)
  {
    for (VersionTask versionTask : versionTasks)
    {
      if (versionTask.getVersion().equals(version))
      {
        return versionTask;
      }
    }

    return null;
  }

  public int getVersionTaskCount(Version version)
  {
    int count = 0;
    for (VersionTask versionTask : versionTasks)
    {
      if (versionTask.getVersion().equals(version))
      {
        ++count;
      }
    }

    return count;
  }

  public int getSeverityType()
  {
    int type = 0;
    for (VersionTask versionTask : versionTasks)
    {
      if ("enhancement".equalsIgnoreCase(versionTask.getSeverity()))
      {
        type |= 1;
      }
      else
      {
        type |= 2;
      }
    }

    return type;
  }

  public boolean isAllFixed()
  {
    for (VersionTask versionTask : versionTasks)
    {
      if (!"FIXED".equalsIgnoreCase(versionTask.getResolution()))
      {
        return false;
      }
    }

    return true;
  }

  public int compareTo(LogicalTask o)
  {
    int result = getMaxVersion().compareTo(o.getMaxVersion());
    if (result == 0)
    {
      int unresolvedCount = getUnresolvedCount();
      int otherUnresolvedCount = o.getUnresolvedCount();
      if (unresolvedCount == otherUnresolvedCount)
      {
        return summary.compareTo(o.getSummary());
      }

      return unresolvedCount < otherUnresolvedCount ? 1 : -1;
    }

    return result;
  }

  public Version getMaxVersion()
  {
    if (maxVersion == null)
    {
      maxVersion = Version.NONE;
      for (VersionTask task : versionTasks)
      {
        Version version = task.getVersion();
        if (version.compareTo(maxVersion) < 0)
        {
          maxVersion = version;
        }
      }
    }

    return maxVersion;
  }

  public int getUnresolvedCount()
  {
    if (unresolvedCount == -1)
    {
      unresolvedCount = 0;
      for (VersionTask task : versionTasks)
      {
        if ("".equals(task.getResolution()))
        {
          ++unresolvedCount;
        }
      }
    }

    return unresolvedCount;
  }

  @Override
  public String toString()
  {
    return summary;
  }
}
