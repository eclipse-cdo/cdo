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
package org.eclipse.emf.cdo.releng.version.tests;

import org.eclipse.emf.cdo.releng.version.Markers;
import org.eclipse.emf.cdo.releng.version.VersionUtil;
import org.eclipse.emf.cdo.releng.version.ui.quickfixes.VersionResolutionGenerator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMarkerResolution;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public class VersionBuilderTest extends TestCase
{
  private static final VersionResolutionGenerator FIX_GENERATOR = new VersionResolutionGenerator();

  private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

  private static final IWorkspaceRoot ROOT = WORKSPACE.getRoot();

  private static final File WORKSPACE_FILE = ROOT.getLocation().toFile();

  private static final int TRIM_LENGTH = WORKSPACE_FILE.getAbsolutePath().length() + 1;

  private static final String DELETE_SUFFIX = "-DELETE";

  private static final PrintStream MSG = System.out;

  private BundleFile testFolder;

  public VersionBuilderTest(BundleFile testFolder)
  {
    super(testFolder.getName());
    this.testFolder = testFolder;
  }

  @Override
  public void runBare() throws Throwable
  {
    MSG.println("===============================================================================================");
    MSG.println("Test " + getName());
    MSG.println("===============================================================================================");

    clearWorkspace();

    boolean clean = true;
    for (BundleFile phase : testFolder.getChildren())
    {
      if (phase.isDirectory())
      {
        MSG.println("  Phase '" + phase.getName() + "'");
        runPhase(phase, clean);
        clean = false;
      }
    }

    MSG.println();
  }

  private void runPhase(BundleFile phase, boolean clean) throws Throwable
  {
    MSG.println("    Update workspace");
    updateWorkspace(phase);
    IMarker[] markers = buildWorkspace(phase, clean);
    processMarkers(phase, markers, "build.markers");

    int fixAttempt = 0;
    while (markers.length != 0) // TODO Can be inifinite?
    {
      MSG.println("    Fix workspace (attempt " + ++fixAttempt + ")");
      fixWorkspace(phase, markers);
      markers = buildWorkspace(phase, false);
      processMarkers(phase, markers, "fix" + fixAttempt + ".markers");
    }
  }

  private void clearWorkspace() throws Throwable
  {
    WORKSPACE.getDescription().setAutoBuilding(false);
    WORKSPACE.run(new IWorkspaceRunnable()
    {
      public void run(IProgressMonitor monitor) throws CoreException
      {
        for (IProject project : ROOT.getProjects())
        {
          MSG.println("  Deleting project " + project.getName());
          project.delete(true, null);
        }

        for (File file : WORKSPACE_FILE.listFiles())
        {
          if (file.isDirectory() && !".metadata".equals(file.getName()))
          {
            MSG.println("  Deleting location " + file);
            VersionUtil.delete(file);
          }
        }
      }
    }, null);
  }

  private void updateWorkspace(final BundleFile phase) throws Throwable
  {
    WORKSPACE.run(new IWorkspaceRunnable()
    {
      public void run(IProgressMonitor monitor) throws CoreException
      {
        try
        {
          updateWorkspace(phase, WORKSPACE_FILE, 0);

          for (File file : WORKSPACE_FILE.listFiles())
          {
            String name = file.getName();
            if (file.isDirectory() && !".metadata".equals(name))
            {
              IProject project = ROOT.getProject(name);
              if (project.exists())
              {
                if (project.isOpen())
                {
                  project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                }
                else
                {
                  project.open(monitor);
                }
              }
              else
              {
                project.create(monitor);
                project.open(monitor);
              }
            }
          }

          // TODO Remove deleted projects
        }
        catch (CoreException ex)
        {
          throw ex;
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Throwable ex)
        {
          throw new RuntimeException(ex);
        }
      }
    }, null);
  }

  private void updateWorkspace(BundleFile source, File target, int level) throws Throwable
  {
    if (source.getName().endsWith(DELETE_SUFFIX))
    {
      msg("- " + getRelativePath(target));
      VersionUtil.delete(target);
      return;
    }

    if (source.isDirectory())
    {
      if (!target.exists())
      {
        msg("+ " + getRelativePath(target));
        target.mkdir();
      }

      for (BundleFile sourceChild : source.getChildren())
      {
        File targetChild = new File(target, sourceChild.getName());
        updateWorkspace(sourceChild, targetChild, level + 1);
      }
    }
    else if (level > 1) // Exclude files on project level
    {
      if (!target.exists())
      {
        msg("+ " + getRelativePath(target));
      }
      else
      {
        msg("* " + getRelativePath(target));
      }

      source.copyTo(target);
    }
  }

  private IMarker[] buildWorkspace(BundleFile phase, boolean clean) throws Throwable
  {
    MSG.println("    Build " + (clean ? "clean" : "incremental"));
    long start = System.currentTimeMillis();

    if (clean)
    {
      WORKSPACE.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
      WORKSPACE.build(IncrementalProjectBuilder.FULL_BUILD, null);
    }
    else
    {
      WORKSPACE.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
    }

    msg("Took " + (System.currentTimeMillis() - start) + " millis");
    return ROOT.findMarkers(Markers.MARKER_TYPE, false, IResource.DEPTH_INFINITE);
  }

  private void fixWorkspace(BundleFile phase, IMarker[] markers)
  {
    for (IMarker marker : markers)
    {
      IMarkerResolution[] resolutions = FIX_GENERATOR.getResolutions(marker);
      if (resolutions != null && resolutions.length != 0)
      {
        assertTrue("Marker has resolutions but hasResolutions() returns false", FIX_GENERATOR.hasResolutions(marker));
        for (IMarkerResolution resolution : resolutions)
        {
          msg("Fixing " + marker.getResource().getFullPath() + ": " + resolution.getLabel());
          resolution.run(marker);
        }
      }
    }
  }

  private void processMarkers(BundleFile phase, IMarker[] markers, String fileName) throws Throwable
  {
    BundleFile markersFile = phase.getChild(fileName);
    if (markersFile != null)
    {
      MSG.println("    Check markers");
      checkMarkers(phase, markers, markersFile);
    }
    else
    {
      MSG.println("    Generate markers");
      generateMarkers(phase, markers, fileName);
    }
  }

  private void checkMarkers(BundleFile phase, IMarker[] markers, BundleFile markersFile) throws Throwable
  {
    String expected = markersFile.getContents();
    String actual = createMarkers(markers);
    assertEquals("After " + phase.getName() + " build", expected, actual);
  }

  private void generateMarkers(BundleFile phase, IMarker[] markers, String fileName) throws Throwable
  {
    String contents = createMarkers(markers);
    BundleFile resultsFile = phase.addChild(fileName, false);
    resultsFile.setContents(contents);
  }

  private static String createMarkers(IMarker[] markers) throws Throwable
  {
    if (markers.length == 0)
    {
      msg("No markers");
      return "";
    }

    Arrays.sort(markers, new Comparator<IMarker>()
    {
      public int compare(IMarker m1, IMarker m2)
      {
        int result = Markers.compareAttributes(Markers.RESOURCE_ATTRIBUTE, m1, m2);
        if (result == 0)
        {
          result = Markers.compareAttributes(IMarker.LINE_NUMBER, m1, m2);
          if (result == 0)
          {
            result = Markers.compareAttributes(IMarker.CHAR_START, m1, m2);
            if (result == 0)
            {
              result = Markers.compareAttributes(Markers.PROBLEM_TYPE, m1, m2);
            }
          }
        }

        return result;
      }
    });

    IFile lastContentsFile = null;
    String contents = null;

    StringBuilder builder = new StringBuilder();
    for (IMarker marker : markers)
    {
      msg("Marker");
      builder.append("Marker\n");

      IFile file = (IFile)marker.getResource();
      addAttribute(builder, Markers.RESOURCE_ATTRIBUTE + " ", file.getFullPath());

      Map<String, Object> attributes = marker.getAttributes();
      List<String> keys = new ArrayList<String>(attributes.keySet());
      keys.remove(IMarker.LINE_NUMBER);

      if (keys.remove(IMarker.CHAR_START))
      {
        int indexStart = (Integer)attributes.get(IMarker.CHAR_START);
        int indexEnd = -1;
        if (keys.remove(IMarker.CHAR_END))
        {
          indexEnd = (Integer)attributes.get(IMarker.CHAR_END);
        }

        if (file != lastContentsFile)
        {
          contents = VersionUtil.getContents(file);
          lastContentsFile = file;
        }

        int size = contents.length();
        for (int i = 0, lf = 1, cr = 1, column = 0; i < size; ++i, ++column)
        {
          char c = contents.charAt(i);
          if (c == '\n')
          {
            ++lf;
            column = 1;
          }
          else if (c == '\r')
          {
            ++cr;
            column = 1;
          }

          if (i == indexStart || i == indexEnd)
          {
            String value = "(" + Math.max(cr, lf) + "," + column + ")";

            if (i == indexStart)
            {
              addAttribute(builder, "<" + IMarker.CHAR_START + ">", value);
              if (indexEnd == -1)
              {
                break;
              }
            }
            else
            {
              addAttribute(builder, "<" + IMarker.CHAR_END + ">  ", value);
              break;
            }
          }
        }

      }

      if (keys.remove(IMarker.SEVERITY))
      {
        int severity = (Integer)attributes.get(IMarker.SEVERITY);
        addAttribute(builder, "<" + IMarker.SEVERITY + "> ", getSeverityLabel(severity));
      }

      if (keys.remove(IMarker.MESSAGE))
      {
        addAttribute(builder, "<" + IMarker.MESSAGE + ">  ", attributes.get(IMarker.MESSAGE));
      }

      if (keys.remove(Markers.PROBLEM_TYPE))
      {
        addAttribute(builder, Markers.PROBLEM_TYPE, attributes.get(Markers.PROBLEM_TYPE));
      }

      Collections.sort(keys);
      for (String key : keys)
      {
        Object value = attributes.get(key);
        addAttribute(builder, key, value);
      }
    }

    return builder.toString();
  }

  private static Object getSeverityLabel(int severity)
  {
    switch (severity)
    {
    case IMarker.SEVERITY_INFO:
      return "INFO";
    case IMarker.SEVERITY_WARNING:
      return "WARNING";
    case IMarker.SEVERITY_ERROR:
      return "ERROR";
    default:
      throw new IllegalStateException("Illegal severity code " + severity);
    }
  }

  private static void addAttribute(StringBuilder builder, String key, Object value)
  {
    String str = "  " + key + " = " + value;
    msg(str);

    builder.append(str);
    builder.append("\n");
  }

  private static String getRelativePath(File file)
  {
    return file.getAbsolutePath().substring(TRIM_LENGTH).replace('\\', '/');
  }

  private static void msg(String string)
  {
    MSG.println("      " + string);
  }
}
