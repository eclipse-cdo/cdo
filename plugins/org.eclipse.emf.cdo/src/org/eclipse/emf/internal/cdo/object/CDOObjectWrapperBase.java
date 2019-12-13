/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

  @Override
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
  @Override
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
  @Override
  public boolean cdoConflict()
  {
    return FSMUtil.isConflict(this);
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean cdoInvalid()
  {
    return FSMUtil.isInvalid(this);
  }

  /**
   * @since 3.0
   */
  @Override
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

  @Override
  public EList<Adapter> eAdapters()
  {
    return instance.eAdapters();
  }

  @Override
  public TreeIterator<EObject> eAllContents()
  {
    return instance.eAllContents();
  }

  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return instance.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  @Override
  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return instance.eBasicRemoveFromContainer(notifications);
  }

  @Override
  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID, NotificationChain notifications)
  {
    return instance.eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  @Override
  public EClass eClass()
  {
    return instance.eClass();
  }

  @Override
  public EObject eContainer()
  {
    return instance.eContainer();
  }

  @Override
  public int eContainerFeatureID()
  {
    return instance.eContainerFeatureID();
  }

  @Override
  public EStructuralFeature eContainingFeature()
  {
    return instance.eContainingFeature();
  }

  @Override
  public EReference eContainmentFeature()
  {
    return instance.eContainmentFeature();
  }

  @Override
  public EList<EObject> eContents()
  {
    return instance.eContents();
  }

  @Override
  public EList<EObject> eCrossReferences()
  {
    return instance.eCrossReferences();
  }

  @Override
  public boolean eDeliver()
  {
    return instance.eDeliver();
  }

  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return instance.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  @Override
  public Resource.Internal eDirectResource()
  {
    return instance.eDirectResource();
  }

  @Override
  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return instance.eGet(feature, resolve, coreType);
  }

  @Override
  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return instance.eGet(feature, resolve);
  }

  @Override
  public Object eGet(EStructuralFeature feature)
  {
    return instance.eGet(feature);
  }

  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return instance.eGet(featureID, resolve, coreType);
  }

  /**
   * @since 3.0
   */
  @Override
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass)
  {
    // Note: This causes a compiler error with EMF < 2.6M4!!! Ignore it or update your target platform.
    return instance.eDerivedOperationID(baseOperationID, baseClass);
  }

  /**
   * @since 3.0
   */
  @Override
  public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException
  {
    // Note: This causes a compiler error with EMF < 2.6M4!!! Ignore it or update your target platform.
    return instance.eInvoke(operation, arguments);
  }

  /**
   * @since 3.0
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    // Note: This causes a compiler error with EMF < 2.6M4!!! Ignore it or update your target platform.
    return instance.eInvoke(operationID, arguments);
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    return instance.eInternalContainer();
  }

  @Override
  public Resource.Internal eInternalResource()
  {
    CDOView view = cdoView();
    if (view != null && view.isClosed())
    {
      return null;
    }

    return instance.eInternalResource();
  }

  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain notifications)
  {
    return instance.eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass, NotificationChain notifications)
  {
    return instance.eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  @Override
  public boolean eIsProxy()
  {
    return instance.eIsProxy();
  }

  @Override
  public boolean eIsSet(EStructuralFeature feature)
  {
    return isSetInstanceValue(instance, feature);
  }

  @Override
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

  @Override
  public boolean eNotificationRequired()
  {
    return instance.eNotificationRequired();
  }

  @Override
  public void eNotify(Notification notification)
  {
    instance.eNotify(notification);
  }

  @Override
  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return instance.eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  @Override
  public URI eProxyURI()
  {
    return instance.eProxyURI();
  }

  @Override
  public EObject eResolveProxy(InternalEObject proxy)
  {
    return instance.eResolveProxy(proxy);
  }

  @Override
  public Resource eResource()
  {
    return instance.eResource();
  }

  @Override
  public void eSet(EStructuralFeature feature, Object newValue)
  {
    instance.eSet(feature, newValue);
  }

  @Override
  public void eSet(int featureID, Object newValue)
  {
    instance.eSet(featureID, newValue);
  }

  @Override
  public void eSetClass(EClass class1)
  {
    instance.eSetClass(class1);
  }

  @Override
  public void eSetDeliver(boolean deliver)
  {
    instance.eSetDeliver(deliver);
  }

  @Override
  public void eSetProxyURI(URI uri)
  {
    instance.eSetProxyURI(uri);
  }

  @Override
  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return instance.eSetResource(resource, notifications);
  }

  @Override
  public void eSetStore(EStore store)
  {
    instance.eSetStore(store);
  }

  @Override
  public Setting eSetting(EStructuralFeature feature)
  {
    return instance.eSetting(feature);
  }

  @Override
  public EStore eStore()
  {
    return instance.eStore();
  }

  @Override
  public void eUnset(EStructuralFeature feature)
  {
    instance.eUnset(feature);
  }

  @Override
  public void eUnset(int featureID)
  {
    instance.eUnset(featureID);
  }

  @Override
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
