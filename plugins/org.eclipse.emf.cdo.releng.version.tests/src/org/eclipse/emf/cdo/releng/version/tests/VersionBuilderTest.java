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
import org.eclipse.core.runtime.NullProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public class VersionBuilderTest extends TestCase
{
  private static final PrintStream MSG = System.out;

  private static final String RESULTS_FILE = "results.txt";

  private static final long BUILD_TIMEOUT = 10; // SECONDS

  private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

  private static final IWorkspaceRoot ROOT = WORKSPACE.getRoot();

  private static final File WORKSPACE_FILE = ROOT.getLocation().toFile();

  private static final int TRIM_LENGTH = WORKSPACE_FILE.getAbsolutePath().length() + 1;

  private static final String[] PHASES = { "clean", "incremental" };

  private static final String DELETE_SUFFIX = "-DELETE";

  private BundleFile testFolder;

  public VersionBuilderTest(BundleFile testFolder)
  {
    super(testFolder.getName());
    this.testFolder = testFolder;
  }

  @Override
  public void runBare() throws Throwable
  {
    MSG.println("Test " + getName());
    WORKSPACE.getDescription().setAutoBuilding(false);
    clearWorkspace();

    boolean clean = true;
    for (String phase : PHASES)
    {
      final BundleFile folder = testFolder.getChild(phase);
      if (folder != null)
      {
        MSG.println("  Update workspace for " + phase + " build");
        WORKSPACE.run(new IWorkspaceRunnable()
        {
          public void run(IProgressMonitor monitor) throws CoreException
          {
            try
            {
              updateWorkspace(folder, monitor);
            }
            catch (IOException ex)
            {
              ex.printStackTrace();
              // TODO throw CoreException?
            }
          }
        }, new NullProgressMonitor());

        MSG.println("  Build " + phase);
        long start = System.currentTimeMillis();
        IMarker[] markers = buildWorkspace(clean);
        msg("Took " + (System.currentTimeMillis() - start) + " millis");

        final BundleFile resultsFile = folder.getChild(RESULTS_FILE);
        if (resultsFile != null)
        {
          MSG.println("  Check results of " + phase + " build");
          checkResults(phase, resultsFile, markers);
        }
        else
        {
          MSG.println("  Generate results of " + phase + " build");
          generateResults(folder, markers);
        }
      }

      clean = false;
    }

    MSG.println();
  }

  private void clearWorkspace() throws CoreException
  {
    boolean first = true;
    for (IProject project : ROOT.getProjects())
    {
      if (first)
      {
        MSG.println("  Clear workspace");
        first = false;
      }

      msg("Deleting project " + project.getName());
      project.delete(true, new NullProgressMonitor());
    }

    for (File file : WORKSPACE_FILE.listFiles())
    {
      if (file.isDirectory() && !".metadata".equals(file.getName()))
      {
        msg("Deleting location " + file);
        VersionUtil.delete(file);
      }
    }
  }

  private void updateWorkspace(BundleFile updateFolder, IProgressMonitor monitor) throws CoreException, IOException
  {
    updateWorkspace(updateFolder, WORKSPACE_FILE, true);

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
  }

  private void updateWorkspace(BundleFile source, File target, boolean onlyDirectories) throws IOException
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
        updateWorkspace(sourceChild, targetChild, false);
      }
    }
    else if (!onlyDirectories)
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

  private IMarker[] buildWorkspace(boolean clean) throws CoreException, InterruptedException
  {
    final CountDownLatch done = new CountDownLatch(1);
    IProgressMonitor monitor = new NullProgressMonitor()
    {
      @Override
      public void done()
      {
        done.countDown();
        super.done();
      }
    };

    int kind = clean ? IncrementalProjectBuilder.CLEAN_BUILD : IncrementalProjectBuilder.INCREMENTAL_BUILD;
    WORKSPACE.build(kind, monitor);

    if (!done.await(BUILD_TIMEOUT, TimeUnit.SECONDS))
    {
      monitor.setCanceled(true);
      throw new RuntimeException("Build timed out");
    }

    return ROOT.findMarkers(Markers.MARKER_TYPE, false, IResource.DEPTH_INFINITE);
  }

  private void checkResults(String phase, BundleFile resultsFile, IMarker[] markers) throws CoreException
  {
    String expectedResults = resultsFile.getContents();
    String actualResults = createResults(markers);
    assertEquals("After " + phase + " build", expectedResults, actualResults);
  }

  private void generateResults(BundleFile folder, IMarker[] markers) throws CoreException, IOException
  {
    String results = createResults(markers);

    BundleFile resultsFile = folder.addChild(RESULTS_FILE, false);
    resultsFile.setContents(results);
  }

  private String createResults(IMarker[] markers) throws CoreException
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

    StringBuilder builder = new StringBuilder();
    for (IMarker marker : markers)
    {
      msg("Marker");
      builder.append("Marker\n");
      addAttribute(builder, Markers.RESOURCE_ATTRIBUTE, marker.getResource().getFullPath());

      Map<String, Object> attributes = marker.getAttributes();
      List<String> keys = new ArrayList<String>(attributes.keySet());
      Collections.sort(keys);

      for (String key : keys)
      {
        Object value = attributes.get(key);
        addAttribute(builder, key, value);
      }
    }

    return builder.toString();
  }

  private void addAttribute(StringBuilder builder, String key, Object value)
  {
    String str = key + " = " + value;
    msg("  " + str);

    builder.append(str);
    builder.append("\n");
  }

  private static String getRelativePath(File file)
  {
    return file.getAbsolutePath().substring(TRIM_LENGTH).replace('\\', '/');
  }

  private static void msg(String string)
  {
    MSG.println("    " + string);
  }
}
