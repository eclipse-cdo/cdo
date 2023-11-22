/*
 * Copyright (c) 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.spi.server.AbstractOperationAuthorizer;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class RealmOperationAuthorizer<T extends SecurityItem> extends AbstractOperationAuthorizer<ISession>
{
  private final Set<String> itemIDs;

  public RealmOperationAuthorizer(String operationID, Set<String> itemIDs)
  {
    super(operationID);
    this.itemIDs = itemIDs;
  }

  public final Set<String> getItemIDs()
  {
    return itemIDs;
  }

  @Override
  protected String authorizeOperation(ISession session, Map<String, Object> parameters)
  {
    IRepository repository = session.getManager().getRepository();
    ISecurityManager securityManager = SecurityManagerUtil.getSecurityManager(repository);
    if (securityManager == null)
    {
      return "No security manager";
    }

    Realm realm = securityManager.getRealm();
    if (realm == null)
    {
      return "No realm";
    }

    String userID = session.getUserID();
    if (userID == null)
    {
      return "No user ID";
    }

    User user = realm.getUser(userID);
    if (user == null)
    {
      return "User " + userID + " is not authenticated";
    }

    Collection<T> items = getItemsOfUser(user);
    for (T item : items)
    {
      if (itemIDs.contains(getID(item)))
      {
        return null;
      }
    }

    return "User " + userID + " is not authorized";
  }

  protected abstract Collection<T> getItemsOfUser(User user);

  protected abstract String getID(T item);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory<T extends SecurityItem> extends org.eclipse.emf.cdo.spi.server.AbstractOperationAuthorizer.Factory<ISession>
  {
    public Factory(String type)
    {
      super(type);
    }

    @Override
    protected RealmOperationAuthorizer<T> create(String operationID, String description) throws ProductCreationException
    {
      Set<String> itemIDs = new HashSet<>();

      if (description != null)
      {
        for (String itemID : description.split(","))
        {
          itemID = itemID.trim();
          if (itemID.length() != 0)
          {
            itemIDs.add(itemID);
          }
        }
      }

      return create(operationID, itemIDs);
    }

    protected abstract RealmOperationAuthorizer<T> create(String operationID, Set<String> itemIDs) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireUser extends RealmOperationAuthorizer<User>
  {
    public RequireUser(String operationID, Set<String> itemIDs)
    {
      super(operationID, itemIDs);
    }

    @Override
    protected Collection<User> getItemsOfUser(User user)
    {
      return Collections.singleton(user);
    }

    @Override
    protected String getID(User user)
    {
      return user.getId();
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends org.eclipse.emf.cdo.server.internal.security.RealmOperationAuthorizer.Factory<User>
    {
      public static final String TYPE = "requireUser";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected RequireUser create(String operationID, Set<String> itemIDs) throws ProductCreationException
      {
        return new RequireUser(operationID, itemIDs);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireGroup extends RealmOperationAuthorizer<Group>
  {
    public RequireGroup(String operationID, Set<String> itemIDs)
    {
      super(operationID, itemIDs);
    }

    @Override
    protected Collection<Group> getItemsOfUser(User user)
    {
      return user.getAllGroups();
    }

    @Override
    protected String getID(Group group)
    {
      return group.getId();
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends org.eclipse.emf.cdo.server.internal.security.RealmOperationAuthorizer.Factory<Group>
    {
      public static final String TYPE = "requireGroup";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected RequireGroup create(String operationID, Set<String> itemIDs) throws ProductCreationException
      {
        return new RequireGroup(operationID, itemIDs);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireRole extends RealmOperationAuthorizer<Role>
  {
    public RequireRole(String operationID, Set<String> itemIDs)
    {
      super(operationID, itemIDs);
    }

    @Override
    protected Collection<Role> getItemsOfUser(User user)
    {
      return user.getAllRoles();
    }

    @Override
    protected String getID(Role role)
    {
      return role.getId();
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends org.eclipse.emf.cdo.server.internal.security.RealmOperationAuthorizer.Factory<Role>
    {
      public static final String TYPE = "requireRole";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected RequireRole create(String operationID, Set<String> itemIDs) throws ProductCreationException
      {
        return new RequireRole(operationID, itemIDs);
      }
    }
  }
}
