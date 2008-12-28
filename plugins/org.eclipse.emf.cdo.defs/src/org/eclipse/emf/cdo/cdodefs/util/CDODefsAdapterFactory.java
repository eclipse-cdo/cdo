/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDODefsAdapterFactory.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.util;

import org.eclipse.emf.cdo.cdodefs.CDOAuditDef;
import org.eclipse.emf.cdo.cdodefs.CDOClientProtocolFactoryDef;
import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOEagerPackageRegistryDef;
import org.eclipse.emf.cdo.cdodefs.CDOLazyPackageRegistryDef;
import org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.cdodefs.CDOResourceDef;
import org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef;
import org.eclipse.emf.cdo.cdodefs.CDOSessionDef;
import org.eclipse.emf.cdo.cdodefs.CDOTransactionDef;
import org.eclipse.emf.cdo.cdodefs.CDOViewDef;
import org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef;
import org.eclipse.emf.cdo.cdodefs.EGlobalPackageDef;
import org.eclipse.emf.cdo.cdodefs.EPackageDef;
import org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef;
import org.eclipse.emf.cdo.cdodefs.PluginRepositoryProviderDef;
import org.eclipse.emf.cdo.cdodefs.RepositoryProviderDef;
import org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef;

import org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef;
import org.eclipse.net4j.net4jdefs.ProtocolProviderDef;
import org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef;
import org.eclipse.net4j.util.net4jutildefs.Def;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage
 * @generated
 */
