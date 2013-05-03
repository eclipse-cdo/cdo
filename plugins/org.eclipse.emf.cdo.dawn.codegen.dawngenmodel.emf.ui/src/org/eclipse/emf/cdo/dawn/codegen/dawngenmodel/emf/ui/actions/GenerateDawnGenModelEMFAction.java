/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.ui.actions;

import org.eclipse.emf.cdo.dawn.codegen.actions.GenerateDawnGenModelAction;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelFactory;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.resources.IFile;

/**
 * @author Martin Fluegge
 */
public class GenerateDawnGenModelEMFAction extends GenerateDawnGenModelAction
{
  protected final String emfGenmodelFileExtension = "emf";

  @Override
  protected DawnFragmentGenerator getDawnFragmentGenerator(IFile genFile, ResourceSet resourceSet)
  {
    String genModelFile = genFile.getRawLocationURI().toString();
    DawnEMFGenerator dawnEMFGenerator = DawnEmfGenmodelFactory.eINSTANCE.createDawnEMFGenerator();

    URI emfGenModelResourceUri = URI.createURI(genModelFile);
    Resource emfGenModelResource = resourceSet.getResource(emfGenModelResourceUri, true);

    GenModel genModel = (GenModel)emfGenModelResource.getContents().get(0);

    dawnEMFGenerator.setDawnEditorClassName("Dawn" + genModel.getModelName() + "Editor");
    dawnEMFGenerator.setFragmentName(genModel.getEditorPluginID() + ".dawn");
    dawnEMFGenerator.setEmfGenModel(genModel);
    return dawnEMFGenerator;
  }

  @Override
  protected ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = super.createResourceSet();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
    return resourceSet;
  }

  @Override
  protected Resource getDawnFragmentModelResource(String path, String modelname, ResourceSet resourceSet)
  {
    return getResource(path, modelname, resourceSet, dawngenmodelFileExtension + "_" + emfGenmodelFileExtension);
  }
}
