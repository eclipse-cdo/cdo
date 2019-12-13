/*
 * Copyright (c) 2011-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - bug 247226: Transparently support legacy models
 *    Christian W. Damus (CEA) - bug 397822: handling of REMOVE_MANY notifications
 *    Christian W. Damus (CEA) - bug 381395: don't notify closed view of adapter changes
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.DynamicValueHolder;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOLegacyAdapter extends CDOLegacyWrapper implements Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyAdapter.class);

  /**
   * @since 3.0
   */
  public CDOLegacyAdapter(InternalEObject object)
  {
    super(object);

    instance.eAdapters().add(this);
    ((org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList)instance.eAdapters()).addListener(new AdapterListListener());
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    instance = (InternalEObject)newTarget;
  }

  @Override
  public void unsetTarget(Notifier oldTarget)
  {
    if (instance == oldTarget)
    {
      instance = null;
    }
  }

  @Override
  public Notifier getTarget()
  {
    return instance;
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == CDOLegacyAdapter.class;
  }

  public static boolean extendedLegacyAttachmentChecks = true;

  @Override
  public void notifyChanged(Notification msg)
  {
    if (msg.isTouch() || msg instanceof CDONotification)
    {
      return;
    }

    EStructuralFeature feature = (EStructuralFeature)msg.getFeature();
    if (feature == null)
    {
      return;
    }

    if (viewAndState.view == null || !(viewAndState.view instanceof CDOTransaction))
    {
      return;
    }

    int featureID = eClass().getFeatureID(feature);
    boolean persistent = cdoClassInfo().isPersistent(featureID);

    if (persistent || extendedLegacyAttachmentChecks)
    {
      int eventType = msg.getEventType();
      int position = msg.getPosition();
      Object oldValue = msg.getOldValue();
      Object newValue = msg.getNewValue();

      switch (eventType)
      {
      case Notification.SET:
        notifySet(feature, persistent, position, oldValue, newValue);
        break;

      case Notification.UNSET:
        notifyUnset(feature, persistent, oldValue);
        break;

      case Notification.MOVE:
        notifyMove(feature, persistent, position, oldValue);
        break;

      case Notification.ADD:
        notifyAdd(feature, persistent, position, newValue);
        break;

      case Notification.ADD_MANY:
        notifyAddMany(feature, persistent, position, newValue);
        break;

      case Notification.REMOVE:
        notifyRemove(feature, persistent, position, oldValue);
        break;

      case Notification.REMOVE_MANY:
        // newValue will be null if the entire list was cleared.
        // Otherwise it is the array of indices that were removed
        if (newValue != null && !(newValue instanceof int[]))
        {
          throw new IllegalArgumentException("New value of REMOVE_MANY notification is not an array of indices.");
        }

        notifyRemoveMany(feature, persistent, (int[])newValue);
        break;
      }

      // Align Container for bidirectional references because this is not set in the store. See Bugzilla_246622_Test
      instanceToRevisionContainment();
    }
  }

  protected void notifySet(EStructuralFeature feature, boolean persistent, int position, Object oldValue, Object newValue)
  {
    CDOStore store = viewAndState.view.getStore();

    if (persistent)
    {
      // bug 405257: handle unsettable features set explicitly to null.
      // Note that an unsettable list feature doesn't allow individual
      // positions to be set/unset
      if (newValue == null && feature.isUnsettable() && position == Notification.NO_INDEX)
      {
        store.set(instance, feature, position, DynamicValueHolder.NIL);
      }
      else
      {
        store.set(instance, feature, position, newValue);
      }
    }

    if (feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      if (reference.isContainment())
      {
        if (oldValue != null)
        {
          InternalEObject oldChild = (InternalEObject)oldValue;
          setContainer(store, oldChild, null, InternalEObject.EOPPOSITE_FEATURE_BASE, persistent);
        }

        if (newValue != null)
        {
          InternalEObject newChild = (InternalEObject)newValue;
          setContainer(store, newChild, this, newChild.eContainerFeatureID(), persistent);
        }
      }
    }
  }

  protected void notifyUnset(EStructuralFeature feature, boolean persistent, Object oldValue)
  {
    CDOStore store = viewAndState.view.getStore();
    if (feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      if (reference.isContainment())
      {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>)oldValue;
        for (Object child : list)
        {
          if (child != null)
          {
            setContainer(store, (InternalEObject)child, null, InternalEObject.EOPPOSITE_FEATURE_BASE, persistent);
          }
        }
      }
    }

    if (persistent)
    {
      store.unset(instance, feature);
    }
  }

  protected void notifyMove(EStructuralFeature feature, boolean persistent, int position, Object oldValue)
  {
    if (persistent)
    {
      CDOStore store = viewAndState.view.getStore();
      store.move(instance, feature, position, (Integer)oldValue);
    }
  }

  protected void notifyAdd(EStructuralFeature feature, boolean persistent, int position, Object newValue)
  {
    CDOStore store = viewAndState.view.getStore();

    if (persistent)
    {
      store.add(instance, feature, position, newValue);
    }

    if (newValue != null && feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      if (reference.isContainment())
      {
        InternalEObject newChild = (InternalEObject)newValue;
        setContainer(store, newChild, this, newChild.eContainerFeatureID(), persistent);
      }
    }
  }

  protected void notifyAddMany(EStructuralFeature feature, boolean persistent, int position, Object newValue)
  {
    CDOStore store = viewAndState.view.getStore();
    int pos = position;

    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>)newValue;

    for (Object object : list)
    {
      if (persistent)
      {
        store.add(instance, feature, pos++, object);
      }

      if (object != null && feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          InternalEObject newChild = (InternalEObject)object;
          setContainer(store, newChild, this, newChild.eContainerFeatureID(), persistent);
        }
      }
    }
  }

  protected void notifyRemove(EStructuralFeature feature, boolean persistent, int position, Object oldValue)
  {
    CDOStore store = viewAndState.view.getStore();

    Object oldChild = null;
    if (persistent)
    {
      oldChild = store.remove(instance, feature, position);
    }
    else
    {
      oldChild = oldValue;
    }

    if (oldChild instanceof InternalEObject)
    {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          InternalEObject oldChildEObject = (InternalEObject)oldChild;
          setContainer(store, oldChildEObject, null, InternalEObject.EOPPOSITE_FEATURE_BASE, persistent);
        }
      }
    }
  }

  protected void notifyRemoveMany(EStructuralFeature feature, boolean persistent, int[] positions)
  {
    CDOStore store = viewAndState.view.getStore();

    if (positions == null)
    {
      // The list was cleared
      Object[] oldChildren = store.toArray(instance, feature);

      if (persistent)
      {
        store.clear(instance, feature);
      }

      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          for (int i = 0; i < oldChildren.length; i++)
          {
            Object oldChild = oldChildren[i];
            if (oldChild instanceof InternalEObject)
            {
              setContainer(store, (InternalEObject)oldChild, null, InternalEObject.EOPPOSITE_FEATURE_BASE, persistent);
            }
          }
        }
      }
    }
    else
    {
      Object[] oldChildren = null;
      if (!persistent)
      {
        oldChildren = store.toArray(instance, feature);
      }

      // Select indices were removed from the list
      for (int i = positions.length - 1; i >= 0; --i)
      {
        Object oldChild = null;
        if (persistent)
        {
          oldChild = store.remove(instance, feature, positions[i]);
        }
        else
        {
          oldChild = oldChildren[i];
        }

        if (oldChild instanceof InternalEObject)
        {
          if (feature instanceof EReference)
          {
            EReference reference = (EReference)feature;
            if (reference.isContainment())
            {
              InternalEObject oldChildEObject = (InternalEObject)oldChild;
              setContainer(store, oldChildEObject, null, InternalEObject.EOPPOSITE_FEATURE_BASE, persistent);
            }
          }
        }
      }
    }
  }

  private void setContainer(CDOStore store, InternalEObject object, InternalEObject container, int containingFeatureID, boolean featurePersistent)
  {
    if (object instanceof CDOObjectImpl)
    {
      // Don't touch native objects
      return;
    }

    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    boolean objectPersistent = !FSMUtil.isTransient(cdoObject);

    if (extendedLegacyAttachmentChecks)
    {
      Resource.Internal directResource = object.eDirectResource();

      if (container == null)
      {
        if (!(directResource instanceof CDOResource))
        {
          if (objectPersistent)
          {
            CDOStateMachine.INSTANCE.detach((InternalCDOObject)cdoObject);
            objectPersistent = false;
          }
        }
      }
      else
      {
        if (directResource == null || directResource instanceof CDOResource)
        {
          CDOObject cdoContainer = CDOUtil.getCDOObject(container);
          if (featurePersistent)
          {
            boolean containerPersistent = !FSMUtil.isTransient(cdoContainer);
            if (!containerPersistent)
            {
              featurePersistent = false;
            }
          }

          if (featurePersistent)
          {
            if (!objectPersistent)
            {
              InternalCDOTransaction transaction = (InternalCDOTransaction)cdoContainer.cdoView();
              CDOStateMachine.INSTANCE.attach((InternalCDOObject)cdoObject, transaction);
              objectPersistent = true;
            }
          }
        }
      }
    }

    if (!objectPersistent)
    {
      // Don't touch transient objects
      return;
    }

    store.setContainer(object, null, container, containingFeatureID);
  }

  /**
   * @author Martin Fluegge
   * @since 3.0
   */
  protected class AdapterListListener implements org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList.Listener
  {
    /**
     * @since 4.0
     */
    public AdapterListListener()
    {
    }

    @Override
    public void added(Notifier notifier, Adapter adapter)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Added : {0} to {1} ", adapter, CDOLegacyAdapter.this); //$NON-NLS-1$
      }

      if (!FSMUtil.isTransient(CDOLegacyAdapter.this))
      {
        InternalCDOView view = cdoView();
        if (view != null && view.isActive())
        {
          view.handleAddAdapter(CDOLegacyAdapter.this, adapter);
        }
      }
    }

    @Override
    public void removed(Notifier notifier, Adapter adapter)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removed : {0} from {1} ", adapter, CDOLegacyAdapter.this); //$NON-NLS-1$
      }

      if (!FSMUtil.isTransient(CDOLegacyAdapter.this))
      {
        InternalCDOView view = cdoView();
        if (view != null && view.isActive())
        {
          view.handleRemoveAdapter(CDOLegacyAdapter.this, adapter);
        }
      }
    }
  }
}
