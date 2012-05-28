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
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.spi.security.IRoleProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ResourceRoleProvider implements IRoleProvider
{
  public ResourceRoleProvider()
  {
  }

  public void handleCommit(ISecurityManager securityManager, CommitContext commitContext)
  {
    // Do nothing
  }

  public Set<Role> getRoles(ISecurityManager securityManager, CDOBranchPoint securityContext,
      CDORevisionProvider revisionProvider, CDORevision revision, CDOPermission permission)
  {
    if (revisionProvider == null)
    {
      return null;
    }

    String path = CDORevisionUtil.getResourceNodePath(revision, revisionProvider);
    return getRoles(securityManager, path, permission);
  }

  private Set<Role> getRoles(ISecurityManager securityManager, String path, CDOPermission permission)
  {
    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends IRoleProvider.Factory
  {
    public static final String TYPE = "resource";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public ResourceRoleProvider create(String description) throws ProductCreationException
    {
      return new ResourceRoleProvider();
    }
  }
}
