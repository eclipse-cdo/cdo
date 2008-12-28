/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jUtilDefsPackage.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory
 * @model kind="package"
 * @generated
 */
public interface Net4jUtilDefsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "net4jutildefs";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/NET4J/util/defs/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "net4jutildefs";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Net4jUtilDefsPackage eINSTANCE = org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.DefsContainerImpl <em>Defs Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.DefsContainerImpl
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getDefsContainer()
	 * @generated
	 */
	int DEFS_CONTAINER = 0;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFS_CONTAINER__DEFINITIONS = 0;

	/**
	 * The feature id for the '<em><b>Default Definition</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFS_CONTAINER__DEFAULT_DEFINITION = 1;

	/**
	 * The number of structural features of the '<em>Defs Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFS_CONTAINER_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.DefImpl <em>Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.DefImpl
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getDef()
	 * @generated
	 */
	int DEF = 1;

	/**
	 * The number of structural features of the '<em>Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEF_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.ExecutorServiceDefImpl <em>Executor Service Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.ExecutorServiceDefImpl
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getExecutorServiceDef()
	 * @generated
	 */
	int EXECUTOR_SERVICE_DEF = 2;

	/**
	 * The number of structural features of the '<em>Executor Service Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXECUTOR_SERVICE_DEF_FEATURE_COUNT = DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.ThreadPoolDefImpl <em>Thread Pool Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.ThreadPoolDefImpl
	 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getThreadPoolDef()
	 * @generated
	 */
	int THREAD_POOL_DEF = 3;

	/**
	 * The number of structural features of the '<em>Thread Pool Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int THREAD_POOL_DEF_FEATURE_COUNT = EXECUTOR_SERVICE_DEF_FEATURE_COUNT + 0;

	/**
	 * Returns the meta object for class '{@link org.eclipse.net4j.util.net4jutildefs.DefsContainer <em>Defs Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Defs Container</em>'.
	 * @see org.eclipse.net4j.util.net4jutildefs.DefsContainer
	 * @generated
	 */
	EClass getDefsContainer();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefinitions <em>Definitions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Definitions</em>'.
	 * @see org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefinitions()
	 * @see #getDefsContainer()
	 * @generated
	 */
	EReference getDefsContainer_Definitions();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefaultDefinition <em>Default Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Default Definition</em>'.
	 * @see org.eclipse.net4j.util.net4jutildefs.DefsContainer#getDefaultDefinition()
	 * @see #getDefsContainer()
	 * @generated
	 */
	EReference getDefsContainer_DefaultDefinition();

	/**
	 * Returns the meta object for class '{@link org.eclipse.net4j.util.net4jutildefs.Def <em>Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Def</em>'.
	 * @see org.eclipse.net4j.util.net4jutildefs.Def
	 * @generated
	 */
	EClass getDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef <em>Executor Service Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Executor Service Def</em>'.
	 * @see org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef
	 * @generated
	 */
	EClass getExecutorServiceDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef <em>Thread Pool Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Thread Pool Def</em>'.
	 * @see org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef
	 * @generated
	 */
	EClass getThreadPoolDef();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	Net4jUtilDefsFactory getNet4jUtilDefsFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.DefsContainerImpl <em>Defs Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.DefsContainerImpl
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getDefsContainer()
		 * @generated
		 */
		EClass DEFS_CONTAINER = eINSTANCE.getDefsContainer();

		/**
		 * The meta object literal for the '<em><b>Definitions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEFS_CONTAINER__DEFINITIONS = eINSTANCE.getDefsContainer_Definitions();

		/**
		 * The meta object literal for the '<em><b>Default Definition</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEFS_CONTAINER__DEFAULT_DEFINITION = eINSTANCE.getDefsContainer_DefaultDefinition();

		/**
		 * The meta object literal for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.DefImpl <em>Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.DefImpl
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getDef()
		 * @generated
		 */
		EClass DEF = eINSTANCE.getDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.ExecutorServiceDefImpl <em>Executor Service Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.ExecutorServiceDefImpl
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getExecutorServiceDef()
		 * @generated
		 */
		EClass EXECUTOR_SERVICE_DEF = eINSTANCE.getExecutorServiceDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.net4j.util.net4jutildefs.impl.ThreadPoolDefImpl <em>Thread Pool Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.ThreadPoolDefImpl
		 * @see org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsPackageImpl#getThreadPoolDef()
		 * @generated
		 */
		EClass THREAD_POOL_DEF = eINSTANCE.getThreadPoolDef();

	}

} //Net4jUtilDefsPackage
