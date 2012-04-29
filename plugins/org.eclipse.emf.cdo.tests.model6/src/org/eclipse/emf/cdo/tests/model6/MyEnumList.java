/**
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>My Enum List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.MyEnumList#getMyEnum <em>My Enum</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getMyEnumList()
 * @model
 * @generated
 */
public interface MyEnumList extends EObject
{
  /**
   * Returns the value of the '<em><b>My Enum</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.MyEnum}.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.model6.MyEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>My Enum</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>My Enum</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnum
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getMyEnumList_MyEnum()
   * @model
   * @generated
   */
  EList<MyEnum> getMyEnum();

} // MyEnumList
