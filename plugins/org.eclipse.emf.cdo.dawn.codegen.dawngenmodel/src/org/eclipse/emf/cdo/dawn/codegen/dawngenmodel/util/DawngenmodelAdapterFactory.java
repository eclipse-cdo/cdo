/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.util;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage
 * @generated
 */
public class DawngenmodelAdapterFactory extends AdapterFactoryImpl
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static DawngenmodelPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawngenmodelAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = DawngenmodelPackage.eINSTANCE;
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
  protected DawngenmodelSwitch<Adapter> modelSwitch = new DawngenmodelSwitch<Adapter>()
  {
    @Override
    public Adapter caseDawnGenerator(DawnGenerator object)
    {
      return createDawnGeneratorAdapter();
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
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator
   * <em>Dawn Generator</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator
   * @generated
   * @since 1.0
   */
  public Adapter createDawnGeneratorAdapter()
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
   * @since 1.0
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

} // DawngenmodelAdapterFactory
