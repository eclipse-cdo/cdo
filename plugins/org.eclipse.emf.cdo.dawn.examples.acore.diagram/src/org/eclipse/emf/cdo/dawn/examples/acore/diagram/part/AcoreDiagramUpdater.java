/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttribute2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttributeEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAAttributeCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAOperationClassCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAggregationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAssociationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassCompositionsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassSubClassesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAAttributeInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAOperationInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperation2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperationEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.gmf.runtime.notation.View;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @generated
 */
public class AcoreDiagramUpdater
{

  /**
   * @generated
   */
  public static List<AcoreNodeDescriptor> getSemanticChildren(View view)
  {
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case ACoreRootEditPart.VISUAL_ID:
      return getACoreRoot_1000SemanticChildren(view);
    case AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID:
      return getAInterfaceAAttributeInterfaceCompartment_7001SemanticChildren(view);
    case AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID:
      return getAInterfaceAOperationInterfaceCompartment_7002SemanticChildren(view);
    case AClassAAttributeCompartmentEditPart.VISUAL_ID:
      return getAClassAAttributeCompartment_7003SemanticChildren(view);
    case AClassAOperationClassCompartmentEditPart.VISUAL_ID:
      return getAClassAOperationClassCompartment_7004SemanticChildren(view);
    }
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreNodeDescriptor> getACoreRoot_1000SemanticChildren(View view)
  {
    if (!view.isSetElement())
    {
      return Collections.emptyList();
    }
    ACoreRoot modelElement = (ACoreRoot)view.getElement();
    LinkedList<AcoreNodeDescriptor> result = new LinkedList<AcoreNodeDescriptor>();
    for (Iterator it = modelElement.getInterfaces().iterator(); it.hasNext();)
    {
      AInterface childElement = (AInterface)it.next();
      int visualID = AcoreVisualIDRegistry.getNodeVisualID(view, childElement);
      if (visualID == AInterfaceEditPart.VISUAL_ID)
      {
        result.add(new AcoreNodeDescriptor(childElement, visualID));
        continue;
      }
    }
    for (Iterator it = modelElement.getClasses().iterator(); it.hasNext();)
    {
      AClass childElement = (AClass)it.next();
      int visualID = AcoreVisualIDRegistry.getNodeVisualID(view, childElement);
      if (visualID == AClassEditPart.VISUAL_ID)
      {
        result.add(new AcoreNodeDescriptor(childElement, visualID));
        continue;
      }
    }
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreNodeDescriptor> getAInterfaceAAttributeInterfaceCompartment_7001SemanticChildren(View view)
  {
    if (false == view.eContainer() instanceof View)
    {
      return Collections.emptyList();
    }
    View containerView = (View)view.eContainer();
    if (!containerView.isSetElement())
    {
      return Collections.emptyList();
    }
    AInterface modelElement = (AInterface)containerView.getElement();
    LinkedList<AcoreNodeDescriptor> result = new LinkedList<AcoreNodeDescriptor>();
    for (Iterator it = modelElement.getAttributes().iterator(); it.hasNext();)
    {
      AAttribute childElement = (AAttribute)it.next();
      int visualID = AcoreVisualIDRegistry.getNodeVisualID(view, childElement);
      if (visualID == AAttributeEditPart.VISUAL_ID)
      {
        result.add(new AcoreNodeDescriptor(childElement, visualID));
        continue;
      }
    }
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreNodeDescriptor> getAInterfaceAOperationInterfaceCompartment_7002SemanticChildren(View view)
  {
    if (false == view.eContainer() instanceof View)
    {
      return Collections.emptyList();
    }
    View containerView = (View)view.eContainer();
    if (!containerView.isSetElement())
    {
      return Collections.emptyList();
    }
    AInterface modelElement = (AInterface)containerView.getElement();
    LinkedList<AcoreNodeDescriptor> result = new LinkedList<AcoreNodeDescriptor>();
    for (Iterator it = modelElement.getOperations().iterator(); it.hasNext();)
    {
      AOperation childElement = (AOperation)it.next();
      int visualID = AcoreVisualIDRegistry.getNodeVisualID(view, childElement);
      if (visualID == AOperationEditPart.VISUAL_ID)
      {
        result.add(new AcoreNodeDescriptor(childElement, visualID));
        continue;
      }
    }
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreNodeDescriptor> getAClassAAttributeCompartment_7003SemanticChildren(View view)
  {
    if (false == view.eContainer() instanceof View)
    {
      return Collections.emptyList();
    }
    View containerView = (View)view.eContainer();
    if (!containerView.isSetElement())
    {
      return Collections.emptyList();
    }
    AClass modelElement = (AClass)containerView.getElement();
    LinkedList<AcoreNodeDescriptor> result = new LinkedList<AcoreNodeDescriptor>();
    for (Iterator it = modelElement.getAttributes().iterator(); it.hasNext();)
    {
      AAttribute childElement = (AAttribute)it.next();
      int visualID = AcoreVisualIDRegistry.getNodeVisualID(view, childElement);
      if (visualID == AAttribute2EditPart.VISUAL_ID)
      {
        result.add(new AcoreNodeDescriptor(childElement, visualID));
        continue;
      }
    }
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreNodeDescriptor> getAClassAOperationClassCompartment_7004SemanticChildren(View view)
  {
    if (false == view.eContainer() instanceof View)
    {
      return Collections.emptyList();
    }
    View containerView = (View)view.eContainer();
    if (!containerView.isSetElement())
    {
      return Collections.emptyList();
    }
    AClass modelElement = (AClass)containerView.getElement();
    LinkedList<AcoreNodeDescriptor> result = new LinkedList<AcoreNodeDescriptor>();
    for (Iterator it = modelElement.getOperations().iterator(); it.hasNext();)
    {
      AOperation childElement = (AOperation)it.next();
      int visualID = AcoreVisualIDRegistry.getNodeVisualID(view, childElement);
      if (visualID == AOperation2EditPart.VISUAL_ID)
      {
        result.add(new AcoreNodeDescriptor(childElement, visualID));
        continue;
      }
    }
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getContainedLinks(View view)
  {
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case ACoreRootEditPart.VISUAL_ID:
      return getACoreRoot_1000ContainedLinks(view);
    case AInterfaceEditPart.VISUAL_ID:
      return getAInterface_2001ContainedLinks(view);
    case AClassEditPart.VISUAL_ID:
      return getAClass_2002ContainedLinks(view);
    case AAttributeEditPart.VISUAL_ID:
      return getAAttribute_3001ContainedLinks(view);
    case AOperationEditPart.VISUAL_ID:
      return getAOperation_3002ContainedLinks(view);
    case AAttribute2EditPart.VISUAL_ID:
      return getAAttribute_3003ContainedLinks(view);
    case AOperation2EditPart.VISUAL_ID:
      return getAOperation_3004ContainedLinks(view);
    }
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getIncomingLinks(View view)
  {
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case AInterfaceEditPart.VISUAL_ID:
      return getAInterface_2001IncomingLinks(view);
    case AClassEditPart.VISUAL_ID:
      return getAClass_2002IncomingLinks(view);
    case AAttributeEditPart.VISUAL_ID:
      return getAAttribute_3001IncomingLinks(view);
    case AOperationEditPart.VISUAL_ID:
      return getAOperation_3002IncomingLinks(view);
    case AAttribute2EditPart.VISUAL_ID:
      return getAAttribute_3003IncomingLinks(view);
    case AOperation2EditPart.VISUAL_ID:
      return getAOperation_3004IncomingLinks(view);
    }
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getOutgoingLinks(View view)
  {
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case AInterfaceEditPart.VISUAL_ID:
      return getAInterface_2001OutgoingLinks(view);
    case AClassEditPart.VISUAL_ID:
      return getAClass_2002OutgoingLinks(view);
    case AAttributeEditPart.VISUAL_ID:
      return getAAttribute_3001OutgoingLinks(view);
    case AOperationEditPart.VISUAL_ID:
      return getAOperation_3002OutgoingLinks(view);
    case AAttribute2EditPart.VISUAL_ID:
      return getAAttribute_3003OutgoingLinks(view);
    case AOperation2EditPart.VISUAL_ID:
      return getAOperation_3004OutgoingLinks(view);
    }
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getACoreRoot_1000ContainedLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAInterface_2001ContainedLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAClass_2002ContainedLinks(View view)
  {
    AClass modelElement = (AClass)view.getElement();
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_SubClasses_4001(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_ImplementedInterfaces_4002(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_Associations_4003(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_Aggregations_4004(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_Compositions_4005(modelElement));
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAAttribute_3001ContainedLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAOperation_3002ContainedLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAAttribute_3003ContainedLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAOperation_3004ContainedLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAInterface_2001IncomingLinks(View view)
  {
    AInterface modelElement = (AInterface)view.getElement();
    Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource().getResourceSet().getResources());
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    result.addAll(getIncomingFeatureModelFacetLinks_AClass_ImplementedInterfaces_4002(modelElement, crossReferences));
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAClass_2002IncomingLinks(View view)
  {
    AClass modelElement = (AClass)view.getElement();
    Map crossReferences = EcoreUtil.CrossReferencer.find(view.eResource().getResourceSet().getResources());
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    result.addAll(getIncomingFeatureModelFacetLinks_AClass_SubClasses_4001(modelElement, crossReferences));
    result.addAll(getIncomingFeatureModelFacetLinks_AClass_Associations_4003(modelElement, crossReferences));
    result.addAll(getIncomingFeatureModelFacetLinks_AClass_Aggregations_4004(modelElement, crossReferences));
    result.addAll(getIncomingFeatureModelFacetLinks_AClass_Compositions_4005(modelElement, crossReferences));
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAAttribute_3001IncomingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAOperation_3002IncomingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAAttribute_3003IncomingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAOperation_3004IncomingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAInterface_2001OutgoingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAClass_2002OutgoingLinks(View view)
  {
    AClass modelElement = (AClass)view.getElement();
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_SubClasses_4001(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_ImplementedInterfaces_4002(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_Associations_4003(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_Aggregations_4004(modelElement));
    result.addAll(getOutgoingFeatureModelFacetLinks_AClass_Compositions_4005(modelElement));
    return result;
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAAttribute_3001OutgoingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAOperation_3002OutgoingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAAttribute_3003OutgoingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  public static List<AcoreLinkDescriptor> getAOperation_3004OutgoingLinks(View view)
  {
    return Collections.emptyList();
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getIncomingFeatureModelFacetLinks_AClass_SubClasses_4001(AClass target, Map crossReferences)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    Collection settings = (Collection)crossReferences.get(target);
    for (Iterator it = settings.iterator(); it.hasNext();)
    {
      EStructuralFeature.Setting setting = (EStructuralFeature.Setting)it.next();
      if (setting.getEStructuralFeature() == AcorePackage.eINSTANCE.getAClass_SubClasses())
      {
        result.add(new AcoreLinkDescriptor(setting.getEObject(), target, AcoreElementTypes.AClassSubClasses_4001, AClassSubClassesEditPart.VISUAL_ID));
      }
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getIncomingFeatureModelFacetLinks_AClass_ImplementedInterfaces_4002(AInterface target, Map crossReferences)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    Collection settings = (Collection)crossReferences.get(target);
    for (Iterator it = settings.iterator(); it.hasNext();)
    {
      EStructuralFeature.Setting setting = (EStructuralFeature.Setting)it.next();
      if (setting.getEStructuralFeature() == AcorePackage.eINSTANCE.getAClass_ImplementedInterfaces())
      {
        result.add(new AcoreLinkDescriptor(setting.getEObject(), target, AcoreElementTypes.AClassImplementedInterfaces_4002,
            AClassImplementedInterfacesEditPart.VISUAL_ID));
      }
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getIncomingFeatureModelFacetLinks_AClass_Associations_4003(AClass target, Map crossReferences)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    Collection settings = (Collection)crossReferences.get(target);
    for (Iterator it = settings.iterator(); it.hasNext();)
    {
      EStructuralFeature.Setting setting = (EStructuralFeature.Setting)it.next();
      if (setting.getEStructuralFeature() == AcorePackage.eINSTANCE.getAClass_Associations())
      {
        result.add(new AcoreLinkDescriptor(setting.getEObject(), target, AcoreElementTypes.AClassAssociations_4003, AClassAssociationsEditPart.VISUAL_ID));
      }
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getIncomingFeatureModelFacetLinks_AClass_Aggregations_4004(AClass target, Map crossReferences)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    Collection settings = (Collection)crossReferences.get(target);
    for (Iterator it = settings.iterator(); it.hasNext();)
    {
      EStructuralFeature.Setting setting = (EStructuralFeature.Setting)it.next();
      if (setting.getEStructuralFeature() == AcorePackage.eINSTANCE.getAClass_Aggregations())
      {
        result.add(new AcoreLinkDescriptor(setting.getEObject(), target, AcoreElementTypes.AClassAggregations_4004, AClassAggregationsEditPart.VISUAL_ID));
      }
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getIncomingFeatureModelFacetLinks_AClass_Compositions_4005(AClass target, Map crossReferences)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    Collection settings = (Collection)crossReferences.get(target);
    for (Iterator it = settings.iterator(); it.hasNext();)
    {
      EStructuralFeature.Setting setting = (EStructuralFeature.Setting)it.next();
      if (setting.getEStructuralFeature() == AcorePackage.eINSTANCE.getAClass_Compositions())
      {
        result.add(new AcoreLinkDescriptor(setting.getEObject(), target, AcoreElementTypes.AClassCompositions_4005, AClassCompositionsEditPart.VISUAL_ID));
      }
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getOutgoingFeatureModelFacetLinks_AClass_SubClasses_4001(AClass source)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    for (Iterator destinations = source.getSubClasses().iterator(); destinations.hasNext();)
    {
      AClass destination = (AClass)destinations.next();
      result.add(new AcoreLinkDescriptor(source, destination, AcoreElementTypes.AClassSubClasses_4001, AClassSubClassesEditPart.VISUAL_ID));
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getOutgoingFeatureModelFacetLinks_AClass_ImplementedInterfaces_4002(AClass source)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    for (Iterator destinations = source.getImplementedInterfaces().iterator(); destinations.hasNext();)
    {
      AInterface destination = (AInterface)destinations.next();
      result
          .add(new AcoreLinkDescriptor(source, destination, AcoreElementTypes.AClassImplementedInterfaces_4002, AClassImplementedInterfacesEditPart.VISUAL_ID));
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getOutgoingFeatureModelFacetLinks_AClass_Associations_4003(AClass source)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    for (Iterator destinations = source.getAssociations().iterator(); destinations.hasNext();)
    {
      AClass destination = (AClass)destinations.next();
      result.add(new AcoreLinkDescriptor(source, destination, AcoreElementTypes.AClassAssociations_4003, AClassAssociationsEditPart.VISUAL_ID));
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getOutgoingFeatureModelFacetLinks_AClass_Aggregations_4004(AClass source)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    for (Iterator destinations = source.getAggregations().iterator(); destinations.hasNext();)
    {
      AClass destination = (AClass)destinations.next();
      result.add(new AcoreLinkDescriptor(source, destination, AcoreElementTypes.AClassAggregations_4004, AClassAggregationsEditPart.VISUAL_ID));
    }
    return result;
  }

  /**
   * @generated
   */
  private static Collection<AcoreLinkDescriptor> getOutgoingFeatureModelFacetLinks_AClass_Compositions_4005(AClass source)
  {
    LinkedList<AcoreLinkDescriptor> result = new LinkedList<AcoreLinkDescriptor>();
    for (Iterator destinations = source.getCompositions().iterator(); destinations.hasNext();)
    {
      AClass destination = (AClass)destinations.next();
      result.add(new AcoreLinkDescriptor(source, destination, AcoreElementTypes.AClassCompositions_4005, AClassCompositionsEditPart.VISUAL_ID));
    }
    return result;
  }

}
