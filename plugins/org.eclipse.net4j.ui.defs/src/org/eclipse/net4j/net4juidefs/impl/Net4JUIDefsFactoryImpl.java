/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4JUIDefsFactoryImpl.java,v 1.1 2008-12-28 18:07:30 estepper Exp $
 */
package org.eclipse.net4j.net4juidefs.impl;

import org.eclipse.net4j.net4juidefs.InteractiveCredentialsProviderDef;
import org.eclipse.net4j.net4juidefs.Net4JUIDefsFactory;
import org.eclipse.net4j.net4juidefs.Net4JUIDefsPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Net4JUIDefsFactoryImpl extends EFactoryImpl implements Net4JUIDefsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Net4JUIDefsFactory init() {
		try {
			Net4JUIDefsFactory theNet4JUIDefsFactory = (Net4JUIDefsFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/NET4J/ui/defs/1.0.0"); 
			if (theNet4JUIDefsFactory != null) {
				return theNet4JUIDefsFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new Net4JUIDefsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4JUIDefsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case Net4JUIDefsPackage.INTERACTIVE_CREDENTIALS_PROVIDER_DEF: return createInteractiveCredentialsProviderDef();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InteractiveCredentialsProviderDef createInteractiveCredentialsProviderDef() {
		InteractiveCredentialsProviderDefImpl interactiveCredentialsProviderDef = new InteractiveCredentialsProviderDefImpl();
		return interactiveCredentialsProviderDef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4JUIDefsPackage getNet4JUIDefsPackage() {
		return (Net4JUIDefsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static Net4JUIDefsPackage getPackage() {
		return Net4JUIDefsPackage.eINSTANCE;
	}

} //Net4JUIDefsFactoryImpl
