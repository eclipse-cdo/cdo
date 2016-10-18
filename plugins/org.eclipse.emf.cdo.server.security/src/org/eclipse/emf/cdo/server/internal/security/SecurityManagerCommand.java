/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.spi.server.CDOCommand;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

/**
 * @author Eike Stepper
 */
public abstract class SecurityManagerCommand extends CDOCommand.WithRepository
{
  public SecurityManagerCommand(String name, String description, CommandParameter... parameters)
  {
    super(name, description, parameters);
  }

  public SecurityManagerCommand(String name, String description)
  {
    super(name, description);
  }

  @Override
  public void execute(InternalRepository repository, String[] args) throws Exception
  {
    ISecurityManager securityManager = SecurityManagerUtil.getSecurityManager(repository);
    if (securityManager == null)
    {
      throw new CommandException("Security manager not found for " + repository);
    }

    execute(securityManager, args);
  }

  protected abstract void execute(ISecurityManager securityManager, String[] args);

  /**
   * @author Eike Stepper
   */
  public static final class AddUser extends SecurityManagerCommand
  {
    public AddUser()
    {
      super("adduser", "adds a user to the security realm of a repository", parameter("username"), optional("password"));
    }

    @Override
    protected void execute(ISecurityManager securityManager, String[] args)
    {
      String username = args[0];
      String password = args[1];
      if (password != null)
      {
        securityManager.addUser(username, password);
      }
      else
      {
        securityManager.addUser(username);
      }

      println("User " + username + " added");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SetPassword extends SecurityManagerCommand
  {
    public SetPassword()
    {
      super("setpassword", "sets or unsets the password of a repository user", parameter("username"), optional("password"));
    }

    @Override
    protected void execute(ISecurityManager securityManager, String[] args)
    {
      String username = args[0];
      String password = args[1];
      securityManager.setPassword(username, password);

      println("Password of user " + username + (password != null ? " set" : " unset"));
    }
  }
}
