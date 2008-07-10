/**
 * <copyright>
 * </copyright>
 *
 * $Id: RefMultiNonContained.java,v 1.1 2008-07-10 15:42:26 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ref Multi Non Contained</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContained()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface RefMultiNonContained extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Elements</em>' containment reference list.
	 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContained_Elements()
	 * @see org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement#getParent
	 * @model opposite="parent" containment="true"
	 * @generated
	 */
	EList<MultiNonContainedElement> getElements();

} // RefMultiNonContained
