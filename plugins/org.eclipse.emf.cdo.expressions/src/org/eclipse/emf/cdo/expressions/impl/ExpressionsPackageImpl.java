/*
 * Copyright (c) 2013, 2015, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.Access;
import org.eclipse.emf.cdo.expressions.BooleanValue;
import org.eclipse.emf.cdo.expressions.ByteValue;
import org.eclipse.emf.cdo.expressions.CharValue;
import org.eclipse.emf.cdo.expressions.ContainedObject;
import org.eclipse.emf.cdo.expressions.ContextAccess;
import org.eclipse.emf.cdo.expressions.DoubleValue;
import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsFactory;
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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ExpressionsPackageImpl extends EPackageImpl implements ExpressionsPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass valueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass booleanValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass byteValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass shortValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass intValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass longValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass floatValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass doubleValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass charValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stringValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass invocationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass functionInvocationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass memberInvocationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass accessEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass staticAccessEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass memberAccessEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass contextAccessEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass containedObjectEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass linkedObjectEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass linkedExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass listConstructionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType evaluationContextEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private ExpressionsPackageImpl()
  {
    super(eNS_URI, ExpressionsFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link ExpressionsPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static ExpressionsPackage init()
  {
    if (isInited)
    {
      return (ExpressionsPackage)EPackage.Registry.INSTANCE.getEPackage(ExpressionsPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredExpressionsPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    ExpressionsPackageImpl theExpressionsPackage = registeredExpressionsPackage instanceof ExpressionsPackageImpl
        ? (ExpressionsPackageImpl)registeredExpressionsPackage
        : new ExpressionsPackageImpl();

    isInited = true;

    // Create package meta-data objects
    theExpressionsPackage.createPackageContents();

    // Initialize created meta-data
    theExpressionsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theExpressionsPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(ExpressionsPackage.eNS_URI, theExpressionsPackage);
    return theExpressionsPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getExpression()
  {
    return expressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getExpression__Evaluate__EvaluationContext()
  {
    return expressionEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getValue()
  {
    return valueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EOperation getValue__GetLiteral()
  {
    return valueEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getBooleanValue()
  {
    return booleanValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBooleanValue_Literal()
  {
    return (EAttribute)booleanValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getByteValue()
  {
    return byteValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getByteValue_Literal()
  {
    return (EAttribute)byteValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getShortValue()
  {
    return shortValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getShortValue_Literal()
  {
    return (EAttribute)shortValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntValue()
  {
    return intValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntValue_Literal()
  {
    return (EAttribute)intValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLongValue()
  {
    return longValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getLongValue_Literal()
  {
    return (EAttribute)longValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFloatValue()
  {
    return floatValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getFloatValue_Literal()
  {
    return (EAttribute)floatValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDoubleValue()
  {
    return doubleValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDoubleValue_Literal()
  {
    return (EAttribute)doubleValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCharValue()
  {
    return charValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getCharValue_Literal()
  {
    return (EAttribute)charValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringValue()
  {
    return stringValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringValue_Literal()
  {
    return (EAttribute)stringValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getInvocation()
  {
    return invocationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getInvocation_Arguments()
  {
    return (EReference)invocationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getInvocation_Name()
  {
    return (EReference)invocationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFunctionInvocation()
  {
    return functionInvocationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMemberInvocation()
  {
    return memberInvocationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMemberInvocation_Object()
  {
    return (EReference)memberInvocationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAccess()
  {
    return accessEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAccess_Name()
  {
    return (EReference)accessEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStaticAccess()
  {
    return staticAccessEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMemberAccess()
  {
    return memberAccessEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMemberAccess_Object()
  {
    return (EReference)memberAccessEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getContextAccess()
  {
    return contextAccessEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getContainedObject()
  {
    return containedObjectEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getContainedObject_Object()
  {
    return (EReference)containedObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLinkedObject()
  {
    return linkedObjectEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getLinkedObject_Object()
  {
    return (EReference)linkedObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getLinkedExpression()
  {
    return linkedExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getLinkedExpression_Expression()
  {
    return (EReference)linkedExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getListConstruction()
  {
    return listConstructionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getListConstruction_Elements()
  {
    return (EReference)listConstructionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getEvaluationContext()
  {
    return evaluationContextEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ExpressionsFactory getExpressionsFactory()
  {
    return (ExpressionsFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    expressionEClass = createEClass(EXPRESSION);
    createEOperation(expressionEClass, EXPRESSION___EVALUATE__EVALUATIONCONTEXT);

    valueEClass = createEClass(VALUE);
    createEOperation(valueEClass, VALUE___GET_LITERAL);

    booleanValueEClass = createEClass(BOOLEAN_VALUE);
    createEAttribute(booleanValueEClass, BOOLEAN_VALUE__LITERAL);

    byteValueEClass = createEClass(BYTE_VALUE);
    createEAttribute(byteValueEClass, BYTE_VALUE__LITERAL);

    shortValueEClass = createEClass(SHORT_VALUE);
    createEAttribute(shortValueEClass, SHORT_VALUE__LITERAL);

    intValueEClass = createEClass(INT_VALUE);
    createEAttribute(intValueEClass, INT_VALUE__LITERAL);

    longValueEClass = createEClass(LONG_VALUE);
    createEAttribute(longValueEClass, LONG_VALUE__LITERAL);

    floatValueEClass = createEClass(FLOAT_VALUE);
    createEAttribute(floatValueEClass, FLOAT_VALUE__LITERAL);

    doubleValueEClass = createEClass(DOUBLE_VALUE);
    createEAttribute(doubleValueEClass, DOUBLE_VALUE__LITERAL);

    charValueEClass = createEClass(CHAR_VALUE);
    createEAttribute(charValueEClass, CHAR_VALUE__LITERAL);

    stringValueEClass = createEClass(STRING_VALUE);
    createEAttribute(stringValueEClass, STRING_VALUE__LITERAL);

    invocationEClass = createEClass(INVOCATION);
    createEReference(invocationEClass, INVOCATION__ARGUMENTS);
    createEReference(invocationEClass, INVOCATION__NAME);

    functionInvocationEClass = createEClass(FUNCTION_INVOCATION);

    memberInvocationEClass = createEClass(MEMBER_INVOCATION);
    createEReference(memberInvocationEClass, MEMBER_INVOCATION__OBJECT);

    accessEClass = createEClass(ACCESS);
    createEReference(accessEClass, ACCESS__NAME);

    staticAccessEClass = createEClass(STATIC_ACCESS);

    memberAccessEClass = createEClass(MEMBER_ACCESS);
    createEReference(memberAccessEClass, MEMBER_ACCESS__OBJECT);

    contextAccessEClass = createEClass(CONTEXT_ACCESS);

    containedObjectEClass = createEClass(CONTAINED_OBJECT);
    createEReference(containedObjectEClass, CONTAINED_OBJECT__OBJECT);

    linkedObjectEClass = createEClass(LINKED_OBJECT);
    createEReference(linkedObjectEClass, LINKED_OBJECT__OBJECT);

    linkedExpressionEClass = createEClass(LINKED_EXPRESSION);
    createEReference(linkedExpressionEClass, LINKED_EXPRESSION__EXPRESSION);

    listConstructionEClass = createEClass(LIST_CONSTRUCTION);
    createEReference(listConstructionEClass, LIST_CONSTRUCTION__ELEMENTS);

    // Create data types
    evaluationContextEDataType = createEDataType(EVALUATION_CONTEXT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    valueEClass.getESuperTypes().add(getExpression());
    booleanValueEClass.getESuperTypes().add(getValue());
    byteValueEClass.getESuperTypes().add(getValue());
    shortValueEClass.getESuperTypes().add(getValue());
    intValueEClass.getESuperTypes().add(getValue());
    longValueEClass.getESuperTypes().add(getValue());
    floatValueEClass.getESuperTypes().add(getValue());
    doubleValueEClass.getESuperTypes().add(getValue());
    charValueEClass.getESuperTypes().add(getValue());
    stringValueEClass.getESuperTypes().add(getValue());
    invocationEClass.getESuperTypes().add(getExpression());
    functionInvocationEClass.getESuperTypes().add(getInvocation());
    memberInvocationEClass.getESuperTypes().add(getInvocation());
    accessEClass.getESuperTypes().add(getExpression());
    staticAccessEClass.getESuperTypes().add(getAccess());
    memberAccessEClass.getESuperTypes().add(getAccess());
    contextAccessEClass.getESuperTypes().add(getAccess());
    containedObjectEClass.getESuperTypes().add(getExpression());
    linkedObjectEClass.getESuperTypes().add(getExpression());
    linkedExpressionEClass.getESuperTypes().add(getExpression());
    listConstructionEClass.getESuperTypes().add(getExpression());

    // Initialize classes, features, and operations; add parameters
    initEClass(expressionEClass, Expression.class, "Expression", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    EOperation op = initEOperation(getExpression__Evaluate__EvaluationContext(), ecorePackage.getEJavaObject(), "evaluate", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, getEvaluationContext(), "context", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(valueEClass, Value.class, "Value", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEOperation(getValue__GetLiteral(), ecorePackage.getEJavaObject(), "getLiteral", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(booleanValueEClass, BooleanValue.class, "BooleanValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBooleanValue_Literal(), ecorePackage.getEBoolean(), "literal", null, 0, 1, BooleanValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(byteValueEClass, ByteValue.class, "ByteValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getByteValue_Literal(), ecorePackage.getEByte(), "literal", null, 0, 1, ByteValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(shortValueEClass, ShortValue.class, "ShortValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getShortValue_Literal(), ecorePackage.getEShort(), "literal", null, 0, 1, ShortValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(intValueEClass, IntValue.class, "IntValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntValue_Literal(), ecorePackage.getEInt(), "literal", null, 0, 1, IntValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(longValueEClass, LongValue.class, "LongValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLongValue_Literal(), ecorePackage.getELong(), "literal", null, 0, 1, LongValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(floatValueEClass, FloatValue.class, "FloatValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFloatValue_Literal(), ecorePackage.getEFloat(), "literal", null, 0, 1, FloatValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(doubleValueEClass, DoubleValue.class, "DoubleValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDoubleValue_Literal(), ecorePackage.getEDouble(), "literal", null, 0, 1, DoubleValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(charValueEClass, CharValue.class, "CharValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCharValue_Literal(), ecorePackage.getEChar(), "literal", null, 0, 1, CharValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringValueEClass, StringValue.class, "StringValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringValue_Literal(), ecorePackage.getEString(), "literal", null, 0, 1, StringValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(invocationEClass, Invocation.class, "Invocation", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getInvocation_Arguments(), getExpression(), null, "arguments", null, 0, -1, Invocation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getInvocation_Name(), getExpression(), null, "name", null, 1, 1, Invocation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(functionInvocationEClass, FunctionInvocation.class, "FunctionInvocation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(memberInvocationEClass, MemberInvocation.class, "MemberInvocation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMemberInvocation_Object(), getExpression(), null, "object", null, 1, 1, MemberInvocation.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(accessEClass, Access.class, "Access", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAccess_Name(), getExpression(), null, "name", null, 1, 1, Access.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(staticAccessEClass, StaticAccess.class, "StaticAccess", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(memberAccessEClass, MemberAccess.class, "MemberAccess", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMemberAccess_Object(), getExpression(), null, "object", null, 1, 1, MemberAccess.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(contextAccessEClass, ContextAccess.class, "ContextAccess", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(containedObjectEClass, ContainedObject.class, "ContainedObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getContainedObject_Object(), ecorePackage.getEObject(), null, "object", null, 0, 1, ContainedObject.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(linkedObjectEClass, LinkedObject.class, "LinkedObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLinkedObject_Object(), ecorePackage.getEObject(), null, "object", null, 0, 1, LinkedObject.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(linkedExpressionEClass, LinkedExpression.class, "LinkedExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLinkedExpression_Expression(), getExpression(), null, "expression", null, 1, 1, LinkedExpression.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(listConstructionEClass, ListConstruction.class, "ListConstruction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getListConstruction_Elements(), getExpression(), null, "elements", null, 0, -1, ListConstruction.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(evaluationContextEDataType, EvaluationContext.class, "EvaluationContext", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // ExpressionsPackageImpl
