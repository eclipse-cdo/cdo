/**
 * <copyright>
 * </copyright>
 *
 * $Id: DefsContainer.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Defs Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefaultDefinition <em>Default Definition</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage#getDefsContainer()
 * @model
 * @generated
 */
public interface DefsContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Definitions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.net4j.util.net4jutildefs.Def}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Definitions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Definitions</em>' containment reference list.
	 * @see org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage#getDefsContainer_Definitions()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Def> getDefinitions();

	/**
	 * Returns the value of the '<em><b>Default Definition</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Definition</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default Definition</em>' reference.
	 * @see #setDefaultDefinition(Def)
	 * @see org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage#getDefsContainer_DefaultDefinition()
	 * @model
	 * @generated
	 */
	Def getDefaultDefinition();

	/**
	 * Sets the value of the '{@link org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefaultDefinition <em>Default Definition</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Definition</em>' reference.
	 * @see #getDefaultDefinition()
	 * @generated
	 */
	void setDefaultDefinition(Def value);

} // DefsContainer
