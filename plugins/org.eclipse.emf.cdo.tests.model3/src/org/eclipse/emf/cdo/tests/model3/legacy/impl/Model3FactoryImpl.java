/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

//import org.eclipse.emf.cdo.tests.model3.*;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment;
import org.eclipse.emf.cdo.tests.model3.Diagram;
import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.EdgeTarget;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.MultiLob;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.tests.model3.NodeB;
import org.eclipse.emf.cdo.tests.model3.NodeC;
import org.eclipse.emf.cdo.tests.model3.NodeD;
import org.eclipse.emf.cdo.tests.model3.NodeE;
import org.eclipse.emf.cdo.tests.model3.NodeF;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model3FactoryImpl extends EFactoryImpl implements Model3Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public static Model3Factory init()
  {
    try
    {
      Model3Factory theModel3Factory = (Model3Factory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/CDO/tests/legacy/model3/1.0.0");
      if (theModel3Factory != null)
      {
        return theModel3Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model3FactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model3FactoryImpl()
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
    case Model3Package.CLASS1:
      return createClass1();
    case Model3Package.META_REF:
      return createMetaRef();
    case Model3Package.POLYGON:
      return createPolygon();
    case Model3Package.POLYGON_WITH_DUPLICATES:
      return createPolygonWithDuplicates();
    case Model3Package.NODE_A:
      return createNodeA();
    case Model3Package.NODE_B:
      return createNodeB();
    case Model3Package.NODE_C:
      return createNodeC();
    case Model3Package.NODE_D:
      return createNodeD();
    case Model3Package.NODE_E:
      return createNodeE();
    case Model3Package.IMAGE:
      return createImage();
    case Model3Package.FILE:
      return createFile();
    case Model3Package.MULTI_LOB:
      return createMultiLob();
    case Model3Package.CLASS_WITH_ID_ATTRIBUTE:
      return createClassWithIDAttribute();
    case Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE:
      return createClassWithJavaClassAttribute();
    case Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE:
      return createClassWithJavaObjectAttribute();
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT:
      return createClassWithTransientContainment();
    case Model3Package.EDGE_TARGET:
      return createEdgeTarget();
    case Model3Package.NODE_F:
      return createNodeF();
    case Model3Package.EDGE:
      return createEdge();
    case Model3Package.DIAGRAM:
      return createDiagram();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
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
    case Model3Package.POINT:
      return createPointFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
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
    case Model3Package.POINT:
      return convertPointToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Class1 createClass1()
  {
    Class1Impl class1 = new Class1Impl();
    return class1;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MetaRef createMetaRef()
  {
    MetaRefImpl metaRef = new MetaRefImpl();
    return metaRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Polygon createPolygon()
  {
    PolygonImpl polygon = new PolygonImpl();
    return polygon;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PolygonWithDuplicates createPolygonWithDuplicates()
  {
    PolygonWithDuplicatesImpl polygonWithDuplicates = new PolygonWithDuplicatesImpl();
    return polygonWithDuplicates;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeA createNodeA()
  {
    NodeAImpl nodeA = new NodeAImpl();
    return nodeA;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeB createNodeB()
  {
    NodeBImpl nodeB = new NodeBImpl();
    return nodeB;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeC createNodeC()
  {
    NodeCImpl nodeC = new NodeCImpl();
    return nodeC;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeD createNodeD()
  {
    NodeDImpl nodeD = new NodeDImpl();
    return nodeD;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeE createNodeE()
  {
    NodeEImpl nodeE = new NodeEImpl();
    return nodeE;
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
  public File createFile()
  {
    FileImpl file = new FileImpl();
    return file;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MultiLob createMultiLob()
  {
    MultiLobImpl multiLob = new MultiLobImpl();
    return multiLob;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithIDAttribute createClassWithIDAttribute()
  {
    ClassWithIDAttributeImpl classWithIDAttribute = new ClassWithIDAttributeImpl();
    return classWithIDAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithJavaClassAttribute createClassWithJavaClassAttribute()
  {
    ClassWithJavaClassAttributeImpl classWithJavaClassAttribute = new ClassWithJavaClassAttributeImpl();
    return classWithJavaClassAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithJavaObjectAttribute createClassWithJavaObjectAttribute()
  {
    ClassWithJavaObjectAttributeImpl classWithJavaObjectAttribute = new ClassWithJavaObjectAttributeImpl();
    return classWithJavaObjectAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithTransientContainment createClassWithTransientContainment()
  {
    ClassWithTransientContainmentImpl classWithTransientContainment = new ClassWithTransientContainmentImpl();
    return classWithTransientContainment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EdgeTarget createEdgeTarget()
  {
    EdgeTargetImpl edgeTarget = new EdgeTargetImpl();
    return edgeTarget;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NodeF createNodeF()
  {
    NodeFImpl nodeF = new NodeFImpl();
    return nodeF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Edge createEdge()
  {
    EdgeImpl edge = new EdgeImpl();
    return edge;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
   *
   * @generated NOT
   */
  public Point createPointFromString(EDataType eDataType, String initialValue)
  {
    return Point.parse(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public String convertPointToString(EDataType eDataType, Object instanceValue)
  {
    return ((Point)instanceValue).toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Model3Package getModel3Package()
  {
    return (Model3Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model3Package getPackage()
  {
    return Model3Package.eINSTANCE;
  }

} // Model3FactoryImpl
