/*
 * Copyright (c) 2007-2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Christian W. Damus - partial/conditional persistence of features
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo.PersistenceFilter;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOLockImpl;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList.Listener;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.DynamicValueHolder;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl.BasicEStoreFeatureMap;
import org.eclipse.emf.ecore.impl.MinimalEStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOLoadable;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOView.ViewAndState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The base class of all <em>native</em> {@link CDOObject objects}.
 *
 * @author Eike Stepper
 */
public class CDOObjectImpl extends MinimalEStoreEObjectImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectImpl.class);

  private static final EObservableAdapterList.Listener ADAPTERS_LISTENER = new EObservableAdapterList.Listener()
  {
    public void added(Notifier notifier, Adapter adapter)
    {
      CDOObjectImpl object = (CDOObjectImpl)notifier;
      object.eAdapterAdded(adapter);
    }

    public void removed(Notifier notifier, Adapter adapter)
    {
      CDOObjectImpl object = (CDOObjectImpl)notifier;
      object.eAdapterRemoved(adapter);
    }
  };

  private static final EObservableAdapterList.Listener[] ADAPTERS_LISTENERS = { ADAPTERS_LISTENER };

  private static final boolean EMF_TO_STRING = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.internal.cdo.CDOObjectImpl.emfToString");

  /**
   * Optimized storage of {@link CDOObject#cdoView()} and {@link CDOObject#cdoState()}.
   *
   * @see ViewAndState
   */
  private ViewAndState viewAndState = ViewAndState.TRANSIENT;

  /**
   * Optimized storage of {@link CDOObject#cdoID()} and {@link CDOObject#cdoRevision()}.
   * The idea is that, if a revision is set, the object's ID is equal to the {@link CDORevision revision's} ID.
   * The same is true for the classInfo field.
   */
  private InternalCDORevision revision;

  /**
   * Don't use the optional slot in MinimalEObject because a CDOObject always needs eSettings to store:
   * <ul>
   *  <li>the values of all features of transient objects,
   *  <li>the values of transient features of persistent objects and
   *  <li>the list wrappers of all many-valued features of all objects.
   * </ul>
   */
  private Object[] eSettings;

  public CDOObjectImpl()
  {
    initClassInfo(eStaticClass());
  }

  /**
   * @since 4.2
   */
  public final InternalCDOClassInfo cdoClassInfo()
  {
    return revision.getClassInfo();
  }

  public final CDOState cdoState()
  {
    return viewAndState.state;
  }

  /**
   * @since 2.0
   */
  public final InternalCDOView cdoView()
  {
    return viewAndState.view;
  }

  public final CDOID cdoID()
  {
    return revision.getID();
  }

  /**
   * @since 2.0
   */
  public final InternalCDORevision cdoRevision()
  {
    return revision.getProperRevision();
  }

  /**
   * @since 4.3
   */
  public final InternalCDORevision cdoRevision(boolean loadOnDemand)
  {
    InternalCDORevision revision = cdoRevision();
    if (revision == null && loadOnDemand)
    {
      revision = CDOStateMachine.INSTANCE.read(this);
    }

    return revision;
  }

  /**
   * @since 4.3
   */
  public final CDOPermission cdoPermission()
  {
    if (FSMUtil.isTransient(this))
    {
      return CDOPermission.WRITE;
    }

    return cdoRevision(true).getPermission();
  }

  public final CDOResource cdoResource()
  {
    Resource resource = eResource();
    if (resource instanceof CDOResource)
    {
      return (CDOResource)resource;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public final CDOResource cdoDirectResource()
  {
    Resource.Internal resource = eDirectResource();
    if (resource instanceof CDOResource)
    {
      return (CDOResource)resource;
    }

    return null;
  }

  /**
   * @since 3.0
   */
  public final void cdoPrefetch(int depth)
  {
    CDOID id = cdoID();
    viewAndState.view.prefetchRevisions(id, depth);
  }

  @Deprecated
  public final void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  /**
   * @since 4.2
   */
  public final CDOObjectHistory cdoHistory()
  {
    return viewAndState.view.getHistory(this);
  }

  /**
   * @since 2.0
   */
  public final boolean cdoConflict()
  {
    return FSMUtil.isConflict(this);
  }

  /**
   * @since 2.0
   */
  public final boolean cdoInvalid()
  {
    return FSMUtil.isInvalid(this);
  }

  /**
   * @since 2.0
   */
  public final CDOLock cdoReadLock()
  {
    return createLock(this, LockType.READ);
  }

  /**
   * @since 2.0
   */
  public final CDOLock cdoWriteLock()
  {
    return createLock(this, LockType.WRITE);
  }

  /**
   * @since 4.1
   */
  public final CDOLock cdoWriteOption()
  {
    return createLock(this, LockType.OPTION);
  }

  /**
   * @since 4.1
   */
  public final CDOLockState cdoLockState()
  {
    return getLockState(this);
  }

  public final CDOState cdoInternalSetState(CDOState state)
  {
    CDOState oldState = viewAndState.state;
    if (oldState != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this); //$NON-NLS-1$
      }

      viewAndState = viewAndState.getViewAndState(state);
      if (viewAndState.view != null)
      {
        viewAndState.view.handleObjectStateChanged(this, oldState, state);
      }

      return oldState;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public final void cdoInternalSetView(CDOView view)
  {
    InternalCDOView newView = (InternalCDOView)view;
    if (newView != null)
    {
      viewAndState = newView.getViewAndState(viewAndState.state);
    }
    else
    {
      viewAndState = ViewAndState.TRANSIENT.getViewAndState(viewAndState.state);
    }
  }

  public final void cdoInternalSetID(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id); //$NON-NLS-1$
    }

    if (id == null)
    {
      revision = cdoClassInfo().getRevisionForID(null);
    }
    else
    {
      revision = revision.getRevisionForID(id);
    }
  }

  /**
   * @since 2.0
   */
  public final void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision); //$NON-NLS-1$
    }

    if (revision == null)
    {
      InternalCDOClassInfo classInfo = cdoClassInfo();
      CDOID id = this.revision.getID();
      this.revision = classInfo.getRevisionForID(id);
    }
    else
    {
      if (this.revision != null)
      {
        CDOID objectID = this.revision.getID();
        if (objectID != null && objectID != revision.getID())
        {
          throw new IllegalArgumentException("The revision " + revision + " does not match the object " + objectID);
        }
      }

      this.revision = (InternalCDORevision)revision;
    }
  }

  public final void cdoInternalSetResource(CDOResource resource)
  {
    // Unsets direct resource and/or eContainer.
    // Only intended to be called by CDOTransactionImpl.removeObject(CDOID, CDOObject).
    // See bug 383370.

    if (resource != null)
    {
      throw new IllegalArgumentException("Only intended to be called by CDOTransactionImpl.removeObject(CDOID, CDOObject");
    }

    super.eSetDirectResource(null);
    eBasicSetContainer(null);
    eBasicSetContainerFeatureID(0);
  }

  /**
   * @since 2.0
   */
  public void cdoInternalPreLoad()
  {
    // Do nothing
  }

  public final void cdoInternalPostLoad()
  {
    // Reset EMAP objects
    Object[] eSettings = eBasicSettings();
    if (eSettings != null)
    {
      // Make sure transient features are kept but persisted values are not cached.
      InternalCDOClassInfo classInfo = cdoClassInfo();
      int featureCount = classInfo.getEClass().getFeatureCount();
      for (int i = 0; i < featureCount; i++)
      {
        // We need to keep the existing list if possible.
        int index = classInfo.getSettingsFeatureIndex(i);
        if (index != InternalCDOClassInfo.NO_SLOT)
        {
          Object value = eSettings[index];
          if (value instanceof InternalCDOLoadable)
          {
            ((InternalCDOLoadable)value).cdoInternalPostLoad();
          }
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public final void cdoInternalPostInvalidate()
  {
    cdoInternalSetRevision(null);
  }

  /**
   * @since 4.7
   */
  public void cdoInternalPreAttach()
  {
    // Do nothing
  }

  public final void cdoInternalPostAttach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating revision for {0}", this); //$NON-NLS-1$
    }

    InternalEObject eContainer = eBasicInternalContainer();
    int eContainerFeatureID = eBasicContainerFeatureID();

    InternalCDORevision revision = cdoRevision();
    revision.setContainerID(eContainer == null ? CDOID.NULL : viewAndState.view.convertObjectToID(eContainer, true));
    revision.setContainingFeatureID(eContainerFeatureID);

    Resource directResource = eDirectResource();
    if (directResource instanceof CDOResource)
    {
      CDOResource cdoResource = (CDOResource)directResource;
      revision.setResourceID(cdoResource.cdoID());
    }

    if (eSettings != null)
    {
      InternalCDOClassInfo classInfo = cdoClassInfo();
      EStructuralFeature[] allPersistentFeatures = classInfo.getAllPersistentFeatures();
      int length = allPersistentFeatures.length;
      for (int i = 0; i < length; i++)
      {
        EStructuralFeature eFeature = allPersistentFeatures[i];
        int transientIndex = classInfo.getTransientFeatureIndex(eFeature);
        Object setting = eSettings[transientIndex]; // Can be safely accessed directly because we come from TRANSIENT

        instanceToRevisionFeature(viewAndState.view, this, eFeature, setting);
      }

      cdoRevision().setUnchunked();

      int newSize = classInfo.getSettingsFeatureCount();
      if (newSize != eSettings.length)
      {
        Object[] newSettings = new Object[newSize];
        System.arraycopy(eSettings, 0, newSettings, 0, newSize);
        eSettings = newSettings;
      }
    }
  }

  /**
   * It is really important for accessing the data to go through {@link #cdoStore()}. {@link #eStore()} will redirect
   * you to the transient data.
   *
   * @since 2.0
   */
  public void cdoInternalPostDetach(boolean remote)
  {
    if (remote)
    {
      eSetDeliver(false);
      eBasicSetContainer(null, eContainerFeatureID());
      eSetDeliver(true);
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating revision for {0}", this); //$NON-NLS-1$
    }

    InternalCDOClassInfo classInfo = cdoClassInfo();

    CDOStore store = cdoStore();
    super.eSetDirectResource((Resource.Internal)store.getResource(this));
    eBasicSetContainer(store.getContainer(this));
    eBasicSetContainerFeatureID(store.getContainingFeatureID(this));

    if (eSettings != null)
    {
      int newSize = classInfo.getSettingsFeatureCount() + classInfo.getTransientFeatureCount();
      if (newSize != eSettings.length)
      {
        Object[] newSettings = new Object[newSize];
        System.arraycopy(eSettings, 0, newSettings, 0, eSettings.length);
        eSettings = newSettings;
      }
    }

    InternalCDORevision revision = cdoRevision();
    EStructuralFeature[] allPersistentFeatures = classInfo.getAllPersistentFeatures();
    for (EStructuralFeature eFeature : allPersistentFeatures)
    {
      revisionToInstanceFeature(this, revision, eFeature);
    }
  }

  /**
   * @since 3.0
   */
  public final void cdoInternalPostRollback()
  {
    // Do nothing
  }

  public final void cdoInternalPreCommit()
  {
    // Do nothing
  }

  public final InternalEObject cdoInternalInstance()
  {
    return this;
  }

  @Deprecated
  public final EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    throw new UnsupportedOperationException();
  }

  private CDOStore cdoStore()
  {
    return viewAndState.view.getStore();
  }

  /**
   * @since 2.0
   */
  @Override
  public final EStore eStore()
  {
    if (FSMUtil.isTransient(this))
    {
      return TransientStore.INSTANCE;
    }

    return cdoStore();
  }

  @Override
  protected final EClass eDynamicClass()
  {
    EClass eClass = eClass();
    if (eClass == eStaticClass())
    {
      return null;
    }

    return eClass;
  }

  @Override
  public final EClass eClass()
  {
    return revision.getEClass();
  }

  @Override
  public void eSetClass(EClass eClass)
  {
    initClassInfo(eClass);
  }

  /**
   * @since 2.0
   */
  @Override
  public Resource.Internal eDirectResource()
  {
    if (FSMUtil.isTransient(this))
    {
      return super.eDirectResource();
    }

    return (Resource.Internal)cdoStore().getResource(this);
  }

  @Override
  public final Resource.Internal eInternalResource()
  {
    CDOView view = cdoView();
    if (view != null && view.isClosed())
    {
      return null;
    }

    if (FSMUtil.isInvalid(this))
    {
      return null;
    }

    return super.eInternalResource();
  }

  @Override
  public final Object dynamicGet(int dynamicFeatureID)
  {
    InternalCDOClassInfo classInfo = cdoClassInfo();
    int index = classInfo.getSettingsFeatureIndex(dynamicFeatureID);
    if (index == InternalCDOClassInfo.NO_SLOT)
    {
      // The feature has no slot in eSettings, i.e., it's persistent or single-valued.
      // Delegate to the store. TransientStore delegates back to eSettings.
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      EStore eStore = eStore();
      return eStore.get(this, eStructuralFeature, EStore.NO_INDEX);
    }

    // Here we know that the feature is transient or many-valued, hence it has a slot in eSettings.
    Object[] eSettings = eBasicSettings();
    Object result = eSettings[index];
    if (result == null)
    {
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      if (classInfo.isPersistent(dynamicFeatureID))
      {
        if (FeatureMapUtil.isFeatureMap(eStructuralFeature))
        {
          eSettings[index] = result = createFeatureMap(eStructuralFeature);
        }
        else if (eStructuralFeature.isMany())
        {
          eSettings[index] = result = createList(eStructuralFeature);
        }
      }
    }

    return result;
  }

  @Override
  public final void dynamicSet(int dynamicFeatureID, Object value)
  {
    InternalCDOClassInfo classInfo = cdoClassInfo();
    int index = classInfo.getSettingsFeatureIndex(dynamicFeatureID);
    if (index == InternalCDOClassInfo.NO_SLOT)
    {
      // The feature has no slot in eSettings, i.e., it's persistent or single-valued.
      // Delegate to the store. TransientStore delegates back to eSettings.
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      EStore eStore = eStore();
      eStore.set(this, eStructuralFeature, EStore.NO_INDEX, value);
    }
    else
    {
      Object[] eSettings = eBasicSettings();
      eSettings[index] = value;
    }
  }

  @Override
  public final void dynamicUnset(int dynamicFeatureID)
  {
    InternalCDOClassInfo classInfo = cdoClassInfo();
    int index = classInfo.getSettingsFeatureIndex(dynamicFeatureID);
    if (index == InternalCDOClassInfo.NO_SLOT)
    {
      // The feature has no slot in eSettings, i.e., it's persistent or single-valued.
      // Delegate to the store. TransientStore delegates back to eSettings.
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      EStore eStore = eStore();
      eStore.unset(this, eStructuralFeature);
    }
    else
    {
      Object[] eSettings = eBasicSettings();
      Object oldValue = eSettings[index];
      if (oldValue != null)
      {
        if (oldValue instanceof InternalEList)
        {
          InternalEList<?> list = (InternalEList<?>)oldValue;
          if (list instanceof InternalEList.Unsettable)
          {
            ((InternalEList.Unsettable<?>)list).unset();
          }
          else
          {
            list.clear();
          }
        }
        else
        {
          eSettings[index] = null;
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected final boolean eDynamicIsSet(int dynamicFeatureID, EStructuralFeature eFeature)
  {
    if (dynamicFeatureID < 0)
    {
      return eOpenIsSet(eFeature);
    }

    InternalCDOClassInfo classInfo = cdoClassInfo();
    if (classInfo.isPersistent(dynamicFeatureID))
    {
      return eStore().isSet(this, eFeature);
    }

    return eSettingDelegate(eFeature).dynamicIsSet(this, eSettings(), dynamicFeatureID);
  }

  @Override
  public final InternalEObject eInternalContainer()
  {
    if (FSMUtil.isTransient(this) || FSMUtil.isInvalid(this))
    {
      return eBasicInternalContainer();
    }

    return cdoStore().getContainer(this);
  }

  @Override
  public final int eContainerFeatureID()
  {
    if (FSMUtil.isTransient(this) || FSMUtil.isInvalid(this))
    {
      return eBasicContainerFeatureID();
    }

    return cdoStore().getContainingFeatureID(this);
  }

  /**
   * Code taken from {@link BasicEObjectImpl#eBasicSetContainer} and modified to detect whether the object is moved in the
   * same context. (E.g.: An object is moved from resA to resB. resA and resB belongs to the same CDORepositoryInfo.
   * Without this special handling, a detach and newObject will be generated for the object moved)
   *
   * @since 2.0
   */
  @Override
  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID, NotificationChain msgs)
  {
    InternalEObject oldContainer = eInternalContainer();
    Resource.Internal oldResource = eDirectResource();
    Resource.Internal newResource = null;
    EReference newContainmentFeature = null;

    if (oldResource != null)
    {
      if (newContainer != null)
      {
        newContainmentFeature = eContainmentFeature(this, newContainer, newContainerFeatureID);
        if (!newContainmentFeature.isResolveProxies())
        {
          msgs = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, msgs);
          eSetDirectResource(null);
          newResource = newContainer.eInternalResource();
        }
        else
        {
          oldResource = null;
        }
      }
      else
      {
        oldResource = null;
      }
    }
    else
    {
      if (oldContainer != null)
      {
        oldResource = oldContainer.eInternalResource();
      }

      if (newContainer != null)
      {
        newResource = newContainer.eInternalResource();
      }
    }

    CDOView oldView = viewAndState.view;
    CDOView newView = newResource instanceof CDOResource ? ((CDOResource)newResource).cdoView() : null;
    boolean movedWithinView = oldView != null && oldView == newView;

    boolean oldAttached = viewAndState.state != CDOState.TRANSIENT;
    boolean newAttached = newContainer != null && newView != null ? FSMUtil.adapt(newContainer, newView).cdoState() != CDOState.TRANSIENT : true;

    if (newAttached)
    {
      if (newContainmentFeature == null && newContainer != null)
      {
        newContainmentFeature = eContainmentFeature(this, newContainer, newContainerFeatureID);
      }

      if (newContainmentFeature != null && !EMFUtil.isPersistent(newContainmentFeature))
      {
        newAttached = false;
      }
    }

    if (newAttached != oldAttached)
    {
      movedWithinView = false;
    }

    if (oldResource != null)
    {
      if (oldResource instanceof CDOResource)
      {
        if (oldAttached && !movedWithinView && !isRootResource())
        {
          oldResource.detached(this);
        }
      }
      else if (oldResource != newResource)
      {
        // Non-CDO resources may always expect the detached() call.
        oldResource.detached(this);
      }
    }

    int oldContainerFeatureID = eContainerFeatureID();
    eBasicSetContainer(newContainer, newContainerFeatureID);

    if (newResource != null)
    {
      if (newResource instanceof CDOResource)
      {
        if (newAttached && !movedWithinView)
        {
          newResource.attached(this);
        }
      }
      else if (oldResource != newResource)
      {
        newResource.attached(this);
      }
    }

    if (newContainer != null && newContainer != oldContainer && this instanceof CDOResourceNodeImpl)
    {
      ((CDOResourceNodeImpl)this).recacheURIs();
    }

    if (eNotificationRequired())
    {
      if (oldContainer != null && oldContainerFeatureID >= 0 && oldContainerFeatureID != newContainerFeatureID)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, oldContainerFeatureID, oldContainer, null);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }

      if (newContainerFeatureID >= 0)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, newContainerFeatureID,
            oldContainerFeatureID == newContainerFeatureID ? oldContainer : null, newContainer);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
    }

    return msgs;
  }

  /**
   * Code taken from {@link BasicEObjectImpl#eSetResource} and modified to detect when objects are moved in the same
   * context.
   *
   * @since 2.0
   */
  @Override
  public final NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    Resource.Internal oldResource = eDirectResource();

    CDOView oldView = viewAndState.view;
    CDOView newView = resource != null && resource instanceof CDOResource ? ((CDOResource)resource).cdoView() : null;

    boolean isSameView;
    if (viewAndState.state == CDOState.NEW)
    {
      isSameView = false;
    }
    else
    {
      isSameView = oldView != null && oldView == newView;
    }

    if (oldResource != null && resource != null)
    {
      notifications = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, notifications);

      // When setting the resource to null we assume that detach has already been called in the resource implementation
      if (!isSameView)
      {
        oldResource.detached(this);
      }
    }

    InternalEObject oldContainer = eInternalContainer();
    if (oldContainer != null && !isSameView)
    {
      if (eContainmentFeature().isResolveProxies())
      {
        Resource.Internal oldContainerResource = oldContainer.eInternalResource();
        if (oldContainerResource != null)
        {
          // If we're not setting a new resource, attach it to the old container's resource.
          if (resource == null)
          {
            oldContainerResource.attached(this);
          }

          // If we didn't detach it from an old resource already, detach it from the old container's resource.
          //
          else if (oldResource == null)
          {
            oldContainerResource.detached(this);
          }
        }
      }
      else
      {
        notifications = eBasicRemoveFromContainer(notifications);
        notifications = eBasicSetContainer(null, -1, notifications);
      }
    }

    eSetDirectResource(resource);

    return notifications;
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    if (FSMUtil.isTransient(this))
    {
      super.eSetDirectResource(resource);
    }
    else if (resource == null || resource instanceof CDOResource)
    {
      cdoStore().setContainer(this, (CDOResource)resource, eInternalContainer(), eContainerFeatureID());
    }
    else
    {
      throw new IllegalArgumentException(Messages.getString("CDOObjectImpl.8")); //$NON-NLS-1$
    }
  }

  @Override
  protected final void eBasicSetContainer(InternalEObject newEContainer, int newContainerFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting container: {0}, featureID={1}", newEContainer, newContainerFeatureID); //$NON-NLS-1$
    }

    if (FSMUtil.isTransient(this) || FSMUtil.isInvalid(this))
    {
      super.eBasicSetContainer(newEContainer, newContainerFeatureID);
    }
    else
    {
      CDOStore cdoStore = cdoStore();
      cdoStore.setContainer(this, cdoDirectResource(), newEContainer, newContainerFeatureID);
    }
  }

  @Override
  protected final int eDynamicFeatureID(EStructuralFeature eStructuralFeature)
  {
    // CDOObjectImpl has no static features, so don't subract their count here:
    EClass eClass = eClass();
    return eClass.getFeatureID(eStructuralFeature);
  }

  @Override
  protected final EStructuralFeature eDynamicFeature(int dynamicFeatureID)
  {
    // CDOObjectImpl has no static features, so don't add their count here:
    EClass eClass = eClass();
    return eClass.getEStructuralFeature(dynamicFeatureID);
  }

  @Override
  protected final DynamicValueHolder eSettings()
  {
    if (!eHasSettings())
    {
      InternalCDOClassInfo classInfo = cdoClassInfo();
      int size = classInfo.getSettingsFeatureCount();
      if (FSMUtil.isTransient(this))
      {
        size += classInfo.getTransientFeatureCount();
      }

      if (size != 0)
      {
        eBasicSetSettings(new Object[size]);
      }
    }

    return this;
  }

  @Override
  protected final boolean eHasSettings()
  {
    return eSettings != null;
  }

  @Override
  protected final Object[] eBasicSettings()
  {
    return eSettings;
  }

  @Override
  protected final void eBasicSetSettings(Object[] settings)
  {
    eSettings = settings;
  }

  /**
   * Don't cache non-transient features in this CDOObject's {@link #eSettings()}.
   */
  @Override
  protected final boolean eIsCaching()
  {
    return false;
  }

  @Override
  protected final Adapter[] eContainerAdapterArray()
  {
    if (FSMUtil.isTransient(this))
    {
      return super.eContainerAdapterArray();
    }

    InternalCDOView view = cdoView();
    if (view.isClosed())
    {
      return null;
    }

    CDOObject container;

    InternalCDORevision revision = cdoRevision();
    if (revision != null)
    {
      Object containerID = revision.getContainerID();

      if (containerID instanceof CDOID)
      {
        container = view.getObject((CDOID)containerID, false);
      }
      else
      {
        container = (CDOObject)containerID;
      }

      if (container != null)
      {
        if (container instanceof CDOObjectImpl)
        {
          return ((CDOObjectImpl)container).eBasicAdapterArray();
        }

        if (container instanceof BasicEObjectImpl)
        {
          return super.eContainerAdapterArray();
        }
      }
    }

    return null;
  }

  /**
   * This method is not called by the MinimalEStoreEObjectImpl in CDO's ecore.minimal (retrofitting) fragment
   * but it is called by the normal MinimalEObjectImpl as of EMF 2.9.
   *
   * @since 4.2
   */
  @Override
  protected final Listener[] eBasicAdapterListeners()
  {
    Listener[] listeners = super.eBasicAdapterListeners();
    if (listeners == null)
    {
      return ADAPTERS_LISTENERS;
    }

    return listeners;
  }

  /**
   * This method is not called by the MinimalEStoreEObjectImpl in CDO's ecore.minimal (retrofitting) fragment
   * but it is called by the normal MinimalEObjectImpl as of EMF 2.9.
   *
   * @since 4.2
   */
  @Override
  protected final void eBasicSetAdapterListeners(Listener[] eAdapterListeners)
  {
    if (eAdapterListeners != null)
    {
      if (eAdapterListeners.length == 1)
      {
        // Because noone else can remove ADAPTERS_LISTENER this must be it
        eAdapterListeners = null;
      }
    }

    super.eBasicSetAdapterListeners(eAdapterListeners);
  }

  /**
   * This method must not be private because the MinimalEStoreEObjectImpl in CDO's ecore.minimal (retrofitting) fragment
   * introduces it as a protected method and calls it.
   *
   * @since 4.2
   */
  protected final void eAdapterAdded(Adapter adapter)
  {
    if (!FSMUtil.isTransient(this))
    {
      viewAndState.view.handleAddAdapter(this, adapter);
    }
  }

  /**
   * This method must not be private because the MinimalEStoreEObjectImpl in CDO's ecore.minimal (retrofitting) fragment
   * introduces it as a protected method and calls it.
   *
   * @since 4.2
   */
  protected final void eAdapterRemoved(Adapter adapter)
  {
    if (!FSMUtil.isTransient(this))
    {
      viewAndState.view.handleRemoveAdapter(this, adapter);
    }
  }

  /**
   * Specializing the behavior of {@link #hashCode()} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final int hashCode()
  {
    return super.hashCode();
  }

  /**
   * Specializing the behavior of {@link #equals(Object)} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public String toString()
  {
    if (EMF_TO_STRING)
    {
      return super.toString();
    }

    String str = eClass().getName();

    CDOID id = cdoID();
    if (id != null)
    {
      str += "@" + id;
    }

    CDOState cdoState = cdoState();
    if (cdoState != CDOState.CLEAN)
    {
      str += "[" + cdoState + "]";
    }

    return str;
  }

  @Override
  protected final FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreFeatureMap(eStructuralFeature);
  }

  @Override
  protected EList<?> createList(EStructuralFeature eStructuralFeature)
  {
    if (isMap(eStructuralFeature))
    {
      return createMap(eStructuralFeature);
    }

    if (!eStructuralFeature.isOrdered())
    {
      return createUnorderedList(eStructuralFeature);
    }

    return super.createList(eStructuralFeature);
  }

  /**
   * @since 4.1
   */
  protected final CDOStoreUnorderedEList<Object> createUnorderedList(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreUnorderedEList<Object>(eStructuralFeature);
  }

  /**
   * @since 4.1
   */
  protected final CDOStoreEcoreEMap createMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreEcoreEMap(eStructuralFeature);
  }

  private boolean isMap(EStructuralFeature eStructuralFeature)
  {
    // Java ensures that string constants are interned, so == is actually more efficient than equals().
    return eStructuralFeature.getEType().getInstanceClassName() == "java.util.Map$Entry"; //$NON-NLS-1$
  }

  private boolean isRootResource()
  {
    return this instanceof CDOResource && ((CDOResource)this).isRoot();
  }

  private void initClassInfo(EClass eClass)
  {
    InternalCDOClassInfo classInfo = (InternalCDOClassInfo)CDOModelUtil.getClassInfo(eClass);
    revision = classInfo.getRevisionForID(null);
  }

  /**
   * Adjust the reference ONLY if the opposite reference used CDOID. This is true ONLY if the state of <cdo>this</code>
   * was not {@link CDOState#NEW}.
   */
  private static void adjustOppositeReference(InternalCDOObject instance, InternalEObject opposite, EReference oppositeReference)
  {
    if (opposite != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(opposite);
      if (cdoObject != null && !FSMUtil.isTransient(cdoObject))
      {
        if (oppositeReference.isMany())
        {
          EStore eStore = cdoObject.eStore();
          int index = eStore.indexOf(cdoObject, oppositeReference, instance.cdoID());
          if (index != -1)
          {
            eStore.set(cdoObject, oppositeReference, index, instance);
          }
        }
        else
        {
          EStore eStore = cdoObject.eStore();
          eStore.set(cdoObject, oppositeReference, 0, instance);
        }
      }
      else
      {
        if (oppositeReference.isResolveProxies())
        {
          // We should not trigger events. But we have no choice :-(.
          if (oppositeReference.isMany())
          {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>)opposite.eGet(oppositeReference);
            int index = list.indexOf(instance);
            if (index != -1)
            {
              list.set(index, instance);
            }
          }
          else
          {
            opposite.eSet(oppositeReference, instance);
          }
        }
      }
    }
  }

  /**
   * @since 3.0
   */
  public static void instanceToRevisionFeature(InternalCDOView view, InternalCDOObject object, EStructuralFeature feature, Object setting)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating feature {0}", feature); //$NON-NLS-1$
    }

    PersistenceFilter filter = ((InternalCDOClassInfo)CDOModelUtil.getClassInfo(feature.getEContainingClass())).getPersistenceFilter(feature);
    if (filter != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Filtering value of feature {0}", feature); //$NON-NLS-1$
      }

      setting = filter.getPersistableValue(object, setting);
    }

    CDOStore cdoStore = view.getStore();
    InternalCDORevision revision = object.cdoRevision();

    if (feature.isMany())
    {
      if (setting != null)
      {
        @SuppressWarnings("unchecked")
        EList<Object> instanceList = (EList<Object>)setting;
        int size = instanceList.size();

        if (feature.isUnsettable())
        {
          if (!object.eIsSet(feature))
          {
            // Avoid list creation for unset lists.
            return;
          }
        }
        else
        {
          if (size == 0)
          {
            // Avoid list creation for empty lists that can't be unset.
            return;
          }
        }

        // Get (and possibly create) the list here in order to support unsettable empty lists.
        CDOList revisionList = revision.getOrCreateList(feature, size);

        for (Object value : instanceList)
        {
          Object cdoValue = cdoStore.convertToCDO(object, feature, value);
          revisionList.add(cdoValue);
        }
      }
    }
    else
    {
      Object cdoValue = cdoStore.convertToCDO(object, feature, setting);
      revision.set(feature, 0, cdoValue);
    }
  }

  /**
   * @since 2.0
   */
  public static void revisionToInstanceFeature(InternalCDOObject instance, InternalCDORevision revision, EStructuralFeature eFeature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating feature {0}", eFeature); //$NON-NLS-1$
    }

    EStructuralFeature.Internal internalFeature = (EStructuralFeature.Internal)eFeature;
    InternalCDOView view = instance.cdoView();
    EReference oppositeReference = view.isObjectNew(instance.cdoID()) ? null : internalFeature.getEOpposite();

    CDOStore cdoStore = view.getStore();
    EStore eStore = instance.eStore();

    if (eFeature.isMany())
    {
      int size = cdoStore.size(instance, eFeature);
      for (int index = 0; index < size; index++)
      {
        // Do not trigger events.
        // Do not trigger inverse updates.
        Object opposite = cdoStore.get(instance, eFeature, index);
        eStore.add(instance, eFeature, index, opposite);
        if (oppositeReference != null)
        {
          adjustOppositeReference(instance, (InternalEObject)opposite, oppositeReference);
        }
      }
    }
    else
    {
      Object opposite = cdoStore.get(instance, eFeature, EStore.NO_INDEX);
      eStore.set(instance, eFeature, EStore.NO_INDEX, opposite);
      if (oppositeReference != null)
      {
        adjustOppositeReference(instance, (InternalEObject)opposite, oppositeReference);
      }
    }
  }

  /**
   * @since 4.1
   */
  public static CDOLock createLock(InternalCDOObject object, LockType type)
  {
    if (FSMUtil.isTransient(object))
    {
      throw new IllegalStateException("Call CDOView.lockObjects() for transient object " + object);
    }

    return new CDOLockImpl(object, type);
  }

  /**
   * @since 4.1
   */
  public static CDOLockState getLockState(InternalCDOObject object)
  {
    if (FSMUtil.isTransient(object))
    {
      return null;
    }

    InternalCDOView view = object.cdoView();
    CDOID id = object.cdoID();

    return view.getLockStates(Collections.singletonList(id))[0];
  }

  /**
   * Implements an internal EStore for TRANSIENT objects, where there is no view or {@link CDOStore}.
   * <p>
   * Feature values are actually stored in {@link CDOObjectImpl#cdoSettings}.
   *
   * @author Simon McDuff
   * @since 2.0
   */
  private static final class TransientStore implements InternalEObject.EStore
  {
    public static TransientStore INSTANCE = new TransientStore();

    private TransientStore()
    {
    }

    private Object getValue(InternalEObject eObject, int transientIndex)
    {
      Object[] settings = getSettings(eObject);
      return settings[transientIndex];
    }

    private EList<Object> getValueAsList(InternalEObject eObject, int transientIndex)
    {
      Object[] settings = getSettings(eObject);

      @SuppressWarnings("unchecked")
      EList<Object> result = (EList<Object>)settings[transientIndex];
      if (result == null)
      {
        result = new BasicEList<Object>();
        settings[transientIndex] = result;
      }

      return result;
    }

    private Object setValue(InternalEObject eObject, int transientIndex, Object newValue)
    {
      Object[] settings = getSettings(eObject);
      Object oldValue = settings[transientIndex];
      settings[transientIndex] = newValue;
      return oldValue;
    }

    private Object[] getSettings(InternalEObject eObject)
    {
      CDOObjectImpl object = (CDOObjectImpl)eObject;
      object.eSettings(); // Ensure that the array is created
      return object.eSettings;
    }

    private int getTransientFeatureIndex(InternalEObject eObject, EStructuralFeature feature)
    {
      CDOObjectImpl object = (CDOObjectImpl)eObject;
      InternalCDOClassInfo classInfo = object.cdoClassInfo();
      return classInfo.getTransientFeatureIndex(feature);
    }

    public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      if (index != NO_INDEX)
      {
        return getValueAsList(eObject, transientIndex).get(index);
      }

      return getValue(eObject, transientIndex);
    }

    public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      if (index != NO_INDEX)
      {
        return getValueAsList(eObject, transientIndex).set(index, value);
      }

      return setValue(eObject, transientIndex, value);
    }

    public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      getValueAsList(eObject, transientIndex).add(index, value);
    }

    public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).remove(index);
    }

    public Object move(InternalEObject eObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).move(targetIndex, sourceIndex);
    }

    public void clear(InternalEObject eObject, EStructuralFeature feature)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      if (feature.isMany())
      {
        getValueAsList(eObject, transientIndex).clear();
      }

      setValue(eObject, transientIndex, null);
    }

    public int size(InternalEObject eObject, EStructuralFeature feature)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).size();
    }

    public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).indexOf(value);
    }

    public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).lastIndexOf(value);
    }

    public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).toArray();
    }

    public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] array)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).toArray(array);
    }

    public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).isEmpty();
    }

    public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).contains(value);
    }

    public int hashCode(InternalEObject eObject, EStructuralFeature feature)
    {
      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return getValueAsList(eObject, transientIndex).hashCode();
    }

    public InternalEObject getContainer(InternalEObject eObject)
    {
      return null;
    }

    public EStructuralFeature getContainingFeature(InternalEObject eObject)
    {
      throw new UnsupportedOperationException("Should never be called");
    }

    public EObject create(EClass eClass)
    {
      throw new UnsupportedOperationException("Should never be called");
    }

    public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
    {
      if (!feature.isUnsettable())
      {
        if (feature.isMany())
        {
          @SuppressWarnings("unchecked")
          List<Object> list = (InternalEList<Object>)eObject.eGet(feature, false);
          return list != null && !list.isEmpty();
        }

        return !ObjectUtil.equals(eObject.eGet(feature, false), feature.getDefaultValue());
      }

      Object[] settings = ((CDOObjectImpl)eObject).eSettings;
      if (settings == null)
      {
        return false;
      }

      int transientIndex = getTransientFeatureIndex(eObject, feature);
      return settings[transientIndex] != null;
    }

    public void unset(InternalEObject eObject, EStructuralFeature feature)
    {
      CDOObjectImpl cdoObject = (CDOObjectImpl)eObject;

      if (feature.isMany())
      {
        // Object object = get(eObject, feature, NO_INDEX);
        Object object = cdoObject.eGet(feature, false);
        if (object instanceof List<?>)
        {
          List<?> list = (List<?>)object;
          list.clear();
        }
      }
      else
      {

        Object[] settings = cdoObject.eSettings;
        if (settings == null)
        {
          // Is already unset
          return;
        }

        int transientIndex = getTransientFeatureIndex(eObject, feature);
        settings[transientIndex] = null;
      }
    }
  }

  /**
   * For internal use only.
   *
   * @author Eike Stepper
   * @since 4.1
   */
  private final class CDOStoreEcoreEMap extends EcoreEMap<Object, Object> implements InternalCDOLoadable
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreEcoreEMap(EStructuralFeature eStructuralFeature)
    {
      super((EClass)eStructuralFeature.getEType(), BasicEMap.Entry.class, null);
      delegateEList = new EStoreEObjectImpl.BasicEStoreEList<BasicEMap.Entry<Object, Object>>(CDOObjectImpl.this, eStructuralFeature)
      {
        private static final long serialVersionUID = 1L;

        // {
        // ensureEntryDataExists();
        // }

        @Override
        public void unset()
        {
          super.unset();
          doClear();
        }

        @Override
        protected void didAdd(int index, BasicEMap.Entry<Object, Object> newObject)
        {
          CDOStoreEcoreEMap.this.doPut(newObject);
        }

        @Override
        protected void didSet(int index, BasicEMap.Entry<Object, Object> newObject, BasicEMap.Entry<Object, Object> oldObject)
        {
          didRemove(index, oldObject);
          didAdd(index, newObject);
        }

        @Override
        protected void didRemove(int index, BasicEMap.Entry<Object, Object> oldObject)
        {
          CDOStoreEcoreEMap.this.doRemove(oldObject);
        }

        @Override
        protected void didClear(int size, Object[] oldObjects)
        {
          CDOStoreEcoreEMap.this.doClear();
          // ensureEntryDataExists();
        }

        @Override
        protected void didMove(int index, BasicEMap.Entry<Object, Object> movedObject, int oldIndex)
        {
          CDOStoreEcoreEMap.this.doMove(movedObject);
        }
      };

      size = delegateEList.size();
    }

    private void checkListForReading()
    {
      if (!FSMUtil.isTransient(CDOObjectImpl.this))
      {
        CDOStateMachine.INSTANCE.read(CDOObjectImpl.this);
      }
    }

    /**
     * Ensures that the entry data is created and is populated with contents of the delegate list.
     */
    @Override
    protected synchronized void ensureEntryDataExists()
    {
      checkListForReading();
      super.ensureEntryDataExists();
    }

    @Override
    public int size()
    {
      checkListForReading();
      return size;
    }

    @Override
    public boolean isEmpty()
    {
      checkListForReading();
      return size == 0;
    }

    @Override
    public boolean contains(Object object)
    {
      checkListForReading();
      return super.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
      checkListForReading();
      return super.containsAll(collection);
    }

    @Override
    public boolean containsKey(Object key)
    {
      checkListForReading();
      return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
      checkListForReading();
      return super.containsValue(value);
    }

    public void cdoInternalPreLoad()
    {
    }

    public void cdoInternalPostLoad()
    {
      entryData = null;
      size = delegateEList.size();
    }
  }

  /**
   * For internal use only.
   *
   * @author Andras Peteri
   * @since 4.1
   */
  private final class CDOStoreUnorderedEList<E> extends EStoreEObjectImpl.BasicEStoreEList<E>
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreUnorderedEList(EStructuralFeature feature)
    {
      super(CDOObjectImpl.this, feature);
    }

    @Override
    public E remove(int index)
    {
      boolean oldObjectIsLast = index == size() - 1;
      E oldObject = super.remove(index);

      if (!oldObjectIsLast)
      {
        move(index, size() - 1);
      }

      return oldObject;
    }
  }

  /**
   * For internal use only.
   *
   * @author Eike Stepper
   */
  private final class CDOStoreFeatureMap extends BasicEStoreFeatureMap
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreFeatureMap(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }
  }
}
