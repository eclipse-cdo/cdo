/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOPackageRegistryDefImpl.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.cdodefs.EPackageDef;

import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>CDO Package Registry Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.impl.CDOPackageRegistryDefImpl#getPackages <em>Packages</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CDOPackageRegistryDefImpl extends DefImpl implements CDOPackageRegistryDef
{

  /**
   * The cached value of the '{@link #getPackages() <em>Packages</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   */
  protected EList<EPackageDef> packages;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @generated
   */
  protected CDOPackageRegistryDefImpl()
  {
		super();
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
		return CDODefsPackage.Literals.CDO_PACKAGE_REGISTRY_DEF;
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @return the packages
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<EPackageDef> getPackages()
  {
		if (packages == null) {
			packages = new EObjectContainmentEList<EPackageDef>(EPackageDef.class, this, CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF__PACKAGES);
		}
		return packages;
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature id
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
		switch (featureID) {
			case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF__PACKAGES:
				return ((InternalEList<?>)getPackages()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
		switch (featureID) {
			case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF__PACKAGES:
				return getPackages();
		}
		return super.eGet(featureID, resolve, coreType);
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @param newValue
   *          the new value
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
		switch (featureID) {
			case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF__PACKAGES:
				getPackages().clear();
				getPackages().addAll((Collection<? extends EPackageDef>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
		switch (featureID) {
			case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF__PACKAGES:
				getPackages().clear();
				return;
		}
		super.eUnset(featureID);
	}

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   * 
   * @param featureID
   *          the feature id
   * @return true, if e is set
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
		switch (featureID) {
			case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF__PACKAGES:
				return packages != null && !packages.isEmpty();
		}
		return super.eIsSet(featureID);
	}

  /**
   * Creates the instance.
   * 
   * @return the CDO package registry
   */
  protected Object createInstance()
  {
    return addPackages(new EPackageRegistryImpl());
  }

  /**
   * Adds the packages.
   * 
   * @param packageRegistry
   *          the package registry
   * @return the e package. registry
   */
  protected EPackage.Registry addPackages(EPackage.Registry packageRegistry)
  {
    for (EPackageDef ePackageDef : getPackages())
    {
      addPackage(ePackageDef, packageRegistry);
    }
    return packageRegistry;
  }

  /**
   * Adds the given {@link EPackage} within the supplied <tt>definition<tt> to the given <tt>CDOPackageRegistry<tt> .
   * 
   * @param ePackageDef
   *          the ePackage definition
   * @param packageRegistry
   *          the package registry
   */
  protected void addPackage(EPackageDef ePackageDef, EPackage.Registry packageRegistry)
  {
    EPackage ePackage = (EPackage)ePackageDef.getInstance();
    packageRegistry.put(ePackage.getNsURI(), ePackage);
  }

  /**
   * Adds the given {@link EPackage} within the supplied <tt>definition<tt> to the given <tt>CDOPackageRegistry<tt> .
   * 
   * @param ePackageDef
   *          the ePackage definition
   * @param packageRegistry
   *          the package registry
   */
  protected void removePackage(EPackageDef ePackageDef, EPackage.Registry packageRegistry)
  {
    EPackage ePackage = (EPackage)ePackageDef.getInstance();
    packageRegistry.remove(ePackage.getNsURI());
  }

} // CDOPackageRegistryDefImpl
