/**
 * <copyright>
 * </copyright>
 *
 * $Id: IInterface.java,v 1.1.2.2 2008-07-02 14:10:02 estepper Exp $
 */
package interface_;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IInterface</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link interface_.IInterface#getTest <em>Test</em>}</li>
 * </ul>
 * </p>
 *
 * @see interface_.InterfacePackage#getIInterface()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface IInterface extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Test</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Test</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Test</em>' attribute.
   * @see #setTest(String)
   * @see interface_.InterfacePackage#getIInterface_Test()
   * @model
   * @generated
   */
  String getTest();

  /**
   * Sets the value of the '{@link interface_.IInterface#getTest <em>Test</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Test</em>' attribute.
   * @see #getTest()
   * @generated
   */
  void setTest(String value);

} // IInterface
