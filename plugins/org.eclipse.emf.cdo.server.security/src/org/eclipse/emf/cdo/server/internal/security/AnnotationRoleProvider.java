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
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
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
public class AnnotationRoleProvider implements InternalSecurityManager.CommitHandler
{
  public static final String SOURCE_URI = "http://www.eclipse.org/CDO/Security";

  public static final String READ_KEY = "read";

  public static final String WRITE_KEY = "write";

  public static final String DELIMITERS = " ,;|";

  private final Set<InternalSecurityManager> initialized = new HashSet<InternalSecurityManager>();

  private final Map<EClass, EClassRoles> cache = new WeakHashMap<EClass, EClassRoles>();

  public AnnotationRoleProvider()
  {
  }

  private void initialize(InternalSecurityManager securityManager)
  {
    if (initialized.add(securityManager))
    {
      CDOPackageRegistry packageRegistry = securityManager.getRepository().getPackageRegistry();
      initialize(securityManager, packageRegistry.getPackageUnits());
    }
  }

  private void initialize(InternalSecurityManager securityManager, CDOPackageUnit[] packageUnits)
  {
    if (packageUnits != null && packageUnits.length != 0)
    {
      for (CDOPackageUnit packageUnit : packageUnits)
      {
        for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
        {
          EPackage ePackage = packageInfo.getEPackage();
          for (EClassifier eClassifier : ePackage.getEClassifiers())
          {
            if (eClassifier instanceof EClass)
            {
              EClass eClass = (EClass)eClassifier;
              initialize(securityManager, eClass, READ_KEY);
              initialize(securityManager, eClass, WRITE_KEY);
            }
          }
        }
      }
    }
  }

  private void initialize(InternalSecurityManager securityManager, EClass eClass, String key)
  {
    String annotation = EcoreUtil.getAnnotation(eClass, SOURCE_URI, key);
    if (annotation == null || annotation.length() == 0)
    {
      return;
    }

    EList<SecurityItem> items = securityManager.getRealm().getItems();

    StringTokenizer tokenizer = new StringTokenizer(annotation, DELIMITERS);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token != null && token.length() != 0)
      {
        Role role = getRole(securityManager.getRealm(), token);
        if (role == null)
        {
          role = SecurityFactory.eINSTANCE.createRole();
          role.setId(token);

          items.add(role);
        }
      }
    }
  }

  public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user)
  {
    initialize(securityManager);
    initialize(securityManager, commitContext.getNewPackageUnits());
  }

  private Set<Role> getRoles(InternalSecurityManager securityManager, CDOBranchPoint securityContext,
      CDORevisionProvider revisionProvider, CDORevision revision, CDOPermission permission)
  {
    initialize(securityManager);

    EClass eClass = revision.getEClass();
    return getRoles(securityManager, eClass, permission);
  }

  private Set<Role> getRoles(InternalSecurityManager securityManager, EClass eClass, CDOPermission permission)
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

  private Set<Role> getRoles(InternalSecurityManager securityManager, EClass eClass, String key)
  {
    String annotation = EcoreUtil.getAnnotation(eClass, SOURCE_URI, key);
    if (annotation == null || annotation.length() == 0)
    {
      return Collections.emptySet();
    }

    Set<Role> result = new HashSet<Role>();
    StringTokenizer tokenizer = new StringTokenizer(annotation, DELIMITERS);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token != null && token.length() != 0)
      {
        Role role = getRole(securityManager.getRealm(), token);
        result.add(role);
      }
    }

    return result;
  }

  private Role getRole(Realm realm, String roleID)
  {
    EList<SecurityItem> items = realm.getItems();
    Role role = RealmUtil.findRole(items, roleID);
    if (role == null)
    {
      throw new SecurityException("Role " + roleID + " not found");
    }

    return role;
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
}
