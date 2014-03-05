/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation.actions;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ConfirmationDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressDialog;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil.UpdatingException;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.KeyBindingTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class PerformSetupAction extends AbstractSetupAction
{
  private static final boolean TEST_SINGLE_TASK = false;

  public PerformSetupAction()
  {
  }

  public void run(IAction action)
  {
    try
    {
      final SetupTaskPerformer setupTaskPerformer = new SetupTaskPerformer(true);
      Setup setup = setupTaskPerformer.getSetup();

      if (TEST_SINGLE_TASK)
      {
        SetupTask task = createTestTask();
        if (task.isNeeded(setupTaskPerformer))
        {
          task.perform(setupTaskPerformer);
        }

        return;
      }

      MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, 0, "Resource load errors", null);

      Branch branch = setup.getBranch();
      if (branch.eIsProxy())
      {
        status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Branch cannot be resolved: "
            + EcoreUtil.getURI(branch)));
      }

      Eclipse eclipse = setup.getEclipseVersion();
      if (eclipse.eIsProxy())
      {
        status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Eclipse version cannot be resolved: "
            + EcoreUtil.getURI(eclipse)));
      }

      Map<URI, List<String>> brokenProxies = new LinkedHashMap<URI, List<String>>();
      for (Iterator<EObject> it = EcoreUtil.getAllContents(setupTaskPerformer.getTriggeredSetupTasks()); it.hasNext();)
      {
        EObject referencingEObject = it.next();
        for (EContentsEList.FeatureIterator<EObject> it2 = (FeatureIterator<EObject>)referencingEObject
            .eCrossReferences().iterator(); it2.hasNext();)
        {
          EObject referencedEObject = it2.next();
          if (referencedEObject.eIsProxy())
          {
            URI proxyURI = ((InternalEObject)referencedEObject).eProxyURI();
            List<String> messages = brokenProxies.get(proxyURI);
            if (messages == null)
            {
              messages = new ArrayList<String>();
              brokenProxies.put(proxyURI, messages);
            }

            messages.add("The object " + EcoreUtil.getURI(referencingEObject) + " has a feature '"
                + it2.feature().getName() + "' with an unresolved proxy: ");
          }
        }
      }

      Set<Resource> brokenResources = new HashSet<Resource>();
      ResourceSet resourceSet = setupTaskPerformer.getResourceSet();
      for (Map.Entry<URI, List<String>> entry : brokenProxies.entrySet())
      {
        URI uri = entry.getKey();
        Resource resource = resourceSet.getResource(uri.trimFragment(), false);
        if (resource != null && brokenResources.add(resource))
        {
          for (Resource.Diagnostic diagnostic : resource.getErrors())
          {
            String message = diagnostic.getMessage();
            if (diagnostic instanceof Throwable)
            {
              status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, (Throwable)diagnostic));
            }
            else
            {
              status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
            }
          }
        }

        for (String message : entry.getValue())
        {
          status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message + uri));
        }
      }

      if (!status.isOK())
      {
        CoreException exception = new CoreException(status);
        exception.setStackTrace(new StackTraceElement[0]);
        throw exception;
      }

      EList<SetupTask> neededSetupTasks = setupTaskPerformer.initNeededSetupTasks();

      ConfirmationDialog dialog = new ConfirmationDialog(UIUtil.getShell(), setup, neededSetupTasks);
      if (dialog.open() == ConfirmationDialog.OK)
      {
        Shell shell = getWindow().getShell();
        ProgressDialog.run(shell, new ProgressLogRunnable()
        {
          public Set<String> run(ProgressLog log) throws Exception
          {
            setupTaskPerformer.performNeededSetupTasks();
            return setupTaskPerformer.getRestartReasons();
          }
        }, Collections.singletonList(setupTaskPerformer));
      }
    }
    catch (UpdatingException ex)
    {
      PlatformUI.getWorkbench().restart();
    }
    catch (Throwable ex)
    {
      SetupEditorPlugin.INSTANCE.log(ex);
      ErrorDialog.open(ex);
    }
  }

  private SetupTask createTestTask()
  {
    KeyBindingTask task = SetupFactory.eINSTANCE.createKeyBindingTask();
    task.setKeys("F12");
    task.setCommand("org.eclipse.emf.cdo.releng.OpenManifest");

    // FileAssociationTask task = SetupFactory.eINSTANCE.createFileAssociationTask();
    // task.setFilePattern(".project");
    // task.setDefaultEditorID("com.objfac.xmleditor.XMLEditor");

    return task;
  }
}
