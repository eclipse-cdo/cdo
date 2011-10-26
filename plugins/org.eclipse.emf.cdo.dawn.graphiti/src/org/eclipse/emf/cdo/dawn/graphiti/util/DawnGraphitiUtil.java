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
package org.eclipse.emf.cdo.dawn.graphiti.util;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiUtil
{
  /**
   * This method tries to find an editpart for a given pictogram element. It recursivly searches all children for the
   * given editpart if the model matches to pictogramElement.
   */
  public static EditPart getEditpart(PictogramElement pictogramElement, EditPart part)
  {
    for (Object object : part.getChildren())
    {
      EditPart child = (EditPart)object;
      if (child.getModel().equals(pictogramElement))
      {
        return child;
      }
      return getEditpart(pictogramElement, child);
    }
    return null;
  }

  /**
   * Tries to retriev the pictogram element from a given element. If the element itself is a PictogramElement, the
   * element will be returned. Otherwise all eContainers will be checked until a PictogramElement ist found.
   */
  public static PictogramElement getPictgramElement(EObject element)
  {
    if (element == null)
    {
      return null;
    }

    if (element instanceof PictogramElement)
    {
      return (PictogramElement)element;
    }

    EObject eContainer = element.eContainer();

    if (eContainer instanceof PictogramElement)
    {
      return (PictogramElement)eContainer;
    }
    return getPictgramElement(eContainer);
  }
}
