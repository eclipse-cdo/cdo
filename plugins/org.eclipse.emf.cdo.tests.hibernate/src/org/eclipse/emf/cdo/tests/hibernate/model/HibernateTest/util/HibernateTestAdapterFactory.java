/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.util;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A1;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage
 * @generated
 */
public class HibernateTestAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static HibernateTestPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public HibernateTestAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = HibernateTestPackage.eINSTANCE;
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
  protected HibernateTestSwitch<Adapter> modelSwitch = new HibernateTestSwitch<Adapter>()
  {
    @Override
    public Adapter caseBz356181_Main(Bz356181_Main object)
    {
      return createBz356181_MainAdapter();
    }

    @Override
    public Adapter caseBz356181_Transient(Bz356181_Transient object)
    {
      return createBz356181_TransientAdapter();
    }

    @Override
    public Adapter caseBz356181_NonTransient(Bz356181_NonTransient object)
    {
      return createBz356181_NonTransientAdapter();
    }

    @Override
    public Adapter caseBz387752_Main(Bz387752_Main object)
    {
      return createBz387752_MainAdapter();
    }

    @Override
    public Adapter caseBz380987_Group(Bz380987_Group object)
    {
      return createBz380987_GroupAdapter();
    }

    @Override
    public Adapter caseBz380987_Place(Bz380987_Place object)
    {
      return createBz380987_PlaceAdapter();
    }

    @Override
    public Adapter caseBz380987_Person(Bz380987_Person object)
    {
      return createBz380987_PersonAdapter();
    }

    @Override
    public Adapter caseBz398057A(Bz398057A object)
    {
      return createBz398057AAdapter();
    }

    @Override
    public Adapter caseBz398057A1(Bz398057A1 object)
    {
      return createBz398057A1Adapter();
    }

    @Override
    public Adapter caseBz398057B(Bz398057B object)
    {
      return createBz398057BAdapter();
    }

    @Override
    public Adapter caseBz398057B1(Bz398057B1 object)
    {
      return createBz398057B1Adapter();
    }

    @Override
    public Adapter caseBz397682P(Bz397682P object)
    {
      return createBz397682PAdapter();
    }

    @Override
    public Adapter caseBz397682C(Bz397682C object)
    {
      return createBz397682CAdapter();
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
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main <em>Bz356181 Main</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main
   * @generated
   */
  public Adapter createBz356181_MainAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient <em>Bz356181 Transient</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient
   * @generated
   */
  public Adapter createBz356181_TransientAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient <em>Bz356181 Non Transient</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient
   * @generated
   */
  public Adapter createBz356181_NonTransientAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main <em>Bz387752 Main</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main
   * @generated
   */
  public Adapter createBz387752_MainAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group <em>Bz380987 Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group
   * @generated
   */
  public Adapter createBz380987_GroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place <em>Bz380987 Place</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place
   * @generated
   */
  public Adapter createBz380987_PlaceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person <em>Bz380987 Person</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person
   * @generated
   */
  public Adapter createBz380987_PersonAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A <em>Bz398057 A</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A
   * @generated
   */
  public Adapter createBz398057AAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A1 <em>Bz398057 A1</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A1
   * @generated
   */
  public Adapter createBz398057A1Adapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B <em>Bz398057 B</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B
   * @generated
   */
  public Adapter createBz398057BAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1 <em>Bz398057 B1</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1
   * @generated
   */
  public Adapter createBz398057B1Adapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P <em>Bz397682 P</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P
   * @generated
   */
  public Adapter createBz397682PAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C <em>Bz397682 C</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C
   * @generated
   */
  public Adapter createBz397682CAdapter()
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

} // HibernateTestAdapterFactory
