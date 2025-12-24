/*
 * Copyright (c) 2008-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.util;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.EnumListHolder;
import org.eclipse.emf.cdo.tests.model2.MapHolder;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.NotUnsettable;
import org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault;
import org.eclipse.emf.cdo.tests.model2.PersistentContainment;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.model2.TransientContainer;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import java.util.Map;

//import org.eclipse.emf.cdo.tests.model2.*;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package
 * @generated
 */
public class Model2Switch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static Model2Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model2Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = Model2Package.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER:
    {
      SpecialPurchaseOrder specialPurchaseOrder = (SpecialPurchaseOrder)theEObject;
      T result = caseSpecialPurchaseOrder(specialPurchaseOrder);
      if (result == null)
      {
        result = casePurchaseOrder(specialPurchaseOrder);
      }
      if (result == null)
      {
        result = caseOrder(specialPurchaseOrder);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.TASK_CONTAINER:
    {
      TaskContainer taskContainer = (TaskContainer)theEObject;
      T result = caseTaskContainer(taskContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.TASK:
    {
      Task task = (Task)theEObject;
      T result = caseTask(task);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.UNSETTABLE1:
    {
      Unsettable1 unsettable1 = (Unsettable1)theEObject;
      T result = caseUnsettable1(unsettable1);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.UNSETTABLE2_WITH_DEFAULT:
    {
      Unsettable2WithDefault unsettable2WithDefault = (Unsettable2WithDefault)theEObject;
      T result = caseUnsettable2WithDefault(unsettable2WithDefault);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.PERSISTENT_CONTAINMENT:
    {
      PersistentContainment persistentContainment = (PersistentContainment)theEObject;
      T result = casePersistentContainment(persistentContainment);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.TRANSIENT_CONTAINER:
    {
      TransientContainer transientContainer = (TransientContainer)theEObject;
      T result = caseTransientContainer(transientContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.NOT_UNSETTABLE:
    {
      NotUnsettable notUnsettable = (NotUnsettable)theEObject;
      T result = caseNotUnsettable(notUnsettable);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT:
    {
      NotUnsettableWithDefault notUnsettableWithDefault = (NotUnsettableWithDefault)theEObject;
      T result = caseNotUnsettableWithDefault(notUnsettableWithDefault);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.MAP_HOLDER:
    {
      MapHolder mapHolder = (MapHolder)theEObject;
      T result = caseMapHolder(mapHolder);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.STRING_TO_STRING_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, String> stringToStringMap = (Map.Entry<String, String>)theEObject;
      T result = caseStringToStringMap(stringToStringMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.INTEGER_TO_STRING_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<Integer, String> integerToStringMap = (Map.Entry<Integer, String>)theEObject;
      T result = caseIntegerToStringMap(integerToStringMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.STRING_TO_VAT_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, VAT> stringToVATMap = (Map.Entry<String, VAT>)theEObject;
      T result = caseStringToVATMap(stringToVATMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.STRING_TO_ADDRESS_CONTAINMENT_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, Address> stringToAddressContainmentMap = (Map.Entry<String, Address>)theEObject;
      T result = caseStringToAddressContainmentMap(stringToAddressContainmentMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.STRING_TO_ADDRESS_REFERENCE_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, Address> stringToAddressReferenceMap = (Map.Entry<String, Address>)theEObject;
      T result = caseStringToAddressReferenceMap(stringToAddressReferenceMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.EOBJECT_TO_EOBJECT_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<EObject, EObject> eObjectToEObjectMap = (Map.Entry<EObject, EObject>)theEObject;
      T result = caseEObjectToEObjectMap(eObjectToEObjectMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<EObject, EObject> eObjectToEObjectKeyContainedMap = (Map.Entry<EObject, EObject>)theEObject;
      T result = caseEObjectToEObjectKeyContainedMap(eObjectToEObjectKeyContainedMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<EObject, EObject> eObjectToEObjectBothContainedMap = (Map.Entry<EObject, EObject>)theEObject;
      T result = caseEObjectToEObjectBothContainedMap(eObjectToEObjectBothContainedMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<EObject, EObject> eObjectToEObjectValueContainedMap = (Map.Entry<EObject, EObject>)theEObject;
      T result = caseEObjectToEObjectValueContainedMap(eObjectToEObjectValueContainedMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model2Package.ENUM_LIST_HOLDER:
    {
      EnumListHolder enumListHolder = (EnumListHolder)theEObject;
      T result = caseEnumListHolder(enumListHolder);
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
   * Returns the result of interpreting the object as an instance of '<em>Special Purchase Order</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Special Purchase Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSpecialPurchaseOrder(SpecialPurchaseOrder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Task Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Task Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTaskContainer(TaskContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Task</em>'.
   * <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Task</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTask(Task object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unsettable1</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unsettable1</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnsettable1(Unsettable1 object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unsettable2 With Default</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unsettable2 With Default</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnsettable2WithDefault(Unsettable2WithDefault object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Persistent Containment</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Persistent Containment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePersistentContainment(PersistentContainment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Transient Container</em>'.
   * <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Transient Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTransientContainer(TransientContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Not Unsettable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Not Unsettable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNotUnsettable(NotUnsettable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Not Unsettable With Default</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Not Unsettable With Default</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNotUnsettableWithDefault(NotUnsettableWithDefault object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Map Holder</em>'.
   * <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Map Holder</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMapHolder(MapHolder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String To String Map</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String To String Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringToStringMap(Map.Entry<String, String> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer To String Map</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer To String Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerToStringMap(Map.Entry<Integer, String> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String To VAT Map</em>'.
   * <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String To VAT Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringToVATMap(Map.Entry<String, VAT> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String To Address Containment Map</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String To Address Containment Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringToAddressContainmentMap(Map.Entry<String, Address> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String To Address Reference Map</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String To Address Reference Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringToAddressReferenceMap(Map.Entry<String, Address> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject To EObject Map</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject To EObject Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEObjectToEObjectMap(Map.Entry<EObject, EObject> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject To EObject Key Contained Map</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject To EObject Key Contained Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEObjectToEObjectKeyContainedMap(Map.Entry<EObject, EObject> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject To EObject Both Contained Map</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject To EObject Both Contained Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEObjectToEObjectBothContainedMap(Map.Entry<EObject, EObject> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject To EObject Value Contained Map</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject To EObject Value Contained Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEObjectToEObjectValueContainedMap(Map.Entry<EObject, EObject> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Enum List Holder</em>'.
   * <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Enum List Holder</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEnumListHolder(EnumListHolder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Order</em>'.
   * <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrder(Order object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Purchase Order</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Purchase Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePurchaseOrder(PurchaseOrder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
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

} // Model2Switch
