/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.part;

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttribute2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttributeEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAAttributeCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAOperationClassCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassNameEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAAttributeInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAOperationInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceNameEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperation2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperationEditPart;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;

/**
 * This registry is used to determine which type of visual object should be created for the corresponding Diagram, Node,
 * ChildNode or Link represented by a domain model object.
 * 
 * @generated
 */
public class AcoreVisualIDRegistry
{

  /**
   * @generated
   */
  private static final String DEBUG_KEY = "org.eclipse.emf.cdo.dawn.examples.acore.diagram/debug/visualID"; //$NON-NLS-1$

  /**
   * @generated
   */
  public static int getVisualID(View view)
  {
    if (view instanceof Diagram)
    {
      if (ACoreRootEditPart.MODEL_ID.equals(view.getType()))
      {
        return ACoreRootEditPart.VISUAL_ID;
      }
      else
      {
        return -1;
      }
    }
    return org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry.getVisualID(view.getType());
  }

  /**
   * @generated
   */
  public static String getModelID(View view)
  {
    View diagram = view.getDiagram();
    while (view != diagram)
    {
      EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
      if (annotation != null)
      {
        return (String)annotation.getDetails().get("modelID"); //$NON-NLS-1$
      }
      view = (View)view.eContainer();
    }
    return diagram != null ? diagram.getType() : null;
  }

  /**
   * @generated
   */
  public static int getVisualID(String type)
  {
    try
    {
      return Integer.parseInt(type);
    }
    catch (NumberFormatException e)
    {
      if (Boolean.TRUE.toString().equalsIgnoreCase(Platform.getDebugOption(DEBUG_KEY)))
      {
        AcoreDiagramEditorPlugin.getInstance().logError("Unable to parse view type as a visualID number: " + type);
      }
    }
    return -1;
  }

  /**
   * @generated
   */
  public static String getType(int visualID)
  {
    return String.valueOf(visualID);
  }

  /**
   * @generated
   */
  public static int getDiagramVisualID(EObject domainElement)
  {
    if (domainElement == null)
    {
      return -1;
    }
    if (AcorePackage.eINSTANCE.getACoreRoot().isSuperTypeOf(domainElement.eClass())
        && isDiagram((ACoreRoot)domainElement))
    {
      return ACoreRootEditPart.VISUAL_ID;
    }
    return -1;
  }

  /**
   * @generated
   */
  public static int getNodeVisualID(View containerView, EObject domainElement)
  {
    if (domainElement == null)
    {
      return -1;
    }
    String containerModelID = org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry
        .getModelID(containerView);
    if (!ACoreRootEditPart.MODEL_ID.equals(containerModelID))
    {
      return -1;
    }
    int containerVisualID;
    if (ACoreRootEditPart.MODEL_ID.equals(containerModelID))
    {
      containerVisualID = org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry
          .getVisualID(containerView);
    }
    else
    {
      if (containerView instanceof Diagram)
      {
        containerVisualID = ACoreRootEditPart.VISUAL_ID;
      }
      else
      {
        return -1;
      }
    }
    switch (containerVisualID)
    {
    case ACoreRootEditPart.VISUAL_ID:
      if (AcorePackage.eINSTANCE.getAInterface().isSuperTypeOf(domainElement.eClass()))
      {
        return AInterfaceEditPart.VISUAL_ID;
      }
      if (AcorePackage.eINSTANCE.getAClass().isSuperTypeOf(domainElement.eClass()))
      {
        return AClassEditPart.VISUAL_ID;
      }
      break;
    case AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID:
      if (AcorePackage.eINSTANCE.getAAttribute().isSuperTypeOf(domainElement.eClass()))
      {
        return AAttributeEditPart.VISUAL_ID;
      }
      break;
    case AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID:
      if (AcorePackage.eINSTANCE.getAOperation().isSuperTypeOf(domainElement.eClass()))
      {
        return AOperationEditPart.VISUAL_ID;
      }
      break;
    case AClassAAttributeCompartmentEditPart.VISUAL_ID:
      if (AcorePackage.eINSTANCE.getAAttribute().isSuperTypeOf(domainElement.eClass()))
      {
        return AAttribute2EditPart.VISUAL_ID;
      }
      break;
    case AClassAOperationClassCompartmentEditPart.VISUAL_ID:
      if (AcorePackage.eINSTANCE.getAOperation().isSuperTypeOf(domainElement.eClass()))
      {
        return AOperation2EditPart.VISUAL_ID;
      }
      break;
    }
    return -1;
  }

  /**
   * @generated
   */
  public static boolean canCreateNode(View containerView, int nodeVisualID)
  {
    String containerModelID = org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry
        .getModelID(containerView);
    if (!ACoreRootEditPart.MODEL_ID.equals(containerModelID))
    {
      return false;
    }
    int containerVisualID;
    if (ACoreRootEditPart.MODEL_ID.equals(containerModelID))
    {
      containerVisualID = org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry
          .getVisualID(containerView);
    }
    else
    {
      if (containerView instanceof Diagram)
      {
        containerVisualID = ACoreRootEditPart.VISUAL_ID;
      }
      else
      {
        return false;
      }
    }
    switch (containerVisualID)
    {
    case ACoreRootEditPart.VISUAL_ID:
      if (AInterfaceEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      if (AClassEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    case AInterfaceEditPart.VISUAL_ID:
      if (AInterfaceNameEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      if (AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      if (AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    case AClassEditPart.VISUAL_ID:
      if (AClassNameEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      if (AClassAAttributeCompartmentEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      if (AClassAOperationClassCompartmentEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    case AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID:
      if (AAttributeEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    case AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID:
      if (AOperationEditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    case AClassAAttributeCompartmentEditPart.VISUAL_ID:
      if (AAttribute2EditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    case AClassAOperationClassCompartmentEditPart.VISUAL_ID:
      if (AOperation2EditPart.VISUAL_ID == nodeVisualID)
      {
        return true;
      }
      break;
    }
    return false;
  }

  /**
   * @generated
   */
  public static int getLinkWithClassVisualID(EObject domainElement)
  {
    if (domainElement == null)
    {
      return -1;
    }
    return -1;
  }

  /**
   * User can change implementation of this method to handle some specific situations not covered by default logic.
   * 
   * @generated
   */
  private static boolean isDiagram(ACoreRoot element)
  {
    return true;
  }

}
