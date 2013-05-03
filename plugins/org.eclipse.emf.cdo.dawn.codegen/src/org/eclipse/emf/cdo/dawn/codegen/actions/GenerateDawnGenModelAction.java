/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.actions;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelFactory;
import org.eclipse.emf.cdo.dawn.codegen.util.ProjectCreationHelper;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

/**
 * @author Martin Fluegge
 */
public abstract class GenerateDawnGenModelAction implements IObjectActionDelegate
{
  /**
   * @since 1.0
   */
  protected IResource selectedElement;

  public final static String dawngenmodelFileExtension = "dawngenmodel";

  /**
   * @since 1.0
   */
  protected final String generalPrefix = "Dawn";

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void run(IAction action)
  {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    try
    {
      window.run(true, true, new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          monitor.beginTask("Generate Dawn Code", 1000);
          IFile genFile = (IFile)selectedElement;

          String genModelFile = genFile.getRawLocationURI().toString();
          String path = genModelFile.replace(genFile.getName(), "");

          ResourceSet resourceSet = createResourceSet();
          String modelname = "default";
          modelname = getModelName(genFile);

          Resource dawnGenModelResource = getResource(path, modelname, resourceSet, dawngenmodelFileExtension);
          Resource dawnFragmentModelResource = getDawnFragmentModelResource(path, modelname, resourceSet);

          DawnGenerator dawnGenerator = getDawngenerator(dawnGenModelResource);

          DawnFragmentGenerator fragmentGenerator = getDawnFragmentGenerator(genFile, resourceSet);
          fragmentGenerator.setDawnGenerator(dawnGenerator);

          dawnGenModelResource.getContents().add(dawnGenerator);
          dawnFragmentModelResource.getContents().add(fragmentGenerator);

          try
          {
            dawnGenModelResource.save(Collections.EMPTY_MAP);
            dawnFragmentModelResource.save(Collections.EMPTY_MAP);
          }
          catch (IOException ex)
          {
            throw new RuntimeException(ex);
          }

          ProjectCreationHelper.refreshProject(null, monitor);
        }

        private DawnGenerator getDawngenerator(Resource dawnGenModelResource)
        {
          DawnGenerator dawnGenerator;

          if (dawnGenModelResource.getContents().size() != 0)
          {
            dawnGenerator = (DawnGenerator)dawnGenModelResource.getContents().get(0);
          }
          else
          {
            dawnGenerator = DawngenmodelFactory.eINSTANCE.createDawnGenerator();
          }
          return dawnGenerator;
        }

      });
    }
    catch (InvocationTargetException ex)
    {
      throw new RuntimeException(ex);
    }
    catch (InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @since 1.0
   */
  protected abstract Resource getDawnFragmentModelResource(String path, String modelname, ResourceSet resourceSet);

  /**
   * @since 1.0
   */
  protected abstract DawnFragmentGenerator getDawnFragmentGenerator(IFile genFile, ResourceSet resourceSet);

  private String getModelName(IFile genFile)
  {
    int lastIndexOf = genFile.getName().lastIndexOf(".");
    return genFile.getName().substring(0, lastIndexOf);
  }

  /**
   * @since 1.0
   */
  protected Resource getResource(String path, String modelname, ResourceSet resourceSet, String extension)
  {
    String resourcePath = path + "" + modelname + "." + extension;
    URI uri = URI.createURI(resourcePath);
    Resource resource = null;
    try
    {
      resource = resourceSet.getResource(uri, true);
    }
    catch (Exception ignore)
    {
      // ignore
    }

    if (resource == null)
    {
      resource = resourceSet.createResource(uri);
    }
    return resource;
  }

  /**
   * @since 1.0
   */
  protected ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put(dawngenmodelFileExtension, new XMIResourceFactoryImpl());
    return resourceSet;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      Object sel = ((IStructuredSelection)selection).getFirstElement();
      if (sel instanceof IResource)
      {
        selectedElement = (IResource)sel;
      }
    }
  }
}
