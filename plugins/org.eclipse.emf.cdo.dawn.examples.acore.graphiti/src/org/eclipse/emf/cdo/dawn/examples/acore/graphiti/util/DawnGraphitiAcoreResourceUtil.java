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
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.util;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;

import java.io.IOException;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiAcoreResourceUtil
{
  /**
   * In our example the root of the semantic model is an AcoreRoot. This method ensures that AcoreRoot is wiredn to the
   * Diagram element if needed.
   */
  public static void wireDomainModel(final Diagram diagram, final ResourceSet resourceSet, EditingDomain domain)
  {
    domain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)domain)
    {
      @Override
      protected void doExecute()
      {
        PictogramLink link = diagram.getLink();
        if (link == null)
        {
          link = PictogramsFactory.eINSTANCE.createPictogramLink();
          diagram.setLink(link);
        }

        EList<EObject> businessObjects = link.getBusinessObjects();

        if (businessObjects.size() == 0)
        {
          // create AcoreRoot
          ACoreRoot acoreRoot = AcoreFactory.eINSTANCE.createACoreRoot();
          Resource modelResource = resourceSet.getResources().get(0);
          if (modelResource.equals(diagram.eResource()))
          {
            modelResource = resourceSet.getResources().get(1);
          }
          modelResource.getContents().add(acoreRoot);

          link.getBusinessObjects().add(acoreRoot);

          try
          {
            diagram.eResource().save(null);
            modelResource.save(null);
          }
          catch (IOException ex)
          {
            throw new RuntimeException(ex);
          }
        }
      }
    });
  }

  public static void addToModelResource(EObject element, ResourceSet resourceSet)
  {
    Resource resource = resourceSet.getResources().get(0);

    if (resource.getContents().size() > 0 && resource.getContents().get(0) instanceof Diagram)
    {
      // wrong resource
      resource = resourceSet.getResources().get(1);
    }

    if (resource.getContents().size() > 0)
    {
      ACoreRoot acoreRoot = (ACoreRoot)resource.getContents().get(0);

      addElement(element, acoreRoot);
    }
    // else
    // {
    // ACoreRoot acoreRoot = AcoreFactory.eINSTANCE.createACoreRoot();
    // resource.getContents().add(acoreRoot);
    // addElement(element, acoreRoot);
    // }
  }

  private static void addElement(EObject element, ACoreRoot acoreRoot)
  {
    if (acoreRoot instanceof ACoreRoot)
    {
      if (element instanceof AClass)
      {
        acoreRoot.getClasses().add((AClass)element);
      }
      else if (element instanceof AInterface)
      {
        acoreRoot.getInterfaces().add((AInterface)element);
      }
    }
  }
}
