/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz398057 B</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl#getRefToClassA <em>Ref To Class A</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057BImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz398057BImpl extends EObjectImpl implements Bz398057B {
	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final double VALUE_EDEFAULT = 0.0;
	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected double value = VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Bz398057BImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return HibernateTestPackage.Literals.BZ398057_B;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bz398057A getRefToClassA() {
		if (eContainerFeatureID() != HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A) return null;
		return (Bz398057A)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRefToClassA(Bz398057A newRefToClassA, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newRefToClassA, HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefToClassA(Bz398057A newRefToClassA) {
		if (newRefToClassA != eInternalContainer() || (eContainerFeatureID() != HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A && newRefToClassA != null)) {
			if (EcoreUtil.isAncestor(this, newRefToClassA))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newRefToClassA != null)
				msgs = ((InternalEObject)newRefToClassA).eInverseAdd(this, HibernateTestPackage.BZ398057_A__LIST_OF_B, Bz398057A.class, msgs);
			msgs = basicSetRefToClassA(newRefToClassA, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A, newRefToClassA, newRefToClassA));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(double newValue) {
		double oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ398057_B__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ398057_B__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetRefToClassA((Bz398057A)otherEnd, msgs);
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
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				return basicSetRefToClassA(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				return eInternalContainer().eInverseRemove(this, HibernateTestPackage.BZ398057_A__LIST_OF_B, Bz398057A.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				return getRefToClassA();
			case HibernateTestPackage.BZ398057_B__VALUE:
				return getValue();
			case HibernateTestPackage.BZ398057_B__ID:
				return getId();
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
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				setRefToClassA((Bz398057A)newValue);
				return;
			case HibernateTestPackage.BZ398057_B__VALUE:
				setValue((Double)newValue);
				return;
			case HibernateTestPackage.BZ398057_B__ID:
				setId((String)newValue);
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
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				setRefToClassA((Bz398057A)null);
				return;
			case HibernateTestPackage.BZ398057_B__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case HibernateTestPackage.BZ398057_B__ID:
				setId(ID_EDEFAULT);
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
			case HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A:
				return getRefToClassA() != null;
			case HibernateTestPackage.BZ398057_B__VALUE:
				return value != VALUE_EDEFAULT;
			case HibernateTestPackage.BZ398057_B__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(" (value: ");
		result.append(value);
		result.append(", id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //Bz398057BImpl
