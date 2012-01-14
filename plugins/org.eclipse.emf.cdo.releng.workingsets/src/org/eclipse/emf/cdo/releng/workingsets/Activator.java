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
package org.eclipse.emf.cdo.releng.workingsets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class Activator extends AbstractUIPlugin implements IResourceChangeListener
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.workingsets"; //$NON-NLS-1$

  private static final String WORKING_SET_PAGE = "org.eclipse.jdt.ui.JavaWorkingSetPage";

  private static final IWorkingSetManager MANAGER = PlatformUI.getWorkbench().getWorkingSetManager();

  private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

  private static Activator plugin;

  private List<WorkingSetDefinition> workingSetDefinitions = new ArrayList<WorkingSetDefinition>();

  private IWorkingSet featureWorkingSet;

  public Activator()
  {
  }

  public void resourceChanged(IResourceChangeEvent event)
  {
    IResourceDelta delta = event.getDelta();
    if (delta != null)
    {
      for (IResourceDelta resourceDelta : delta.getAffectedChildren())
      {
        int kind = resourceDelta.getKind();
        if (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED)
        {
          updateProjects();
          return;
        }
      }
    }
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;

    featureWorkingSet = createWorkingSet("Features");
    addWorkingSetDefinition("Releng", "org.eclipse.emf.cdo.releng");
    addWorkingSetDefinition("CDO Dawn", "org.eclipse.emf.cdo.dawn");
    addWorkingSetDefinition("CDO Tests", "org.eclipse.emf.cdo.tests");
    addWorkingSetDefinition("CDO Examples", "org.eclipse.emf.cdo.examples", "org.gastro");
    addWorkingSetDefinition("CDO", "org.eclipse.emf.cdo");
    addWorkingSetDefinition("Net4j DB", "org.eclipse.net4j.db");
    addWorkingSetDefinition("Net4j Examples", "org.eclipse.net4j.examples", "org.eclipse.net4j.buddies",
        "org.eclipse.net4j.jms");
    addWorkingSetDefinition("Net4j", "org.eclipse.net4j");

    updateProjects();
    WORKSPACE.addResourceChangeListener(this);
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    WORKSPACE.removeResourceChangeListener(this);
    plugin = null;
    super.stop(context);
  }

  private IWorkingSet createWorkingSet(String name)
  {
    IWorkingSet workingSet = MANAGER.getWorkingSet(name);
    if (workingSet == null)
    {
      workingSet = MANAGER.createWorkingSet(name, new IAdaptable[0]);
      workingSet.setLabel(name);
      workingSet.setId(WORKING_SET_PAGE);

      MANAGER.addWorkingSet(workingSet);
    }

    return workingSet;
  }

  private void addWorkingSetDefinition(String name, String... prefixes)
  {
    WorkingSetDefinition workingSetDefinition = new WorkingSetDefinition(name, prefixes);
    workingSetDefinitions.add(workingSetDefinition);

    createWorkingSet(name);
  }

  private void updateProjects()
  {
    List<IProject> features = new ArrayList<IProject>();
    Map<String, List<IProject>> projects = new HashMap<String, List<IProject>>();

    for (IProject project : WORKSPACE.getRoot().getProjects())
    {
      if (project.getName().endsWith("-feature"))
      {
        features.add(project);
      }
      else
      {
        updateProject(projects, project);
      }
    }

    updateWorkingSet(featureWorkingSet, features);

    for (Entry<String, List<IProject>> entry : projects.entrySet())
    {
      IWorkingSet workingSet = MANAGER.getWorkingSet(entry.getKey());
      if (workingSet != null)
      {
        updateWorkingSet(workingSet, entry.getValue());
      }
    }
  }

  private void updateProject(Map<String, List<IProject>> projects, IProject project)
  {
    for (WorkingSetDefinition workingSetDefinition : workingSetDefinitions)
    {
      for (String prefix : workingSetDefinition.getPrefixes())
      {
        if (project.getName().startsWith(prefix))
        {
          String name = workingSetDefinition.getName();
          List<IProject> list = projects.get(name);
          if (list == null)
          {
            list = new ArrayList<IProject>();
            projects.put(name, list);
          }

          list.add(project);
          return;
        }
      }
    }
  }

  private void updateWorkingSet(IWorkingSet workingSet, List<IProject> projects)
  {
    workingSet.setElements(projects.toArray(new IProject[projects.size()]));
  }

  public static Activator getDefault()
  {
    return plugin;
  }

  /**
   * @author Eike Stepper
   */
  private static final class WorkingSetDefinition
  {
    private final String name;

    private final String[] prefixes;

    public WorkingSetDefinition(String name, String[] prefixes)
    {
      this.name = name;
      this.prefixes = prefixes;
    }

    public String getName()
    {
      return name;
    }

    public String[] getPrefixes()
    {
      return prefixes;
    }
  }
}
