/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.util;

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClassChild;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.AParameter;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage
 * @generated
 */
public class AcoreAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static AcorePackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AcoreAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = AcorePackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   *
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
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected AcoreSwitch<Adapter> modelSwitch = new AcoreSwitch<Adapter>()
  {
    @Override
    public Adapter caseAClass(AClass object)
    {
      return createAClassAdapter();
    }

    @Override
    public Adapter caseAInterface(AInterface object)
    {
      return createAInterfaceAdapter();
    }

    @Override
    public Adapter caseACoreRoot(ACoreRoot object)
    {
      return createACoreRootAdapter();
    }

    @Override
    public Adapter caseAAttribute(AAttribute object)
    {
      return createAAttributeAdapter();
    }

    @Override
    public Adapter caseAOperation(AOperation object)
    {
      return createAOperationAdapter();
    }

    @Override
    public Adapter caseABasicClass(ABasicClass object)
    {
      return createABasicClassAdapter();
    }

    @Override
    public Adapter caseAParameter(AParameter object)
    {
      return createAParameterAdapter();
    }

    @Override
    public Adapter caseAClassChild(AClassChild object)
    {
      return createAClassChildAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass
   * <em>AClass</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass
   * @generated
   */
  public Adapter createAClassAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AInterface
   * <em>AInterface</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AInterface
   * @generated
   */
  public Adapter createAInterfaceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot
   * <em>ACore Root</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot
   * @generated
   */
  public Adapter createACoreRootAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AAttribute
   * <em>AAttribute</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AAttribute
   * @generated
   */
  public Adapter createAAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AOperation
   * <em>AOperation</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AOperation
   * @generated
   */
  public Adapter createAOperationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass
   * <em>ABasic Class</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass
   * @generated
   */
  public Adapter createABasicClassAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AParameter
   * <em>AParameter</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AParameter
   * @generated
   */
  public Adapter createAParameterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild
   * <em>AClass Child</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClassChild
   * @generated
   */
  public Adapter createAClassChildAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // AcoreAdapterFactory
