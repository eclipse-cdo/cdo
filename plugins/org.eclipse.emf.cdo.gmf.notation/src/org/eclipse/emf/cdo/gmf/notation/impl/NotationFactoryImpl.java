/******************************************************************************
 * Copyright (c) 2018-2020 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.gmf.runtime.notation.Alignment;
import org.eclipse.gmf.runtime.notation.ArrowStyle;
import org.eclipse.gmf.runtime.notation.ArrowType;
import org.eclipse.gmf.runtime.notation.BasicCompartment;
import org.eclipse.gmf.runtime.notation.BasicDecorationNode;
import org.eclipse.gmf.runtime.notation.BasicSemanticCompartment;
import org.eclipse.gmf.runtime.notation.BooleanListValueStyle;
import org.eclipse.gmf.runtime.notation.BooleanValueStyle;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.ByteArrayValueStyle;
import org.eclipse.gmf.runtime.notation.CanonicalStyle;
import org.eclipse.gmf.runtime.notation.Compartment;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.gmf.runtime.notation.ConnectorStyle;
import org.eclipse.gmf.runtime.notation.DataTypeStyle;
import org.eclipse.gmf.runtime.notation.DecorationNode;
import org.eclipse.gmf.runtime.notation.DescriptionStyle;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.DiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.DiagramStyle;
import org.eclipse.gmf.runtime.notation.DoubleListValueStyle;
import org.eclipse.gmf.runtime.notation.DoubleValueStyle;
import org.eclipse.gmf.runtime.notation.DrawerStyle;
import org.eclipse.gmf.runtime.notation.EObjectListValueStyle;
import org.eclipse.gmf.runtime.notation.EObjectValueStyle;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.FillStyle;
import org.eclipse.gmf.runtime.notation.Filtering;
import org.eclipse.gmf.runtime.notation.FilteringStyle;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.GradientStyle;
import org.eclipse.gmf.runtime.notation.Guide;
import org.eclipse.gmf.runtime.notation.GuideStyle;
import org.eclipse.gmf.runtime.notation.HintedDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.IdentityAnchor;
import org.eclipse.gmf.runtime.notation.Image;
import org.eclipse.gmf.runtime.notation.ImageBufferStyle;
import org.eclipse.gmf.runtime.notation.ImageStyle;
import org.eclipse.gmf.runtime.notation.IntListValueStyle;
import org.eclipse.gmf.runtime.notation.IntValueStyle;
import org.eclipse.gmf.runtime.notation.JumpLinkStatus;
import org.eclipse.gmf.runtime.notation.JumpLinkType;
import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.LineType;
import org.eclipse.gmf.runtime.notation.LineTypeStyle;
import org.eclipse.gmf.runtime.notation.ListCompartment;
import org.eclipse.gmf.runtime.notation.ListValueStyle;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.MultiDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.NamedStyle;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.PageStyle;
import org.eclipse.gmf.runtime.notation.PropertiesSetStyle;
import org.eclipse.gmf.runtime.notation.PropertyValue;
import org.eclipse.gmf.runtime.notation.Ratio;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.RoundedCornersStyle;
import org.eclipse.gmf.runtime.notation.Routing;
import org.eclipse.gmf.runtime.notation.RoutingStyle;
import org.eclipse.gmf.runtime.notation.SemanticListCompartment;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.ShapeStyle;
import org.eclipse.gmf.runtime.notation.SingleValueStyle;
import org.eclipse.gmf.runtime.notation.Size;
import org.eclipse.gmf.runtime.notation.Smoothness;
import org.eclipse.gmf.runtime.notation.Sorting;
import org.eclipse.gmf.runtime.notation.SortingDirection;
import org.eclipse.gmf.runtime.notation.SortingStyle;
import org.eclipse.gmf.runtime.notation.StandardDiagram;
import org.eclipse.gmf.runtime.notation.StringListValueStyle;
import org.eclipse.gmf.runtime.notation.StringValueStyle;
import org.eclipse.gmf.runtime.notation.TextAlignment;
import org.eclipse.gmf.runtime.notation.TextStyle;
import org.eclipse.gmf.runtime.notation.TitleStyle;
import org.eclipse.gmf.runtime.notation.datatype.GradientData;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * @generated
 */
