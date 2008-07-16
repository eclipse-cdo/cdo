/**
 * <copyright>
 * </copyright>
 *
 * $Id: Reference.java,v 1.1.2.3 2008-07-16 16:34:52 estepper Exp $
 */
package reference;

import org.eclipse.emf.cdo.CDOObject;

import interface_.IInterface;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link reference.Reference#getRef <em>Ref</em>}</li>
 * </ul>
 * </p>
 * 
 * @see reference.ReferencePackage#getReference()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Reference extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Ref</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ref</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Ref</em>' reference.
   * @see #setRef(IInterface)
   * @see reference.ReferencePackage#getReference_Ref()
   * @model
   * @generated
   */
  IInterface getRef();

  /**
   * Sets the value of the '{@link reference.Reference#getRef <em>Ref</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Ref</em>' reference.
   * @see #getRef()
   * @generated
   */
  void setRef(IInterface value);

} // Reference
