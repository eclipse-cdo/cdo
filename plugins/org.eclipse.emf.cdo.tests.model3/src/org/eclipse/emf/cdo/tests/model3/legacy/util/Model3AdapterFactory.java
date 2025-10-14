/*
 * Copyright (c) 2013, 2015, 2018, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.util;

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
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model3.legacy.Model3Package
 * @generated
 */
public class Model3AdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static Model3Package modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model3AdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = Model3Package.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Model3Switch<Adapter> modelSwitch = new Model3Switch<Adapter>()
  {
    @Override
    public Adapter caseClass1(Class1 object)
    {
      return createClass1Adapter();
    }

    @Override
    public Adapter caseMetaRef(MetaRef object)
    {
      return createMetaRefAdapter();
    }

    @Override
    public Adapter casePolygon(Polygon object)
    {
      return createPolygonAdapter();
    }

    @Override
    public Adapter casePolygonWithDuplicates(PolygonWithDuplicates object)
    {
      return createPolygonWithDuplicatesAdapter();
    }

    @Override
    public Adapter caseNodeA(NodeA object)
    {
      return createNodeAAdapter();
    }

    @Override
    public Adapter caseNodeB(NodeB object)
    {
      return createNodeBAdapter();
    }

    @Override
    public Adapter caseNodeC(NodeC object)
    {
      return createNodeCAdapter();
    }

    @Override
    public Adapter caseNodeD(NodeD object)
    {
      return createNodeDAdapter();
    }

    @Override
    public Adapter caseNodeE(NodeE object)
    {
      return createNodeEAdapter();
    }

    @Override
    public Adapter caseImage(Image object)
    {
      return createImageAdapter();
    }

    @Override
    public Adapter caseFile(File object)
    {
      return createFileAdapter();
    }

    @Override
    public Adapter caseMultiLob(MultiLob object)
    {
      return createMultiLobAdapter();
    }

    @Override
    public Adapter caseClassWithIDAttribute(ClassWithIDAttribute object)
    {
      return createClassWithIDAttributeAdapter();
    }

    @Override
    public Adapter caseClassWithJavaClassAttribute(ClassWithJavaClassAttribute object)
    {
      return createClassWithJavaClassAttributeAdapter();
    }

    @Override
    public Adapter caseClassWithJavaObjectAttribute(ClassWithJavaObjectAttribute object)
    {
      return createClassWithJavaObjectAttributeAdapter();
    }

    @Override
    public Adapter caseClassWithTransientContainment(ClassWithTransientContainment object)
    {
      return createClassWithTransientContainmentAdapter();
    }

    @Override
    public Adapter caseEdgeTarget(EdgeTarget object)
    {
      return createEdgeTargetAdapter();
    }

    @Override
    public Adapter caseNodeF(NodeF object)
    {
      return createNodeFAdapter();
    }

    @Override
    public Adapter caseEdge(Edge object)
    {
      return createEdgeAdapter();
    }

    @Override
    public Adapter caseDiagram(Diagram object)
    {
      return createDiagramAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.Class1 <em>Class1</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.Class1
   * @generated
   */
  public Adapter createClass1Adapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.MetaRef <em>Meta Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.MetaRef
   * @generated
   */
  public Adapter createMetaRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.Polygon <em>Polygon</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.Polygon
   * @generated
   */
  public Adapter createPolygonAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates <em>Polygon With Duplicates</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates
   * @generated
   */
  public Adapter createPolygonWithDuplicatesAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.NodeA <em>Node A</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.NodeA
   * @generated
   */
  public Adapter createNodeAAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.NodeB <em>Node B</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.NodeB
   * @generated
   */
  public Adapter createNodeBAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.NodeC <em>Node C</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.NodeC
   * @generated
   */
  public Adapter createNodeCAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.NodeD <em>Node D</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.NodeD
   * @generated
   */
  public Adapter createNodeDAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.NodeE <em>Node E</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.NodeE
   * @generated
   */
  public Adapter createNodeEAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.Image <em>Image</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.Image
   * @generated
   */
  public Adapter createImageAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.File <em>File</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.File
   * @generated
   */
  public Adapter createFileAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.MultiLob <em>Multi Lob</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.MultiLob
   * @generated
   */
  public Adapter createMultiLobAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute <em>Class With ID Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithIDAttribute
   * @generated
   */
  public Adapter createClassWithIDAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute <em>Class With Java Class Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute
   * @generated
   */
  public Adapter createClassWithJavaClassAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute <em>Class With Java Object Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute
   * @generated
   */
  public Adapter createClassWithJavaObjectAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment <em>Class With Transient Containment</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment
   * @generated
   */
  public Adapter createClassWithTransientContainmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.EdgeTarget <em>Edge Target</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.EdgeTarget
   * @generated
   */
  public Adapter createEdgeTargetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.NodeF <em>Node F</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.NodeF
   * @generated
   */
  public Adapter createNodeFAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.Edge <em>Edge</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.Edge
   * @generated
   */
  public Adapter createEdgeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model3.Diagram <em>Diagram</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model3.Diagram
   * @generated
   */
  public Adapter createDiagramAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // Model3AdapterFactory
