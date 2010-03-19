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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.internal.branch.figure.BranchPointFigure;
import org.eclipse.emf.cdo.ui.internal.branch.figure.TreeRootFigure;
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.item.NewBranchConnection;
import org.eclipse.emf.cdo.ui.internal.branch.item.RootNode;
import org.eclipse.emf.cdo.ui.internal.branch.item.SameBranchConnection;
import org.eclipse.emf.cdo.ui.internal.branch.layout.BranchTreeLayoutAlgorithm;
import org.eclipse.emf.cdo.ui.internal.branch.layout.HorizontallyAlternatingSubBranches;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;

/**
 * A view that displays cdo branch points, cdo branches and cdo commit infos in a graphical tree.
 * 
 * @author Andre Dietisheim
 */
public class BranchViewPart extends ViewPart
{
  public static final String VIEW_ID = "org.eclipse.emf.cdo.ui.branch"; //$NON-NLS-1$

  private IManagedContainer container;

  private IConnector connector;

  private CDOSession session;

  @Override
  public void createPartControl(Composite parent)
  {
    Graph graph = new Graph(parent, SWT.NONE);
    // graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

    createTestTree(graph);

    LayoutAlgorithm layout = new BranchTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING,
        new HorizontallyAlternatingSubBranches());
    graph.setLayoutAlgorithm(layout, true);
  }

  @Override
  public void init(IViewSite site) throws PartInitException
  {
    super.init(site);
    initCDO();
    site.getPage().addPartListener(new IPartListener2()
    {
      public void partVisible(IWorkbenchPartReference partRef)
      {
      }

      public void partOpened(IWorkbenchPartReference partRef)
      {
      }

      public void partInputChanged(IWorkbenchPartReference partRef)
      {
      }

      public void partHidden(IWorkbenchPartReference partRef)
      {
      }

      public void partDeactivated(IWorkbenchPartReference partRef)
      {
      }

      public void partClosed(IWorkbenchPartReference partRef)
      {
        cleanupCDO();
      }

      public void partBroughtToTop(IWorkbenchPartReference partRef)
      {
      }

      public void partActivated(IWorkbenchPartReference partRef)
      {
      }
    });
  }

  @Override
  public void setFocus()
  {
  }

  private void createTestTree(Graph graph)
  {
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch subBranch1 = mainBranch.createBranch("1");
    CDOBranch subBranch1_1 = subBranch1.createBranch("1-1", subBranch1.getBase().getTimeStamp() + 30000000);
    CDOBranch subBranch1_2 = subBranch1.createBranch("1-2", subBranch1_1.getBase().getTimeStamp() + 30000000);
    CDOBranch subBranch1_2_1 = subBranch1.createBranch("1-2_1", subBranch1_2.getBase().getTimeStamp() + 30000000);
    // CDOBranch subBranch1_2_2 = subBranch1.createBranch("1-2_2", subBranch1_2_1.getBase().getTimeStamp() + 30000000);
    CDOBranch subBranch2 = mainBranch.createBranch("2", subBranch1.getBase().getTimeStamp() + 30000000);
    CDOBranch subBranch2_1 = subBranch1.createBranch("2-1", subBranch2.getBase().getTimeStamp() + 30000000);
    CDOBranch subBranch2_2 = subBranch1.createBranch("2-2", subBranch2_1.getBase().getTimeStamp() + 30000000);
    CDOBranch subBranch3 = mainBranch.createBranch("3", subBranch2.getBase().getTimeStamp() + 30000000);

    RootNode rootNode = new RootNode(mainBranch.getBase(), graph, SWT.NONE, new TreeRootFigure());
    BranchPointNode branchNode1 = new BranchPointNode(subBranch1.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 1"));
    BranchPointNode branchNode1_1 = new BranchPointNode(subBranch1_1.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-1"));
    BranchPointNode branchNode1_2 = new BranchPointNode(subBranch1_2.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 1-2"));
    BranchPointNode branchNode1_2_1 = new BranchPointNode(subBranch1_2_1.getBase(), graph, SWT.NONE,
        new BranchPointFigure("branch point 1-2-1"));
    // BranchPointNode branchNode1_2_2 = new BranchPointNode(subBranch1_2_2.getBase(), graph, SWT.NONE,
    // new BranchPointFigure("branch point 1-2-2"));
    BranchPointNode branchNode2 = new BranchPointNode(subBranch2.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 2"));
    BranchPointNode branchNode2_1 = new BranchPointNode(subBranch2_1.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 2-1"));
    BranchPointNode branchNode2_2 = new BranchPointNode(subBranch2_2.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 2-2"));
    BranchPointNode branchNode3 = new BranchPointNode(subBranch3.getBase(), graph, SWT.NONE, new BranchPointFigure(
        "branch point 3"));

    new SameBranchConnection(graph, SWT.NONE, rootNode, branchNode1);
    new NewBranchConnection(graph, SWT.NONE, branchNode1, branchNode1_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode1_1, branchNode1_2);
    new NewBranchConnection(graph, SWT.NONE, branchNode1_2, branchNode1_2_1);
    // new SameBranchConnection(graph, SWT.NONE, branchNode1_2_1, branchNode1_2_2);
    new SameBranchConnection(graph, SWT.NONE, branchNode1, branchNode2);
    new SameBranchConnection(graph, SWT.NONE, branchNode2, branchNode3);
    new NewBranchConnection(graph, SWT.NONE, branchNode2, branchNode2_1);
    new SameBranchConnection(graph, SWT.NONE, branchNode2_1, branchNode2_2);

  }

  private void initCDO()
  {

    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);

    container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container); // Register Net4j factories
    TCPUtil.prepareContainer(container); // Register TCP factories
    CDONet4jUtil.prepareContainer(container); // Register CDO factories
    LifecycleUtil.activate(container);

    connector = TCPUtil.getConnector(container, "localhost:2036"); //$NON-NLS-1$

    org.eclipse.emf.cdo.net4j.CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("repo1"); //$NON-NLS-1$

    session = configuration.openSession();
    session.getPackageRegistry().putEPackage(CompanyPackage.eINSTANCE);
  }

  private void cleanupCDO()
  {
    // Cleanup
    session.close();
    connector.close();
    container.deactivate();
  }
}
