/***************************************************************************
 * Copyright (c) 2008 Open Canarias S.L. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/

package org.eclipse.emf.cdo.ui.cdouidefs.editorlauncher;

import org.eclipse.emf.cdo.ui.cdouidefs.EditorDef;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;

@SuppressWarnings("restriction")
public class EditorLauncher implements IEditorLauncher
{

  public void open(IPath file)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    URI resourceURI = URI.createFileURI(file.toString());
    Resource ecoreResource = resourceSet.getResource(resourceURI, true);
    EObject eObject = ecoreResource.getContents().get(0);
    if (eObject instanceof EditorDef)
    {
      EditorDef editorDef = ((EditorDef) eObject);
      editorDef.getInstance();
    }
  }
}
