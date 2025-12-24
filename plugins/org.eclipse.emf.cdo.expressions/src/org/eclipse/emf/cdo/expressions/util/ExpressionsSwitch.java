/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.util;

import org.eclipse.emf.cdo.expressions.Access;
import org.eclipse.emf.cdo.expressions.BooleanValue;
import org.eclipse.emf.cdo.expressions.ByteValue;
import org.eclipse.emf.cdo.expressions.CharValue;
import org.eclipse.emf.cdo.expressions.ContainedObject;
import org.eclipse.emf.cdo.expressions.ContextAccess;
import org.eclipse.emf.cdo.expressions.DoubleValue;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.FloatValue;
import org.eclipse.emf.cdo.expressions.FunctionInvocation;
import org.eclipse.emf.cdo.expressions.IntValue;
import org.eclipse.emf.cdo.expressions.Invocation;
import org.eclipse.emf.cdo.expressions.LinkedExpression;
import org.eclipse.emf.cdo.expressions.LinkedObject;
import org.eclipse.emf.cdo.expressions.ListConstruction;
import org.eclipse.emf.cdo.expressions.LongValue;
import org.eclipse.emf.cdo.expressions.MemberAccess;
import org.eclipse.emf.cdo.expressions.MemberInvocation;
import org.eclipse.emf.cdo.expressions.ShortValue;
import org.eclipse.emf.cdo.expressions.StaticAccess;
import org.eclipse.emf.cdo.expressions.StringValue;
import org.eclipse.emf.cdo.expressions.Value;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

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
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage
 * @generated
 */
