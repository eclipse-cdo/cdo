/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.util;

import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.ModuleElement;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.ProcessElement;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamElement;
import org.eclipse.emf.cdo.lm.SystemElement;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides
 * an adapter <code>createXXX</code> method for each class of the model. <!--
 * end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.LMPackage
 * @generated
 */
public class LMAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static LMPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public LMAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = LMPackage.eINSTANCE;
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
  protected LMSwitch<Adapter> modelSwitch = new LMSwitch<>()
  {
    @Override
    public Adapter caseSystemElement(SystemElement object)
    {
      return createSystemElementAdapter();
    }

    @Override
    public Adapter caseProcessElement(ProcessElement object)
    {
      return createProcessElementAdapter();
    }

    @Override
    public Adapter caseModuleElement(ModuleElement object)
    {
      return createModuleElementAdapter();
    }

    @Override
    public Adapter caseStreamElement(StreamElement object)
    {
      return createStreamElementAdapter();
    }

    @Override
    public Adapter caseSystem(org.eclipse.emf.cdo.lm.System object)
    {
      return createSystemAdapter();
    }

    @Override
    public Adapter caseProcess(org.eclipse.emf.cdo.lm.Process object)
    {
      return createProcessAdapter();
    }

    @Override
    public Adapter caseModuleType(ModuleType object)
    {
      return createModuleTypeAdapter();
    }

    @Override
    public Adapter caseDropType(DropType object)
    {
      return createDropTypeAdapter();
    }

    @Override
    public Adapter caseModule(org.eclipse.emf.cdo.lm.Module object)
    {
      return createModuleAdapter();
    }

    @Override
    public Adapter caseBaseline(Baseline object)
    {
      return createBaselineAdapter();
    }

    @Override
    public Adapter caseFloatingBaseline(FloatingBaseline object)
    {
      return createFloatingBaselineAdapter();
    }

    @Override
    public Adapter caseFixedBaseline(FixedBaseline object)
    {
      return createFixedBaselineAdapter();
    }

    @Override
    public Adapter caseStream(Stream object)
    {
      return createStreamAdapter();
    }

    @Override
    public Adapter caseChange(Change object)
    {
      return createChangeAdapter();
    }

    @Override
    public Adapter caseDelivery(Delivery object)
    {
      return createDeliveryAdapter();
    }

    @Override
    public Adapter caseDrop(Drop object)
    {
      return createDropAdapter();
    }

    @Override
    public Adapter caseDependency(Dependency object)
    {
      return createDependencyAdapter();
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
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.SystemElement <em>System Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will
   * catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.SystemElement
   * @generated
   */
  public Adapter createSystemElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.ProcessElement <em>Process Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.ProcessElement
   * @generated
   */
  public Adapter createProcessElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.ModuleElement <em>Module Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will
   * catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.ModuleElement
   * @generated
   */
  public Adapter createModuleElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.StreamElement <em>Stream Element</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will
   * catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.StreamElement
   * @generated
   */
  public Adapter createStreamElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.System <em>System</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.System
   * @generated
   */
  public Adapter createSystemAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Process <em>Process</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Process
   * @generated
   */
  public Adapter createProcessAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.DropType <em>Drop Type</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.DropType
   * @generated
   */
  public Adapter createDropTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Module <em>Module</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Module
   * @generated
   */
  public Adapter createModuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Dependency <em>Dependency</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Dependency
   * @generated
   */
  public Adapter createDependencyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.ModuleType <em>Module Type</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.ModuleType
   * @generated
   */
  public Adapter createModuleTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Baseline <em>Baseline</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Baseline
   * @generated
   */
  public Adapter createBaselineAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.FloatingBaseline <em>Floating Baseline</em>}'.
   * <!-- begin-user-doc --> This default implementation returns
   * null so that we can easily ignore cases; it's useful to ignore a case when
   * inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.FloatingBaseline
   * @generated
   */
  public Adapter createFloatingBaselineAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.lm.FixedBaseline <em>Fixed Baseline</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will
   * catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.FixedBaseline
   * @generated
   */
  public Adapter createFixedBaselineAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Stream <em>Stream</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Stream
   * @generated
   */
  public Adapter createStreamAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Change <em>Change</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Change
   * @generated
   */
  public Adapter createChangeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Delivery <em>Delivery</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Delivery
   * @generated
   */
  public Adapter createDeliveryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class
   * '{@link org.eclipse.emf.cdo.lm.Drop <em>Drop</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.lm.Drop
   * @generated
   */
  public Adapter createDropAdapter()
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

} // LMAdapterFactory
