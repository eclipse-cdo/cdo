/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jUtilDefsFactoryImpl.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs.impl;

import org.eclipse.net4j.util.net4jutildefs.DefsContainer;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef;

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
public class Net4jUtilDefsFactoryImpl extends EFactoryImpl implements Net4jUtilDefsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Net4jUtilDefsFactory init() {
		try {
			Net4jUtilDefsFactory theNet4jUtilDefsFactory = (Net4jUtilDefsFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/NET4J/util/defs/1.0.0"); 
			if (theNet4jUtilDefsFactory != null) {
				return theNet4jUtilDefsFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new Net4jUtilDefsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4jUtilDefsFactoryImpl() {
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
			case Net4jUtilDefsPackage.DEFS_CONTAINER: return createDefsContainer();
			case Net4jUtilDefsPackage.THREAD_POOL_DEF: return createThreadPoolDef();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DefsContainer createDefsContainer() {
		DefsContainerImpl defsContainer = new DefsContainerImpl();
		return defsContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ThreadPoolDef createThreadPoolDef() {
		ThreadPoolDefImpl threadPoolDef = new ThreadPoolDefImpl();
		return threadPoolDef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Net4jUtilDefsPackage getNet4jUtilDefsPackage() {
		return (Net4jUtilDefsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static Net4jUtilDefsPackage getPackage() {
		return Net4jUtilDefsPackage.eINSTANCE;
	}

} //Net4jUtilDefsFactoryImpl
