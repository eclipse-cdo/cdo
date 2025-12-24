/*
 * Copyright (c) 2008-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package base.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import base.BaseClass;
import base.BasePackage;
import base.Document;
import base.Element;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * @see base.BasePackage
 * @generated
 */
public class BaseSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static BasePackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public BaseSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = BasePackage.eINSTANCE;
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
    case BasePackage.BASE_CLASS:
    {
      BaseClass baseClass = (BaseClass)theEObject;
      T result = caseBaseClass(baseClass);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case BasePackage.DOCUMENT:
    {
      Document document = (Document)theEObject;
      T result = caseDocument(document);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case BasePackage.ELEMENT:
    {
      Element element = (Element)theEObject;
      T result = caseElement(element);
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
   * Returns the result of interpreting the object as an instance of '<em>Class</em>'.
   * <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBaseClass(BaseClass object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Document</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Document</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDocument(Document object)
  {
    return null;
  }

  /**
  	 * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
  	 * <!-- begin-user-doc -->
  	 * This implementation returns null;
  	 * returning a non-null result will terminate the switch.
  	 * <!-- end-user-doc -->
  	 * @param object the target of the switch.
  	 * @return the result of interpreting the object as an instance of '<em>Element</em>'.
  	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
  	 * @generated
  	 */
  public T caseElement(Element object)
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

} // BaseSwitch
