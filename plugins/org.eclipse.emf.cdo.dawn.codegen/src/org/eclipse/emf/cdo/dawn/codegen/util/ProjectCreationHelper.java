/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Martin Fluegge
 */
public class ProjectCreationHelper
{

  private String name;

  private String[] natures;

  public String getName()
  {
    return name;
  }

  public IProject getProject()
  {
    return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
  }

  public IProject createProject() throws CoreException
  {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);

    if (!project.exists())
    {
      project.create(null);
    }

    if (!project.isOpen())
    {
      project.open(null);
    }

    IProjectDescription description = project.getDescription();
    description.setNatureIds(natures);
    project.setDescription(description, null);

    return project;
  }

  /**
   * @param path
   * @param javaProject
   * @throws JavaModelException
   */
  public static void addJarToBuildPath(String path, IJavaProject javaProject) throws JavaModelException
  {
    IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(new Path(path), null, null);
    ProjectCreationHelper.addToClasspath(javaProject, newLibraryEntry);
  }

  public static void addJarToBuildPath(IPath path, IJavaProject javaProject) throws JavaModelException
  {
    IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(path, null, null);
    ProjectCreationHelper.addToClasspath(javaProject, newLibraryEntry);
  }

  public static void addVariableEntryToBuildPath(IPath path, IJavaProject javaProject) throws JavaModelException
  {
    IClasspathEntry newLibraryEntry = JavaCore.newVariableEntry(path, null, null);
    ProjectCreationHelper.addToClasspath(javaProject, newLibraryEntry);
  }

  /**
   * adds all jar files in this folder to the build path. Searches only on level 1
   *
   * @param folder
   *          the folder which contains the jar files
   * @param javaProject
   * @throws CoreException
   */
  public static void addAllJarsToBuildPath(IFolder folder, IJavaProject javaProject) throws CoreException
  {
    try
    {
      folder.refreshLocal(100, new NullProgressMonitor());
      for (IResource resource : folder.members())
      {

        if (resource instanceof IFile && resource.getRawLocation().toString().endsWith(".jar"))
        {
          addJarToBuildPath(resource.getRawLocation(), javaProject);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * @param javaProject
   * @param newEntry
   * @throws JavaModelException
   */
  public final static void addToClasspath(IJavaProject javaProject, IClasspathEntry newEntry) throws JavaModelException
  {
    if (newEntry == null)
    {
      return;
    }
    IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
    System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);

    newEntries[oldEntries.length] = newEntry;
    javaProject.setRawClasspath(newEntries, null);
  }

  /**
   * @param javaProject
   * @param toBeRemoved
   * @throws JavaModelException
   */
  public final static void removeFromClasspath(IJavaProject javaProject, IPath toBeRemoved) throws JavaModelException
  {
    IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>();

    for (IClasspathEntry classpathEntry : oldEntries)
    {
      if (!classpathEntry.getPath().equals(toBeRemoved))
      {
        newEntries.add(classpathEntry);
      }
    }

    IClasspathEntry[] newEntriesArray = new IClasspathEntry[newEntries.size()];
    javaProject.setRawClasspath(newEntries.toArray(newEntriesArray), null);
  }

  /**
   * @return the newly created java project
   * @throws CoreException
   */
  public IJavaProject createJavaProject() throws CoreException
  {
    IProject project = createProject();
    return createJavaProject(project);
  }

  /**
   * Creates a JavaProject prom the given project
   *
   * @param project
   * @return the created JavaProject
   */
  public IJavaProject createJavaProject(IProject project)
  {
    try
    {
      // addJavaNature(project);
      IJavaProject javaProject = JavaCore.create(project);
      if (!javaProject.isOpen())
      {
        javaProject.open(null);
      }
      createBinFolder(project, javaProject);
      clearSourcePath(javaProject);

      IClasspathEntry sourceFolder = createSourceFolder("src", project);

      addToClasspath(javaProject, sourceFolder);
      addJREContainerToProject(javaProject);
      return javaProject;
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
    return null;

  }

  /**
   * @param javaProject
   * @throws JavaModelException
   */
  private void addJREContainerToProject(IJavaProject javaProject) throws JavaModelException
  {
    addToClasspath(javaProject, JavaRuntime.getDefaultJREContainerEntry());
  }

  /**
   * @param javaProject
   * @throws JavaModelException
   */
  private void clearSourcePath(IJavaProject javaProject) throws JavaModelException
  {
    javaProject.setRawClasspath(new IClasspathEntry[] {}, new NullProgressMonitor()); // clean classpath, means remove
    // Project root from classpath
  }

  /**
   * @param project
   * @throws CoreException
   */
  public void addJavaNature(IProject project) throws CoreException
  {
    IProjectDescription description = project.getDescription();
    String[] natures = description.getNatureIds();
    String[] newNatures = new String[natures.length + 1];
    System.arraycopy(natures, 0, newNatures, 0, natures.length);
    newNatures[natures.length] = JavaCore.NATURE_ID;
    description.setNatureIds(newNatures);
    project.setDescription(description, new NullProgressMonitor());
  }

  /**
   * @param path
   * @param project
   * @return
   * @throws CoreException
   */
  private IClasspathEntry createSourceFolder(String path, IProject project) throws CoreException
  {
    IFolder srcFolder = project.getFolder(new Path(path));
    if (!srcFolder.exists())
    {
      srcFolder.create(false, true, null);
      IPath sourceFolderPath = srcFolder.getFullPath();
      IClasspathEntry entry = JavaCore.newSourceEntry(sourceFolderPath);

      return entry;
    }
    return null;
  }

  /**
   * @param path
   * @param project
   * @param javaProject
   * @return the created source folder
   * @throws CoreException
   */
  public IFolder createSourceFolder(String path, IProject project, IJavaProject javaProject) throws CoreException
  {
    IFolder srcFolder = project.getFolder(new Path(path));
    if (!srcFolder.exists())
    {
      srcFolder.create(false, true, null);
      IPath sourceFolderPath = srcFolder.getFullPath();
      IClasspathEntry entry = JavaCore.newSourceEntry(sourceFolderPath);
      addToClasspath(javaProject, entry);
      return srcFolder;
    }
    return null;
  }

  /**
   * @param project
   * @param javaProject
   * @throws CoreException
   * @throws JavaModelException
   */
  private void createBinFolder(IProject project, IJavaProject javaProject) throws CoreException, JavaModelException
  {
    createOutputFolder("bin", project, javaProject);
  }

  /**
   * @param project
   * @param javaProject
   * @throws CoreException
   * @throws JavaModelException
   */
  private void createOutputFolder(String path, IProject project, IJavaProject javaProject)
      throws CoreException, JavaModelException
  {
    try
    {
      IFolder binFolder = project.getFolder(new Path(path));
      if (!binFolder.exists())
      {
        binFolder.create(true, true, null);
        javaProject.setOutputLocation(binFolder.getFullPath(), null);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * @param name
   * @param project
   * @return the created folder
   */
  public IFolder createFolder(String name, IProject project)
  {
    IFolder folder = project.getFolder(name);

    if (!folder.exists())
    {
      try
      {
        folder.create(IResource.NONE, true, null);
      }
      catch (CoreException e)
      {
        e.printStackTrace();
      }
    }
    return folder;
  }

  public IFolder getFolder(String name, IProject project)
  {
    IFolder folder = project.getFolder(name);

    return folder;
  }

  /**
   * @param name
   * @param folder
   * @param content
   * @return the created file
   */
  public IFile createFile(String name, IFolder folder, String content)
  {
    IFile file = folder.getFile("web.xml");

    try
    {

      if (!file.exists())
      {
        byte[] bytes = content.getBytes();
        InputStream source = new ByteArrayInputStream(bytes);
        file.create(source, IResource.NONE, null);

      }
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
    return file;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String[] getNatures()
  {
    return natures;
  }

  public void setNatures(String[] natures)
  {
    this.natures = natures;
  }

  public ProjectCreationHelper()
  {
  }

  public static void refreshProject(IResource resource, IProgressMonitor monitor)
      throws InvocationTargetException, InterruptedException
  {
    IRunnableWithProgress op = new WorkspaceModifyOperation(null)
    {
      @Override
      protected void execute(IProgressMonitor monitor) throws CoreException, InterruptedException
      {
        try
        {
          IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
          root.refreshLocal(IResource.DEPTH_INFINITE, monitor);

        }
        catch (CoreException e)
        {

          e.printStackTrace();
        }
      }
    };
    op.run(monitor);
  }
}
