/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDODefsPackage.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;

import org.eclipse.net4j.net4jdefs.Net4jDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
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
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsFactory
 * @model kind="package"
 * @generated
 */
public interface CDODefsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "cdodefs";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/emf/CDO/defs/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "cdodefs";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CDODefsPackage eINSTANCE = org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOViewDefImpl <em>CDO View Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOViewDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOViewDef()
	 * @generated
	 */
	int CDO_VIEW_DEF = 0;

	/**
	 * The feature id for the '<em><b>Cdo Session Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_VIEW_DEF__CDO_SESSION_DEF = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>CDO View Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_VIEW_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOTransactionDefImpl <em>CDO Transaction Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOTransactionDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOTransactionDef()
	 * @generated
	 */
	int CDO_TRANSACTION_DEF = 1;

	/**
	 * The feature id for the '<em><b>Cdo Session Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_TRANSACTION_DEF__CDO_SESSION_DEF = CDO_VIEW_DEF__CDO_SESSION_DEF;

	/**
	 * The number of structural features of the '<em>CDO Transaction Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_TRANSACTION_DEF_FEATURE_COUNT = CDO_VIEW_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOAuditDefImpl <em>CDO Audit Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOAuditDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOAuditDef()
	 * @generated
	 */
	int CDO_AUDIT_DEF = 2;

	/**
	 * The feature id for the '<em><b>Cdo Session Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_AUDIT_DEF__CDO_SESSION_DEF = CDO_VIEW_DEF__CDO_SESSION_DEF;

	/**
	 * The feature id for the '<em><b>Time Stamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_AUDIT_DEF__TIME_STAMP = CDO_VIEW_DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>CDO Audit Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_AUDIT_DEF_FEATURE_COUNT = CDO_VIEW_DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOSessionDefImpl <em>CDO Session Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOSessionDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOSessionDef()
	 * @generated
	 */
	int CDO_SESSION_DEF = 3;

	/**
	 * The feature id for the '<em><b>Connector Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SESSION_DEF__CONNECTOR_DEF = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Repository Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SESSION_DEF__REPOSITORY_NAME = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Cdo Package Registry Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Legacy Support Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Fail Over Strategy Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>CDO Session Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SESSION_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.FailOverStrategyDefImpl <em>Fail Over Strategy Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.FailOverStrategyDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getFailOverStrategyDef()
	 * @generated
	 */
	int FAIL_OVER_STRATEGY_DEF = 4;

	/**
	 * The feature id for the '<em><b>Connector Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Fail Over Strategy Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FAIL_OVER_STRATEGY_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.RetryFailOverStrategyDefImpl <em>Retry Fail Over Strategy Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.RetryFailOverStrategyDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getRetryFailOverStrategyDef()
	 * @generated
	 */
	int RETRY_FAIL_OVER_STRATEGY_DEF = 5;

	/**
	 * The feature id for the '<em><b>Connector Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RETRY_FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF = FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF;

	/**
	 * The feature id for the '<em><b>Retries</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES = FAIL_OVER_STRATEGY_DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Retry Fail Over Strategy Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RETRY_FAIL_OVER_STRATEGY_DEF_FEATURE_COUNT = FAIL_OVER_STRATEGY_DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOPackageRegistryDefImpl <em>CDO Package Registry Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOPackageRegistryDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOPackageRegistryDef()
	 * @generated
	 */
	int CDO_PACKAGE_REGISTRY_DEF = 6;

	/**
	 * The feature id for the '<em><b>Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_PACKAGE_REGISTRY_DEF__PACKAGES = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>CDO Package Registry Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_PACKAGE_REGISTRY_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOEagerPackageRegistryDefImpl <em>CDO Eager Package Registry Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOEagerPackageRegistryDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOEagerPackageRegistryDef()
	 * @generated
	 */
	int CDO_EAGER_PACKAGE_REGISTRY_DEF = 7;

	/**
	 * The feature id for the '<em><b>Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_EAGER_PACKAGE_REGISTRY_DEF__PACKAGES = CDO_PACKAGE_REGISTRY_DEF__PACKAGES;

	/**
	 * The number of structural features of the '<em>CDO Eager Package Registry Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_EAGER_PACKAGE_REGISTRY_DEF_FEATURE_COUNT = CDO_PACKAGE_REGISTRY_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOLazyPackageRegistryDefImpl <em>CDO Lazy Package Registry Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOLazyPackageRegistryDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOLazyPackageRegistryDef()
	 * @generated
	 */
	int CDO_LAZY_PACKAGE_REGISTRY_DEF = 8;

	/**
	 * The feature id for the '<em><b>Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_LAZY_PACKAGE_REGISTRY_DEF__PACKAGES = CDO_PACKAGE_REGISTRY_DEF__PACKAGES;

	/**
	 * The number of structural features of the '<em>CDO Lazy Package Registry Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_LAZY_PACKAGE_REGISTRY_DEF_FEATURE_COUNT = CDO_PACKAGE_REGISTRY_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.EPackageDefImpl <em>EPackage Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.EPackageDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getEPackageDef()
	 * @generated
	 */
	int EPACKAGE_DEF = 9;

	/**
	 * The feature id for the '<em><b>Ns URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPACKAGE_DEF__NS_URI = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EPackage Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EPACKAGE_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.EDynamicPackageDefImpl <em>EDynamic Package Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.EDynamicPackageDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getEDynamicPackageDef()
	 * @generated
	 */
	int EDYNAMIC_PACKAGE_DEF = 10;

	/**
	 * The feature id for the '<em><b>Ns URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDYNAMIC_PACKAGE_DEF__NS_URI = EPACKAGE_DEF__NS_URI;

	/**
	 * The feature id for the '<em><b>Resource URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDYNAMIC_PACKAGE_DEF__RESOURCE_URI = EPACKAGE_DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EDynamic Package Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EDYNAMIC_PACKAGE_DEF_FEATURE_COUNT = EPACKAGE_DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.EGlobalPackageDefImpl <em>EGlobal Package Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.EGlobalPackageDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getEGlobalPackageDef()
	 * @generated
	 */
	int EGLOBAL_PACKAGE_DEF = 11;

	/**
	 * The feature id for the '<em><b>Ns URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGLOBAL_PACKAGE_DEF__NS_URI = EPACKAGE_DEF__NS_URI;

	/**
	 * The number of structural features of the '<em>EGlobal Package Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGLOBAL_PACKAGE_DEF_FEATURE_COUNT = EPACKAGE_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOClientProtocolFactoryDefImpl <em>CDO Client Protocol Factory Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOClientProtocolFactoryDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOClientProtocolFactoryDef()
	 * @generated
	 */
	int CDO_CLIENT_PROTOCOL_FACTORY_DEF = 12;

	/**
	 * The number of structural features of the '<em>CDO Client Protocol Factory Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_CLIENT_PROTOCOL_FACTORY_DEF_FEATURE_COUNT = Net4jDefsPackage.CLIENT_PROTOCOL_FACTORY_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOServerProtocolFactoryDefImpl <em>CDO Server Protocol Factory Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOServerProtocolFactoryDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOServerProtocolFactoryDef()
	 * @generated
	 */
	int CDO_SERVER_PROTOCOL_FACTORY_DEF = 13;

	/**
	 * The feature id for the '<em><b>Repository Provider Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF = Net4jDefsPackage.SERVER_PROTOCOL_FACTORY_DEF_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>CDO Server Protocol Factory Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_SERVER_PROTOCOL_FACTORY_DEF_FEATURE_COUNT = Net4jDefsPackage.SERVER_PROTOCOL_FACTORY_DEF_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.RepositoryProviderDefImpl <em>Repository Provider Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.RepositoryProviderDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getRepositoryProviderDef()
	 * @generated
	 */
	int REPOSITORY_PROVIDER_DEF = 14;

	/**
	 * The number of structural features of the '<em>Repository Provider Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REPOSITORY_PROVIDER_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.PluginRepositoryProviderDefImpl <em>Plugin Repository Provider Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.PluginRepositoryProviderDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getPluginRepositoryProviderDef()
	 * @generated
	 */
	int PLUGIN_REPOSITORY_PROVIDER_DEF = 15;

	/**
	 * The number of structural features of the '<em>Plugin Repository Provider Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PLUGIN_REPOSITORY_PROVIDER_DEF_FEATURE_COUNT = REPOSITORY_PROVIDER_DEF_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOResourceDefImpl <em>CDO Resource Def</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOResourceDefImpl
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOResourceDef()
	 * @generated
	 */
	int CDO_RESOURCE_DEF = 16;

	/**
	 * The feature id for the '<em><b>Cdo Transaction</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_RESOURCE_DEF__CDO_TRANSACTION = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Resource Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_RESOURCE_DEF__RESOURCE_MODE = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_RESOURCE_DEF__PATH = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>CDO Resource Def</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CDO_RESOURCE_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.cdo.cdodefs.ResourceMode <em>Resource Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.cdo.cdodefs.ResourceMode
	 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getResourceMode()
	 * @generated
	 */
	int RESOURCE_MODE = 17;


	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOViewDef <em>CDO View Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO View Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOViewDef
	 * @generated
	 */
	EClass getCDOViewDef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.CDOViewDef#getCdoSessionDef <em>Cdo Session Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Cdo Session Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOViewDef#getCdoSessionDef()
	 * @see #getCDOViewDef()
	 * @generated
	 */
	EReference getCDOViewDef_CdoSessionDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOTransactionDef <em>CDO Transaction Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Transaction Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOTransactionDef
	 * @generated
	 */
	EClass getCDOTransactionDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOAuditDef <em>CDO Audit Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Audit Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOAuditDef
	 * @generated
	 */
	EClass getCDOAuditDef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.CDOAuditDef#getTimeStamp <em>Time Stamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time Stamp</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOAuditDef#getTimeStamp()
	 * @see #getCDOAuditDef()
	 * @generated
	 */
	EAttribute getCDOAuditDef_TimeStamp();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef <em>CDO Session Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Session Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef
	 * @generated
	 */
	EClass getCDOSessionDef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getConnectorDef <em>Connector Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Connector Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getConnectorDef()
	 * @see #getCDOSessionDef()
	 * @generated
	 */
	EReference getCDOSessionDef_ConnectorDef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getRepositoryName <em>Repository Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Repository Name</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getRepositoryName()
	 * @see #getCDOSessionDef()
	 * @generated
	 */
	EAttribute getCDOSessionDef_RepositoryName();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getCdoPackageRegistryDef <em>Cdo Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Cdo Package Registry Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getCdoPackageRegistryDef()
	 * @see #getCDOSessionDef()
	 * @generated
	 */
	EReference getCDOSessionDef_CdoPackageRegistryDef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef#isLegacySupportEnabled <em>Legacy Support Enabled</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Legacy Support Enabled</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef#isLegacySupportEnabled()
	 * @see #getCDOSessionDef()
	 * @generated
	 */
	EAttribute getCDOSessionDef_LegacySupportEnabled();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getFailOverStrategyDef <em>Fail Over Strategy Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Fail Over Strategy Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef#getFailOverStrategyDef()
	 * @see #getCDOSessionDef()
	 * @generated
	 */
	EReference getCDOSessionDef_FailOverStrategyDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef <em>Fail Over Strategy Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fail Over Strategy Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef
	 * @generated
	 */
	EClass getFailOverStrategyDef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef#getConnectorDef <em>Connector Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Connector Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef#getConnectorDef()
	 * @see #getFailOverStrategyDef()
	 * @generated
	 */
	EReference getFailOverStrategyDef_ConnectorDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef <em>Retry Fail Over Strategy Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Retry Fail Over Strategy Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef
	 * @generated
	 */
	EClass getRetryFailOverStrategyDef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef#getRetries <em>Retries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Retries</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef#getRetries()
	 * @see #getRetryFailOverStrategyDef()
	 * @generated
	 */
	EAttribute getRetryFailOverStrategyDef_Retries();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef <em>CDO Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Package Registry Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef
	 * @generated
	 */
	EClass getCDOPackageRegistryDef();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef#getPackages <em>Packages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Packages</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef#getPackages()
	 * @see #getCDOPackageRegistryDef()
	 * @generated
	 */
	EReference getCDOPackageRegistryDef_Packages();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOEagerPackageRegistryDef <em>CDO Eager Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Eager Package Registry Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOEagerPackageRegistryDef
	 * @generated
	 */
	EClass getCDOEagerPackageRegistryDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOLazyPackageRegistryDef <em>CDO Lazy Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Lazy Package Registry Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOLazyPackageRegistryDef
	 * @generated
	 */
	EClass getCDOLazyPackageRegistryDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.EPackageDef <em>EPackage Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EPackage Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.EPackageDef
	 * @generated
	 */
	EClass getEPackageDef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.EPackageDef#getNsURI <em>Ns URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ns URI</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.EPackageDef#getNsURI()
	 * @see #getEPackageDef()
	 * @generated
	 */
	EAttribute getEPackageDef_NsURI();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef <em>EDynamic Package Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EDynamic Package Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef
	 * @generated
	 */
	EClass getEDynamicPackageDef();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef#getResourceURI <em>Resource URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource URI</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef#getResourceURI()
	 * @see #getEDynamicPackageDef()
	 * @generated
	 */
	EAttribute getEDynamicPackageDef_ResourceURI();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.EGlobalPackageDef <em>EGlobal Package Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGlobal Package Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.EGlobalPackageDef
	 * @generated
	 */
	EClass getEGlobalPackageDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOClientProtocolFactoryDef <em>CDO Client Protocol Factory Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Client Protocol Factory Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOClientProtocolFactoryDef
	 * @generated
	 */
	EClass getCDOClientProtocolFactoryDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef <em>CDO Server Protocol Factory Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Server Protocol Factory Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef
	 * @generated
	 */
	EClass getCDOServerProtocolFactoryDef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef#getRepositoryProviderDef <em>Repository Provider Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Repository Provider Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef#getRepositoryProviderDef()
	 * @see #getCDOServerProtocolFactoryDef()
	 * @generated
	 */
	EReference getCDOServerProtocolFactoryDef_RepositoryProviderDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.RepositoryProviderDef <em>Repository Provider Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Repository Provider Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.RepositoryProviderDef
	 * @generated
	 */
	EClass getRepositoryProviderDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.PluginRepositoryProviderDef <em>Plugin Repository Provider Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Plugin Repository Provider Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.PluginRepositoryProviderDef
	 * @generated
	 */
	EClass getPluginRepositoryProviderDef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.cdo.cdodefs.CDOResourceDef <em>CDO Resource Def</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CDO Resource Def</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOResourceDef
	 * @generated
	 */
	EClass getCDOResourceDef();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.cdodefs.CDOResourceDef#getCdoTransaction <em>Cdo Transaction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Cdo Transaction</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOResourceDef#getCdoTransaction()
	 * @see #getCDOResourceDef()
	 * @generated
	 */
	EReference getCDOResourceDef_CdoTransaction();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.CDOResourceDef#getResourceMode <em>Resource Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource Mode</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOResourceDef#getResourceMode()
	 * @see #getCDOResourceDef()
	 * @generated
	 */
	EAttribute getCDOResourceDef_ResourceMode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.cdodefs.CDOResourceDef#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOResourceDef#getPath()
	 * @see #getCDOResourceDef()
	 * @generated
	 */
	EAttribute getCDOResourceDef_Path();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.emf.cdo.cdodefs.ResourceMode <em>Resource Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Resource Mode</em>'.
	 * @see org.eclipse.emf.cdo.cdodefs.ResourceMode
	 * @generated
	 */
	EEnum getResourceMode();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CDODefsFactory getCDODefsFactory();

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
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOViewDefImpl <em>CDO View Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOViewDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOViewDef()
		 * @generated
		 */
		EClass CDO_VIEW_DEF = eINSTANCE.getCDOViewDef();

		/**
		 * The meta object literal for the '<em><b>Cdo Session Def</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_VIEW_DEF__CDO_SESSION_DEF = eINSTANCE.getCDOViewDef_CdoSessionDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOTransactionDefImpl <em>CDO Transaction Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOTransactionDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOTransactionDef()
		 * @generated
		 */
		EClass CDO_TRANSACTION_DEF = eINSTANCE.getCDOTransactionDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOAuditDefImpl <em>CDO Audit Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOAuditDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOAuditDef()
		 * @generated
		 */
		EClass CDO_AUDIT_DEF = eINSTANCE.getCDOAuditDef();

		/**
		 * The meta object literal for the '<em><b>Time Stamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CDO_AUDIT_DEF__TIME_STAMP = eINSTANCE.getCDOAuditDef_TimeStamp();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOSessionDefImpl <em>CDO Session Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOSessionDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOSessionDef()
		 * @generated
		 */
		EClass CDO_SESSION_DEF = eINSTANCE.getCDOSessionDef();

		/**
		 * The meta object literal for the '<em><b>Connector Def</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_SESSION_DEF__CONNECTOR_DEF = eINSTANCE.getCDOSessionDef_ConnectorDef();

		/**
		 * The meta object literal for the '<em><b>Repository Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CDO_SESSION_DEF__REPOSITORY_NAME = eINSTANCE.getCDOSessionDef_RepositoryName();

		/**
		 * The meta object literal for the '<em><b>Cdo Package Registry Def</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_SESSION_DEF__CDO_PACKAGE_REGISTRY_DEF = eINSTANCE.getCDOSessionDef_CdoPackageRegistryDef();

		/**
		 * The meta object literal for the '<em><b>Legacy Support Enabled</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CDO_SESSION_DEF__LEGACY_SUPPORT_ENABLED = eINSTANCE.getCDOSessionDef_LegacySupportEnabled();

		/**
		 * The meta object literal for the '<em><b>Fail Over Strategy Def</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_SESSION_DEF__FAIL_OVER_STRATEGY_DEF = eINSTANCE.getCDOSessionDef_FailOverStrategyDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.FailOverStrategyDefImpl <em>Fail Over Strategy Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.FailOverStrategyDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getFailOverStrategyDef()
		 * @generated
		 */
		EClass FAIL_OVER_STRATEGY_DEF = eINSTANCE.getFailOverStrategyDef();

		/**
		 * The meta object literal for the '<em><b>Connector Def</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FAIL_OVER_STRATEGY_DEF__CONNECTOR_DEF = eINSTANCE.getFailOverStrategyDef_ConnectorDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.RetryFailOverStrategyDefImpl <em>Retry Fail Over Strategy Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.RetryFailOverStrategyDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getRetryFailOverStrategyDef()
		 * @generated
		 */
		EClass RETRY_FAIL_OVER_STRATEGY_DEF = eINSTANCE.getRetryFailOverStrategyDef();

		/**
		 * The meta object literal for the '<em><b>Retries</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RETRY_FAIL_OVER_STRATEGY_DEF__RETRIES = eINSTANCE.getRetryFailOverStrategyDef_Retries();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOPackageRegistryDefImpl <em>CDO Package Registry Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOPackageRegistryDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOPackageRegistryDef()
		 * @generated
		 */
		EClass CDO_PACKAGE_REGISTRY_DEF = eINSTANCE.getCDOPackageRegistryDef();

		/**
		 * The meta object literal for the '<em><b>Packages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_PACKAGE_REGISTRY_DEF__PACKAGES = eINSTANCE.getCDOPackageRegistryDef_Packages();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOEagerPackageRegistryDefImpl <em>CDO Eager Package Registry Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOEagerPackageRegistryDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOEagerPackageRegistryDef()
		 * @generated
		 */
		EClass CDO_EAGER_PACKAGE_REGISTRY_DEF = eINSTANCE.getCDOEagerPackageRegistryDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOLazyPackageRegistryDefImpl <em>CDO Lazy Package Registry Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOLazyPackageRegistryDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOLazyPackageRegistryDef()
		 * @generated
		 */
		EClass CDO_LAZY_PACKAGE_REGISTRY_DEF = eINSTANCE.getCDOLazyPackageRegistryDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.EPackageDefImpl <em>EPackage Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.EPackageDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getEPackageDef()
		 * @generated
		 */
		EClass EPACKAGE_DEF = eINSTANCE.getEPackageDef();

		/**
		 * The meta object literal for the '<em><b>Ns URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EPACKAGE_DEF__NS_URI = eINSTANCE.getEPackageDef_NsURI();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.EDynamicPackageDefImpl <em>EDynamic Package Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.EDynamicPackageDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getEDynamicPackageDef()
		 * @generated
		 */
		EClass EDYNAMIC_PACKAGE_DEF = eINSTANCE.getEDynamicPackageDef();

		/**
		 * The meta object literal for the '<em><b>Resource URI</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EDYNAMIC_PACKAGE_DEF__RESOURCE_URI = eINSTANCE.getEDynamicPackageDef_ResourceURI();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.EGlobalPackageDefImpl <em>EGlobal Package Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.EGlobalPackageDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getEGlobalPackageDef()
		 * @generated
		 */
		EClass EGLOBAL_PACKAGE_DEF = eINSTANCE.getEGlobalPackageDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOClientProtocolFactoryDefImpl <em>CDO Client Protocol Factory Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOClientProtocolFactoryDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOClientProtocolFactoryDef()
		 * @generated
		 */
		EClass CDO_CLIENT_PROTOCOL_FACTORY_DEF = eINSTANCE.getCDOClientProtocolFactoryDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOServerProtocolFactoryDefImpl <em>CDO Server Protocol Factory Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOServerProtocolFactoryDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOServerProtocolFactoryDef()
		 * @generated
		 */
		EClass CDO_SERVER_PROTOCOL_FACTORY_DEF = eINSTANCE.getCDOServerProtocolFactoryDef();

		/**
		 * The meta object literal for the '<em><b>Repository Provider Def</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_SERVER_PROTOCOL_FACTORY_DEF__REPOSITORY_PROVIDER_DEF = eINSTANCE.getCDOServerProtocolFactoryDef_RepositoryProviderDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.RepositoryProviderDefImpl <em>Repository Provider Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.RepositoryProviderDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getRepositoryProviderDef()
		 * @generated
		 */
		EClass REPOSITORY_PROVIDER_DEF = eINSTANCE.getRepositoryProviderDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.PluginRepositoryProviderDefImpl <em>Plugin Repository Provider Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.PluginRepositoryProviderDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getPluginRepositoryProviderDef()
		 * @generated
		 */
		EClass PLUGIN_REPOSITORY_PROVIDER_DEF = eINSTANCE.getPluginRepositoryProviderDef();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.impl.CDOResourceDefImpl <em>CDO Resource Def</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDOResourceDefImpl
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getCDOResourceDef()
		 * @generated
		 */
		EClass CDO_RESOURCE_DEF = eINSTANCE.getCDOResourceDef();

		/**
		 * The meta object literal for the '<em><b>Cdo Transaction</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CDO_RESOURCE_DEF__CDO_TRANSACTION = eINSTANCE.getCDOResourceDef_CdoTransaction();

		/**
		 * The meta object literal for the '<em><b>Resource Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CDO_RESOURCE_DEF__RESOURCE_MODE = eINSTANCE.getCDOResourceDef_ResourceMode();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CDO_RESOURCE_DEF__PATH = eINSTANCE.getCDOResourceDef_Path();

		/**
		 * The meta object literal for the '{@link org.eclipse.emf.cdo.cdodefs.ResourceMode <em>Resource Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.emf.cdo.cdodefs.ResourceMode
		 * @see org.eclipse.emf.cdo.cdodefs.impl.CDODefsPackageImpl#getResourceMode()
		 * @generated
		 */
		EEnum RESOURCE_MODE = eINSTANCE.getResourceMode();

	}

} //CDODefsPackage
