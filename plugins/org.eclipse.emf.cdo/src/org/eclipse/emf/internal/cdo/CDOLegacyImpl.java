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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;

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
public abstract class CDOLegacyImpl extends CDOWrapperImpl
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyImpl.class);

  protected CDOState state;

  protected CDOResourceImpl resource;

  protected CDORevisionImpl revision;

  public CDOLegacyImpl()
  {
    state = CDOState.TRANSIENT;
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

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      try
      {
        return this.state;
      }
      finally
      {
        this.state = state;
      }
    }
    else
    {
      // TODO Detect duplicate cdoInternalSetState() calls
      return null;
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

  public void cdoInternalSetResource(CDOResource resource)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting resource: {0}", resource);
    }

    this.resource = (CDOResourceImpl)resource;
  }

  public void cdoInternalPostAttach()
  {
    // Do nothing
  }

  public void cdoInternalPreCommit()
  {
    transferInstanceToRevision();
  }

  public void cdoInternalPostLoad()
  {
    transferRevisionToInstance();
    cdoInternalSetState(CDOState.CLEAN);
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

  protected void transferInstanceToRevision()
  {
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
        revision.setContainingFeature(0);
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
        revision.setContainingFeature(containerFeatureID);
      }
    }

    // Handle values
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
      Object instanceValue = getInstanceValue(instance, feature);
      if (feature.isMany())
      {
        List revisionList = revision.getList(feature); // TODO lazy?
        revisionList.clear();

        if (instanceValue != null)
        {
          if (instanceValue instanceof InternalEList)
          {
            InternalEList instanceList = (InternalEList)instanceValue;
            if (instanceList != null)
            {
              for (Iterator it = instanceList.basicIterator(); it.hasNext();)
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
    CDOViewImpl view = cdoView();
    if (view == null)
    {
      throw new ImplementationError("view == null");
    }

    // Handle containment
    transferContainmentToInstance(view);

    // Handle values
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (CDOFeatureImpl feature : features)
    {
      transferFeatureToInstance(view, feature);
    }
  }

  protected void transferContainmentToInstance(CDOViewImpl view)
  {
    CDOID containerID = revision.getContainerID();
    if (containerID.isNull())
    {
      CDOID resourceID = revision.getResourceID();
      Resource.Internal resource = (Resource.Internal)view.lookupObject(resourceID);
      transferResourceToInstance((BasicEObjectImpl)instance, resource);
    }
    else
    {
      int containingFeatureID = revision.getContainingFeatureID();
      // TODO Maybe convertPotentialID() better here?
      InternalCDOObject container = view.lookupObject(containerID);
      ((BasicEObjectImpl)instance).eBasicSetContainer(container, containingFeatureID, null);
    }
  }

  protected void transferResourceToInstance(BasicEObjectImpl instance, Resource.Internal resource)
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

  protected void transferFeatureToInstance(CDOViewImpl view, CDOFeatureImpl feature)
  {
    Object value = revision.getValue(feature);
    if (feature.isMany())
    {
      InternalEList instanceList = (InternalEList)getInstanceValue(instance, feature);
      if (instanceList != null)
      {
        clearEList(instanceList);
        List revisionList = (List)value;
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
    else
    {
      if (feature.isReference())
      {
        value = convertPotentialID(view, value);
      }

      setInstanceValue(instance, feature, value);
    }
  }

  protected abstract Object convertPotentialID(CDOViewImpl view, Object potentialID);

  protected Object getInstanceValue(InternalEObject instance, CDOFeatureImpl feature)
  {
    // Class<?> targetClass = target.getClass();
    // String featureName = feature.getName();
    EStructuralFeature eFeature = ModelUtil.getEFeature(feature, cdoView().getSession().getPackageRegistry());
    Object value = instance.eGet(eFeature);
    return value;

    // // TODO BOOLEAN_OBJECT?
    // String methodName = GenUtil.getFeatureGetterName(featureName,
    // feature.getType() == CDOType.BOOLEAN);
    // Method method = getMethod(targetClass, methodName,
    // ReflectUtil.NO_PARAMETERS);
    //
    // String fieldName = featureName;// TODO safeName()
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

  protected void setInstanceValue(InternalEObject instance, CDOFeatureImpl feature, Object value)
  {
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

  protected void clearEList(InternalEList list)
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
