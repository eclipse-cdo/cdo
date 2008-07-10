/**
 * <copyright>
 * </copyright>
 *
 * $Id: IMultiRefNonContainedElement.java,v 1.2 2008-07-10 15:57:45 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>IMulti Ref Non Contained Element</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefNonContainedElement()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface IMultiRefNonContainedElement extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer#getElements <em>Elements</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parent</em>' reference.
   * @see #setParent(IMultiRefNonContainer)
   * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefNonContainedElement_Parent()
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer#getElements
   * @model opposite="elements"
   * @generated
   */
  IMultiRefNonContainer getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement#getParent
   * <em>Parent</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Parent</em>' reference.
   * @see #getParent()
   * @generated
   */
  void setParent(IMultiRefNonContainer value);

} // IMultiRefNonContainedElement
