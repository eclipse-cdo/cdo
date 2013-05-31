/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public boolean isSessionOpen()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isActivateOnOpen()
  {
    throw new UnsupportedOperationException();
  }

  public void setActivateOnOpen(boolean activateOnOpen)
  {
    throw new UnsupportedOperationException();
  }

  public String getUserID()
  {
    throw new UnsupportedOperationException();
  }

  public void setUserID(String userID)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator()
  {
    throw new UnsupportedOperationException();
  }

  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    throw new UnsupportedOperationException();
  }

  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isPassiveUpdateEnabled()
  {
    throw new UnsupportedOperationException();
  }

  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    throw new UnsupportedOperationException();
  }

  public PassiveUpdateMode getPassiveUpdateMode()
  {
    throw new UnsupportedOperationException();
  }

  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    throw new UnsupportedOperationException();
  }

  public LockNotificationMode getLockNotificationMode()
  {
    throw new UnsupportedOperationException();
  }

  public void setLockNotificationMode(LockNotificationMode mode)
  {
    throw new UnsupportedOperationException();
  }

  public CDOSession.ExceptionHandler getExceptionHandler()
  {
    throw new UnsupportedOperationException();
  }

  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler)
  {
    throw new UnsupportedOperationException();
  }

  public CDOIDGenerator getIDGenerator()
  {
    throw new UnsupportedOperationException();
  }

  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    throw new UnsupportedOperationException();
  }

  public CDOFetchRuleManager getFetchRuleManager()
  {
    throw new UnsupportedOperationException();
  }

  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
  {
    throw new UnsupportedOperationException();
  }

  public CDOBranchManager getBranchManager()
  {
    throw new UnsupportedOperationException();
  }

  public void setBranchManager(CDOBranchManager branchManager)
  {
    throw new UnsupportedOperationException();
  }
}
