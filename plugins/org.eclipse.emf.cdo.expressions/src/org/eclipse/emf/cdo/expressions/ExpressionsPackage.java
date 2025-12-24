/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.expressions.ExpressionsFactory
 * @model kind="package"
 * @generated
 */
public interface ExpressionsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "expressions";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/expressions/4.3.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "expressions";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  ExpressionsPackage eINSTANCE = org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.Expression <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.Expression
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 0;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = 0;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION___EVALUATE__EVALUATIONCONTEXT = 0;

  /**
   * The number of operations of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_OPERATION_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.ValueImpl <em>Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.ValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getValue()
   * @generated
   */
  int VALUE = 1;

  /**
   * The number of structural features of the '<em>Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VALUE_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VALUE___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VALUE___GET_LITERAL = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VALUE_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.BooleanValueImpl <em>Boolean Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.BooleanValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getBooleanValue()
   * @generated
   */
  int BOOLEAN_VALUE = 2;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Boolean Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Boolean Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.ByteValueImpl <em>Byte Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.ByteValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getByteValue()
   * @generated
   */
  int BYTE_VALUE = 3;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BYTE_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Byte Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BYTE_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BYTE_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BYTE_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Byte Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BYTE_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.ShortValueImpl <em>Short Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.ShortValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getShortValue()
   * @generated
   */
  int SHORT_VALUE = 4;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SHORT_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Short Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SHORT_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SHORT_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SHORT_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Short Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SHORT_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.IntValueImpl <em>Int Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.IntValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getIntValue()
   * @generated
   */
  int INT_VALUE = 5;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Int Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Int Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.LongValueImpl <em>Long Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.LongValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getLongValue()
   * @generated
   */
  int LONG_VALUE = 6;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LONG_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Long Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LONG_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LONG_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LONG_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Long Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LONG_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.FloatValueImpl <em>Float Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.FloatValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getFloatValue()
   * @generated
   */
  int FLOAT_VALUE = 7;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOAT_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Float Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOAT_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOAT_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOAT_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Float Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOAT_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.DoubleValueImpl <em>Double Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.DoubleValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getDoubleValue()
   * @generated
   */
  int DOUBLE_VALUE = 8;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Double Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Double Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.CharValueImpl <em>Char Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.CharValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getCharValue()
   * @generated
   */
  int CHAR_VALUE = 9;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAR_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Char Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAR_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAR_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAR_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>Char Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAR_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.StringValueImpl <em>String Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.StringValueImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getStringValue()
   * @generated
   */
  int STRING_VALUE = 10;

  /**
   * The feature id for the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE__LITERAL = VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_FEATURE_COUNT = VALUE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE___EVALUATE__EVALUATIONCONTEXT = VALUE___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The operation id for the '<em>Get Literal</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE___GET_LITERAL = VALUE___GET_LITERAL;

  /**
   * The number of operations of the '<em>String Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_OPERATION_COUNT = VALUE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.InvocationImpl <em>Invocation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.InvocationImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getInvocation()
   * @generated
   */
  int INVOCATION = 11;

  /**
   * The feature id for the '<em><b>Arguments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INVOCATION__ARGUMENTS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INVOCATION__NAME = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Invocation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INVOCATION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INVOCATION___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Invocation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INVOCATION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.FunctionInvocationImpl <em>Function Invocation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.FunctionInvocationImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getFunctionInvocation()
   * @generated
   */
  int FUNCTION_INVOCATION = 12;

  /**
   * The feature id for the '<em><b>Arguments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_INVOCATION__ARGUMENTS = INVOCATION__ARGUMENTS;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_INVOCATION__NAME = INVOCATION__NAME;

  /**
   * The number of structural features of the '<em>Function Invocation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_INVOCATION_FEATURE_COUNT = INVOCATION_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_INVOCATION___EVALUATE__EVALUATIONCONTEXT = INVOCATION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Function Invocation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_INVOCATION_OPERATION_COUNT = INVOCATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.MemberInvocationImpl <em>Member Invocation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.MemberInvocationImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getMemberInvocation()
   * @generated
   */
  int MEMBER_INVOCATION = 13;

  /**
   * The feature id for the '<em><b>Arguments</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_INVOCATION__ARGUMENTS = INVOCATION__ARGUMENTS;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_INVOCATION__NAME = INVOCATION__NAME;

  /**
   * The feature id for the '<em><b>Object</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_INVOCATION__OBJECT = INVOCATION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Member Invocation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_INVOCATION_FEATURE_COUNT = INVOCATION_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_INVOCATION___EVALUATE__EVALUATIONCONTEXT = INVOCATION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Member Invocation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_INVOCATION_OPERATION_COUNT = INVOCATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.AccessImpl <em>Access</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.AccessImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getAccess()
   * @generated
   */
  int ACCESS = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS__NAME = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.StaticAccessImpl <em>Static Access</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.StaticAccessImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getStaticAccess()
   * @generated
   */
  int STATIC_ACCESS = 15;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATIC_ACCESS__NAME = ACCESS__NAME;

  /**
   * The number of structural features of the '<em>Static Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATIC_ACCESS_FEATURE_COUNT = ACCESS_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATIC_ACCESS___EVALUATE__EVALUATIONCONTEXT = ACCESS___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Static Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATIC_ACCESS_OPERATION_COUNT = ACCESS_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl <em>Member Access</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getMemberAccess()
   * @generated
   */
  int MEMBER_ACCESS = 16;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_ACCESS__NAME = ACCESS__NAME;

  /**
   * The feature id for the '<em><b>Object</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_ACCESS__OBJECT = ACCESS_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Member Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_ACCESS_FEATURE_COUNT = ACCESS_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_ACCESS___EVALUATE__EVALUATIONCONTEXT = ACCESS___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Member Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_ACCESS_OPERATION_COUNT = ACCESS_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.ContextAccessImpl <em>Context Access</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.ContextAccessImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getContextAccess()
   * @generated
   */
  int CONTEXT_ACCESS = 17;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_ACCESS__NAME = ACCESS__NAME;

  /**
   * The number of structural features of the '<em>Context Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_ACCESS_FEATURE_COUNT = ACCESS_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_ACCESS___EVALUATE__EVALUATIONCONTEXT = ACCESS___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Context Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTEXT_ACCESS_OPERATION_COUNT = ACCESS_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.ContainedObjectImpl <em>Contained Object</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.ContainedObjectImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getContainedObject()
   * @generated
   */
  int CONTAINED_OBJECT = 18;

  /**
   * The feature id for the '<em><b>Object</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINED_OBJECT__OBJECT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Contained Object</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINED_OBJECT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINED_OBJECT___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Contained Object</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINED_OBJECT_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.LinkedObjectImpl <em>Linked Object</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.LinkedObjectImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getLinkedObject()
   * @generated
   */
  int LINKED_OBJECT = 19;

  /**
   * The feature id for the '<em><b>Object</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_OBJECT__OBJECT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Linked Object</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_OBJECT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_OBJECT___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Linked Object</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_OBJECT_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.LinkedExpressionImpl <em>Linked Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.LinkedExpressionImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getLinkedExpression()
   * @generated
   */
  int LINKED_EXPRESSION = 20;

  /**
   * The feature id for the '<em><b>Expression</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_EXPRESSION__EXPRESSION = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Linked Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_EXPRESSION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_EXPRESSION___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>Linked Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINKED_EXPRESSION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.expressions.impl.ListConstructionImpl <em>List Construction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.impl.ListConstructionImpl
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getListConstruction()
   * @generated
   */
  int LIST_CONSTRUCTION = 21;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LIST_CONSTRUCTION__ELEMENTS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>List Construction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LIST_CONSTRUCTION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Evaluate</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LIST_CONSTRUCTION___EVALUATE__EVALUATIONCONTEXT = EXPRESSION___EVALUATE__EVALUATIONCONTEXT;

  /**
   * The number of operations of the '<em>List Construction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LIST_CONSTRUCTION_OPERATION_COUNT = EXPRESSION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>Evaluation Context</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.expressions.EvaluationContext
   * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getEvaluationContext()
   * @generated
   */
  int EVALUATION_CONTEXT = 22;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression</em>'.
   * @see org.eclipse.emf.cdo.expressions.Expression
   * @generated
   */
  EClass getExpression();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.expressions.Expression#evaluate(org.eclipse.emf.cdo.expressions.EvaluationContext) <em>Evaluate</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Evaluate</em>' operation.
   * @see org.eclipse.emf.cdo.expressions.Expression#evaluate(org.eclipse.emf.cdo.expressions.EvaluationContext)
   * @generated
   */
  EOperation getExpression__Evaluate__EvaluationContext();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.Value <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.Value
   * @generated
   */
  EClass getValue();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.expressions.Value#getLiteral() <em>Get Literal</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Literal</em>' operation.
   * @see org.eclipse.emf.cdo.expressions.Value#getLiteral()
   * @generated
   */
  EOperation getValue__GetLiteral();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.BooleanValue <em>Boolean Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Boolean Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.BooleanValue
   * @generated
   */
  EClass getBooleanValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.BooleanValue#isLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.BooleanValue#isLiteral()
   * @see #getBooleanValue()
   * @generated
   */
  EAttribute getBooleanValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.ByteValue <em>Byte Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Byte Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.ByteValue
   * @generated
   */
  EClass getByteValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.ByteValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.ByteValue#getLiteral()
   * @see #getByteValue()
   * @generated
   */
  EAttribute getByteValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.ShortValue <em>Short Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Short Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.ShortValue
   * @generated
   */
  EClass getShortValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.ShortValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.ShortValue#getLiteral()
   * @see #getShortValue()
   * @generated
   */
  EAttribute getShortValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.IntValue <em>Int Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Int Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.IntValue
   * @generated
   */
  EClass getIntValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.IntValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.IntValue#getLiteral()
   * @see #getIntValue()
   * @generated
   */
  EAttribute getIntValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.LongValue <em>Long Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Long Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.LongValue
   * @generated
   */
  EClass getLongValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.LongValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.LongValue#getLiteral()
   * @see #getLongValue()
   * @generated
   */
  EAttribute getLongValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.FloatValue <em>Float Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Float Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.FloatValue
   * @generated
   */
  EClass getFloatValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.FloatValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.FloatValue#getLiteral()
   * @see #getFloatValue()
   * @generated
   */
  EAttribute getFloatValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.DoubleValue <em>Double Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Double Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.DoubleValue
   * @generated
   */
  EClass getDoubleValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.DoubleValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.DoubleValue#getLiteral()
   * @see #getDoubleValue()
   * @generated
   */
  EAttribute getDoubleValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.CharValue <em>Char Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Char Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.CharValue
   * @generated
   */
  EClass getCharValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.CharValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.CharValue#getLiteral()
   * @see #getCharValue()
   * @generated
   */
  EAttribute getCharValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.StringValue <em>String Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Value</em>'.
   * @see org.eclipse.emf.cdo.expressions.StringValue
   * @generated
   */
  EClass getStringValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.expressions.StringValue#getLiteral <em>Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Literal</em>'.
   * @see org.eclipse.emf.cdo.expressions.StringValue#getLiteral()
   * @see #getStringValue()
   * @generated
   */
  EAttribute getStringValue_Literal();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.Invocation <em>Invocation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Invocation</em>'.
   * @see org.eclipse.emf.cdo.expressions.Invocation
   * @generated
   */
  EClass getInvocation();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.expressions.Invocation#getArguments <em>Arguments</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Arguments</em>'.
   * @see org.eclipse.emf.cdo.expressions.Invocation#getArguments()
   * @see #getInvocation()
   * @generated
   */
  EReference getInvocation_Arguments();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.expressions.Invocation#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.expressions.Invocation#getName()
   * @see #getInvocation()
   * @generated
   */
  EReference getInvocation_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.FunctionInvocation <em>Function Invocation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Function Invocation</em>'.
   * @see org.eclipse.emf.cdo.expressions.FunctionInvocation
   * @generated
   */
  EClass getFunctionInvocation();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.MemberInvocation <em>Member Invocation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Invocation</em>'.
   * @see org.eclipse.emf.cdo.expressions.MemberInvocation
   * @generated
   */
  EClass getMemberInvocation();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.expressions.MemberInvocation#getObject <em>Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Object</em>'.
   * @see org.eclipse.emf.cdo.expressions.MemberInvocation#getObject()
   * @see #getMemberInvocation()
   * @generated
   */
  EReference getMemberInvocation_Object();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.Access <em>Access</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Access</em>'.
   * @see org.eclipse.emf.cdo.expressions.Access
   * @generated
   */
  EClass getAccess();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.expressions.Access#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.expressions.Access#getName()
   * @see #getAccess()
   * @generated
   */
  EReference getAccess_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.StaticAccess <em>Static Access</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Static Access</em>'.
   * @see org.eclipse.emf.cdo.expressions.StaticAccess
   * @generated
   */
  EClass getStaticAccess();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.MemberAccess <em>Member Access</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Access</em>'.
   * @see org.eclipse.emf.cdo.expressions.MemberAccess
   * @generated
   */
  EClass getMemberAccess();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.expressions.MemberAccess#getObject <em>Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Object</em>'.
   * @see org.eclipse.emf.cdo.expressions.MemberAccess#getObject()
   * @see #getMemberAccess()
   * @generated
   */
  EReference getMemberAccess_Object();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.ContextAccess <em>Context Access</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Context Access</em>'.
   * @see org.eclipse.emf.cdo.expressions.ContextAccess
   * @generated
   */
  EClass getContextAccess();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.ContainedObject <em>Contained Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Contained Object</em>'.
   * @see org.eclipse.emf.cdo.expressions.ContainedObject
   * @generated
   */
  EClass getContainedObject();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.expressions.ContainedObject#getObject <em>Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Object</em>'.
   * @see org.eclipse.emf.cdo.expressions.ContainedObject#getObject()
   * @see #getContainedObject()
   * @generated
   */
  EReference getContainedObject_Object();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.LinkedObject <em>Linked Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Linked Object</em>'.
   * @see org.eclipse.emf.cdo.expressions.LinkedObject
   * @generated
   */
  EClass getLinkedObject();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.expressions.LinkedObject#getObject <em>Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Object</em>'.
   * @see org.eclipse.emf.cdo.expressions.LinkedObject#getObject()
   * @see #getLinkedObject()
   * @generated
   */
  EReference getLinkedObject_Object();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.LinkedExpression <em>Linked Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Linked Expression</em>'.
   * @see org.eclipse.emf.cdo.expressions.LinkedExpression
   * @generated
   */
  EClass getLinkedExpression();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.expressions.LinkedExpression#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Expression</em>'.
   * @see org.eclipse.emf.cdo.expressions.LinkedExpression#getExpression()
   * @see #getLinkedExpression()
   * @generated
   */
  EReference getLinkedExpression_Expression();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.expressions.ListConstruction <em>List Construction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>List Construction</em>'.
   * @see org.eclipse.emf.cdo.expressions.ListConstruction
   * @generated
   */
  EClass getListConstruction();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.expressions.ListConstruction#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.expressions.ListConstruction#getElements()
   * @see #getListConstruction()
   * @generated
   */
  EReference getListConstruction_Elements();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.expressions.EvaluationContext <em>Evaluation Context</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>Evaluation Context</em>'.
   * @see org.eclipse.emf.cdo.expressions.EvaluationContext
   * @model instanceClass="org.eclipse.emf.cdo.expressions.EvaluationContext" serializeable="false"
   * @generated
   */
  EDataType getEvaluationContext();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  ExpressionsFactory getExpressionsFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * @noimplement This interface is not intended to be implemented by clients.
   * @noextend This interface is not intended to be extended by clients.
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.Expression <em>Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.Expression
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getExpression()
     * @generated
     */
    EClass EXPRESSION = eINSTANCE.getExpression();

    /**
     * The meta object literal for the '<em><b>Evaluate</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation EXPRESSION___EVALUATE__EVALUATIONCONTEXT = eINSTANCE.getExpression__Evaluate__EvaluationContext();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.ValueImpl <em>Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.ValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getValue()
     * @generated
     */
    EClass VALUE = eINSTANCE.getValue();

    /**
     * The meta object literal for the '<em><b>Get Literal</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation VALUE___GET_LITERAL = eINSTANCE.getValue__GetLiteral();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.BooleanValueImpl <em>Boolean Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.BooleanValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getBooleanValue()
     * @generated
     */
    EClass BOOLEAN_VALUE = eINSTANCE.getBooleanValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BOOLEAN_VALUE__LITERAL = eINSTANCE.getBooleanValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.ByteValueImpl <em>Byte Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.ByteValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getByteValue()
     * @generated
     */
    EClass BYTE_VALUE = eINSTANCE.getByteValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BYTE_VALUE__LITERAL = eINSTANCE.getByteValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.ShortValueImpl <em>Short Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.ShortValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getShortValue()
     * @generated
     */
    EClass SHORT_VALUE = eINSTANCE.getShortValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SHORT_VALUE__LITERAL = eINSTANCE.getShortValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.IntValueImpl <em>Int Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.IntValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getIntValue()
     * @generated
     */
    EClass INT_VALUE = eINSTANCE.getIntValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INT_VALUE__LITERAL = eINSTANCE.getIntValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.LongValueImpl <em>Long Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.LongValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getLongValue()
     * @generated
     */
    EClass LONG_VALUE = eINSTANCE.getLongValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LONG_VALUE__LITERAL = eINSTANCE.getLongValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.FloatValueImpl <em>Float Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.FloatValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getFloatValue()
     * @generated
     */
    EClass FLOAT_VALUE = eINSTANCE.getFloatValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FLOAT_VALUE__LITERAL = eINSTANCE.getFloatValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.DoubleValueImpl <em>Double Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.DoubleValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getDoubleValue()
     * @generated
     */
    EClass DOUBLE_VALUE = eINSTANCE.getDoubleValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DOUBLE_VALUE__LITERAL = eINSTANCE.getDoubleValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.CharValueImpl <em>Char Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.CharValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getCharValue()
     * @generated
     */
    EClass CHAR_VALUE = eINSTANCE.getCharValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CHAR_VALUE__LITERAL = eINSTANCE.getCharValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.StringValueImpl <em>String Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.StringValueImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getStringValue()
     * @generated
     */
    EClass STRING_VALUE = eINSTANCE.getStringValue();

    /**
     * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VALUE__LITERAL = eINSTANCE.getStringValue_Literal();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.InvocationImpl <em>Invocation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.InvocationImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getInvocation()
     * @generated
     */
    EClass INVOCATION = eINSTANCE.getInvocation();

    /**
     * The meta object literal for the '<em><b>Arguments</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INVOCATION__ARGUMENTS = eINSTANCE.getInvocation_Arguments();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INVOCATION__NAME = eINSTANCE.getInvocation_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.FunctionInvocationImpl <em>Function Invocation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.FunctionInvocationImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getFunctionInvocation()
     * @generated
     */
    EClass FUNCTION_INVOCATION = eINSTANCE.getFunctionInvocation();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.MemberInvocationImpl <em>Member Invocation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.MemberInvocationImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getMemberInvocation()
     * @generated
     */
    EClass MEMBER_INVOCATION = eINSTANCE.getMemberInvocation();

    /**
     * The meta object literal for the '<em><b>Object</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_INVOCATION__OBJECT = eINSTANCE.getMemberInvocation_Object();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.AccessImpl <em>Access</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.AccessImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getAccess()
     * @generated
     */
    EClass ACCESS = eINSTANCE.getAccess();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCESS__NAME = eINSTANCE.getAccess_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.StaticAccessImpl <em>Static Access</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.StaticAccessImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getStaticAccess()
     * @generated
     */
    EClass STATIC_ACCESS = eINSTANCE.getStaticAccess();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl <em>Member Access</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getMemberAccess()
     * @generated
     */
    EClass MEMBER_ACCESS = eINSTANCE.getMemberAccess();

    /**
     * The meta object literal for the '<em><b>Object</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_ACCESS__OBJECT = eINSTANCE.getMemberAccess_Object();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.ContextAccessImpl <em>Context Access</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.ContextAccessImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getContextAccess()
     * @generated
     */
    EClass CONTEXT_ACCESS = eINSTANCE.getContextAccess();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.ContainedObjectImpl <em>Contained Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.ContainedObjectImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getContainedObject()
     * @generated
     */
    EClass CONTAINED_OBJECT = eINSTANCE.getContainedObject();

    /**
     * The meta object literal for the '<em><b>Object</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONTAINED_OBJECT__OBJECT = eINSTANCE.getContainedObject_Object();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.LinkedObjectImpl <em>Linked Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.LinkedObjectImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getLinkedObject()
     * @generated
     */
    EClass LINKED_OBJECT = eINSTANCE.getLinkedObject();

    /**
     * The meta object literal for the '<em><b>Object</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LINKED_OBJECT__OBJECT = eINSTANCE.getLinkedObject_Object();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.LinkedExpressionImpl <em>Linked Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.LinkedExpressionImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getLinkedExpression()
     * @generated
     */
    EClass LINKED_EXPRESSION = eINSTANCE.getLinkedExpression();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LINKED_EXPRESSION__EXPRESSION = eINSTANCE.getLinkedExpression_Expression();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.expressions.impl.ListConstructionImpl <em>List Construction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.impl.ListConstructionImpl
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getListConstruction()
     * @generated
     */
    EClass LIST_CONSTRUCTION = eINSTANCE.getListConstruction();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LIST_CONSTRUCTION__ELEMENTS = eINSTANCE.getListConstruction_Elements();

    /**
     * The meta object literal for the '<em>Evaluation Context</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.expressions.EvaluationContext
     * @see org.eclipse.emf.cdo.expressions.impl.ExpressionsPackageImpl#getEvaluationContext()
     * @generated
     */
    EDataType EVALUATION_CONTEXT = eINSTANCE.getEvaluationContext();

  }

} // ExpressionsPackage
