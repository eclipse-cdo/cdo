/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesFactory;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.util.SetupUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;

import org.eclipse.jface.preference.PreferenceDialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class PreferenceRecorderAction extends AbstractContainerAction
{
  private final boolean withDialog;

  private PreferenceNode rootPreferenceNode;

  private EContentAdapter preferenceAdapter;

  public PreferenceRecorderAction(boolean withDialog)
  {
    super("Record" + (withDialog ? "" : " Without Dialog"), AS_CHECK_BOX);
    this.withDialog = withDialog;

    if (withDialog)
    {
      setToolTipText("Open the Preferences dialog and record changes into the selected setup task container");
    }
    else
    {
      setToolTipText("Record preference changes into the selected setup task container (without dialog)");
    }
  }

  @Override
  protected boolean runInit(SetupTaskContainer container)
  {
    preferenceAdapter = createPreferenceAdapter();
    rootPreferenceNode = PreferencesUtil.getRootPreferenceNode(true);
    rootPreferenceNode.eAdapters().add(preferenceAdapter);
    return true;
  }

  @Override
  protected void runModify(SetupTaskContainer container)
  {
    if (withDialog)
    {
      PreferenceDialog dialog = org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(null, null, null, null);
      dialog.open();
    }
  }

  @Override
  protected void runDone(SetupTaskContainer container)
  {
    rootPreferenceNode.eAdapters().remove(preferenceAdapter);
    rootPreferenceNode = null;
    preferenceAdapter = null;
  }

  protected void updatePreference(URI key, String value)
  {
    String path = PreferencesFactory.eINSTANCE.convertURI(key);
    for (TreeIterator<EObject> it = getContainer().eResource().getAllContents(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof EclipsePreferenceTask)
      {
        EclipsePreferenceTask preferenceTask = (EclipsePreferenceTask)object;
        if (path.equals(preferenceTask.getKey()))
        {
          preferenceTask.setValue(value);
          expandItem(preferenceTask);
          return;
        }
      }
    }

    EclipsePreferenceTask task = SetupFactory.eINSTANCE.createEclipsePreferenceTask();
    task.setKey(path);
    task.setValue(SetupUtil.escape(value));

    String pluginID = key.segment(1).toString();
    CompoundSetupTask compoundTask = getCompoundTask(pluginID);
    compoundTask.getSetupTasks().add(task);
    expandItem(task);
  }

  private CompoundSetupTask getCompoundTask(String pluginID)
  {
    EList<SetupTask> setupTasks = getContainer().getSetupTasks();
    for (Iterator<SetupTask> it = setupTasks.iterator(); it.hasNext();)
    {
      SetupTask setupTask = it.next();
      if (setupTask instanceof CompoundSetupTask)
      {
        CompoundSetupTask compoundTask = (CompoundSetupTask)setupTask;
        if (pluginID.equals(compoundTask.getName()))
        {
          return compoundTask;
        }
      }
    }

    CompoundSetupTask compoundTask = SetupFactory.eINSTANCE.createCompoundSetupTask(pluginID);
    compoundTask.setName(pluginID);
    setupTasks.add(compoundTask);
    return compoundTask;
  }

  private EContentAdapter createPreferenceAdapter()
  {
    return new EContentAdapter()
    {
      private Map<Property, URI> paths = new HashMap<Property, URI>();

      @Override
      protected void setTarget(EObject target)
      {
        super.setTarget(target);
        if (target instanceof Property)
        {
          Property property = (Property)target;
          URI absolutePath = property.getAbsolutePath();
          if ("instance".equals(absolutePath.authority()) || "configuration".equals(absolutePath.authority()))
          {
            paths.put(property, absolutePath);
          }
        }
      }

      @Override
      public void notifyChanged(Notification notification)
      {
        super.notifyChanged(notification);

        if (!notification.isTouch())
        {
          switch (notification.getEventType())
          {
          case Notification.SET:
            if (notification.getFeature() == PreferencesPackage.Literals.PROPERTY__VALUE)
            {
              Property property = (Property)notification.getNotifier();
              notifyChanged(property, property.getValue());
            }
            break;

          case Notification.ADD:
            if (notification.getFeature() == PreferencesPackage.Literals.PREFERENCE_NODE__PROPERTIES)
            {
              Property property = (Property)notification.getNewValue();
              notifyChanged(property, property.getValue());
            }
            break;

          case Notification.REMOVE:
            if (notification.getFeature() == PreferencesPackage.Literals.PREFERENCE_NODE__PROPERTIES)
            {
              Property property = (Property)notification.getOldValue();
              notifyChanged(property, null);
            }
            break;
          }
        }
      }

      private void notifyChanged(Property property, String value)
      {
        URI absolutePath = paths.get(property);
        if (absolutePath != null)
        {
          updatePreference(absolutePath, value);
        }
      }
    };
  }
}
