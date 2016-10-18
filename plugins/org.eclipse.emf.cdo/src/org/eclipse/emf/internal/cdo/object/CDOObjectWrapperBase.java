/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus - Fix failure to attach CDOLegacyAdapter to EGenericTypes (bug 403681)
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class CDOObjectWrapperBase implements CDOObject, InternalEObject
{
  private static final Method E_SET_DIRECT_RESOURCE = getMethod("eSetDirectResource", Resource.Internal.class);

  private static final Method E_BASIC_SET_CONTAINER = getMethod("eBasicSetContainer", InternalEObject.class, int.class);

  protected InternalEObject instance;

  public CDOObjectWrapperBase()
  {
  }

  public CDOResourceImpl cdoResource()
  {
    Resource resource = eResource();
    if (resource instanceof CDOResourceImpl)
    {
      return (CDOResourceImpl)resource;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public CDOResourceImpl cdoDirectResource()
  {
    Resource.Internal resource = eDirectResource();
    if (resource instanceof CDOResourceImpl)
    {
      return (CDOResourceImpl)resource;
    }

    return null;
  }

  public InternalEObject cdoInternalInstance()
  {
    return instance;
  }

  /**
   * @since 2.0
   */
  public boolean cdoConflict()
  {
    return FSMUtil.isConflict(this);
  }

  /**
   * @since 2.0
   */
  public boolean cdoInvalid()
  {
    return FSMUtil.isInvalid(this);
  }

  /**
   * @since 3.0
   */
  public void cdoPrefetch(int depth)
  {
    InternalCDOView view = (InternalCDOView)cdoView();
    view.prefetchRevisions(cdoID(), depth);
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    return eDynamicFeature(dynamicFeatureID);
  }

  /**
   * @since 3.0
   */
  protected EStructuralFeature eDynamicFeature(int dynamicFeatureID)
  {
    EClass eClass = eClass();
    return eClass.getEStructuralFeature(dynamicFeatureID + eStaticFeatureCount());
  }

  /**
   * @since 3.0
   */
  protected int eStaticFeatureCount()
  {
    return eStaticClass().getFeatureCount();
  }

  /**
   * @since 3.0
   */
  protected final EClass eStaticClass()
  {
    return EcorePackage.eINSTANCE.getEObject();
  }

  public Resource.Internal getInstanceResource(InternalEObject instance)
  {
    return instance.eDirectResource();
  }

  public InternalEObject getInstanceContainer(InternalEObject instance)
  {
    return instance.eInternalContainer();
  }

  public int getInstanceContainerFeatureID(InternalEObject instance)
  {
    return instance.eContainerFeatureID();
  }

  public Object getInstanceValue(InternalEObject instance, EStructuralFeature feature)
  {
    return instance.eGet(feature);
  }

  public boolean isSetInstanceValue(InternalEObject instance, EStructuralFeature feature)
  {
    // Single-valued features that need special handling
    if (feature == EMFUtil.ETYPED_ELEMENT_EGENERIC_TYPE || feature == EMFUtil.ECLASSIFIER_INSTANCE_TYPE_NAME)
    {
      return getInstanceValue(instance, feature) != null;
    }

    // Many-valued features that need special handling
    if (feature == EMFUtil.ECLASS_EGENERIC_SUPER_TYPES || feature == EMFUtil.EOPERATION_EGENERIC_EXCEPTIONS)
    {
      return !((List<?>)getInstanceValue(instance, feature)).isEmpty();
    }

    return instance.eIsSet(feature);
  }

  public void setInstanceResource(Resource.Internal resource)
  {
    try
    {
      E_SET_DIRECT_RESOURCE.invoke(instance, resource);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public void setInstanceContainer(InternalEObject container, int containerFeatureID)
  {
    try
    {
      E_BASIC_SET_CONTAINER.invoke(instance, container, containerFeatureID);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public void setInstanceValue(InternalEObject instance, EStructuralFeature feature, Object value)
  {
    instance.eSet(feature, value);
  }

  public EList<Adapter> eAdapters()
  {
    return instance.eAdapters();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return instance.eAllContents();
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return instance.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return instance.eBasicRemoveFromContainer(notifications);
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID, NotificationChain notifications)
  {
    return instance.eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  public EClass eClass()
  {
    return instance.eClass();
  }

  public EObject eContainer()
  {
    return instance.eContainer();
  }

  public int eContainerFeatureID()
  {
    return instance.eContainerFeatureID();
  }

  public EStructuralFeature eContainingFeature()
  {
    return instance.eContainingFeature();
  }

  public EReference eContainmentFeature()
  {
    return instance.eContainmentFeature();
  }

  public EList<EObject> eContents()
  {
    return instance.eContents();
  }

  public EList<EObject> eCrossReferences()
  {
    return instance.eCrossReferences();
  }

  public boolean eDeliver()
  {
    return instance.eDeliver();
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return instance.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  public Resource.Internal eDirectResource()
  {
    return instance.eDirectResource();
  }

  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return instance.eGet(feature, resolve, coreType);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return instance.eGet(feature, resolve);
  }

  public Object eGet(EStructuralFeature feature)
  {
    return instance.eGet(feature);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return instance.eGet(featureID, resolve, coreType);
  }

  /**
   * @since 3.0
   */
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass)
  {
    // Note: This causes a compiler error with EMF < 2.6M4!!! Ignore it or update your target platform.
    return instance.eDerivedOperationID(baseOperationID, baseClass);
  }

  /**
   * @since 3.0
   */
  public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException
  {
    // Note: This causes a compiler error with EMF < 2.6M4!!! Ignore it or update your target platform.
    return instance.eInvoke(operation, arguments);
  }

  /**
   * @since 3.0
   */
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    // Note: This causes a compiler error with EMF < 2.6M4!!! Ignore it or update your target platform.
    return instance.eInvoke(operationID, arguments);
  }

  public InternalEObject eInternalContainer()
  {
    return instance.eInternalContainer();
  }

  public Resource.Internal eInternalResource()
  {
    CDOView view = cdoView();
    if (view != null && view.isClosed())
    {
      return null;
    }

    return instance.eInternalResource();
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain notifications)
  {
    return instance.eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain notifications)
  {
    return instance.eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  public boolean eIsProxy()
  {
    return instance.eIsProxy();
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return isSetInstanceValue(instance, feature);
  }

  public boolean eIsSet(int featureID)
  {
    // Features that need special handling
    if (featureID == EcorePackage.ETYPED_ELEMENT__EGENERIC_TYPE || featureID == EcorePackage.ECLASSIFIER__INSTANCE_TYPE_NAME
        || featureID == EcorePackage.ECLASS__EGENERIC_SUPER_TYPES || featureID == EcorePackage.EOPERATION__EGENERIC_EXCEPTIONS)
    {
      return isSetInstanceValue(instance, instance.eClass().getEStructuralFeature(featureID));
    }

    return instance.eIsSet(featureID);
  }

  public boolean eNotificationRequired()
  {
    return instance.eNotificationRequired();
  }

  public void eNotify(Notification notification)
  {
    instance.eNotify(notification);
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return instance.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  public URI eProxyURI()
  {
    return instance.eProxyURI();
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    return instance.eResolveProxy(proxy);
  }

  public Resource eResource()
  {
    return instance.eResource();
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    instance.eSet(feature, newValue);
  }

  public void eSet(int featureID, Object newValue)
  {
    instance.eSet(featureID, newValue);
  }

  public void eSetClass(EClass class1)
  {
    instance.eSetClass(class1);
  }

  public void eSetDeliver(boolean deliver)
  {
    instance.eSetDeliver(deliver);
  }

  public void eSetProxyURI(URI uri)
  {
    instance.eSetProxyURI(uri);
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return instance.eSetResource(resource, notifications);
  }

  public void eSetStore(EStore store)
  {
    instance.eSetStore(store);
  }

  public Setting eSetting(EStructuralFeature feature)
  {
    return instance.eSetting(feature);
  }

  public EStore eStore()
  {
    return instance.eStore();
  }

  public void eUnset(EStructuralFeature feature)
  {
    instance.eUnset(feature);
  }

  public void eUnset(int featureID)
  {
    instance.eUnset(featureID);
  }

  public String eURIFragmentSegment(EStructuralFeature feature, EObject object)
  {
    return instance.eURIFragmentSegment(feature, object);
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == this || obj == instance || //
        obj instanceof CDOObjectWrapperBase && ((CDOObjectWrapperBase)obj).instance == instance;
  }

  @Override
  public int hashCode()
  {
    if (instance != null)
    {
      return instance.hashCode();
    }

    return super.hashCode();
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + instance.getClass().getSimpleName() + "@" + cdoID() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  private static Method getMethod(String methodName, Class<?>... parameterTypes)
  {
    Method method = null;

    try
    {
      method = BasicEObjectImpl.class.getDeclaredMethod(methodName, parameterTypes);
      method.setAccessible(true);
    }
    catch (Throwable ex)
    {
      method = null;
    }

    return method;
  }
}
