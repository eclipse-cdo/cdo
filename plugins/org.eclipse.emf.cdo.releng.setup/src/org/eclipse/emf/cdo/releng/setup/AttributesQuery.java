/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attributes Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.AttributesQuery#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getAttributesQuery()
 * @model
 * @generated
 */
public interface AttributesQuery extends MylynQuery
{
  /**
   * Returns the value of the '<em><b>Attributes</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attributes</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attributes</em>' map.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getAttributesQuery_Attributes()
   * @model mapType="org.eclipse.emf.cdo.releng.setup.QueryAttribute<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
   * @generated
   */
  EMap<String, String> getAttributes();

} // AttributesQuery
