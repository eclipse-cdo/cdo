/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValueList.java,v 1.4 2008-09-18 12:56:15 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.mango;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Value List</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.mango.ValueList#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.mango.ValueList#getValues <em>Values</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.mango.MangoPackage#getValueList()
 * @model
 * @generated
 */
public interface ValueList extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.mango.MangoPackage#getValueList_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.mango.ValueList#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Values</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.mango.Value}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Values</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Values</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.mango.MangoPackage#getValueList_Values()
   * @model
   * @generated
   */
  EList<Value> getValues();

} // ValueList
