/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/201997
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;

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

  public boolean hasUniqueResourceContents();

  /**
   * Specifies whether the contents list of resources will be unique or not.
   * <p>
   * This property is transient in that it does not stick with resources outside of the scope of this view. Especially
   * it will not be persisted with resources in the repository. Each new view will start with <code>true</code> as a
   * default value. Changing to <code>false</code> will subsequently apply to all resources being loaded or created.
   * <p>
   * Notice that the resource contents is a containment list and as such <b>must be</b> unique. Setting this property to
   * <code>false</code> is only recommended for performance optimization when uniqueness is granted by other means.
   * Violating the uniqueness constraint will result in unpredictable behaviour and possible corruption of the
   * repository!
   */
  public void setUniqueResourceContents(boolean uniqueResourceContents);

  public boolean isInvalidationNotificationsEnabled();

  public void setInvalidationNotificationsEnabled(boolean invalidationNotificationsEnabled);

  public int getLoadRevisionCollectionChunkSize();

  public void setLoadRevisionCollectionChunkSize(int loadRevisionCollectionChunkSize);

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
