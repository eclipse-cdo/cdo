/******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.gmf.runtime.notation.Alignment;
import org.eclipse.gmf.runtime.notation.Anchor;
import org.eclipse.gmf.runtime.notation.ArrowStyle;
import org.eclipse.gmf.runtime.notation.ArrowType;
import org.eclipse.gmf.runtime.notation.BasicCompartment;
import org.eclipse.gmf.runtime.notation.BasicDecorationNode;
import org.eclipse.gmf.runtime.notation.BasicSemanticCompartment;
import org.eclipse.gmf.runtime.notation.Bendpoints;
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
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
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
import org.eclipse.gmf.runtime.notation.StringObjectConverter;
import org.eclipse.gmf.runtime.notation.StringValueStyle;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.TextAlignment;
import org.eclipse.gmf.runtime.notation.TextStyle;
import org.eclipse.gmf.runtime.notation.TitleStyle;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.GradientData;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!--
 * end-user-doc -->
 * @generated
 */
public class NotationPackageImpl extends EPackageImpl implements NotationPackage
{
  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass edgeEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass nodeEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass styleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass fillStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass lineStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass fontStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass titleStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass sortingStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass descriptionStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass layoutConstraintEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass sizeEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass locationEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass boundsEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass ratioEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass anchorEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass bendpointsEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass identityAnchorEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass routingStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass viewEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass relativeBendpointsEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass diagramEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass imageEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass canonicalStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass shapeStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass connectorStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass pageStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass drawerStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass guideStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass guideEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass nodeEntryEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass filteringStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass diagramStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass imageStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass imageBufferStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass propertiesSetStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass stringToPropertyValueMapEntryEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass propertyValueEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass singleValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass listValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass namedStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass stringObjectConverterEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass dataTypeStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass intValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass intListValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass booleanValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass doubleValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass doubleListValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass stringValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass stringListValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass eObjectValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass eObjectListValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass byteArrayValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass booleanListValueStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass hintedDiagramLinkStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass diagramLinkStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass multiDiagramLinkStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass textStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass lineTypeStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass arrowStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass shapeEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass compartmentEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass listCompartmentEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass connectorEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass standardDiagramEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass decorationNodeEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass basicDecorationNodeEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass basicCompartmentEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass basicSemanticCompartmentEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass semanticListCompartmentEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EClass roundedCornersStyleEClass = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum sortingEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum filteringEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum routingEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum smoothnessEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum jumpLinkStatusEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum jumpLinkTypeEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum alignmentEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum sortingDirectionEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum measurementUnitEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum textAlignmentEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum lineTypeEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum arrowTypeEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EEnum gradientStyleEEnum = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EDataType relativeBendpointListEDataType = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EDataType filterKeyListEDataType = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EDataType sortKeyMapEDataType = null;

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private EDataType gradientDataEDataType = null;

