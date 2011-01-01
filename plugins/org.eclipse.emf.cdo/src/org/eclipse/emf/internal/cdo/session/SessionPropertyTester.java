/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.core.expressions.PropertyTester;

/**
 * @author Eike Stepper
 */
public class SessionPropertyTester extends PropertyTester
{
  public SessionPropertyTester()
  {
  }

  public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
  {
    CDOSession session = (CDOSession)receiver;
    if ("sessionID".equals(property)) //$NON-NLS-1$
    {
      int expected = (Integer)expectedValue;
      return session.getSessionID() == expected;
    }

    if ("userID".equals(property)) //$NON-NLS-1$
    {
      String expected = (String)expectedValue;
      return ObjectUtil.equals(session.getUserID(), expected);
    }

    if ("passiveUpdateEnabled".equals(property)) //$NON-NLS-1$
    {
      boolean expected = (Boolean)expectedValue;
      return session.options().isPassiveUpdateEnabled() == expected;
    }

    if ("passiveUpdateMode".equals(property)) //$NON-NLS-1$
    {
      String expected = (String)expectedValue;
      return ObjectUtil.equals(session.options().getPassiveUpdateMode().toString(), expected);
    }

    if ("supportingAudits".equals(property)) //$NON-NLS-1$
    {
      boolean expected = (Boolean)expectedValue;
      return session.getRepositoryInfo().isSupportingAudits() == expected;
    }

    if ("supportingBranches".equals(property)) //$NON-NLS-1$
    {
      boolean expected = (Boolean)expectedValue;
      return session.getRepositoryInfo().isSupportingBranches() == expected;
    }

    if ("repositoryName".equals(property)) //$NON-NLS-1$
    {
      String expected = (String)expectedValue;
      return ObjectUtil.equals(session.getRepositoryInfo().getName(), expected);
    }

    if ("repositoryUUID".equals(property)) //$NON-NLS-1$
    {
      String expected = (String)expectedValue;
      return ObjectUtil.equals(session.getRepositoryInfo().getUUID(), expected);
    }

    if ("repositoryType".equals(property)) //$NON-NLS-1$
    {
      String expected = (String)expectedValue;
      return ObjectUtil.equals(session.getRepositoryInfo().getType().toString(), expected);
    }

    if ("repositoryState".equals(property)) //$NON-NLS-1$
    {
      String expected = (String)expectedValue;
      return ObjectUtil.equals(session.getRepositoryInfo().getState().toString(), expected);
    }

    if ("repositoryCreationTime".equals(property)) //$NON-NLS-1$
    {
      long expected = (Long)expectedValue;
      return session.getRepositoryInfo().getCreationTime() == expected;
    }

    return false;
  }
}
