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
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class Activator extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.version";

  private static Activator plugin;

  private Map<String, BuildState> buildStates;

  private File stateFile;

  public Activator()
  {
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;

    try
    {
      File stateFolder = Platform.getStateLocation(getBundle()).toFile();
      stateFile = new File(stateFolder, "buildStates.bin");
      if (stateFile.exists())
      {
        loadBuildStates();
        stateFile.delete(); // Future indication for possible workspace crash
      }
    }
    finally
    {
      if (buildStates == null)
      {
        buildStates = new HashMap<String, BuildState>();
      }
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    if (!buildStates.isEmpty())
    {
      saveBuildStates();
    }

    stateFile = null;
    plugin = null;
    super.stop(context);
  }

  private void loadBuildStates()
  {
    ObjectInputStream stream = null;

    try
    {
      stream = new ObjectInputStream(new FileInputStream(stateFile));
      @SuppressWarnings("unchecked")
      Map<String, BuildState> object = (Map<String, BuildState>)stream.readObject();
      buildStates = object;
    }
    catch (Exception ex)
    {
      log(ex);
    }
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }
        catch (Exception ex)
        {
          log(ex);
        }
      }
    }
  }

  private void saveBuildStates()
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    for (Iterator<Entry<String, BuildState>> it = buildStates.entrySet().iterator(); it.hasNext();)
    {
      Entry<String, BuildState> entry = it.next();
      IProject project = root.getProject(entry.getKey());
      if (!project.exists())
      {
        it.remove();
      }
    }

    ObjectOutputStream stream = null;

    try
    {
      stream = new ObjectOutputStream(new FileOutputStream(stateFile));
      stream.writeObject(buildStates);
    }
    catch (Exception ex)
    {
      log(ex);
    }
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }
        catch (Exception ex)
        {
          log(ex);
        }
      }
    }
  }

  public static BuildState getBuildState(IProject project)
  {
    String name = project.getName();
    BuildState buildState = plugin.buildStates.get(name);
    if (buildState == null)
    {
      buildState = new BuildState();
      plugin.buildStates.put(name, buildState);
    }

    return buildState;
  }

  public static void log(String message)
  {
    plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
  }

  public static void log(IStatus status)
  {
    plugin.getLog().log(status);
  }

  public static String log(Throwable t)
  {
    IStatus status = getStatus(t);
    log(status);
    return status.getMessage();
  }

  public static IStatus getStatus(Throwable t)
  {
    if (t instanceof CoreException)
    {
      CoreException coreException = (CoreException)t;
      return coreException.getStatus();
    }

    String msg = t.getLocalizedMessage();
    if (msg == null || msg.length() == 0)
    {
      msg = t.getClass().getName();
    }

    return new Status(IStatus.ERROR, PLUGIN_ID, msg, t);
  }
}
