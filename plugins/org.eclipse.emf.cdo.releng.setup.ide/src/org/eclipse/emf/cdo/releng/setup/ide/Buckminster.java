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

import org.eclipse.emf.cdo.releng.setup.ApiBaseline;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.GitClone;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.helper.Downloads;
import org.eclipse.emf.cdo.releng.setup.helper.Files;
import org.eclipse.emf.cdo.releng.setup.helper.OS;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLogProvider;

import org.eclipse.net4j.util.io.FileLock;
import org.eclipse.net4j.util.io.ZIPUtil;
import org.eclipse.net4j.util.io.ZIPUtil.FileSystemUnzipHandler;

import org.eclipse.buckminster.cmdline.Headless;
import org.eclipse.buckminster.core.commands.Import;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.equinox.p2.publisher.AbstractPublisherApplication;
import org.eclipse.equinox.p2.publisher.eclipse.FeaturesAndBundlesPublisherApplication;
import org.eclipse.ui.internal.progress.ProgressManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class Buckminster
{
  private static final SetupContext CONTEXT = Activator.getDefault();

  // TODO Don't use Headless because if cancels important jobs! Use e.g. ImportAction code instead...
  public static void run(String... args) throws Exception
  {
    Activator.log("Buckminster command: " + Arrays.asList(args));

    try
    {
      Headless buckminster = new Headless();
      buckminster.run(args);
    }
    finally
    {
      Job.getJobManager().setProgressProvider(ProgressManager.getInstance());
    }
  }

  public static void importBaseline() throws Exception
  {
    Branch branch = CONTEXT.getSetup().getBranch();
    ApiBaseline apiBaseline = branch.getApiBaseline();
    if (apiBaseline == null)
    {
      return;
    }

    boolean autoBuilding = disableAutoBuilding();

    try
    {
      String version = apiBaseline.getVersion();
      Progress.log().addLine("Setting baseline " + version);

      final File baselineDir = CONTEXT.getBaselineDir();
      File target = new File(baselineDir, version);
      if (!target.exists())
      {
        String url = apiBaseline.getZipLocation();
        File file = Downloads.downloadURL(url);
        baselineDir.mkdirs();

        final File[] rootDir = { null };
        ZIPUtil.unzip(file, new FileSystemUnzipHandler(baselineDir, ZIPUtil.DEFAULT_BUFFER_SIZE)
        {
          @Override
          public void unzipFile(String name, InputStream zipStream)
          {
            if (rootDir[0] == null)
            {
              rootDir[0] = new File(baselineDir, new Path(name).segment(0));
            }

            Progress.log().addLine("Unzipping " + name);
            super.unzipFile(name, zipStream);
          }
        });

        rootDir[0].renameTo(target);
      }

      String name = apiBaseline.getProject().getName() + " Baseline";
      importTarget(target, name, false);

      Progress.log().addLine("Setting baseline: " + name);
      run("addbaseline", "-A", name);
    }
    finally
    {
      restoreAutoBuilding(autoBuilding);
    }
  }

  private static String getTargetName()
  {
    Setup setup = CONTEXT.getSetup();
    Branch branch = setup.getBranch();
    Project project = branch.getProject();
    return project.getName() + " Target";
  }

  private static String createTargetDefinition(File folder, String name) throws IOException
  {
    File xml = getTargetXML(folder);
    String xmlPath = xml.getAbsolutePath();
    Progress.log().addLine("Creating target definition: " + xmlPath);

    List<String> lines = new ArrayList<String>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
    lines.add("<?pde version=\"3.8\"?>");
    lines.add("<target name=\"" + name + "\" sequenceNumber=\"0\">");
    lines.add(" <locations>");
    lines.add("   <location path=\"" + folder.getCanonicalPath().replace('\\', '/') + "\" type=\"Directory\"/>");
    lines.add(" </locations>");
    lines.add("</target>");

    folder.mkdirs();
    OS.INSTANCE.writeText(xml, lines);
    return xmlPath;
  }

  private static void importTarget(File folder, String name, boolean activate) throws Exception
  {
    String xmlPath = createTargetDefinition(folder, name);
    Progress.log().addLine("Loading target definition: " + xmlPath);

    if (activate)
    {
      run("importtarget", "--active", xmlPath);
    }
    else
    {
      run("importtarget", xmlPath);
    }
  }

  public static void importTarget() throws Exception
  {
    File targetPlatformDir = CONTEXT.getTargetPlatformDir();
    String name = getTargetName();

    boolean autoBuilding = disableAutoBuilding();

    try
    {
      importTarget(targetPlatformDir, name, true);
    }
    finally
    {
      restoreAutoBuilding(autoBuilding);
    }
  }

  public static void importMSpec() throws Exception
  {
    initVars();

    File tpOld = null;
    File tp = CONTEXT.getTargetPlatformDir();
    if (tp.exists())
    {
      tpOld = new File(tp.getParentFile(), tp.getName() + "." + System.currentTimeMillis());
      Files.rename(tp, tpOld);
    }

    FileLock tpPoolLock = null;
    boolean autoBuilding = disableAutoBuilding();

    try
    {
      File tpPool = updateTargetPlatformPool();
      tpPoolLock = FileLock.forWrite(tpPool);

      File gitDir = CONTEXT.getGitDir();
      Setup setup = CONTEXT.getSetup();

      Progress.log().addLine("Creating new target platform folder");
      createTargetDefinition(tp, getTargetName());

      String bomPath = new File(CONTEXT.getBranchDir(), "bom.xml").getAbsolutePath();
      String mspecPath = new File(gitDir, setup.getBranch().getMspecFilePath()).getAbsolutePath();
      Progress.log().addLine("Importing MSpec " + mspecPath);

      run("--displaystacktrace", "import2", "-B", bomPath, mspecPath);

      if (tpPoolLock != null)
      {
        updateBundlePool(tp.getAbsolutePath(), tpPool.getAbsolutePath());
      }

      if (Progress.log().isCancelled())
      {
        throw new InterruptedException();
      }
    }
    catch (Exception ex)
    {
      File tpBroken = new File(tp.getParentFile(), tp.getName() + "." + System.currentTimeMillis());
      Files.rename(tp, tpBroken);
      if (tpOld != null)
      {
        Files.rename(tpOld, tp);
      }

      Files.deleteAsync(tpBroken);
      throw ex;
    }
    finally
    {
      try
      {
        if (tpPoolLock != null)
        {
          tpPoolLock.release();
        }
      }
      finally
      {
        restoreAutoBuilding(autoBuilding);
      }
    }

    if (tpOld != null)
    {
      Files.deleteAsync(tpOld);
    }
  }

  private static File updateTargetPlatformPool() throws Exception
  {
    File installFolder = new File(CONTEXT.getSetup().getPreferences().getInstallFolder());

    File idePool = new File(installFolder, ".p2pool-ide");
    idePool.mkdirs();

    File tpPool = new File(installFolder, ".p2pool-tp");
    tpPool.mkdirs();

    FileLock idePoolLock = FileLock.forWrite(idePool);

    try
    {
      updateBundlePool(idePool.getAbsolutePath(), tpPool.getAbsolutePath());
    }
    finally
    {
      idePoolLock.release();
    }

    return tpPool;
  }

  private static File getTargetXML(File folder)
  {
    return new File(folder, "target.xml");
  }

  private static void initVars() throws Exception
  {
    Setup setup = CONTEXT.getSetup();
    Branch branch = setup.getBranch();

    for (GitClone gitClone : branch.getGitClones())
    {
      File workDir = CONTEXT.getWorkDir(gitClone);
      String cloneName = workDir.getName();
      String variableName = branch.getCloneVariableName();
      if (variableName == null)
      {
        variableName = "git.clone." + cloneName;
      }

      String description = "Location of " + cloneName + " git clone";
      Variables.set(variableName, description, workDir.getAbsolutePath().replace('\\', '/'));
    }
  }

  private static void updateBundlePool(String source, String bundlePool) throws Exception
  {
    Progress.log().addLine("Updating bundle pool: " + bundlePool);

    String bundlePoolURL = "file:/" + bundlePool.replace('\\', '/');
    String[] args = { "-source", source, "-metadataRepository", bundlePoolURL, "-artifactRepository", bundlePoolURL,
        "-append", "-publishArtifacts" };

    AbstractPublisherApplication publisher = new FeaturesAndBundlesPublisherApplication();
    publisher.run(args);
  }

  public static boolean disableAutoBuilding() throws CoreException
  {
    boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();
    if (autoBuilding)
    {
      restoreAutoBuilding(false);
    }

    return autoBuilding;
  }

  public static void restoreAutoBuilding(boolean autoBuilding) throws CoreException
  {
    if (autoBuilding != ResourcesPlugin.getWorkspace().isAutoBuilding())
    {
      IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
      description.setAutoBuilding(autoBuilding);

      ResourcesPlugin.getWorkspace().setDescription(description);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ImportCommand extends Import
  {
    @Override
    public ProgressProvider getProgressProvider()
    {
      return new ProgressLogProvider(Progress.log(), ProgressManager.getInstance());
    }
  }
}
