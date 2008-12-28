/**
 * <copyright>
 * </copyright>
 *
 * $Id: InteractiveCredentialsProviderDefImpl.java,v 1.1 2008-12-28 18:07:30 estepper Exp $
 */
package org.eclipse.net4j.net4juidefs.impl;

import org.eclipse.net4j.net4juidefs.InteractiveCredentialsProviderDef;
import org.eclipse.net4j.net4juidefs.Net4JUIDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.ui.security.InteractiveCredentialsProvider;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Interactive Credentials Provider Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class InteractiveCredentialsProviderDefImpl extends
		DefImpl implements
		InteractiveCredentialsProviderDef {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected InteractiveCredentialsProviderDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Net4JUIDefsPackage.Literals.INTERACTIVE_CREDENTIALS_PROVIDER_DEF;
	}

	@Override
	protected Object createInstance() {
		InteractiveCredentialsProvider interactivePasswordCredentialsProvider = new InteractiveCredentialsProvider();
		IPasswordCredentials credentials = interactivePasswordCredentialsProvider
				.getCredentials();
		return interactivePasswordCredentialsProvider;
	}
} // InteractiveCredentialsProviderDefImpl
