/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_NonTransient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz356181_Transient;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz356181 Main</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl#getTransient <em>Transient</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl#getNonTransient <em>Non Transient</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl#getTransientRef <em>Transient Ref</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz356181_MainImpl#getTransientOtherRef <em>Transient Other Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz356181_MainImpl extends EObjectImpl implements Bz356181_Main {
	/**
	 * The default value of the '{@link #getTransient() <em>Transient</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransient()
	 * @generated
	 * @ordered
	 */
	protected static final String TRANSIENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTransient() <em>Transient</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransient()
	 * @generated
	 * @ordered
	 */
	protected String transient_ = TRANSIENT_EDEFAULT;

	/**
	 * The default value of the '{@link #getNonTransient() <em>Non Transient</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNonTransient()
	 * @generated
	 * @ordered
	 */
	protected static final String NON_TRANSIENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNonTransient() <em>Non Transient</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNonTransient()
	 * @generated
	 * @ordered
	 */
	protected String nonTransient = NON_TRANSIENT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTransientRef() <em>Transient Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransientRef()
	 * @generated
	 * @ordered
	 */
	protected Bz356181_Transient transientRef;

	/**
	 * The cached value of the '{@link #getTransientOtherRef() <em>Transient Other Ref</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransientOtherRef()
	 * @generated
	 * @ordered
	 */
	protected Bz356181_NonTransient transientOtherRef;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Bz356181_MainImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return HibernateTestPackage.Literals.BZ356181_MAIN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTransient() {
		return transient_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransient(String newTransient) {
		String oldTransient = transient_;
		transient_ = newTransient;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ356181_MAIN__TRANSIENT, oldTransient, transient_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNonTransient() {
		return nonTransient;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNonTransient(String newNonTransient) {
		String oldNonTransient = nonTransient;
		nonTransient = newNonTransient;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ356181_MAIN__NON_TRANSIENT, oldNonTransient, nonTransient));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_Transient getTransientRef() {
		if (transientRef != null && transientRef.eIsProxy()) {
			InternalEObject oldTransientRef = (InternalEObject)transientRef;
			transientRef = (Bz356181_Transient)eResolveProxy(oldTransientRef);
			if (transientRef != oldTransientRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, HibernateTestPackage.BZ356181_MAIN__TRANSIENT_REF, oldTransientRef, transientRef));
			}
		}
		return transientRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_Transient basicGetTransientRef() {
		return transientRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransientRef(Bz356181_Transient newTransientRef) {
		Bz356181_Transient oldTransientRef = transientRef;
		transientRef = newTransientRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ356181_MAIN__TRANSIENT_REF, oldTransientRef, transientRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_NonTransient getTransientOtherRef() {
		if (transientOtherRef != null && transientOtherRef.eIsProxy()) {
			InternalEObject oldTransientOtherRef = (InternalEObject)transientOtherRef;
			transientOtherRef = (Bz356181_NonTransient)eResolveProxy(oldTransientOtherRef);
			if (transientOtherRef != oldTransientOtherRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, HibernateTestPackage.BZ356181_MAIN__TRANSIENT_OTHER_REF, oldTransientOtherRef, transientOtherRef));
			}
		}
		return transientOtherRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz356181_NonTransient basicGetTransientOtherRef() {
		return transientOtherRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransientOtherRef(Bz356181_NonTransient newTransientOtherRef) {
		Bz356181_NonTransient oldTransientOtherRef = transientOtherRef;
		transientOtherRef = newTransientOtherRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ356181_MAIN__TRANSIENT_OTHER_REF, oldTransientOtherRef, transientOtherRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT:
				return getTransient();
			case HibernateTestPackage.BZ356181_MAIN__NON_TRANSIENT:
				return getNonTransient();
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_REF:
				if (resolve) return getTransientRef();
				return basicGetTransientRef();
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_OTHER_REF:
				if (resolve) return getTransientOtherRef();
				return basicGetTransientOtherRef();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT:
				setTransient((String)newValue);
				return;
			case HibernateTestPackage.BZ356181_MAIN__NON_TRANSIENT:
				setNonTransient((String)newValue);
				return;
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_REF:
				setTransientRef((Bz356181_Transient)newValue);
				return;
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_OTHER_REF:
				setTransientOtherRef((Bz356181_NonTransient)newValue);
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
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT:
				setTransient(TRANSIENT_EDEFAULT);
				return;
			case HibernateTestPackage.BZ356181_MAIN__NON_TRANSIENT:
				setNonTransient(NON_TRANSIENT_EDEFAULT);
				return;
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_REF:
				setTransientRef((Bz356181_Transient)null);
				return;
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_OTHER_REF:
				setTransientOtherRef((Bz356181_NonTransient)null);
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
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT:
				return TRANSIENT_EDEFAULT == null ? transient_ != null : !TRANSIENT_EDEFAULT.equals(transient_);
			case HibernateTestPackage.BZ356181_MAIN__NON_TRANSIENT:
				return NON_TRANSIENT_EDEFAULT == null ? nonTransient != null : !NON_TRANSIENT_EDEFAULT.equals(nonTransient);
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_REF:
				return transientRef != null;
			case HibernateTestPackage.BZ356181_MAIN__TRANSIENT_OTHER_REF:
				return transientOtherRef != null;
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
		result.append(" (transient: ");
		result.append(transient_);
		result.append(", nonTransient: ");
		result.append(nonTransient);
		result.append(')');
		return result.toString();
	}

} //Bz356181_MainImpl
