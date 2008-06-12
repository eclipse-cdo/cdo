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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOClass;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public interface CDORevisionResolver
{
  public CDOIDObjectFactory getCDOIDObjectFactory();

  /**
   * @return The type of an object if a revision for that object is in the revision cache, <code>null</code> otherwise.
   */
  public CDOClass getObjectType(CDOID id);

  public boolean containsRevision(CDOID id);

  public boolean containsRevisionByTime(CDOID id, long timeStamp);

  public boolean containsRevisionByVersion(CDOID id, int version);

  public CDORevision getRevision(CDOID id, int referenceChunk);

  public CDORevision getRevision(CDOID id, int referenceChunk, boolean loadOnDemand);

  public CDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

  public CDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp, boolean loadOnDemand);

  public CDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version);

  public CDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version, boolean loadOnDemand);

  public List<CDORevision> getRevisions(Collection<CDOID> ids, int referenceChunk);

  public List<CDORevision> getRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp);

  public CDOID resolveReferenceProxy(CDOReferenceProxy referenceProxy);

  /**
   * Analyzing a list of values with respect to consecutive sequences of {@link CDOReferenceProxy} instances. A sequence
   * of reference proxies is considered consecutive if and only if the {@link CDOReferenceProxy#getIndex() ids} of each
   * proxy is the ids of its predecessor increased by one.
   * <p>
   * Implementation note: The implementation of this method should try to determine and deliver the longest possible
   * consecutive sequences.
   * 
   * @return An integer list of the range <b>sizes</b>. A positive integer denotes a range of non-proxies. A negative
   *         integer denotes a range of proxies. Ranges of zero size are not possible by definition.
   */
  public List<Integer> analyzeReferenceRanges(List<Object> list);
}
