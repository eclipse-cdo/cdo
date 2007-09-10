/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.internal.protocol.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.protocol.CDOID;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDODynamicFetchRuleManager implements CDOFetchRuleManager
{
  private static final ThreadLocal<CDOFetchRuleManager> threadLocal = new ThreadLocal<CDOFetchRuleManager>();

  public CDODynamicFetchRuleManager()
  {
  }

  public static void join(CDOFetchRuleManager fetchRulemanager)
  {
    threadLocal.set(fetchRulemanager);
  }

  public static CDOFetchRuleManager getCurrent()
  {
    return threadLocal.get();
  }

  public static void leave()
  {
    threadLocal.set(null);
  }

  public CDOID getContext()
  {
    CDOFetchRuleManager analyzer = CDODynamicFetchRuleManager.getCurrent();
    return analyzer != null ? analyzer.getContext() : null;
  }

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    CDOFetchRuleManager analyzer = CDODynamicFetchRuleManager.getCurrent();
    return analyzer != null ? analyzer.getFetchRules(ids) : null;
  }
}
