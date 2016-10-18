/*
 * Copyright (c) 2010, 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttribute2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttributeEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAAttributeCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAOperationClassCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAggregationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAssociationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassCompositionsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassNameEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassSubClassesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAAttributeInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAOperationInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceNameEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperation2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperationEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.transaction.util.TransactionUtil;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.providers.IViewProvider;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateDiagramViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateEdgeViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateNodeViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateViewForKindOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateViewOperation;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.preferences.IPreferenceConstants;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.gmf.runtime.notation.DecorationNode;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.TitleStyle;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import java.util.ArrayList;

/**
 * @generated
 */
public class AcoreViewProvider extends AbstractProvider implements IViewProvider
{

  /**
   * @generated
   */
  public final boolean provides(IOperation operation)
  {
    if (operation instanceof CreateViewForKindOperation)
    {
      return provides((CreateViewForKindOperation)operation);
    }
    assert operation instanceof CreateViewOperation;
    if (operation instanceof CreateDiagramViewOperation)
    {
      return provides((CreateDiagramViewOperation)operation);
    }
    else if (operation instanceof CreateEdgeViewOperation)
    {
      return provides((CreateEdgeViewOperation)operation);
    }
    else if (operation instanceof CreateNodeViewOperation)
    {
      return provides((CreateNodeViewOperation)operation);
    }
    return false;
  }

  /**
   * @generated
   */
  protected boolean provides(CreateViewForKindOperation op)
  {
    /*
     * if (op.getViewKind() == Node.class) return getNodeViewClass(op.getSemanticAdapter(), op.getContainerView(),
     * op.getSemanticHint()) != null; if (op.getViewKind() == Edge.class) return
     * getEdgeViewClass(op.getSemanticAdapter(), op.getContainerView(), op.getSemanticHint()) != null;
     */
    return true;
  }

  /**
   * @generated
   */
  protected boolean provides(CreateDiagramViewOperation op)
  {
    return ACoreRootEditPart.MODEL_ID.equals(op.getSemanticHint())
        && AcoreVisualIDRegistry.getDiagramVisualID(getSemanticElement(op.getSemanticAdapter())) != -1;
  }

  /**
   * @generated
   */
  protected boolean provides(CreateNodeViewOperation op)
  {
    if (op.getContainerView() == null)
    {
      return false;
    }
    IElementType elementType = getSemanticElementType(op.getSemanticAdapter());
    EObject domainElement = getSemanticElement(op.getSemanticAdapter());
    int visualID;
    if (op.getSemanticHint() == null)
    {
      // Semantic hint is not specified. Can be a result of call from CanonicalEditPolicy.
      // In this situation there should be NO elementType, visualID will be determined
      // by VisualIDRegistry.getNodeVisualID() for domainElement.
      if (elementType != null || domainElement == null)
      {
        return false;
      }
      visualID = AcoreVisualIDRegistry.getNodeVisualID(op.getContainerView(), domainElement);
    }
    else
    {
      visualID = AcoreVisualIDRegistry.getVisualID(op.getSemanticHint());
      if (elementType != null)
      {
        if (!AcoreElementTypes.isKnownElementType(elementType) || !(elementType instanceof IHintedType))
        {
          return false; // foreign element type
        }
        String elementTypeHint = ((IHintedType)elementType).getSemanticHint();
        if (!op.getSemanticHint().equals(elementTypeHint))
        {
          return false; // if semantic hint is specified it should be the same as in element type
        }
        if (domainElement != null && visualID != AcoreVisualIDRegistry.getNodeVisualID(op.getContainerView(), domainElement))
        {
          return false; // visual id for node EClass should match visual id from element type
        }
      }
      else
      {
        if (!ACoreRootEditPart.MODEL_ID.equals(AcoreVisualIDRegistry.getModelID(op.getContainerView())))
        {
          return false; // foreign diagram
        }
        switch (visualID)
        {
        case AInterfaceEditPart.VISUAL_ID:
        case AClassEditPart.VISUAL_ID:
        case AAttributeEditPart.VISUAL_ID:
        case AOperationEditPart.VISUAL_ID:
        case AAttribute2EditPart.VISUAL_ID:
        case AOperation2EditPart.VISUAL_ID:
          if (domainElement == null || visualID != AcoreVisualIDRegistry.getNodeVisualID(op.getContainerView(), domainElement))
          {
            return false; // visual id in semantic hint should match visual id for domain element
          }
          break;
        default:
          return false;
        }
      }
    }
    return AInterfaceEditPart.VISUAL_ID == visualID || AClassEditPart.VISUAL_ID == visualID || AAttributeEditPart.VISUAL_ID == visualID
        || AOperationEditPart.VISUAL_ID == visualID || AAttribute2EditPart.VISUAL_ID == visualID || AOperation2EditPart.VISUAL_ID == visualID;
  }

