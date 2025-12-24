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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance
 * hierarchy. It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the
 * result of the switch. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.LMPackage
 * @generated
 */
public class LMSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static LMPackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  public LMSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = LMPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case LMPackage.SYSTEM_ELEMENT:
    {
      SystemElement systemElement = (SystemElement)theEObject;
      T result = caseSystemElement(systemElement);
      if (result == null)
      {
        result = caseModelElement(systemElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.PROCESS_ELEMENT:
    {
      ProcessElement processElement = (ProcessElement)theEObject;
      T result = caseProcessElement(processElement);
      if (result == null)
      {
        result = caseSystemElement(processElement);
      }
      if (result == null)
      {
        result = caseModelElement(processElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.MODULE_ELEMENT:
    {
      ModuleElement moduleElement = (ModuleElement)theEObject;
      T result = caseModuleElement(moduleElement);
      if (result == null)
      {
        result = caseSystemElement(moduleElement);
      }
      if (result == null)
      {
        result = caseModelElement(moduleElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.STREAM_ELEMENT:
    {
      StreamElement streamElement = (StreamElement)theEObject;
      T result = caseStreamElement(streamElement);
      if (result == null)
      {
        result = caseModuleElement(streamElement);
      }
      if (result == null)
      {
        result = caseSystemElement(streamElement);
      }
      if (result == null)
      {
        result = caseModelElement(streamElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.SYSTEM:
    {
      org.eclipse.emf.cdo.lm.System system = (org.eclipse.emf.cdo.lm.System)theEObject;
      T result = caseSystem(system);
      if (result == null)
      {
        result = caseModelElement(system);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.PROCESS:
    {
      org.eclipse.emf.cdo.lm.Process process = (org.eclipse.emf.cdo.lm.Process)theEObject;
      T result = caseProcess(process);
      if (result == null)
      {
        result = caseSystemElement(process);
      }
      if (result == null)
      {
        result = caseModelElement(process);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.MODULE_TYPE:
    {
      ModuleType moduleType = (ModuleType)theEObject;
      T result = caseModuleType(moduleType);
      if (result == null)
      {
        result = caseProcessElement(moduleType);
      }
      if (result == null)
      {
        result = caseSystemElement(moduleType);
      }
      if (result == null)
      {
        result = caseModelElement(moduleType);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.DROP_TYPE:
    {
      DropType dropType = (DropType)theEObject;
      T result = caseDropType(dropType);
      if (result == null)
      {
        result = caseProcessElement(dropType);
      }
      if (result == null)
      {
        result = caseSystemElement(dropType);
      }
      if (result == null)
      {
        result = caseModelElement(dropType);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.MODULE:
    {
      org.eclipse.emf.cdo.lm.Module module = (org.eclipse.emf.cdo.lm.Module)theEObject;
      T result = caseModule(module);
      if (result == null)
      {
        result = caseSystemElement(module);
      }
      if (result == null)
      {
        result = caseModelElement(module);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.BASELINE:
    {
      Baseline baseline = (Baseline)theEObject;
      T result = caseBaseline(baseline);
      if (result == null)
      {
        result = caseStreamElement(baseline);
      }
      if (result == null)
      {
        result = caseModuleElement(baseline);
      }
      if (result == null)
      {
        result = caseSystemElement(baseline);
      }
      if (result == null)
      {
        result = caseModelElement(baseline);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.FLOATING_BASELINE:
    {
      FloatingBaseline floatingBaseline = (FloatingBaseline)theEObject;
      T result = caseFloatingBaseline(floatingBaseline);
      if (result == null)
      {
        result = caseBaseline(floatingBaseline);
      }
      if (result == null)
      {
        result = caseStreamElement(floatingBaseline);
      }
      if (result == null)
      {
        result = caseModuleElement(floatingBaseline);
      }
      if (result == null)
      {
        result = caseSystemElement(floatingBaseline);
      }
      if (result == null)
      {
        result = caseModelElement(floatingBaseline);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.FIXED_BASELINE:
    {
      FixedBaseline fixedBaseline = (FixedBaseline)theEObject;
      T result = caseFixedBaseline(fixedBaseline);
      if (result == null)
      {
        result = caseBaseline(fixedBaseline);
      }
      if (result == null)
      {
        result = caseStreamElement(fixedBaseline);
      }
      if (result == null)
      {
        result = caseModuleElement(fixedBaseline);
      }
      if (result == null)
      {
        result = caseSystemElement(fixedBaseline);
      }
      if (result == null)
      {
        result = caseModelElement(fixedBaseline);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.STREAM:
    {
      Stream stream = (Stream)theEObject;
      T result = caseStream(stream);
      if (result == null)
      {
        result = caseFloatingBaseline(stream);
      }
      if (result == null)
      {
        result = caseBaseline(stream);
      }
      if (result == null)
      {
        result = caseStreamElement(stream);
      }
      if (result == null)
      {
        result = caseModuleElement(stream);
      }
      if (result == null)
      {
        result = caseSystemElement(stream);
      }
      if (result == null)
      {
        result = caseModelElement(stream);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.CHANGE:
    {
      Change change = (Change)theEObject;
      T result = caseChange(change);
      if (result == null)
      {
        result = caseFloatingBaseline(change);
      }
      if (result == null)
      {
        result = caseBaseline(change);
      }
      if (result == null)
      {
        result = caseStreamElement(change);
      }
      if (result == null)
      {
        result = caseModuleElement(change);
      }
      if (result == null)
      {
        result = caseSystemElement(change);
      }
      if (result == null)
      {
        result = caseModelElement(change);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.DELIVERY:
    {
      Delivery delivery = (Delivery)theEObject;
      T result = caseDelivery(delivery);
      if (result == null)
      {
        result = caseFixedBaseline(delivery);
      }
      if (result == null)
      {
        result = caseBaseline(delivery);
      }
      if (result == null)
      {
        result = caseStreamElement(delivery);
      }
      if (result == null)
      {
        result = caseModuleElement(delivery);
      }
      if (result == null)
      {
        result = caseSystemElement(delivery);
      }
      if (result == null)
      {
        result = caseModelElement(delivery);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.DROP:
    {
      Drop drop = (Drop)theEObject;
      T result = caseDrop(drop);
      if (result == null)
      {
        result = caseFixedBaseline(drop);
      }
      if (result == null)
      {
        result = caseBaseline(drop);
      }
      if (result == null)
      {
        result = caseStreamElement(drop);
      }
      if (result == null)
      {
        result = caseModuleElement(drop);
      }
      if (result == null)
      {
        result = caseSystemElement(drop);
      }
      if (result == null)
      {
        result = caseModelElement(drop);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case LMPackage.DEPENDENCY:
    {
      Dependency dependency = (Dependency)theEObject;
      T result = caseDependency(dependency);
      if (result == null)
      {
        result = caseStreamElement(dependency);
      }
      if (result == null)
      {
        result = caseModuleElement(dependency);
      }
      if (result == null)
      {
        result = caseSystemElement(dependency);
      }
      if (result == null)
      {
        result = caseModelElement(dependency);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>System Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>System Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSystemElement(SystemElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Process Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Process Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProcessElement(ProcessElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Module Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Module Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModuleElement(ModuleElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Stream Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Stream Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStreamElement(StreamElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>System</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>System</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSystem(org.eclipse.emf.cdo.lm.System object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Process</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Process</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProcess(org.eclipse.emf.cdo.lm.Process object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Drop Type</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Drop Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDropType(DropType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Module</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Module</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModule(org.eclipse.emf.cdo.lm.Module object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Dependency</em>'.
   * <!-- begin-user-doc --> This implementation returns
   * null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Dependency</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDependency(Dependency object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Module Type</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Module Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModuleType(ModuleType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Baseline</em>'.
   * <!-- begin-user-doc --> This implementation returns
   * null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBaseline(Baseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Floating Baseline</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Floating Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFloatingBaseline(FloatingBaseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Fixed Baseline</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Fixed Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFixedBaseline(FixedBaseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Stream</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Stream</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStream(Stream object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Change</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Change</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChange(Change object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Delivery</em>'.
   * <!-- begin-user-doc --> This implementation returns
   * null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Delivery</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDelivery(Delivery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Drop</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Drop</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDrop(Drop object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModelElement(ModelElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc --> This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last
   * case anyway. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} // LMSwitch
