/*
 * Copyright (c) 2008-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.util;

import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Package;
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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import java.util.Map;

//import org.eclipse.emf.cdo.tests.model4.*;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4.model4Package
 * @generated
 */
public class model4AdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static model4Package modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public model4AdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = model4Package.eINSTANCE;
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
  protected model4Switch<Adapter> modelSwitch = new model4Switch<Adapter>()
  {
    @Override
    public Adapter caseRefSingleContained(RefSingleContained object)
    {
      return createRefSingleContainedAdapter();
    }

    @Override
    public Adapter caseSingleContainedElement(SingleContainedElement object)
    {
      return createSingleContainedElementAdapter();
    }

    @Override
    public Adapter caseRefSingleNonContained(RefSingleNonContained object)
    {
      return createRefSingleNonContainedAdapter();
    }

    @Override
    public Adapter caseSingleNonContainedElement(SingleNonContainedElement object)
    {
      return createSingleNonContainedElementAdapter();
    }

    @Override
    public Adapter caseRefMultiContained(RefMultiContained object)
    {
      return createRefMultiContainedAdapter();
    }

    @Override
    public Adapter caseMultiContainedElement(MultiContainedElement object)
    {
      return createMultiContainedElementAdapter();
    }

    @Override
    public Adapter caseRefMultiNonContained(RefMultiNonContained object)
    {
      return createRefMultiNonContainedAdapter();
    }

    @Override
    public Adapter caseMultiNonContainedElement(MultiNonContainedElement object)
    {
      return createMultiNonContainedElementAdapter();
    }

    @Override
    public Adapter caseRefMultiNonContainedUnsettable(RefMultiNonContainedUnsettable object)
    {
      return createRefMultiNonContainedUnsettableAdapter();
    }

    @Override
    public Adapter caseMultiNonContainedUnsettableElement(MultiNonContainedUnsettableElement object)
    {
      return createMultiNonContainedUnsettableElementAdapter();
    }

    @Override
    public Adapter caseRefSingleContainedNPL(RefSingleContainedNPL object)
    {
      return createRefSingleContainedNPLAdapter();
    }

    @Override
    public Adapter caseRefSingleNonContainedNPL(RefSingleNonContainedNPL object)
    {
      return createRefSingleNonContainedNPLAdapter();
    }

    @Override
    public Adapter caseRefMultiContainedNPL(RefMultiContainedNPL object)
    {
      return createRefMultiContainedNPLAdapter();
    }

    @Override
    public Adapter caseRefMultiNonContainedNPL(RefMultiNonContainedNPL object)
    {
      return createRefMultiNonContainedNPLAdapter();
    }

    @Override
    public Adapter caseContainedElementNoOpposite(ContainedElementNoOpposite object)
    {
      return createContainedElementNoOppositeAdapter();
    }

    @Override
    public Adapter caseGenRefSingleContained(GenRefSingleContained object)
    {
      return createGenRefSingleContainedAdapter();
    }

    @Override
    public Adapter caseGenRefSingleNonContained(GenRefSingleNonContained object)
    {
      return createGenRefSingleNonContainedAdapter();
    }

    @Override
    public Adapter caseGenRefMultiContained(GenRefMultiContained object)
    {
      return createGenRefMultiContainedAdapter();
    }

    @Override
    public Adapter caseGenRefMultiNonContained(GenRefMultiNonContained object)
    {
      return createGenRefMultiNonContainedAdapter();
    }

    @Override
    public Adapter caseImplSingleRefContainer(ImplSingleRefContainer object)
    {
      return createImplSingleRefContainerAdapter();
    }

    @Override
    public Adapter caseImplSingleRefContainedElement(ImplSingleRefContainedElement object)
    {
      return createImplSingleRefContainedElementAdapter();
    }

    @Override
    public Adapter caseImplSingleRefNonContainer(ImplSingleRefNonContainer object)
    {
      return createImplSingleRefNonContainerAdapter();
    }

    @Override
    public Adapter caseImplSingleRefNonContainedElement(ImplSingleRefNonContainedElement object)
    {
      return createImplSingleRefNonContainedElementAdapter();
    }

    @Override
    public Adapter caseImplMultiRefNonContainer(ImplMultiRefNonContainer object)
    {
      return createImplMultiRefNonContainerAdapter();
    }

    @Override
    public Adapter caseImplMultiRefNonContainedElement(ImplMultiRefNonContainedElement object)
    {
      return createImplMultiRefNonContainedElementAdapter();
    }

    @Override
    public Adapter caseImplMultiRefContainer(ImplMultiRefContainer object)
    {
      return createImplMultiRefContainerAdapter();
    }

    @Override
    public Adapter caseImplMultiRefContainedElement(ImplMultiRefContainedElement object)
    {
      return createImplMultiRefContainedElementAdapter();
    }

    @Override
    public Adapter caseImplSingleRefContainerNPL(ImplSingleRefContainerNPL object)
    {
      return createImplSingleRefContainerNPLAdapter();
    }

    @Override
    public Adapter caseImplSingleRefNonContainerNPL(ImplSingleRefNonContainerNPL object)
    {
      return createImplSingleRefNonContainerNPLAdapter();
    }

    @Override
    public Adapter caseImplMultiRefContainerNPL(ImplMultiRefContainerNPL object)
    {
      return createImplMultiRefContainerNPLAdapter();
    }

    @Override
    public Adapter caseImplMultiRefNonContainerNPL(ImplMultiRefNonContainerNPL object)
    {
      return createImplMultiRefNonContainerNPLAdapter();
    }

    @Override
    public Adapter caseImplContainedElementNPL(ImplContainedElementNPL object)
    {
      return createImplContainedElementNPLAdapter();
    }

    @Override
    public Adapter caseGenRefMultiNUNonContained(GenRefMultiNUNonContained object)
    {
      return createGenRefMultiNUNonContainedAdapter();
    }

    @Override
    public Adapter caseGenRefMapNonContained(GenRefMapNonContained object)
    {
      return createGenRefMapNonContainedAdapter();
    }

    @Override
    public Adapter caseStringToEObject(Map.Entry<String, EObject> object)
    {
      return createStringToEObjectAdapter();
    }

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
    public Adapter caseIContainedElementNoParentLink(IContainedElementNoParentLink object)
    {
      return createIContainedElementNoParentLinkAdapter();
    }

    @Override
    public Adapter caseINamedElement(INamedElement object)
    {
      return createINamedElementAdapter();
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
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefSingleContained <em>Ref Single Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefSingleContained
   * @generated
   */
  public Adapter createRefSingleContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.SingleContainedElement <em>Single Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.SingleContainedElement
   * @generated
   */
  public Adapter createSingleContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContained <em>Ref Single Non Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefSingleNonContained
   * @generated
   */
  public Adapter createRefSingleNonContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement <em>Single Non Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement
   * @generated
   */
  public Adapter createSingleNonContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefMultiContained <em>Ref Multi Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefMultiContained
   * @generated
   */
  public Adapter createRefMultiContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.MultiContainedElement <em>Multi Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.MultiContainedElement
   * @generated
   */
  public Adapter createMultiContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContained <em>Ref Multi Non Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefMultiNonContained
   * @generated
   */
  public Adapter createRefMultiNonContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement <em>Multi Non Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement
   * @generated
   */
  public Adapter createMultiNonContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable <em>Ref Multi Non Contained Unsettable</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
   * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable
   * @generated
   */
  public Adapter createRefMultiNonContainedUnsettableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement <em>Multi Non Contained Unsettable Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null
   * so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement
   * @generated
   */
  public Adapter createMultiNonContainedUnsettableElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL <em>Ref Single Contained NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL
   * @generated
   */
  public Adapter createRefSingleContainedNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL <em>Ref Single Non Contained NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL
   * @generated
   */
  public Adapter createRefSingleNonContainedNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL <em>Ref Multi Contained NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL
   * @generated
   */
  public Adapter createRefMultiContainedNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL <em>Ref Multi Non Contained NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL
   * @generated
   */
  public Adapter createRefMultiNonContainedNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite <em>Contained Element No Opposite</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite
   * @generated
   */
  public Adapter createContainedElementNoOppositeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.GenRefSingleContained <em>Gen Ref Single Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.GenRefSingleContained
   * @generated
   */
  public Adapter createGenRefSingleContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained <em>Gen Ref Single Non Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained
   * @generated
   */
  public Adapter createGenRefSingleNonContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.GenRefMultiContained <em>Gen Ref Multi Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.GenRefMultiContained
   * @generated
   */
  public Adapter createGenRefMultiContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained <em>Gen Ref Multi Non Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained
   * @generated
   */
  public Adapter createGenRefMultiNonContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer <em>Impl Single Ref Container</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer
   * @generated
   */
  public Adapter createImplSingleRefContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement <em>Impl Single Ref Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement
   * @generated
   */
  public Adapter createImplSingleRefContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer <em>Impl Single Ref Non Container</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer
   * @generated
   */
  public Adapter createImplSingleRefNonContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement <em>Impl Single Ref Non Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null
   * so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement
   * @generated
   */
  public Adapter createImplSingleRefNonContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer <em>Impl Multi Ref Non Container</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer
   * @generated
   */
  public Adapter createImplMultiRefNonContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement <em>Impl Multi Ref Non Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null
   * so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement
   * @generated
   */
  public Adapter createImplMultiRefNonContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer <em>Impl Multi Ref Container</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer
   * @generated
   */
  public Adapter createImplMultiRefContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement <em>Impl Multi Ref Contained Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement
   * @generated
   */
  public Adapter createImplMultiRefContainedElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL <em>Impl Single Ref Container NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL
   * @generated
   */
  public Adapter createImplSingleRefContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL <em>Impl Single Ref Non Container NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL
   * @generated
   */
  public Adapter createImplSingleRefNonContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL <em>Impl Multi Ref Container NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL
   * @generated
   */
  public Adapter createImplMultiRefContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL <em>Impl Multi Ref Non Container NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL
   * @generated
   */
  public Adapter createImplMultiRefNonContainerNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL <em>Impl Contained Element NPL</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL
   * @generated
   */
  public Adapter createImplContainedElementNPLAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained <em>Gen Ref Multi NU Non Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained
   * @generated
   */
  public Adapter createGenRefMultiNUNonContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained <em>Gen Ref Map Non Contained</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained
   * @generated
   */
  public Adapter createGenRefMapNonContainedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To EObject</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see java.util.Map.Entry
   * @generated
   */
  public Adapter createStringToEObjectAdapter()
  {
    return null;
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

} // model4AdapterFactory
