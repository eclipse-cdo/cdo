/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Meta Index</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MetaIndex#getIndexes <em>Indexes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMetaIndex()
 * @model
 * @generated
 */
public interface MetaIndex extends EObject
{
  /**
   * Returns the value of the '<em><b>Indexes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.Index}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Indexes</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Indexes</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMetaIndex_Indexes()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Index> getIndexes();

} // MetaIndex