  /**
   * @generated
   */
  protected boolean provides(CreateEdgeViewOperation op)
  {
    IElementType elementType = getSemanticElementType(op.getSemanticAdapter());
    if (!AcoreElementTypes.isKnownElementType(elementType) || !(elementType instanceof IHintedType))
    {
      return false; // foreign element type
    }
    String elementTypeHint = ((IHintedType)elementType).getSemanticHint();
    if (elementTypeHint == null || op.getSemanticHint() != null && !elementTypeHint.equals(op.getSemanticHint()))
    {
      return false; // our hint is visual id and must be specified, and it should be the same as in element type
    }
    int visualID = AcoreVisualIDRegistry.getVisualID(elementTypeHint);
    EObject domainElement = getSemanticElement(op.getSemanticAdapter());
    if (domainElement != null && visualID != AcoreVisualIDRegistry.getLinkWithClassVisualID(domainElement))
    {
      return false; // visual id for link EClass should match visual id from element type
    }
    return true;
  }

  /**
   * @generated
   */
  public Diagram createDiagram(IAdaptable semanticAdapter, String diagramKind, PreferencesHint preferencesHint)
  {
    Diagram diagram = NotationFactory.eINSTANCE.createDiagram();
    diagram.getStyles().add(NotationFactory.eINSTANCE.createDiagramStyle());
    diagram.setType(ACoreRootEditPart.MODEL_ID);
    diagram.setElement(getSemanticElement(semanticAdapter));
    diagram.setMeasurementUnit(MeasurementUnit.PIXEL_LITERAL);
    return diagram;
  }

  /**
   * @generated
   */
  public Node createNode(IAdaptable semanticAdapter, View containerView, String semanticHint, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    final EObject domainElement = getSemanticElement(semanticAdapter);
    final int visualID;
    if (semanticHint == null)
    {
      visualID = AcoreVisualIDRegistry.getNodeVisualID(containerView, domainElement);
    }
    else
    {
      visualID = AcoreVisualIDRegistry.getVisualID(semanticHint);
    }
    switch (visualID)
    {
    case AInterfaceEditPart.VISUAL_ID:
      return createAInterface_2001(domainElement, containerView, index, persisted, preferencesHint);
    case AClassEditPart.VISUAL_ID:
      return createAClass_2002(domainElement, containerView, index, persisted, preferencesHint);
    case AAttributeEditPart.VISUAL_ID:
      return createAAttribute_3001(domainElement, containerView, index, persisted, preferencesHint);
    case AOperationEditPart.VISUAL_ID:
      return createAOperation_3002(domainElement, containerView, index, persisted, preferencesHint);
    case AAttribute2EditPart.VISUAL_ID:
      return createAAttribute_3003(domainElement, containerView, index, persisted, preferencesHint);
    case AOperation2EditPart.VISUAL_ID:
      return createAOperation_3004(domainElement, containerView, index, persisted, preferencesHint);
    }
    // can't happen, provided #provides(CreateNodeViewOperation) is correct
    return null;
  }

  /**
   * @generated
   */
  public Edge createEdge(IAdaptable semanticAdapter, View containerView, String semanticHint, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    IElementType elementType = getSemanticElementType(semanticAdapter);
    String elementTypeHint = ((IHintedType)elementType).getSemanticHint();
    switch (AcoreVisualIDRegistry.getVisualID(elementTypeHint))
    {
    case AClassSubClassesEditPart.VISUAL_ID:
      return createAClassSubClasses_4001(containerView, index, persisted, preferencesHint);
    case AClassImplementedInterfacesEditPart.VISUAL_ID:
      return createAClassImplementedInterfaces_4002(containerView, index, persisted, preferencesHint);
    case AClassAssociationsEditPart.VISUAL_ID:
      return createAClassAssociations_4003(containerView, index, persisted, preferencesHint);
    case AClassAggregationsEditPart.VISUAL_ID:
      return createAClassAggregations_4004(containerView, index, persisted, preferencesHint);
    case AClassCompositionsEditPart.VISUAL_ID:
      return createAClassCompositions_4005(containerView, index, persisted, preferencesHint);
    }
    // can never happen, provided #provides(CreateEdgeViewOperation) is correct
    return null;
  }

