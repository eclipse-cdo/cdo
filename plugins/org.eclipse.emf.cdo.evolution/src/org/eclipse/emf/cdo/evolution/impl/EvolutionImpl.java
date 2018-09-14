/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.Release;

import org.eclipse.net4j.util.collection.CollectionUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Evolution</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#isUseEcorePackage <em>Use Ecore Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#isUseEresourcePackage <em>Use Eresource Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#isUseEtypesPackage <em>Use Etypes Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#isUniqueNamespaces <em>Unique Namespaces</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getModels <em>Models</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getRootPackages <em>Root Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getAllPackages <em>All Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getMissingPackages <em>Missing Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getReleases <em>Releases</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getOrderedReleases <em>Ordered Releases</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getLatestRelease <em>Latest Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.EvolutionImpl#getNextReleaseVersion <em>Next Release Version</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EvolutionImpl extends ModelSetImpl implements Evolution
{
  /**
   * The default value of the '{@link #isUseEcorePackage() <em>Use Ecore Package</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUseEcorePackage()
   * @generated
   * @ordered
   */
  protected static final boolean USE_ECORE_PACKAGE_EDEFAULT = true;

  /**
   * The default value of the '{@link #isUseEresourcePackage() <em>Use Eresource Package</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUseEresourcePackage()
   * @generated
   * @ordered
   */
  protected static final boolean USE_ERESOURCE_PACKAGE_EDEFAULT = false;

  /**
   * The default value of the '{@link #isUseEtypesPackage() <em>Use Etypes Package</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUseEtypesPackage()
   * @generated
   * @ordered
   */
  protected static final boolean USE_ETYPES_PACKAGE_EDEFAULT = false;

  /**
   * The default value of the '{@link #isUniqueNamespaces() <em>Unique Namespaces</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isUniqueNamespaces()
   * @generated
   * @ordered
   */
  protected static final boolean UNIQUE_NAMESPACES_EDEFAULT = true;

  /**
   * The default value of the '{@link #getNextReleaseVersion() <em>Next Release Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNextReleaseVersion()
   * @generated
   * @ordered
   */
  protected static final int NEXT_RELEASE_VERSION_EDEFAULT = 0;

  private Map<String, Set<EPackage>> releasedPackages;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EvolutionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EvolutionPackage.Literals.EVOLUTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Model> getModels()
  {
    return (EList<Model>)eDynamicGet(EvolutionPackage.EVOLUTION__MODELS, EvolutionPackage.Literals.EVOLUTION__MODELS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isUseEcorePackage()
  {
    return (Boolean)eDynamicGet(EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE, EvolutionPackage.Literals.EVOLUTION__USE_ECORE_PACKAGE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUseEcorePackage(boolean newUseEcorePackage)
  {
    eDynamicSet(EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE, EvolutionPackage.Literals.EVOLUTION__USE_ECORE_PACKAGE, newUseEcorePackage);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isUseEresourcePackage()
  {
    return (Boolean)eDynamicGet(EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE, EvolutionPackage.Literals.EVOLUTION__USE_ERESOURCE_PACKAGE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUseEresourcePackage(boolean newUseEresourcePackage)
  {
    eDynamicSet(EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE, EvolutionPackage.Literals.EVOLUTION__USE_ERESOURCE_PACKAGE, newUseEresourcePackage);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isUseEtypesPackage()
  {
    return (Boolean)eDynamicGet(EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE, EvolutionPackage.Literals.EVOLUTION__USE_ETYPES_PACKAGE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUseEtypesPackage(boolean newUseEtypesPackage)
  {
    eDynamicSet(EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE, EvolutionPackage.Literals.EVOLUTION__USE_ETYPES_PACKAGE, newUseEtypesPackage);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isUniqueNamespaces()
  {
    return (Boolean)eDynamicGet(EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES, EvolutionPackage.Literals.EVOLUTION__UNIQUE_NAMESPACES, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUniqueNamespaces(boolean newUniqueNamespaces)
  {
    eDynamicSet(EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES, EvolutionPackage.Literals.EVOLUTION__UNIQUE_NAMESPACES, newUniqueNamespaces);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<EPackage> getRootPackages()
  {
    EcoreEList<EPackage> list = new EcoreEList<EPackage>(EPackage.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.EVOLUTION__ROOT_PACKAGES;
      }
    };

    for (Model model : getModels())
    {
      EPackage rootPackage = model.getRootPackage();
      if (rootPackage != null)
      {
        list.add(rootPackage);
      }
    }

    return list;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<EPackage> getAllPackages()
  {
    EcoreEList<EPackage> list = new EcoreEList<EPackage>(EPackage.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.EVOLUTION__ALL_PACKAGES;
      }
    };

    for (Model model : getModels())
    {
      list.addAll(model.getAllPackages());
    }

    return list;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EPackage> getMissingPackages()
  {
    EcoreEList<EPackage> list = new EcoreEList<EPackage>(EPackage.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.EVOLUTION__MISSING_PACKAGES;
      }
    };

    Set<EPackage> set = new HashSet<EPackage>();
    for (Model model : getModels())
    {
      set.addAll(model.getMissingPackages());
    }

    list.addAll(set);
    return list;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Release getRelease(int version)
  {
    for (Release release : getReleases())
    {
      if (release.getVersion() == version)
      {
        return release;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Release> getReleases()
  {
    return (EList<Release>)eDynamicGet(EvolutionPackage.EVOLUTION__RELEASES, EvolutionPackage.Literals.EVOLUTION__RELEASES, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Release> getOrderedReleases()
  {
    EcoreEList<Release> list = new EcoreEList<Release>(Release.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.EVOLUTION__ORDERED_RELEASES;
      }
    };

    list.addAll(getReleases());
    ECollections.sort(list);
    return list;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Release getLatestRelease()
  {
    Release latestRelease = basicGetLatestRelease();
    return latestRelease != null && ((EObject)latestRelease).eIsProxy() ? (Release)eResolveProxy((InternalEObject)latestRelease) : latestRelease;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Release basicGetLatestRelease()
  {
    EList<Release> orderedReleases = getOrderedReleases();
    if (orderedReleases.isEmpty())
    {
      return null;
    }

    return orderedReleases.get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public int getNextReleaseVersion()
  {
    Release latestRelease = getLatestRelease();
    return latestRelease == null ? 1 : latestRelease.getVersion() + 1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EvolutionPackage.EVOLUTION__MODELS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getModels()).basicAdd(otherEnd, msgs);
    case EvolutionPackage.EVOLUTION__RELEASES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getReleases()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EvolutionPackage.EVOLUTION__MODELS:
      return ((InternalEList<?>)getModels()).basicRemove(otherEnd, msgs);
    case EvolutionPackage.EVOLUTION__RELEASES:
      return ((InternalEList<?>)getReleases()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE:
      return isUseEcorePackage();
    case EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE:
      return isUseEresourcePackage();
    case EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE:
      return isUseEtypesPackage();
    case EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES:
      return isUniqueNamespaces();
    case EvolutionPackage.EVOLUTION__MODELS:
      return getModels();
    case EvolutionPackage.EVOLUTION__ROOT_PACKAGES:
      return getRootPackages();
    case EvolutionPackage.EVOLUTION__ALL_PACKAGES:
      return getAllPackages();
    case EvolutionPackage.EVOLUTION__MISSING_PACKAGES:
      return getMissingPackages();
    case EvolutionPackage.EVOLUTION__RELEASES:
      return getReleases();
    case EvolutionPackage.EVOLUTION__ORDERED_RELEASES:
      return getOrderedReleases();
    case EvolutionPackage.EVOLUTION__LATEST_RELEASE:
      if (resolve)
      {
        return getLatestRelease();
      }
      return basicGetLatestRelease();
    case EvolutionPackage.EVOLUTION__NEXT_RELEASE_VERSION:
      return getNextReleaseVersion();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE:
      setUseEcorePackage((Boolean)newValue);
      return;
    case EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE:
      setUseEresourcePackage((Boolean)newValue);
      return;
    case EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE:
      setUseEtypesPackage((Boolean)newValue);
      return;
    case EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES:
      setUniqueNamespaces((Boolean)newValue);
      return;
    case EvolutionPackage.EVOLUTION__MODELS:
      getModels().clear();
      getModels().addAll((Collection<? extends Model>)newValue);
      return;
    case EvolutionPackage.EVOLUTION__RELEASES:
      getReleases().clear();
      getReleases().addAll((Collection<? extends Release>)newValue);
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
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE:
      setUseEcorePackage(USE_ECORE_PACKAGE_EDEFAULT);
      return;
    case EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE:
      setUseEresourcePackage(USE_ERESOURCE_PACKAGE_EDEFAULT);
      return;
    case EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE:
      setUseEtypesPackage(USE_ETYPES_PACKAGE_EDEFAULT);
      return;
    case EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES:
      setUniqueNamespaces(UNIQUE_NAMESPACES_EDEFAULT);
      return;
    case EvolutionPackage.EVOLUTION__MODELS:
      getModels().clear();
      return;
    case EvolutionPackage.EVOLUTION__RELEASES:
      getReleases().clear();
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
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE:
      return isUseEcorePackage() != USE_ECORE_PACKAGE_EDEFAULT;
    case EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE:
      return isUseEresourcePackage() != USE_ERESOURCE_PACKAGE_EDEFAULT;
    case EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE:
      return isUseEtypesPackage() != USE_ETYPES_PACKAGE_EDEFAULT;
    case EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES:
      return isUniqueNamespaces() != UNIQUE_NAMESPACES_EDEFAULT;
    case EvolutionPackage.EVOLUTION__MODELS:
      return !getModels().isEmpty();
    case EvolutionPackage.EVOLUTION__ROOT_PACKAGES:
      return !getRootPackages().isEmpty();
    case EvolutionPackage.EVOLUTION__ALL_PACKAGES:
      return !getAllPackages().isEmpty();
    case EvolutionPackage.EVOLUTION__MISSING_PACKAGES:
      return !getMissingPackages().isEmpty();
    case EvolutionPackage.EVOLUTION__RELEASES:
      return !getReleases().isEmpty();
    case EvolutionPackage.EVOLUTION__ORDERED_RELEASES:
      return !getOrderedReleases().isEmpty();
    case EvolutionPackage.EVOLUTION__LATEST_RELEASE:
      return basicGetLatestRelease() != null;
    case EvolutionPackage.EVOLUTION__NEXT_RELEASE_VERSION:
      return getNextReleaseVersion() != NEXT_RELEASE_VERSION_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case EvolutionPackage.EVOLUTION___GET_RELEASE__INT:
      return getRelease((Integer)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public Evolution getEvolution()
  {
    return this;
  }

  @Override
  public int getVersion()
  {
    return Integer.MAX_VALUE;
  }

  @Override
  public Release getPreviousRelease()
  {
    return getLatestRelease();
  }

  @Override
  public boolean containsElement(EModelElement modelElement)
  {
    for (EPackage ePackage : getRootPackages())
    {
      if (EcoreUtil.isAncestor(ePackage, modelElement))
      {
        return true;
      }
    }

    return false;
  }

  public Map<String, Set<EPackage>> getReleasedPackages()
  {
    if (releasedPackages == null)
    {
      releasedPackages = new HashMap<String, Set<EPackage>>();

      for (Release release : getReleases())
      {
        for (EPackage ePackage : release.getAllPackages())
        {
          CollectionUtil.add(releasedPackages, ePackage.getNsURI(), ePackage);
        }
      }
    }

    return releasedPackages;
  }

  public static Evolution get(Notifier notifier)
  {
    if (notifier instanceof ResourceSet)
    {
      EList<Resource> resources = ((ResourceSet)notifier).getResources();
      if (resources.isEmpty())
      {
        return null;
      }

      notifier = resources.get(0);
    }

    if (notifier instanceof Resource)
    {
      EList<EObject> contents = ((Resource)notifier).getContents();
      if (contents.isEmpty())
      {
        return null;
      }

      notifier = contents.get(0);
    }

    if (notifier instanceof Evolution)
    {
      return (Evolution)notifier;
    }

    if (notifier instanceof EObject)
    {
      Resource resource = ((EObject)notifier).eResource();
      if (resource != null)
      {
        return get(resource.getResourceSet());
      }
    }

    return null;
  }

} // EvolutionImpl
