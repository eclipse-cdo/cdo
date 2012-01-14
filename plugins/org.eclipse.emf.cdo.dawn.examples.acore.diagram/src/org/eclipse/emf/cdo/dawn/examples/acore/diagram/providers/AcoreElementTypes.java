/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers;

import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttribute2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttributeEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAggregationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAssociationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassCompositionsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassSubClassesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperation2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperationEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
public class AcoreElementTypes extends ElementInitializers
{

  /**
   * @generated
   */
  private AcoreElementTypes()
  {
  }

  /**
   * @generated
   */
  private static Map<IElementType, ENamedElement> elements;

  /**
   * @generated
   */
  private static ImageRegistry imageRegistry;

  /**
   * @generated
   */
  private static Set<IElementType> KNOWN_ELEMENT_TYPES;

  /**
   * @generated
   */
  public static final IElementType ACoreRoot_1000 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.ACoreRoot_1000"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AInterface_2001 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AInterface_2001"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AClass_2002 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AClass_2002"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AAttribute_3001 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AAttribute_3001"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AOperation_3002 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AOperation_3002"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AAttribute_3003 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AAttribute_3003"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AOperation_3004 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AOperation_3004"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AClassSubClasses_4001 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AClassSubClasses_4001"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AClassImplementedInterfaces_4002 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AClassImplementedInterfaces_4002"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AClassAssociations_4003 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AClassAssociations_4003"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AClassAggregations_4004 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AClassAggregations_4004"); //$NON-NLS-1$

  /**
   * @generated
   */
  public static final IElementType AClassCompositions_4005 = getElementType("org.eclipse.emf.cdo.dawn.examples.acore.diagram.AClassCompositions_4005"); //$NON-NLS-1$

  /**
   * @generated
   */
  private static ImageRegistry getImageRegistry()
  {
    if (imageRegistry == null)
    {
      imageRegistry = new ImageRegistry();
    }
    return imageRegistry;
  }

  /**
   * @generated
   */
  private static String getImageRegistryKey(ENamedElement element)
  {
    return element.getName();
  }

  /**
   * @generated
   */
  private static ImageDescriptor getProvidedImageDescriptor(ENamedElement element)
  {
    if (element instanceof EStructuralFeature)
    {
      EStructuralFeature feature = ((EStructuralFeature)element);
      EClass eContainingClass = feature.getEContainingClass();
      EClassifier eType = feature.getEType();
      if (eContainingClass != null && !eContainingClass.isAbstract())
      {
        element = eContainingClass;
      }
      else if (eType instanceof EClass && !((EClass)eType).isAbstract())
      {
        element = eType;
      }
    }
    if (element instanceof EClass)
    {
      EClass eClass = (EClass)element;
      if (!eClass.isAbstract())
      {
        return AcoreDiagramEditorPlugin.getInstance().getItemImageDescriptor(
            eClass.getEPackage().getEFactoryInstance().create(eClass));
      }
    }
    // TODO : support structural features
    return null;
  }

  /**
   * @generated
   */
  public static ImageDescriptor getImageDescriptor(ENamedElement element)
  {
    String key = getImageRegistryKey(element);
    ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(key);
    if (imageDescriptor == null)
    {
      imageDescriptor = getProvidedImageDescriptor(element);
      if (imageDescriptor == null)
      {
        imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
      }
      getImageRegistry().put(key, imageDescriptor);
    }
    return imageDescriptor;
  }

  /**
   * @generated
   */
  public static Image getImage(ENamedElement element)
  {
    String key = getImageRegistryKey(element);
    Image image = getImageRegistry().get(key);
    if (image == null)
    {
      ImageDescriptor imageDescriptor = getProvidedImageDescriptor(element);
      if (imageDescriptor == null)
      {
        imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
      }
      getImageRegistry().put(key, imageDescriptor);
      image = getImageRegistry().get(key);
    }
    return image;
  }

  /**
   * @generated
   */
  public static ImageDescriptor getImageDescriptor(IAdaptable hint)
  {
    ENamedElement element = getElement(hint);
    if (element == null)
    {
      return null;
    }
    return getImageDescriptor(element);
  }

  /**
   * @generated
   */
  public static Image getImage(IAdaptable hint)
  {
    ENamedElement element = getElement(hint);
    if (element == null)
    {
      return null;
    }
    return getImage(element);
  }

