/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.ISecurityManager;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IRoleProvider
{
  public void handleCommit(ISecurityManager securityManager, CommitContext commitContext);

  public Set<Role> getRoles(ISecurityManager securityManager, CDOBranchPoint securityContext,
      CDORevisionProvider revisionProvider, CDORevision revision, CDOPermission permission);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.roleProviders";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public abstract IRoleProvider create(String description) throws ProductCreationException;
  }
}