public class NotationFactoryImpl extends EFactoryImpl implements NotationFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static NotationFactory init()
  {
    try
    {
      NotationFactory theNotationFactory = (NotationFactory)EPackage.Registry.INSTANCE.getEFactory(NotationPackage.eNS_URI);
      if (theNotationFactory != null)
      {
        return theNotationFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new NotationFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   */
  public NotationFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case NotationPackage.EDGE:
      return createEdge();
    case NotationPackage.NODE:
      return createNode();
    case NotationPackage.FILL_STYLE:
      return createFillStyle();
    case NotationPackage.LINE_STYLE:
      return createLineStyle();
    case NotationPackage.FONT_STYLE:
      return createFontStyle();
    case NotationPackage.TITLE_STYLE:
      return createTitleStyle();
    case NotationPackage.SORTING_STYLE:
      return createSortingStyle();
    case NotationPackage.DESCRIPTION_STYLE:
      return createDescriptionStyle();
    case NotationPackage.SIZE:
      return createSize();
    case NotationPackage.LOCATION:
      return createLocation();
    case NotationPackage.BOUNDS:
      return createBounds();
    case NotationPackage.RATIO:
      return createRatio();
    case NotationPackage.IDENTITY_ANCHOR:
      return createIdentityAnchor();
    case NotationPackage.ROUTING_STYLE:
      return createRoutingStyle();
    case NotationPackage.RELATIVE_BENDPOINTS:
      return createRelativeBendpoints();
    case NotationPackage.DIAGRAM:
      return createDiagram();
    case NotationPackage.IMAGE:
      return createImage();
    case NotationPackage.CANONICAL_STYLE:
      return createCanonicalStyle();
    case NotationPackage.SHAPE_STYLE:
      return createShapeStyle();
    case NotationPackage.CONNECTOR_STYLE:
      return createConnectorStyle();
    case NotationPackage.PAGE_STYLE:
      return createPageStyle();
    case NotationPackage.DRAWER_STYLE:
      return createDrawerStyle();
    case NotationPackage.GUIDE_STYLE:
      return createGuideStyle();
    case NotationPackage.GUIDE:
      return createGuide();
    case NotationPackage.NODE_ENTRY:
      return (EObject)createNodeEntry();
    case NotationPackage.FILTERING_STYLE:
      return createFilteringStyle();
    case NotationPackage.DIAGRAM_STYLE:
      return createDiagramStyle();
    case NotationPackage.IMAGE_STYLE:
      return createImageStyle();
    case NotationPackage.IMAGE_BUFFER_STYLE:
      return createImageBufferStyle();
    case NotationPackage.PROPERTIES_SET_STYLE:
      return createPropertiesSetStyle();
    case NotationPackage.STRING_TO_PROPERTY_VALUE_MAP_ENTRY:
      return (EObject)createStringToPropertyValueMapEntry();
    case NotationPackage.PROPERTY_VALUE:
      return createPropertyValue();
    case NotationPackage.SINGLE_VALUE_STYLE:
      return createSingleValueStyle();
    case NotationPackage.LIST_VALUE_STYLE:
      return createListValueStyle();
    case NotationPackage.NAMED_STYLE:
      return createNamedStyle();
    case NotationPackage.DATA_TYPE_STYLE:
      return createDataTypeStyle();
    case NotationPackage.INT_VALUE_STYLE:
      return createIntValueStyle();
    case NotationPackage.INT_LIST_VALUE_STYLE:
      return createIntListValueStyle();
    case NotationPackage.BOOLEAN_VALUE_STYLE:
      return createBooleanValueStyle();
    case NotationPackage.DOUBLE_VALUE_STYLE:
      return createDoubleValueStyle();
    case NotationPackage.DOUBLE_LIST_VALUE_STYLE:
      return createDoubleListValueStyle();
    case NotationPackage.STRING_VALUE_STYLE:
      return createStringValueStyle();
    case NotationPackage.STRING_LIST_VALUE_STYLE:
      return createStringListValueStyle();
    case NotationPackage.EOBJECT_VALUE_STYLE:
      return createEObjectValueStyle();
    case NotationPackage.EOBJECT_LIST_VALUE_STYLE:
      return createEObjectListValueStyle();
    case NotationPackage.BYTE_ARRAY_VALUE_STYLE:
      return createByteArrayValueStyle();
    case NotationPackage.BOOLEAN_LIST_VALUE_STYLE:
      return createBooleanListValueStyle();
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE:
      return createHintedDiagramLinkStyle();
    case NotationPackage.DIAGRAM_LINK_STYLE:
      return createDiagramLinkStyle();
    case NotationPackage.MULTI_DIAGRAM_LINK_STYLE:
      return createMultiDiagramLinkStyle();
    case NotationPackage.TEXT_STYLE:
      return createTextStyle();
    case NotationPackage.LINE_TYPE_STYLE:
      return createLineTypeStyle();
    case NotationPackage.ARROW_STYLE:
      return createArrowStyle();
    case NotationPackage.SHAPE:
      return createShape();
    case NotationPackage.COMPARTMENT:
      return createCompartment();
    case NotationPackage.LIST_COMPARTMENT:
      return createListCompartment();
    case NotationPackage.CONNECTOR:
      return createConnector();
    case NotationPackage.STANDARD_DIAGRAM:
      return createStandardDiagram();
    case NotationPackage.DECORATION_NODE:
      return createDecorationNode();
    case NotationPackage.BASIC_DECORATION_NODE:
      return createBasicDecorationNode();
    case NotationPackage.BASIC_COMPARTMENT:
      return createBasicCompartment();
    case NotationPackage.BASIC_SEMANTIC_COMPARTMENT:
      return createBasicSemanticCompartment();
    case NotationPackage.SEMANTIC_LIST_COMPARTMENT:
      return createSemanticListCompartment();
    case NotationPackage.ROUNDED_CORNERS_STYLE:
      return createRoundedCornersStyle();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case NotationPackage.SORTING:
      return createSortingFromString(eDataType, initialValue);
    case NotationPackage.FILTERING:
      return createFilteringFromString(eDataType, initialValue);
    case NotationPackage.ROUTING:
      return createRoutingFromString(eDataType, initialValue);
    case NotationPackage.SMOOTHNESS:
      return createSmoothnessFromString(eDataType, initialValue);
    case NotationPackage.JUMP_LINK_STATUS:
      return createJumpLinkStatusFromString(eDataType, initialValue);
    case NotationPackage.JUMP_LINK_TYPE:
      return createJumpLinkTypeFromString(eDataType, initialValue);
    case NotationPackage.ALIGNMENT:
      return createAlignmentFromString(eDataType, initialValue);
    case NotationPackage.SORTING_DIRECTION:
      return createSortingDirectionFromString(eDataType, initialValue);
    case NotationPackage.MEASUREMENT_UNIT:
      return createMeasurementUnitFromString(eDataType, initialValue);
    case NotationPackage.TEXT_ALIGNMENT:
      return createTextAlignmentFromString(eDataType, initialValue);
    case NotationPackage.LINE_TYPE:
      return createLineTypeFromString(eDataType, initialValue);
    case NotationPackage.ARROW_TYPE:
      return createArrowTypeFromString(eDataType, initialValue);
    case NotationPackage.GRADIENT_STYLE:
      return createGradientStyleFromString(eDataType, initialValue);
    case NotationPackage.RELATIVE_BENDPOINT_LIST:
      return createRelativeBendpointListFromString(eDataType, initialValue);
    case NotationPackage.FILTER_KEY_LIST:
      return createFilterKeyListFromString(eDataType, initialValue);
    case NotationPackage.SORT_KEY_MAP:
      return createSortKeyMapFromString(eDataType, initialValue);
    case NotationPackage.GRADIENT_DATA:
      return createGradientDataFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case NotationPackage.SORTING:
      return convertSortingToString(eDataType, instanceValue);
    case NotationPackage.FILTERING:
      return convertFilteringToString(eDataType, instanceValue);
    case NotationPackage.ROUTING:
      return convertRoutingToString(eDataType, instanceValue);
    case NotationPackage.SMOOTHNESS:
      return convertSmoothnessToString(eDataType, instanceValue);
    case NotationPackage.JUMP_LINK_STATUS:
      return convertJumpLinkStatusToString(eDataType, instanceValue);
    case NotationPackage.JUMP_LINK_TYPE:
      return convertJumpLinkTypeToString(eDataType, instanceValue);
    case NotationPackage.ALIGNMENT:
      return convertAlignmentToString(eDataType, instanceValue);
    case NotationPackage.SORTING_DIRECTION:
      return convertSortingDirectionToString(eDataType, instanceValue);
    case NotationPackage.MEASUREMENT_UNIT:
      return convertMeasurementUnitToString(eDataType, instanceValue);
    case NotationPackage.TEXT_ALIGNMENT:
      return convertTextAlignmentToString(eDataType, instanceValue);
    case NotationPackage.LINE_TYPE:
      return convertLineTypeToString(eDataType, instanceValue);
    case NotationPackage.ARROW_TYPE:
      return convertArrowTypeToString(eDataType, instanceValue);
    case NotationPackage.GRADIENT_STYLE:
      return convertGradientStyleToString(eDataType, instanceValue);
    case NotationPackage.RELATIVE_BENDPOINT_LIST:
      return convertRelativeBendpointListToString(eDataType, instanceValue);
    case NotationPackage.FILTER_KEY_LIST:
      return convertFilterKeyListToString(eDataType, instanceValue);
    case NotationPackage.SORT_KEY_MAP:
      return convertSortKeyMapToString(eDataType, instanceValue);
    case NotationPackage.GRADIENT_DATA:
      return convertGradientDataToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Edge createEdge()
  {
    EdgeImpl edge = new EdgeImpl();
    return edge;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Node createNode()
  {
    NodeImpl node = new NodeImpl();
    return node;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FillStyle createFillStyle()
  {
    FillStyleImpl fillStyle = new FillStyleImpl();
    return fillStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public LineStyle createLineStyle()
  {
    LineStyleImpl lineStyle = new LineStyleImpl();
    return lineStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FontStyle createFontStyle()
  {
    FontStyleImpl fontStyle = new FontStyleImpl();
    return fontStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TitleStyle createTitleStyle()
  {
    TitleStyleImpl titleStyle = new TitleStyleImpl();
    return titleStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SortingStyle createSortingStyle()
  {
    SortingStyleImpl sortingStyle = new SortingStyleImpl();
    return sortingStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DescriptionStyle createDescriptionStyle()
  {
    DescriptionStyleImpl descriptionStyle = new DescriptionStyleImpl();
    return descriptionStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Size createSize()
  {
    SizeImpl size = new SizeImpl();
    return size;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Location createLocation()
  {
    LocationImpl location = new LocationImpl();
    return location;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Bounds createBounds()
  {
    BoundsImpl bounds = new BoundsImpl();
    return bounds;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Ratio createRatio()
  {
    RatioImpl ratio = new RatioImpl();
    return ratio;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IdentityAnchor createIdentityAnchor()
  {
    IdentityAnchorImpl identityAnchor = new IdentityAnchorImpl();
    return identityAnchor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RoutingStyle createRoutingStyle()
  {
    RoutingStyleImpl routingStyle = new RoutingStyleImpl();
    return routingStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RelativeBendpoints createRelativeBendpoints()
  {
    RelativeBendpointsImpl relativeBendpoints = new RelativeBendpointsImpl();
    return relativeBendpoints;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CanonicalStyle createCanonicalStyle()
  {
    CanonicalStyleImpl canonicalStyle = new CanonicalStyleImpl();
    return canonicalStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ShapeStyle createShapeStyle()
  {
    ShapeStyleImpl shapeStyle = new ShapeStyleImpl();
    return shapeStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ConnectorStyle createConnectorStyle()
  {
    ConnectorStyleImpl connectorStyle = new ConnectorStyleImpl();
    return connectorStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Diagram createDiagram()
  {
    DiagramImpl diagram = new DiagramImpl();
    return diagram;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Image createImage()
  {
    ImageImpl image = new ImageImpl();
    return image;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PageStyle createPageStyle()
  {
    PageStyleImpl pageStyle = new PageStyleImpl();
    return pageStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DrawerStyle createDrawerStyle()
  {
    DrawerStyleImpl drawerStyle = new DrawerStyleImpl();
    return drawerStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GuideStyle createGuideStyle()
  {
    GuideStyleImpl guideStyle = new GuideStyleImpl();
    return guideStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Guide createGuide()
  {
    GuideImpl guide = new GuideImpl();
    return guide;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry createNodeEntry()
  {
    NodeEntryImpl nodeEntry = new NodeEntryImpl();
    return nodeEntry;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FilteringStyle createFilteringStyle()
  {
    FilteringStyleImpl filteringStyle = new FilteringStyleImpl();
    return filteringStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DiagramStyle createDiagramStyle()
  {
    DiagramStyleImpl diagramStyle = new DiagramStyleImpl();
    return diagramStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImageStyle createImageStyle()
  {
    ImageStyleImpl imageStyle = new ImageStyleImpl();
    return imageStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ImageBufferStyle createImageBufferStyle()
  {
    ImageBufferStyleImpl imageBufferStyle = new ImageBufferStyleImpl();
    return imageBufferStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PropertiesSetStyle createPropertiesSetStyle()
  {
    PropertiesSetStyleImpl propertiesSetStyle = new PropertiesSetStyleImpl();
    return propertiesSetStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry createStringToPropertyValueMapEntry()
  {
    StringToPropertyValueMapEntryImpl stringToPropertyValueMapEntry = new StringToPropertyValueMapEntryImpl();
    return stringToPropertyValueMapEntry;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PropertyValue createPropertyValue()
  {
    PropertyValueImpl propertyValue = new PropertyValueImpl();
    return propertyValue;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SingleValueStyle createSingleValueStyle()
  {
    SingleValueStyleImpl singleValueStyle = new SingleValueStyleImpl();
    return singleValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ListValueStyle createListValueStyle()
  {
    ListValueStyleImpl listValueStyle = new ListValueStyleImpl();
    return listValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NamedStyle createNamedStyle()
  {
    NamedStyleImpl namedStyle = new NamedStyleImpl();
    return namedStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DataTypeStyle createDataTypeStyle()
  {
    DataTypeStyleImpl dataTypeStyle = new DataTypeStyleImpl();
    return dataTypeStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IntValueStyle createIntValueStyle()
  {
    IntValueStyleImpl intValueStyle = new IntValueStyleImpl();
    return intValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IntListValueStyle createIntListValueStyle()
  {
    IntListValueStyleImpl intListValueStyle = new IntListValueStyleImpl();
    return intListValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BooleanValueStyle createBooleanValueStyle()
  {
    BooleanValueStyleImpl booleanValueStyle = new BooleanValueStyleImpl();
    return booleanValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DoubleValueStyle createDoubleValueStyle()
  {
    DoubleValueStyleImpl doubleValueStyle = new DoubleValueStyleImpl();
    return doubleValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DoubleListValueStyle createDoubleListValueStyle()
  {
    DoubleListValueStyleImpl doubleListValueStyle = new DoubleListValueStyleImpl();
    return doubleListValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public StringValueStyle createStringValueStyle()
  {
    StringValueStyleImpl stringValueStyle = new StringValueStyleImpl();
    return stringValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public StringListValueStyle createStringListValueStyle()
  {
    StringListValueStyleImpl stringListValueStyle = new StringListValueStyleImpl();
    return stringListValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObjectValueStyle createEObjectValueStyle()
  {
    EObjectValueStyleImpl eObjectValueStyle = new EObjectValueStyleImpl();
    return eObjectValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObjectListValueStyle createEObjectListValueStyle()
  {
    EObjectListValueStyleImpl eObjectListValueStyle = new EObjectListValueStyleImpl();
    return eObjectListValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ByteArrayValueStyle createByteArrayValueStyle()
  {
    ByteArrayValueStyleImpl byteArrayValueStyle = new ByteArrayValueStyleImpl();
    return byteArrayValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BooleanListValueStyle createBooleanListValueStyle()
  {
    BooleanListValueStyleImpl booleanListValueStyle = new BooleanListValueStyleImpl();
    return booleanListValueStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public HintedDiagramLinkStyle createHintedDiagramLinkStyle()
  {
    HintedDiagramLinkStyleImpl hintedDiagramLinkStyle = new HintedDiagramLinkStyleImpl();
    return hintedDiagramLinkStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DiagramLinkStyle createDiagramLinkStyle()
  {
    DiagramLinkStyleImpl diagramLinkStyle = new DiagramLinkStyleImpl();
    return diagramLinkStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MultiDiagramLinkStyle createMultiDiagramLinkStyle()
  {
    MultiDiagramLinkStyleImpl multiDiagramLinkStyle = new MultiDiagramLinkStyleImpl();
    return multiDiagramLinkStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TextStyle createTextStyle()
  {
    TextStyleImpl textStyle = new TextStyleImpl();
    return textStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public LineTypeStyle createLineTypeStyle()
  {
    LineTypeStyleImpl lineTypeStyle = new LineTypeStyleImpl();
    return lineTypeStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ArrowStyle createArrowStyle()
  {
    ArrowStyleImpl arrowStyle = new ArrowStyleImpl();
    return arrowStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Shape createShape()
  {
    ShapeImpl shape = new ShapeImpl();
    return shape;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Compartment createCompartment()
  {
    CompartmentImpl compartment = new CompartmentImpl();
    return compartment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ListCompartment createListCompartment()
  {
    ListCompartmentImpl listCompartment = new ListCompartmentImpl();
    return listCompartment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Connector createConnector()
  {
    ConnectorImpl connector = new ConnectorImpl();
    return connector;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public StandardDiagram createStandardDiagram()
  {
    StandardDiagramImpl standardDiagram = new StandardDiagramImpl();
    return standardDiagram;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DecorationNode createDecorationNode()
  {
    DecorationNodeImpl decorationNode = new DecorationNodeImpl();
    return decorationNode;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BasicDecorationNode createBasicDecorationNode()
  {
    BasicDecorationNodeImpl basicDecorationNode = new BasicDecorationNodeImpl();
    return basicDecorationNode;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BasicCompartment createBasicCompartment()
  {
    BasicCompartmentImpl basicCompartment = new BasicCompartmentImpl();
    return basicCompartment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BasicSemanticCompartment createBasicSemanticCompartment()
  {
    BasicSemanticCompartmentImpl basicSemanticCompartment = new BasicSemanticCompartmentImpl();
    return basicSemanticCompartment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SemanticListCompartment createSemanticListCompartment()
  {
    SemanticListCompartmentImpl semanticListCompartment = new SemanticListCompartmentImpl();
    return semanticListCompartment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RoundedCornersStyle createRoundedCornersStyle()
  {
    RoundedCornersStyleImpl roundedCornersStyle = new RoundedCornersStyleImpl();
    return roundedCornersStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Sorting createSortingFromString(EDataType eDataType, String initialValue)
  {
    Sorting result = Sorting.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertSortingToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Filtering createFilteringFromString(EDataType eDataType, String initialValue)
  {
    Filtering result = Filtering.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertFilteringToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Routing createRoutingFromString(EDataType eDataType, String initialValue)
  {
    Routing result = Routing.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertRoutingToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Smoothness createSmoothnessFromString(EDataType eDataType, String initialValue)
  {
    Smoothness result = Smoothness.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertSmoothnessToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public JumpLinkStatus createJumpLinkStatusFromString(EDataType eDataType, String initialValue)
  {
    JumpLinkStatus result = JumpLinkStatus.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertJumpLinkStatusToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public JumpLinkType createJumpLinkTypeFromString(EDataType eDataType, String initialValue)
  {
    JumpLinkType result = JumpLinkType.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertJumpLinkTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Alignment createAlignmentFromString(EDataType eDataType, String initialValue)
  {
    Alignment result = Alignment.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertAlignmentToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public SortingDirection createSortingDirectionFromString(EDataType eDataType, String initialValue)
  {
    SortingDirection result = SortingDirection.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertSortingDirectionToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public MeasurementUnit createMeasurementUnitFromString(EDataType eDataType, String initialValue)
  {
    MeasurementUnit result = MeasurementUnit.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertMeasurementUnitToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public TextAlignment createTextAlignmentFromString(EDataType eDataType, String initialValue)
  {
    TextAlignment result = TextAlignment.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertTextAlignmentToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public LineType createLineTypeFromString(EDataType eDataType, String initialValue)
  {
    LineType result = LineType.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertLineTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ArrowType createArrowTypeFromString(EDataType eDataType, String initialValue)
  {
    ArrowType result = ArrowType.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertArrowTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public GradientStyle createGradientStyleFromString(EDataType eDataType, String initialValue)
  {
    GradientStyle result = GradientStyle.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertGradientStyleToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public List createRelativeBendpointListFromString(EDataType eDataType, String initialValue)
  {
    StringTokenizer st = new StringTokenizer(initialValue, "$"); //$NON-NLS-1$
    List newList = new ArrayList(st.countTokens());
    while (st.hasMoreTokens())
    {
      newList.add(new RelativeBendpoint(st.nextToken().trim()));
    }
    return newList;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public String convertRelativeBendpointListToString(EDataType eDataType, Object instanceValue)
  {
    StringBuffer sb = new StringBuffer();
    for (Iterator i = ((List)instanceValue).iterator(); i.hasNext();)
    {
      RelativeBendpoint point = (RelativeBendpoint)i.next();
      if (sb.length() != 0)
      {
        sb.append('$');
      }
      sb.append(point.convertToString());
    }
    return sb.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public List createFilterKeyListFromString(EDataType eDataType, String initialValue)
  {
    StringTokenizer st = new StringTokenizer(initialValue, ","); //$NON-NLS-1$
    List newList = new ArrayList(st.countTokens());
    while (st.hasMoreTokens())
    {
      newList.add(st.nextToken().trim());
    }
    return newList;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public String convertFilterKeyListToString(EDataType eDataType, Object instanceValue)
  {
    StringBuffer sb = new StringBuffer();
    for (Iterator i = ((List)instanceValue).iterator(); i.hasNext();)
    {
      String key = (String)i.next();
      if (sb.length() != 0)
      {
        sb.append(',');
      }
      sb.append(key);
    }
    return sb.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Map createSortKeyMapFromString(EDataType eDataType, String initialValue)
  {
    StringTokenizer st = new StringTokenizer(initialValue, ","); //$NON-NLS-1$
    Map newMap = new LinkedHashMap(st.countTokens());
    String s;
    int i;
    while (st.hasMoreTokens())
    {
      s = st.nextToken().trim();
      i = s.indexOf(':');
      newMap.put(s.substring(0, i).trim(), SortingDirection.get(s.substring(i + 1).trim()));
    }
    return newMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public String convertSortKeyMapToString(EDataType eDataType, Object instanceValue)
  {
    StringBuffer sb = new StringBuffer();
    Map keyMap = (Map)instanceValue;
    for (Iterator i = keyMap.keySet().iterator(); i.hasNext();)
    {
      String key = (String)i.next();
      SortingDirection direction = (SortingDirection)keyMap.get(key);
      if (sb.length() != 0)
      {
        sb.append(',');
      }
      sb.append(key + ":" + direction.getName()); //$NON-NLS-1$
    }
    return sb.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public GradientData createGradientDataFromString(EDataType eDataType, String initialValue)
  {
    GradientData gradient = null;
    if (initialValue != null && !initialValue.equals("")) //$NON-NLS-1$
    {
      gradient = new GradientData();
      StringTokenizer st = new StringTokenizer(initialValue, ","); //$NON-NLS-1$
      String s;
      if (st.hasMoreTokens())
      {
        s = st.nextToken().trim();
        try
        {
          gradient.setGradientColor1(Integer.parseInt(s));
        }
        catch (NumberFormatException e)
        {
        }
      }
      if (st.hasMoreTokens())
      {
        s = st.nextToken().trim();
        try
        {
          gradient.setGradientColor2(Integer.parseInt(s));
        }
        catch (NumberFormatException e)
        {
        }
      }
      if (st.hasMoreTokens())
      {
        s = st.nextToken().trim();
        try
        {
          gradient.setGradientStyle(Integer.parseInt(s));
        }
        catch (NumberFormatException e)
        {
        }
      }
    }
    return gradient;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public String convertGradientDataToString(EDataType eDataType, Object instanceValue)
  {
    GradientData gradient = (GradientData)instanceValue;
    if (gradient == null)
    {
      return ""; //$NON-NLS-1$
    }
    else
    {
      StringBuffer sb = new StringBuffer();
      sb.append(gradient.getGradientColor1());
      sb.append(", "); //$NON-NLS-1$
      sb.append(gradient.getGradientColor2());
      sb.append(", "); //$NON-NLS-1$
      sb.append(gradient.getGradientStyle());
      return sb.toString();
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotationPackage getNotationPackage()
  {
    return (NotationPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static NotationPackage getPackage()
  {
    return NotationPackage.eINSTANCE;
  }

} // NotationFactoryImpl
