/*
 * Copyright (c) 2011-2013, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * @author Eike Stepper
 */
public abstract class TestSessionConfiguration extends Notifier implements CDOSessionConfiguration
{
  @Override
  public boolean isSessionOpen()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isActivateOnOpen()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setActivateOnOpen(boolean activateOnOpen)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getUserID()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setUserID(String userID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] getOneTimeLoginToken()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setOneTimeLoginToken(byte[] oneTimeLoginToken)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOSession openSession()
  {
    return null;
  }

  @Override
  public boolean isPassiveUpdateEnabled()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public PassiveUpdateMode getPassiveUpdateMode()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LockNotificationMode getLockNotificationMode()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLockNotificationMode(LockNotificationMode mode)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOSession.ExceptionHandler getExceptionHandler()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOIDGenerator getIDGenerator()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOFetchRuleManager getFetchRuleManager()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOBranchManager getBranchManager()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setBranchManager(CDOBranchManager branchManager)
  {
    throw new UnsupportedOperationException();
  }
}
