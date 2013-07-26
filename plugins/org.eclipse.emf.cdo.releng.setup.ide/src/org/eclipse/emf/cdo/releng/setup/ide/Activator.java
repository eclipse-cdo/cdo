/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.GitClone;
import org.eclipse.emf.cdo.releng.setup.Setup;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class Activator extends AbstractUIPlugin implements SetupContext
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.releng.setup.ide";

  private static Activator plugin;

  static BundleContext bundleContext;

  private File branchDir;

  private Setup setup;

  public Activator()
  {
  }

  public File getProjectDir()
  {
    return branchDir.getParentFile();
  }

  public File getBaselineDir()
  {
    return new File(getProjectDir(), ".baselines");
  }

  public File getBranchDir()
  {
    return branchDir;
  }

  public File getEclipseDir()
  {
    return new File(branchDir, "eclipse");
  }

  public File getGitDir()
  {
    return new File(branchDir, "git");
  }

  public File getWorkDir(GitClone clone)
  {
    return new File(getGitDir(), clone.getName());
  }

  public File getWorkspaceDir()
  {
    return new File(branchDir, "ws");
  }

  public File getTargetPlatformDir()
  {
    return new File(branchDir, "tp");
  }

  public Setup getSetup()
  {
    return setup;
  }

  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    bundleContext = context;
    plugin = this;

    try
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

      IPath branchDirPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().removeLastSegments(1);
      branchDir = new File(branchDirPath.toOSString());

      URI uri = URI.createFileURI(branchDirPath.append("setup.xmi").toOSString());
      Resource resource = resourceSet.getResource(uri, true);

      setup = (Setup)resource.getContents().get(0);
      SetupIDE.run();
    }
    catch (Exception ex)
    {
      log(ex);
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    setup = null;
    branchDir = null;

    plugin = null;
    super.stop(context);
  }

  public static void log(String message)
  {
    log(message, IStatus.INFO);
  }

  protected static void log(String message, int severity)
  {
    plugin.getLog().log(new Status(severity, PLUGIN_ID, message));
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

  public static Activator getDefault()
  {
    return plugin;
  }

  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  /**
   * @author Eike Stepper
   */
  public static final class EarlyStartup implements IStartup
  {
    public void earlyStartup()
    {
    }
  }
}
