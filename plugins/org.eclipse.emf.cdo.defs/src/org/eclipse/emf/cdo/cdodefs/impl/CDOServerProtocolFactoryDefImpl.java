/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOServerProtocolFactoryDefImpl.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef;
import org.eclipse.emf.cdo.cdodefs.RepositoryProviderDef;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocolFactory;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.net4jdefs.impl.ServerProtocolFactoryDefImpl;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CDO Server Protocol Factory Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.impl.CDOServerProtocolFactoryDefImpl#getRepositoryProviderDef <em>Repository Provider Def</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CDOServerProtocolFactoryDefImpl extends ServerProtocolFactoryDefImpl implements CDOServerProtocolFactoryDef {

	/**
	 * The cached value of the '{@link #getRepositoryProviderDef() <em>Repository Provider Def</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRepositoryProviderDef()
	 * @generated
	 * @ordered
	 */
	protected RepositoryProviderDef repositoryProviderDef;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CDOServerProtocolFactoryDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CDODefsPackage.Literals.CDO_SERVER_PROTOCOL_FACTORY_DEF;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RepositoryProviderDef getRepositoryProviderDef() {
		if (repositoryProviderDef != null && repositoryProviderDef.eIsProxy()) {
			InternalEObject oldRepositoryProviderDef = (InternalEObject)repositoryProviderDef;
			repositoryProviderDef = (RepositoryProviderDef)eResolveProxy(oldRepositoryProviderDef);
			if (repositoryProviderDef != oldRepositoryProviderDef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF, oldRepositoryProviderDef, repositoryProviderDef));
			}
		}
		return repositoryProviderDef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RepositoryProviderDef basicGetRepositoryProviderDef() {
		return repositoryProviderDef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRepositoryProviderDef(RepositoryProviderDef newRepositoryProviderDef) {
		RepositoryProviderDef oldRepositoryProviderDef = repositoryProviderDef;
		repositoryProviderDef = newRepositoryProviderDef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF, oldRepositoryProviderDef, repositoryProviderDef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF:
				if (resolve) return getRepositoryProviderDef();
				return basicGetRepositoryProviderDef();
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
			case CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF:
				setRepositoryProviderDef((RepositoryProviderDef)newValue);
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
			case CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF:
				setRepositoryProviderDef((RepositoryProviderDef)null);
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
			case CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF:
				return repositoryProviderDef != null;
		}
		return super.eIsSet(featureID);
	}

	@Override
	protected Object createInstance() {
		return new CDOServerProtocolFactory((IRepositoryProvider) getRepositoryProviderDef().getInstance());
	}
	
	
	@Override
	protected void validateDefinition() {
		super.validateDefinition();
		CheckUtil.checkState(getRepositoryProviderDef() != null, "repository provider not set!");
	}

} //CDOServerProtocolFactoryDefImpl
