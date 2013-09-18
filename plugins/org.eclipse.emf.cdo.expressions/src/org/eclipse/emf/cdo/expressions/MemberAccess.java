/**
 */
package org.eclipse.emf.cdo.expressions;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Member Access</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.MemberAccess#getObject <em>Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.expressions.MemberAccess#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getMemberAccess()
 * @model
 * @generated
 */
public interface MemberAccess extends Expression
{
  /**
   * Returns the value of the '<em><b>Object</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Object</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Object</em>' containment reference.
   * @see #setObject(Expression)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getMemberAccess_Object()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getObject();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.MemberAccess#getObject <em>Object</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Object</em>' containment reference.
   * @see #getObject()
   * @generated
   */
  void setObject(Expression value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' containment reference.
   * @see #setName(Expression)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getMemberAccess_Name()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.MemberAccess#getName <em>Name</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' containment reference.
   * @see #getName()
   * @generated
   */
  void setName(Expression value);

} // MemberAccess
