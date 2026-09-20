/*
 * Copyright (c) 2007-2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import java.util.Collection;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOFetchRuleManagerThreadLocal implements CDOFetchRuleManager
{
  private static final ThreadLocal<CDOFetchRuleManager> THREAD_LOCAL = new ThreadLocal<>();

  public CDOFetchRuleManagerThreadLocal()
  {
  }

  @Override
  public CDOID getContext()
  {
    CDOFetchRuleManager fetchRuleManager = CDOFetchRuleManagerThreadLocal.getCurrent();
    return fetchRuleManager != null ? fetchRuleManager.getContext() : null;
  }

  @Override
  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    CDOFetchRuleManager fetchRuleManager = CDOFetchRuleManagerThreadLocal.getCurrent();
    return fetchRuleManager != null ? fetchRuleManager.getFetchRules(ids) : null;
  }

  @Override
  @SuppressWarnings("deprecation")
  public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
  {
    CDOFetchRuleManager fetchRuleManager = CDOFetchRuleManagerThreadLocal.getCurrent();
    return fetchRuleManager != null ? fetchRuleManager.getCollectionLoadingPolicy() : null;
  }

  public static CDOFetchRuleManager getCurrent()
  {
    return THREAD_LOCAL.get();
  }

  public static void join(CDOFetchRuleManager fetchRuleManager)
  {
    THREAD_LOCAL.set(fetchRuleManager);
  }

  public static void leave()
  {
    THREAD_LOCAL.remove();
  }
}