public class ExpressionsSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static ExpressionsPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionsSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = ExpressionsPackage.eINSTANCE;
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case ExpressionsPackage.EXPRESSION:
    {
      Expression expression = (Expression)theEObject;
      T result = caseExpression(expression);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.VALUE:
    {
      Value value = (Value)theEObject;
      T result = caseValue(value);
      if (result == null)
      {
        result = caseExpression(value);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.BOOLEAN_VALUE:
    {
      BooleanValue booleanValue = (BooleanValue)theEObject;
      T result = caseBooleanValue(booleanValue);
      if (result == null)
      {
        result = caseValue(booleanValue);
      }
      if (result == null)
      {
        result = caseExpression(booleanValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.BYTE_VALUE:
    {
      ByteValue byteValue = (ByteValue)theEObject;
      T result = caseByteValue(byteValue);
      if (result == null)
      {
        result = caseValue(byteValue);
      }
      if (result == null)
      {
        result = caseExpression(byteValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.SHORT_VALUE:
    {
      ShortValue shortValue = (ShortValue)theEObject;
      T result = caseShortValue(shortValue);
      if (result == null)
      {
        result = caseValue(shortValue);
      }
      if (result == null)
      {
        result = caseExpression(shortValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.INT_VALUE:
    {
      IntValue intValue = (IntValue)theEObject;
      T result = caseIntValue(intValue);
      if (result == null)
      {
        result = caseValue(intValue);
      }
      if (result == null)
      {
        result = caseExpression(intValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.LONG_VALUE:
    {
      LongValue longValue = (LongValue)theEObject;
      T result = caseLongValue(longValue);
      if (result == null)
      {
        result = caseValue(longValue);
      }
      if (result == null)
      {
        result = caseExpression(longValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.FLOAT_VALUE:
    {
      FloatValue floatValue = (FloatValue)theEObject;
      T result = caseFloatValue(floatValue);
      if (result == null)
      {
        result = caseValue(floatValue);
      }
      if (result == null)
      {
        result = caseExpression(floatValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.DOUBLE_VALUE:
    {
      DoubleValue doubleValue = (DoubleValue)theEObject;
      T result = caseDoubleValue(doubleValue);
      if (result == null)
      {
        result = caseValue(doubleValue);
      }
      if (result == null)
      {
        result = caseExpression(doubleValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.CHAR_VALUE:
    {
      CharValue charValue = (CharValue)theEObject;
      T result = caseCharValue(charValue);
      if (result == null)
      {
        result = caseValue(charValue);
      }
      if (result == null)
      {
        result = caseExpression(charValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.STRING_VALUE:
    {
      StringValue stringValue = (StringValue)theEObject;
      T result = caseStringValue(stringValue);
      if (result == null)
      {
        result = caseValue(stringValue);
      }
      if (result == null)
      {
        result = caseExpression(stringValue);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.INVOCATION:
    {
      Invocation invocation = (Invocation)theEObject;
      T result = caseInvocation(invocation);
      if (result == null)
      {
        result = caseExpression(invocation);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.FUNCTION_INVOCATION:
    {
      FunctionInvocation functionInvocation = (FunctionInvocation)theEObject;
      T result = caseFunctionInvocation(functionInvocation);
      if (result == null)
      {
        result = caseInvocation(functionInvocation);
      }
      if (result == null)
      {
        result = caseExpression(functionInvocation);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.MEMBER_INVOCATION:
    {
      MemberInvocation memberInvocation = (MemberInvocation)theEObject;
      T result = caseMemberInvocation(memberInvocation);
      if (result == null)
      {
        result = caseInvocation(memberInvocation);
      }
      if (result == null)
      {
        result = caseExpression(memberInvocation);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.ACCESS:
    {
      Access access = (Access)theEObject;
      T result = caseAccess(access);
      if (result == null)
      {
        result = caseExpression(access);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.STATIC_ACCESS:
    {
      StaticAccess staticAccess = (StaticAccess)theEObject;
      T result = caseStaticAccess(staticAccess);
      if (result == null)
      {
        result = caseAccess(staticAccess);
      }
      if (result == null)
      {
        result = caseExpression(staticAccess);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.MEMBER_ACCESS:
    {
      MemberAccess memberAccess = (MemberAccess)theEObject;
      T result = caseMemberAccess(memberAccess);
      if (result == null)
      {
        result = caseAccess(memberAccess);
      }
      if (result == null)
      {
        result = caseExpression(memberAccess);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.CONTEXT_ACCESS:
    {
      ContextAccess contextAccess = (ContextAccess)theEObject;
      T result = caseContextAccess(contextAccess);
      if (result == null)
      {
        result = caseAccess(contextAccess);
      }
      if (result == null)
      {
        result = caseExpression(contextAccess);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.CONTAINED_OBJECT:
    {
      ContainedObject containedObject = (ContainedObject)theEObject;
      T result = caseContainedObject(containedObject);
      if (result == null)
      {
        result = caseExpression(containedObject);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.LINKED_OBJECT:
    {
      LinkedObject linkedObject = (LinkedObject)theEObject;
      T result = caseLinkedObject(linkedObject);
      if (result == null)
      {
        result = caseExpression(linkedObject);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.LINKED_EXPRESSION:
    {
      LinkedExpression linkedExpression = (LinkedExpression)theEObject;
      T result = caseLinkedExpression(linkedExpression);
      if (result == null)
      {
        result = caseExpression(linkedExpression);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ExpressionsPackage.LIST_CONSTRUCTION:
    {
      ListConstruction listConstruction = (ListConstruction)theEObject;
      T result = caseListConstruction(listConstruction);
      if (result == null)
      {
        result = caseExpression(listConstruction);
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
   * Returns the result of interpreting the object as an instance of '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpression(Expression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseValue(Value object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Boolean Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Boolean Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBooleanValue(BooleanValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Byte Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Byte Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseByteValue(ByteValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Short Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Short Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseShortValue(ShortValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Int Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Int Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntValue(IntValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Long Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Long Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLongValue(LongValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Float Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Float Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFloatValue(FloatValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Double Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Double Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDoubleValue(DoubleValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Char Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Char Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCharValue(CharValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringValue(StringValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Invocation</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Invocation</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseInvocation(Invocation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Function Invocation</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Function Invocation</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFunctionInvocation(FunctionInvocation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Member Invocation</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Member Invocation</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMemberInvocation(MemberInvocation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Access</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Access</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAccess(Access object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Static Access</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Static Access</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStaticAccess(StaticAccess object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Member Access</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Member Access</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMemberAccess(MemberAccess object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Context Access</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Context Access</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseContextAccess(ContextAccess object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Contained Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Contained Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseContainedObject(ContainedObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Linked Object</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Linked Object</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLinkedObject(LinkedObject object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Linked Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Linked Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLinkedExpression(LinkedExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>List Construction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>List Construction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseListConstruction(ListConstruction object)
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
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} // ExpressionsSwitch
