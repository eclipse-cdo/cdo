/**
 * <copyright>
 * </copyright>
 *
 * $Id: ISingleRefContainerNPL.java,v 1.1 2008-07-10 15:42:28 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ISingle Ref Container NPL</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getISingleRefContainerNPL()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface ISingleRefContainerNPL extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Element</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Element</em>' containment reference.
	 * @see #setElement(IContainedElementNoParentLink)
	 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getISingleRefContainerNPL_Element()
	 * @model containment="true"
	 * @generated
	 */
	IContainedElementNoParentLink getElement();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL#getElement <em>Element</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Element</em>' containment reference.
	 * @see #getElement()
	 * @generated
	 */
	void setElement(IContainedElementNoParentLink value);

} // ISingleRefContainerNPL
