/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.actions;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGMFGenerator;
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
import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;
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
public class GenerateDawnGenModelAction implements IObjectActionDelegate
{

  private IResource selectedElement;

  public static final String dawngenmodelFileExtension = "dawngenmodel";

  private final String generalPrefix = "Dawn";

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
          IFile gmfGen = (IFile)selectedElement;

          String gmfGenModelFile = gmfGen.getRawLocationURI().toString();
          String path = gmfGenModelFile.replace(gmfGen.getName(), "");
          String modelname = gmfGen.getName().replace(".gmfgen", "");

          ResourceSet resourceSet = new ResourceSetImpl();
          resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("gmfgen",
              new XMIResourceFactoryImpl());
          resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel",
              new XMIResourceFactoryImpl());
          resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(dawngenmodelFileExtension,
              new XMIResourceFactoryImpl());

          URI gmfGenModelResourceUri = URI.createURI(gmfGenModelFile);
          Resource gmfGenModelResource = resourceSet.getResource(gmfGenModelResourceUri, true);

          String dawnGenModelResourcePath = path + "" + modelname + "." + dawngenmodelFileExtension;
          Resource dawnGenModelResource = resourceSet.createResource(URI.createURI(dawnGenModelResourcePath));

          GenEditorGenerator editorGenerator = (GenEditorGenerator)gmfGenModelResource.getContents().get(0);

          DawnGMFGenerator dawnGMFGenerator = DawngenmodelFactory.eINSTANCE.createDawnGMFGenerator();

          String dawnEditorClassName = getDawnEditorClassName(editorGenerator);

          dawnGMFGenerator.setDawnEditorClassName(dawnEditorClassName);
          dawnGMFGenerator.setFragmentName(editorGenerator.getPlugin().getID() + ".dawn");
          dawnGMFGenerator.setDawnCanonicalEditingPolicyClassName(generalPrefix
              + editorGenerator.getDiagram().getCanonicalEditPolicyClassName());
          dawnGMFGenerator.setDawnCreationWizardClassName(generalPrefix
              + editorGenerator.getDiagram().getCreationWizardClassName());
          dawnGMFGenerator.setDawnDiagramEditPartClassName(generalPrefix
              + editorGenerator.getDiagram().getEditPartClassName());
          dawnGMFGenerator.setDawnDocumentProviderClassName(generalPrefix
              + editorGenerator.getDiagram().getDocumentProviderClassName());
          dawnGMFGenerator.setDawnEditorUtilClassName(generalPrefix
              + editorGenerator.getDiagram().getDiagramEditorUtilClassName());
          dawnGMFGenerator.setDawnEditPartFactoryClassName(generalPrefix
              + editorGenerator.getDiagram().getEditPartFactoryClassName());
          dawnGMFGenerator.setDawnEditPartProviderClassName(generalPrefix
              + editorGenerator.getDiagram().getEditPartProviderClassName());
          dawnGMFGenerator.setDawnEditPolicyProviderClassName(generalPrefix
              + editorGenerator.getDiagram().getEditPartProviderClassName().replace("EditPart", "EditPolicy"));

          dawnGMFGenerator.setGMFGenEditorGenerator(editorGenerator);

          dawnGenModelResource.getContents().add(dawnGMFGenerator);

          try
          {
            dawnGenModelResource.save(Collections.EMPTY_MAP);
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }

          ProjectCreationHelper.refreshProject(null, monitor);
        }
      });
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  private String getDawnEditorClassName(GenEditorGenerator editorGenerator)
  {
    String dawnEditorClassName = editorGenerator.getEditor().getClassName();
    if (dawnEditorClassName == null || dawnEditorClassName.equals(""))
    {
      dawnEditorClassName = "Dawn" + editorGenerator.getDomainGenModel() + "DiagramEditor";
    }
    dawnEditorClassName = generalPrefix + dawnEditorClassName;
    return dawnEditorClassName;
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