public class CDODefsAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CDODefsPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CDODefsAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = CDODefsPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CDODefsSwitch<Adapter> modelSwitch =
		new CDODefsSwitch<Adapter>() {
			@Override
			public Adapter caseCDOViewDef(CDOViewDef object) {
				return createCDOViewDefAdapter();
			}
			@Override
			public Adapter caseCDOTransactionDef(CDOTransactionDef object) {
				return createCDOTransactionDefAdapter();
			}
			@Override
			public Adapter caseCDOAuditDef(CDOAuditDef object) {
				return createCDOAuditDefAdapter();
			}
			@Override
			public Adapter caseCDOSessionDef(CDOSessionDef object) {
				return createCDOSessionDefAdapter();
			}
			@Override
			public Adapter caseFailOverStrategyDef(FailOverStrategyDef object) {
				return createFailOverStrategyDefAdapter();
			}
			@Override
			public Adapter caseRetryFailOverStrategyDef(RetryFailOverStrategyDef object) {
				return createRetryFailOverStrategyDefAdapter();
			}
			@Override
			public Adapter caseCDOPackageRegistryDef(CDOPackageRegistryDef object) {
				return createCDOPackageRegistryDefAdapter();
			}
			@Override
			public Adapter caseCDOEagerPackageRegistryDef(CDOEagerPackageRegistryDef object) {
				return createCDOEagerPackageRegistryDefAdapter();
			}
			@Override
			public Adapter caseCDOLazyPackageRegistryDef(CDOLazyPackageRegistryDef object) {
				return createCDOLazyPackageRegistryDefAdapter();
			}
			@Override
			public Adapter caseEPackageDef(EPackageDef object) {
				return createEPackageDefAdapter();
			}
			@Override
			public Adapter caseEDynamicPackageDef(EDynamicPackageDef object) {
				return createEDynamicPackageDefAdapter();
			}
			@Override
			public Adapter caseEGlobalPackageDef(EGlobalPackageDef object) {
				return createEGlobalPackageDefAdapter();
			}
			@Override
			public Adapter caseCDOClientProtocolFactoryDef(CDOClientProtocolFactoryDef object) {
				return createCDOClientProtocolFactoryDefAdapter();
			}
			@Override
			public Adapter caseCDOServerProtocolFactoryDef(CDOServerProtocolFactoryDef object) {
				return createCDOServerProtocolFactoryDefAdapter();
			}
			@Override
			public Adapter caseRepositoryProviderDef(RepositoryProviderDef object) {
				return createRepositoryProviderDefAdapter();
			}
			@Override
			public Adapter casePluginRepositoryProviderDef(PluginRepositoryProviderDef object) {
				return createPluginRepositoryProviderDefAdapter();
			}
			@Override
			public Adapter caseCDOResourceDef(CDOResourceDef object) {
				return createCDOResourceDefAdapter();
			}
			@Override
			public Adapter caseDef(Def object) {
				return createDefAdapter();
			}
			@Override
			public Adapter caseProtocolProviderDef(ProtocolProviderDef object) {
				return createProtocolProviderDefAdapter();
			}
			@Override
			public Adapter caseClientProtocolFactoryDef(ClientProtocolFactoryDef object) {
				return createClientProtocolFactoryDefAdapter();
			}
			@Override
			public Adapter caseServerProtocolFactoryDef(ServerProtocolFactoryDef object) {
				return createServerProtocolFactoryDefAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOViewDef <em>CDO View Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOViewDef
	 * @generated
	 */
	public Adapter createCDOViewDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOTransactionDef <em>CDO Transaction Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOTransactionDef
	 * @generated
	 */
	public Adapter createCDOTransactionDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOAuditDef <em>CDO Audit Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOAuditDef
	 * @generated
	 */
	public Adapter createCDOAuditDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOSessionDef <em>CDO Session Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOSessionDef
	 * @generated
	 */
	public Adapter createCDOSessionDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef <em>Fail Over Strategy Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.FailOverStrategyDef
	 * @generated
	 */
	public Adapter createFailOverStrategyDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef <em>Retry Fail Over Strategy Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.RetryFailOverStrategyDef
	 * @generated
	 */
	public Adapter createRetryFailOverStrategyDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef <em>CDO Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef
	 * @generated
	 */
	public Adapter createCDOPackageRegistryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOEagerPackageRegistryDef <em>CDO Eager Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOEagerPackageRegistryDef
	 * @generated
	 */
	public Adapter createCDOEagerPackageRegistryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOLazyPackageRegistryDef <em>CDO Lazy Package Registry Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOLazyPackageRegistryDef
	 * @generated
	 */
	public Adapter createCDOLazyPackageRegistryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.EPackageDef <em>EPackage Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.EPackageDef
	 * @generated
	 */
	public Adapter createEPackageDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef <em>EDynamic Package Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef
	 * @generated
	 */
	public Adapter createEDynamicPackageDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.EGlobalPackageDef <em>EGlobal Package Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.EGlobalPackageDef
	 * @generated
	 */
	public Adapter createEGlobalPackageDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOClientProtocolFactoryDef <em>CDO Client Protocol Factory Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOClientProtocolFactoryDef
	 * @generated
	 */
	public Adapter createCDOClientProtocolFactoryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef <em>CDO Server Protocol Factory Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef
	 * @generated
	 */
	public Adapter createCDOServerProtocolFactoryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.RepositoryProviderDef <em>Repository Provider Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.RepositoryProviderDef
	 * @generated
	 */
	public Adapter createRepositoryProviderDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.PluginRepositoryProviderDef <em>Plugin Repository Provider Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.PluginRepositoryProviderDef
	 * @generated
	 */
	public Adapter createPluginRepositoryProviderDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.cdodefs.CDOResourceDef <em>CDO Resource Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.emf.cdo.cdodefs.CDOResourceDef
	 * @generated
	 */
	public Adapter createCDOResourceDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.net4jutildefs.Def <em>Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.net4j.util.net4jutildefs.Def
	 * @generated
	 */
	public Adapter createDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ProtocolProviderDef <em>Protocol Provider Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.net4j.net4jdefs.ProtocolProviderDef
	 * @generated
	 */
	public Adapter createProtocolProviderDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef <em>Client Protocol Factory Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.net4j.net4jdefs.ClientProtocolFactoryDef
	 * @generated
	 */
	public Adapter createClientProtocolFactoryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef <em>Server Protocol Factory Def</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef
	 * @generated
	 */
	public Adapter createServerProtocolFactoryDefAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //CDODefsAdapterFactory
