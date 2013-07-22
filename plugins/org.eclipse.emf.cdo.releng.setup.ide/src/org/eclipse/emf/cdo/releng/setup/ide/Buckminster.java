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
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.helper.OS;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLogProvider;

import org.eclipse.net4j.util.io.ZIPUtil;
import org.eclipse.net4j.util.io.ZIPUtil.FileSystemUnzipHandler;

import org.eclipse.buckminster.cmdline.Headless;
import org.eclipse.buckminster.core.commands.Import;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.equinox.p2.publisher.AbstractPublisherApplication;
import org.eclipse.equinox.p2.publisher.eclipse.FeaturesAndBundlesPublisherApplication;
import org.eclipse.ui.internal.progress.ProgressManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class Buckminster
{
  private static final String NO_POOL = "none";

  private static final SetupContext CONTEXT = Activator.getDefault();

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

    String version = apiBaseline.getVersion();
    Progress.log().addLine("Setting baseline " + version);

    final File baselineDir = CONTEXT.getBaselineDir();
    File target = new File(baselineDir, version);
    if (!target.exists())
    {
      // File file = new File("C:/develop/bin/zips/emf-cdo-4.2-baseline.zip");
      String url = apiBaseline.getZipLocation();
      File file = OS.INSTANCE.downloadURL(url);
      baselineDir.mkdirs();

      final File[] rootDir = { null };
      ZIPUtil.unzip(file, new FileSystemUnzipHandler(baselineDir, ZIPUtil.DEFALULT_BUFFER_SIZE)
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

  private static void importTarget(File folder, String name, boolean activate) throws Exception
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
    Setup setup = CONTEXT.getSetup();
    File targetPlatformDir = CONTEXT.getTargetPlatformDir();
    String name = setup.getBranch().getProject().getName() + " Target";

    importTarget(targetPlatformDir, name, true);
  }

  public static void importMSpec() throws Exception
  {
    File tp = CONTEXT.getTargetPlatformDir();
    File tpOld = tp.exists() ? new File(tp.getParentFile(), tp.getName() + "." + System.currentTimeMillis()) : null;
    if (tpOld != null)
    {
      Files.rename(tp, tpOld);
    }

    try
    {
      File gitDir = CONTEXT.getGitDir();
      Setup setup = CONTEXT.getSetup();

      tp.mkdirs();
      if (tpOld != null)
      {
        List<String> lines = OS.INSTANCE.readText(getTargetXML(tpOld));
        OS.INSTANCE.writeText(getTargetXML(tp), lines);
      }

      String bundlePool = getBundlePool();
      Variables.set("p2.pool", "Location of p2 bundle pool", bundlePool);

      String bomPath = new File(CONTEXT.getBranchDir(), "bom.xml").getAbsolutePath();
      String mspecPath = new File(gitDir, setup.getBranch().getMspecFilePath()).getAbsolutePath();
      run("--displaystacktrace", "import2", "-B", bomPath, mspecPath);

      if (Progress.log().isCancelled())
      {
        throw new InterruptedException();
      }

      if (bundlePool != NO_POOL)
      {
        updateBundlePool(bundlePool);
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

      Files.delete(tpBroken);
      throw ex;
    }

    if (tpOld != null)
    {
      Files.delete(tpOld);
    }
  }

  private static File getTargetXML(File folder)
  {
    return new File(folder, "target.xml");
  }

  private static String getBundlePool() throws Exception
  {
    String bundlePool = CONTEXT.getSetup().getPreferences().getBundlePool();
    if (bundlePool != null && bundlePool.length() != 0)
    {
      bundlePool = bundlePool.replace('\\', '/');

      if (!new File(bundlePool, "content.xml").exists())
      {
        updateBundlePool(bundlePool);
      }

      return bundlePool;
    }

    return NO_POOL;
  }

  private static void updateBundlePool(final String bundlePool) throws Exception
  {
    new Job("Update bundle pool")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          String bundlePoolURL = "file:/" + bundlePool;
          String targetPlatform = CONTEXT.getTargetPlatformDir().getAbsolutePath();
          String[] args = { "-source", targetPlatform, "-metadataRepository", bundlePoolURL, "-artifactRepository",
              bundlePoolURL, "-append", "-publishArtifacts" };

          Activator.log("Publisher command: " + Arrays.asList(args));

          AbstractPublisherApplication publisher = new FeaturesAndBundlesPublisherApplication();
          publisher.run(args);

          return Status.OK_STATUS;
        }
        catch (Exception ex)
        {
          Activator.log(ex);
          return Activator.getStatus(ex);
        }
      }
    }.schedule();
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
