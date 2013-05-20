/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.tasks.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Eike Stepper
 */
public class OpenActiveTaskHandler extends AbstractHandler
{
  public OpenActiveTaskHandler()
  {
  }

  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ITask task = TasksUi.getTaskActivityManager().getActiveTask();
    TasksUiUtil.openTask(task);
    return null;
  }
}
