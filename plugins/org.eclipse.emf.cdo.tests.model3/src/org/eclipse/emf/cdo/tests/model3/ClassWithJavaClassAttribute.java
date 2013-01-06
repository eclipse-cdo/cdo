/**
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class With Java Class Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute#getJavaClass <em>Java Class</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model3.legacy.Model3Package#getClassWithJavaClassAttribute()
 * @model
 * @generated
 */
public interface ClassWithJavaClassAttribute extends EObject
{
  /**
   * Returns the value of the '<em><b>Java Class</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Java Class</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Java Class</em>' attribute.
   * @see #setJavaClass(Class)
   * @see org.eclipse.emf.cdo.tests.model3.legacy.Model3Package#getClassWithJavaClassAttribute_JavaClass()
   * @model id="true"
   * @generated
   */
  Class<?> getJavaClass();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute#getJavaClass <em>Java Class</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Java Class</em>' attribute.
   * @see #getJavaClass()
   * @generated
   */
  void setJavaClass(Class<?> value);

} // ClassWithJavaClassAttribute
