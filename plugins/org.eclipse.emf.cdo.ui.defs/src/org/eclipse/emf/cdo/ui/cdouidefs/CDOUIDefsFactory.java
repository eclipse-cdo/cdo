/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOUIDefsFactory.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.ui.cdouidefs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.ui.cdouidefs.CDOUIDefsPackage
 * @generated
 */
public interface CDOUIDefsFactory extends EFactory {
	/**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	CDOUIDefsFactory eINSTANCE = org.eclipse.emf.cdo.ui.cdouidefs.impl.CDOUIDefsFactoryImpl.init();

	/**
   * Returns a new object of class '<em>Editor Def</em>'.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @return a new object of class '<em>Editor Def</em>'.
   * @generated
   */
	EditorDef createEditorDef();

	/**
   * Returns a new object of class '<em>CDO Editor Def</em>'.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @return a new object of class '<em>CDO Editor Def</em>'.
   * @generated
   */
	CDOEditorDef createCDOEditorDef();

	/**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
	CDOUIDefsPackage getCDOUIDefsPackage();

} //CDOUIDefsFactory
