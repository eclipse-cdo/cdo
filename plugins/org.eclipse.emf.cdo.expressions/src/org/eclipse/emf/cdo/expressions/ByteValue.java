/**
 */
package org.eclipse.emf.cdo.expressions;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Byte Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.ByteValue#getLiteral <em>Literal</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getByteValue()
 * @model
 * @generated
 */
public interface ByteValue extends Value
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
   * @see #setLiteral(byte)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getByteValue_Literal()
   * @model
   * @generated NOT
   */
  Byte getLiteral();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.ByteValue#getLiteral <em>Literal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Literal</em>' attribute.
   * @see #getLiteral()
   * @generated
   */
  void setLiteral(byte value);

} // ByteValue
