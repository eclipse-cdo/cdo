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
package org.eclipse.emf.cdo.tests.model4.util;

import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Package;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Map;

//import org.eclipse.emf.cdo.tests.model4.*;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4.model4Package
 * @generated
 */
public class model4Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static model4Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public model4Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = model4Package.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case model4Package.REF_SINGLE_CONTAINED:
    {
      RefSingleContained refSingleContained = (RefSingleContained)theEObject;
      T result = caseRefSingleContained(refSingleContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.SINGLE_CONTAINED_ELEMENT:
    {
      SingleContainedElement singleContainedElement = (SingleContainedElement)theEObject;
      T result = caseSingleContainedElement(singleContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_SINGLE_NON_CONTAINED:
    {
      RefSingleNonContained refSingleNonContained = (RefSingleNonContained)theEObject;
      T result = caseRefSingleNonContained(refSingleNonContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.SINGLE_NON_CONTAINED_ELEMENT:
    {
      SingleNonContainedElement singleNonContainedElement = (SingleNonContainedElement)theEObject;
      T result = caseSingleNonContainedElement(singleNonContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_MULTI_CONTAINED:
    {
      RefMultiContained refMultiContained = (RefMultiContained)theEObject;
      T result = caseRefMultiContained(refMultiContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.MULTI_CONTAINED_ELEMENT:
    {
      MultiContainedElement multiContainedElement = (MultiContainedElement)theEObject;
      T result = caseMultiContainedElement(multiContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_MULTI_NON_CONTAINED:
    {
      RefMultiNonContained refMultiNonContained = (RefMultiNonContained)theEObject;
      T result = caseRefMultiNonContained(refMultiNonContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.MULTI_NON_CONTAINED_ELEMENT:
    {
      MultiNonContainedElement multiNonContainedElement = (MultiNonContainedElement)theEObject;
      T result = caseMultiNonContainedElement(multiNonContainedElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_MULTI_NON_CONTAINED_UNSETTABLE:
    {
      RefMultiNonContainedUnsettable refMultiNonContainedUnsettable = (RefMultiNonContainedUnsettable)theEObject;
      T result = caseRefMultiNonContainedUnsettable(refMultiNonContainedUnsettable);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT:
    {
      MultiNonContainedUnsettableElement multiNonContainedUnsettableElement = (MultiNonContainedUnsettableElement)theEObject;
      T result = caseMultiNonContainedUnsettableElement(multiNonContainedUnsettableElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_SINGLE_CONTAINED_NPL:
    {
      RefSingleContainedNPL refSingleContainedNPL = (RefSingleContainedNPL)theEObject;
      T result = caseRefSingleContainedNPL(refSingleContainedNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_SINGLE_NON_CONTAINED_NPL:
    {
      RefSingleNonContainedNPL refSingleNonContainedNPL = (RefSingleNonContainedNPL)theEObject;
      T result = caseRefSingleNonContainedNPL(refSingleNonContainedNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_MULTI_CONTAINED_NPL:
    {
      RefMultiContainedNPL refMultiContainedNPL = (RefMultiContainedNPL)theEObject;
      T result = caseRefMultiContainedNPL(refMultiContainedNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.REF_MULTI_NON_CONTAINED_NPL:
    {
      RefMultiNonContainedNPL refMultiNonContainedNPL = (RefMultiNonContainedNPL)theEObject;
      T result = caseRefMultiNonContainedNPL(refMultiNonContainedNPL);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.CONTAINED_ELEMENT_NO_OPPOSITE:
    {
      ContainedElementNoOpposite containedElementNoOpposite = (ContainedElementNoOpposite)theEObject;
      T result = caseContainedElementNoOpposite(containedElementNoOpposite);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.GEN_REF_SINGLE_CONTAINED:
    {
      GenRefSingleContained genRefSingleContained = (GenRefSingleContained)theEObject;
      T result = caseGenRefSingleContained(genRefSingleContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.GEN_REF_SINGLE_NON_CONTAINED:
    {
      GenRefSingleNonContained genRefSingleNonContained = (GenRefSingleNonContained)theEObject;
      T result = caseGenRefSingleNonContained(genRefSingleNonContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.GEN_REF_MULTI_CONTAINED:
    {
      GenRefMultiContained genRefMultiContained = (GenRefMultiContained)theEObject;
      T result = caseGenRefMultiContained(genRefMultiContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.GEN_REF_MULTI_NON_CONTAINED:
    {
      GenRefMultiNonContained genRefMultiNonContained = (GenRefMultiNonContained)theEObject;
      T result = caseGenRefMultiNonContained(genRefMultiNonContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_SINGLE_REF_CONTAINER:
    {
      ImplSingleRefContainer implSingleRefContainer = (ImplSingleRefContainer)theEObject;
      T result = caseImplSingleRefContainer(implSingleRefContainer);
      if (result == null)
      {
        result = caseISingleRefContainer(implSingleRefContainer);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_SINGLE_REF_CONTAINED_ELEMENT:
    {
      ImplSingleRefContainedElement implSingleRefContainedElement = (ImplSingleRefContainedElement)theEObject;
      T result = caseImplSingleRefContainedElement(implSingleRefContainedElement);
      if (result == null)
      {
        result = caseISingleRefContainedElement(implSingleRefContainedElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER:
    {
      ImplSingleRefNonContainer implSingleRefNonContainer = (ImplSingleRefNonContainer)theEObject;
      T result = caseImplSingleRefNonContainer(implSingleRefNonContainer);
      if (result == null)
      {
        result = caseISingleRefNonContainer(implSingleRefNonContainer);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT:
    {
      ImplSingleRefNonContainedElement implSingleRefNonContainedElement = (ImplSingleRefNonContainedElement)theEObject;
      T result = caseImplSingleRefNonContainedElement(implSingleRefNonContainedElement);
      if (result == null)
      {
        result = caseISingleRefNonContainedElement(implSingleRefNonContainedElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER:
    {
      ImplMultiRefNonContainer implMultiRefNonContainer = (ImplMultiRefNonContainer)theEObject;
      T result = caseImplMultiRefNonContainer(implMultiRefNonContainer);
      if (result == null)
      {
        result = caseIMultiRefNonContainer(implMultiRefNonContainer);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_MULTI_REF_NON_CONTAINED_ELEMENT:
    {
      ImplMultiRefNonContainedElement implMultiRefNonContainedElement = (ImplMultiRefNonContainedElement)theEObject;
      T result = caseImplMultiRefNonContainedElement(implMultiRefNonContainedElement);
      if (result == null)
      {
        result = caseIMultiRefNonContainedElement(implMultiRefNonContainedElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_MULTI_REF_CONTAINER:
    {
      ImplMultiRefContainer implMultiRefContainer = (ImplMultiRefContainer)theEObject;
      T result = caseImplMultiRefContainer(implMultiRefContainer);
      if (result == null)
      {
        result = caseIMultiRefContainer(implMultiRefContainer);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_MULTI_REF_CONTAINED_ELEMENT:
    {
      ImplMultiRefContainedElement implMultiRefContainedElement = (ImplMultiRefContainedElement)theEObject;
      T result = caseImplMultiRefContainedElement(implMultiRefContainedElement);
      if (result == null)
      {
        result = caseIMultiRefContainedElement(implMultiRefContainedElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_SINGLE_REF_CONTAINER_NPL:
    {
      ImplSingleRefContainerNPL implSingleRefContainerNPL = (ImplSingleRefContainerNPL)theEObject;
      T result = caseImplSingleRefContainerNPL(implSingleRefContainerNPL);
      if (result == null)
      {
        result = caseISingleRefContainerNPL(implSingleRefContainerNPL);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER_NPL:
    {
      ImplSingleRefNonContainerNPL implSingleRefNonContainerNPL = (ImplSingleRefNonContainerNPL)theEObject;
      T result = caseImplSingleRefNonContainerNPL(implSingleRefNonContainerNPL);
      if (result == null)
      {
        result = caseISingleRefNonContainerNPL(implSingleRefNonContainerNPL);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_MULTI_REF_CONTAINER_NPL:
    {
      ImplMultiRefContainerNPL implMultiRefContainerNPL = (ImplMultiRefContainerNPL)theEObject;
      T result = caseImplMultiRefContainerNPL(implMultiRefContainerNPL);
      if (result == null)
      {
        result = caseIMultiRefContainerNPL(implMultiRefContainerNPL);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL:
    {
      ImplMultiRefNonContainerNPL implMultiRefNonContainerNPL = (ImplMultiRefNonContainerNPL)theEObject;
      T result = caseImplMultiRefNonContainerNPL(implMultiRefNonContainerNPL);
      if (result == null)
      {
        result = caseIMultiRefNonContainerNPL(implMultiRefNonContainerNPL);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.IMPL_CONTAINED_ELEMENT_NPL:
    {
      ImplContainedElementNPL implContainedElementNPL = (ImplContainedElementNPL)theEObject;
      T result = caseImplContainedElementNPL(implContainedElementNPL);
      if (result == null)
      {
        result = caseIContainedElementNoParentLink(implContainedElementNPL);
      }
      if (result == null)
      {
        result = caseINamedElement(implContainedElementNPL);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.GEN_REF_MULTI_NU_NON_CONTAINED:
    {
      GenRefMultiNUNonContained genRefMultiNUNonContained = (GenRefMultiNUNonContained)theEObject;
      T result = caseGenRefMultiNUNonContained(genRefMultiNUNonContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.GEN_REF_MAP_NON_CONTAINED:
    {
      GenRefMapNonContained genRefMapNonContained = (GenRefMapNonContained)theEObject;
      T result = caseGenRefMapNonContained(genRefMapNonContained);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case model4Package.STRING_TO_EOBJECT:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<String, EObject> stringToEObject = (Map.Entry<String, EObject>)theEObject;
      T result = caseStringToEObject(stringToEObject);
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
   * Returns the result of interpreting the object as an instance of '<em>Ref Single Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Single Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefSingleContained(RefSingleContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Single Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Single Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSingleContainedElement(SingleContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Single Non Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Single Non Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefSingleNonContained(RefSingleNonContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Single Non Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Single Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSingleNonContainedElement(SingleNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Multi Contained</em>'.
   * <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Multi Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefMultiContained(RefMultiContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Multi Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Multi Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMultiContainedElement(MultiContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Multi Non Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Multi Non Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefMultiNonContained(RefMultiNonContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Multi Non Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Multi Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMultiNonContainedElement(MultiNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Multi Non Contained Unsettable</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Multi Non Contained Unsettable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefMultiNonContainedUnsettable(RefMultiNonContainedUnsettable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Multi Non Contained Unsettable Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Multi Non Contained Unsettable Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMultiNonContainedUnsettableElement(MultiNonContainedUnsettableElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Single Contained NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Single Contained NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefSingleContainedNPL(RefSingleContainedNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Single Non Contained NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Single Non Contained NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefSingleNonContainedNPL(RefSingleNonContainedNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Multi Contained NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Multi Contained NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefMultiContainedNPL(RefMultiContainedNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Multi Non Contained NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Multi Non Contained NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefMultiNonContainedNPL(RefMultiNonContainedNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Contained Element No Opposite</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Contained Element No Opposite</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseContainedElementNoOpposite(ContainedElementNoOpposite object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen Ref Single Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen Ref Single Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenRefSingleContained(GenRefSingleContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen Ref Single Non Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen Ref Single Non Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenRefSingleNonContained(GenRefSingleNonContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen Ref Multi Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen Ref Multi Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenRefMultiContained(GenRefMultiContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen Ref Multi Non Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen Ref Multi Non Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenRefMultiNonContained(GenRefMultiNonContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Single Ref Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Single Ref Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplSingleRefContainer(ImplSingleRefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Single Ref Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Single Ref Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplSingleRefContainedElement(ImplSingleRefContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Single Ref Non Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Single Ref Non Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplSingleRefNonContainer(ImplSingleRefNonContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Single Ref Non Contained Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Single Ref Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplSingleRefNonContainedElement(ImplSingleRefNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Multi Ref Non Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Multi Ref Non Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplMultiRefNonContainer(ImplMultiRefNonContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Multi Ref Non Contained Element</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Multi Ref Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplMultiRefNonContainedElement(ImplMultiRefNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Multi Ref Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Multi Ref Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplMultiRefContainer(ImplMultiRefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Multi Ref Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Multi Ref Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplMultiRefContainedElement(ImplMultiRefContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Single Ref Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Single Ref Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplSingleRefContainerNPL(ImplSingleRefContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Single Ref Non Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Single Ref Non Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplSingleRefNonContainerNPL(ImplSingleRefNonContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Multi Ref Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Multi Ref Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplMultiRefContainerNPL(ImplMultiRefContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Multi Ref Non Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Multi Ref Non Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplMultiRefNonContainerNPL(ImplMultiRefNonContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Impl Contained Element NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Impl Contained Element NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImplContainedElementNPL(ImplContainedElementNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen Ref Multi NU Non Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen Ref Multi NU Non Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenRefMultiNUNonContained(GenRefMultiNUNonContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Gen Ref Map Non Contained</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Gen Ref Map Non Contained</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenRefMapNonContained(GenRefMapNonContained object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String To EObject</em>'.
   * <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String To EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringToEObject(Map.Entry<String, EObject> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefContainer(ISingleRefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefContainedElement(ISingleRefContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Non Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Non Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefNonContainer(ISingleRefNonContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Non Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefNonContainedElement(ISingleRefNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Non Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Non Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefNonContainer(IMultiRefNonContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Non Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Non Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefNonContainedElement(IMultiRefNonContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefContainer(IMultiRefContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Contained Element</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Contained Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefContainedElement(IMultiRefContainedElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefContainerNPL(ISingleRefContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>ISingle Ref Non Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>ISingle Ref Non Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseISingleRefNonContainerNPL(ISingleRefNonContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefContainerNPL(IMultiRefContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMulti Ref Non Container NPL</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMulti Ref Non Container NPL</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMultiRefNonContainerNPL(IMultiRefNonContainerNPL object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IContained Element No Parent Link</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IContained Element No Parent Link</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIContainedElementNoParentLink(IContainedElementNoParentLink object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>INamed Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
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
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // model4Switch
