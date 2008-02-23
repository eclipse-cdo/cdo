/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201997
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.CDOProtocolView;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public interface CDOView extends CDOProtocolView, INotifier
{
  public static final long UNSPECIFIED_DATE = CDORevision.UNSPECIFIED_DATE;

  public static final int NO_PRELOAD = 1;

  public CDOSession getSession();

  public ResourceSet getResourceSet();

  public boolean isDirty();

  public boolean hasConflict();

  public boolean isEnableInvalidationNotifications();

  public void setEnableInvalidationNotifications(boolean on);

  public int getLoadRevisionCollectionChunkSize();

  public void setLoadRevisionCollectionChunkSize(int loadRevisionCollectionChunkSize);

  /**
   * Returns the {@link CDORevision} that is stored in the {@link CDORevisionManager} associated with this view and that
   * is mapped for the given id. Note that, if this method is called in the context of a {@link CDOTransaction} and the
   * object with the given id is dirty, it returns the base revision of the session and not the dirty revision of the
   * transaction!
   */
  public CDORevision getRevision(CDOID id);

  public boolean hasResource(String path);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path);

  public CDOObject getObject(CDOID id);

  public CDOObject getObject(CDOID id, boolean loadOnDemand);

  public boolean isObjectRegistered(CDOID id);

  public int reload(CDOObject... objects);

  public void close();
}
