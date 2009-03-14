/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - delta support
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.ecore.EClass;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevision
{
  public static final long UNSPECIFIED_DATE = 0;

  /**
   * @since 2.0
   */
  public static final int UNSPECIFIED_VERSION = 0;

  public static final int UNCHUNKED = -1;

  public EClass getEClass();

  public CDOID getID();

  public int getVersion();

  public long getCreated();

  public long getRevised();

  public boolean isCurrent();

  public boolean isValid(long timeStamp);

  public boolean isTransactional();

  /**
   * @since 2.0
   */
  public boolean isResourceNode();

  /**
   * @since 2.0
   */
  public boolean isResourceFolder();

  public boolean isResource();

  /**
   * @since 2.0
   */
  public CDORevisionData data();

  public CDORevisionDelta compare(CDORevision origin);

  public void merge(CDORevisionDelta delta);

  /**
   * @since 2.0
   */
  public CDORevision copy();
}
