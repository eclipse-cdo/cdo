/*
 * Copyright (c) 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.server.LDAPUserAuthenticator.LDAPDN;
import org.eclipse.emf.cdo.internal.server.LDAPUserAuthenticator.LDAPUserInfo;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.AbstractOperationAuthorizer;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class LDAPOperationAuthorizer extends AbstractOperationAuthorizer<ISession>
{
  private final Set<String> dns;

  public LDAPOperationAuthorizer(String operationID, Set<String> dns)
  {
    super(operationID);
    this.dns = dns;
  }

  public final Set<String> getDNs()
  {
    return dns;
  }

  @Override
  protected String authorizeOperation(ISession session, Map<String, Object> parameters)
  {
    IRepositoryProtector protector = session.getManager().getRepository().getProtector();
    if (protector == null)
    {
      return "No repository protector";
    }

    String userID = session.getUserID();
    if (userID == null)
    {
      return "No user ID";
    }

    LDAPUserInfo userInfo = (LDAPUserInfo)protector.getUserInfo(userID);
    if (userInfo == null)
    {
      return "User " + userID + " is not authenticated";
    }

    if (authorizeOperation(userInfo, dns))
    {
      return null;
    }

    return "User " + userID + " is not authorized";
  }

  protected abstract boolean authorizeOperation(LDAPUserInfo userInfo, Set<String> dns);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.emf.cdo.spi.server.AbstractOperationAuthorizer.Factory<ISession>
  {
    public Factory(String type)
    {
      super(type);
    }

    @Override
    protected LDAPOperationAuthorizer create(String operationID, String description) throws ProductCreationException
    {
      Set<String> dns = new HashSet<>();

      if (description != null)
      {
        for (String dn : description.split(","))
        {
          dn = dn.trim();
          if (dn.length() != 0)
          {
            dns.add(dn);
          }
        }
      }

      return create(operationID, dns);
    }

    protected abstract LDAPOperationAuthorizer create(String operationID, Set<String> dns) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireUser extends LDAPOperationAuthorizer
  {
    public RequireUser(String operationID, Set<String> dns)
    {
      super(operationID, dns);
    }

    @Override
    protected boolean authorizeOperation(LDAPUserInfo userInfo, Set<String> dns)
    {
      LDAPDN dn = userInfo.userDN();
      return dns.contains(dn);
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends LDAPOperationAuthorizer.Factory
    {
      public static final String TYPE = "ldapRequireUser";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected LDAPOperationAuthorizer create(String operationID, Set<String> dns) throws ProductCreationException
      {
        return new RequireUser(operationID, dns);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireGroup extends LDAPOperationAuthorizer
  {
    public RequireGroup(String operationID, Set<String> dns)
    {
      super(operationID, dns);
    }

    @Override
    protected boolean authorizeOperation(LDAPUserInfo userInfo, Set<String> dns)
    {
      for (LDAPDN dn : userInfo.groupDNs())
      {
        if (dns.contains(dn))
        {
          return true;
        }
      }

      return false;
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends LDAPOperationAuthorizer.Factory
    {
      public static final String TYPE = "ldapRequireGroup";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected LDAPOperationAuthorizer create(String operationID, Set<String> dns) throws ProductCreationException
      {
        return new RequireGroup(operationID, dns);
      }
    }
  }
}
