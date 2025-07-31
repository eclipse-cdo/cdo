/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.server.FileUserAuthenticator.FileUserInfo;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.AbstractOperationAuthorizer;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public abstract class FileOperationAuthorizer extends AbstractOperationAuthorizer<ISession>
{
  private final Set<String> values;

  public FileOperationAuthorizer(String operationID, Set<String> values)
  {
    super(operationID);
    this.values = values;
  }

  public final Set<String> getValues()
  {
    return values;
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

    FileUserInfo userInfo = (FileUserInfo)protector.getUserInfo(userID);
    if (userInfo == null)
    {
      return "User " + userID + " is not authenticated";
    }

    if (authorizeOperation(userInfo, values))
    {
      return null;
    }

    return "User " + userID + " is not authorized";
  }

  protected abstract boolean authorizeOperation(FileUserInfo userInfo, Set<String> values);

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
    protected FileOperationAuthorizer create(String operationID, String description) throws ProductCreationException
    {
      Set<String> values = new HashSet<>();

      if (description != null)
      {
        for (String value : description.split(","))
        {
          value = value.trim();
          if (value.length() != 0)
          {
            values.add(value);
          }
        }
      }

      return create(operationID, values);
    }

    protected abstract FileOperationAuthorizer create(String operationID, Set<String> values) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireUser extends FileOperationAuthorizer
  {
    public RequireUser(String operationID, Set<String> values)
    {
      super(operationID, values);
    }

    @Override
    protected boolean authorizeOperation(FileUserInfo userInfo, Set<String> values)
    {
      String userID = userInfo.userID();
      return values.contains(userID);
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends FileOperationAuthorizer.Factory
    {
      public static final String TYPE = "fileRequireUser";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected FileOperationAuthorizer create(String operationID, Set<String> values) throws ProductCreationException
      {
        return new RequireUser(operationID, values);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireAdmin extends FileOperationAuthorizer
  {
    private final boolean administrator;

    public RequireAdmin(String operationID, Set<String> values)
    {
      super(operationID, null);

      if (ObjectUtil.isEmpty(values))
      {
        administrator = true;
      }
      else
      {
        administrator = StringUtil.isTrue(values.iterator().next());
      }
    }

    @Override
    protected boolean authorizeOperation(FileUserInfo userInfo, Set<String> values)
    {
      return userInfo.administrator() == administrator;
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends FileOperationAuthorizer.Factory
    {
      public static final String TYPE = "fileRequireAdmin";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected FileOperationAuthorizer create(String operationID, Set<String> values) throws ProductCreationException
      {
        return new RequireAdmin(operationID, values);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RequireGroup extends FileOperationAuthorizer
  {
    private static final String GROUPS_ATTRIBUTE = "groups";

    public RequireGroup(String operationID, Set<String> values)
    {
      super(operationID, values);
    }

    @Override
    protected boolean authorizeOperation(FileUserInfo userInfo, Set<String> values)
    {
      String groupsAttribute = userInfo.attributes().get(GROUPS_ATTRIBUTE);
      if (!StringUtil.isEmpty(groupsAttribute))
      {
        StringTokenizer tokenizer = new StringTokenizer(groupsAttribute, ",");
        while (tokenizer.hasMoreTokens())
        {
          String group = tokenizer.nextToken().trim();
          if (values.contains(group))
          {
            return true;
          }
        }
      }

      return false;
    }

    /**
     * @author Eike Stepper
     */
    public static final class Factory extends FileOperationAuthorizer.Factory
    {
      public static final String TYPE = "fileRequireGroup";

      public Factory()
      {
        super(TYPE);
      }

      @Override
      protected FileOperationAuthorizer create(String operationID, Set<String> values) throws ProductCreationException
      {
        return new RequireGroup(operationID, values);
      }
    }
  }
}
