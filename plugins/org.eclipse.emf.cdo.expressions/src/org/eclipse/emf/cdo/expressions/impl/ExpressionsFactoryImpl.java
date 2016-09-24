/*
 * Copyright (c) 2013, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.BooleanValue;
import org.eclipse.emf.cdo.expressions.ByteValue;
import org.eclipse.emf.cdo.expressions.CharValue;
import org.eclipse.emf.cdo.expressions.ContainedObject;
import org.eclipse.emf.cdo.expressions.ContextAccess;
import org.eclipse.emf.cdo.expressions.DoubleValue;
import org.eclipse.emf.cdo.expressions.ExpressionsFactory;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.FloatValue;
import org.eclipse.emf.cdo.expressions.FunctionInvocation;
import org.eclipse.emf.cdo.expressions.IntValue;
import org.eclipse.emf.cdo.expressions.LinkedExpression;
import org.eclipse.emf.cdo.expressions.LinkedObject;
import org.eclipse.emf.cdo.expressions.ListConstruction;
import org.eclipse.emf.cdo.expressions.LongValue;
import org.eclipse.emf.cdo.expressions.MemberAccess;
import org.eclipse.emf.cdo.expressions.MemberInvocation;
import org.eclipse.emf.cdo.expressions.ShortValue;
import org.eclipse.emf.cdo.expressions.StaticAccess;
import org.eclipse.emf.cdo.expressions.StringValue;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ExpressionsFactoryImpl extends EFactoryImpl implements ExpressionsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static ExpressionsFactory init()
  {
    try
    {
      ExpressionsFactory theExpressionsFactory = (ExpressionsFactory)EPackage.Registry.INSTANCE
          .getEFactory(ExpressionsPackage.eNS_URI);
      if (theExpressionsFactory != null)
      {
        return theExpressionsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new ExpressionsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case ExpressionsPackage.BOOLEAN_VALUE:
      return (EObject)createBooleanValue();
    case ExpressionsPackage.BYTE_VALUE:
      return (EObject)createByteValue();
    case ExpressionsPackage.SHORT_VALUE:
      return (EObject)createShortValue();
    case ExpressionsPackage.INT_VALUE:
      return (EObject)createIntValue();
    case ExpressionsPackage.LONG_VALUE:
      return (EObject)createLongValue();
    case ExpressionsPackage.FLOAT_VALUE:
      return (EObject)createFloatValue();
    case ExpressionsPackage.DOUBLE_VALUE:
      return (EObject)createDoubleValue();
    case ExpressionsPackage.CHAR_VALUE:
      return (EObject)createCharValue();
    case ExpressionsPackage.STRING_VALUE:
      return (EObject)createStringValue();
    case ExpressionsPackage.FUNCTION_INVOCATION:
      return (EObject)createFunctionInvocation();
    case ExpressionsPackage.MEMBER_INVOCATION:
      return (EObject)createMemberInvocation();
    case ExpressionsPackage.STATIC_ACCESS:
      return (EObject)createStaticAccess();
    case ExpressionsPackage.MEMBER_ACCESS:
      return (EObject)createMemberAccess();
    case ExpressionsPackage.CONTEXT_ACCESS:
      return (EObject)createContextAccess();
    case ExpressionsPackage.CONTAINED_OBJECT:
      return (EObject)createContainedObject();
    case ExpressionsPackage.LINKED_OBJECT:
      return (EObject)createLinkedObject();
    case ExpressionsPackage.LINKED_EXPRESSION:
      return (EObject)createLinkedExpression();
    case ExpressionsPackage.LIST_CONSTRUCTION:
      return (EObject)createListConstruction();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanValue createBooleanValue()
  {
    BooleanValueImpl booleanValue = new BooleanValueImpl();
    return booleanValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ByteValue createByteValue()
  {
    ByteValueImpl byteValue = new ByteValueImpl();
    return byteValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ShortValue createShortValue()
  {
    ShortValueImpl shortValue = new ShortValueImpl();
    return shortValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntValue createIntValue()
  {
    IntValueImpl intValue = new IntValueImpl();
    return intValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LongValue createLongValue()
  {
    LongValueImpl longValue = new LongValueImpl();
    return longValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FloatValue createFloatValue()
  {
    FloatValueImpl floatValue = new FloatValueImpl();
    return floatValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DoubleValue createDoubleValue()
  {
    DoubleValueImpl doubleValue = new DoubleValueImpl();
    return doubleValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CharValue createCharValue()
  {
    CharValueImpl charValue = new CharValueImpl();
    return charValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StringValue createStringValue()
  {
    StringValueImpl stringValue = new StringValueImpl();
    return stringValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionInvocation createFunctionInvocation()
  {
    FunctionInvocationImpl functionInvocation = new FunctionInvocationImpl();
    return functionInvocation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberInvocation createMemberInvocation()
  {
    MemberInvocationImpl memberInvocation = new MemberInvocationImpl();
    return memberInvocation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StaticAccess createStaticAccess()
  {
    StaticAccessImpl staticAccess = new StaticAccessImpl();
    return staticAccess;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberAccess createMemberAccess()
  {
    MemberAccessImpl memberAccess = new MemberAccessImpl();
    return memberAccess;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ContextAccess createContextAccess()
  {
    ContextAccessImpl contextAccess = new ContextAccessImpl();
    return contextAccess;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ContainedObject createContainedObject()
  {
    ContainedObjectImpl containedObject = new ContainedObjectImpl();
    return containedObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LinkedObject createLinkedObject()
  {
    LinkedObjectImpl linkedObject = new LinkedObjectImpl();
    return linkedObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LinkedExpression createLinkedExpression()
  {
    LinkedExpressionImpl linkedExpression = new LinkedExpressionImpl();
    return linkedExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ListConstruction createListConstruction()
  {
    ListConstructionImpl listConstruction = new ListConstructionImpl();
    return listConstruction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionsPackage getExpressionsPackage()
  {
    return (ExpressionsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static ExpressionsPackage getPackage()
  {
    return ExpressionsPackage.eINSTANCE;
  }

} // ExpressionsFactoryImpl
