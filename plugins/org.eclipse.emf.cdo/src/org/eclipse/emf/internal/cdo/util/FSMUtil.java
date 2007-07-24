/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.ImplementationError;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.internal.cdo.CDOAdapterImpl;
import org.eclipse.emf.internal.cdo.CDOMetaImpl;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

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
    return state == CDOState.TRANSIENT || state == CDOState.PREPARED_ATTACH;
  }

  public static InternalCDOObject adapt(Object object, CDOViewImpl view)
  {
    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object instanceof EModelElement || object instanceof EGenericType)
    {
      InternalEObject eObject = (InternalEObject)object;
      if (eObject.eIsProxy())
      {
        eObject = (InternalEObject)EcoreUtil.resolve(eObject, view.getResourceSet());
      }

      CDOID id = view.getSession().lookupMetaInstanceID(eObject);
      if (id != null)
      {
        return new CDOMetaImpl(view, eObject, id);
      }
    }

    if (object instanceof InternalEObject)
    {
      EList<Adapter> adapters = ((InternalEObject)object).eAdapters();
      CDOAdapterImpl adapter = (CDOAdapterImpl)EcoreUtil.getAdapter(adapters, CDOAdapterImpl.class);
      if (adapter == null)
      {
        adapter = new CDOAdapterImpl();
        adapters.add(adapter);
      }

      return adapter;
    }

    throw new ImplementationError("Neither InternalCDOObject nor InternalEObject: " + object.getClass().getName());
  }

  public static Iterator<InternalCDOObject> iterator(Collection instances, final CDOViewImpl view)
  {
    final Iterator delegate = instances.iterator();
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
}
