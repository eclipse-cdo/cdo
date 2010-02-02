/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 246705
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOLegacyAdapter;
import org.eclipse.emf.internal.cdo.CDOLegacyWrapper;
import org.eclipse.emf.internal.cdo.CDOMetaWrapper;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public final class FSMUtil
{
  private FSMUtil()
  {
  }

  public static boolean isTransient(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.TRANSIENT || state == CDOState.PREPARED;
  }

  public static boolean isInvalid(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.INVALID || state == CDOState.INVALID_CONFLICT;
  }

  public static boolean isConflict(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.CONFLICT || state == CDOState.INVALID_CONFLICT;
  }

  public static boolean isNew(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.NEW;
  }

  public static boolean isMeta(Object object)
  {
    if (object instanceof EModelElement || object instanceof EGenericType)
    {
      EClass eClass = ((EObject)object).eClass();
      if (eClass == null)
      {
        return false;
      }

      EPackage ePackage = eClass.getEPackage();
      if (ePackage == null)
      {
        return false;
      }

      return ePackage.getNsURI() == EcorePackage.eNS_URI;
    }

    return object instanceof org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
  }

  public static boolean isWatchable(Object obj)
  {
    // Only CLEAN and DIRTY CDOObjects are watchable
    if (obj instanceof CDOObject)
    {
      CDOObject cdoObject = (CDOObject)obj;
      return cdoObject.cdoState() == CDOState.CLEAN || cdoObject.cdoState() == CDOState.DIRTY;
    }

    return false;
  }

  /**
   * @param view
   *          Only needed if object is a meta instance.
   */
  public static InternalCDOObject adapt(Object object, CDOView view)
  {
    if (view.isClosed())
    {
      throw new IllegalStateException(Messages.getString("FSMUtil.0")); //$NON-NLS-1$
    }

    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object == null)
    {
      throw new IllegalArgumentException(Messages.getString("FSMUtil.1")); //$NON-NLS-1$
    }

    if (isMeta(object))
    {
      return adaptMeta((InternalEObject)object, view);
    }

    if (object instanceof InternalEObject)
    {
      return adaptLegacy((InternalEObject)object);
    }

    return null;
  }

  public static InternalCDOObject adaptMeta(InternalEObject object, CDOView view)
  {
    if (view == null)
    {
      throw new IllegalArgumentException(Messages.getString("FSMUtil.2")); //$NON-NLS-1$
    }

    if (object.eIsProxy())
    {
      object = (InternalEObject)EcoreUtil.resolve(object, view.getResourceSet());
    }

    try
    {
      InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)view.getSession().getPackageRegistry();
      CDOID id = packageRegistry.getMetaInstanceMapper().lookupMetaInstanceID(object);
      return new CDOMetaWrapper((InternalCDOView)view, object, id);
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  /*
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  public static InternalCDOObject adaptLegacy(InternalEObject object)
  {
    if (object.getClass() == DynamicEObjectImpl.class)
    {
      throw new IllegalArgumentException(Messages.getString("FSMUtil.4") + object); //$NON-NLS-1$
    }

    EList<Adapter> adapters = object.eAdapters();
    CDOLegacyAdapter adapter = getLegacyAdapter(adapters);
    if (adapter == null)
    {
      adapter = new CDOLegacyAdapter();
      adapters.add(adapter);
    }

    return adapter;

    // EList<InternalEObject.EReadListener> readListeners = object.eReadListeners();
    // CDOLegacyWrapper wrapper = getLegacyWrapper(readListeners);
    // if (wrapper == null)
    // {
    // wrapper = new CDOLegacyWrapper(object);
    // // TODO Only Load/Attach transitions should actually *add* the wrappers!
    // readListeners.add(0, wrapper);
    // object.eWriteListeners().add(0, wrapper);
    // }
    //
    // return wrapper;
  }

  public static CDOLegacyAdapter getLegacyAdapter(EList<Adapter> adapters)
  {
    return (CDOLegacyAdapter)EcoreUtil.getAdapter(adapters, CDOLegacyAdapter.class);
  }

  @Deprecated
  public static CDOLegacyWrapper getLegacyWrapper(EList<?> listeners)
  {
    for (Object listener : listeners)
    {
      if (listener.getClass() == CDOLegacyWrapper.class)
      {
        return (CDOLegacyWrapper)listener;
      }
    }

    return null;
  }

  /*
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  @Deprecated
  public static Object getLegacyWrapper(InternalEObject object)
  {
    // TODO LEGACY
    throw new UnsupportedOperationException();
    // return getLegacyWrapper(object.eReadListeners());
  }

  public static void validate(CDOObject object, CDORevision revision)
  {
    if (revision == null)
    {
      CDOView view = object.cdoView();
      CDOStateMachine.INSTANCE.detachRemote((InternalCDOObject)object);
      throw new InvalidObjectException(object.cdoID(), view);
    }
  }

  public static void validate(CDOID id, CDORevision revision)
  {
    if (revision == null)
    {
      throw new ObjectNotFoundException(id);
    }
  }

  public static Iterator<InternalCDOObject> iterator(final Iterator<?> delegate, final InternalCDOView view)
  {
    return new Iterator<InternalCDOObject>()
    {
      private Object next;

      public boolean hasNext()
      {
        while (delegate.hasNext())
        {
          next = adapt(delegate.next(), view);
          if (next instanceof InternalCDOObject)
          {
            return true;
          }
        }

        return false;
      }

      public InternalCDOObject next()
      {
        return (InternalCDOObject)next;
      }

      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  public static Iterator<InternalCDOObject> iterator(Collection<?> instances, final InternalCDOView view)
  {
    return iterator(instances.iterator(), view);
  }
}
