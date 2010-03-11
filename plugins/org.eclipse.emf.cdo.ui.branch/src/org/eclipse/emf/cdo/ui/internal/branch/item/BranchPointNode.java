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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.draw2d.IFigure;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * A node that holds a branch point. It may be connected to a further (single) BranchPointNode by a NewBranchConnection
 * to a node in a sub-branch. It may also be connected to a node in the same branch by a SameBranchConnection.
 * 
 * @author Andre Dietisheim
 * @see SameBranchConnection
 * @see NewBranchConnection
 */
public class BranchPointNode extends AbstractBranchPointNode
{
  public BranchPointNode(CDOBranchPoint branchPoint, IContainer graphModel, int style, IFigure figure)
  {
    super(branchPoint, graphModel, style, figure);
  }

  public AbstractBranchPointNode getNextChild()
  {
    List<NewBranchConnection> connectionList = getNewBranchSourceConnections();
    if (connectionList.size() >= 1)
    {
      NewBranchConnection connection = connectionList.get(0);
      GraphNode node = connection.getDestination();
      return (AbstractBranchPointNode)node;
    }

    return null;
  }

  public List<NewBranchConnection> getNewBranchSourceConnections()
  {
    List<NewBranchConnection> connectionList = new ArrayList<NewBranchConnection>();
    for (Object targetConnection : getSourceConnections())
    {
      if (targetConnection instanceof NewBranchConnection)
      {
        connectionList.add((NewBranchConnection)targetConnection);
      }
    }

    return connectionList;
  }
}
