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

import org.eclipse.emf.cdo.releng.internal.version.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Eike Stepper
 */
public final class VersionUtil
{
  public static final String BUILDER_ID = "org.eclipse.emf.cdo.releng.version.VersionBuilder";

  public static final boolean DEBUG = Boolean.valueOf(System.getProperty("org.eclipse.emf.cdo.releng.version.debug",
      "false"));

  private static final byte[] BUFFER = new byte[8192];

  private VersionUtil()
  {
  }

  public static IModel getComponentModel(IProject project)
  {
    IModel componentModel = PluginRegistry.findModel(project);
    if (componentModel == null)
    {
      componentModel = getFeatureModel(project);
      if (componentModel == null)
      {
        throw new IllegalStateException("The project " + project.getName() + " is neither a plugin nor a feature");
      }
    }

    return componentModel;
  }

  @SuppressWarnings("restriction")
  public static org.eclipse.pde.internal.core.ifeature.IFeatureModel getFeatureModel(IProject project)
  {
    org.eclipse.pde.internal.core.ifeature.IFeatureModel[] featureModels = org.eclipse.pde.internal.core.PDECore
        .getDefault().getFeatureModelManager().getWorkspaceModels();

    for (org.eclipse.pde.internal.core.ifeature.IFeatureModel featureModel : featureModels)
    {
      if (featureModel.getUnderlyingResource().getProject() == project)
      {
        return featureModel;
      }
    }

    return null;
  }

  @SuppressWarnings("restriction")
  public static Version getComponentVersion(IModel componentModel)
  {
    if (componentModel instanceof IPluginModelBase)
    {
      IPluginModelBase pluginModel = (IPluginModelBase)componentModel;
      return normalize(pluginModel.getBundleDescription().getVersion());
    }

    Version version = new Version(((org.eclipse.pde.internal.core.ifeature.IFeatureModel)componentModel).getFeature()
        .getVersion());
    return normalize(version);
  }

  public static void close(Closeable closeable)
  {
    if (closeable != null)
    {
      try
      {
        closeable.close();
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }
  }

  public static IFile getFile(IPath releasePath, String extension)
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFile file = root.getFile(releasePath);
    IPath path = file.getFullPath().removeFileExtension().addFileExtension(extension);
    return root.getFile(path);
  }

  public static Version normalize(Version version)
  {
    return new Version(version.getMajor(), version.getMinor(), version.getMicro());
  }

  public static synchronized byte[] getSHA1(IFile file) throws NoSuchAlgorithmException, CoreException, IOException
  {
    InputStream stream = null;

    try
    {
      final MessageDigest digest = MessageDigest.getInstance("SHA-1");
      stream = new FilterInputStream(file.getContents())
      {
        @Override
        public int read() throws IOException
        {
          for (;;)
          {
            int ch = super.read();
            switch (ch)
            {
            case -1:
              return -1;

            case 10:
            case 13:
              continue;
            }

            digest.update((byte)ch);
            return ch;
          }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException
        {
          int read = super.read(b, off, len);
          if (read == -1)
          {
            return -1;
          }

          for (int i = off; i < off + read; i++)
          {
            byte c = b[i];
            if (c == 10 || c == 13)
            {
              if (i + 1 < off + read)
              {
                System.arraycopy(b, i + 1, b, i, read - i - 1);
                --i;
              }

              --read;
            }
          }

          digest.update(b, off, read);
          return read;
        }
      };

      while (stream.read(BUFFER) != -1)
      {
        // Do nothing
      }

      return digest.digest();
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
          Activator.log(ex);
        }
      }
    }
  }
}
