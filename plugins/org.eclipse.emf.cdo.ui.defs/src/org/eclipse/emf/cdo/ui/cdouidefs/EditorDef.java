/**
 * <copyright>
 * </copyright>
 *
 * $Id: EditorDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.ui.cdouidefs;

import org.eclipse.net4j.util.net4jutildefs.Def;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Editor Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ui.cdouidefs.EditorDef#getEditorID <em>Editor ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.ui.cdouidefs.CDOUIDefsPackage#getEditorDef()
 * @model
 * @generated
 */
public interface EditorDef extends Def {
	/**
   * Returns the value of the '<em><b>Editor ID</b></em>' attribute.
   * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Editor ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
   * @return the value of the '<em>Editor ID</em>' attribute.
   * @see #setEditorID(String)
   * @see org.eclipse.emf.cdo.ui.cdouidefs.CDOUIDefsPackage#getEditorDef_EditorID()
   * @model required="true"
   * @generated
   */
	String getEditorID();

	/**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ui.cdouidefs.EditorDef#getEditorID <em>Editor ID</em>}' attribute.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Editor ID</em>' attribute.
   * @see #getEditorID()
   * @generated
   */
	void setEditorID(String value);

} // EditorDef
