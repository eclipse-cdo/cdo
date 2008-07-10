/**
 * <copyright>
 * </copyright>
 *
 * $Id: IMultiRefContainedElement.java,v 1.1 2008-07-10 15:42:28 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IMulti Ref Contained Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefContainedElement()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface IMultiRefContainedElement extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Parent</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer#getElements <em>Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' container reference.
	 * @see #setParent(IMultiRefContainer)
	 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefContainedElement_Parent()
	 * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer#getElements
	 * @model opposite="elements" transient="false"
	 * @generated
	 */
	IMultiRefContainer getParent();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(IMultiRefContainer value);

} // IMultiRefContainedElement
