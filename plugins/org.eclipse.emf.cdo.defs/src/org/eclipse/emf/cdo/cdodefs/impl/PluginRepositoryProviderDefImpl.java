/**
 * <copyright>
 * </copyright>
 *
 * $Id: PluginRepositoryProviderDefImpl.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.PluginRepositoryProviderDef;
import org.eclipse.emf.cdo.internal.server.PluginRepositoryProvider;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Plugin Repository Provider Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PluginRepositoryProviderDefImpl extends RepositoryProviderDefImpl implements PluginRepositoryProviderDef {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PluginRepositoryProviderDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CDODefsPackage.Literals.PLUGIN_REPOSITORY_PROVIDER_DEF;
	}

	@Override
	protected Object createInstance() {
		return PluginRepositoryProvider.INSTANCE;
	}

} //PluginRepositoryProviderDefImpl
