/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.team.IRepositoryProject;

/**
 * @author Eike Stepper
 */
public abstract class Node
{
  private static final Object[] EMPTY = {};

  private IRepositoryProject repositoryProject;

  public Node(IRepositoryProject repositoryProject)
  {
    this.repositoryProject = repositoryProject;
  }

  public IRepositoryProject getRepositoryProject()
  {
    return repositoryProject;
  }

  public abstract String getText();

  public abstract String getImageKey();

  public Object[] getChildren()
  {
    return EMPTY;
  }

  public abstract Type getType();

  /**
   * @author Eike Stepper
   */
  public static enum Type
  {
    SESSIONS, PACKAGES, RESOURCES
  }

  /**
   * @author Eike Stepper
   */
  public static final class SessionsNode extends Node
  {
    public SessionsNode(IRepositoryProject repositoryProject)
    {
      super(repositoryProject);
    }

    @Override
    public Type getType()
    {
      return Type.SESSIONS;
    }

    @Override
    public String getText()
    {
      return "Sessions";
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Sessions.gif";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class PackagesNode extends Node
  {
    public PackagesNode(IRepositoryProject repositoryProject)
    {
      super(repositoryProject);
    }

    @Override
    public Type getType()
    {
      return Type.PACKAGES;
    }

    @Override
    public String getText()
    {
      return "Packages";
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Packages.gif";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ResourcesNode extends Node
  {
    public ResourcesNode(IRepositoryProject repositoryProject)
    {
      super(repositoryProject);
    }

    @Override
    public Type getType()
    {
      return Type.RESOURCES;
    }

    @Override
    public String getText()
    {
      return "Resources";
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Resources.gif";
    }
  }

}
