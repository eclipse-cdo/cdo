/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision.cache;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDORevisionCache extends INotifier.Introspection
{
  public EClass getObjectType(CDOID id);

  public InternalCDORevision getRevision(CDOID id);

  public InternalCDORevision getRevisionByTime(CDOID id, long timeStamp);

  public InternalCDORevision getRevisionByVersion(CDOID id, int version);

  public InternalCDORevision removeRevision(CDOID id, int version);

  /**
   * Returns a list of {@link CDORevision revisions} that are current.
   */
  public List<CDORevision> getRevisions();

  public boolean addRevision(InternalCDORevision revision);

  public CDOID getResourceID(CDOID folderID, String name, long timeStamp);

  public void clear();

  /**
   * @author Eike Stepper
   */
  public interface EvictionEvent extends IEvent
  {
    public CDORevisionCache getCache();

    public CDOID getID();

    public int getVersion();

    public InternalCDORevision getRevision();
  }
}
