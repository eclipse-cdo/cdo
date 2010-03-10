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
package org.eclipse.emf.cdo.ui.internal.branch.node;

import org.eclipse.emf.cdo.ui.internal.branch.connection.SameBranchConnection;

import org.eclipse.draw2d.IFigure;
import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Superclass for all nodes of a BranchTree. A branch tree node may have a connection to a single child (a branch tree
 * node in a new sub-branch) and a single next sibling (a branch tree node in the same branch)
 * 
 * @see BranchPointNode
 * @see RootNode
 */
public abstract class BranchTreeNode extends CGraphNode
{
  public static long TIMESTAMP_UNDEFINED = -1;

  /** The time stamp. */
  private long timeStamp = TIMESTAMP_UNDEFINED;

  /**
   * Instantiates a new branch graph node.
   * 
   * @param graphModel
   *          the graph model
   * @param style
   *          the style
   * @param figure
   *          the figure
   */
  public BranchTreeNode(IContainer graphModel, int style, IFigure figure)
  {
    this(graphModel, style, figure, TIMESTAMP_UNDEFINED);
  }

  /**
   * Instantiates a new branch graph node.
   * 
   * @param graphModel
   *          the graph model
   * @param style
   *          the style
   * @param figure
   *          the figure
   * @param timeStamp
   *          the time stamp
   */
  public BranchTreeNode(IContainer graphModel, int style, IFigure figure, long timeStamp)
  {
    super(graphModel, style, figure);
    this.timeStamp = timeStamp;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.cdo.ui.branch.ITimeStampNode#getTimeStamp()
   */
  public long getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * Gets the next (sibling) node in the same branch.
   * 
   * @return the next sibling
   */
  public BranchTreeNode getNextSibling()
  {
    BranchTreeNode node = null;
    for (SameBranchConnection connection : getSameBranchSourceConnections())
    {
      node = (BranchTreeNode)connection.getDestination();
      break;
    }
    return node;
  }

  /**
   * Gets the previous (sibling) node in the same branch.
   * 
   * @return the previous sibling
   */
  public BranchTreeNode getPreviousSibling()
  {
    BranchTreeNode node = null;
    for (SameBranchConnection connection : getSameBranchTargetConnections())
    {
      node = (BranchTreeNode)connection.getSource();
      break;
    }
    return node;
  }

  /**
   * Gets the same branch source connections.
   * 
   * @return the same branch source connections
   */
  public List<SameBranchConnection> getSameBranchSourceConnections()
  {
    ArrayList<SameBranchConnection> connectionList = new ArrayList<SameBranchConnection>();
    for (Object sourceConnection : getSourceConnections())
    {
      if (sourceConnection instanceof SameBranchConnection)
      {
        connectionList.add((SameBranchConnection)sourceConnection);
      }
    }
    return connectionList;
  }

  /**
   * Gets the same branch target connections.
   * 
   * @return the same branch target connections
   */
  public List<SameBranchConnection> getSameBranchTargetConnections()
  {
    ArrayList<SameBranchConnection> connectionList = new ArrayList<SameBranchConnection>();
    for (Object targetConnection : getTargetConnections())
    {
      if (targetConnection instanceof SameBranchConnection)
      {
        connectionList.add((SameBranchConnection)targetConnection);
      }
    }
    return connectionList;
  }

  /**
   * Gets the latter node (in terms of time stamp). Either this node or the given one.
   * 
   * @param node
   *          the node to check against
   * @return the latter node
   */
  public BranchTreeNode getLatter(BranchTreeNode node)
  {
    BranchTreeNode latterNode = this;
    if (node != null)
    {
      if (timeStamp < node.getTimeStamp())
      {
        latterNode = node;
      }
      else if (timeStamp == node.getTimeStamp())
      {
        latterNode = this;
      }
    }
    return latterNode;
  }

  public String toString()
  {
    return super.toString() + " " + getTimeStamp();
  }

}
