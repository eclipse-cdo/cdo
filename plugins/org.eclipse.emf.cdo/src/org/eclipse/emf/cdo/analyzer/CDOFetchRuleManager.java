/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.analyzer;

import org.eclipse.emf.cdo.common.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFetchRuleManager;

import java.util.Collection;
import java.util.List;

/**
 * @author Simon McDuff
 */
public interface CDOFetchRuleManager
{
  public static final CDOFetchRuleManager NOOP = new NOOPFetchRuleManager();

  public CDOID getContext();

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids);

  public int getLoadRevisionCollectionChunkSize();
}
