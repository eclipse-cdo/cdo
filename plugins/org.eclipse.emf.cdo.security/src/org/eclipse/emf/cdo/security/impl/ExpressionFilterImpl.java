/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.impl.EvaluationContextImpl;
import org.eclipse.emf.cdo.security.ExpressionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expression Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ExpressionFilterImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ExpressionFilterImpl extends ObjectFilterImpl implements ExpressionFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExpressionFilterImpl()
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
    return SecurityPackage.Literals.EXPRESSION_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expression getExpression()
  {
    return (Expression)eGet(SecurityPackage.Literals.EXPRESSION_FILTER__EXPRESSION, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setExpression(Expression newExpression)
  {
    eSet(SecurityPackage.Literals.EXPRESSION_FILTER__EXPRESSION, newExpression);
  }

  @Override
  protected boolean filter(CDOObject object, CDOBranchPoint securityContext)
  {
    EvaluationContext evaluationContext = new EvaluationContextImpl(object);

    Expression expression = getExpression();
    return (Boolean)expression.evaluate(evaluationContext);
  }

  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    // TODO Implement impact analysis in the expression model
    return true;
  }

  @Override
  public String format()
  {
    Expression expression = getExpression();
    if (expression == null)
    {
      return "[?]";
    }

    return "[" + expression + "]";
  }

} // ExpressionFilterImpl
