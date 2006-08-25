/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.eclipse;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ResourcesHelper
{
  public static void writeFile(IFile file, String[] content, IProgressMonitor monitor)
      throws CoreException
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < content.length; i++)
    {
      String line = content[i];
      buffer.append(line);
      buffer.append("\n");
    }

    writeFile(file, buffer.toString(), monitor);
  }

  public static void writeFile(IFile file, String content, IProgressMonitor monitor)
      throws CoreException
  {
    InputStream stream = new ByteArrayInputStream(content.getBytes());
    writeFile(file, stream, monitor);
  }

  public static void writeFile(IFile file, InputStream content, IProgressMonitor monitor)
      throws CoreException
  {
    if (file.exists())
    {
      file.setContents(content, false, true, monitor);
    }
    else
    {
      if (file.getParent() instanceof IFolder)
      {
        mkdirs((IFolder) file.getParent(), monitor);
      }

      file.create(content, true, monitor);
    }
  }

  public static void mkdirs(IFolder folder, IProgressMonitor monitor) throws CoreException
  {
    if (folder != null && !folder.exists())
    {
      IContainer parent = folder.getParent();
      if (parent != null && parent instanceof IFolder)
      {
        mkdirs((IFolder) parent, monitor);
      }

      folder.create(true, true, monitor);
    }
  }

  public static String readFileIntoString(IFile file) throws CoreException, IOException
  {
    return readFileIntoString(file, null);
  }

  public static String readFileIntoString(IFile file, String linePrefix) throws CoreException,
      IOException
  {
    BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
    StringBuffer buffer = new StringBuffer();
    String line;

    while ((line = br.readLine()) != null)
    {
      if (linePrefix != null)
      {
        buffer.append(linePrefix);
      }

      buffer.append(line);
      buffer.append("\n");
    }

    return buffer.toString();
  }

  public static String[] readFileIntoStringArray(IFile file) throws IOException, CoreException
  {
    return readFileIntoStringArray(file.getContents());
  }

  public static String[] readFileIntoStringArray(InputStream stream) throws IOException
  {
    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
    List<String> result = new ArrayList<String>();
    String line;

    while ((line = br.readLine()) != null)
    {
      result.add(line);
    }

    return result.toArray(new String[result.size()]);
  }

  public static IProject ensureProject(String name) throws CoreException
  {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);

    if (!project.exists())
    {
      project.create(new NullProgressMonitor());
    }

    if (!project.isOpen())
    {
      project.open(new NullProgressMonitor());
    }

    return project;
  }

  public static IFolder ensureFolder(IContainer container, String path) throws CoreException
  {
    if (container == null)
    {
      container = ResourcesPlugin.getWorkspace().getRoot();
    }

    IFolder folder = container.getFolder(new Path(path));

    if (!folder.exists())
    {
      folder.create(true, true, new NullProgressMonitor());
    }

    return folder;
  }
}
