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
package org.eclipse.emf.cdo.releng.workingsets.presentation;

import org.eclipse.emf.cdo.releng.workingsets.WorkingSet;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup;
import org.eclipse.emf.cdo.releng.workingsets.util.WorkingSetsUtil;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class WorkingSetManager
{
  private static final String WORKING_SET_PAGE = "org.eclipse.jdt.ui.JavaWorkingSetPage";

  private static final String PACKAGE_EXPLORER_ID = "org.eclipse.jdt.ui.PackageExplorer";

  private static final IWorkingSetManager MANAGER = PlatformUI.getWorkbench().getWorkingSetManager();

  private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

  private final IEclipsePreferences.IPreferenceChangeListener preferencesListener = new IEclipsePreferences.IPreferenceChangeListener()
  {
    public void preferenceChange(PreferenceChangeEvent event)
    {
      if (WorkingSetsUtil.WORKING_SET_GROUP_PREFERENCE_KEY.equals(event.getKey()))
      {
        WorkingSetGroup oldWorkingSetGroup = workingSetGroup;

        // Compute the working sets for the new working group.
        workingSetGroup = WorkingSetsUtil.getWorkingSetGroup();
        EMap<String, Set<IAdaptable>> workingSets = new BasicEMap<String, Set<IAdaptable>>();

        // Update the map to include null (to cause an delete) for any old working set not present in the new ones
        for (WorkingSet workingSet : oldWorkingSetGroup.getWorkingSets())
        {
          String name = workingSet.getName();
          workingSets.put(name, null);
        }

        // Update the map to include empty sets (to cause an add) for any new working set not already present.
        for (WorkingSet workingSet : workingSetGroup.getWorkingSets())
        {
          String name = workingSet.getName();
          workingSets.put(name, new LinkedHashSet<IAdaptable>());
        }

        // Update the working sets for all the projects in the workspace and apply the result to the real working sets.
        updateProjects(workingSets);
        apply(workingSets);
      }
    }
  };

  private final IResourceChangeListener resourceChangeListener = new IResourceChangeListener()
  {
    public void resourceChanged(IResourceChangeEvent event)
    {
      IResourceDelta delta = event.getDelta();
      if (delta != null)
      {
        try
        {
          // Compute the workings sets and update them relative to our workspace delta, i.e., relative to added and
          // removed projects.
          final EMap<String, Set<IAdaptable>> workingSets = getWorkingSets();
          IResourceDeltaVisitor resourceDeltaVisitor = new IResourceDeltaVisitor()
          {
            public boolean visit(IResourceDelta delta) throws CoreException
            {
              IResource resource = delta.getResource();
              if (resource instanceof IWorkspaceRoot)
              {
                return true;
              }

              if (resource instanceof IProject)
              {
                int kind = delta.getKind();
                if (kind == IResourceDelta.ADDED)
                {
                  addProject((IProject)resource, workingSets);
                }
                else if (kind == IResourceDelta.REMOVED)
                {
                  removeProject((IProject)resource, workingSets);
                }
              }

              return false;
            }
          };

          delta.accept(resourceDeltaVisitor);
          apply(workingSets);
        }
        catch (CoreException ex)
        {
          // Ignore
        }
      }
    }
  };

  private WorkingSetGroup workingSetGroup;

  public WorkingSetManager()
  {
    workingSetGroup = WorkingSetsUtil.getWorkingSetGroup();

    // Listen for projects being added or removed from the workspace and for preferences changing.
    WORKSPACE.addResourceChangeListener(resourceChangeListener);
    WorkingSetsUtil.WORKING_SET_GROUP_PREFERENCES.addPreferenceChangeListener(preferencesListener);
  }

  /**
   * Clean up the listeners.
   */
  public void dispose()
  {
    WORKSPACE.removeResourceChangeListener(resourceChangeListener);

    WorkingSetsUtil.WORKING_SET_GROUP_PREFERENCES.removePreferenceChangeListener(preferencesListener);
  }

  /**
   * Returns a map with an entry for every working set defined in the working set group from the working set name to either the elements in the real working set or to null, if there isn't one.
   */
  private EMap<String, Set<IAdaptable>> getWorkingSets()
  {
    EMap<String, Set<IAdaptable>> workingSets = new BasicEMap<String, Set<IAdaptable>>();
    for (WorkingSet workingSet : workingSetGroup.getWorkingSets())
    {
      String name = workingSet.getName();
      IWorkingSet iWorkingSet = MANAGER.getWorkingSet(name);
      workingSets.put(name, iWorkingSet == null ? new LinkedHashSet<IAdaptable>() : new LinkedHashSet<IAdaptable>(
          Arrays.asList(iWorkingSet.getElements())));
    }

    return workingSets;
  }

  /**
   * Update the real workings sets based on the map.
   * This deletes the real working set for any map entry with a null value and creates or updates the real working set for every other entry.
   */
  private void apply(final EMap<String, Set<IAdaptable>> workingSets)
  {
    // Do this on the UI thread to avoid problems with JDT's getting out of sync with respect to our updates.
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        for (Map.Entry<String, Set<IAdaptable>> entry : workingSets)
        {
          String key = entry.getKey();
          Set<IAdaptable> value = entry.getValue();
          IWorkingSet workingSet = MANAGER.getWorkingSet(key);
          if (workingSet == null)
          {
            if (value != null)
            {
              workingSet = MANAGER.createWorkingSet(key, value.toArray(new IAdaptable[value.size()]));
              workingSet.setLabel(key);
              workingSet.setId(WORKING_SET_PAGE);
              MANAGER.addWorkingSet(workingSet);
            }
          }
          else
          {
            if (value == null)
            {
              MANAGER.removeWorkingSet(workingSet);
            }
            else
            {
              workingSet.setElements(value.toArray(new IAdaptable[value.size()]));
            }
          }
        }

        managePackageExplorer();
      }
    });
  }

  private void managePackageExplorer()
  {
    try
    {
      for (IWorkbenchWindow workbenchWindow : PlatformUI.getWorkbench().getWorkbenchWindows())
      {
        for (IWorkbenchPage workbenchPage : workbenchWindow.getPages())
        {
          IViewPart view = workbenchPage.findView(PACKAGE_EXPLORER_ID);
          if (view != null)
          {
            Method getWorkingSetModelMethod = view.getClass().getMethod("getWorkingSetModel");
            Object workingSetModel = getWorkingSetModelMethod.invoke(view);
            if (workingSetModel != null)
            {
              Class<?> workingSetModelClass = workingSetModel.getClass();
              Method getAllWorkingSetsMethod = workingSetModelClass.getMethod("getAllWorkingSets");
              IWorkingSet[] allWorkingSets = (IWorkingSet[])getAllWorkingSetsMethod.invoke(workingSetModel);

              Map<WorkingSet, IWorkingSet> managedWorkingSets = new HashMap<WorkingSet, IWorkingSet>();
              for (int i = 0; i < allWorkingSets.length; ++i)
              {
                IWorkingSet iWorkingSet = allWorkingSets[i];
                WorkingSet workingSet = workingSetGroup.getWorkingSet(iWorkingSet.getName());
                if (workingSet != null)
                {
                  managedWorkingSets.put(workingSet, iWorkingSet);
                }
              }

              Map<IWorkingSet, List<IWorkingSet>> orderedWorkingSetGroups = new LinkedHashMap<IWorkingSet, List<IWorkingSet>>();
              for (WorkingSet workingSet : workingSetGroup.getWorkingSets())
              {
                IWorkingSet iWorkingSet = managedWorkingSets.get(workingSet);
                List<IWorkingSet> group = new ArrayList<IWorkingSet>();
                group.add(iWorkingSet);
                orderedWorkingSetGroups.put(iWorkingSet, group);
              }

              Method getActiveWorkingSetsMethod = workingSetModelClass.getMethod("getActiveWorkingSets");
              List<IWorkingSet> activeWorkingSets = Arrays.asList((IWorkingSet[])getActiveWorkingSetsMethod
                  .invoke(workingSetModel));

              List<IWorkingSet> newActiveWorkingSets = new ArrayList<IWorkingSet>();
              List<IWorkingSet> group = newActiveWorkingSets;
              for (IWorkingSet iWorkingSet : activeWorkingSets)
              {
                List<IWorkingSet> targetGroup = orderedWorkingSetGroups.get(iWorkingSet);
                if (targetGroup == null)
                {
                  group.add(iWorkingSet);
                }
                else
                {
                  group = targetGroup;
                }
              }

              for (List<IWorkingSet> workingSets : orderedWorkingSetGroups.values())
              {
                newActiveWorkingSets.addAll(workingSets);
              }

              IWorkingSet[] orderedActiveWorkingSetsArray = newActiveWorkingSets
                  .toArray(new IWorkingSet[newActiveWorkingSets.size()]);

              Method setWorkingSetsMethod = workingSetModelClass.getMethod("setActiveWorkingSets", IWorkingSet[].class);

              setWorkingSetsMethod.invoke(workingSetModel, new Object[] { orderedActiveWorkingSetsArray });

              Method getRootModeMethod = view.getClass().getMethod("getRootMode");
              if (!getRootModeMethod.invoke(view).equals(2))
              {
                Method method = view.getClass().getMethod("rootModeChanged", int.class);
                method.invoke(view, 2);
              }
            }
            return;
          }
        }
      }
    }
    catch (NoSuchMethodException ex)
    {
      WorkingSetsEditorPlugin.INSTANCE.log(ex);
    }
    catch (SecurityException ex)
    {
      WorkingSetsEditorPlugin.INSTANCE.log(ex);
    }
    catch (IllegalAccessException ex)
    {
      WorkingSetsEditorPlugin.INSTANCE.log(ex);
    }
    catch (InvocationTargetException ex)
    {
      WorkingSetsEditorPlugin.INSTANCE.log(ex);
    }
  }

  /**
   * Compute the elements for the working sets based on the projects in the workspace.
   */
  private void updateProjects(EMap<String, Set<IAdaptable>> workingSets)
  {
    for (IProject project : WORKSPACE.getRoot().getProjects())
    {
      addProject(project, workingSets);
    }
  }

  /**
   * Adds the project to the appropriate working set entry, if applicable.
   */
  private void addProject(IProject project, EMap<String, Set<IAdaptable>> workingSets)
  {
    for (WorkingSet workingSet : workingSetGroup.getWorkingSets())
    {
      if (workingSet.matches(project))
      {
        String name = workingSet.getName();
        Set<IAdaptable> elements = workingSets.get(name);
        if (elements == null)
        {
          elements = new LinkedHashSet<IAdaptable>();
          workingSets.put(name, elements);
        }

        elements.add(project);
      }
    }
  }

  /**
   * Removes the project from the appropriate working set entry.
   */
  private void removeProject(IProject project, EMap<String, Set<IAdaptable>> workingSets)
  {
    for (WorkingSet workingSet : workingSetGroup.getWorkingSets())
    {
      if (workingSet.matches(project))
      {
        String name = workingSet.getName();
        Set<IAdaptable> elements = workingSets.get(name);
        if (elements != null)
        {
          elements.remove(name);
        }
      }
    }
  }
}
