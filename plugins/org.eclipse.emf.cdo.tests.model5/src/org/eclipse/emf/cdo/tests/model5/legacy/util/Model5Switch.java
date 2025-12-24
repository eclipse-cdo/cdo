/**
 * Copyright (c) 2013, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.legacy.util;

import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.GenListOfBoolean;
import org.eclipse.emf.cdo.tests.model5.GenListOfChar;
import org.eclipse.emf.cdo.tests.model5.GenListOfDate;
import org.eclipse.emf.cdo.tests.model5.GenListOfDouble;
import org.eclipse.emf.cdo.tests.model5.GenListOfFloat;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.GenListOfIntArray;
import org.eclipse.emf.cdo.tests.model5.GenListOfInteger;
import org.eclipse.emf.cdo.tests.model5.GenListOfLong;
import org.eclipse.emf.cdo.tests.model5.GenListOfShort;
import org.eclipse.emf.cdo.tests.model5.GenListOfString;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.tests.model5.WithCustomType;
import org.eclipse.emf.cdo.tests.model5.legacy.Model5Package;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

//import org.eclipse.emf.cdo.tests.model5.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model5.legacy.Model5Package
 * @generated
 */
public class Model5Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static Model5Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model5Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = Model5Package.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case Model5Package.MANAGER:
    {
      Manager manager = (Manager)theEObject;
      T result = caseManager(manager);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.DOCTOR:
    {
      Doctor doctor = (Doctor)theEObject;
      T result = caseDoctor(doctor);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_STRING:
    {
      GenListOfString genListOfString = (GenListOfString)theEObject;
      T result = caseGenListOfString(genListOfString);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_INT:
    {
      GenListOfInt genListOfInt = (GenListOfInt)theEObject;
      T result = caseGenListOfInt(genListOfInt);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_INTEGER:
    {
      GenListOfInteger genListOfInteger = (GenListOfInteger)theEObject;
      T result = caseGenListOfInteger(genListOfInteger);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_LONG:
    {
      GenListOfLong genListOfLong = (GenListOfLong)theEObject;
      T result = caseGenListOfLong(genListOfLong);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_BOOLEAN:
    {
      GenListOfBoolean genListOfBoolean = (GenListOfBoolean)theEObject;
      T result = caseGenListOfBoolean(genListOfBoolean);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_SHORT:
    {
      GenListOfShort genListOfShort = (GenListOfShort)theEObject;
      T result = caseGenListOfShort(genListOfShort);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_FLOAT:
    {
      GenListOfFloat genListOfFloat = (GenListOfFloat)theEObject;
      T result = caseGenListOfFloat(genListOfFloat);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_DOUBLE:
    {
      GenListOfDouble genListOfDouble = (GenListOfDouble)theEObject;
      T result = caseGenListOfDouble(genListOfDouble);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_DATE:
    {
      GenListOfDate genListOfDate = (GenListOfDate)theEObject;
      T result = caseGenListOfDate(genListOfDate);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_CHAR:
    {
      GenListOfChar genListOfChar = (GenListOfChar)theEObject;
      T result = caseGenListOfChar(genListOfChar);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.GEN_LIST_OF_INT_ARRAY:
    {
      GenListOfIntArray genListOfIntArray = (GenListOfIntArray)theEObject;
      T result = caseGenListOfIntArray(genListOfIntArray);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.PARENT:
    {
      Parent parent = (Parent)theEObject;
      T result = caseParent(parent);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.CHILD:
    {
      Child child = (Child)theEObject;
      T result = caseChild(child);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model5Package.WITH_CUSTOM_TYPE:
    {
      WithCustomType withCustomType = (WithCustomType)theEObject;
      T result = caseWithCustomType(withCustomType);
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
   * Returns the result of interpreting the object as an instance of '<em>Manager</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Manager</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseManager(Manager object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Doctor</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Doctor</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDoctor(Doctor object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of String</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of String</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfString(GenListOfString object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Int</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Int</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfInt(GenListOfInt object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Integer</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Integer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfInteger(GenListOfInteger object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Long</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Long</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfLong(GenListOfLong object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Boolean</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Boolean</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfBoolean(GenListOfBoolean object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Short</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Short</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfShort(GenListOfShort object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Float</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Float</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfFloat(GenListOfFloat object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Double</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Double</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfDouble(GenListOfDouble object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Date</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Date</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfDate(GenListOfDate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Char</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Char</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfChar(GenListOfChar object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen List Of Int Array</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen List Of Int Array</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenListOfIntArray(GenListOfIntArray object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Parent</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Parent</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseParent(Parent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Child</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Child</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChild(Child object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>With Custom Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>With Custom Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseWithCustomType(WithCustomType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // Model5Switch
