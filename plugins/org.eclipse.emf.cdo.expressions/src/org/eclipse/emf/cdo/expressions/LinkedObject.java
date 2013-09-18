/**
 */
package org.eclipse.emf.cdo.expressions;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Object Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.LinkedObject#getObject <em>Object</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getLinkedObject()
 * @model
 * @generated
 */
public interface LinkedObject extends Expression
{
  /**
   * Returns the value of the '<em><b>Object</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Object</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Object</em>' reference.
   * @see #setObject(EObject)
   * @see org.eclipse.emf.cdo.expressions.ExpressionsPackage#getLinkedObject_Object()
   * @model
   * @generated
   */
  EObject getObject();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.expressions.LinkedObject#getObject <em>Object</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Object</em>' reference.
   * @see #getObject()
   * @generated
   */
  void setObject(EObject value);

} // ObjectValue
