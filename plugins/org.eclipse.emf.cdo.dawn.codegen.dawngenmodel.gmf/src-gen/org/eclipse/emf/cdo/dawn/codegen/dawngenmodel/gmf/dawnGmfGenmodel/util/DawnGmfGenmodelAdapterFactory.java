/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.util;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage
 * @generated
 * @author Martin Fluegge
 */
public class DawnGmfGenmodelAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static DawnGmfGenmodelPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DawnGmfGenmodelAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = DawnGmfGenmodelPackage.eINSTANCE;
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
  protected DawnGmfGenmodelSwitch<Adapter> modelSwitch = new DawnGmfGenmodelSwitch<Adapter>()
  {
    @Override
    public Adapter caseDawnGMFGenerator(DawnGMFGenerator object)
    {
      return createDawnGMFGeneratorAdapter();
    }

    @Override
    public Adapter caseDawnFragmentGenerator(DawnFragmentGenerator object)
    {
      return createDawnFragmentGeneratorAdapter();
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
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator
   * <em>Dawn GMF Generator</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator
   * @generated
   */
  public Adapter createDawnGMFGeneratorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator <em>Dawn Fragment Generator</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator
   * @generated
   */
  public Adapter createDawnFragmentGeneratorAdapter()
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

} // DawnGmfGenmodelAdapterFactory