  /**
   * Returns 'type' of the ecore object associated with the hint.
   * 
   * @generated
   */
  public static ENamedElement getElement(IAdaptable hint)
  {
    Object type = hint.getAdapter(IElementType.class);
    if (elements == null)
    {
      elements = new IdentityHashMap<IElementType, ENamedElement>();

      elements.put(ACoreRoot_1000, AcorePackage.eINSTANCE.getACoreRoot());

      elements.put(AInterface_2001, AcorePackage.eINSTANCE.getAInterface());

      elements.put(AClass_2002, AcorePackage.eINSTANCE.getAClass());

      elements.put(AAttribute_3001, AcorePackage.eINSTANCE.getAAttribute());

      elements.put(AOperation_3002, AcorePackage.eINSTANCE.getAOperation());

      elements.put(AAttribute_3003, AcorePackage.eINSTANCE.getAAttribute());

      elements.put(AOperation_3004, AcorePackage.eINSTANCE.getAOperation());

      elements.put(AClassSubClasses_4001, AcorePackage.eINSTANCE.getAClass_SubClasses());

      elements.put(AClassImplementedInterfaces_4002, AcorePackage.eINSTANCE.getAClass_ImplementedInterfaces());

      elements.put(AClassAssociations_4003, AcorePackage.eINSTANCE.getAClass_Associations());

      elements.put(AClassAggregations_4004, AcorePackage.eINSTANCE.getAClass_Aggregations());

      elements.put(AClassCompositions_4005, AcorePackage.eINSTANCE.getAClass_Compositions());
    }
    return (ENamedElement)elements.get(type);
  }

  /**
   * @generated
   */
  private static IElementType getElementType(String id)
  {
    return ElementTypeRegistry.getInstance().getType(id);
  }

  /**
   * @generated
   */
  public static boolean isKnownElementType(IElementType elementType)
  {
    if (KNOWN_ELEMENT_TYPES == null)
    {
      KNOWN_ELEMENT_TYPES = new HashSet<IElementType>();
      KNOWN_ELEMENT_TYPES.add(ACoreRoot_1000);
      KNOWN_ELEMENT_TYPES.add(AInterface_2001);
      KNOWN_ELEMENT_TYPES.add(AClass_2002);
      KNOWN_ELEMENT_TYPES.add(AAttribute_3001);
      KNOWN_ELEMENT_TYPES.add(AOperation_3002);
      KNOWN_ELEMENT_TYPES.add(AAttribute_3003);
      KNOWN_ELEMENT_TYPES.add(AOperation_3004);
      KNOWN_ELEMENT_TYPES.add(AClassSubClasses_4001);
      KNOWN_ELEMENT_TYPES.add(AClassImplementedInterfaces_4002);
      KNOWN_ELEMENT_TYPES.add(AClassAssociations_4003);
      KNOWN_ELEMENT_TYPES.add(AClassAggregations_4004);
      KNOWN_ELEMENT_TYPES.add(AClassCompositions_4005);
    }
    return KNOWN_ELEMENT_TYPES.contains(elementType);
  }

  /**
   * @generated
   */
  public static IElementType getElementType(int visualID)
  {
    switch (visualID)
    {
    case ACoreRootEditPart.VISUAL_ID:
      return ACoreRoot_1000;
    case AInterfaceEditPart.VISUAL_ID:
      return AInterface_2001;
    case AClassEditPart.VISUAL_ID:
      return AClass_2002;
    case AAttributeEditPart.VISUAL_ID:
      return AAttribute_3001;
    case AOperationEditPart.VISUAL_ID:
      return AOperation_3002;
    case AAttribute2EditPart.VISUAL_ID:
      return AAttribute_3003;
    case AOperation2EditPart.VISUAL_ID:
      return AOperation_3004;
    case AClassSubClassesEditPart.VISUAL_ID:
      return AClassSubClasses_4001;
    case AClassImplementedInterfacesEditPart.VISUAL_ID:
      return AClassImplementedInterfaces_4002;
    case AClassAssociationsEditPart.VISUAL_ID:
      return AClassAssociations_4003;
    case AClassAggregationsEditPart.VISUAL_ID:
      return AClassAggregations_4004;
    case AClassCompositionsEditPart.VISUAL_ID:
      return AClassCompositions_4005;
    }
    return null;
  }

}
