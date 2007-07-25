/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.impl.ETypedElementImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.GenUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOAdapterImpl extends AdapterImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOAdapterImpl.class);

  private CDOViewImpl view;

  private CDOID id;

  private CDOState state;

  private CDOResourceImpl resource;

  private CDORevisionImpl revision;

  public CDOAdapterImpl()
  {
    state = CDOState.TRANSIENT;
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == CDOAdapterImpl.class;
  }

  @Override
  public InternalEObject getTarget()
  {
    return (InternalEObject)super.getTarget();
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    if (newTarget instanceof InternalEObject)
    {
      super.setTarget(newTarget);
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + newTarget.getClass().getName());
    }
  }

  @Override
  public void unsetTarget(Notifier oldTarget)
  {
    if (oldTarget instanceof InternalEObject)
    {
      super.unsetTarget(oldTarget);
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + oldTarget.getClass().getName());
    }
  }

  @Override
  public void notifyChanged(Notification msg)
  {
    switch (msg.getEventType())
    {
    case Notification.ADD:
    case Notification.ADD_MANY:
    case Notification.REMOVE:
    case Notification.REMOVE_MANY:
    case Notification.MOVE:
    case Notification.SET:
    case Notification.UNSET:
      InternalEObject notifier = (InternalEObject)msg.getNotifier();
      if (notifier == getTarget() && !notifier.eIsProxy())
      {
        CDOStateMachine.INSTANCE.write(this);
      }
    }
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public CDORevisionImpl cdoRevision()
  {
    return revision;
  }

  public CDOResourceImpl cdoResource()
  {
    return resource;
  }

  public CDOClassImpl cdoClass()
  {
    return CDOObjectImpl.getCDOClass(this);
  }

  public CDOViewImpl cdoView()
  {
    if (view == null)
    {
      view = CDOObjectImpl.getCDOView(this);
    }

    return view;
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id == null");
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0} for {1}", id, getTarget());
    }

    this.id = id;
  }

  public void cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      this.state = state;
      InternalEObject target = getTarget();
      if (state == CDOState.PROXY)
      {
        if (!target.eIsProxy())
        {
          URI uri = URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + ":proxy#" + id);
          target.eSetProxyURI(uri);
        }
      }
      else
      {
        if (target.eIsProxy())
        {
          target.eSetProxyURI(null);
        }
      }
    }
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (CDORevisionImpl)revision;
  }

  public void cdoInternalSetView(CDOView view)
  {
    this.view = (CDOViewImpl)view;
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
  }

  public void cdoInternalPostLoad()
  {
    transferRevisionToTarget();
    cdoInternalSetState(CDOState.CLEAN);
  }

  public void cdoInternalPostAttach()
  {
    // Do nothing
  }

  public InternalEObject cdoInternalInstance()
  {
    return getTarget();
  }

  public void cdoInternalPreCommit()
  {
    transferTargetToRevision();
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    // TODO Implement method CDOAdapterImpl.cdoInternalDynamicFeature()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public String toString()
  {
    if (id == null)
    {
      return eClass().getName() + "?";
    }

    return eClass().getName() + "@" + id;
  }

  private void transferTargetToRevision()
  {
    InternalEObject target = getTarget();
    CDOViewImpl view = cdoView();
    if (view == null)
    {
      throw new ImplementationError("view == null");
    }

    // Handle containment
    EObject container = target.eContainer();
    if (container != null)
    {
      if (container instanceof CDOResource)
      {
        revision.setResourceID(((CDOResource)container).cdoID());
        revision.setContainerID(CDOID.NULL);
        revision.setContainingFeature(0);
      }
      else
      {
        revision.setResourceID(CDOID.NULL);
        CDOID containerID = view.provideCDOID(container);
        if (containerID.isNull())
        {
          throw new ImplementationError("containerID.isNull()");
        }

        int containerFeatureID = target.eContainerFeatureID();// containER???
        revision.setContainerID(containerID);
        revision.setContainingFeature(containerFeatureID);
      }
    }

    // Handle values
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
      Object targetValue = getTargetValue(target, feature);
      if (feature.isMany())
      {
        List revisionList = revision.getList(feature); // TODO lazy?
        revisionList.clear();

        if (targetValue != null)
        {
          if (targetValue instanceof InternalEList)
          {
            InternalEList targetList = (InternalEList)targetValue;
            if (targetList != null)
            {
              for (Iterator it = targetList.basicIterator(); it.hasNext();)
              {
                Object targetElement = it.next();
                if (targetElement != null && feature.isReference())
                {
                  targetElement = view.convertObjectToID(targetElement);
                }

                revisionList.add(targetElement);
              }
            }
          }
          else
          {
            throw new ImplementationError("Not an InternalEList: " + targetValue.getClass().getName());
          }
        }
      }
      else
      {
        if (targetValue != null && feature.isReference())
        {
          targetValue = view.convertObjectToID(targetValue);
        }

        revision.setValue(feature, targetValue);
      }
    }
  }

  private void transferRevisionToTarget()
  {
    InternalEObject target = getTarget();
    CDOViewImpl view = cdoView();
    if (view == null)
    {
      throw new ImplementationError("view == null");
    }

    // Handle containment
    CDOID containerID = revision.getContainerID();
    if (containerID.isNull())
    {
      CDOID resourceID = revision.getResourceID();
      Resource.Internal resource = (Resource.Internal)view.lookupInstance(resourceID);
      transferResourceToTarget((BasicEObjectImpl)target, resource);
    }
    else
    {
      int containingFeatureID = revision.getContainingFeatureID();
      InternalCDOObject container = view.lookupInstance(containerID);
      ((BasicEObjectImpl)target).eBasicSetContainer(container, containingFeatureID, null);
    }

    // Handle values
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
      Object value = revision.getValue(feature);
      if (feature.isMany())
      {
        InternalEList targetList = (InternalEList)getTargetValue(target, feature);
        if (targetList != null)
        {
          while (!targetList.isEmpty())
          {
            Object toBeRemoved = targetList.basicGet(0);
            targetList.basicRemove(toBeRemoved, null);
          }

          List revisionList = (List)value;
          for (Object toBeAdded : revisionList)
          {
            if (feature.isReference())
            {
              toBeAdded = view.convertIDToObject(toBeAdded);
            }

            targetList.basicAdd(toBeAdded, null);
          }
        }
      }
      else
      {
        if (feature.isReference())
        {
          value = view.convertIDToObject(value);
        }

        setTargetValue(target, feature, value);
      }
    }
  }

  private void transferResourceToTarget(BasicEObjectImpl target, Resource.Internal resource)
  {
    Method method = ReflectUtil.getMethod(BasicEObjectImpl.class, "eSetDirectResource", Resource.Internal.class);

    try
    {
      ReflectUtil.invokeMethod(method, target, resource);
    }
    catch (InvocationTargetException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private Object getTargetValue(InternalEObject target, CDOFeatureImpl feature)
  {
    // Class<?> targetClass = target.getClass();
    // String featureName = feature.getName();
    EStructuralFeature eFeature = ModelUtil.getEFeature(feature, cdoView().getSession().getPackageRegistry());
    Object value = target.eGet(eFeature);
    return value;

    // // TODO BOOLEAN_OBJECT?
    // String methodName = GenUtil.getFeatureGetterName(featureName,
    // feature.getType() == CDOType.BOOLEAN);
    // Method method = getMethod(targetClass, methodName,
    // ReflectUtil.NO_PARAMETERS);
    //
    // String fieldName = featureName;// XXX safeName()
    // Field field = getField(targetClass, fieldName);
    // if (field == null && feature.getType() == CDOType.BOOLEAN)
    // {
    // if (targetClass.isAssignableFrom(EAttributeImpl.class) ||
    // targetClass.isAssignableFrom(EClassImpl.class)
    // || targetClass.isAssignableFrom(EDataTypeImpl.class) ||
    // targetClass.isAssignableFrom(EReferenceImpl.class)
    // || targetClass.isAssignableFrom(EStructuralFeatureImpl.class)
    // || targetClass.isAssignableFrom(ETypedElementImpl.class))
    // {
    // // *******************************************
    // // ID_EFLAG = 1 << 15;
    // // *******************************************
    // // ABSTRACT_EFLAG = 1 << 8;
    // // INTERFACE_EFLAG = 1 << 9;
    // // *******************************************
    // // SERIALIZABLE_EFLAG = 1 << 8;
    // // *******************************************
    // // CONTAINMENT_EFLAG = 1 << 15;
    // // RESOLVE_PROXIES_EFLAG = 1 << 16;
    // // *******************************************
    // // CHANGEABLE_EFLAG = 1 << 10;
    // // VOLATILE_EFLAG = 1 << 11;
    // // TRANSIENT_EFLAG = 1 << 12;
    // // UNSETTABLE_EFLAG = 1 << 13;
    // // DERIVED_EFLAG = 1 << 14;
    // // *******************************************
    // // ORDERED_EFLAG = 1 << 8;
    // // UNIQUE_EFLAG = 1 << 9;
    // // *******************************************
    //
    // String flagName = GenUtil.getFeatureUpperName(featureName) + "_EFLAG";
    // int flagsMask = getEFlagMask(targetClass, flagName);
    //
    // field = getField(targetClass, "eFlags");
    // int value = (Integer)getFiedValue(target, field);
    // return new Boolean((value & flagsMask) != 0);
    // }
    // }
    //
    // if (field == null)
    // {
    // throw new ImplementationError("Field not found: " + fieldName);
    // }
    //
    // return getFiedValue(target, field);
  }

  private void setTargetValue(InternalEObject target, CDOFeatureImpl feature, Object value)
  {
    Class<?> targetClass = target.getClass();
    String featureName = feature.getName();
    String fieldName = featureName;// XXX safeName()
    Field field = ReflectUtil.getField(targetClass, fieldName);
    if (field == null && feature.getType() == CDOType.BOOLEAN)
    {
      if (targetClass.isAssignableFrom(EAttributeImpl.class) || targetClass.isAssignableFrom(EClassImpl.class)
          || targetClass.isAssignableFrom(EDataTypeImpl.class) || targetClass.isAssignableFrom(EReferenceImpl.class)
          || targetClass.isAssignableFrom(EStructuralFeatureImpl.class)
          || targetClass.isAssignableFrom(ETypedElementImpl.class))
      {
        // *******************************************
        // ID_EFLAG = 1 << 15;
        // *******************************************
        // ABSTRACT_EFLAG = 1 << 8;
        // INTERFACE_EFLAG = 1 << 9;
        // *******************************************
        // SERIALIZABLE_EFLAG = 1 << 8;
        // *******************************************
        // CONTAINMENT_EFLAG = 1 << 15;
        // RESOLVE_PROXIES_EFLAG = 1 << 16;
        // *******************************************
        // CHANGEABLE_EFLAG = 1 << 10;
        // VOLATILE_EFLAG = 1 << 11;
        // TRANSIENT_EFLAG = 1 << 12;
        // UNSETTABLE_EFLAG = 1 << 13;
        // DERIVED_EFLAG = 1 << 14;
        // *******************************************
        // ORDERED_EFLAG = 1 << 8;
        // UNIQUE_EFLAG = 1 << 9;
        // *******************************************

        String flagName = GenUtil.getFeatureUpperName(featureName) + "_EFLAG";
        int flagsMask = getEFlagMask(targetClass, flagName);

        field = ReflectUtil.getField(targetClass, "eFlags");
        int flags = (Integer)ReflectUtil.getValue(field, target);
        boolean on = (Boolean)value;
        if (on)
        {
          flags |= flagsMask; // Add EFlag
        }
        else
        {
          flags &= ~flagsMask; // Remove EFlag
        }

        ReflectUtil.setValue(field, target, flags);
        return;
      }
    }

    if (field == null)
    {
      throw new ImplementationError("Field not found: " + fieldName);
    }

    ReflectUtil.setValue(field, target, value);
  }

  private static int getEFlagMask(Class<?> targetClass, String flagName)
  {
    Field field = ReflectUtil.getField(targetClass, flagName);
    if (!field.isAccessible())
    {
      field.setAccessible(true);
    }

    try
    {
      return (Integer)field.get(null);
    }
    catch (IllegalAccessException ex)
    {
      throw new ImplementationError(ex);
    }
  }

  public EList<Adapter> eAdapters()
  {
    return getTarget().eAdapters();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return getTarget().eAllContents();
  }

  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    return getTarget().eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  public NotificationChain eBasicRemoveFromContainer(NotificationChain notifications)
  {
    return getTarget().eBasicRemoveFromContainer(notifications);
  }

  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain notifications)
  {
    return getTarget().eBasicSetContainer(newContainer, newContainerFeatureID, notifications);
  }

  public EClass eClass()
  {
    return getTarget().eClass();
  }

  public EObject eContainer()
  {
    return getTarget().eContainer();
  }

  public int eContainerFeatureID()
  {
    return getTarget().eContainerFeatureID();
  }

  public EStructuralFeature eContainingFeature()
  {
    return getTarget().eContainingFeature();
  }

  public EReference eContainmentFeature()
  {
    return getTarget().eContainmentFeature();
  }

  public EList<EObject> eContents()
  {
    return getTarget().eContents();
  }

  public EList<EObject> eCrossReferences()
  {
    return getTarget().eCrossReferences();
  }

  public boolean eDeliver()
  {
    return getTarget().eDeliver();
  }

  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    return getTarget().eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  public Resource.Internal eDirectResource()
  {
    return getTarget().eDirectResource();
  }

  public Object eGet(EStructuralFeature feature, boolean resolve, boolean coreType)
  {
    return getTarget().eGet(feature, resolve, coreType);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return getTarget().eGet(feature, resolve);
  }

  public Object eGet(EStructuralFeature feature)
  {
    return getTarget().eGet(feature);
  }

  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    return getTarget().eGet(featureID, resolve, coreType);
  }

  public InternalEObject eInternalContainer()
  {
    return getTarget().eInternalContainer();
  }

  public Resource.Internal eInternalResource()
  {
    return getTarget().eInternalResource();
  }

  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return getTarget().eInverseAdd(otherEnd, featureID, baseClass, notifications);
  }

  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class<?> baseClass,
      NotificationChain notifications)
  {
    return getTarget().eInverseRemove(otherEnd, featureID, baseClass, notifications);
  }

  public boolean eIsProxy()
  {
    return getTarget().eIsProxy();
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return getTarget().eIsSet(feature);
  }

  public boolean eIsSet(int featureID)
  {
    return getTarget().eIsSet(featureID);
  }

  public boolean eNotificationRequired()
  {
    return getTarget().eNotificationRequired();
  }

  public void eNotify(Notification notification)
  {
    getTarget().eNotify(notification);
  }

  public EObject eObjectForURIFragmentSegment(String uriFragmentSegment)
  {
    return getTarget().eObjectForURIFragmentSegment(uriFragmentSegment);
  }

  public URI eProxyURI()
  {
    return getTarget().eProxyURI();
  }

  public EObject eResolveProxy(InternalEObject proxy)
  {
    return getTarget().eResolveProxy(proxy);
  }

  public Resource eResource()
  {
    return getTarget().eResource();
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    getTarget().eSet(feature, newValue);
  }

  public void eSet(int featureID, Object newValue)
  {
    getTarget().eSet(featureID, newValue);
  }

  public void eSetClass(EClass class1)
  {
    getTarget().eSetClass(class1);
  }

  public void eSetDeliver(boolean deliver)
  {
    getTarget().eSetDeliver(deliver);
  }

  public void eSetProxyURI(URI uri)
  {
    getTarget().eSetProxyURI(uri);
  }

  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    return getTarget().eSetResource(resource, notifications);
  }

  public void eSetStore(EStore store)
  {
    getTarget().eSetStore(store);
  }

  public Setting eSetting(EStructuralFeature feature)
  {
    return getTarget().eSetting(feature);
  }

  public EStore eStore()
  {
    return getTarget().eStore();
  }

  public void eUnset(EStructuralFeature feature)
  {
    getTarget().eUnset(feature);
  }

  public void eUnset(int featureID)
  {
    getTarget().eUnset(featureID);
  }

  public String eURIFragmentSegment(EStructuralFeature feature, EObject object)
  {
    return getTarget().eURIFragmentSegment(feature, object);
  }

  public static CDOAdapterImpl get(InternalEObject eObject)
  {
    EList<Adapter> adapters = eObject.eAdapters();
    return (CDOAdapterImpl)EcoreUtil.getAdapter(adapters, CDOAdapterImpl.class);
  }
}
