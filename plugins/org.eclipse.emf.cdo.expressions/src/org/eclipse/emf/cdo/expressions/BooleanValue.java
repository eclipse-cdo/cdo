/**
 */
package org.eclipse.emf.cdo.expressions;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Boolean Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.BooleanValue#isLiteral <em>Literal</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getBooleanValue()
 * @model
 * @generated
 */
public interface BooleanValue extends Value
{
  /**
   * Returns the value of the '<em><b>Literal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Literal</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Literal</em>' attribute.
   * @see #setLiteral(boolean)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getBooleanValue_Literal()
   * @model
   * @generated
   */
  boolean isLiteral();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.BooleanValue#isLiteral <em>Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Literal</em>' attribute.
   * @see #isLiteral()
   * @generated
   */
  void setLiteral(boolean value);

} // BooleanValue
