/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/246705
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.internal.cdo.CDOLegacyWrapper;
import org.eclipse.emf.internal.cdo.CDOMetaWrapper;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

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

  public static boolean isNew(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.NEW;
  }

  public static boolean isMeta(Object object)
  {
    return object instanceof EModelElement || object instanceof EGenericType
        || object instanceof org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
  }

  /**
   * @param view
   *          Only needed if object is a meta instance.
   */
  public static InternalCDOObject adapt(Object object, CDOView view)
  {
    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object == null)
    {
      throw new IllegalArgumentException("object == null");
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
      throw new IllegalArgumentException("view == null");
    }

    if (object.eIsProxy())
    {
      object = (InternalEObject)EcoreUtil.resolve(object, view.getResourceSet());
    }

    CDOID id = ((CDOViewImpl)view).getSession().lookupMetaInstanceID(object);
    if (id != null)
    {
      return new CDOMetaWrapper((CDOViewImpl)view, object, id);
    }

    return null;
  }

  /*
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  public static InternalCDOObject adaptLegacy(InternalEObject object)
  {
    EList<InternalEObject.EReadListener> readListeners = object.eReadListeners();
    CDOLegacyWrapper wrapper = getLegacyWrapper(readListeners);
    if (wrapper == null)
    {
      wrapper = new CDOLegacyWrapper(object);
      // TODO Only Load/Attach transitions should actually *add* the wrappers!
      readListeners.add(0, wrapper);
      object.eWriteListeners().add(0, wrapper);
    }

    return wrapper;
  }

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
  public static Object getLegacyWrapper(InternalEObject object)
  {
    return getLegacyWrapper(object.eReadListeners());
  }

  /**
   * Similar to {@link EcoreUtil#getAllProperContents(Resource, boolean)} except gives only one depth
   */
  public static Iterator<InternalCDOObject> getProperContents(final InternalCDOObject object)
  {
    final boolean isResource = object instanceof Resource;
    final CDOView cdoView = object.cdoView();
    final Iterator<EObject> delegate = object.eContents().iterator();

    return new Iterator<InternalCDOObject>()
    {
      private Object next;

      public boolean hasNext()
      {
        while (delegate.hasNext())
        {
          InternalEObject eObject = (InternalEObject)delegate.next();

          if (isResource || eObject.eDirectResource() == null)
          {
            next = adapt(eObject, cdoView);
            if (next instanceof InternalCDOObject)
            {
              return true;
            }
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

  public static Iterator<InternalCDOObject> iterator(final Iterator<?> delegate, final CDOViewImpl view)
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

  public static Iterator<InternalCDOObject> iterator(Collection<?> instances, final CDOViewImpl view)
  {
    return iterator(instances.iterator(), view);
  }
}
