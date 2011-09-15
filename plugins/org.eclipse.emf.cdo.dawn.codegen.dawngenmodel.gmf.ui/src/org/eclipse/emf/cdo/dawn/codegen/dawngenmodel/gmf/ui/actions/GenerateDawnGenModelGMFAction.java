/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.ui.actions;

import org.eclipse.emf.cdo.dawn.codegen.actions.GenerateDawnGenModelAction;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;

/**
 * @author Martin Fluegge
 */
public class GenerateDawnGenModelGMFAction extends GenerateDawnGenModelAction
{
  protected final String gmfGenmodelFileExtension = "gmf";

  @Override
  protected DawnFragmentGenerator getDawnFragmentGenerator(IFile genFile, ResourceSet resourceSet)
  {
    String gmfGenModelFile = genFile.getRawLocationURI().toString();
    URI gmfGenModelResourceUri = URI.createURI(gmfGenModelFile);
    Resource gmfGenModelResource = resourceSet.getResource(gmfGenModelResourceUri, true);

    GenEditorGenerator editorGenerator = (GenEditorGenerator)gmfGenModelResource.getContents().get(0);

    DawnGMFGenerator dawnGMFGenerator = DawnGmfGenmodelFactory.eINSTANCE.createDawnGMFGenerator();

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
    return dawnGMFGenerator;
  }

  @Override
  protected ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = super.createResourceSet();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
    return resourceSet;
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

  @Override
  protected Resource getDawnFragmentModelResource(String path, String modelname, ResourceSet resourceSet)
  {
    return getResource(path, modelname, resourceSet, dawngenmodelFileExtension + "_" + gmfGenmodelFileExtension);
  }
}
