/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import java.util.Collection;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz397682 P</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682PImpl#getDbId <em>Db Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682PImpl#getListOfC <em>List Of C</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz397682PImpl extends EObjectImpl implements Bz397682P {
	/**
	 * The default value of the '{@link #getDbId() <em>Db Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDbId()
	 * @generated
	 * @ordered
	 */
	protected static final String DB_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDbId() <em>Db Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDbId()
	 * @generated
	 * @ordered
	 */
	protected String dbId = DB_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getListOfC() <em>List Of C</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getListOfC()
	 * @generated
	 * @ordered
	 */
	protected EList<Bz397682C> listOfC;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Bz397682PImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return HibernateTestPackage.Literals.BZ397682_P;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDbId() {
		return dbId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDbId(String newDbId) {
		String oldDbId = dbId;
		dbId = newDbId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ397682_P__DB_ID, oldDbId, dbId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Bz397682C> getListOfC() {
		if (listOfC == null) {
			listOfC = new EObjectContainmentWithInverseEList<Bz397682C>(Bz397682C.class, this, HibernateTestPackage.BZ397682_P__LIST_OF_C, HibernateTestPackage.BZ397682_C__REF_TO_P);
		}
		return listOfC;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case HibernateTestPackage.BZ397682_P__LIST_OF_C:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getListOfC()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case HibernateTestPackage.BZ397682_P__LIST_OF_C:
				return ((InternalEList<?>)getListOfC()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case HibernateTestPackage.BZ397682_P__DB_ID:
				return getDbId();
			case HibernateTestPackage.BZ397682_P__LIST_OF_C:
				return getListOfC();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case HibernateTestPackage.BZ397682_P__DB_ID:
				setDbId((String)newValue);
				return;
			case HibernateTestPackage.BZ397682_P__LIST_OF_C:
				getListOfC().clear();
				getListOfC().addAll((Collection<? extends Bz397682C>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case HibernateTestPackage.BZ397682_P__DB_ID:
				setDbId(DB_ID_EDEFAULT);
				return;
			case HibernateTestPackage.BZ397682_P__LIST_OF_C:
				getListOfC().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case HibernateTestPackage.BZ397682_P__DB_ID:
				return DB_ID_EDEFAULT == null ? dbId != null : !DB_ID_EDEFAULT.equals(dbId);
			case HibernateTestPackage.BZ397682_P__LIST_OF_C:
				return listOfC != null && !listOfC.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (dbId: ");
		result.append(dbId);
		result.append(')');
		return result.toString();
	}

} //Bz397682PImpl
