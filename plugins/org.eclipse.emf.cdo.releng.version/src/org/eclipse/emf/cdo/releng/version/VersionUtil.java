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
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
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

  public static boolean equals(Object o1, Object o2)
  {
    if (o1 == null)
    {
      return o2 == null;
    }

    return o1.equals(o2);
  }

  public static Version normalize(Version version)
  {
    return new Version(version.getMajor(), version.getMinor(), version.getMicro());
  }

  public static IFile getFile(IPath path, String extension)
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFile file = root.getFile(path);
    IPath newPath = file.getFullPath().removeFileExtension().addFileExtension(extension);
    return root.getFile(newPath);
  }

  public static String getContents(IFile file) throws CoreException, IOException
  {
    InputStream contents = null;

    try
    {
      contents = file.getContents();
      BufferedReader reader = new BufferedReader(new InputStreamReader(contents, file.getCharset()));
      CharArrayWriter caw = new CharArrayWriter();

      int c;
      while ((c = reader.read()) != -1)
      {
        caw.write(c);
      }

      return caw.toString();
    }
    finally
    {
      close(contents);
    }
  }

  public static String getLineDelimiter(IFile file) throws IOException
  {
    InputStream inputStream = null;
    try
    {
      inputStream = file.getContents();
      String encoding = file.getCharset();
      Reader reader = encoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream,
          encoding);
      char[] text = new char[4048];
      char target = 0;
      for (int count = reader.read(text); count > -1; count = reader.read(text))
      {
        for (int i = 0; i < count; ++i)
        {
          char character = text[i];
          if (character == '\n')
          {
            if (target == '\n')
            {
              return "\n";
            }
            else if (target == '\r')
            {
              return "\r\n";
            }
            else
            {
              target = '\n';
            }
          }
          else if (character == '\r')
          {
            if (target == '\n')
            {
              return "\n\r";
            }
            else if (target == '\r')
            {
              return "\r";
            }
            else
            {
              target = '\r';
            }
          }
        }
      }
    }
    catch (Exception exception)
    {
      // If we can't determine it by reading the file,
      // look at the preferences instead.
    }
    finally
    {
      if (inputStream != null)
      {
        inputStream.close();
      }
    }

    return Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR,
        System.getProperty(Platform.PREF_LINE_SEPARATOR),
        new IScopeContext[] { new ProjectScope(file.getProject()), InstanceScope.INSTANCE });
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

      synchronized (BUFFER)
      {
        while (stream.read(BUFFER) != -1)
        {
          // Do nothing
        }
      }

      return digest.digest();
    }
    finally
    {
      close(stream);
    }
  }

  public static void copy(InputStream in, OutputStream out) throws IOException
  {
    synchronized (BUFFER)
    {
      int n;
      while ((n = in.read(BUFFER)) != -1)
      {
        out.write(BUFFER, 0, n);
      }
    }
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

  public static int delete(File file)
  {
    if (file == null)
    {
      return 0;
    }

    int deleted = 0;
    if (file.isDirectory())
    {
      for (File child : file.listFiles())
      {
        deleted += delete(child);
      }
    }

    if (file.delete())
    {
      return deleted + 1;
    }

    file.deleteOnExit();
    return deleted;
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
}
