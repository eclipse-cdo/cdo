/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.evolution.util.ElementRunnable;
import org.eclipse.emf.cdo.evolution.util.IDAnnotation;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.util.CompletePackageClosure;
import org.eclipse.emf.internal.cdo.util.IPackageClosure;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreEList;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl#getEvolution <em>Evolution</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl#getURI <em>URI</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl#getRootPackage <em>Root Package</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl#getAllPackages <em>All Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl#getReferencedPackages <em>Referenced Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelImpl#getMissingPackages <em>Missing Packages</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModelImpl extends CDOObjectImpl implements Model
{
  /**
   * The default value of the '{@link #getURI() <em>URI</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getURI()
   * @generated
   * @ordered
   */
  protected static final URI URI_EDEFAULT = null;

  private ModelStatus status;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModelImpl()
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
    return EvolutionPackage.Literals.MODEL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Evolution getEvolution()
  {
    return (Evolution)eDynamicGet(EvolutionPackage.MODEL__EVOLUTION, EvolutionPackage.Literals.MODEL__EVOLUTION, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Evolution basicGetEvolution()
  {
    return (Evolution)eDynamicGet(EvolutionPackage.MODEL__EVOLUTION, EvolutionPackage.Literals.MODEL__EVOLUTION, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEvolution(Evolution newEvolution, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newEvolution, EvolutionPackage.MODEL__EVOLUTION, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEvolution(Evolution newEvolution)
  {
    eDynamicSet(EvolutionPackage.MODEL__EVOLUTION, EvolutionPackage.Literals.MODEL__EVOLUTION, newEvolution);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public URI getURI()
  {
    return (URI)eDynamicGet(EvolutionPackage.MODEL__URI, EvolutionPackage.Literals.MODEL__URI, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setURIGen(URI newURI)
  {
    eDynamicSet(EvolutionPackage.MODEL__URI, EvolutionPackage.Literals.MODEL__URI, newURI);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setURI(URI newURI)
  {
    status = null;

    EPackage oldRootPackage = null;
    // EList<EPackage> oldAllPackages = null;
    // EList<EPackage> oldReferencedPackages = null;
    // EList<EPackage> oldMissingPackages = null;

    boolean notificationRequired = eNotificationRequired();
    if (notificationRequired)
    {
      oldRootPackage = getRootPackage();
      // oldAllPackages = getAllPackages();
      // oldReferencedPackages = getReferencedPackages();
      // oldMissingPackages = getMissingPackages();
    }

    setURIGen(newURI);

    if (notificationRequired)
    {
      EPackage rootPackage = getRootPackage();
      if (rootPackage != oldRootPackage)
      {
        eNotify(new ENotificationImpl(this, Notification.SET, EvolutionPackage.Literals.MODEL__ROOT_PACKAGE, oldRootPackage, rootPackage));
      }

      // EList<EPackage> allPackages = getAllPackages();
      // if (!allPackages.equals(oldAllPackages))
      // {
      // eNotify(new ENotificationImpl(this, Notification.SET, EvolutionPackage.Literals.MODEL__ALL_PACKAGES,
      // oldAllPackages, allPackages));
      // }
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage getRootPackage()
  {
    EPackage rootPackage = basicGetRootPackage();
    return rootPackage != null && rootPackage.eIsProxy() ? (EPackage)eResolveProxy((InternalEObject)rootPackage) : rootPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EPackage basicGetRootPackage()
  {
    URI uri = getURI();
    if (uri == null)
    {
      status = ModelStatus.NO_URI;
      return null;
    }

    Resource resource = eResource();
    if (resource == null)
    {
      status = ModelStatus.NO_RESOURCE_SET;
      return null;
    }

    ResourceSet resourceSet = resource.getResourceSet();
    if (resourceSet == null)
    {
      status = ModelStatus.NO_RESOURCE_SET;
      return null;
    }

    if (!resourceSet.getURIConverter().exists(uri, Collections.emptyMap()))
    {
      status = ModelStatus.RESOURCE_NOT_FOUND;
      return null;
    }

    Resource modelResource;

    try
    {
      modelResource = resourceSet.getResource(uri, true);
    }
    catch (Exception ex)
    {
      // ex.printStackTrace();
      modelResource = null;
    }

    if (modelResource == null)
    {
      status = ModelStatus.LOAD_PROBLEM;
      return null;
    }

    if (!modelResource.getErrors().isEmpty())
    {
      status = ModelStatus.LOAD_PROBLEM;
      resourceSet.getResources().remove(modelResource);
      return null;
    }

    EList<EObject> contents = modelResource.getContents();
    if (contents.isEmpty())
    {
      status = ModelStatus.CONTENT_PROBLEM;
      return null;
    }

    EObject firstElement = contents.get(0);
    if (!(firstElement instanceof EPackage))
    {
      status = ModelStatus.CONTENT_PROBLEM;
      return null;
    }

    status = ModelStatus.OK;
    return (EPackage)firstElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EPackage> getAllPackages()
  {
    EPackage rootPackage = getRootPackage();
    if (rootPackage == null)
    {
      return ECollections.emptyEList();
    }

    EcoreEList<EPackage> list = new EcoreEList<EPackage>(EPackage.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.MODEL__ALL_PACKAGES;
      }
    };

    collectAllPackages(rootPackage, list);
    return list;
  }

  static void collectAllPackages(EPackage ePackage, EList<EPackage> list)
  {
    list.add(ePackage);

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      collectAllPackages(subPackage, list);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EPackage> getReferencedPackages()
  {
    EList<EPackage> allPackages = getAllPackages();
    if (allPackages.isEmpty())
    {
      return ECollections.emptyEList();
    }

    EcoreEList<EPackage> list = new EcoreEList<EPackage>(EPackage.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.MODEL__REFERENCED_PACKAGES;
      }
    };

    IPackageClosure closure = new CompletePackageClosure();
    Set<EPackage> referencedPackages = closure.calculate(allPackages);

    for (EPackage referencedPackage : referencedPackages)
    {
      if (!allPackages.contains(referencedPackage))
      {
        list.add(referencedPackage);
      }
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
    EList<EPackage> referencedPackages = getReferencedPackages();
    if (referencedPackages.isEmpty())
    {
      return ECollections.emptyEList();
    }

    Evolution evolution = getEvolution();
    if (evolution == null)
    {
      return ECollections.emptyEList();
    }

    EList<EPackage> allPackages = evolution.getAllPackages();

    EcoreEList<EPackage> list = new EcoreEList<EPackage>(EPackage.class, this)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public int getFeatureID()
      {
        return EvolutionPackage.MODEL__MISSING_PACKAGES;
      }
    };

    for (EPackage referencedPackage : referencedPackages)
    {
      if (referencedPackage == EcorePackage.eINSTANCE && evolution.isUseEcorePackage())
      {
        continue;
      }

      if (referencedPackage == EresourcePackage.eINSTANCE && evolution.isUseEresourcePackage())
      {
        continue;
      }

      if (referencedPackage == EtypesPackage.eINSTANCE && evolution.isUseEtypesPackage())
      {
        continue;
      }

      if (allPackages.contains(referencedPackage))
      {
        continue;
      }

      list.add(referencedPackage);
    }

    return list;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean ensureIDs()
  {
    final boolean[] modified = { false };

    ElementHandler.execute(getRootPackage(), new ElementRunnable()
    {
      public void run(EModelElement modelElement)
      {
        if (IDAnnotation.ensureValue(modelElement) != null)
        {
          modified[0] = true;
        }
      }
    });

    return modified[0];
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EPackage getPackage(String nsURI)
  {
    for (EPackage ePackage : getAllPackages())
    {
      if (ObjectUtil.equals(ePackage.getNsURI(), nsURI))
      {
        return ePackage;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void save()
  {
    try
    {
      eResource().save(null);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EvolutionPackage.MODEL__EVOLUTION:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetEvolution((Evolution)otherEnd, msgs);
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
    case EvolutionPackage.MODEL__EVOLUTION:
      return basicSetEvolution(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case EvolutionPackage.MODEL__EVOLUTION:
      return eInternalContainer().eInverseRemove(this, EvolutionPackage.EVOLUTION__MODELS, Evolution.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case EvolutionPackage.MODEL__EVOLUTION:
      if (resolve)
      {
        return getEvolution();
      }
      return basicGetEvolution();
    case EvolutionPackage.MODEL__URI:
      return getURI();
    case EvolutionPackage.MODEL__ROOT_PACKAGE:
      if (resolve)
      {
        return getRootPackage();
      }
      return basicGetRootPackage();
    case EvolutionPackage.MODEL__ALL_PACKAGES:
      return getAllPackages();
    case EvolutionPackage.MODEL__REFERENCED_PACKAGES:
      return getReferencedPackages();
    case EvolutionPackage.MODEL__MISSING_PACKAGES:
      return getMissingPackages();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case EvolutionPackage.MODEL__EVOLUTION:
      setEvolution((Evolution)newValue);
      return;
    case EvolutionPackage.MODEL__URI:
      setURI((URI)newValue);
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
    case EvolutionPackage.MODEL__EVOLUTION:
      setEvolution((Evolution)null);
      return;
    case EvolutionPackage.MODEL__URI:
      setURI(URI_EDEFAULT);
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
    case EvolutionPackage.MODEL__EVOLUTION:
      return basicGetEvolution() != null;
    case EvolutionPackage.MODEL__URI:
      return URI_EDEFAULT == null ? getURI() != null : !URI_EDEFAULT.equals(getURI());
    case EvolutionPackage.MODEL__ROOT_PACKAGE:
      return basicGetRootPackage() != null;
    case EvolutionPackage.MODEL__ALL_PACKAGES:
      return !getAllPackages().isEmpty();
    case EvolutionPackage.MODEL__REFERENCED_PACKAGES:
      return !getReferencedPackages().isEmpty();
    case EvolutionPackage.MODEL__MISSING_PACKAGES:
      return !getMissingPackages().isEmpty();
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
    case EvolutionPackage.MODEL___ENSURE_IDS:
      return ensureIDs();
    case EvolutionPackage.MODEL___GET_PACKAGE__STRING:
      return getPackage((String)arguments.get(0));
    case EvolutionPackage.MODEL___SAVE:
      save();
      return null;
    }
    return super.eInvoke(operationID, arguments);
  }

  public ModelStatus getStatus()
  {
    return status;
  }

} // ModelImpl
