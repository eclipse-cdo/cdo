/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.team.IRepositoryProject;
import org.eclipse.emf.cdo.ui.ide.CommonNavigatorUtils.MessageType;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.core.runtime.PlatformObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class Node extends PlatformObject
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

  public Object getParent()
  {
    return repositoryProject.getProject();
  }

  public abstract Type getType();

  /**
   * @author Eike Stepper
   */
  public static enum Type
  {
    BRANCH, PACKAGES, RESOURCES, SESSIONS
  }

  /**
   * @author Victor Roldan Betancort
   */
  public static final class BranchNode extends Node
  {
    private CDOBranch branch;

    public BranchNode(IRepositoryProject repositoryProject, CDOBranch branch)
    {
      super(repositoryProject);
      this.branch = branch;
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    @Override
    public Type getType()
    {
      return Type.BRANCH;
    }

    @Override
    public String getText()
    {
      return branch.getName();
    }

    @Override
    public String getImageKey()
    {
      return OM.BRANCH_ICON;
    }

    @Override
    public Object[] getChildren()
    {
      if (!getRepositoryProject().getView().getSession().getRepositoryInfo().isSupportingBranches())
      {
        return CommonNavigatorUtils.createMessageProviderChild(Messages.getString("Node.1"), //$NON-NLS-1$
            MessageType.INFO);
      }

      CDOBranch[] branches = branch.getBranches();
      BranchNode[] nodes = new BranchNode[branches.length];
      for (int i = 0; i < branches.length; i++)
      {
        nodes[i] = new BranchNode(getRepositoryProject(), branches[i]);
      }

      return nodes;
    }

    @Override
    public Object getParent()
    {
      if (branch.isMainBranch())
      {
        return getRepositoryProject();
      }

      return branch.getBase().getBranch();
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
      return Messages.getString("Node.2"); //$NON-NLS-1$
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Packages.gif"; //$NON-NLS-1$
    }

    @Override
    public EPackage[] getChildren()
    {
      CDOPackageRegistry packageRegistry = getRepositoryProject().getView().getSession().getPackageRegistry();
      List<EPackage> children = new ArrayList<EPackage>();
      for (String nsURI : packageRegistry.keySet())
      {
        try
        {
          EPackage ePackage = packageRegistry.getEPackage(nsURI);
          children.add(ePackage);
        }
        catch (org.eclipse.emf.cdo.common.util.CDOException ex)
        {
          // Generated packages could not be locally available
          OM.LOG.error(ex);
        }
      }

      return children.toArray(new EPackage[children.size()]);
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
      return Messages.getString("Node.4"); //$NON-NLS-1$
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Resources.gif"; //$NON-NLS-1$
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter)
    {
      if (adapter.equals(CDOObject.class))
      {
        return getRepositoryProject().getView().getRootResource();
      }

      return super.getAdapter(adapter);
    }

    @Override
    public CDOResourceNode[] getChildren()
    {
      CDOView view = getRepositoryProject().getView();
      List<CDOResourceNode> children = new ArrayList<CDOResourceNode>();
      for (EObject resourceNode : view.getRootResource().getContents())
      {
        if (resourceNode instanceof CDOResourceNode)
        {
          children.add((CDOResourceNode)resourceNode);
        }
      }

      return children.toArray(new CDOResourceNode[children.size()]);
    }
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
      return Messages.getString("Node.0"); //$NON-NLS-1$
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Sessions.gif"; //$NON-NLS-1$
    }
  }

}
