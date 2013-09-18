/**
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.FunctionInvocation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Function Invocation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class FunctionInvocationImpl extends InvocationImpl implements FunctionInvocation
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FunctionInvocationImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ExpressionsPackage.Literals.FUNCTION_INVOCATION;
  }

  @Override
  protected Object evaluate(EvaluationContext context, String name, EList<Object> arguments)
      throws InvocationTargetException
  {
    // Object left = getLeft().evaluate(context);
    // Object right = getRight().evaluate(context);
    //
    // Operator operator = getOperator();
    // switch (operator)
    // {
    // case EQUAL:
    // return left.equals(right);
    //
    // case NOT_EQUAL:
    // return !left.equals(right);
    //
    // case LESS_THAN:
    // return ((Comparable<Object>)left).compareTo(right) < 0;
    //
    // case LESS_THAN_OR_EQUAL:
    // return ((Comparable<Object>)left).compareTo(right) <= 0;
    //
    // case GREATER_THAN:
    // return ((Comparable<Object>)left).compareTo(right) > 0;
    //
    // case GREATER_THAN_OR_EQUAL:
    // return ((Comparable<Object>)left).compareTo(right) >= 0;
    //
    // case AND:
    // return (Boolean)left && (Boolean)right;
    //
    // case OR:
    // return (Boolean)left || (Boolean)right;
    //
    // default:
    // throw new IllegalStateException("Unhandled operator: " + operator);
    // }
    //
    return null;
  }

} // FunctionInvocationImpl
