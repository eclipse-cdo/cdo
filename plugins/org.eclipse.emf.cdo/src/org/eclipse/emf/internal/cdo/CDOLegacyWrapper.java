/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - bug 247226: Transparently support legacy models
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class CDOLegacyWrapper extends CDOObjectWrapper
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyWrapper.class);

  protected CDOState state;

  protected InternalCDORevision revision;

  /**
   * It could happen that while <i>revisionToInstance()</i> is executed externally the <i>internalPostLoad()</i> method
   * will be called. This happens for example if <i>internalPostInvalidate()</i> is called. The leads to another
   * <i>revisionToInstance()</i> call while the first call has not finished. This is certainly not so cool. That's why
   * <b>underConstruction</b> will flag that <i>revisionToInstance()</i> is still running and avoid the second call.
   * 
   * @since 3.0
   */
  private boolean underConstruction;

  /**
   * This local ThreadMap stores all pre-registered objects. This avoids a neverending loop when setting the container
   * for the object.
   */
  private static ThreadLocal<Map<CDOID, EObject>> preRegisteredObjects = new InheritableThreadLocal<Map<CDOID, EObject>>()
  {
    @Override
    protected Map<CDOID, EObject> initialValue()
    {
      return new HashMap<CDOID, EObject>();
    }
  };

  private static ThreadLocal<Counter> recursionCounter = new InheritableThreadLocal<Counter>()
  {
    @Override
    protected Counter initialValue()
    {
      return new Counter();
    }
  };

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
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      CDOState oldState = this.state;
      this.state = state;
      adjustEProxy();
      if (view != null)
      {
        view.handleObjectStateChanged(this, oldState, state);
      }

      return oldState;
    }

    return null;
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format(("Setting revision: " + revision + ""));
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalPostAttach()
  {
    instanceToRevision();

    // TODO Avoid if no adapters in list (eBasicAdapters?)
    // TODO LEGACY Clarify how to intercept adapter addition in the legacy
    // instance
    for (Adapter adapter : eAdapters())
    {
      view.subscribe(this, adapter);
    }
  }

  public void cdoInternalPostDetach(boolean remote)
  {
  }

  public void cdoInternalPreCommit()
  {
  }

  public void cdoInternalPreLoad()
  {
  }

  public void cdoInternalPostLoad()
  {
    // TODO Consider not remembering the revisin after copying it to the
    // instance (spare 1/2 of the space)
    revisionToInstance();
  }

  public void cdoInternalPostInvalidate()
  {
    InternalCDORevision revision = cdoView().getRevision(cdoID(), true);
    cdoInternalSetRevision(revision);
    revisionToInstance();
    cdoInternalSetState(CDOState.CLEAN);
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
    return "CDOLegacyWrapper[" + id + "]";
  }

  protected void instanceToRevision()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering instance to revision: {0} --> {1}", instance, revision);
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
    CDOObjectImpl.instanceToRevisionFeature(view, this, feature, instanceValue);
  }

  protected void revisionToInstance()
  {
    if (underConstruction)
    {
      // return if revisionToInstance was called before to avoid doubled calls
      return;
    }

    underConstruction = true;

    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering revision to instance: {0} --> {1}", revision, instance);
    }

    boolean deliver = instance.eDeliver();
    if (deliver)
    {
      instance.eSetDeliver(false);
    }

    Counter counter = recursionCounter.get();
    try
    {
      preRegisterObject(this);
      counter.increment();

      revisionToInstanceContainment();

      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(revision.getEClass()))
      {
        revisionToInstanceFeature(feature);
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
    finally
    {
      if (deliver)
      {
        instance.eSetDeliver(true);
      }

      int newThreadCount = counter.decrement();

      if (newThreadCount == 0 && getPreRegisteredObjects() != null)
      {
        // localThread.remove(); // TODO Martin: check why new
        // objects will be created if this list is cleared
      }

      underConstruction = false;
    }
  }

  /**
   * adds an object to the pre-registered objects list which hold all created objects even if they are not registered in
   * the view
   */
  private void preRegisterObject(CDOLegacyWrapper wrapper)
  {
    getPreRegisteredObjects().put(wrapper.cdoID(), wrapper);
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

  private Map<CDOID, EObject> getPreRegisteredObjects()
  {
    return preRegisteredObjects.get();
  }

  /**
   * @since 3.0
   */

  protected void revisionToInstanceFeature(EStructuralFeature feature)
  {
    if (feature.isMany())
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("State of Object is : " + state);
      }

      if (state == CDOState.CLEAN || state == CDOState.PROXY)
      {
        int size = revision.size(feature);

        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>)instance.eGet(feature);

        clearList(feature, list);
        for (int i = 0; i < size; i++)
        {
          Object object = getValueFromRevision(feature, i);

          if (TRACER.isEnabled())
          {
            TRACER.format(("Adding " + object + " to feature " + feature + "in instance " + instance));
          }

          list.basicAdd(object, null);
        }
      }
    }
    else
    {
      // !feature.isMany()
      Object object = getValueFromRevision(feature, 0);
      if (feature instanceof EAttribute)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format(("Setting attribute value " + object + " to feature " + feature + " in instance " + instance));
        }

        eSet(feature, object);
      }
      else
      {
        // EReferences
        if (TRACER.isEnabled())
        {
          TRACER.format(("Adding object " + object + " to feature " + feature + " in instance " + instance));
        }

        int featureID = instance.eClass().getFeatureID(feature);
        Class<? extends Object> baseClass = object == null ? null : object.getClass();

        try
        {
          instance.eInverseAdd((InternalEObject)object, featureID, baseClass, null);
        }
        catch (NullPointerException e)
        {
          // TODO: Martin:quick hack, because there is still a problem with the feature id. Should investigate this soon
          instance.eSet(feature, object);
        }

        // Adjust opposite for transient opposite features
        EStructuralFeature.Internal internalFeature = (EStructuralFeature.Internal)feature;
        EReference oppositeReference = cdoID().isTemporary() ? null : internalFeature.getEOpposite();
        if (oppositeReference != null && object != null && !EMFUtil.isPersistent(oppositeReference))
        {
          adjustOppositeReference(instance, (InternalEObject)object, oppositeReference);
        }

        if (TRACER.isEnabled())
        {
          TRACER.format(("Added object " + object + " to feature " + feature + " in instance " + instance));
        }
      }
    }
  }

  private void adjustOppositeReference(InternalEObject instance, InternalEObject object, EReference oppositeReference)
  {
    boolean deliver = object.eDeliver(); // Disable notifications
    if (deliver)
    {
      object.eSetDeliver(false);
    }

    try
    {
      if (oppositeReference.isMany())
      {
        // TODO Martin: Is this enough??
        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>)object.eGet(oppositeReference);
        list.basicAdd(instance, null);
      }
      else
      {
        // TODO Martin: This only increases performance if getter is cheaper than setter. Should discuss this.
        if (object.eGet(oppositeReference) != instance)
        {
          object.eInverseAdd(instance, oppositeReference.getFeatureID(), ((EObject)instance).getClass(), null);
        }
      }
    }
    finally
    {
      if (deliver)
      {
        object.eSetDeliver(true);
      }
    }
  }

  private void clearList(EStructuralFeature feature, InternalEList<Object> list)
  {
    for (int i = list.size() - 1; i >= 0; --i)
    {
      InternalEObject obj = (InternalEObject)list.get(i);

      // TODO Clarify obj.getClass()/baseclass
      ((InternalEList<?>)list).basicRemove(obj, null);

      // TODO Martin: baseicRemove seems to be better than eInverseremove
      // instance.eInverseRemove(obj, featureID, obj.getClass(), null);
    }
  }

  /**
   * This method retrieves the value from the feature at the given index. It retrieves the value either from the views's
   * store or the internal pre-registration Map.
   * 
   * @param feature
   *          the feature to retireive the value from
   * @param index
   *          the given index of the object in the feature
   * @return the value from the feature at the given index
   */
  private Object getValueFromRevision(EStructuralFeature feature, int index)
  {
    Object object = revision.get(feature, index);
    if (object == null)
    {
      return null;
    }

    CDOType type = CDOModelUtil.getType(feature.getEType());
    object = type.convertToEMF(feature.getEType(), object);

    if (type == CDOType.OBJECT)
    {
      CDOID id = (CDOID)object;
      if (id.isNull())
      {
        return null;
      }

      object = getPreRegisteredObjects().get(id);
      if (object != null)
      {
        return ((CDOLegacyWrapper)object).cdoInternalInstance();
      }

      object = view.getObject(id);
      if (object instanceof CDOObjectWrapper)
      {
        return ((CDOObjectWrapper)object).cdoInternalInstance();
      }
    }

    return object;
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
    // TODO Change to direct call of eBasicSetContainer
    Method method = ReflectUtil.getMethod(instance.getClass(), "eBasicSetContainer", InternalEObject.class, int.class); //$NON-NLS-1$
    ReflectUtil.invokeMethod(method, instance, container, containerFeatureID);
  }

  protected void setInstanceValue(InternalEObject instance, EStructuralFeature feature, Object value)
  {
    instance.eSet(feature, value);
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
    if (getPreRegisteredObjects().get(potentialID) != null)
    {
      potentialID = ((CDOLegacyWrapper)getPreRegisteredObjects().get(potentialID)).instance;

      if (TRACER.isEnabled())
      {
        TRACER.format(("getting Object (" + potentialID + ") from localThread instead of the view"));
      }
    }
    else
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
   * TODO Martin: Can this be optimized?
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
    CDOPackageRegistry packageRegistry = cdoView().getSession().getPackageRegistry();
    EClass eClass = revision.getEClass();
    for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      if (feature instanceof EReference)
      {
        resolveProxies(feature, packageRegistry);
      }
    }
  }

  /*
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  protected void resolveProxies(EStructuralFeature feature, CDOPackageRegistry packageRegistry)
  {
    Object value = getInstanceValue(instance, feature, packageRegistry);
    if (value != null)
    {
      if (feature.isMany())
      {
        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>)value;
        int size = list.size();

        boolean deliver = instance.eDeliver();
        if (deliver)
        {
          instance.eSetDeliver(false);
        }

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
            // if (list instanceof
            // org.eclipse.emf.ecore.util.DelegatingInternalEList)
            // {
            // list =
            // ((org.eclipse.emf.ecore.util.DelegatingInternalEList)list).getDelegateInternalEList();
            // }

            // if (list instanceof NotifyingListImpl<?>)
            // {
            // ((NotifyingListImpl<Object>)list).basicSet(i, instance, null);
            // }
            // else
            // {
            list.set(i, instance);
            // }
          }
        }

        if (deliver)
        {
          instance.eSetDeliver(true);
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
    private static final Method getIDMethod = ReflectUtil.getMethod(LegacyProxy.class, "getID");

    private static final Method eIsProxyMethod = ReflectUtil.getMethod(EObject.class, "eIsProxy");

    private static final Method eProxyURIMethod = ReflectUtil.getMethod(InternalEObject.class, "eProxyURI");

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
        // Use the resource of the container because it's guaranteed to
        // be in the same CDOView as the resource
        // of the target!
        Resource resource = wrapper.eResource();

        // TODO Consider using a "fake" Resource implementation. See
        // Resource.getEObject(...)
        return resource.getURI().appendFragment(id.toURIFragment());
      }

      // A client must have invoked the proxy while being told not to do
      // so!
      throw new UnsupportedOperationException(method.getName());
    }
  }

  /**
   * @author Martin Fluegge
   */
  private static final class Counter
  {
    private int value;

    public Counter()
    {
    }

    public void increment()
    {
      ++value;
    }

    public int decrement()
    {
      return --value;
    }
  }
}
