/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.evolution.util.ElementRunnable;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Release</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getEvolution <em>Evolution</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getDate <em>Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getNextRelease <em>Next Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getPreviousRelease <em>Previous Release</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getRootPackages <em>Root Packages</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ReleaseImpl#getAllPackages <em>All Packages</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReleaseImpl extends ModelSetImpl implements Release
{
  /**
   * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected static final int VERSION_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getDate() <em>Date</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDate()
   * @generated
   * @ordered
   */
  protected static final Date DATE_EDEFAULT = null;

  private Map<String, EModelElement> idCache;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ReleaseImpl()
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
    return EvolutionPackage.Literals.RELEASE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Date getDate()
  {
    return (Date)eDynamicGet(EvolutionPackage.RELEASE__DATE, EvolutionPackage.Literals.RELEASE__DATE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDate(Date newDate)
  {
    eDynamicSet(EvolutionPackage.RELEASE__DATE, EvolutionPackage.Literals.RELEASE__DATE, newDate);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Release getNextRelease()
  {
    Release nextRelease = basicGetNextRelease();
    return nextRelease != null && ((EObject)nextRelease).eIsProxy() ? (Release)eResolveProxy((InternalEObject)nextRelease) : nextRelease;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Release basicGetNextRelease()
  {
    int nextVersion = getVersion() + 1;
    return getEvolution().getRelease(nextVersion);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Release getPreviousRelease()
  {
    Release previousRelease = basicGetPreviousRelease();
    return previousRelease != null && ((EObject)previousRelease).eIsProxy() ? (Release)eResolveProxy((InternalEObject)previousRelease) : previousRelease;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Release basicGetPreviousRelease()
  {
    int previousVersion = getVersion() - 1;
    if (previousVersion < 1)
    {
      return null;
    }

    return getEvolution().getRelease(previousVersion);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getVersion()
  {
    return (Integer)eDynamicGet(EvolutionPackage.RELEASE__VERSION, EvolutionPackage.Literals.RELEASE__VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setVersion(int newVersion)
  {
    eDynamicSet(EvolutionPackage.RELEASE__VERSION, EvolutionPackage.Literals.RELEASE__VERSION, newVersion);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EPackage> getRootPackages()
  {
    return (EList<EPackage>)eDynamicGet(EvolutionPackage.RELEASE__ROOT_PACKAGES, EvolutionPackage.Literals.RELEASE__ROOT_PACKAGES, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Evolution getEvolution()
  {
    return (Evolution)eDynamicGet(EvolutionPackage.RELEASE__EVOLUTION, EvolutionPackage.Literals.RELEASE__EVOLUTION, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetEvolution(Evolution newEvolution, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newEvolution, EvolutionPackage.RELEASE__EVOLUTION, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEvolution(Evolution newEvolution)
  {
    eDynamicSet(EvolutionPackage.RELEASE__EVOLUTION, EvolutionPackage.Literals.RELEASE__EVOLUTION, newEvolution);
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
        return EvolutionPackage.MODEL__ALL_PACKAGES;
      }
    };

    for (EPackage rootPackage : getRootPackages())
    {
      ModelImpl.collectAllPackages(rootPackage, list);
    }

    return list;
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
    case EvolutionPackage.RELEASE__EVOLUTION:
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
    case EvolutionPackage.RELEASE__EVOLUTION:
      return basicSetEvolution(null, msgs);
    case EvolutionPackage.RELEASE__ROOT_PACKAGES:
      return ((InternalEList<?>)getRootPackages()).basicRemove(otherEnd, msgs);
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
    case EvolutionPackage.RELEASE__EVOLUTION:
      return eInternalContainer().eInverseRemove(this, EvolutionPackage.EVOLUTION__RELEASES, Evolution.class, msgs);
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
    case EvolutionPackage.RELEASE__EVOLUTION:
      return getEvolution();
    case EvolutionPackage.RELEASE__VERSION:
      return getVersion();
    case EvolutionPackage.RELEASE__DATE:
      return getDate();
    case EvolutionPackage.RELEASE__NEXT_RELEASE:
      if (resolve)
      {
        return getNextRelease();
      }
      return basicGetNextRelease();
    case EvolutionPackage.RELEASE__PREVIOUS_RELEASE:
      if (resolve)
      {
        return getPreviousRelease();
      }
      return basicGetPreviousRelease();
    case EvolutionPackage.RELEASE__ROOT_PACKAGES:
      return getRootPackages();
    case EvolutionPackage.RELEASE__ALL_PACKAGES:
      return getAllPackages();
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
    case EvolutionPackage.RELEASE__EVOLUTION:
      setEvolution((Evolution)newValue);
      return;
    case EvolutionPackage.RELEASE__VERSION:
      setVersion((Integer)newValue);
      return;
    case EvolutionPackage.RELEASE__DATE:
      setDate((Date)newValue);
      return;
    case EvolutionPackage.RELEASE__ROOT_PACKAGES:
      getRootPackages().clear();
      getRootPackages().addAll((Collection<? extends EPackage>)newValue);
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
    case EvolutionPackage.RELEASE__EVOLUTION:
      setEvolution((Evolution)null);
      return;
    case EvolutionPackage.RELEASE__VERSION:
      setVersion(VERSION_EDEFAULT);
      return;
    case EvolutionPackage.RELEASE__DATE:
      setDate(DATE_EDEFAULT);
      return;
    case EvolutionPackage.RELEASE__ROOT_PACKAGES:
      getRootPackages().clear();
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
    case EvolutionPackage.RELEASE__EVOLUTION:
      return getEvolution() != null;
    case EvolutionPackage.RELEASE__VERSION:
      return getVersion() != VERSION_EDEFAULT;
    case EvolutionPackage.RELEASE__DATE:
      return DATE_EDEFAULT == null ? getDate() != null : !DATE_EDEFAULT.equals(getDate());
    case EvolutionPackage.RELEASE__NEXT_RELEASE:
      return basicGetNextRelease() != null;
    case EvolutionPackage.RELEASE__PREVIOUS_RELEASE:
      return basicGetPreviousRelease() != null;
    case EvolutionPackage.RELEASE__ROOT_PACKAGES:
      return !getRootPackages().isEmpty();
    case EvolutionPackage.RELEASE__ALL_PACKAGES:
      return !getAllPackages().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  public int compareTo(Release o)
  {
    return o.getVersion() - getVersion();
  }

  @Override
  public void invalidateChange()
  {
    // Do nothing.
  }

  @Override
  public boolean containsElement(EModelElement modelElement)
  {
    return EcoreUtil.isAncestor(this, modelElement);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends EModelElement> T getElement(String id)
  {
    if (idCache == null)
    {
      idCache = new HashMap<String, EModelElement>();

      ElementHandler.execute(getRootPackages(), new ElementRunnable()
      {
        public void run(EModelElement modelElement)
        {
          String id = getElementID(modelElement);
          idCache.put(id, modelElement);
        }
      });
    }

    return (T)idCache.get(id);
  }

} // ReleaseImpl
