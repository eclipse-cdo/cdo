/**
 * <copyright>
 * </copyright>
 *
 * $Id: ISingleRefNonContainer.java,v 1.2 2008-07-10 15:57:45 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>ISingle Ref Non Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getISingleRefNonContainer()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ISingleRefNonContainer extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement#getParent <em>Parent</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' reference.
   * @see #setElement(ISingleRefNonContainedElement)
   * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getISingleRefNonContainer_Element()
   * @see org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement#getParent
   * @model opposite="parent"
   * @generated
   */
  ISingleRefNonContainedElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer#getElement
   * <em>Element</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' reference.
   * @see #getElement()
   * @generated
   */
  void setElement(ISingleRefNonContainedElement value);

} // ISingleRefNonContainer
