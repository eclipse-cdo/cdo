/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.security.bundle;

import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.spi.server.CDOCommand;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class AddUserCommand extends CDOCommand.WithRepository
{
  public static final String NAME = "adduser";

  public AddUserCommand()
  {
    super(NAME, "adds a user to the security realm of a repository", parameter("username"), optional("password"));
  }

  @Override
  public void execute(InternalRepository repository, String[] args) throws Exception
  {
    ISecurityManager securityManager = SecurityManagerUtil.getSecurityManager(repository);
    if (securityManager == null)
    {
      throw new CommandException("Security manager not found for " + repository);
    }

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

  /**
   * @author Eike Stepper
   */
  public static class Factory extends CDOCommand.Factory
  {
    public Factory()
    {
      super(NAME);
    }

    @Override
    public CDOCommand create(String description) throws ProductCreationException
    {
      return new AddUserCommand();
    }
  }
}
