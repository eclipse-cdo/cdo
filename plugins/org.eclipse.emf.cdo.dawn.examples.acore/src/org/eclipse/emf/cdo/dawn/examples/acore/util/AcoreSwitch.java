/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.util;

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AClassChild;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.AParameter;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage
 * @generated
 */
public class AcoreSwitch<T> extends Switch<T>
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static AcorePackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public AcoreSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = AcorePackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @parameter ePackage the package in question.
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
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case AcorePackage.ACLASS:
    {
      AClass aClass = (AClass)theEObject;
      T result = caseAClass(aClass);
      if (result == null)
        result = caseABasicClass(aClass);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.AINTERFACE:
    {
      AInterface aInterface = (AInterface)theEObject;
      T result = caseAInterface(aInterface);
      if (result == null)
        result = caseABasicClass(aInterface);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.ACORE_ROOT:
    {
      ACoreRoot aCoreRoot = (ACoreRoot)theEObject;
      T result = caseACoreRoot(aCoreRoot);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.AATTRIBUTE:
    {
      AAttribute aAttribute = (AAttribute)theEObject;
      T result = caseAAttribute(aAttribute);
      if (result == null)
        result = caseAClassChild(aAttribute);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.AOPERATION:
    {
      AOperation aOperation = (AOperation)theEObject;
      T result = caseAOperation(aOperation);
      if (result == null)
        result = caseAClassChild(aOperation);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.ABASIC_CLASS:
    {
      ABasicClass aBasicClass = (ABasicClass)theEObject;
      T result = caseABasicClass(aBasicClass);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.APARAMETER:
    {
      AParameter aParameter = (AParameter)theEObject;
      T result = caseAParameter(aParameter);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case AcorePackage.ACLASS_CHILD:
    {
      AClassChild aClassChild = (AClassChild)theEObject;
      T result = caseAClassChild(aClassChild);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AClass</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AClass</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAClass(AClass object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AInterface</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AInterface</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAInterface(AInterface object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ACore Root</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ACore Root</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseACoreRoot(ACoreRoot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AAttribute</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AAttribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAAttribute(AAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AOperation</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AOperation</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAOperation(AOperation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ABasic Class</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ABasic Class</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseABasicClass(ABasicClass object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AParameter</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AParameter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAParameter(AParameter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>AClass Child</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>AClass Child</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAClassChild(AClassChild object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} // AcoreSwitch
