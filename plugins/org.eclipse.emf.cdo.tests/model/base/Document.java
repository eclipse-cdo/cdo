/**
 */
package base;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link base.Document#getRoot <em>Root</em>}</li>
 * </ul>
 *
 * @see base.BasePackage#getDocument()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Document extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Root</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Root</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Root</em>' containment reference.
   * @see #setRoot(Element)
   * @see base.BasePackage#getDocument_Root()
   * @model containment="true"
   * @generated
   */
  Element getRoot();

  /**
   * Sets the value of the '{@link base.Document#getRoot <em>Root</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Root</em>' containment reference.
   * @see #getRoot()
   * @generated
   */
  void setRoot(Element value);

} // Document