  /**
   * @generated
   */
  public Node createAInterface_2001(EObject domainElement, View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    node.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
    node.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    node.getStyles().add(NotationFactory.eINSTANCE.createLineStyle());
    node.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
    node.setType(AcoreVisualIDRegistry.getType(AInterfaceEditPart.VISUAL_ID));
    ViewUtil.insertChildView(containerView, node, index, persisted);
    node.setElement(domainElement);
    stampShortcut(containerView, node);
    // initializeFromPreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(node, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle nodeFontStyle = (FontStyle)node.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (nodeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      nodeFontStyle.setFontName(fontData.getName());
      nodeFontStyle.setFontHeight(fontData.getHeight());
      nodeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      nodeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      nodeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Node label5001 = createLabel(node, AcoreVisualIDRegistry.getType(AInterfaceNameEditPart.VISUAL_ID));
    createCompartment(node, AcoreVisualIDRegistry.getType(AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID), false, false, true, true);
    createCompartment(node, AcoreVisualIDRegistry.getType(AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID), false, false, true, true);
    return node;
  }

  /**
   * @generated
   */
  public Node createAClass_2002(EObject domainElement, View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    node.getStyles().add(NotationFactory.eINSTANCE.createDescriptionStyle());
    node.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    node.getStyles().add(NotationFactory.eINSTANCE.createLineStyle());
    node.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
    node.setType(AcoreVisualIDRegistry.getType(AClassEditPart.VISUAL_ID));
    ViewUtil.insertChildView(containerView, node, index, persisted);
    node.setElement(domainElement);
    stampShortcut(containerView, node);
    // initializeFromPreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(node, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle nodeFontStyle = (FontStyle)node.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (nodeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      nodeFontStyle.setFontName(fontData.getName());
      nodeFontStyle.setFontHeight(fontData.getHeight());
      nodeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      nodeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      nodeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Node label5002 = createLabel(node, AcoreVisualIDRegistry.getType(AClassNameEditPart.VISUAL_ID));
    createCompartment(node, AcoreVisualIDRegistry.getType(AClassAAttributeCompartmentEditPart.VISUAL_ID), false, false, true, true);
    createCompartment(node, AcoreVisualIDRegistry.getType(AClassAOperationClassCompartmentEditPart.VISUAL_ID), false, false, true, true);
    return node;
  }

  /**
   * @generated
   */
  public Node createAAttribute_3001(EObject domainElement, View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    node.setLayoutConstraint(NotationFactory.eINSTANCE.createLocation());
    node.setType(AcoreVisualIDRegistry.getType(AAttributeEditPart.VISUAL_ID));
    ViewUtil.insertChildView(containerView, node, index, persisted);
    node.setElement(domainElement);
    return node;
  }

  /**
   * @generated
   */
  public Node createAOperation_3002(EObject domainElement, View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    node.setLayoutConstraint(NotationFactory.eINSTANCE.createLocation());
    node.setType(AcoreVisualIDRegistry.getType(AOperationEditPart.VISUAL_ID));
    ViewUtil.insertChildView(containerView, node, index, persisted);
    node.setElement(domainElement);
    return node;
  }

  /**
   * @generated
   */
  public Node createAAttribute_3003(EObject domainElement, View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    node.setLayoutConstraint(NotationFactory.eINSTANCE.createLocation());
    node.setType(AcoreVisualIDRegistry.getType(AAttribute2EditPart.VISUAL_ID));
    ViewUtil.insertChildView(containerView, node, index, persisted);
    node.setElement(domainElement);
    return node;
  }

  /**
   * @generated
   */
  public Node createAOperation_3004(EObject domainElement, View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    node.setLayoutConstraint(NotationFactory.eINSTANCE.createLocation());
    node.setType(AcoreVisualIDRegistry.getType(AOperation2EditPart.VISUAL_ID));
    ViewUtil.insertChildView(containerView, node, index, persisted);
    node.setElement(domainElement);
    return node;
  }

