/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.GenUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.impl.ETypedElementImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CDOLegacyImpl extends CDOWrapperImpl implements Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyImpl.class);

  private CDOState state;

  private CDOResourceImpl resource;

  private InternalCDORevision revision;

  public CDOLegacyImpl()
  {
    state = CDOState.TRANSIENT;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public InternalCDORevision cdoRevision()
  {
    return revision;
  }

  public CDOResourceImpl cdoResource()
  {
    return resource;
  }

  public CDOClass cdoClass()
  {
    return CDOObjectImpl.getCDOClass(this);
  }

  @Override
  public CDOViewImpl cdoView()
  {
    // TODO Why is this lazy?
    if (view == null)
    {
      view = CDOObjectImpl.getCDOView(this);
    }

    return view;
  }

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      CDOState tmp = this.state;
      this.state = state;
      adjustEProxy();
      return tmp;
    }

    // TODO Detect duplicate cdoInternalSetState() calls
    return null;
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision);
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
    // if (resource != null)
    // {
    // transferResourceToInstance(resource);
    // }
  }

  public void cdoInternalPostAttach()
  {
    // Do nothing
  }

  public void cdoInternalPostDetach()
  {
    // Do nothing
  }

  public void cdoInternalPreCommit()
  {
    transferInstanceToRevision();

    CDORevisionManagerImpl revisionManager = view.getSession().getRevisionManager();
    InternalCDORevision revision = cdoRevision();
    InternalCDORevision originRevision = revisionManager.getRevisionByVersion(revision.getID(), CDORevision.UNCHUNKED,
        revision.getVersion() - 1, false);

    CDOTransactionImpl transaction = cdoView().toTransaction();
    transaction.registerRevisionDelta(cdoRevision().compare(originRevision));
  }

  public void cdoInternalPostLoad()
  {
    transferRevisionToInstance();
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

  public InternalEObject getTarget()
  {
    return instance;
  }

  public void setTarget(Notifier newTarget)
  {
    if (newTarget instanceof InternalEObject)
    {
      instance = (InternalEObject)newTarget;
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + newTarget.getClass().getName());
    }
  }

  public void unsetTarget(Notifier oldTarget)
  {
    if (oldTarget instanceof InternalEObject)
    {
      if (instance == oldTarget)
      {
        instance = null;
      }
    }
    else
    {
      throw new IllegalArgumentException("Not an InternalEObject: " + oldTarget.getClass().getName());
    }
  }

  protected void transferInstanceToRevision()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering instance to revision: {0} --> {1}", instance, revision);
    }

    CDOViewImpl view = cdoView();
    if (view == null)
    {
      throw new ImplementationError("view == null");
    }

    // Handle containment
    EObject container = instance.eContainer();
    if (container != null)
    {
      if (container instanceof CDOResource)
      {
        revision.setResourceID(((CDOResource)container).cdoID());
        revision.setContainerID(CDOID.NULL);
        revision.setContainingFeatureID(0);
      }
      else
      {
        revision.setResourceID(CDOID.NULL);
        // TODO is as CDOIDProvider call ok here?
        CDOID containerID = view.provideCDOID(container);
        if (containerID.isNull())
        {
          throw new ImplementationError("containerID.isNull()");
        }

        int containerFeatureID = instance.eContainerFeatureID();// containER???
        revision.setContainerID(containerID);
        revision.setContainingFeatureID(containerFeatureID);
      }
    }

    // Handle values
    CDOClass cdoClass = revision.getCDOClass();
    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      Object instanceValue = getInstanceValue(instance, feature);
      if (feature.isMany())
      {
        List<Object> revisionList = revision.getList(feature); // TODO lazy?
        revisionList.clear();

        if (instanceValue != null)
        {
          if (instanceValue instanceof InternalEList)
          {
            InternalEList<?> instanceList = (InternalEList<?>)instanceValue;
            if (instanceList != null)
            {
              for (Iterator<?> it = instanceList.basicIterator(); it.hasNext();)
              {
                Object instanceElement = it.next();
                if (instanceElement != null && feature.isReference())
                {
                  instanceElement = view.convertObjectToID(instanceElement);
                }

                revisionList.add(instanceElement);
              }
            }
          }
          else
          {
            throw new ImplementationError("Not an InternalEList: " + instanceValue.getClass().getName());
          }
        }
      }
      else
      {
        if (instanceValue != null && feature.isReference())
        {
          instanceValue = view.convertObjectToID(instanceValue);
        }

        revision.setValue(feature, instanceValue);
      }
    }
  }

  protected void transferRevisionToInstance()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering revision to instance: {0} --> {1}", revision, instance);
    }

    CDOViewImpl view = cdoView();
    if (view == null)
    {
      throw new ImplementationError("view == null");
    }

    boolean deliver = instance.eDeliver();
    if (deliver)
    {
      instance.eSetDeliver(false);
    }

    try
    {
      // Handle containment
      transferContainmentToInstance(view);

      // Handle values
      CDOClass cdoClass = revision.getCDOClass();
      CDOFeature[] features = cdoClass.getAllFeatures();
      for (CDOFeature feature : features)
      {
        transferFeatureToInstance(view, feature);
      }
    }
    finally
    {
      if (deliver)
      {
        instance.eSetDeliver(true);
      }
    }
  }

  protected void transferContainmentToInstance(CDOViewImpl view)
  {
    CDOID containerID = revision.getContainerID();
    if (containerID.isNull())
    {
      CDOID resourceID = revision.getResourceID();
      Resource.Internal resource = (Resource.Internal)view.getObject(resourceID);
      transferResourceToInstance(resource);
    }
    else
    {
      int containingFeatureID = revision.getContainingFeatureID();
      InternalEObject container = convertPotentialID(view, containerID);
      ((BasicEObjectImpl)instance).eBasicSetContainer(container, containingFeatureID, null);
    }
  }

  public void transferResourceToInstance(Resource.Internal resource)
  {
    Method method = ReflectUtil.getMethod(BasicEObjectImpl.class, "eSetDirectResource", Resource.Internal.class);

    try
    {
      ReflectUtil.invokeMethod(method, instance, resource);
    }
    catch (InvocationTargetException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @SuppressWarnings("unchecked")
  protected void transferFeatureToInstance(CDOViewImpl view, CDOFeature feature)
  {
    Object value = revision.getValue(feature);
    if (feature.isMany())
    {
      InternalEList<Object> instanceList = (InternalEList<Object>)getInstanceValue(instance, feature);
      if (instanceList != null)
      {
        clearEList(instanceList);
        if (value != null)
        {
          List<?> revisionList = (List<?>)value;
          for (Object element : revisionList)
          {
            if (feature.isReference())
            {
              element = convertPotentialID(view, element);
            }

            instanceList.basicAdd(element, null);
          }
        }
      }
    }
    else
    {
      if (feature.isReference())
      {
        value = convertPotentialID(view, value);
      }

      setInstanceValue(instance, feature, value);
    }
  }

  protected InternalEObject convertPotentialID(CDOViewImpl view, Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      CDOID id = (CDOID)potentialID;
      if (id.isNull())
      {
        return null;
      }

      potentialID = view.getObject(id, false);
    }

    if (potentialID instanceof InternalCDOObject)
    {
      potentialID = ((InternalCDOObject)potentialID).cdoInternalInstance();
    }

    if (potentialID instanceof InternalEObject)
    {
      return (InternalEObject)potentialID;
    }

    throw new ImplementationError();
  }

  protected Object getInstanceValue(InternalEObject instance, CDOFeature feature)
  {
    EStructuralFeature eFeature = ModelUtil.getEFeature(feature, cdoView().getSession().getPackageRegistry());
    return instance.eGet(eFeature);
  }

  protected void setInstanceValue(InternalEObject instance, CDOFeature feature, Object value)
  {
    // TODO Don't use Java reflection
    Class<?> instanceClass = instance.getClass();
    String featureName = feature.getName();
    String fieldName = featureName;// TODO safeName()
    Field field = ReflectUtil.getField(instanceClass, fieldName);
    if (field == null && feature.getType() == CDOType.BOOLEAN)
    {
      if (instanceClass.isAssignableFrom(EAttributeImpl.class) || instanceClass.isAssignableFrom(EClassImpl.class)
          || instanceClass.isAssignableFrom(EDataTypeImpl.class)
          || instanceClass.isAssignableFrom(EReferenceImpl.class)
          || instanceClass.isAssignableFrom(EStructuralFeatureImpl.class)
          || instanceClass.isAssignableFrom(ETypedElementImpl.class))
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
        int flagsMask = getEFlagMask(instanceClass, flagName);

        field = ReflectUtil.getField(instanceClass, "eFlags");
        int flags = (Integer)ReflectUtil.getValue(field, instance);
        boolean on = (Boolean)value;
        if (on)
        {
          flags |= flagsMask; // Add EFlag
        }
        else
        {
          flags &= ~flagsMask; // Remove EFlag
        }

        ReflectUtil.setValue(field, instance, flags);
        return;
      }
    }

    if (field == null)
    {
      throw new ImplementationError("Field not found: " + fieldName);
    }

    ReflectUtil.setValue(field, instance, value);
  }

  protected void adjustEProxy()
  {
    // Setting eProxyURI is necessary to prevent content adapters from
    // loading the whole content tree.
    // TODO Does not have the desired effect ;-( see CDOEditor.createModel()
    if (state == CDOState.PROXY)
    {
      if (!instance.eIsProxy())
      {
        URI uri = URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + ":proxy#" + id);
        if (TRACER.isEnabled())
        {
          TRACER.format("Setting proxyURI {0} for {1}", uri, instance);
        }

        instance.eSetProxyURI(uri);
      }
    }
    else
    {
      if (instance.eIsProxy())
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Unsetting proxyURI for {0}", instance);
        }

        instance.eSetProxyURI(null);
      }
    }
  }

  protected void clearEList(InternalEList<Object> list)
  {
    while (!list.isEmpty())
    {
      Object toBeRemoved = list.basicGet(0);
      list.basicRemove(toBeRemoved, null);
    }
  }

  protected static int getEFlagMask(Class<?> instanceClass, String flagName)
  {
    Field field = ReflectUtil.getField(instanceClass, flagName);
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
}
