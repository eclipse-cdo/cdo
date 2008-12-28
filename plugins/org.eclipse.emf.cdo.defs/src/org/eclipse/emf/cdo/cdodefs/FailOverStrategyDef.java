/**
 * <copyright>
 * </copyright>
 *
 * $Id: FailOverStrategyDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;

import org.eclipse.net4j.net4jdefs.ConnectorDef;
import org.eclipse.net4j.util.net4jutildefs.Def;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fail Over Strategy Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef#getConnectorDef <em>Connector Def</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getFailOverStrategyDef()
 * @model abstract="true"
 * @generated
 */
public interface FailOverStrategyDef extends Def {
	/**
	 * Returns the value of the '<em><b>Connector Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Connector Def</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Connector Def</em>' reference.
	 * @see #setConnectorDef(ConnectorDef)
	 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getFailOverStrategyDef_ConnectorDef()
	 * @model required="true"
	 * @generated
	 */
	ConnectorDef getConnectorDef();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef#getConnectorDef <em>Connector Def</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Connector Def</em>' reference.
	 * @see #getConnectorDef()
	 * @generated
	 */
	void setConnectorDef(ConnectorDef value);

} // FailOverStrategyDef
