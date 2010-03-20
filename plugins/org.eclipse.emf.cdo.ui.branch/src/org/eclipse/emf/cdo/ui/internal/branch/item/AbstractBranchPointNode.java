/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.branch.item;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.draw2d.IFigure;
import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Superclass for all nodes of a BranchTree. A branch tree node may have a connection to a single child (a branch tree
 * node in a new sub-branch) and a single next sibling (a branch tree node in the same branch)
 * 
 * @author Andre Dietisheim
 * @see BranchPointNode
 * @see RootNode
 */
public abstract class AbstractBranchPointNode extends CGraphNode implements CDOBranchPoint
{
  private CDOBranchPoint branchPoint;

  public AbstractBranchPointNode(CDOBranchPoint branchPoint, IContainer graphModel, int style, IFigure figure)
  {
    super(graphModel, style, figure);
    this.branchPoint = branchPoint;
  }

  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  public int compareTo(CDOBranchPoint o)
  {
    return branchPoint.compareTo(o);
  }

  public AbstractBranchPointNode getNextOnSameBranch()
  {
    for (SameBranchConnection connection : getSameBranchSourceConnections())
    {
      return (AbstractBranchPointNode)connection.getDestination();
    }

    return null;
  }

  public AbstractBranchPointNode getPreviousSibling()
  {
    for (SameBranchConnection connection : getSameBranchTargetConnections())
    {
      return (AbstractBranchPointNode)connection.getSource();
    }

    return null;
  }

  public List<SameBranchConnection> getSameBranchSourceConnections()
  {
    List<SameBranchConnection> connectionList = new ArrayList<SameBranchConnection>();
    for (Object sourceConnection : getSourceConnections())
    {
      if (sourceConnection instanceof SameBranchConnection)
      {
        connectionList.add((SameBranchConnection)sourceConnection);
      }
    }

    return connectionList;
  }

  public List<SameBranchConnection> getSameBranchTargetConnections()
  {
    List<SameBranchConnection> connectionList = new ArrayList<SameBranchConnection>();
    for (Object targetConnection : getTargetConnections())
    {
      if (targetConnection instanceof SameBranchConnection)
      {
        connectionList.add((SameBranchConnection)targetConnection);
      }
    }

    return connectionList;
  }

  public AbstractBranchPointNode getLater(AbstractBranchPointNode node)
  {
    if (node != null && node.getTimeStamp() > getTimeStamp())
    {
      return node;
    }

    return this;
  }

  @Override
  public String toString()
  {
    return super.toString() + " " + branchPoint; //$NON-NLS-1$
  }
}
