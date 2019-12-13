/*
 * Copyright (c) 2007-2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import java.util.Collection;
import java.util.List;

/**
 * @author Simon McDuff
 */
public abstract class NOOPFetchRuleManager implements CDOFetchRuleManager
{
  public NOOPFetchRuleManager()
  {
  }

  @Override
  public CDOID getContext()
  {
    return CDOID.NULL;
  }

  @Override
  public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
  {
    return null;
  }

  @Override
  public String toString()
  {
    return "NOOP"; //$NON-NLS-1$
  }
}
