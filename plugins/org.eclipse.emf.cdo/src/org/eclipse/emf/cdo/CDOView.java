/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - EMF invalidation notifications
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 * @author Simon McDuff
 */
public interface CDOView extends INotifier
{
  public static final long UNSPECIFIED_DATE = CDORevision.UNSPECIFIED_DATE;

  public static final int NO_PRELOAD = 1;

  public int getViewID();

  public Type getViewType();

  public CDOSession getSession();

  public ResourceSet getResourceSet();

  public boolean isDirty();

  public boolean hasConflict();

  public boolean isEnableInvalidationNotifications();

  public void setEnableInvalidationNotifications(boolean on);

  public int getLoadRevisionCollectionChunkSize();

  public void setLoadRevisionCollectionChunkSize(int loadRevisionCollectionChunkSize);

  /**
   * @see ResourceSet#getResource(URI, boolean)
   */
  public CDOResource getResource(String path);

  public CDOObject getObject(CDOID id);

  public CDORevision getRevision(CDOID id);

  public boolean isObjectRegistered(CDOID id);

  public void close();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    TRANSACTION, READONLY, AUDIT
  }
}
