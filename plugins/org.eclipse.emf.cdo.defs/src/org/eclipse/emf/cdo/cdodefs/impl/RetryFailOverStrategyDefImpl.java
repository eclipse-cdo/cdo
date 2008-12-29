/**
 * <copyright>
 * </copyright>
 *
 * $Id: RetryFailOverStrategyDefImpl.java,v 1.2 2008-12-29 14:01:19 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.signal.failover.RetryFailOverStrategy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Retry Fail Over Strategy</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.impl.RetryFailOverStrategyDefImpl#getRetries <em>Retries</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RetryFailOverStrategyDefImpl extends FailOverStrategyDefImpl
		implements RetryFailOverStrategyDef {
	/**
   * The default value of the '{@link #getRetries() <em>Retries</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getRetries()
   * @generated
   * @ordered
   */
	protected static final int RETRIES_EDEFAULT = 0;

	/**
   * The cached value of the '{@link #getRetries() <em>Retries</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getRetries()
   * @generated
   * @ordered
   */
	protected int retries = RETRIES_EDEFAULT;

	/**
   * This is true if the Retries attribute has been set.
   * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
	protected boolean retriesESet;

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	protected RetryFailOverStrategyDefImpl() {
    super();
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	@Override
	protected EClass eStaticClass() {
    return CDODefsPackage.Literals.RETRY_FAIL_OVER_STRATEGY_DEF;
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	public int getRetries() {
    return retries;
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	public void setRetries(int newRetries) {
    int oldRetries = retries;
    retries = newRetries;
    boolean oldRetriesESet = retriesESet;
    retriesESet = true;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES, oldRetries, retries, !oldRetriesESet));
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	public void unsetRetries() {
    int oldRetries = retries;
    boolean oldRetriesESet = retriesESet;
    retries = RETRIES_EDEFAULT;
    retriesESet = false;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.UNSET, CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES, oldRetries, RETRIES_EDEFAULT, oldRetriesESet));
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	public boolean isSetRetries() {
    return retriesESet;
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID)
    {
      case CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES:
        return getRetries();
    }
    return super.eGet(featureID, resolve, coreType);
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	@Override
	public void eSet(int featureID, Object newValue) {
    switch (featureID)
    {
      case CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES:
        setRetries((Integer)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	@Override
	public void eUnset(int featureID) {
    switch (featureID)
    {
      case CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES:
        unsetRetries();
        return;
    }
    super.eUnset(featureID);
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	@Override
	public boolean eIsSet(int featureID) {
    switch (featureID)
    {
      case CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES:
        return isSetRetries();
    }
    return super.eIsSet(featureID);
  }

	/**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
	@Override
	public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (retries: ");
    if (retriesESet) result.append(retries); else result.append("<unset>");
    result.append(')');
    return result.toString();
  }

	@Override
	protected IFailOverStrategy createInstance() {
		IFailOverStrategy failOverStrategy = null;
		IConnector connector = (IConnector) getConnectorDef().getInstance();
		if (isSetRetries()) {
			failOverStrategy = new RetryFailOverStrategy(connector,
					getRetries());
		} else {
			failOverStrategy = new RetryFailOverStrategy(connector);
		}
		return failOverStrategy;
	}
} // RetryFailOverStrategyImpl
