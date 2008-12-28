/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOViewDefImpl.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOSessionDef;
import org.eclipse.emf.cdo.cdodefs.CDOViewDef;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>CDO View Definition</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.impl.CDOViewDefImpl#getCdoSessionDef <em>Cdo Session Def</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CDOViewDefImpl extends DefImpl implements CDOViewDef {

	protected IListener instanceListener = new ContainerEventAdapter<CDOView>() {
		@Override
		protected void onRemoved(IContainer<CDOView> container, CDOView element) {
			Object instance = getInternalInstance();
			if (element == instance) {
				handleDeactivation(instance);
			}
		}
	};

	/**
	 * The cached value of the '{@link #getCdoSessionDef() <em>Cdo Session Def</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getCdoSessionDef()
	 * @generated
	 * @ordered
	 */
	protected CDOSessionDef cdoSessionDef;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected CDOViewDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CDODefsPackage.Literals.CDO_VIEW_DEF;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public CDOSessionDef getCdoSessionDef() {
		if (cdoSessionDef != null && cdoSessionDef.eIsProxy()) {
			InternalEObject oldCdoSessionDef = (InternalEObject)cdoSessionDef;
			cdoSessionDef = (CDOSessionDef)eResolveProxy(oldCdoSessionDef);
			if (cdoSessionDef != oldCdoSessionDef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF, oldCdoSessionDef, cdoSessionDef));
			}
		}
		return cdoSessionDef;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public CDOSessionDef basicGetCdoSessionDef() {
		return cdoSessionDef;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setCdoSessionDef(CDOSessionDef newCdoSessionDef) {
		CDOSessionDef oldCdoSessionDef = cdoSessionDef;
		cdoSessionDef = newCdoSessionDef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF, oldCdoSessionDef, cdoSessionDef));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
				if (resolve) return getCdoSessionDef();
				return basicGetCdoSessionDef();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
				setCdoSessionDef((CDOSessionDef)newValue);
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
		switch (featureID) {
			case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
				setCdoSessionDef((CDOSessionDef)null);
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
		switch (featureID) {
			case CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF:
				return cdoSessionDef != null;
		}
		return super.eIsSet(featureID);
	}

	@Override
	protected Object createInstance() {
		CDOSession cdoSession = (CDOSession) getCdoSessionDef().getInstance();
		return cdoSession.openView();
	}

	@Override
	protected void validateDefinition() {
		CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_VIEW_DEF__CDO_SESSION_DEF),
				"no session definition set yet!");
	}

	protected void wireInstance(Object instance) {
		EventUtil.addListener(((CDOView) instance).getSession(),
				instanceListener);
	}

	protected void unwireInstance(Object instance) {
		EventUtil.removeListener(((CDOView) instance).getSession(), instanceListener);
	}
} // CDOViewDefImpl