  /**
   * @generated
   */
  public Edge createAClassSubClasses_4001(View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Connector edge = NotationFactory.eINSTANCE.createConnector();
    edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    RelativeBendpoints bendpoints = NotationFactory.eINSTANCE.createRelativeBendpoints();
    ArrayList points = new ArrayList(2);
    points.add(new RelativeBendpoint());
    points.add(new RelativeBendpoint());
    bendpoints.setPoints(points);
    edge.setBendpoints(bendpoints);
    ViewUtil.insertChildView(containerView, edge, index, persisted);
    edge.setType(AcoreVisualIDRegistry.getType(AClassSubClassesEditPart.VISUAL_ID));
    edge.setElement(null);
    // initializePreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle edgeFontStyle = (FontStyle)edge.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (edgeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      edgeFontStyle.setFontName(fontData.getName());
      edgeFontStyle.setFontHeight(fontData.getHeight());
      edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Routing routing = Routing.get(prefStore.getInt(IPreferenceConstants.PREF_LINE_STYLE));
    if (routing != null)
    {
      ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getRoutingStyle_Routing(), routing);
    }
    return edge;
  }

  /**
   * @generated
   */
  public Edge createAClassImplementedInterfaces_4002(View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Connector edge = NotationFactory.eINSTANCE.createConnector();
    edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    RelativeBendpoints bendpoints = NotationFactory.eINSTANCE.createRelativeBendpoints();
    ArrayList points = new ArrayList(2);
    points.add(new RelativeBendpoint());
    points.add(new RelativeBendpoint());
    bendpoints.setPoints(points);
    edge.setBendpoints(bendpoints);
    ViewUtil.insertChildView(containerView, edge, index, persisted);
    edge.setType(AcoreVisualIDRegistry.getType(AClassImplementedInterfacesEditPart.VISUAL_ID));
    edge.setElement(null);
    // initializePreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle edgeFontStyle = (FontStyle)edge.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (edgeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      edgeFontStyle.setFontName(fontData.getName());
      edgeFontStyle.setFontHeight(fontData.getHeight());
      edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Routing routing = Routing.get(prefStore.getInt(IPreferenceConstants.PREF_LINE_STYLE));
    if (routing != null)
    {
      ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getRoutingStyle_Routing(), routing);
    }
    return edge;
  }

  /**
   * @generated
   */
  public Edge createAClassAssociations_4003(View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Connector edge = NotationFactory.eINSTANCE.createConnector();
    edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    RelativeBendpoints bendpoints = NotationFactory.eINSTANCE.createRelativeBendpoints();
    ArrayList points = new ArrayList(2);
    points.add(new RelativeBendpoint());
    points.add(new RelativeBendpoint());
    bendpoints.setPoints(points);
    edge.setBendpoints(bendpoints);
    ViewUtil.insertChildView(containerView, edge, index, persisted);
    edge.setType(AcoreVisualIDRegistry.getType(AClassAssociationsEditPart.VISUAL_ID));
    edge.setElement(null);
    // initializePreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle edgeFontStyle = (FontStyle)edge.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (edgeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      edgeFontStyle.setFontName(fontData.getName());
      edgeFontStyle.setFontHeight(fontData.getHeight());
      edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Routing routing = Routing.get(prefStore.getInt(IPreferenceConstants.PREF_LINE_STYLE));
    if (routing != null)
    {
      ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getRoutingStyle_Routing(), routing);
    }
    return edge;
  }

  /**
   * @generated
   */
  public Edge createAClassAggregations_4004(View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Connector edge = NotationFactory.eINSTANCE.createConnector();
    edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    RelativeBendpoints bendpoints = NotationFactory.eINSTANCE.createRelativeBendpoints();
    ArrayList points = new ArrayList(2);
    points.add(new RelativeBendpoint());
    points.add(new RelativeBendpoint());
    bendpoints.setPoints(points);
    edge.setBendpoints(bendpoints);
    ViewUtil.insertChildView(containerView, edge, index, persisted);
    edge.setType(AcoreVisualIDRegistry.getType(AClassAggregationsEditPart.VISUAL_ID));
    edge.setElement(null);
    // initializePreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle edgeFontStyle = (FontStyle)edge.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (edgeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      edgeFontStyle.setFontName(fontData.getName());
      edgeFontStyle.setFontHeight(fontData.getHeight());
      edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Routing routing = Routing.get(prefStore.getInt(IPreferenceConstants.PREF_LINE_STYLE));
    if (routing != null)
    {
      ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getRoutingStyle_Routing(), routing);
    }
    return edge;
  }

  /**
   * @generated
   */
  public Edge createAClassCompositions_4005(View containerView, int index, boolean persisted, PreferencesHint preferencesHint)
  {
    Connector edge = NotationFactory.eINSTANCE.createConnector();
    edge.getStyles().add(NotationFactory.eINSTANCE.createFontStyle());
    RelativeBendpoints bendpoints = NotationFactory.eINSTANCE.createRelativeBendpoints();
    ArrayList points = new ArrayList(2);
    points.add(new RelativeBendpoint());
    points.add(new RelativeBendpoint());
    bendpoints.setPoints(points);
    edge.setBendpoints(bendpoints);
    ViewUtil.insertChildView(containerView, edge, index, persisted);
    edge.setType(AcoreVisualIDRegistry.getType(AClassCompositionsEditPart.VISUAL_ID));
    edge.setElement(null);
    // initializePreferences
    final IPreferenceStore prefStore = (IPreferenceStore)preferencesHint.getPreferenceStore();

    org.eclipse.swt.graphics.RGB lineRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_LINE_COLOR);
    ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getLineStyle_LineColor(), FigureUtilities.RGBToInteger(lineRGB));
    FontStyle edgeFontStyle = (FontStyle)edge.getStyle(NotationPackage.Literals.FONT_STYLE);
    if (edgeFontStyle != null)
    {
      FontData fontData = PreferenceConverter.getFontData(prefStore, IPreferenceConstants.PREF_DEFAULT_FONT);
      edgeFontStyle.setFontName(fontData.getName());
      edgeFontStyle.setFontHeight(fontData.getHeight());
      edgeFontStyle.setBold((fontData.getStyle() & SWT.BOLD) != 0);
      edgeFontStyle.setItalic((fontData.getStyle() & SWT.ITALIC) != 0);
      org.eclipse.swt.graphics.RGB fontRGB = PreferenceConverter.getColor(prefStore, IPreferenceConstants.PREF_FONT_COLOR);
      edgeFontStyle.setFontColor(FigureUtilities.RGBToInteger(fontRGB).intValue());
    }
    Routing routing = Routing.get(prefStore.getInt(IPreferenceConstants.PREF_LINE_STYLE));
    if (routing != null)
    {
      ViewUtil.setStructuralFeatureValue(edge, NotationPackage.eINSTANCE.getRoutingStyle_Routing(), routing);
    }
    return edge;
  }

  /**
   * @generated
   */
  private void stampShortcut(View containerView, Node target)
  {
    if (!ACoreRootEditPart.MODEL_ID.equals(AcoreVisualIDRegistry.getModelID(containerView)))
    {
      EAnnotation shortcutAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
      shortcutAnnotation.setSource("Shortcut"); //$NON-NLS-1$
      shortcutAnnotation.getDetails().put("modelID", ACoreRootEditPart.MODEL_ID); //$NON-NLS-1$
      target.getEAnnotations().add(shortcutAnnotation);
    }
  }

  /**
   * @generated
   */
  private Node createLabel(View owner, String hint)
  {
    DecorationNode rv = NotationFactory.eINSTANCE.createDecorationNode();
    rv.setType(hint);
    ViewUtil.insertChildView(owner, rv, ViewUtil.APPEND, true);
    return rv;
  }

  /**
   * @generated
   */
  private Node createCompartment(View owner, String hint, boolean canCollapse, boolean hasTitle, boolean canSort, boolean canFilter)
  {
    // SemanticListCompartment rv = NotationFactory.eINSTANCE.createSemanticListCompartment();
    // rv.setShowTitle(showTitle);
    // rv.setCollapsed(isCollapsed);
    Node rv;
    if (canCollapse)
    {
      rv = NotationFactory.eINSTANCE.createBasicCompartment();
    }
    else
    {
      rv = NotationFactory.eINSTANCE.createDecorationNode();
    }
    if (hasTitle)
    {
      TitleStyle ts = NotationFactory.eINSTANCE.createTitleStyle();
      ts.setShowTitle(true);
      rv.getStyles().add(ts);
    }
    if (canSort)
    {
      rv.getStyles().add(NotationFactory.eINSTANCE.createSortingStyle());
    }
    if (canFilter)
    {
      rv.getStyles().add(NotationFactory.eINSTANCE.createFilteringStyle());
    }
    rv.setType(hint);
    ViewUtil.insertChildView(owner, rv, ViewUtil.APPEND, true);
    return rv;
  }

  /**
   * @generated
   */
  private EObject getSemanticElement(IAdaptable semanticAdapter)
  {
    if (semanticAdapter == null)
    {
      return null;
    }
    EObject eObject = semanticAdapter.getAdapter(EObject.class);
    if (eObject != null)
    {
      return EMFCoreUtil.resolve(TransactionUtil.getEditingDomain(eObject), eObject);
    }
    return null;
  }

  /**
   * @generated
   */
  private IElementType getSemanticElementType(IAdaptable semanticAdapter)
  {
    if (semanticAdapter == null)
    {
      return null;
    }
    return semanticAdapter.getAdapter(IElementType.class);
  }
}
