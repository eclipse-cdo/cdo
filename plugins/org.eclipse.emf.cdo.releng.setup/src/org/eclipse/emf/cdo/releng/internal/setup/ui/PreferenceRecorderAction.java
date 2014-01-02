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

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class PreferenceRecorderAction extends Action
{
  private final boolean withDialog;

  private SetupTaskContainer container;

  private PreferenceNode rootPreferenceNode;

  private EContentAdapter preferenceAdapter;

  private ISelectionProvider selectionProvider;

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

  public void selectionChanged(SelectionChangedEvent event)
  {
    if (!isChecked())
    {
      selectionProvider = event.getSelectionProvider();

      ISelection selection = event.getSelection();
      if (selection instanceof IStructuredSelection)
      {
        IStructuredSelection structuredSelection = (IStructuredSelection)selection;
        if (structuredSelection.size() == 1)
        {
          Object element = structuredSelection.getFirstElement();
          if (element instanceof EObject)
          {
            container = getSetupTaskContainer((EObject)element);
            if (container != null)
            {
              setEnabled(true);
              return;
            }
          }
        }
      }

      container = null;
      setEnabled(false);
    }
  }

  @Override
  public void run()
  {
    if (isChecked())
    {
      expandItem(container);

      preferenceAdapter = createPreferenceAdapter();
      rootPreferenceNode = PreferencesUtil.getRootPreferenceNode(true);
      rootPreferenceNode.eAdapters().add(preferenceAdapter);

      if (withDialog)
      {
        ChangeCommand command = new ChangeCommand(container.eResource())
        {
          @Override
          protected void doExecute()
          {
            PreferenceDialog dialog = org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(null, null, null,
                null);
            dialog.open();
          }
        };

        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(container);
        CommandStack commandStack = editingDomain.getCommandStack();
        commandStack.execute(command);
      }

      rootPreferenceNode.eAdapters().remove(preferenceAdapter);
      rootPreferenceNode = null;
      preferenceAdapter = null;
      setChecked(false);
    }
  }

  protected void updatePreference(URI key, String value)
  {
    String path = PreferencesFactory.eINSTANCE.convertURI(key);
    for (TreeIterator<EObject> it = container.eResource().getAllContents(); it.hasNext();)
    {
      EObject object = it.next();
      if (object instanceof EclipsePreferenceTask)
      {
        EclipsePreferenceTask preferenceTask = (EclipsePreferenceTask)object;
        if (path.equals(preferenceTask.getKey()))
        {
          preferenceTask.setValue(value);
          expandItem(preferenceTask.eContainer());
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
    expandItem(compoundTask);
  }

  private void expandItem(final EObject object)
  {
    if (selectionProvider instanceof IViewerProvider)
    {
      IViewerProvider viewerProvider = (IViewerProvider)selectionProvider;
      final Viewer viewer = viewerProvider.getViewer();
      if (viewer instanceof TreeViewer)
      {
        viewer.getControl().getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            ((TreeViewer)viewer).setExpandedState(object, true);
          }
        });
      }
    }
  }

  private CompoundSetupTask getCompoundTask(String pluginID)
  {
    EList<SetupTask> setupTasks = container.getSetupTasks();
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

    CompoundSetupTask compoundTask = SetupFactory.eINSTANCE.createCompoundSetupTask();
    compoundTask.setName(pluginID);
    setupTasks.add(compoundTask);
    return compoundTask;
  }

  private SetupTaskContainer getSetupTaskContainer(EObject object)
  {
    while (object != null && !(object instanceof SetupTaskContainer))
    {
      object = object.eContainer();
    }

    return (SetupTaskContainer)object;
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
