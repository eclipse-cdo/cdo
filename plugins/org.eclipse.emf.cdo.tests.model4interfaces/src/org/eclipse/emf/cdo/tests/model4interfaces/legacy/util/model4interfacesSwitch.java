/**
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4interfaces.legacy.util;

import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.INamedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

//import org.eclipse.emf.cdo.tests.model4interfaces.*;

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
 * @see org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage
 * @generated
 */
public class model4interfacesSwitch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static model4interfacesPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public model4interfacesSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = model4interfacesPackage.eINSTANCE;
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
    case model4interfacesPackage.ISINGLE_REF_CONTAINER:
    {
      ISingleRefContainer iSingleRefContainer = (ISingleRefContainer)theEObject;
      T result = caseISingleRefContainer(iSingleRefContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.ISINGLE_REF_CONTAINED_ELEMENT:
    {
      ISingleRefContainedElement iSingleRefContainedElement = (ISingleRefContainedElement)theEObject;
      T result = caseISingleRefContainedElement(iSingleRefContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.ISINGLE_REF_NON_CONTAINER:
    {
      ISingleRefNonContainer iSingleRefNonContainer = (ISingleRefNonContainer)theEObject;
      T result = caseISingleRefNonContainer(iSingleRefNonContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.ISINGLE_REF_NON_CONTAINED_ELEMENT:
    {
      ISingleRefNonContainedElement iSingleRefNonContainedElement = (ISingleRefNonContainedElement)theEObject;
      T result = caseISingleRefNonContainedElement(iSingleRefNonContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.IMULTI_REF_CONTAINER:
    {
      IMultiRefContainer iMultiRefContainer = (IMultiRefContainer)theEObject;
      T result = caseIMultiRefContainer(iMultiRefContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.IMULTI_REF_CONTAINED_ELEMENT:
    {
      IMultiRefContainedElement iMultiRefContainedElement = (IMultiRefContainedElement)theEObject;
      T result = caseIMultiRefContainedElement(iMultiRefContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.IMULTI_REF_NON_CONTAINER:
    {
      IMultiRefNonContainer iMultiRefNonContainer = (IMultiRefNonContainer)theEObject;
      T result = caseIMultiRefNonContainer(iMultiRefNonContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.IMULTI_REF_NON_CONTAINED_ELEMENT:
    {
      IMultiRefNonContainedElement iMultiRefNonContainedElement = (IMultiRefNonContainedElement)theEObject;
      T result = caseIMultiRefNonContainedElement(iMultiRefNonContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.INAMED_ELEMENT:
    {
      INamedElement iNamedElement = (INamedElement)theEObject;
      T result = caseINamedElement(iNamedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.ICONTAINED_ELEMENT_NO_PARENT_LINK:
    {
      IContainedElementNoParentLink iContainedElementNoParentLink = (IContainedElementNoParentLink)theEObject;
      T result = caseIContainedElementNoParentLink(iContainedElementNoParentLink);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.ISINGLE_REF_CONTAINER_NPL:
    {
      ISingleRefContainerNPL iSingleRefContainerNPL = (ISingleRefContainerNPL)theEObject;
      T result = caseISingleRefContainerNPL(iSingleRefContainerNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.ISINGLE_REF_NON_CONTAINER_NPL:
    {
      ISingleRefNonContainerNPL iSingleRefNonContainerNPL = (ISingleRefNonContainerNPL)theEObject;
      T result = caseISingleRefNonContainerNPL(iSingleRefNonContainerNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.IMULTI_REF_CONTAINER_NPL:
    {
      IMultiRefContainerNPL iMultiRefContainerNPL = (IMultiRefContainerNPL)theEObject;
      T result = caseIMultiRefContainerNPL(iMultiRefContainerNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4interfacesPackage.IMULTI_REF_NON_CONTAINER_NPL:
    {
      IMultiRefNonContainerNPL iMultiRefNonContainerNPL = (IMultiRefNonContainerNPL)theEObject;
      T result = caseIMultiRefNonContainerNPL(iMultiRefNonContainerNPL);
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
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefContainer(ISingleRefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Contained Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefContainedElement(ISingleRefContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Non Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Non Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefNonContainer(ISingleRefNonContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Non Contained Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefNonContainedElement(ISingleRefNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefContainer(IMultiRefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Contained Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefContainedElement(IMultiRefContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Non Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Non Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefNonContainer(IMultiRefNonContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Non Contained Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefNonContainedElement(IMultiRefNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>INamed Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>INamed Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseINamedElement(INamedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IContained Element No Parent Link</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IContained Element No Parent Link</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIContainedElementNoParentLink(IContainedElementNoParentLink object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Container NPL</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefContainerNPL(ISingleRefContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Non Container NPL</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Non Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefNonContainerNPL(ISingleRefNonContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Container NPL</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefContainerNPL(IMultiRefContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Non Container NPL</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Non Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefNonContainerNPL(IMultiRefNonContainerNPL object)
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

} // model4interfacesSwitch
