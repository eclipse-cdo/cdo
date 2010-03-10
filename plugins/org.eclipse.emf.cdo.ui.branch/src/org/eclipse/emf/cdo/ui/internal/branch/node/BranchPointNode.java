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

import org.eclipse.emf.cdo.ui.internal.branch.connection.NewBranchConnection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;

public class BranchPointNode extends BranchTreeNode
{

  public BranchPointNode(IContainer graphModel, int style, BranchPointFigure figure, long timeStamp)
  {
    super(graphModel, style, figure, timeStamp);
  }

  public BranchTreeNode getNextChild() {
    List<NewBranchConnection> connectionList = getNewBranchSourceConnections();
    if (connectionList.size() >= 1) {
      NewBranchConnection connection = connectionList.get(0);
      GraphNode node = connection.getDestination();
      Assert.isTrue(node instanceof BranchTreeNode);
      return (BranchTreeNode)node;
    } else {
      return null;
    }
  }
  
  
  public List<NewBranchConnection> getNewBranchSourceConnections() {
    ArrayList<NewBranchConnection> connectionList = new ArrayList<NewBranchConnection>();
    for (Object targetConnection : getSourceConnections()) {
      if (targetConnection instanceof NewBranchConnection) {
        connectionList.add((NewBranchConnection)targetConnection);
      }
    }
    return connectionList;
  }
}
