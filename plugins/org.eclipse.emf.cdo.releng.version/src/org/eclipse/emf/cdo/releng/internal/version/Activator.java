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
package org.eclipse.emf.cdo.releng.internal.version;

import org.eclipse.emf.cdo.releng.version.VersionUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeListener;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Activator extends Plugin
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.version";

  private static final String IGNORED_RELEASES = "ignoredReleases.bin";

  private static final String BUILD_STATES = "buildStates.bin";

  private static Activator plugin;

  private static IResourceChangeListener postBuildListener;

  private IgnoredReleases ignoredReleases;

  private Map<String, BuildState> buildStates;

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
      File stateFile = getStateFile(IGNORED_RELEASES);
      if (stateFile.exists())
      {
        loadIgnoredReleases();
      }
    }
    finally
    {
      if (ignoredReleases == null)
      {
        ignoredReleases = new IgnoredReleases();
      }
    }

    try
    {
      File stateFile = getStateFile(BUILD_STATES);
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
    if (postBuildListener != null)
    {
      ResourcesPlugin.getWorkspace().removeResourceChangeListener(postBuildListener);
      postBuildListener = null;
    }

    if (!buildStates.isEmpty())
    {
      saveBuildStates();
      buildStates = null;
    }

    plugin = null;
    super.stop(context);
  }

  private File getStateFile(String name)
  {
    File stateFolder = Platform.getStateLocation(getBundle()).toFile();
    return new File(stateFolder, name);
  }

  private void loadIgnoredReleases()
  {
    ObjectInputStream stream = null;

    try
    {
      File stateFile = getStateFile(IGNORED_RELEASES);
      stream = new ObjectInputStream(new FileInputStream(stateFile));
      ignoredReleases = (IgnoredReleases)stream.readObject();
    }
    catch (Exception ex)
    {
      log(ex);
    }
    finally
    {
      VersionUtil.close(stream);
    }
  }

  private void saveIgnoredReleases()
  {
    ObjectOutputStream stream = null;

    try
    {
      File stateFile = getStateFile(IGNORED_RELEASES);
      stream = new ObjectOutputStream(new FileOutputStream(stateFile));
      stream.writeObject(ignoredReleases);
    }
    catch (Exception ex)
    {
      log(ex);
    }
    finally
    {
      VersionUtil.close(stream);
    }
  }

  private void loadBuildStates()
  {
    ObjectInputStream stream = null;

    try
    {
      File stateFile = getStateFile(BUILD_STATES);
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
      VersionUtil.close(stream);
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
      File stateFile = getStateFile(BUILD_STATES);
      stream = new ObjectOutputStream(new FileOutputStream(stateFile));
      stream.writeObject(buildStates);
    }
    catch (Exception ex)
    {
      log(ex);
    }
    finally
    {
      VersionUtil.close(stream);
    }
  }

  public static Set<String> getIgnoredReleases()
  {
    return plugin.ignoredReleases;
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

  public static void clearBuildState(IProject project)
  {
    String name = project.getName();
    plugin.buildStates.remove(name);
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

  public static void setPostBuildListener(IResourceChangeListener postBuildListener)
  {
    Activator.postBuildListener = postBuildListener;
  }

  /**
   * @author Eike Stepper
   */
  private static final class IgnoredReleases extends HashSet<String>
  {
    private static final long serialVersionUID = 1L;

    public IgnoredReleases()
    {
    }

    @Override
    public boolean add(String releasePath)
    {
      if (super.add(releasePath))
      {
        plugin.saveIgnoredReleases();
        return true;
      }

      return false;
    }

    @Override
    public boolean remove(Object releasePath)
    {
      if (super.remove(releasePath))
      {
        plugin.saveIgnoredReleases();
        return true;
      }

      return false;
    }
  }
}
