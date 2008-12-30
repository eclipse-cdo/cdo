/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestDef.java,v 1.1 2008-12-30 08:43:08 estepper Exp $
 */
package org.eclipse.net4j.util.tests.defs;

import org.eclipse.net4j.util.net4jutildefs.Def;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.tests.defs.TestDef#getReferences <em>References</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.tests.defs.TestDef#getAttribute <em>Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.net4j.util.tests.defs.DefsPackage#getTestDef()
 * @model
 * @generated
 */
public interface TestDef extends Def {
	/**
   * Returns the value of the '<em><b>References</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.net4j.util.net4jutildefs.Def}.
   * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>References</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
   * @return the value of the '<em>References</em>' reference list.
   * @see org.eclipse.net4j.util.tests.defs.DefsPackage#getTestDef_References()
   * @model
   * @generated
   */
	EList<Def> getReferences();

	/**
   * Returns the value of the '<em><b>Attribute</b></em>' attribute.
   * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
   * @return the value of the '<em>Attribute</em>' attribute.
   * @see #setAttribute(String)
   * @see org.eclipse.net4j.util.tests.defs.DefsPackage#getTestDef_Attribute()
   * @model
   * @generated
   */
	String getAttribute();

	/**
   * Sets the value of the '{@link org.eclipse.net4j.util.tests.defs.TestDef#getAttribute <em>Attribute</em>}' attribute.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Attribute</em>' attribute.
   * @see #getAttribute()
   * @generated
   */
	void setAttribute(String value);

} // TestDef
