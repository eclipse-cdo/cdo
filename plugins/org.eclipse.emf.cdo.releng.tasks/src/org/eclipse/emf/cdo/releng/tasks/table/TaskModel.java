/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.tasks.table;

import org.eclipse.emf.cdo.releng.tasks.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class TaskModel
{
  private static final String QUERY_IDENTIFIER = "TaskTable";

  private org.eclipse.mylyn.internal.tasks.core.ITaskList taskList;

  private ITaskDataManager taskDataManager;

  private org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query;

  private LogicalTask[] logicalTasks;

  private Version[] versions;

  // private ITaskListChangeListener taskListListener = new ITaskListChangeListener()
  // {
  // public void containersChanged(Set<TaskContainerDelta> deltas)
  // {
  // for (TaskContainerDelta delta : deltas)
  // {
  // IRepositoryElement element = delta.getElement();
  // if (element instanceof ITask && query.contains(element.getHandleIdentifier()))
  // {
  // refresh();
  // fireEvent(new Event(TaskModel.this));
  // break;
  //
  // // ITask task = (ITask)element;
  // // Kind kind = delta.getKind();
  // // System.out.println(kind + ": " + task);
  // //
  // // switch (kind)
  // // {
  // // case ADDED:
  // // taskAdded(task);
  // // break;
  // //
  // // case REMOVED:
  // // case DELETED:
  // // taskRemoved(task);
  // // break;
  // //
  // // case CONTENT:
  // // taskChanged(task);
  // // break;
  // //
  // // case ROOT:
  // // break;
  // // }
  // }
  // }
  // }
  //
  // private synchronized void taskAdded(ITask task)
  // {
  // // logicalTasks.put(task, new LogicalTask(task));
  // }
  //
  // private synchronized void taskRemoved(ITask task)
  // {
  // }
  //
  // private synchronized void taskChanged(ITask task)
  // {
  // }
  // };

  public TaskModel()
  {
    taskDataManager = org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskDataManager();
    taskList = org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getTaskList();
    query = getQuery();
    refresh();
    // taskList.addChangeListener(taskListListener);
  }

  public void dispose()
  {
    // taskList.removeChangeListener(taskListListener);
  }

  public org.eclipse.mylyn.internal.tasks.core.ITaskList getTaskList()
  {
    return taskList;
  }

  public ITaskDataManager getTaskDataManager()
  {
    return taskDataManager;
  }

  public Version[] getVersions()
  {
    return versions;
  }

  public LogicalTask[] getLogicalTasks()
  {
    return logicalTasks;
  }

  public void refresh()
  {
    Map<String, LogicalTask> logicalTasks = new HashMap<String, LogicalTask>();
    Map<Version, Version> versions = new HashMap<Version, Version>();

    for (ITask task : query.getChildren())
    {
      // Activator.getDefault().getLog().log(
      // new Status(IStatus.INFO, Activator.PLUGIN_ID, "Processing task " + task.getTaskId()));

      LogicalTask logicalTask = null;
      VersionTask versionTask = null;

      try
      {
        String summary = task.getSummary();
        logicalTask = logicalTasks.get(summary);
        if (logicalTask == null)
        {
          logicalTask = new LogicalTask(this, summary);
          logicalTasks.put(summary, logicalTask);
        }

        TaskData taskData = taskDataManager.getTaskData(task);
        TaskAttributeMapper attributeMapper = taskData.getAttributeMapper();
        TaskAttribute root = taskData.getRoot();

        TaskAttribute versionAttribute = root.getMappedAttribute(TaskAttribute.VERSION);
        String versionStr = attributeMapper.getValue(versionAttribute);
        Version version = Version.getVersion(versionStr);

        Version existingVersion = versions.get(version);
        if (existingVersion == null)
        {
          versions.put(version, version);
        }
        else
        {
          version = existingVersion;
        }

        TaskAttribute severityAttribute = root.getMappedAttribute(TaskAttribute.SEVERITY);
        String severity = attributeMapper.getValue(severityAttribute);

        TaskAttribute statusAttribute = root.getMappedAttribute(TaskAttribute.STATUS);
        String status = attributeMapper.getValue(statusAttribute);

        TaskAttribute resolutionAttribute = root.getMappedAttribute(TaskAttribute.RESOLUTION);
        String resolution = attributeMapper.getValue(resolutionAttribute);

        versionTask = new VersionTask(logicalTask, task, version, severity, status, resolution);
        logicalTask.getVersionTasks().add(versionTask);

        Activator.getDefault().getLog()
            .log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Version " + version + " added to task " + logicalTask));
      }
      catch (Exception ex)
      {
        Activator.getDefault().getLog()
            .log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Processing task " + task.getTaskId() + " failed", ex));
        ex.printStackTrace();

        // Cleanup
        if (versionTask != null)
        {
          if (logicalTask.getVersionTasks().remove(versionTask))
          {
            if (logicalTask.getVersionTasks().isEmpty())
            {
              logicalTasks.remove(logicalTask.getSummary());
            }
          }
        }
      }
    }

    this.logicalTasks = logicalTasks.values().toArray(new LogicalTask[logicalTasks.size()]);
    Arrays.sort(this.logicalTasks);

    this.versions = versions.keySet().toArray(new Version[versions.size()]);
    Arrays.sort(this.versions);
  }

  private org.eclipse.mylyn.internal.tasks.core.RepositoryQuery getQuery()
  {
    for (org.eclipse.mylyn.internal.tasks.core.RepositoryQuery query : taskList.getQueries())
    {
      String identifier = query.getSummary();
      if (QUERY_IDENTIFIER.equals(identifier))
      {
        return query;
      }
    }

    throw new IllegalStateException("Query 'TaskTable' is missing");
  }
}
