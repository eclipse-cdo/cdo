/*
 * Copyright (c) 2008, 2009, 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4interfaces.util;

//import org.eclipse.emf.cdo.tests.model4interfaces.*;
import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.INamedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage
 * @generated
 */
public class model4interfacesAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static model4interfacesPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public model4interfacesAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = model4interfacesPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected model4interfacesSwitch<Adapter> modelSwitch = new model4interfacesSwitch<Adapter>()
  {
    @Override
    public Adapter caseISingleRefContainer(ISingleRefContainer object)
    {
      return createISingleRefContainerAdapter();
    }

    @Override
    public Adapter caseISingleRefContainedElement(ISingleRefContainedElement object)
    {
      return createISingleRefContainedElementAdapter();
    }

    @Override
    public Adapter caseISingleRefNonContainer(ISingleRefNonContainer object)
    {
      return createISingleRefNonContainerAdapter();
    }

    @Override
    public Adapter caseISingleRefNonContainedElement(ISingleRefNonContainedElement object)
    {
      return createISingleRefNonContainedElementAdapter();
    }

    @Override
    public Adapter caseIMultiRefContainer(IMultiRefContainer object)
    {
      return createIMultiRefContainerAdapter();
    }

    @Override
    public Adapter caseIMultiRefContainedElement(IMultiRefContainedElement object)
    {
      return createIMultiRefContainedElementAdapter();
    }

    @Override
    public Adapter caseIMultiRefNonContainer(IMultiRefNonContainer object)
    {
      return createIMultiRefNonContainerAdapter();
    }

    @Override
    public Adapter caseIMultiRefNonContainedElement(IMultiRefNonContainedElement object)
    {
      return createIMultiRefNonContainedElementAdapter();
    }

    @Override
    public Adapter caseINamedElement(INamedElement object)
    {
      return createINamedElementAdapter();
    }

    @Override
    public Adapter caseIContainedElementNoParentLink(IContainedElementNoParentLink object)
    {
      return createIContainedElementNoParentLinkAdapter();
    }

    @Override
    public Adapter caseISingleRefContainerNPL(ISingleRefContainerNPL object)
    {
      return createISingleRefContainerNPLAdapter();
    }

    @Override
    public Adapter caseISingleRefNonContainerNPL(ISingleRefNonContainerNPL object)
    {
      return createISingleRefNonContainerNPLAdapter();
    }

    @Override
    public Adapter caseIMultiRefContainerNPL(IMultiRefContainerNPL object)
    {
      return createIMultiRefContainerNPLAdapter();
    }

    @Override
    public Adapter caseIMultiRefNonContainerNPL(IMultiRefNonContainerNPL object)
    {
      return createIMultiRefNonContainerNPLAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer <em>ISingle Ref Container</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer
   * @generated
   */
  public Adapter createISingleRefContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement <em>ISingle Ref Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement
   * @generated
   */
  public Adapter createISingleRefContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer <em>ISingle Ref Non Container</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer
   * @generated
   */
  public Adapter createISingleRefNonContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement <em>ISingle Ref Non Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement
   * @generated
   */
  public Adapter createISingleRefNonContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer <em>IMulti Ref Container</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer
   * @generated
   */
  public Adapter createIMultiRefContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement <em>IMulti Ref Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
   * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement
   * @generated
   */
  public Adapter createIMultiRefContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer <em>IMulti Ref Non Container</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer
   * @generated
   */
  public Adapter createIMultiRefNonContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement <em>IMulti Ref Non Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement
   * @generated
   */
  public Adapter createIMultiRefNonContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.INamedElement <em>INamed Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.INamedElement
   * @generated
   */
  public Adapter createINamedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink <em>IContained Element No Parent Link</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink
   * @generated
   */
  public Adapter createIContainedElementNoParentLinkAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL <em>ISingle Ref Container NPL</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL
   * @generated
   */
  public Adapter createISingleRefContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL <em>ISingle Ref Non Container NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
   * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL
   * @generated
   */
  public Adapter createISingleRefNonContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL <em>IMulti Ref Container NPL</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL
   * @generated
   */
  public Adapter createIMultiRefContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL <em>IMulti Ref Non Container NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL
   * @generated
   */
  public Adapter createIMultiRefNonContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // model4interfacesAdapterFactory
