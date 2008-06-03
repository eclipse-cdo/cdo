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
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.common.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.common.id.CDOID;

import java.util.Collection;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class NOOPFetchRuleManager implements CDOFetchRuleManager
{
  public NOOPFetchRuleManager()
  {
  }

  public CDOID getContext()
  {
    return CDOID.NULL;
  }

  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    return null;
  }

  public int getLoadRevisionCollectionChunkSize()
  {
    return 1;
  }
}
