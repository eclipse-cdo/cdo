/*
 * Copyright (c) 2013, 2015, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.util;

import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.B;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.C;
import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.D;
import org.eclipse.emf.cdo.tests.model6.E;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefault;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable;
import org.eclipse.emf.cdo.tests.model6.F;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.tests.model6.Holdable;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.MyEnumList;
import org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.tests.model6.Thing;
import org.eclipse.emf.cdo.tests.model6.UnorderedList;
import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Map;

//import org.eclipse.emf.cdo.tests.model6.*;

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
 * @see org.eclipse.emf.cdo.tests.model6.legacy.Model6Package
 * @generated
 */
public class Model6Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static Model6Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model6Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = Model6Package.eINSTANCE;
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
    case Model6Package.ROOT:
    {
      Root root = (Root)theEObject;
      T result = caseRoot(root);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.BASE_OBJECT:
    {
      BaseObject baseObject = (BaseObject)theEObject;
      T result = caseBaseObject(baseObject);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.REFERENCE_OBJECT:
    {
      ReferenceObject referenceObject = (ReferenceObject)theEObject;
      T result = caseReferenceObject(referenceObject);
      if (result == null)
      {
        result = caseBaseObject(referenceObject);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.CONTAINMENT_OBJECT:
    {
      ContainmentObject containmentObject = (ContainmentObject)theEObject;
      T result = caseContainmentObject(containmentObject);
      if (result == null)
      {
        result = caseBaseObject(containmentObject);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.UNORDERED_LIST:
    {
      UnorderedList unorderedList = (UnorderedList)theEObject;
      T result = caseUnorderedList(unorderedList);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.PROPERTIES_MAP:
    {
      PropertiesMap propertiesMap = (PropertiesMap)theEObject;
      T result = casePropertiesMap(propertiesMap);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.PROPERTIES_MAP_ENTRY:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, PropertiesMapEntryValue> propertiesMapEntry = (Map.Entry<String, PropertiesMapEntryValue>)theEObject;
      T result = casePropertiesMapEntry(propertiesMapEntry);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.PROPERTIES_MAP_ENTRY_VALUE:
    {
      PropertiesMapEntryValue propertiesMapEntryValue = (PropertiesMapEntryValue)theEObject;
      T result = casePropertiesMapEntryValue(propertiesMapEntryValue);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.A:
    {
      A a = (A)theEObject;
      T result = caseA(a);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.B:
    {
      B b = (B)theEObject;
      T result = caseB(b);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.C:
    {
      C c = (C)theEObject;
      T result = caseC(c);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.D:
    {
      D d = (D)theEObject;
      T result = caseD(d);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.E:
    {
      E e = (E)theEObject;
      T result = caseE(e);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.F:
    {
      F f = (F)theEObject;
      T result = caseF(f);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.G:
    {
      G g = (G)theEObject;
      T result = caseG(g);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.MY_ENUM_LIST:
    {
      MyEnumList myEnumList = (MyEnumList)theEObject;
      T result = caseMyEnumList(myEnumList);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.MY_ENUM_LIST_UNSETTABLE:
    {
      MyEnumListUnsettable myEnumListUnsettable = (MyEnumListUnsettable)theEObject;
      T result = caseMyEnumListUnsettable(myEnumListUnsettable);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.HOLDER:
    {
      Holder holder = (Holder)theEObject;
      T result = caseHolder(holder);
      if (result == null)
      {
        result = caseHoldable(holder);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.THING:
    {
      Thing thing = (Thing)theEObject;
      T result = caseThing(thing);
      if (result == null)
      {
        result = caseHoldable(thing);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.HOLDABLE:
    {
      Holdable holdable = (Holdable)theEObject;
      T result = caseHoldable(holdable);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.HAS_NILLABLE_ATTRIBUTE:
    {
      HasNillableAttribute hasNillableAttribute = (HasNillableAttribute)theEObject;
      T result = caseHasNillableAttribute(hasNillableAttribute);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.EMPTY_STRING_DEFAULT:
    {
      EmptyStringDefault emptyStringDefault = (EmptyStringDefault)theEObject;
      T result = caseEmptyStringDefault(emptyStringDefault);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.EMPTY_STRING_DEFAULT_UNSETTABLE:
    {
      EmptyStringDefaultUnsettable emptyStringDefaultUnsettable = (EmptyStringDefaultUnsettable)theEObject;
      T result = caseEmptyStringDefaultUnsettable(emptyStringDefaultUnsettable);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.UNSETTABLE_ATTRIBUTES:
    {
      UnsettableAttributes unsettableAttributes = (UnsettableAttributes)theEObject;
      T result = caseUnsettableAttributes(unsettableAttributes);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model6Package.CAN_REFERENCE_LEGACY:
    {
      CanReferenceLegacy canReferenceLegacy = (CanReferenceLegacy)theEObject;
      T result = caseCanReferenceLegacy(canReferenceLegacy);
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
   * Returns the result of interpreting the object as an instance of '<em>Root</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Root</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRoot(Root object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Base Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Base Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBaseObject(BaseObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Reference Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Reference Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseReferenceObject(ReferenceObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Containment Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Containment Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseContainmentObject(ContainmentObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unordered List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unordered List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnorderedList(UnorderedList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Properties Map</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Properties Map</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePropertiesMap(PropertiesMap object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Properties Map Entry</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Properties Map Entry</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePropertiesMapEntry(Map.Entry<String, PropertiesMapEntryValue> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Properties Map Entry Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Properties Map Entry Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePropertiesMapEntryValue(PropertiesMapEntryValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>A</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>A</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseA(A object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>B</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>B</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseB(B object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>C</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>C</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseC(C object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>D</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>D</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseD(D object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>E</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>E</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseE(E object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>F</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>F</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseF(F object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>G</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>G</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseG(G object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>My Enum List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>My Enum List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMyEnumList(MyEnumList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>My Enum List Unsettable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>My Enum List Unsettable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMyEnumListUnsettable(MyEnumListUnsettable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Holder</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Holder</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseHolder(Holder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Thing</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Thing</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseThing(Thing object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Holdable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Holdable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseHoldable(Holdable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Has Nillable Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Has Nillable Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseHasNillableAttribute(HasNillableAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Empty String Default</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Empty String Default</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEmptyStringDefault(EmptyStringDefault object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Empty String Default Unsettable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Empty String Default Unsettable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEmptyStringDefaultUnsettable(EmptyStringDefaultUnsettable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unsettable Attributes</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unsettable Attributes</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnsettableAttributes(UnsettableAttributes object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Can Reference Legacy</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Can Reference Legacy</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCanReferenceLegacy(CanReferenceLegacy object)
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

} // Model6Switch
