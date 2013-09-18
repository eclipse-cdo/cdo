/**
 */
package org.eclipse.emf.cdo.expressions;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Double Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.DoubleValue#getLiteral <em>Literal</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getDoubleValue()
 * @model
 * @generated
 */
public interface DoubleValue extends Value
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
   * @see #setLiteral(double)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getDoubleValue_Literal()
   * @model
   * @generated NOT
   */
  Double getLiteral();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.DoubleValue#getLiteral <em>Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Literal</em>' attribute.
   * @see #getLiteral()
   * @generated
   */
  void setLiteral(double value);

} // DoubleValue
