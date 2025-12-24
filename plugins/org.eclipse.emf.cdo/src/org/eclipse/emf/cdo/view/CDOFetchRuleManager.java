/*
 * Copyright (c) 2009-2012, 2014, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;

import java.util.Collection;
import java.util.List;

/**
 * Collects and updates {@link CDOFetchRule fetch rules} for {@link CDORevision revisions}, usually based on
 * {@link CDOFeatureAnalyzer feature analyzer} statistics.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOFetchRuleManager
{
  /**
   * Returns the context ID for which this fetch rule manager is collecting statistics.
   * <p>
   * A context-specific fetch rule manager collects statistics only for the objects that are
   * associated with the given context ID.
   * A context ID is typically the ID of a root object.
   * A fetch rule manager that is not context-specific returns {@link CDOID#NULL}.
   *
   * @return the context ID, or {@link CDOID#NULL} if this fetch rule manager is not context-specific.
   */
  public CDOID getContext();

  /**
   * Returns the fetch rules for the given object IDs.
   * <p>
   * The fetch rules are usually based on statistics collected by one or more {@link CDOFeatureAnalyzer feature
   * analyzers}. The fetch rules may be context-specific, that is, they may be based only on statistics
   * collected for objects that are associated with the context ID returned by {@link #getContext()}.
   * <p>
   * The returned list of fetch rules must not contain multiple rules for the same {@link CDOFetchRule#getEClass()
   * EClass}. If multiple rules for the same EClass are applicable to an object, the first applicable
   * rule in the list is used.
   * <p>
   * If no fetch rules are applicable to an object, the view's default fetch rule is used.
   * <p>
   * The returned list must not be modified by the caller.
   *
   * @param ids the IDs of the objects for which fetch rules are requested; note that the objects may not
   * exist or be accessible.
   * @return the fetch rules for the given object IDs, or <code>null</code> if no fetch rules are
   * applicable. The list may be empty.
   */
  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids);

  /**
   * Returns the collection loading policy that should be used when loading collections.
   *
   * @return the collection loading policy; or <code>null</code> to use {@link CDORevision#UNCHUNKED}.
   */
  public default CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
  {
    return null;
  }
}
