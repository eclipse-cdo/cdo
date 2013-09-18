/**
 */
package org.eclipse.emf.cdo.expressions;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getExpression()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Expression extends CDOObject
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model contextDataType="org.eclipse.emf.cdo.expressions.EvaluationContext"
   * @generated
   */
  Object evaluate(EvaluationContext context);

} // Expression
