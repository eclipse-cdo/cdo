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
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.spi.security.IRoleProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class AnnotationRoleProvider implements IRoleProvider
{
  public static final String SOURCE_URI = "http://www.eclipse.org/CDO/Security";

  public static final String READ_KEY = "read";

  public static final String WRITE_KEY = "write";

  private final Map<EClass, EClassRoles> cache = new WeakHashMap<EClass, EClassRoles>();

  public AnnotationRoleProvider()
  {
  }

  public Set<Role> getRoles(ISecurityManager securityManager, CDOBranchPoint securityContext,
      CDORevisionProvider revisionProvider, CDORevision revision, CDOPermission permission)
  {
    EClass eClass = revision.getEClass();
    return getRoles(securityManager, eClass, permission);
  }

  private Set<Role> getRoles(ISecurityManager securityManager, EClass eClass, CDOPermission permission)
  {
    EClassRoles eClassRoles = cache.get(eClass);
    if (eClassRoles == null)
    {
      eClassRoles = new EClassRoles();
      cache.put(eClass, eClassRoles);
    }

    switch (permission)
    {
    case READ:
      Set<Role> readRoles = eClassRoles.getReadRoles();
      if (readRoles == null)
      {
        readRoles = getRoles(securityManager, eClass, READ_KEY);
        eClassRoles.setReadRoles(readRoles);
      }

      return readRoles;

    case WRITE:
      Set<Role> writeRoles = eClassRoles.getWriteRoles();
      if (writeRoles == null)
      {
        writeRoles = getRoles(securityManager, eClass, WRITE_KEY);
        eClassRoles.setWriteRoles(writeRoles);
      }

      return writeRoles;

    default:
      throw new IllegalStateException("Illegal permission: " + permission);
    }
  }

  private Set<Role> getRoles(ISecurityManager securityManager, EClass eClass, String key)
  {
    String annotation = EcoreUtil.getAnnotation(eClass, SOURCE_URI, key);
    if (annotation == null || annotation.length() == 0)
    {
      return Collections.emptySet();
    }

    Set<Role> result = new HashSet<Role>();
    StringTokenizer tokenizer = new StringTokenizer(annotation, " ,;|");
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token != null && token.length() != 0)
      {
        Role role = securityManager.getRole(token);
        result.add(role);
      }
    }

    return result;
  }

  /**
   * @author Eike Stepper
   */
  private static class EClassRoles
  {
    private Set<Role> readRoles;

    private Set<Role> writeRoles;

    public EClassRoles()
    {
    }

    public Set<Role> getReadRoles()
    {
      return readRoles;
    }

    public void setReadRoles(Set<Role> readRoles)
    {
      this.readRoles = readRoles;
    }

    public Set<Role> getWriteRoles()
    {
      return writeRoles;
    }

    public void setWriteRoles(Set<Role> writeRoles)
    {
      this.writeRoles = writeRoles;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends IRoleProvider.Factory
  {
    public static final String TYPE = "annotation";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public AnnotationRoleProvider create(String description) throws ProductCreationException
    {
      return new AnnotationRoleProvider();
    }
  }
}
