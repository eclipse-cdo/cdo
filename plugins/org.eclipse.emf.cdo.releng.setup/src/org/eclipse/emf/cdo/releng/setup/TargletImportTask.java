/**
 */
package org.eclipse.emf.cdo.releng.setup;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Targlet Import Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletImportTask#getTargletURI <em>Targlet URI</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletImportTask()
 * @model
 * @generated
 */
@Deprecated
public interface TargletImportTask extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Targlet URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Targlet URI</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Targlet URI</em>' attribute.
   * @see #setTargletURI(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletImportTask_TargletURI()
   * @model required="true"
   * @generated
   */
  String getTargletURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.TargletImportTask#getTargletURI <em>Targlet URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Targlet URI</em>' attribute.
   * @see #getTargletURI()
   * @generated
   */
  void setTargletURI(String value);

} // TargletImportTask
