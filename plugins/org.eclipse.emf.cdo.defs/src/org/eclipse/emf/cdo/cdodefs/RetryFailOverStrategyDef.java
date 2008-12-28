/**
 * <copyright>
 * </copyright>
 *
 * $Id: RetryFailOverStrategyDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Retry Fail Over Strategy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getRetryFailOverStrategyDef()
 * @model
 * @generated
 */
public interface RetryFailOverStrategyDef extends FailOverStrategyDef {
	/**
	 * Returns the value of the '<em><b>Retries</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Retries</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Retries</em>' attribute.
	 * @see #isSetRetries()
	 * @see #unsetRetries()
	 * @see #setRetries(int)
	 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getRetryFailOverStrategyDef_Retries()
	 * @model unsettable="true"
	 * @generated
	 */
	int getRetries();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Retries</em>' attribute.
	 * @see #isSetRetries()
	 * @see #unsetRetries()
	 * @see #getRetries()
	 * @generated
	 */
	void setRetries(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRetries()
	 * @see #getRetries()
	 * @see #setRetries(int)
	 * @generated
	 */
	void unsetRetries();

	/**
	 * Returns whether the value of the '{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Retries</em>' attribute is set.
	 * @see #unsetRetries()
	 * @see #getRetries()
	 * @see #setRetries(int)
	 * @generated
	 */
	boolean isSetRetries();

} // RetryFailOverStrategy
