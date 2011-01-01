/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFetchRuleManager;

import java.util.Collection;
import java.util.List;

/**
 * TODO Simon: JavaDoc
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOFetchRuleManager
{
  /**
   * TODO Simon: JavaDoc
   */
  public static final CDOFetchRuleManager NOOP = new NOOPFetchRuleManager();

  /**
   * TODO Simon: JavaDoc
   */
  public CDOID getContext();

  /**
   * TODO Simon: JavaDoc
   */
  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids);

  /**
   * TODO Simon: JavaDoc
   * 
   * @since 2.0
   */
  public CDOCollectionLoadingPolicy getCollectionLoadingPolicy();
}
