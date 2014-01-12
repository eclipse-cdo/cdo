/**
 */
package org.eclipse.emf.cdo.releng.setup;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>URL Query</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.URLQuery#getRelativeURL <em>Relative URL</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getURLQuery()
 * @model
 * @generated
 */
public interface URLQuery extends MylynQuery
{
  /**
   * Returns the value of the '<em><b>Relative URL</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relative URL</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relative URL</em>' attribute.
   * @see #setRelativeURL(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getURLQuery_RelativeURL()
   * @model required="true"
   * @generated
   */
  String getRelativeURL();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.URLQuery#getRelativeURL <em>Relative URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Relative URL</em>' attribute.
   * @see #getRelativeURL()
   * @generated
   */
  void setRelativeURL(String value);

} // URLQuery
