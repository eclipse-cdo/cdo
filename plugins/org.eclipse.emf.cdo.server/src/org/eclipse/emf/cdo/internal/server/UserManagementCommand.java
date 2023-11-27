/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator.PasswordChangeSupport;
import org.eclipse.emf.cdo.spi.server.CDOCommand;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.IUserManagement;
import org.eclipse.net4j.util.security.SecurityUtil;

/**
 * @author Eike Stepper
 */
public abstract class UserManagementCommand extends CDOCommand.WithRepository
{
  public UserManagementCommand(String name, String description, CommandParameter... parameters)
  {
    super(name, description, parameters);
  }

  public UserManagementCommand(String name, String description)
  {
    super(name, description);
  }

  @Override
  public void execute(InternalRepository repository, String[] args) throws Exception
  {
    IUserManagement userManagement = getUserManagement(repository);
    execute(repository, userManagement, args);
  }

  protected abstract void execute(InternalRepository repository, IUserManagement userManagement, String[] args) throws Exception;

  private IUserManagement getUserManagement(InternalRepository repository)
  {
    IRepositoryProtector protector = repository.getProtector();
    if (protector != null)
    {
      UserAuthenticator userAuthenticator = protector.getUserAuthenticator();
      if (userAuthenticator instanceof IUserManagement)
      {
        return (IUserManagement)userAuthenticator;
      }
    }

    IAuthenticator authenticator = repository.getSessionManager().getAuthenticator();
    if (authenticator instanceof IUserManagement)
    {
      return (IUserManagement)authenticator;
    }

    throw new CommandException("User management not found for " + repository);
  }

  protected final PasswordChangeSupport getPasswordChangeSupport(InternalRepository repository)
  {
    IRepositoryProtector protector = repository.getProtector();
    if (protector != null)
    {
      UserAuthenticator userAuthenticator = protector.getUserAuthenticator();
      if (userAuthenticator instanceof PasswordChangeSupport)
      {
        return (PasswordChangeSupport)userAuthenticator;
      }
    }

    IAuthenticator authenticator = repository.getSessionManager().getAuthenticator();
    if (authenticator instanceof PasswordChangeSupport)
    {
      return (PasswordChangeSupport)authenticator;
    }

    throw new CommandException("Password change support not found for " + repository);
  }

  /**
   * @author Eike Stepper
   */
  public static final class UserAdd extends UserManagementCommand
  {
    public UserAdd()
    {
      super("useradd", "add a user to a repository", parameter("username"), optional("password"));
    }

    @Override
    protected void execute(InternalRepository repository, IUserManagement userManagement, String[] args) throws Exception
    {
      String username = args[0];
      String password = args[1];

      userManagement.addUser(username, SecurityUtil.toCharArray(password));
      println("User " + username + " added");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UserDelete extends UserManagementCommand
  {
    public UserDelete()
    {
      super("userdel", "delete a user from a repository", parameter("username"));
    }

    @Override
    protected void execute(InternalRepository repository, IUserManagement userManagement, String[] args) throws Exception
    {
      String username = args[0];

      userManagement.removeUser(username);
      println("User " + username + " deleted");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UserPassword extends UserManagementCommand
  {
    public UserPassword()
    {
      super("userpw", "set or unset the password of a repository user", parameter("username"), optional("password"));
    }

    @Override
    protected void execute(InternalRepository repository, IUserManagement userManagement, String[] args) throws Exception
    {
      String username = args[0];
      String password = args[1];

      userManagement.setPassword(username, SecurityUtil.toCharArray(password));
      println("Password of user " + username + (password != null ? " set" : " unset"));
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UserAdmin extends UserManagementCommand
  {
    public UserAdmin()
    {
      super("useradmin", "set or unset the administrator role of a repository user", parameter("username"), parameter("administrator"));
    }

    @Override
    protected void execute(InternalRepository repository, IUserManagement userManagement, String[] args) throws Exception
    {
      String username = args[0];
      boolean administrator = Boolean.parseBoolean(args[1]);

      userManagement.setAdministrator(username, administrator);
      println("Administrator role of user " + username + (administrator ? " set" : " unset"));
    }
  }
}
