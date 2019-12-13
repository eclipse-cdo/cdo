/*
 * Copyright (c) 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.apireports;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class ApiReportsHandler extends AbstractHandler
{
  public ApiReportsHandler()
  {
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ISelection selection = HandlerUtil.getActiveMenuSelection(event);
    IFile file = getFile(((IStructuredSelection)selection).getFirstElement());

    InputStream contents = null;

    try
    {
      contents = file.getContents();

      Properties properties = new Properties();
      properties.load(contents);

      final String reportFileName = getProperty(properties, "reportFileName",
          file.getParent().getLocation().append("api.xml").toOSString());
      final String baselineName = getProperty(properties, "baselineName", null);
      final String exclusionPatterns = getProperty(properties, "exclusionPatterns", null);

      new ProgressMonitorDialog(HandlerUtil.getActiveShell(event)).run(true, true, new IRunnableWithProgress()
      {
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          try
          {
            IStatus status = ApiReportsGenerator.generate(reportFileName, baselineName, exclusionPatterns, monitor);
            if (status.isOK())
            {
              IFile apiReport = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(reportFileName));
              if (apiReport != null)
              {
                final IContainer parent = apiReport.getParent();
                parent.refreshLocal(IResource.DEPTH_INFINITE, monitor);
              }
            }
            else if (status.getSeverity() != IStatus.CANCEL)
            {
              Activator.log(status);
            }
          }
          catch (CoreException ex)
          {
            Activator.log(ex.getStatus());
          }
          catch (Throwable ex)
          {
            Activator.log(ex);
          }
        }
      });
    }
    catch (CoreException ex)
    {
      Activator.log(ex.getStatus());
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
    }
    finally
    {
      if (contents != null)
      {
        try
        {
          contents.close();
        }
        catch (Throwable ex)
        {
          Activator.log(ex);
        }
      }
    }

    return null;
  }

  private static String getProperty(Properties properties, String key, String defaultValue)
  {
    String value = properties.getProperty(key);
    if (value == null || value.length() == 0)
    {
      value = defaultValue;
    }

    return value;
  }

  private static IFile getFile(Object element)
  {
    if (element instanceof IFile)
    {
      return (IFile)element;
    }

    if (element instanceof IAdaptable)
    {
      Object adapter = ((IAdaptable)element).getAdapter(IFile.class);
      return getFile(adapter);
    }

    return null;
  }
}
