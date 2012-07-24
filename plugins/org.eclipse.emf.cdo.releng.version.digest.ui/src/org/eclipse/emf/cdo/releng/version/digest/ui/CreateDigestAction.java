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
package org.eclipse.emf.cdo.releng.version.digest.ui;

import org.eclipse.emf.cdo.releng.version.Element;
import org.eclipse.emf.cdo.releng.version.Release;
import org.eclipse.emf.cdo.releng.version.ReleaseManager;
import org.eclipse.emf.cdo.releng.version.VersionBuilder;
import org.eclipse.emf.cdo.releng.version.digest.DigestValidator;
import org.eclipse.emf.cdo.releng.version.digest.DigestValidatorState;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.core.IModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import org.osgi.framework.Version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CreateDigestAction implements IObjectActionDelegate
{
  private Shell shell;

  private ISelection selection;

  public CreateDigestAction()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  public void run(IAction action)
  {
    try
    {
      final IFile file = (IFile)((IStructuredSelection)selection).getFirstElement();
      final Release release = ReleaseManager.INSTANCE.getRelease(file);
      final IFile target = DigestValidator.getDigestFile(file.getFullPath());

      new Job("Create digest")
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          try
          {
            List<String> warnings = createDigest(release, target, monitor);
            if (!warnings.isEmpty())
            {
              final StringBuilder builder = new StringBuilder("The following problems occured:\n");
              for (String warning : warnings)
              {
                builder.append("\n");
                builder.append(warning);
              }

              shell.getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  MessageDialog.openWarning(shell, "CDO Release Engineering Version Tool", builder.toString());
                }
              });
            }

            return Status.OK_STATUS;
          }
          catch (CoreException ex)
          {
            return ex.getStatus();
          }
        }
      }.schedule();
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      MessageDialog.openError(shell, "CDO Release Engineering Version Tool",
          "An error occured. Consult the error log for details.");
    }
  }

  private List<String> createDigest(Release release, IFile target, IProgressMonitor monitor) throws CoreException
  {
    monitor.beginTask(null, release.getSize() + 1);
    List<String> warnings = new ArrayList<String>();

    try
    {
      Map<String, byte[]> result = new HashMap<String, byte[]>();
      for (Entry<Element, Element> entry : release.getElements().entrySet())
      {
        String name = entry.getKey().getName();
        monitor.subTask(name);

        try
        {
          try
          {
            Element element = entry.getValue();
            if (element.getName().endsWith(".source"))
            {
              continue;
            }

            IModel componentModel = ReleaseManager.INSTANCE.getComponentModel(element);
            if (componentModel == null)
            {
              warnings.add(name + ": Component not found");
              continue;
            }

            IResource resource = componentModel.getUnderlyingResource();
            if (resource == null)
            {
              warnings.add(name + ": Component is not in workspace");
              continue;
            }

            Version version = VersionBuilder.getComponentVersion(componentModel);

            if (!element.getVersion().equals(version))
            {
              warnings.add(name + ": Plugin version is not " + element.getVersion());
            }

            // TODO Determine validator class from .project
            DigestValidator validator = new DigestValidator.BuildModel();
            validator.beforeValidation(null, componentModel);
            DigestValidatorState state = validator.validateFull(resource.getProject(), null, componentModel,
                new NullProgressMonitor());
            validator.afterValidation(state);
            result.put(state.getName(), state.getDigest());
          }
          finally
          {
            monitor.worked(1);
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          warnings.add(name + ": " + Activator.getStatus(ex).getMessage());
        }
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(result);
      oos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      if (target.exists())
      {
        int i = 1;
        for (;;)
        {
          try
          {
            target.move(target.getFullPath().addFileExtension("bak" + i), true, monitor);
            break;
          }
          catch (Exception ex)
          {
            ++i;
          }
        }
      }

      target.create(bais, true, monitor);
      monitor.worked(1);
    }
    catch (CoreException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CoreException(Activator.getStatus(ex));
    }
    finally
    {
      monitor.done();
    }

    return warnings;
  }
}
