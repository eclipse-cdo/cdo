/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.winexplorer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.io.File;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLocationAction implements IObjectActionDelegate
{
  private File location;

  public AbstractLocationAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      Object element = ssel.getFirstElement();
      location = getLocation(element);
    }
    else
    {
      location = null;
    }
  }

  protected File getLocation(Object element)
  {
    IContainer container = getContainer(element);
    if (container != null)
    {
      return new File(container.getLocation().toOSString());
    }

    File directory = getDirectory(element);
    if (directory != null)
    {
      return directory;
    }

    File workTree = getWorkTree(element);
    if (workTree != null)
    {
      return workTree;
    }

    return null;
  }

  protected IContainer getContainer(Object element)
  {
    if (element instanceof IContainer)
    {
      return (IContainer)element;
    }

    if (element instanceof IResource)
    {
      return ((IResource)element).getParent();
    }

    if (element instanceof IAdaptable)
    {
      Object adapter = ((IAdaptable)element).getAdapter(IResource.class);
      return getContainer(adapter);
    }

    return null;
  }

  protected File getDirectory(Object element)
  {
    if (element instanceof File)
    {
      File file = (File)element;
      if (file.isDirectory())
      {
        return file;
      }

      return file.getParentFile();
    }

    if (element instanceof IAdaptable)
    {
      Object adapter = ((IAdaptable)element).getAdapter(File.class);
      return getDirectory(adapter);
    }

    return null;
  }

  protected File getWorkTree(Object element)
  {
    try
    {
      return GitHelper.getWorkTree(element);
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  public void run(IAction action)
  {
    try
    {
      run(location);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  protected abstract void run(File location) throws Exception;

  /**
   * @author Eike Stepper
   */
  private static final class GitHelper
  {
    public static File getWorkTree(Object element)
    {
      if (element instanceof Repository)
      {
        return ((Repository)element).getWorkTree();
      }

      if (element instanceof IAdaptable)
      {
        Object adapter = ((IAdaptable)element).getAdapter(Repository.class);
        return getWorkTree(adapter);
      }

      return null;
    }
  }
}
