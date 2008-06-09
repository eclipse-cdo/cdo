/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.internal.cdo.CDOViewImpl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Eike Stepper
 */
final class ProxyResolverResource implements Resource
{
  @SuppressWarnings("unused")
  private CDOViewImpl view;

  public ProxyResolverResource(CDOViewImpl view)
  {
    this.view = view;
  }

  /*
   * @ADDED Called by {@link ResourceSetImpl#getResource(URI, boolean)}
   */
  public boolean isLoaded()
  {
    return true;
  }

  /*
   * @ADDED Called by {@link ResourceSetImpl#getEObject(URI, boolean)}
   */
  public EObject getEObject(String uriFragment)
  {
    // TODO Implement me
    throw new UnsupportedOperationException("Not yet implemented");
    // CDOID id = CDOIDUtil.create(Long.parseLong(uriFragment));
    // InternalCDOObject object = view.getObject(id);
    // InternalEObject instance = object.cdoInternalInstance();
    // if (instance instanceof CDOAdapterImpl)
    // {
    // if (object.cdoState() == CDOState.PROXY)
    // {
    // object.cdoInternalPostLoad();
    // }
    // }
    //
    // return instance;
  }

  public TreeIterator<EObject> getAllContents()
  {
    throw new UnsupportedOperationException();
  }

  public EList<EObject> getContents()
  {
    throw new UnsupportedOperationException();
  }

  public EList<Diagnostic> getErrors()
  {
    throw new UnsupportedOperationException();
  }

  public ResourceSet getResourceSet()
  {
    throw new UnsupportedOperationException();
  }

  public URI getURI()
  {
    throw new UnsupportedOperationException();
  }

  public String getURIFragment(EObject object)
  {
    throw new UnsupportedOperationException();
  }

  public void delete(Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public long getTimeStamp()
  {
    throw new UnsupportedOperationException();
  }

  public void setTimeStamp(long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public EList<Diagnostic> getWarnings()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isModified()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isTrackingModification()
  {
    throw new UnsupportedOperationException();
  }

  public void load(Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void load(InputStream inputStream, Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void save(Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void save(OutputStream outputStream, Map<?, ?> options) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void setModified(boolean isModified)
  {
    throw new UnsupportedOperationException();
  }

  public void setTrackingModification(boolean isTrackingModification)
  {
    throw new UnsupportedOperationException();
  }

  public void setURI(URI uri)
  {
    throw new UnsupportedOperationException();
  }

  public void unload()
  {
    throw new UnsupportedOperationException();
  }

  public EList<Adapter> eAdapters()
  {
    throw new UnsupportedOperationException();
  }

  public boolean eDeliver()
  {
    throw new UnsupportedOperationException();
  }

  public void eNotify(Notification notification)
  {
    throw new UnsupportedOperationException();
  }

  public void eSetDeliver(boolean deliver)
  {
    throw new UnsupportedOperationException();
  }
}
