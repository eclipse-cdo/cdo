/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Index</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Index#getURI <em>URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Index#getOldURIs <em>Old UR Is</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getIndex()
 * @model
 * @generated
 */
public interface Index extends EObject
{
  /**
   * Returns the value of the '<em><b>URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>URI</em>' attribute.
   * @see #setURI(URI)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getIndex_URI()
   * @model dataType="org.eclipse.emf.cdo.releng.setup.URI" required="true"
   *        extendedMetaData="kind='attribute' name='uri'"
   * @generated
   */
  URI getURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Index#getURI <em>URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>URI</em>' attribute.
   * @see #getURI()
   * @generated
   */
  void setURI(URI value);

  /**
   * Returns the value of the '<em><b>Old UR Is</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.common.util.URI}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Old UR Is</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Old UR Is</em>' attribute list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getIndex_OldURIs()
   * @model dataType="org.eclipse.emf.cdo.releng.setup.URI"
   * @generated
   */
  EList<URI> getOldURIs();

} // Index
