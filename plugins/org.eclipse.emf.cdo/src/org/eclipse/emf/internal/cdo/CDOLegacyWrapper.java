/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.model.GenUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotifyingListImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.impl.ETypedElementImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Eike Stepper
 * @since 2.0
 */
/*
 * IMPORTANT: Compile errors in this class might indicate an old version of EMF. Legacy support is only enabled for EMF
 * with fixed bug #247130. These compile errors do not affect native models!
 */
public abstract class CDOLegacyWrapper extends CDOObjectWrapper
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyWrapper.class);

  protected CDOState state;

  protected InternalCDORevision revision;

  private boolean allProxiesResolved;

  public CDOLegacyWrapper(InternalEObject instance)
  {
    this.instance = instance;
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

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this); //$NON-NLS-1$
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
      TRACER.format("Setting revision: {0}", revision); //$NON-NLS-1$
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalPostAttach()
  {
    instanceToRevision();

    // TODO Avoid if no adapters in list (eBasicAdapters?)
    // TODO LEGACY Clarify how to intercept adapter addition in the legacy instance
    for (Adapter adapter : eAdapters())
    {
      view.subscribe(this, adapter);
    }
  }

  public void cdoInternalPostDetach(boolean remote)
  {
    // Do nothing
  }

  public void cdoInternalPreCommit()
  {
    // instanceToRevision();
    // if (cdoState() == CDOState.DIRTY) // NEW is handled in PrepareTransition
    // {
    // CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)cdoView().getSession().getRevisionManager();
    // InternalCDORevision originRevision = revisionManager.getRevisionByVersion(revision.getID(),
    // CDORevision.UNCHUNKED, revision.getVersion() - 1, false);
    // CDORevisionDelta delta = revision.compare(originRevision);
    //
    // // TODO LEGACY Consider to gather the deltas on the fly with noremal EMF change notifications
    // cdoView().toTransaction().registerRevisionDelta(delta);
    // }
  }

  public void cdoInternalPreLoad()
  {
  }

  public void cdoInternalPostLoad()
  {
    // TODO Consider not remembering the revisin after copying it to the instance (spare 1/2 of the space)
    revisionToInstance();
  }

  public void cdoInternalPostInvalidate()
  {
  }

  public void cdoInternalCleanup()
  {
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == this || obj == instance;
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
    return "CDOLegacyWrapper[" + id + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected void instanceToRevision()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering instance to revision: {0} --> {1}", instance, revision); //$NON-NLS-1$
    }

    // Handle containment
    instanceToRevisionContainment();

    // Handle values
    CDOPackageRegistry packageRegistry = cdoView().getSession().getPackageRegistry();
    EClass eClass = revision.getEClass();
    for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      instanceToRevisionFeature(feature, packageRegistry);
    }
  }

  protected void instanceToRevisionContainment()
  {
    CDOResource resource = (CDOResource)getInstanceResource(instance);
    revision.setResourceID(resource == null ? CDOID.NULL : resource.cdoID());

    InternalEObject eContainer = getInstanceContainer(instance);
    if (eContainer == null)
    {
      revision.setContainerID(CDOID.NULL);
      revision.setContainingFeatureID(0);
    }
    else
    {
      CDOObject cdoContainer = FSMUtil.adapt(eContainer, view);
      revision.setContainerID(cdoContainer.cdoID());
      revision.setContainingFeatureID(getInstanceContainerFeatureID(instance));
    }
  }

  protected void instanceToRevisionFeature(EStructuralFeature feature, CDOPackageRegistry packageRegistry)
  {
    Object instanceValue = getInstanceValue(instance, feature, packageRegistry);
    CDOObjectImpl.instanceToRevisionFeature(view, revision, feature, instanceValue);
  }

  /**
   * TODO Simon: Fix this whole mess ;-)
   */
  protected void revisionToInstance()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering revision to instance: {0} --> {1}", revision, instance); //$NON-NLS-1$
    }

    boolean deliver = instance.eDeliver();
    if (deliver)
    {
      instance.eSetDeliver(false);
    }

    try
    {
      // Handle containment
      revisionToInstanceContainment();

      // Handle values
      CDOPackageRegistry packageRegistry = cdoView().getSession().getPackageRegistry();
      EClass eClass = revision.getEClass();
      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
      {
        revisionToInstanceFeature(feature, packageRegistry);
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

  protected void revisionToInstanceContainment()
  {
    CDOID resourceID = revision.getResourceID();
    InternalEObject resource = getEObjectFromPotentialID(view, null, resourceID);
    setInstanceResource((Resource.Internal)resource);

    Object containerID = revision.getContainerID();
    InternalEObject container = getEObjectFromPotentialID(view, null, containerID);
    setInstanceContainer(container, revision.getContainingFeatureID());
  }

  protected void revisionToInstanceFeature(EStructuralFeature feature, CDOPackageRegistry packageRegistry)
  {
    // Attempt 4
    Object value = revision.getValue(feature);
    view.getStore().set(instance, feature, Notification.NO_INDEX, value);
  }

  protected Resource.Internal getInstanceResource(InternalEObject instance)
  {
    return instance.eDirectResource();
  }

  protected InternalEObject getInstanceContainer(InternalEObject instance)
  {
    return instance.eInternalContainer();
  }

  protected int getInstanceContainerFeatureID(InternalEObject instance)
  {
    return instance.eContainerFeatureID();
  }

  protected Object getInstanceValue(InternalEObject instance, EStructuralFeature feature,
      CDOPackageRegistry packageRegistry)
  {
    return instance.eGet(feature);
  }

  protected void setInstanceResource(Resource.Internal resource)
  {
    Method method = ReflectUtil.getMethod(instance.getClass(), "eSetDirectResource", Resource.Internal.class); //$NON-NLS-1$
    ReflectUtil.invokeMethod(method, instance, resource);
  }

  protected void setInstanceContainer(InternalEObject container, int containerFeatureID)
  {
    Method method = ReflectUtil.getMethod(instance.getClass(), "eBasicSetContainer", InternalEObject.class, int.class); //$NON-NLS-1$
    ReflectUtil.invokeMethod(method, instance, container, containerFeatureID);
  }

  protected void setInstanceValue(InternalEObject instance, EStructuralFeature feature, Object value)
  {
    instance.eSet(feature, value);
  }

  /**
   * TODO Ed: Help to fix whole mess (avoid inverse updates)
   */
  private void setInstanceValueOLD(InternalEObject instance, EStructuralFeature feature, Object value)
  {
    // TODO Consider EStoreEObjectImpl based objects as well!
    // TODO Don't use Java reflection
    Class<?> instanceClass = instance.getClass();
    String featureName = feature.getName();
    String fieldName = featureName;// TODO safeName()
    Field field = ReflectUtil.getField(instanceClass, fieldName);
    if (field == null && feature.getEType() == EcorePackage.eINSTANCE.getEBoolean())
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

        String flagName = GenUtil.getFeatureUpperName(featureName) + "_EFLAG"; //$NON-NLS-1$
        int flagsMask = getEFlagMask(instanceClass, flagName);

        field = ReflectUtil.getField(instanceClass, "eFlags"); //$NON-NLS-1$
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
      throw new ImplementationError("Field not found: " + fieldName); //$NON-NLS-1$
    }

    ReflectUtil.setValue(field, instance, value);
  }

  /**
   * @param feature
   *          in case that a proxy has to be created the feature that will determine the interface type of the proxy and
   *          that will be used later to resolve the proxy. <code>null</code> indicates that proxy creation will be
   *          avoided!
   */
  protected InternalEObject getEObjectFromPotentialID(InternalCDOView view, EStructuralFeature feature,
      Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      CDOID id = (CDOID)potentialID;
      if (id.isNull())
      {
        return null;
      }

      boolean loadOnDemand = feature == null;
      potentialID = view.getObject(id, loadOnDemand);
      if (potentialID == null && !loadOnDemand)
      {
        return createProxy(view, feature, id);
      }
    }

    if (potentialID instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)potentialID).cdoInternalInstance();
    }

    return (InternalEObject)potentialID;
  }

  /**
   * Creates and returns a <em>proxy</em> object. The usage of a proxy object is strongly limited. The only guarantee
   * that can be made is that the following methods are callable and will behave in the expected way:
   * <ul>
   * <li>{@link CDOObject#cdoID()} will return the {@link CDOID} of the target object
   * <li>{@link CDOObject#cdoState()} will return {@link CDOState#PROXY PROXY}
   * <li>{@link InternalEObject#eIsProxy()} will return <code>true</code>
   * <li>{@link InternalEObject#eProxyURI()} will return the EMF proxy URI of the target object
   * </ul>
   * Calling any other method on the proxy object will result in an {@link UnsupportedOperationException} being thrown
   * at runtime. Note also that the proxy object might even not be cast to the concrete type of the target object. The
   * proxy can only guaranteed to be of <em>any</em> concrete subtype of the declared type of the given feature.
   * <p>
   * TODO {@link InternalEObject#eResolveProxy(InternalEObject)
   */
  protected InternalEObject createProxy(InternalCDOView view, EStructuralFeature feature, CDOID id)
  {
    EClassifier eType = feature.getEType();
    Class<?> instanceClass = eType.getInstanceClass();

    Class<?>[] interfaces = { instanceClass, InternalEObject.class, LegacyProxy.class };
    ClassLoader classLoader = CDOLegacyWrapper.class.getClassLoader();
    LegacyProxyInvocationHandler handler = new LegacyProxyInvocationHandler(this, id);
    return (InternalEObject)Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

  /**
   * TODO Ed: Fix whole mess ;-)
   */
  protected void clearEList(InternalEList<Object> list)
  {
    while (!list.isEmpty())
    {
      Object toBeRemoved = list.basicGet(0);
      list.basicRemove(toBeRemoved, null);
    }
  }

  /**
   * TODO Consider using only EMF concepts for resolving proxies!
   */
  protected void resolveAllProxies()
  {
    // if (!allProxiesResolved)
    {
      CDOPackageRegistry packageRegistry = cdoView().getSession().getPackageRegistry();
      EClass eClass = revision.getEClass();
      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
      {
        if (feature instanceof EReference)
        {
          resolveProxies(feature, packageRegistry);
        }
      }

      // allProxiesResolved = true;
    }
  }

  /*
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  @SuppressWarnings("unchecked")
  protected void resolveProxies(EStructuralFeature feature, CDOPackageRegistry packageRegistry)
  {
    Object value = getInstanceValue(instance, feature, packageRegistry);
    if (value != null)
    {
      if (feature.isMany())
      {
        InternalEList<Object> list = (InternalEList<Object>)value;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
          Object element = list.get(i);
          if (element instanceof LegacyProxy)
          {
            CDOID id = ((LegacyProxy)element).getID();
            InternalCDOObject resolved = (InternalCDOObject)view.getObject(id);
            InternalEObject instance = resolved.cdoInternalInstance();

            // TODO LEGACY
            // // TODO Is InternalEList.basicSet() needed???
            // if (list instanceof org.eclipse.emf.ecore.util.DelegatingInternalEList)
            // {
            // list = ((org.eclipse.emf.ecore.util.DelegatingInternalEList)list).getDelegateInternalEList();
            // }

            if (list instanceof NotifyingListImpl)
            {
              ((NotifyingListImpl)list).basicSet(i, instance, null);
            }
            else
            {
              list.set(i, instance);
            }
          }
        }
      }
      else
      {
        if (value instanceof LegacyProxy)
        {
          CDOID id = ((LegacyProxy)value).getID();
          InternalCDOObject resolved = (InternalCDOObject)view.getObject(id);
          InternalEObject instance = resolved.cdoInternalInstance();
          setInstanceValue(instance, feature, instance);
        }
      }
    }
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
        URI uri = URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + ":proxy#" + id); //$NON-NLS-1$
        if (TRACER.isEnabled())
        {
          TRACER.format("Setting proxyURI {0} for {1}", uri, instance); //$NON-NLS-1$
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
          TRACER.format("Unsetting proxyURI for {0}", instance); //$NON-NLS-1$
        }

        instance.eSetProxyURI(null);
      }
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

  public static boolean isLegacyProxy(Object object)
  {
    return object instanceof LegacyProxy;
  }

  /**
   * @author Eike Stepper
   */
  private static interface LegacyProxy
  {
    public CDOID getID();
  }

  /**
   * @author Eike Stepper
   */
  private static final class LegacyProxyInvocationHandler implements InvocationHandler, LegacyProxy
  {
    private static final Method getIDMethod = ReflectUtil.getMethod(LegacyProxy.class, "getID"); //$NON-NLS-1$

    private static final Method eIsProxyMethod = ReflectUtil.getMethod(EObject.class, "eIsProxy"); //$NON-NLS-1$

    private static final Method eProxyURIMethod = ReflectUtil.getMethod(InternalEObject.class, "eProxyURI"); //$NON-NLS-1$

    private CDOLegacyWrapper wrapper;

    private CDOID id;

    public LegacyProxyInvocationHandler(CDOLegacyWrapper wrapper, CDOID id)
    {
      this.wrapper = wrapper;
      this.id = id;
    }

    public CDOID getID()
    {
      return id;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      if (method.equals(getIDMethod))
      {
        return id;
      }

      if (method.equals(eIsProxyMethod))
      {
        return true;
      }

      if (method.equals(eProxyURIMethod))
      {
        // Use the resource of the container because it's guaranteed to be in the same CDOView as the resource
        // of the target!
        Resource resource = wrapper.eResource();

        // TODO Consider using a "fake" Resource implementation. See Resource.getEObject(...)
        return resource.getURI().appendFragment(id.toURIFragment());
      }

      // A client must have invoked the proxy while being told not to do so!
      throw new UnsupportedOperationException(method.getName());
    }
  }
}
