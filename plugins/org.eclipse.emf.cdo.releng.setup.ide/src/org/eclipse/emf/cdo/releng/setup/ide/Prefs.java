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
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.ToolInstallation;
import org.eclipse.emf.cdo.releng.setup.ToolPreference;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public final class Prefs
{
  private static final SetupContext CONTEXT = Activator.getDefault();

  private static final IEclipsePreferences ROOT = Platform.getPreferencesService().getRootNode();

  public static void init() throws Exception
  {
    Setup setup = CONTEXT.getSetup();
    Branch branch = setup.getBranch();
    Project project = branch.getProject();

    final String name = project.getName() + ("master".equals(branch.getName()) ? "" : " " + branch.getName());
    PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell().getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        set("instance/org.eclipse.ui.ide/WORKSPACE_NAME", name);
      }
    });

    set("instance/org.eclipse.ui.workbench/RUN_IN_BACKGROUND", "true");

    Preferences preferences = setup.getPreferences();
    init(preferences);
    init(branch);
    init(project);
  }

  private static void init(ToolInstallation preferenceHolder)
  {
    for (ToolPreference toolPreference : preferenceHolder.getToolPreferences())
    {
      init(toolPreference);
    }
  }

  private static void init(ToolPreference toolPreference)
  {
    String key = toolPreference.getKey();
    if (key.startsWith("/"))
    {
      key = key.substring(1);
    }

    String value = toolPreference.getValue();
    set(key, value);
  }

  public static void set(String key, String value)
  {
    org.osgi.service.prefs.Preferences node = ROOT;

    String[] segments = key.split("/");
    for (int i = 0; i < segments.length - 1; i++)
    {
      String segment = segments[i];
      node = node.node(segment);
    }

    Progress.log().addLine("Setting preference " + key + " = " + value);
    node.put(segments[segments.length - 1], value);
  }
}