  /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.eclipse.gmf.runtime.notation.NotationPackage#eNS_URI
     * @see #init()
     * @generated
     */
  private NotationPackageImpl()
  {
    super(eNS_URI, NotationFactory.eINSTANCE);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private static boolean isInited = false;

  /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     *
     * <p>This method is used to initialize {@link NotationPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
  public static NotationPackage init()
  {
    if (isInited)
    {
      return (NotationPackage)EPackage.Registry.INSTANCE.getEPackage(NotationPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredNotationPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    NotationPackageImpl theNotationPackage = registeredNotationPackage instanceof NotationPackageImpl ? (NotationPackageImpl)registeredNotationPackage
        : new NotationPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theNotationPackage.createPackageContents();

    // Initialize created meta-data
    theNotationPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theNotationPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(NotationPackage.eNS_URI, theNotationPackage);
    return theNotationPackage;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getEdge()
  {
    return edgeEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEdge_Source()
  {
    return (EReference)edgeEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEdge_Target()
  {
    return (EReference)edgeEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEdge_Bendpoints()
  {
    return (EReference)edgeEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEdge_SourceAnchor()
  {
    return (EReference)edgeEClass.getEStructuralFeatures().get(3);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEdge_TargetAnchor()
  {
    return (EReference)edgeEClass.getEStructuralFeatures().get(4);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getNode()
  {
    return nodeEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getNode_LayoutConstraint()
  {
    return (EReference)nodeEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getStyle()
  {
    return styleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getFillStyle()
  {
    return fillStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFillStyle_FillColor()
  {
    return (EAttribute)fillStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFillStyle_Transparency()
  {
    return (EAttribute)fillStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFillStyle_Gradient()
  {
    return (EAttribute)fillStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getLineStyle()
  {
    return lineStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getLineStyle_LineColor()
  {
    return (EAttribute)lineStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getLineStyle_LineWidth()
  {
    return (EAttribute)lineStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getFontStyle()
  {
    return fontStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_FontColor()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_FontName()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_FontHeight()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_Bold()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(3);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_Italic()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(4);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_Underline()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(5);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFontStyle_StrikeThrough()
  {
    return (EAttribute)fontStyleEClass.getEStructuralFeatures().get(6);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getTitleStyle()
  {
    return titleStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getTitleStyle_ShowTitle()
  {
    return (EAttribute)titleStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getSortingStyle()
  {
    return sortingStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getSortingStyle_Sorting()
  {
    return (EAttribute)sortingStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getSortingStyle_SortingKeys()
  {
    return (EAttribute)sortingStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getSortingStyle_SortedObjects()
  {
    return (EReference)sortingStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDescriptionStyle()
  {
    return descriptionStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getDescriptionStyle_Description()
  {
    return (EAttribute)descriptionStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getLayoutConstraint()
  {
    return layoutConstraintEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getSize()
  {
    return sizeEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getSize_Width()
  {
    return (EAttribute)sizeEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getSize_Height()
  {
    return (EAttribute)sizeEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getLocation()
  {
    return locationEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getLocation_X()
  {
    return (EAttribute)locationEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getLocation_Y()
  {
    return (EAttribute)locationEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBounds()
  {
    return boundsEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getRatio()
  {
    return ratioEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRatio_Value()
  {
    return (EAttribute)ratioEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getAnchor()
  {
    return anchorEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBendpoints()
  {
    return bendpointsEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getIdentityAnchor()
  {
    return identityAnchorEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getIdentityAnchor_Id()
  {
    return (EAttribute)identityAnchorEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getRoutingStyle()
  {
    return routingStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_Routing()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_Smoothness()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_AvoidObstructions()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_ClosestDistance()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(3);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_JumpLinkStatus()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(4);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_JumpLinkType()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(5);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoutingStyle_JumpLinksReverse()
  {
    return (EAttribute)routingStyleEClass.getEStructuralFeatures().get(6);
  }

  /**
   * Left in for backwards compatibility
   * 
   * @deprecated Use getRoundedCornersStyle_RoundedBendpointsRadius()
   */
  @Deprecated
  @Override
  public EAttribute getRoutingStyle_RoundedBendpointsRadius()
  {
    return getRoundedCornersStyle_RoundedBendpointsRadius();
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getView()
  {
    return viewEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getView_Visible()
  {
    return (EAttribute)viewEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getView_Type()
  {
    return (EAttribute)viewEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getView_Mutable()
  {
    return (EAttribute)viewEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_SourceEdges()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(3);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_TargetEdges()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(4);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_PersistedChildren()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(5);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_Styles()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(6);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_Element()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(7);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_Diagram()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(8);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getView_TransientChildren()
  {
    return (EReference)viewEClass.getEStructuralFeatures().get(9);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getRelativeBendpoints()
  {
    return relativeBendpointsEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRelativeBendpoints_Points()
  {
    return (EAttribute)relativeBendpointsEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDiagram()
  {
    return diagramEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getDiagram_Name()
  {
    return (EAttribute)diagramEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getDiagram_MeasurementUnit()
  {
    return (EAttribute)diagramEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getDiagram_PersistedEdges()
  {
    return (EReference)diagramEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getDiagram_TransientEdges()
  {
    return (EReference)diagramEClass.getEStructuralFeatures().get(3);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getImage()
  {
    return imageEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getImage_Data()
  {
    return (EAttribute)imageEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getCanonicalStyle()
  {
    return canonicalStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getCanonicalStyle_Canonical()
  {
    return (EAttribute)canonicalStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getShapeStyle()
  {
    return shapeStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getConnectorStyle()
  {
    return connectorStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getPageStyle()
  {
    return pageStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getPageStyle_PageX()
  {
    return (EAttribute)pageStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getPageStyle_PageY()
  {
    return (EAttribute)pageStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getPageStyle_PageWidth()
  {
    return (EAttribute)pageStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getPageStyle_PageHeight()
  {
    return (EAttribute)pageStyleEClass.getEStructuralFeatures().get(3);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDrawerStyle()
  {
    return drawerStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getDrawerStyle_Collapsed()
  {
    return (EAttribute)drawerStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getGuideStyle()
  {
    return guideStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getGuideStyle_HorizontalGuides()
  {
    return (EReference)guideStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getGuideStyle_VerticalGuides()
  {
    return (EReference)guideStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getGuide()
  {
    return guideEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getGuide_Position()
  {
    return (EAttribute)guideEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getGuide_NodeMap()
  {
    return (EReference)guideEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getNodeEntry()
  {
    return nodeEntryEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getNodeEntry_Value()
  {
    return (EAttribute)nodeEntryEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getNodeEntry_Key()
  {
    return (EReference)nodeEntryEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getFilteringStyle()
  {
    return filteringStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFilteringStyle_Filtering()
  {
    return (EAttribute)filteringStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getFilteringStyle_FilteringKeys()
  {
    return (EAttribute)filteringStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getFilteringStyle_FilteredObjects()
  {
    return (EReference)filteringStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDiagramStyle()
  {
    return diagramStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getImageStyle()
  {
    return imageStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getImageStyle_AntiAlias()
  {
    return (EAttribute)imageStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getImageStyle_MaintainAspectRatio()
  {
    return (EAttribute)imageStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getImageStyle_CropBound()
  {
    return (EReference)imageStyleEClass.getEStructuralFeatures().get(2);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getImageBufferStyle()
  {
    return imageBufferStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getImageBufferStyle_ImageBuffer()
  {
    return (EReference)imageBufferStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getPropertiesSetStyle()
  {
    return propertiesSetStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getPropertiesSetStyle_PropertiesMap()
  {
    return (EReference)propertiesSetStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getStringToPropertyValueMapEntry()
  {
    return stringToPropertyValueMapEntryEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getStringToPropertyValueMapEntry_Key()
  {
    return (EAttribute)stringToPropertyValueMapEntryEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getStringToPropertyValueMapEntry_Value()
  {
    return (EReference)stringToPropertyValueMapEntryEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getPropertyValue()
  {
    return propertyValueEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getPropertyValue_RawValue()
  {
    return (EAttribute)propertyValueEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getPropertyValue_InstanceType()
  {
    return (EReference)propertyValueEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getSingleValueStyle()
  {
    return singleValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getSingleValueStyle_RawValue()
  {
    return (EAttribute)singleValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getListValueStyle()
  {
    return listValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getListValueStyle_RawValuesList()
  {
    return (EAttribute)listValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getNamedStyle()
  {
    return namedStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getNamedStyle_Name()
  {
    return (EAttribute)namedStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getStringObjectConverter()
  {
    return stringObjectConverterEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDataTypeStyle()
  {
    return dataTypeStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getDataTypeStyle_InstanceType()
  {
    return (EReference)dataTypeStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getIntValueStyle()
  {
    return intValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getIntValueStyle_IntValue()
  {
    return (EAttribute)intValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getIntListValueStyle()
  {
    return intListValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getIntListValueStyle_IntListValue()
  {
    return (EAttribute)intListValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBooleanValueStyle()
  {
    return booleanValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getBooleanValueStyle_BooleanValue()
  {
    return (EAttribute)booleanValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDoubleValueStyle()
  {
    return doubleValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getDoubleValueStyle_DoubleValue()
  {
    return (EAttribute)doubleValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDoubleListValueStyle()
  {
    return doubleListValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getDoubleListValueStyle_DoubleListValue()
  {
    return (EAttribute)doubleListValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getStringValueStyle()
  {
    return stringValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getStringValueStyle_StringValue()
  {
    return (EAttribute)stringValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getStringListValueStyle()
  {
    return stringListValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getStringListValueStyle_StringListValue()
  {
    return (EAttribute)stringListValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getEObjectValueStyle()
  {
    return eObjectValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEObjectValueStyle_EObjectValue()
  {
    return (EReference)eObjectValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getEObjectListValueStyle()
  {
    return eObjectListValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getEObjectListValueStyle_EObjectListValue()
  {
    return (EReference)eObjectListValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getByteArrayValueStyle()
  {
    return byteArrayValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getByteArrayValueStyle_ByteArrayValue()
  {
    return (EAttribute)byteArrayValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBooleanListValueStyle()
  {
    return booleanListValueStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getBooleanListValueStyle_BooleanListValue()
  {
    return (EAttribute)booleanListValueStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getHintedDiagramLinkStyle()
  {
    return hintedDiagramLinkStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getHintedDiagramLinkStyle_Hint()
  {
    return (EAttribute)hintedDiagramLinkStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDiagramLinkStyle()
  {
    return diagramLinkStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getDiagramLinkStyle_DiagramLink()
  {
    return (EReference)diagramLinkStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getMultiDiagramLinkStyle()
  {
    return multiDiagramLinkStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EReference getMultiDiagramLinkStyle_DiagramLinks()
  {
    return (EReference)multiDiagramLinkStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getTextStyle()
  {
    return textStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getTextStyle_TextAlignment()
  {
    return (EAttribute)textStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getLineTypeStyle()
  {
    return lineTypeStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getLineTypeStyle_LineType()
  {
    return (EAttribute)lineTypeStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getArrowStyle()
  {
    return arrowStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getArrowStyle_ArrowSource()
  {
    return (EAttribute)arrowStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getArrowStyle_ArrowTarget()
  {
    return (EAttribute)arrowStyleEClass.getEStructuralFeatures().get(1);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getShape()
  {
    return shapeEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getCompartment()
  {
    return compartmentEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getListCompartment()
  {
    return listCompartmentEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getConnector()
  {
    return connectorEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getStandardDiagram()
  {
    return standardDiagramEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getDecorationNode()
  {
    return decorationNodeEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBasicDecorationNode()
  {
    return basicDecorationNodeEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBasicCompartment()
  {
    return basicCompartmentEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getBasicSemanticCompartment()
  {
    return basicSemanticCompartmentEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getSemanticListCompartment()
  {
    return semanticListCompartmentEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EClass getRoundedCornersStyle()
  {
    return roundedCornersStyleEClass;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EAttribute getRoundedCornersStyle_RoundedBendpointsRadius()
  {
    return (EAttribute)roundedCornersStyleEClass.getEStructuralFeatures().get(0);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getSorting()
  {
    return sortingEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getFiltering()
  {
    return filteringEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getRouting()
  {
    return routingEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getSmoothness()
  {
    return smoothnessEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getJumpLinkStatus()
  {
    return jumpLinkStatusEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getJumpLinkType()
  {
    return jumpLinkTypeEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getAlignment()
  {
    return alignmentEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getSortingDirection()
  {
    return sortingDirectionEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getMeasurementUnit()
  {
    return measurementUnitEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getTextAlignment()
  {
    return textAlignmentEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getLineType()
  {
    return lineTypeEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getArrowType()
  {
    return arrowTypeEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EEnum getGradientStyle()
  {
    return gradientStyleEEnum;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EDataType getRelativeBendpointList()
  {
    return relativeBendpointListEDataType;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EDataType getFilterKeyList()
  {
    return filterKeyListEDataType;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EDataType getSortKeyMap()
  {
    return sortKeyMapEDataType;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public EDataType getGradientData()
  {
    return gradientDataEDataType;
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  @Override
  public NotationFactory getNotationFactory()
  {
    return (NotationFactory)getEFactoryInstance();
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private boolean isCreated = false;

  /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!--
   * end-user-doc -->
     * @generated
     */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    edgeEClass = createEClass(EDGE);
    createEReference(edgeEClass, EDGE__SOURCE);
    createEReference(edgeEClass, EDGE__TARGET);
    createEReference(edgeEClass, EDGE__BENDPOINTS);
    createEReference(edgeEClass, EDGE__SOURCE_ANCHOR);
    createEReference(edgeEClass, EDGE__TARGET_ANCHOR);

    nodeEClass = createEClass(NODE);
    createEReference(nodeEClass, NODE__LAYOUT_CONSTRAINT);

    styleEClass = createEClass(STYLE);

    fillStyleEClass = createEClass(FILL_STYLE);
    createEAttribute(fillStyleEClass, FILL_STYLE__FILL_COLOR);
    createEAttribute(fillStyleEClass, FILL_STYLE__TRANSPARENCY);
    createEAttribute(fillStyleEClass, FILL_STYLE__GRADIENT);

    lineStyleEClass = createEClass(LINE_STYLE);
    createEAttribute(lineStyleEClass, LINE_STYLE__LINE_COLOR);
    createEAttribute(lineStyleEClass, LINE_STYLE__LINE_WIDTH);

    fontStyleEClass = createEClass(FONT_STYLE);
    createEAttribute(fontStyleEClass, FONT_STYLE__FONT_COLOR);
    createEAttribute(fontStyleEClass, FONT_STYLE__FONT_NAME);
    createEAttribute(fontStyleEClass, FONT_STYLE__FONT_HEIGHT);
    createEAttribute(fontStyleEClass, FONT_STYLE__BOLD);
    createEAttribute(fontStyleEClass, FONT_STYLE__ITALIC);
    createEAttribute(fontStyleEClass, FONT_STYLE__UNDERLINE);
    createEAttribute(fontStyleEClass, FONT_STYLE__STRIKE_THROUGH);

    titleStyleEClass = createEClass(TITLE_STYLE);
    createEAttribute(titleStyleEClass, TITLE_STYLE__SHOW_TITLE);

    sortingStyleEClass = createEClass(SORTING_STYLE);
    createEAttribute(sortingStyleEClass, SORTING_STYLE__SORTING);
    createEAttribute(sortingStyleEClass, SORTING_STYLE__SORTING_KEYS);
    createEReference(sortingStyleEClass, SORTING_STYLE__SORTED_OBJECTS);

    descriptionStyleEClass = createEClass(DESCRIPTION_STYLE);
    createEAttribute(descriptionStyleEClass, DESCRIPTION_STYLE__DESCRIPTION);

    layoutConstraintEClass = createEClass(LAYOUT_CONSTRAINT);

    sizeEClass = createEClass(SIZE);
    createEAttribute(sizeEClass, SIZE__WIDTH);
    createEAttribute(sizeEClass, SIZE__HEIGHT);

    locationEClass = createEClass(LOCATION);
    createEAttribute(locationEClass, LOCATION__X);
    createEAttribute(locationEClass, LOCATION__Y);

    boundsEClass = createEClass(BOUNDS);

    ratioEClass = createEClass(RATIO);
    createEAttribute(ratioEClass, RATIO__VALUE);

    anchorEClass = createEClass(ANCHOR);

    bendpointsEClass = createEClass(BENDPOINTS);

    identityAnchorEClass = createEClass(IDENTITY_ANCHOR);
    createEAttribute(identityAnchorEClass, IDENTITY_ANCHOR__ID);

    routingStyleEClass = createEClass(ROUTING_STYLE);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__ROUTING);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__SMOOTHNESS);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__AVOID_OBSTRUCTIONS);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__CLOSEST_DISTANCE);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__JUMP_LINK_STATUS);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__JUMP_LINK_TYPE);
    createEAttribute(routingStyleEClass, ROUTING_STYLE__JUMP_LINKS_REVERSE);

    viewEClass = createEClass(VIEW);
    createEAttribute(viewEClass, VIEW__VISIBLE);
    createEAttribute(viewEClass, VIEW__TYPE);
    createEAttribute(viewEClass, VIEW__MUTABLE);
    createEReference(viewEClass, VIEW__SOURCE_EDGES);
    createEReference(viewEClass, VIEW__TARGET_EDGES);
    createEReference(viewEClass, VIEW__PERSISTED_CHILDREN);
    createEReference(viewEClass, VIEW__STYLES);
    createEReference(viewEClass, VIEW__ELEMENT);
    createEReference(viewEClass, VIEW__DIAGRAM);
    createEReference(viewEClass, VIEW__TRANSIENT_CHILDREN);

    relativeBendpointsEClass = createEClass(RELATIVE_BENDPOINTS);
    createEAttribute(relativeBendpointsEClass, RELATIVE_BENDPOINTS__POINTS);

    diagramEClass = createEClass(DIAGRAM);
    createEAttribute(diagramEClass, DIAGRAM__NAME);
    createEAttribute(diagramEClass, DIAGRAM__MEASUREMENT_UNIT);
    createEReference(diagramEClass, DIAGRAM__PERSISTED_EDGES);
    createEReference(diagramEClass, DIAGRAM__TRANSIENT_EDGES);

    imageEClass = createEClass(IMAGE);
    createEAttribute(imageEClass, IMAGE__DATA);

    canonicalStyleEClass = createEClass(CANONICAL_STYLE);
    createEAttribute(canonicalStyleEClass, CANONICAL_STYLE__CANONICAL);

    shapeStyleEClass = createEClass(SHAPE_STYLE);

    connectorStyleEClass = createEClass(CONNECTOR_STYLE);

    pageStyleEClass = createEClass(PAGE_STYLE);
    createEAttribute(pageStyleEClass, PAGE_STYLE__PAGE_X);
    createEAttribute(pageStyleEClass, PAGE_STYLE__PAGE_Y);
    createEAttribute(pageStyleEClass, PAGE_STYLE__PAGE_WIDTH);
    createEAttribute(pageStyleEClass, PAGE_STYLE__PAGE_HEIGHT);

    drawerStyleEClass = createEClass(DRAWER_STYLE);
    createEAttribute(drawerStyleEClass, DRAWER_STYLE__COLLAPSED);

    guideStyleEClass = createEClass(GUIDE_STYLE);
    createEReference(guideStyleEClass, GUIDE_STYLE__HORIZONTAL_GUIDES);
    createEReference(guideStyleEClass, GUIDE_STYLE__VERTICAL_GUIDES);

    guideEClass = createEClass(GUIDE);
    createEAttribute(guideEClass, GUIDE__POSITION);
    createEReference(guideEClass, GUIDE__NODE_MAP);

    nodeEntryEClass = createEClass(NODE_ENTRY);
    createEAttribute(nodeEntryEClass, NODE_ENTRY__VALUE);
    createEReference(nodeEntryEClass, NODE_ENTRY__KEY);

    filteringStyleEClass = createEClass(FILTERING_STYLE);
    createEAttribute(filteringStyleEClass, FILTERING_STYLE__FILTERING);
    createEAttribute(filteringStyleEClass, FILTERING_STYLE__FILTERING_KEYS);
    createEReference(filteringStyleEClass, FILTERING_STYLE__FILTERED_OBJECTS);

    diagramStyleEClass = createEClass(DIAGRAM_STYLE);

    imageStyleEClass = createEClass(IMAGE_STYLE);
    createEAttribute(imageStyleEClass, IMAGE_STYLE__ANTI_ALIAS);
    createEAttribute(imageStyleEClass, IMAGE_STYLE__MAINTAIN_ASPECT_RATIO);
    createEReference(imageStyleEClass, IMAGE_STYLE__CROP_BOUND);

    imageBufferStyleEClass = createEClass(IMAGE_BUFFER_STYLE);
    createEReference(imageBufferStyleEClass, IMAGE_BUFFER_STYLE__IMAGE_BUFFER);

    propertiesSetStyleEClass = createEClass(PROPERTIES_SET_STYLE);
    createEReference(propertiesSetStyleEClass, PROPERTIES_SET_STYLE__PROPERTIES_MAP);

    stringToPropertyValueMapEntryEClass = createEClass(STRING_TO_PROPERTY_VALUE_MAP_ENTRY);
    createEAttribute(stringToPropertyValueMapEntryEClass, STRING_TO_PROPERTY_VALUE_MAP_ENTRY__KEY);
    createEReference(stringToPropertyValueMapEntryEClass, STRING_TO_PROPERTY_VALUE_MAP_ENTRY__VALUE);

    propertyValueEClass = createEClass(PROPERTY_VALUE);
    createEAttribute(propertyValueEClass, PROPERTY_VALUE__RAW_VALUE);
    createEReference(propertyValueEClass, PROPERTY_VALUE__INSTANCE_TYPE);

    singleValueStyleEClass = createEClass(SINGLE_VALUE_STYLE);
    createEAttribute(singleValueStyleEClass, SINGLE_VALUE_STYLE__RAW_VALUE);

    listValueStyleEClass = createEClass(LIST_VALUE_STYLE);
    createEAttribute(listValueStyleEClass, LIST_VALUE_STYLE__RAW_VALUES_LIST);

    namedStyleEClass = createEClass(NAMED_STYLE);
    createEAttribute(namedStyleEClass, NAMED_STYLE__NAME);

    stringObjectConverterEClass = createEClass(STRING_OBJECT_CONVERTER);

    dataTypeStyleEClass = createEClass(DATA_TYPE_STYLE);
    createEReference(dataTypeStyleEClass, DATA_TYPE_STYLE__INSTANCE_TYPE);

    intValueStyleEClass = createEClass(INT_VALUE_STYLE);
    createEAttribute(intValueStyleEClass, INT_VALUE_STYLE__INT_VALUE);

    intListValueStyleEClass = createEClass(INT_LIST_VALUE_STYLE);
    createEAttribute(intListValueStyleEClass, INT_LIST_VALUE_STYLE__INT_LIST_VALUE);

    booleanValueStyleEClass = createEClass(BOOLEAN_VALUE_STYLE);
    createEAttribute(booleanValueStyleEClass, BOOLEAN_VALUE_STYLE__BOOLEAN_VALUE);

    doubleValueStyleEClass = createEClass(DOUBLE_VALUE_STYLE);
    createEAttribute(doubleValueStyleEClass, DOUBLE_VALUE_STYLE__DOUBLE_VALUE);

    doubleListValueStyleEClass = createEClass(DOUBLE_LIST_VALUE_STYLE);
    createEAttribute(doubleListValueStyleEClass, DOUBLE_LIST_VALUE_STYLE__DOUBLE_LIST_VALUE);

    stringValueStyleEClass = createEClass(STRING_VALUE_STYLE);
    createEAttribute(stringValueStyleEClass, STRING_VALUE_STYLE__STRING_VALUE);

    stringListValueStyleEClass = createEClass(STRING_LIST_VALUE_STYLE);
    createEAttribute(stringListValueStyleEClass, STRING_LIST_VALUE_STYLE__STRING_LIST_VALUE);

    eObjectValueStyleEClass = createEClass(EOBJECT_VALUE_STYLE);
    createEReference(eObjectValueStyleEClass, EOBJECT_VALUE_STYLE__EOBJECT_VALUE);

    eObjectListValueStyleEClass = createEClass(EOBJECT_LIST_VALUE_STYLE);
    createEReference(eObjectListValueStyleEClass, EOBJECT_LIST_VALUE_STYLE__EOBJECT_LIST_VALUE);

    byteArrayValueStyleEClass = createEClass(BYTE_ARRAY_VALUE_STYLE);
    createEAttribute(byteArrayValueStyleEClass, BYTE_ARRAY_VALUE_STYLE__BYTE_ARRAY_VALUE);

    booleanListValueStyleEClass = createEClass(BOOLEAN_LIST_VALUE_STYLE);
    createEAttribute(booleanListValueStyleEClass, BOOLEAN_LIST_VALUE_STYLE__BOOLEAN_LIST_VALUE);

    hintedDiagramLinkStyleEClass = createEClass(HINTED_DIAGRAM_LINK_STYLE);
    createEAttribute(hintedDiagramLinkStyleEClass, HINTED_DIAGRAM_LINK_STYLE__HINT);

    diagramLinkStyleEClass = createEClass(DIAGRAM_LINK_STYLE);
    createEReference(diagramLinkStyleEClass, DIAGRAM_LINK_STYLE__DIAGRAM_LINK);

    multiDiagramLinkStyleEClass = createEClass(MULTI_DIAGRAM_LINK_STYLE);
    createEReference(multiDiagramLinkStyleEClass, MULTI_DIAGRAM_LINK_STYLE__DIAGRAM_LINKS);

    textStyleEClass = createEClass(TEXT_STYLE);
    createEAttribute(textStyleEClass, TEXT_STYLE__TEXT_ALIGNMENT);

    lineTypeStyleEClass = createEClass(LINE_TYPE_STYLE);
    createEAttribute(lineTypeStyleEClass, LINE_TYPE_STYLE__LINE_TYPE);

    arrowStyleEClass = createEClass(ARROW_STYLE);
    createEAttribute(arrowStyleEClass, ARROW_STYLE__ARROW_SOURCE);
    createEAttribute(arrowStyleEClass, ARROW_STYLE__ARROW_TARGET);

    shapeEClass = createEClass(SHAPE);

    compartmentEClass = createEClass(COMPARTMENT);

    listCompartmentEClass = createEClass(LIST_COMPARTMENT);

    connectorEClass = createEClass(CONNECTOR);

    standardDiagramEClass = createEClass(STANDARD_DIAGRAM);

    decorationNodeEClass = createEClass(DECORATION_NODE);

    basicDecorationNodeEClass = createEClass(BASIC_DECORATION_NODE);

    basicCompartmentEClass = createEClass(BASIC_COMPARTMENT);

    basicSemanticCompartmentEClass = createEClass(BASIC_SEMANTIC_COMPARTMENT);

    semanticListCompartmentEClass = createEClass(SEMANTIC_LIST_COMPARTMENT);

    roundedCornersStyleEClass = createEClass(ROUNDED_CORNERS_STYLE);
    createEAttribute(roundedCornersStyleEClass, ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS);

    // Create enums
    sortingEEnum = createEEnum(SORTING);
    filteringEEnum = createEEnum(FILTERING);
    routingEEnum = createEEnum(ROUTING);
    smoothnessEEnum = createEEnum(SMOOTHNESS);
    jumpLinkStatusEEnum = createEEnum(JUMP_LINK_STATUS);
    jumpLinkTypeEEnum = createEEnum(JUMP_LINK_TYPE);
    alignmentEEnum = createEEnum(ALIGNMENT);
    sortingDirectionEEnum = createEEnum(SORTING_DIRECTION);
    measurementUnitEEnum = createEEnum(MEASUREMENT_UNIT);
    textAlignmentEEnum = createEEnum(TEXT_ALIGNMENT);
    lineTypeEEnum = createEEnum(LINE_TYPE);
    arrowTypeEEnum = createEEnum(ARROW_TYPE);
    gradientStyleEEnum = createEEnum(GRADIENT_STYLE);

    // Create data types
    relativeBendpointListEDataType = createEDataType(RELATIVE_BENDPOINT_LIST);
    filterKeyListEDataType = createEDataType(FILTER_KEY_LIST);
    sortKeyMapEDataType = createEDataType(SORT_KEY_MAP);
    gradientDataEDataType = createEDataType(GRADIENT_DATA);
  }

  /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is
   * guarded to have no affect on any invocation but its first. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

    // Add supertypes to classes
    edgeEClass.getESuperTypes().add(getView());
    nodeEClass.getESuperTypes().add(getView());
    fillStyleEClass.getESuperTypes().add(getStyle());
    lineStyleEClass.getESuperTypes().add(getStyle());
    fontStyleEClass.getESuperTypes().add(getStyle());
    titleStyleEClass.getESuperTypes().add(getStyle());
    sortingStyleEClass.getESuperTypes().add(getStyle());
    descriptionStyleEClass.getESuperTypes().add(getStyle());
    sizeEClass.getESuperTypes().add(getLayoutConstraint());
    locationEClass.getESuperTypes().add(getLayoutConstraint());
    boundsEClass.getESuperTypes().add(getLocation());
    boundsEClass.getESuperTypes().add(getSize());
    ratioEClass.getESuperTypes().add(getLayoutConstraint());
    identityAnchorEClass.getESuperTypes().add(getAnchor());
    routingStyleEClass.getESuperTypes().add(getRoundedCornersStyle());
    viewEClass.getESuperTypes().add(theEcorePackage.getEModelElement());
    relativeBendpointsEClass.getESuperTypes().add(getBendpoints());
    diagramEClass.getESuperTypes().add(getView());
    canonicalStyleEClass.getESuperTypes().add(getStyle());
    shapeStyleEClass.getESuperTypes().add(getFontStyle());
    shapeStyleEClass.getESuperTypes().add(getDescriptionStyle());
    shapeStyleEClass.getESuperTypes().add(getFillStyle());
    shapeStyleEClass.getESuperTypes().add(getLineStyle());
    shapeStyleEClass.getESuperTypes().add(getRoundedCornersStyle());
    connectorStyleEClass.getESuperTypes().add(getRoutingStyle());
    connectorStyleEClass.getESuperTypes().add(getLineStyle());
    pageStyleEClass.getESuperTypes().add(getStyle());
    drawerStyleEClass.getESuperTypes().add(getStyle());
    guideStyleEClass.getESuperTypes().add(getStyle());
    filteringStyleEClass.getESuperTypes().add(getStyle());
    diagramStyleEClass.getESuperTypes().add(getPageStyle());
    diagramStyleEClass.getESuperTypes().add(getGuideStyle());
    diagramStyleEClass.getESuperTypes().add(getDescriptionStyle());
    imageStyleEClass.getESuperTypes().add(getStyle());
    imageBufferStyleEClass.getESuperTypes().add(getImageStyle());
    propertiesSetStyleEClass.getESuperTypes().add(getNamedStyle());
    propertyValueEClass.getESuperTypes().add(getStringObjectConverter());
    singleValueStyleEClass.getESuperTypes().add(getDataTypeStyle());
    listValueStyleEClass.getESuperTypes().add(getDataTypeStyle());
    namedStyleEClass.getESuperTypes().add(getStyle());
    dataTypeStyleEClass.getESuperTypes().add(getNamedStyle());
    dataTypeStyleEClass.getESuperTypes().add(getStringObjectConverter());
    intValueStyleEClass.getESuperTypes().add(getNamedStyle());
    intListValueStyleEClass.getESuperTypes().add(getNamedStyle());
    booleanValueStyleEClass.getESuperTypes().add(getNamedStyle());
    doubleValueStyleEClass.getESuperTypes().add(getNamedStyle());
    doubleListValueStyleEClass.getESuperTypes().add(getNamedStyle());
    stringValueStyleEClass.getESuperTypes().add(getNamedStyle());
    stringListValueStyleEClass.getESuperTypes().add(getNamedStyle());
    eObjectValueStyleEClass.getESuperTypes().add(getNamedStyle());
    eObjectListValueStyleEClass.getESuperTypes().add(getNamedStyle());
    byteArrayValueStyleEClass.getESuperTypes().add(getNamedStyle());
    booleanListValueStyleEClass.getESuperTypes().add(getNamedStyle());
    hintedDiagramLinkStyleEClass.getESuperTypes().add(getDiagramLinkStyle());
    hintedDiagramLinkStyleEClass.getESuperTypes().add(getStyle());
    diagramLinkStyleEClass.getESuperTypes().add(getStyle());
    multiDiagramLinkStyleEClass.getESuperTypes().add(getStyle());
    textStyleEClass.getESuperTypes().add(getStyle());
    lineTypeStyleEClass.getESuperTypes().add(getStyle());
    arrowStyleEClass.getESuperTypes().add(getStyle());
    shapeEClass.getESuperTypes().add(getNode());
    shapeEClass.getESuperTypes().add(getShapeStyle());
    compartmentEClass.getESuperTypes().add(getBasicCompartment());
    compartmentEClass.getESuperTypes().add(getCanonicalStyle());
    compartmentEClass.getESuperTypes().add(getTitleStyle());
    listCompartmentEClass.getESuperTypes().add(getBasicCompartment());
    listCompartmentEClass.getESuperTypes().add(getSortingStyle());
    listCompartmentEClass.getESuperTypes().add(getFilteringStyle());
    listCompartmentEClass.getESuperTypes().add(getTitleStyle());
    connectorEClass.getESuperTypes().add(getEdge());
    connectorEClass.getESuperTypes().add(getConnectorStyle());
    standardDiagramEClass.getESuperTypes().add(getDiagram());
    standardDiagramEClass.getESuperTypes().add(getDiagramStyle());
    decorationNodeEClass.getESuperTypes().add(getBasicDecorationNode());
    basicDecorationNodeEClass.getESuperTypes().add(getNode());
    basicCompartmentEClass.getESuperTypes().add(getDecorationNode());
    basicCompartmentEClass.getESuperTypes().add(getDrawerStyle());
    basicSemanticCompartmentEClass.getESuperTypes().add(getBasicDecorationNode());
    basicSemanticCompartmentEClass.getESuperTypes().add(getDrawerStyle());
    semanticListCompartmentEClass.getESuperTypes().add(getBasicSemanticCompartment());
    semanticListCompartmentEClass.getESuperTypes().add(getSortingStyle());
    semanticListCompartmentEClass.getESuperTypes().add(getFilteringStyle());
    semanticListCompartmentEClass.getESuperTypes().add(getTitleStyle());
    roundedCornersStyleEClass.getESuperTypes().add(getStyle());

    // Initialize classes and features; add operations and parameters
    initEClass(edgeEClass, Edge.class, "Edge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getEdge_Source(), getView(), getView_SourceEdges(), "source", null, 1, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdge_Target(), getView(), getView_TargetEdges(), "target", null, 1, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdge_Bendpoints(), getBendpoints(), null, "bendpoints", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdge_SourceAnchor(), getAnchor(), null, "sourceAnchor", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdge_TargetAnchor(), getAnchor(), null, "targetAnchor", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    EOperation op = addEOperation(edgeEClass, getBendpoints(), "createBendpoints", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    op = addEOperation(edgeEClass, getAnchor(), "createSourceAnchor", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    op = addEOperation(edgeEClass, getAnchor(), "createTargetAnchor", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    initEClass(nodeEClass, Node.class, "Node", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getNode_LayoutConstraint(), getLayoutConstraint(), null, "layoutConstraint", null, 0, 1, Node.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    op = addEOperation(nodeEClass, getLayoutConstraint(), "createLayoutConstraint", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    initEClass(styleEClass, Style.class, "Style", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(fillStyleEClass, FillStyle.class, "FillStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getFillStyle_FillColor(), ecorePackage.getEInt(), "fillColor", "16777215", 0, 1, FillStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFillStyle_Transparency(), ecorePackage.getEInt(), "transparency", "-1", 0, 1, FillStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFillStyle_Gradient(), getGradientData(), "gradient", null, 0, 1, FillStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(lineStyleEClass, LineStyle.class, "LineStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getLineStyle_LineColor(), ecorePackage.getEInt(), "lineColor", "11579568", 0, 1, LineStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLineStyle_LineWidth(), theEcorePackage.getEInt(), "lineWidth", "-1", 0, 1, LineStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(fontStyleEClass, FontStyle.class, "FontStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getFontStyle_FontColor(), ecorePackage.getEInt(), "fontColor", "0", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFontStyle_FontName(), ecorePackage.getEString(), "fontName", "Tahoma", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFontStyle_FontHeight(), ecorePackage.getEInt(), "fontHeight", "9", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFontStyle_Bold(), ecorePackage.getEBoolean(), "bold", "false", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFontStyle_Italic(), ecorePackage.getEBoolean(), "italic", "false", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFontStyle_Underline(), ecorePackage.getEBoolean(), "underline", "false", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFontStyle_StrikeThrough(), ecorePackage.getEBoolean(), "strikeThrough", "false", 0, 1, FontStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(titleStyleEClass, TitleStyle.class, "TitleStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getTitleStyle_ShowTitle(), ecorePackage.getEBoolean(), "showTitle", "false", 0, 1, TitleStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sortingStyleEClass, SortingStyle.class, "SortingStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getSortingStyle_Sorting(), getSorting(), "sorting", "None", 0, 1, SortingStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSortingStyle_SortingKeys(), getSortKeyMap(), "sortingKeys", null, 0, 1, SortingStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSortingStyle_SortedObjects(), theEcorePackage.getEObject(), null, "sortedObjects", null, 0, -1, SortingStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(descriptionStyleEClass, DescriptionStyle.class, "DescriptionStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getDescriptionStyle_Description(), ecorePackage.getEString(), "description", "", 0, 1, DescriptionStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(layoutConstraintEClass, LayoutConstraint.class, "LayoutConstraint", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(sizeEClass, Size.class, "Size", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getSize_Width(), ecorePackage.getEInt(), "width", "-1", 0, 1, Size.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, //$NON-NLS-1$ //$NON-NLS-2$
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSize_Height(), ecorePackage.getEInt(), "height", "-1", 0, 1, Size.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(locationEClass, Location.class, "Location", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getLocation_X(), ecorePackage.getEInt(), "x", "0", 0, 1, Location.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, //$NON-NLS-1$ //$NON-NLS-2$
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLocation_Y(), ecorePackage.getEInt(), "y", "0", 0, 1, Location.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, //$NON-NLS-1$ //$NON-NLS-2$
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(boundsEClass, Bounds.class, "Bounds", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(ratioEClass, Ratio.class, "Ratio", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getRatio_Value(), ecorePackage.getEDouble(), "value", "-1", 0, 1, Ratio.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(anchorEClass, Anchor.class, "Anchor", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(bendpointsEClass, Bendpoints.class, "Bendpoints", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(identityAnchorEClass, IdentityAnchor.class, "IdentityAnchor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getIdentityAnchor_Id(), ecorePackage.getEString(), "id", "anchor", 0, 1, IdentityAnchor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(routingStyleEClass, RoutingStyle.class, "RoutingStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getRoutingStyle_Routing(), getRouting(), "routing", "Manual", 0, 1, RoutingStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRoutingStyle_Smoothness(), getSmoothness(), "smoothness", "None", 0, 1, RoutingStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRoutingStyle_AvoidObstructions(), ecorePackage.getEBoolean(), "avoidObstructions", "false", 0, 1, RoutingStyle.class, !IS_TRANSIENT, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRoutingStyle_ClosestDistance(), ecorePackage.getEBoolean(), "closestDistance", "false", 0, 1, RoutingStyle.class, !IS_TRANSIENT, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRoutingStyle_JumpLinkStatus(), getJumpLinkStatus(), "jumpLinkStatus", "None", 0, 1, RoutingStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRoutingStyle_JumpLinkType(), getJumpLinkType(), "jumpLinkType", "Semicircle", 0, 1, RoutingStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRoutingStyle_JumpLinksReverse(), ecorePackage.getEBoolean(), "jumpLinksReverse", "false", 0, 1, RoutingStyle.class, !IS_TRANSIENT, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(viewEClass, View.class, "View", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getView_Visible(), ecorePackage.getEBoolean(), "visible", "true", 0, 1, View.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getView_Type(), ecorePackage.getEString(), "type", "", 0, 1, View.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, //$NON-NLS-1$ //$NON-NLS-2$
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getView_Mutable(), ecorePackage.getEBoolean(), "mutable", "false", 0, 1, View.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_SourceEdges(), getEdge(), getEdge_Source(), "sourceEdges", null, 0, -1, View.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_TargetEdges(), getEdge(), getEdge_Target(), "targetEdges", null, 0, -1, View.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_PersistedChildren(), getNode(), null, "children", null, 0, -1, View.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_PersistedChildren(), getNode(), null, "children", null, 0, -1, View.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_Styles(), getStyle(), null, "styles", null, 0, -1, View.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_Element(), theEcorePackage.getEObject(), null, "element", null, 0, 1, View.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_Diagram(), getDiagram(), null, "diagram", null, 1, 1, View.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, //$NON-NLS-1$
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getView_TransientChildren(), getNode(), null, "transientChildren", null, 0, -1, View.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    op = addEOperation(viewEClass, getStyle(), "getStyle", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    op = addEOperation(viewEClass, getNode(), "createChild", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    op = addEOperation(viewEClass, getStyle(), "createStyle", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    op = addEOperation(viewEClass, getNamedStyle(), "getNamedStyle", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "name", 0, 1); //$NON-NLS-1$

    initEClass(relativeBendpointsEClass, RelativeBendpoints.class, "RelativeBendpoints", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getRelativeBendpoints_Points(), getRelativeBendpointList(), "points", null, 0, 1, RelativeBendpoints.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(diagramEClass, Diagram.class, "Diagram", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getDiagram_Name(), ecorePackage.getEString(), "name", "", 0, 1, Diagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDiagram_MeasurementUnit(), getMeasurementUnit(), "measurementUnit", "Himetric", 0, 1, Diagram.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDiagram_PersistedEdges(), getEdge(), null, "edges", null, 0, -1, Diagram.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDiagram_TransientEdges(), getEdge(), null, "TransientEdges", null, 0, -1, Diagram.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    op = addEOperation(diagramEClass, getEdge(), "createEdge", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEClass(), "eClass", 0, 1); //$NON-NLS-1$

    initEClass(imageEClass, Image.class, "Image", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getImage_Data(), theEcorePackage.getEByteArray(), "data", null, 0, 1, Image.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(canonicalStyleEClass, CanonicalStyle.class, "CanonicalStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getCanonicalStyle_Canonical(), ecorePackage.getEBoolean(), "canonical", "true", 0, 1, CanonicalStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(shapeStyleEClass, ShapeStyle.class, "ShapeStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(connectorStyleEClass, ConnectorStyle.class, "ConnectorStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(pageStyleEClass, PageStyle.class, "PageStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getPageStyle_PageX(), ecorePackage.getEInt(), "pageX", "0", 0, 1, PageStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPageStyle_PageY(), ecorePackage.getEInt(), "pageY", "0", 0, 1, PageStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPageStyle_PageWidth(), ecorePackage.getEInt(), "pageWidth", "100", 0, 1, PageStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPageStyle_PageHeight(), ecorePackage.getEInt(), "pageHeight", "100", 0, 1, PageStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(drawerStyleEClass, DrawerStyle.class, "DrawerStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getDrawerStyle_Collapsed(), ecorePackage.getEBoolean(), "collapsed", "false", 0, 1, DrawerStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(guideStyleEClass, GuideStyle.class, "GuideStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getGuideStyle_HorizontalGuides(), getGuide(), null, "horizontalGuides", null, 0, -1, GuideStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGuideStyle_VerticalGuides(), getGuide(), null, "verticalGuides", null, 0, -1, GuideStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(guideEClass, Guide.class, "Guide", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getGuide_Position(), ecorePackage.getEInt(), "position", "0", 0, 1, Guide.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGuide_NodeMap(), getNodeEntry(), null, "nodeMap", null, 0, -1, Guide.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, //$NON-NLS-1$
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nodeEntryEClass, Map.Entry.class, "NodeEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getNodeEntry_Value(), getAlignment(), "value", "Center", 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getNodeEntry_Key(), getNode(), null, "key", null, 1, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, //$NON-NLS-1$
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(filteringStyleEClass, FilteringStyle.class, "FilteringStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getFilteringStyle_Filtering(), getFiltering(), "filtering", "None", 0, 1, FilteringStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFilteringStyle_FilteringKeys(), getFilterKeyList(), "filteringKeys", null, 0, 1, FilteringStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFilteringStyle_FilteredObjects(), theEcorePackage.getEObject(), null, "filteredObjects", null, 0, -1, FilteringStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(diagramStyleEClass, DiagramStyle.class, "DiagramStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(imageStyleEClass, ImageStyle.class, "ImageStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getImageStyle_AntiAlias(), ecorePackage.getEBooleanObject(), "antiAlias", "true", 0, 1, ImageStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$ //$NON-NLS-2$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getImageStyle_MaintainAspectRatio(), ecorePackage.getEBooleanObject(), "maintainAspectRatio", "true", 0, 1, ImageStyle.class, !IS_TRANSIENT, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getImageStyle_CropBound(), getBounds(), null, "cropBound", null, 1, 1, ImageStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(imageBufferStyleEClass, ImageBufferStyle.class, "ImageBufferStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getImageBufferStyle_ImageBuffer(), getImage(), null, "imageBuffer", null, 1, 1, ImageBufferStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertiesSetStyleEClass, PropertiesSetStyle.class, "PropertiesSetStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getPropertiesSetStyle_PropertiesMap(), getStringToPropertyValueMapEntry(), null, "propertiesMap", null, 0, -1, PropertiesSetStyle.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    op = addEOperation(propertiesSetStyleEClass, theEcorePackage.getEJavaObject(), "getProperty", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "propertyName", 0, 1); //$NON-NLS-1$

    op = addEOperation(propertiesSetStyleEClass, theEcorePackage.getEBoolean(), "setProperty", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "propertyName", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEJavaObject(), "newValue", 0, 1); //$NON-NLS-1$

    op = addEOperation(propertiesSetStyleEClass, theEcorePackage.getEBoolean(), "createProperty", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "propertyName", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEDataType(), "instanceType", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEJavaObject(), "initialValue", 0, 1); //$NON-NLS-1$

    op = addEOperation(propertiesSetStyleEClass, theEcorePackage.getEBoolean(), "removeProperty", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "propertyName", 0, 1); //$NON-NLS-1$

    op = addEOperation(propertiesSetStyleEClass, theEcorePackage.getEBoolean(), "hasProperty", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "propertyName", 0, 1); //$NON-NLS-1$

    op = addEOperation(propertiesSetStyleEClass, theEcorePackage.getEBoolean(), "createProperty", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "propertyName", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "initialValue", 0, 1); //$NON-NLS-1$

    initEClass(stringToPropertyValueMapEntryEClass, Map.Entry.class, "StringToPropertyValueMapEntry", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringToPropertyValueMapEntry_Key(), theEcorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStringToPropertyValueMapEntry_Value(), getPropertyValue(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertyValueEClass, PropertyValue.class, "PropertyValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getPropertyValue_RawValue(), ecorePackage.getEString(), "rawValue", null, 0, 1, PropertyValue.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertyValue_InstanceType(), theEcorePackage.getEDataType(), null, "instanceType", null, 0, 1, PropertyValue.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(propertyValueEClass, theEcorePackage.getEJavaObject(), "getValue", 0, 1); //$NON-NLS-1$

    op = addEOperation(propertyValueEClass, null, "setValue"); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEJavaObject(), "newValue", 0, 1); //$NON-NLS-1$

    initEClass(singleValueStyleEClass, SingleValueStyle.class, "SingleValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getSingleValueStyle_RawValue(), ecorePackage.getEString(), "rawValue", null, 0, 1, SingleValueStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(singleValueStyleEClass, theEcorePackage.getEJavaObject(), "getValue", 0, 1); //$NON-NLS-1$

    op = addEOperation(singleValueStyleEClass, null, "setValue"); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEJavaObject(), "newValue", 0, 1); //$NON-NLS-1$

    initEClass(listValueStyleEClass, ListValueStyle.class, "ListValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getListValueStyle_RawValuesList(), theEcorePackage.getEString(), "rawValuesList", null, 0, -1, ListValueStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(namedStyleEClass, NamedStyle.class, "NamedStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getNamedStyle_Name(), ecorePackage.getEString(), "name", null, 0, 1, NamedStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringObjectConverterEClass, StringObjectConverter.class, "StringObjectConverter", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    op = addEOperation(stringObjectConverterEClass, theEcorePackage.getEString(), "getStringFromObject", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEJavaObject(), "objectValue", 0, 1); //$NON-NLS-1$

    op = addEOperation(stringObjectConverterEClass, theEcorePackage.getEJavaObject(), "getObjectFromString", 0, 1); //$NON-NLS-1$
    addEParameter(op, theEcorePackage.getEString(), "stringValue", 0, 1); //$NON-NLS-1$

    initEClass(dataTypeStyleEClass, DataTypeStyle.class, "DataTypeStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getDataTypeStyle_InstanceType(), theEcorePackage.getEDataType(), null, "instanceType", null, 0, 1, DataTypeStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(intValueStyleEClass, IntValueStyle.class, "IntValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getIntValueStyle_IntValue(), theEcorePackage.getEInt(), "intValue", null, 0, 1, IntValueStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(intListValueStyleEClass, IntListValueStyle.class, "IntListValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getIntListValueStyle_IntListValue(), theEcorePackage.getEInt(), "intListValue", null, 0, -1, IntListValueStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(booleanValueStyleEClass, BooleanValueStyle.class, "BooleanValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getBooleanValueStyle_BooleanValue(), theEcorePackage.getEBoolean(), "booleanValue", null, 0, 1, BooleanValueStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(doubleValueStyleEClass, DoubleValueStyle.class, "DoubleValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getDoubleValueStyle_DoubleValue(), theEcorePackage.getEDouble(), "doubleValue", null, 0, 1, DoubleValueStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(doubleListValueStyleEClass, DoubleListValueStyle.class, "DoubleListValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getDoubleListValueStyle_DoubleListValue(), theEcorePackage.getEDoubleObject(), "doubleListValue", null, 0, -1, DoubleListValueStyle.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringValueStyleEClass, StringValueStyle.class, "StringValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getStringValueStyle_StringValue(), theEcorePackage.getEString(), "stringValue", null, 0, 1, StringValueStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringListValueStyleEClass, StringListValueStyle.class, "StringListValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getStringListValueStyle_StringListValue(), theEcorePackage.getEString(), "stringListValue", null, 0, -1, StringListValueStyle.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eObjectValueStyleEClass, EObjectValueStyle.class, "EObjectValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getEObjectValueStyle_EObjectValue(), theEcorePackage.getEObject(), null, "eObjectValue", null, 0, 1, EObjectValueStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eObjectListValueStyleEClass, EObjectListValueStyle.class, "EObjectListValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getEObjectListValueStyle_EObjectListValue(), theEcorePackage.getEObject(), null, "eObjectListValue", null, 0, -1, //$NON-NLS-1$
        EObjectListValueStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(byteArrayValueStyleEClass, ByteArrayValueStyle.class, "ByteArrayValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getByteArrayValueStyle_ByteArrayValue(), theEcorePackage.getEByteArray(), "byteArrayValue", null, 0, 1, ByteArrayValueStyle.class, //$NON-NLS-1$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(booleanListValueStyleEClass, BooleanListValueStyle.class, "BooleanListValueStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getBooleanListValueStyle_BooleanListValue(), theEcorePackage.getEBooleanObject(), "booleanListValue", null, 0, -1, //$NON-NLS-1$
        BooleanListValueStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(hintedDiagramLinkStyleEClass, HintedDiagramLinkStyle.class, "HintedDiagramLinkStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getHintedDiagramLinkStyle_Hint(), theEcorePackage.getEString(), "hint", null, 0, 1, HintedDiagramLinkStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(diagramLinkStyleEClass, DiagramLinkStyle.class, "DiagramLinkStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getDiagramLinkStyle_DiagramLink(), getDiagram(), null, "diagramLink", null, 0, 1, DiagramLinkStyle.class, !IS_TRANSIENT, !IS_VOLATILE, //$NON-NLS-1$
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(multiDiagramLinkStyleEClass, MultiDiagramLinkStyle.class, "MultiDiagramLinkStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEReference(getMultiDiagramLinkStyle_DiagramLinks(), getDiagram(), null, "diagramLinks", null, 0, -1, MultiDiagramLinkStyle.class, !IS_TRANSIENT, //$NON-NLS-1$
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(textStyleEClass, TextStyle.class, "TextStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getTextStyle_TextAlignment(), getTextAlignment(), "textAlignment", "Left", 0, 1, TextStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(lineTypeStyleEClass, LineTypeStyle.class, "LineTypeStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getLineTypeStyle_LineType(), getLineType(), "lineType", "Solid", 0, 1, LineTypeStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(arrowStyleEClass, ArrowStyle.class, "ArrowStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getArrowStyle_ArrowSource(), getArrowType(), "arrowSource", "None", 0, 1, ArrowStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getArrowStyle_ArrowTarget(), getArrowType(), "arrowTarget", "None", 0, 1, ArrowStyle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(shapeEClass, Shape.class, "Shape", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(compartmentEClass, Compartment.class, "Compartment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(listCompartmentEClass, ListCompartment.class, "ListCompartment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(connectorEClass, Connector.class, "Connector", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(standardDiagramEClass, StandardDiagram.class, "StandardDiagram", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(decorationNodeEClass, DecorationNode.class, "DecorationNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(basicDecorationNodeEClass, BasicDecorationNode.class, "BasicDecorationNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(basicCompartmentEClass, BasicCompartment.class, "BasicCompartment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    initEClass(basicSemanticCompartmentEClass, BasicSemanticCompartment.class, "BasicSemanticCompartment", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(semanticListCompartmentEClass, SemanticListCompartment.class, "SemanticListCompartment", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(roundedCornersStyleEClass, RoundedCornersStyle.class, "RoundedCornersStyle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEAttribute(getRoundedCornersStyle_RoundedBendpointsRadius(), ecorePackage.getEInt(), "roundedBendpointsRadius", "0", 0, 1, RoundedCornersStyle.class, //$NON-NLS-1$ //$NON-NLS-2$
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(sortingEEnum, Sorting.class, "Sorting"); //$NON-NLS-1$
    addEEnumLiteral(sortingEEnum, Sorting.NONE_LITERAL);
    addEEnumLiteral(sortingEEnum, Sorting.MANUAL_LITERAL);
    addEEnumLiteral(sortingEEnum, Sorting.AUTOMATIC_LITERAL);

    initEEnum(filteringEEnum, Filtering.class, "Filtering"); //$NON-NLS-1$
    addEEnumLiteral(filteringEEnum, Filtering.NONE_LITERAL);
    addEEnumLiteral(filteringEEnum, Filtering.MANUAL_LITERAL);
    addEEnumLiteral(filteringEEnum, Filtering.AUTOMATIC_LITERAL);

    initEEnum(routingEEnum, Routing.class, "Routing"); //$NON-NLS-1$
    addEEnumLiteral(routingEEnum, Routing.MANUAL_LITERAL);
    addEEnumLiteral(routingEEnum, Routing.RECTILINEAR_LITERAL);
    addEEnumLiteral(routingEEnum, Routing.TREE_LITERAL);

    initEEnum(smoothnessEEnum, Smoothness.class, "Smoothness"); //$NON-NLS-1$
    addEEnumLiteral(smoothnessEEnum, Smoothness.NONE_LITERAL);
    addEEnumLiteral(smoothnessEEnum, Smoothness.NORMAL_LITERAL);
    addEEnumLiteral(smoothnessEEnum, Smoothness.LESS_LITERAL);
    addEEnumLiteral(smoothnessEEnum, Smoothness.MORE_LITERAL);

    initEEnum(jumpLinkStatusEEnum, JumpLinkStatus.class, "JumpLinkStatus"); //$NON-NLS-1$
    addEEnumLiteral(jumpLinkStatusEEnum, JumpLinkStatus.NONE_LITERAL);
    addEEnumLiteral(jumpLinkStatusEEnum, JumpLinkStatus.ALL_LITERAL);
    addEEnumLiteral(jumpLinkStatusEEnum, JumpLinkStatus.BELOW_LITERAL);
    addEEnumLiteral(jumpLinkStatusEEnum, JumpLinkStatus.ABOVE_LITERAL);

    initEEnum(jumpLinkTypeEEnum, JumpLinkType.class, "JumpLinkType"); //$NON-NLS-1$
    addEEnumLiteral(jumpLinkTypeEEnum, JumpLinkType.SEMICIRCLE_LITERAL);
    addEEnumLiteral(jumpLinkTypeEEnum, JumpLinkType.SQUARE_LITERAL);
    addEEnumLiteral(jumpLinkTypeEEnum, JumpLinkType.CHAMFERED_LITERAL);
    addEEnumLiteral(jumpLinkTypeEEnum, JumpLinkType.TUNNEL_LITERAL);

    initEEnum(alignmentEEnum, Alignment.class, "Alignment"); //$NON-NLS-1$
    addEEnumLiteral(alignmentEEnum, Alignment.LEFT_LITERAL);
    addEEnumLiteral(alignmentEEnum, Alignment.RIGHT_LITERAL);
    addEEnumLiteral(alignmentEEnum, Alignment.CENTER_LITERAL);
    addEEnumLiteral(alignmentEEnum, Alignment.TOP_LITERAL);
    addEEnumLiteral(alignmentEEnum, Alignment.BOTTOM_LITERAL);

    initEEnum(sortingDirectionEEnum, SortingDirection.class, "SortingDirection"); //$NON-NLS-1$
    addEEnumLiteral(sortingDirectionEEnum, SortingDirection.ASCENDING_LITERAL);
    addEEnumLiteral(sortingDirectionEEnum, SortingDirection.DESCENDING_LITERAL);

    initEEnum(measurementUnitEEnum, MeasurementUnit.class, "MeasurementUnit"); //$NON-NLS-1$
    addEEnumLiteral(measurementUnitEEnum, MeasurementUnit.HIMETRIC_LITERAL);
    addEEnumLiteral(measurementUnitEEnum, MeasurementUnit.PIXEL_LITERAL);

    initEEnum(textAlignmentEEnum, TextAlignment.class, "TextAlignment"); //$NON-NLS-1$
    addEEnumLiteral(textAlignmentEEnum, TextAlignment.LEFT_LITERAL);
    addEEnumLiteral(textAlignmentEEnum, TextAlignment.RIGHT_LITERAL);
    addEEnumLiteral(textAlignmentEEnum, TextAlignment.CENTER_LITERAL);

    initEEnum(lineTypeEEnum, LineType.class, "LineType"); //$NON-NLS-1$
    addEEnumLiteral(lineTypeEEnum, LineType.SOLID_LITERAL);
    addEEnumLiteral(lineTypeEEnum, LineType.DASH_LITERAL);
    addEEnumLiteral(lineTypeEEnum, LineType.DOT_LITERAL);
    addEEnumLiteral(lineTypeEEnum, LineType.DASH_DOT_LITERAL);
    addEEnumLiteral(lineTypeEEnum, LineType.DASH_DOT_DOT_LITERAL);
    addEEnumLiteral(lineTypeEEnum, LineType.DOUBLE_LITERAL);

    initEEnum(arrowTypeEEnum, ArrowType.class, "ArrowType"); //$NON-NLS-1$
    addEEnumLiteral(arrowTypeEEnum, ArrowType.NONE_LITERAL);
    addEEnumLiteral(arrowTypeEEnum, ArrowType.OPEN_ARROW_LITERAL);
    addEEnumLiteral(arrowTypeEEnum, ArrowType.SOLID_ARROW_LITERAL);

    initEEnum(gradientStyleEEnum, GradientStyle.class, "GradientStyle"); //$NON-NLS-1$
    addEEnumLiteral(gradientStyleEEnum, GradientStyle.VERTICAL_LITERAL);
    addEEnumLiteral(gradientStyleEEnum, GradientStyle.HORIZONTAL_LITERAL);

    // Initialize data types
    initEDataType(relativeBendpointListEDataType, List.class, "RelativeBendpointList", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(filterKeyListEDataType, List.class, "FilterKeyList", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(sortKeyMapEDataType, Map.class, "SortKeyMap", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(gradientDataEDataType, GradientData.class, "GradientData", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    // Create resource
    createResource(eNS_URI);
  }

  void createAnnotations()
  {
    for (Iterator it = getEClassifiers().iterator(); it.hasNext();)
    {
      EClassifier eClassifier = (EClassifier)it.next();
      String name = eClassifier.getName();
      if (name.equals("Node") || name.indexOf("Style") != -1 || name.equals("Location") || name.equals("Size") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
          name.indexOf("Anchor") != -1 || name.indexOf("point") != -1 || name.equals("Bounds") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          || name.indexOf("Edge") != -1 || name.indexOf("Constraint") != -1) //$NON-NLS-1$ //$NON-NLS-2$
      {
        String source = "foo"; //$NON-NLS-1$
        addAnnotation(eClassifier, source, new String[] { "uuid", "false" //$NON-NLS-1$ //$NON-NLS-2$
        });
      }
    }
  }

} // NotationPackageImpl
