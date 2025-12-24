/*
 * Copyright (c) 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.Diagram;
import org.eclipse.emf.cdo.tests.model3.Edge;
import org.eclipse.emf.cdo.tests.model3.NodeF;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collections;

/**
 * Bug 407157: Bidirectional reference corrupted on invalidation.
 *
 * @author <a href="mailto:esteban.dugueperoux@obeo.fr">Steve Monnier</a>
 */
public class Bugzilla_407157_Test extends AbstractCDOTest
{

  private static final String RESOURCE_NAME = "test1.model3";

  public void testUndoRedo() throws Exception
  {
    Diagram dDiagram = getModel3Factory().createDiagram();
    NodeF dNode1 = getModel3Factory().createNodeF();
    NodeF dNode2 = getModel3Factory().createNodeF();
    NodeF dNode3 = getModel3Factory().createNodeF();
    Edge dEdge = getModel3Factory().createEdge();

    dDiagram.getEdgeTargets().add(dNode1);
    dDiagram.getEdgeTargets().add(dNode2);
    dDiagram.getEdgeTargets().add(dNode3);
    dDiagram.getEdges().add(dEdge);
    dEdge.setSourceNode(dNode1);
    dEdge.setTargetNode(dNode3);

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));

    resource1.getContents().add(dDiagram);
    resource1.save(Collections.emptyMap());

    // Second user
    CDOSession sessionOfSecondUser = openSession();
    CDOTransaction transactionOfSecondUser = sessionOfSecondUser.openTransaction();
    CDOResource resourceOfSecondUser = transactionOfSecondUser.getResource(getResourcePath(RESOURCE_NAME));
    Diagram dDiagramOfSecondUser = (Diagram)resourceOfSecondUser.getContents().get(0);
    Edge dEdgeOfSecondUser = dDiagramOfSecondUser.getEdges().get(0);
    NodeF dNode1OfSecondUser = (NodeF)dDiagramOfSecondUser.getEdgeTargets().get(0);
    NodeF dNode2OfSecondUser = (NodeF)dDiagramOfSecondUser.getEdgeTargets().get(1);
    NodeF dNode3OfSecondUser = (NodeF)dDiagramOfSecondUser.getEdgeTargets().get(2);

    // Do from first user
    EcoreUtil.delete(dEdge);
    Edge newDEdge = getModel3Factory().createEdge();
    dNode2.getOutgoingEdges().add(newDEdge);
    dNode3.getIncomingEdges().add(newDEdge);
    dDiagram.getEdges().add(newDEdge);

    // Undo from first user
    dNode1.getOutgoingEdges().add(dEdge);
    dNode2.getOutgoingEdges().remove(newDEdge);
    dNode3.getIncomingEdges().remove(newDEdge);
    dNode3.getIncomingEdges().add(dEdge);
    dDiagram.getEdges().add(dEdge);
    dDiagram.getEdges().remove(newDEdge);

    commitAndSync(transaction1, transactionOfSecondUser);

    // Do assertion from second user
    assertEquals(dNode1OfSecondUser, dEdgeOfSecondUser.getSourceNode());
    assertEquals(dNode3OfSecondUser, dEdgeOfSecondUser.getTargetNode());
    assertTrue(dNode3OfSecondUser.getIncomingEdges().contains(dEdgeOfSecondUser));
    assertEquals(1, dNode3OfSecondUser.getIncomingEdges().size());
    assertTrue(dNode1OfSecondUser.getOutgoingEdges().contains(dEdgeOfSecondUser));
    assertEquals(1, dNode1OfSecondUser.getOutgoingEdges().size());
    assertEquals(0, dNode2OfSecondUser.getOutgoingEdges().size());
    assertEquals(0, dNode2OfSecondUser.getIncomingEdges().size());
  }

}
