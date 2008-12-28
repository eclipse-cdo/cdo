/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDODefsSwitch.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage
 * @generated
 */
public class CDODefsSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CDODefsPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CDODefsSwitch() {
		if (modelPackage == null) {
			modelPackage = CDODefsPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case CDODefsPackage.CDO_VIEW_DEF: {
				CDOViewDef cdoViewDef = (CDOViewDef)theEObject;
				T result = caseCDOViewDef(cdoViewDef);
				if (result == null) result = caseDef(cdoViewDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_TRANSACTION_DEF: {
				CDOTransactionDef cdoTransactionDef = (CDOTransactionDef)theEObject;
				T result = caseCDOTransactionDef(cdoTransactionDef);
				if (result == null) result = caseCDOViewDef(cdoTransactionDef);
				if (result == null) result = caseDef(cdoTransactionDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_AUDIT_DEF: {
				CDOAuditDef cdoAuditDef = (CDOAuditDef)theEObject;
				T result = caseCDOAuditDef(cdoAuditDef);
				if (result == null) result = caseCDOViewDef(cdoAuditDef);
				if (result == null) result = caseDef(cdoAuditDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_SESSION_DEF: {
				CDOSessionDef cdoSessionDef = (CDOSessionDef)theEObject;
				T result = caseCDOSessionDef(cdoSessionDef);
				if (result == null) result = caseDef(cdoSessionDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.FAIL_OVER_STRATEGY_DEF: {
				FailOverStrategyDef failOverStrategyDef = (FailOverStrategyDef)theEObject;
				T result = caseFailOverStrategyDef(failOverStrategyDef);
				if (result == null) result = caseDef(failOverStrategyDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.RETRY_FAIL_OVER_STRATEGY_DEF: {
				RetryFailOverStrategyDef retryFailOverStrategyDef = (RetryFailOverStrategyDef)theEObject;
				T result = caseRetryFailOverStrategyDef(retryFailOverStrategyDef);
				if (result == null) result = caseFailOverStrategyDef(retryFailOverStrategyDef);
				if (result == null) result = caseDef(retryFailOverStrategyDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF: {
				CDOPackageRegistryDef cdoPackageRegistryDef = (CDOPackageRegistryDef)theEObject;
				T result = caseCDOPackageRegistryDef(cdoPackageRegistryDef);
				if (result == null) result = caseDef(cdoPackageRegistryDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_EAGER_PACKAGE_REGISTRY_DEF: {
				CDOEagerPackageRegistryDef cdoEagerPackageRegistryDef = (CDOEagerPackageRegistryDef)theEObject;
				T result = caseCDOEagerPackageRegistryDef(cdoEagerPackageRegistryDef);
				if (result == null) result = caseCDOPackageRegistryDef(cdoEagerPackageRegistryDef);
				if (result == null) result = caseDef(cdoEagerPackageRegistryDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_LAZY_PACKAGE_REGISTRY_DEF: {
				CDOLazyPackageRegistryDef cdoLazyPackageRegistryDef = (CDOLazyPackageRegistryDef)theEObject;
				T result = caseCDOLazyPackageRegistryDef(cdoLazyPackageRegistryDef);
				if (result == null) result = caseCDOPackageRegistryDef(cdoLazyPackageRegistryDef);
				if (result == null) result = caseDef(cdoLazyPackageRegistryDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.EPACKAGE_DEF: {
				EPackageDef ePackageDef = (EPackageDef)theEObject;
				T result = caseEPackageDef(ePackageDef);
				if (result == null) result = caseDef(ePackageDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.EDYNAMIC_PACKAGE_DEF: {
				EDynamicPackageDef eDynamicPackageDef = (EDynamicPackageDef)theEObject;
				T result = caseEDynamicPackageDef(eDynamicPackageDef);
				if (result == null) result = caseEPackageDef(eDynamicPackageDef);
				if (result == null) result = caseDef(eDynamicPackageDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.EGLOBAL_PACKAGE_DEF: {
				EGlobalPackageDef eGlobalPackageDef = (EGlobalPackageDef)theEObject;
				T result = caseEGlobalPackageDef(eGlobalPackageDef);
				if (result == null) result = caseEPackageDef(eGlobalPackageDef);
				if (result == null) result = caseDef(eGlobalPackageDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_CLIENT_PROTOCOL_FACTORY_DEF: {
				CDOClientProtocolFactoryDef cdoClientProtocolFactoryDef = (CDOClientProtocolFactoryDef)theEObject;
				T result = caseCDOClientProtocolFactoryDef(cdoClientProtocolFactoryDef);
				if (result == null) result = caseClientProtocolFactoryDef(cdoClientProtocolFactoryDef);
				if (result == null) result = caseProtocolProviderDef(cdoClientProtocolFactoryDef);
				if (result == null) result = caseDef(cdoClientProtocolFactoryDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_SERVER_PROTOCOL_FACTORY_DEF: {
				CDOServerProtocolFactoryDef cdoServerProtocolFactoryDef = (CDOServerProtocolFactoryDef)theEObject;
				T result = caseCDOServerProtocolFactoryDef(cdoServerProtocolFactoryDef);
				if (result == null) result = caseServerProtocolFactoryDef(cdoServerProtocolFactoryDef);
				if (result == null) result = caseProtocolProviderDef(cdoServerProtocolFactoryDef);
				if (result == null) result = caseDef(cdoServerProtocolFactoryDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.REPOSITORY_PROVIDER_DEF: {
				RepositoryProviderDef repositoryProviderDef = (RepositoryProviderDef)theEObject;
				T result = caseRepositoryProviderDef(repositoryProviderDef);
				if (result == null) result = caseDef(repositoryProviderDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.PLUGIN_REPOSITORY_PROVIDER_DEF: {
				PluginRepositoryProviderDef pluginRepositoryProviderDef = (PluginRepositoryProviderDef)theEObject;
				T result = casePluginRepositoryProviderDef(pluginRepositoryProviderDef);
				if (result == null) result = caseRepositoryProviderDef(pluginRepositoryProviderDef);
				if (result == null) result = caseDef(pluginRepositoryProviderDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CDODefsPackage.CDO_RESOURCE_DEF: {
				CDOResourceDef cdoResourceDef = (CDOResourceDef)theEObject;
				T result = caseCDOResourceDef(cdoResourceDef);
				if (result == null) result = caseDef(cdoResourceDef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO View Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO View Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOViewDef(CDOViewDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Transaction Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Transaction Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOTransactionDef(CDOTransactionDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Audit Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Audit Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOAuditDef(CDOAuditDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Session Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Session Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOSessionDef(CDOSessionDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Fail Over Strategy Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Fail Over Strategy Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFailOverStrategyDef(FailOverStrategyDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Retry Fail Over Strategy Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Retry Fail Over Strategy Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRetryFailOverStrategyDef(RetryFailOverStrategyDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Package Registry Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Package Registry Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOPackageRegistryDef(CDOPackageRegistryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Eager Package Registry Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Eager Package Registry Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOEagerPackageRegistryDef(CDOEagerPackageRegistryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Lazy Package Registry Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Lazy Package Registry Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOLazyPackageRegistryDef(CDOLazyPackageRegistryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EPackage Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EPackage Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEPackageDef(EPackageDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EDynamic Package Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EDynamic Package Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEDynamicPackageDef(EDynamicPackageDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EGlobal Package Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EGlobal Package Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEGlobalPackageDef(EGlobalPackageDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Client Protocol Factory Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Client Protocol Factory Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOClientProtocolFactoryDef(CDOClientProtocolFactoryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Server Protocol Factory Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Server Protocol Factory Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOServerProtocolFactoryDef(CDOServerProtocolFactoryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Repository Provider Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Repository Provider Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRepositoryProviderDef(RepositoryProviderDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plugin Repository Provider Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plugin Repository Provider Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePluginRepositoryProviderDef(PluginRepositoryProviderDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>CDO Resource Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>CDO Resource Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCDOResourceDef(CDOResourceDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDef(Def object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Protocol Provider Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Protocol Provider Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProtocolProviderDef(ProtocolProviderDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Client Protocol Factory Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Client Protocol Factory Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseClientProtocolFactoryDef(ClientProtocolFactoryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Server Protocol Factory Def</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Server Protocol Factory Def</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServerProtocolFactoryDef(ServerProtocolFactoryDef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //CDODefsSwitch
