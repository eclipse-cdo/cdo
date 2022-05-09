/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.modules.util;

import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides
 * an adapter <code>createXXX</code> method for each class of the model. <!--
 * end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage
 * @generated
 */
public class ModulesAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static ModulesPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public ModulesAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = ModulesPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!--
   * begin-user-doc --> This implementation returns <code>true</code> if the
   * object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
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
   * The switch that delegates to the <code>createXXX</code> methods. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ModulesSwitch<Adapter> modelSwitch = new ModulesSwitch<Adapter>()
  {
    @Override
    public Adapter caseModuleDefinition(ModuleDefinition object)
    {
      return createModuleDefinitionAdapter();
    }

    @Override
    public Adapter caseDependencyDefinition(DependencyDefinition object)
    {
      return createDependencyDefinitionAdapter();
    }

    @Override
    public Adapter caseModelElement(ModelElement object)
    {
      return createModelElementAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
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
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition <em>Module Definition</em>}'.
   * <!-- begin-user-doc --> This default implementation
   * returns null so that we can easily ignore cases; it's useful to ignore a case
   * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.modules.ModuleDefinition
   * @generated
   */
  public Adapter createModuleDefinitionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition <em>Dependency Definition</em>}'.
   * <!-- begin-user-doc --> This default implementation
   * returns null so that we can easily ignore cases; it's useful to ignore a case
   * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.modules.DependencyDefinition
   * @generated
   */
  public Adapter createDependencyDefinitionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.etypes.ModelElement <em>Model Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will
   * catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.etypes.ModelElement
   * @generated
   */
  public Adapter createModelElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc --> This
   * default implementation returns null. <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // ModulesAdapterFactory
