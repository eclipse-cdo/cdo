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
package org.eclipse.emf.cdo.releng.buildstamp;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class BuildMonitor implements IResourceChangeListener
{
  private static final boolean DEBUG = false;

  private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

  private static final String BUILD_STAMP_NAME = "buildstamp.properties";

  private static final String PROP_BUILD_NUMBER = "build.number";

  private final Set<Error> errors = new HashSet<Error>();

  private long buildNumber;

  public BuildMonitor() throws CoreException
  {
    initErrors();
    initBuildNumber();

    WORKSPACE.addResourceChangeListener(this, IResourceChangeEvent.POST_BUILD);
  }

  public void resourceChanged(IResourceChangeEvent event)
  {
    try
    {
      IResourceDelta delta = event.getDelta();
      if (delta != null)
      {
        if (updateErrors(delta))
        {
          dumpErrors("Errors changed");
        }
        else
        {
          dumpErrors("Errors unchanged");
        }
      }

      if (errors.isEmpty())
      {
        File stampFile = getStampFile();
        writeStampFile(stampFile, ++buildNumber);
      }
    }
    catch (Exception ex)
    {
      Activator.log("Problem in build listener: " + ex.getMessage(), ex);
    }
  }

  public void dispose()
  {
    WORKSPACE.removeResourceChangeListener(this);
  }

  private void initBuildNumber()
  {
    File stampFile = getStampFile();
    if (stampFile.exists())
    {
      buildNumber = readStampFile(stampFile);
    }
    else
    {
      writeStampFile(stampFile, buildNumber);
    }
  }

  private void initErrors() throws CoreException
  {
    IMarker[] markers = WORKSPACE.getRoot().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    for (IMarker marker : markers)
    {
      if (isError(marker))
      {
        errors.add(new Error(marker));
      }
    }

    dumpErrors("Errors initialized");
  }

  private boolean updateErrors(IResourceDelta delta) throws CoreException
  {
    boolean changed = false;

    for (IMarkerDelta markerDelta : delta.getMarkerDeltas())
    {
      IMarker marker = markerDelta.getMarker();

      boolean add = false;
      if (markerDelta.getKind() != IResourceDelta.REMOVED)
      {
        add = isError(marker);
      }

      Error error = new Error(marker);

      if (add)
      {
        changed |= errors.add(error);
      }
      else
      {
        changed |= errors.remove(error);
      }
    }

    // Recurse
    for (IResourceDelta childDelta : delta.getAffectedChildren())
    {
      changed |= updateErrors(childDelta);
    }

    return changed;
  }

  private void dumpErrors(String message)
  {
    if (DEBUG)
    {
      System.out.println(message);

      List<Error> list = new ArrayList<Error>(errors);
      Collections.sort(list);

      for (Error error : list)
      {
        System.out.println(error);
      }

      System.out.println();
    }
  }

  private boolean isError(IMarker marker) throws CoreException
  {
    if (marker.isSubtypeOf(IMarker.PROBLEM))
    {
      int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
      if (severity == IMarker.SEVERITY_ERROR)
      {
        return true;
      }
    }

    return false;
  }

  private File getStampFile()
  {
    return new File(Activator.getDefault().getStateLocation().toFile(), BUILD_STAMP_NAME);
  }

  private static long readStampFile(File stampFile)
  {
    Properties properties = new Properties();
    FileInputStream stream = null;

    try
    {
      stream = new FileInputStream(stampFile);
      properties.load(stream);
    }
    catch (IOException ex)
    {
      Activator.log("Could not read stamp file: " + stampFile.getAbsolutePath(), ex);
    }
    finally
    {
      close(stream);
    }

    try
    {
      return Long.parseLong(properties.getProperty(PROP_BUILD_NUMBER));
    }
    catch (Exception ex)
    {
      Activator.log("Could not parse build number: " + stampFile.getAbsolutePath(), ex);
      return 0L;
    }
  }

  private static void writeStampFile(File stampFile, long buildNumber)
  {
    Properties properties = new Properties();
    properties.setProperty(PROP_BUILD_NUMBER, Long.toString(buildNumber));

    FileOutputStream stream = null;

    try
    {
      stream = new FileOutputStream(stampFile);
      properties.store(stream, null);
    }
    catch (IOException ex)
    {
      Activator.log("Could not write stamp file: " + stampFile.getAbsolutePath(), ex);
    }
    finally
    {
      close(stream);
    }
  }

  private static void close(Closeable stream)
  {
    if (stream != null)
    {
      try
      {
        stream.close();
      }
      catch (IOException ex)
      {
        if (DEBUG)
        {
          ex.printStackTrace();
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Error implements Comparable<Error>
  {
    private final IPath fullPath;

    private final long markerID;

    public Error(IMarker marker)
    {
      fullPath = marker.getResource().getFullPath();
      markerID = marker.getId();
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (fullPath == null ? 0 : fullPath.hashCode());
      result = prime * result + (int)(markerID ^ markerID >>> 32);
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      Error other = (Error)obj;
      if (fullPath == null)
      {
        if (other.fullPath != null)
        {
          return false;
        }
      }
      else if (!fullPath.equals(other.fullPath))
      {
        return false;
      }

      if (markerID != other.markerID)
      {
        return false;
      }

      return true;
    }

    public int compareTo(Error o)
    {
      int result = fullPath.toString().compareTo(o.fullPath.toString());
      if (result == 0)
      {
        result = new Long(markerID).compareTo(o.markerID);
      }

      return result;
    }

    @Override
    public String toString()
    {
      return fullPath.toString() + " [" + markerID + "]";
    }
  }
}
