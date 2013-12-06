/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup;

import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EContentAdapter;

import java.util.List;

/**
 * TODO Use in editors.
 *
 * @author Eike Stepper
 */
public class AdditionalRequirementsGenerator extends EContentAdapter
{
  @Override
  public void notifyChanged(Notification notification)
  {
    super.notifyChanged(notification);
    if (!notification.isTouch())
    {
      Object notifier = notification.getNotifier();
      if (notifier instanceof SetupTaskContainer)
      {
        SetupTaskContainer container = (SetupTaskContainer)notifier;

        switch (notification.getEventType())
        {
        case Notification.ADD:
        {
          onAdded(container, notification.getNewValue());
          break;
        }

        case Notification.ADD_MANY:
        {
          for (Object object : (List<?>)notification.getNewValue())
          {
            onAdded(container, object);
          }

          break;
        }
        }
      }
    }
  }

  private void onAdded(SetupTaskContainer container, Object addedObject)
  {
    if (addedObject instanceof SetupTask)
    {
      SetupTask addedTask = (SetupTask)addedObject;

      EList<? extends SetupTask> additionalRequirements = addedTask.generateAdditionalRequirements();
      if (additionalRequirements != null)
      {
        for (SetupTask additionalRequirement : additionalRequirements)
        {
          addAdditionalRequirement(container, addedTask, additionalRequirement);
        }
      }
    }
  }

  protected void addAdditionalRequirement(SetupTaskContainer container, SetupTask addedTask,
      SetupTask additionalRequirement)
  {
    ScopeRoot scopeRoot = addedTask.getScopeRoot();
    if (scopeRoot != null)
    {
      container = scopeRoot;
    }

    EList<SetupTask> setupTasks = container.getSetupTasks();
    int index = setupTasks.indexOf(addedTask);
    if (index == -1)
    {
      index = 0;
    }

    setupTasks.add(index, additionalRequirement);
    addedTask.getRequirements().add(additionalRequirement);
  }
}
