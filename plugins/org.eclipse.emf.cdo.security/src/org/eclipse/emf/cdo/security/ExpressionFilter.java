/**
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.cdo.expressions.Expression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.ExpressionFilter#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getExpressionFilter()
 * @model
 * @generated
 */
public interface ExpressionFilter extends ObjectFilter
{
  /**
   * Returns the value of the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression</em>' containment reference.
   * @see #setExpression(Expression)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getExpressionFilter_Expression()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getExpression();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ExpressionFilter#getExpression <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression</em>' containment reference.
   * @see #getExpression()
   * @generated
   */
  void setExpression(Expression value);

} // ExpressionFilter
