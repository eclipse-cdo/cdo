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
package org.eclipse.emf.cdo.ui.internal.branch;

import org.eclipse.emf.cdo.ui.internal.branch.connection.NewBranchConnection;
import org.eclipse.emf.cdo.ui.internal.branch.connection.SameBranchConnection;
import org.eclipse.emf.cdo.ui.internal.branch.layout.BranchTreeLayoutAlgorithm;
import org.eclipse.emf.cdo.ui.internal.branch.node.BranchPointFigure;
import org.eclipse.emf.cdo.ui.internal.branch.node.BranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.node.BranchRootFigure;
import org.eclipse.emf.cdo.ui.internal.branch.node.RootNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;

/**
 * A view that displays cdo branch points, cdo branches and cdo commit infos in a graphical tree.
 * 
 * @author Andre Dietisheim
 */
public class BranchViewPart extends ViewPart
{
  public static final String VIEW_ID = "org.eclipse.emf.cdo.ui.branch";

  @Override
  public void createPartControl(Composite parent)
  {
    Graph graph = new Graph(parent, SWT.NONE);
    graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

    RootNode rootNode = new RootNode(graph, SWT.NONE, new BranchRootFigure());

    BranchPointNode branchNode1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 1"), 100);
    BranchPointNode branchNode1_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 1-1"), 300);
    BranchPointNode branchNode1_1_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 1-1-1"),
        600);
    BranchPointNode branchNode1_1_1_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-1-1-1"), 1102);
    BranchPointNode branchNode1_1_1_1_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-1-1-1-1"), 1302);
    BranchPointNode branchNode1_1_1_2 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-1-1-2"), 1402);
    BranchPointNode branchNode1_1_1_2_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-1-1-2-1"), 1602);
    BranchPointNode branchNode1_1_2 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 1-1-2"),
        802);
    BranchPointNode branchNode1_1_2_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-1-2-1"), 902);

    BranchPointNode branchNode1_2 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 1-2"), 500);
    BranchPointNode branchNode1_2_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 1-2-1"),
        800);
    BranchPointNode branchNode2 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 2"), 301);
    BranchPointNode branchNode2_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 2-1"), 401);
    BranchPointNode branchNode2_1_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 2-1-1"),
        600);
    BranchPointNode branchNode2_1_2 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 2-1-2"),
        1001);
    BranchPointNode branchNode2_1_2_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 2-1-2-1"), 1200);
    BranchPointNode branchNode2_1_1_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure(
        "branch point 2-1-1-1"), 900);
    BranchPointNode branchNode2_2 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 2-2"), 700);
    BranchPointNode branchNode2_2_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 2-2-1"),
        1401);

    BranchPointNode branchNode3 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 3"), 701);
    BranchPointNode branchNode3_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 3-1"), 900);

    BranchPointNode branchNode4 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 4"), 1004);
    BranchPointNode branchNode4_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 4-1"),
        1300);

    BranchPointNode branchNode5 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 5"), 1400);
    BranchPointNode branchNode5_1 = new BranchPointNode(graph, SWT.NONE, new BranchPointFigure("branch point 5-1"),
        1600);

    new SameBranchConnection(graph, SWT.NONE, rootNode, branchNode1);
    new NewBranchConnection(graph, SWT.NONE, branchNode1, branchNode1_1);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_1, branchNode1_1_1);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_1_1, branchNode1_1_1_1);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_1_1_1, branchNode1_1_1_1_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode1_1_1_1, branchNode1_1_1_2);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_1_1_2, branchNode1_1_1_2_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode1_1_1, branchNode1_1_2);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_1_2, branchNode1_1_2_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode1_1, branchNode1_2);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_2, branchNode1_2_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode1, branchNode2);
    new NewBranchConnection(graph, SWT.NONE, branchNode2, branchNode2_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode2_1, branchNode2_2);
    new NewBranchConnection(graph, SWT.NONE, branchNode2_1, branchNode2_1_1);
    new NewBranchConnection(graph, SWT.NONE, branchNode2_1_1, branchNode2_1_1_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode2_1_1, branchNode2_1_2);
    new NewBranchConnection(graph, SWT.NONE, branchNode2_1_2, branchNode2_1_2_1);
    new NewBranchConnection(graph, SWT.NONE, branchNode2_2, branchNode2_2_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode2, branchNode3);
    new NewBranchConnection(graph, SWT.NONE, branchNode3, branchNode3_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode3, branchNode4);
    new NewBranchConnection(graph, SWT.NONE, branchNode4, branchNode4_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode4, branchNode5);
    new NewBranchConnection(graph, SWT.NONE, branchNode5, branchNode5_1);

    LayoutAlgorithm layout = new BranchTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    graph.setLayoutAlgorithm(layout, true);
  }

  @Override
  public void setFocus()
  {
  }
}
