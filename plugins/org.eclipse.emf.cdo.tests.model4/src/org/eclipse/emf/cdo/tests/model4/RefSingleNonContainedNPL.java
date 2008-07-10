/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefSingleNonContainedNPL.java,v 1.1 2008-07-10 15:42:26 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ref Single Non Contained NPL</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleNonContainedNPL()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface RefSingleNonContainedNPL extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Element</em>' reference.
	 * @see #setElement(ContainedElementNoOpposite)
	 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleNonContainedNPL_Element()
	 * @model
	 * @generated
	 */
	ContainedElementNoOpposite getElement();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL#getElement <em>Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Element</em>' reference.
	 * @see #getElement()
	 * @generated
	 */
	void setElement(ContainedElementNoOpposite value);

} // RefSingleNonContainedNPL
