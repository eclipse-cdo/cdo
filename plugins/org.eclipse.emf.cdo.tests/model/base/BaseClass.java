/**
 * <copyright>
 * </copyright>
 *
 * $Id: BaseClass.java,v 1.1.2.3 2008-07-16 16:34:52 estepper Exp $
 */
package base;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Class</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link base.BaseClass#getCouter <em>Couter</em>}</li>
 * </ul>
 * </p>
 * 
 * @see base.BasePackage#getBaseClass()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface BaseClass extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Couter</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Couter</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Couter</em>' attribute.
   * @see #setCouter(int)
   * @see base.BasePackage#getBaseClass_Couter()
   * @model
   * @generated
   */
  int getCouter();

  /**
   * Sets the value of the '{@link base.BaseClass#getCouter <em>Couter</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Couter</em>' attribute.
   * @see #getCouter()
   * @generated
   */
  void setCouter(int value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @model
   * @generated
   */
  void increment();

} // BaseClass
