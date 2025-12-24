/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.edit.command.DragAndDropCommand;

import java.text.MessageFormat;

/**
 * Moving nodes in a tree structure (simulate a {@link DragAndDropCommand}) may result in an inconsistent tree.
 * <p>
 * See bug 319836
 *
 * @author Cyril Jaquier
 */
public class Bugzilla_319836_Test extends AbstractCDOTest
{
  private static final boolean SHOW_BUG = true;

  public void testNodeMovesInTreeCreatesCycle() throws Exception
  {
    {
      // Setup an initial session and a transaction.
      CDOSession session = openSession();
      CDOTransaction tr1 = session.openTransaction();
      CDOResource resource = tr1.createResource(getResourcePath("/test1"));

      NodeA n1 = getModel3Factory().createNodeA();
      NodeA n2 = getModel3Factory().createNodeA();
      NodeA n3 = getModel3Factory().createNodeA();

      resource.getContents().add(n1);

      // Create a deep tree.
      //
      // n1
      // `- n2
      // ___`- n3
      n1.getChildren().add(n2);
      n2.getChildren().add(n3);

      tr1.commit();
      setNames(n1, n2, n3);
      tr1.commit();
      addCommitContextTracer();

      // First move.
      //
      // n1
      // |- n2
      // `- n3
      if (SHOW_BUG)
      {
        n2.getChildren().remove(n3);
      }

      n1.getChildren().add(n3);

      // Second move.
      //
      // n1
      // `- n3
      // ___`- n2

      // Something bad will happen during the execution of the next line. Set a breakpoint in
      // org.eclipse.emf.internal.cdo.transaction.CDOSavepointImpl.detachedObjects.new Map<CDOID, CDOObject>()
      // {...}.put(CDOID, CDOObject) and see how the previous REMOVE is eaten.
      if (SHOW_BUG)
      {
        n1.getChildren().remove(n2);
      }

      n3.getChildren().add(n2);

      // Problem is with n2; the removal of its child has been lost
      CDOID n2ID = CDOUtil.getCDOObject(n2).cdoID();
      CDORevisionDelta revDelta = tr1.getRevisionDeltas().get(n2ID);
      assertEquals(2, revDelta.getFeatureDeltas().size());

      tr1.commit();

      // Checks the tree.
      assertEquals(1, n1.getChildren().size());
      assertEquals(0, n2.getChildren().size());
      assertEquals(1, n3.getChildren().size());
    }

    {
      // Setup a new session. The bug only does not seem to occur if we use the same session.
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/test1"));

      NodeA n1 = (NodeA)resource.getContents().get(0);

      // Checks the tree.
      assertEquals(1, n1.getChildren().size());
      NodeA n3 = n1.getChildren().get(0);
      assertEquals(1, n3.getChildren().size());
      NodeA n2 = n3.getChildren().get(0);
      // Oups... n2 has a child!? And even worst... it's n3 :'( Houston, we have a problem...
      assertEquals(0, n2.getChildren().size());
    }
  }

  public void testNodeMovesInTreeDuplicatesNode() throws Exception
  {
    {
      // Setup an initial session and a transaction.
      CDOSession session = openSession();
      CDOTransaction tr1 = session.openTransaction();
      CDOResource resource = tr1.createResource(getResourcePath("/test1"));

      NodeA n1 = getModel3Factory().createNodeA();
      NodeA n2 = getModel3Factory().createNodeA();
      NodeA n3 = getModel3Factory().createNodeA();
      NodeA n4 = getModel3Factory().createNodeA();

      resource.getContents().add(n1);

      // Create a deep tree.
      //
      // n1
      // `- n2
      // ___`- n3
      // ______`- n4
      n1.getChildren().add(n2);
      n2.getChildren().add(n3);
      n3.getChildren().add(n4);

      tr1.commit();
      setNames(n1, n2, n3);
      tr1.commit();
      addCommitContextTracer();

      // First move.
      //
      // n1
      // `- n2
      // ___|- n3
      // ___`- n4
      if (SHOW_BUG)
      {
        n3.getChildren().remove(n4);
      }

      n2.getChildren().add(n4);

      // Second move.
      //
      // n1
      // |- n2
      // |__`- n4
      // `- n3

      // Something bad will happen during the execution of the next line. Set a breakpoint in
      // org.eclipse.emf.internal.cdo.transaction.CDOSavepointImpl.detachedObjects.new Map<CDOID, CDOObject>()
      // {...}.put(CDOID, CDOObject) and see how the previous REMOVE is eaten.
      if (SHOW_BUG)
      {
        n2.getChildren().remove(n3);
      }

      n1.getChildren().add(n3);

      tr1.commit();

      // Checks the tree.
      assertEquals(2, n1.getChildren().size());
      assertEquals(1, n2.getChildren().size());
      assertEquals(0, n3.getChildren().size());
      assertEquals(0, n4.getChildren().size());
    }

    {
      // Setup a new session. The bug only does not seem to occur if we use the same session.
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/test1"));

      NodeA n1 = (NodeA)resource.getContents().get(0);

      // Checks the tree.
      assertEquals(2, n1.getChildren().size());
      NodeA n2 = n1.getChildren().get(0);
      assertEquals(1, n2.getChildren().size());
      NodeA n4 = n2.getChildren().get(0);
      assertEquals(0, n4.getChildren().size());
      NodeA n3 = n1.getChildren().get(1);
      // Oups... n3 has a child!?
      assertEquals(0, n3.getChildren().size());
    }
  }

  public void testNodeMovesInTreeEatsNode() throws Exception
  {
    {
      // Setup an initial session and a transaction.
      CDOSession session = openSession();
      CDOTransaction tr1 = session.openTransaction();
      CDOResource resource = tr1.createResource(getResourcePath("/test1"));

      NodeA n1 = getModel3Factory().createNodeA();
      NodeA n2 = getModel3Factory().createNodeA();
      NodeA n3 = getModel3Factory().createNodeA();
      NodeA n4 = getModel3Factory().createNodeA();

      resource.getContents().add(n1);

      // Create a flat tree.
      //
      // n1
      // |- n2
      // |- n3
      // `- n4
      n1.getChildren().add(n2);
      n1.getChildren().add(n3);
      n1.getChildren().add(n4);

      tr1.commit();
      setNames(n1, n2, n3);
      tr1.commit();
      addCommitContextTracer();

      // First move.
      //
      // n1
      // |- n2
      // `- n3
      // ___`- n4
      if (SHOW_BUG)
      {
        n1.getChildren().remove(n4);
      }

      n3.getChildren().add(n4);

      // Second move.
      //
      // n1
      // `- n2
      // ___`- n3
      // ______`- n4

      // Something bad will happen during the execution of the next line. Set a breakpoint in
      // org.eclipse.emf.internal.cdo.transaction.CDOSavepointImpl.detachedObjects.new Map<CDOID, CDOObject>()
      // {...}.put(CDOID, CDOObject) and see how the previous ADD is eaten.
      if (SHOW_BUG)
      {
        n1.getChildren().remove(n3);
      }

      n2.getChildren().add(n3);

      tr1.commit();

      // Checks the tree.
      assertEquals(1, n1.getChildren().size());
      assertEquals(n2, n1.getChildren().get(0));
      assertEquals(1, n2.getChildren().size());
      assertEquals(n3, n2.getChildren().get(0));
      assertEquals(1, n3.getChildren().size());
      assertEquals(n4, n3.getChildren().get(0));
      assertEquals(0, n4.getChildren().size());
    }

    {
      // Setup a new session. The bug only does not seem to occur if we use the same session.
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/test1"));

      NodeA n1 = (NodeA)resource.getContents().get(0);

      // Checks the tree.
      assertEquals(1, n1.getChildren().size());
      NodeA n2 = n1.getChildren().get(0);
      assertEquals(1, n2.getChildren().size());
      NodeA n3 = n2.getChildren().get(0);
      // Oups... Where is n4!?
      assertEquals(1, n3.getChildren().size());
      NodeA n4 = n3.getChildren().get(0);
      assertEquals(0, n4.getChildren().size());
    }
  }

  private void setNames(NodeA... nodes)
  {
    for (NodeA node : nodes)
    {
      String id = CDOUtil.getCDOObject(node).cdoID().toString();
      msg("Node: " + id);
      node.setName(id);
    }
  }

  private void addCommitContextTracer()
  {
    getRepository().addHandler(new WriteAccessHandler()
    {
      @Override
      public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
      {
        msg(toPrettyString("\t", commitContext.getDirtyObjectDeltas()));
      }

      @Override
      public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      {
      }

      /**
       * Prints the {@link InternalCDORevisionDelta}s in a more friendly way than {@link #toString()}.
       *
       * @param spacer
       *          the spacer used to increment the output
       * @return {@link String}
       */
      private String toPrettyString(String spacer, InternalCDORevisionDelta[] dirtyObjects)
      {
        StringBuilder sb = new StringBuilder();

        // Delta.
        sb.append(spacer).append("Delta revision(s):\n");
        for (InternalCDORevisionDelta item : dirtyObjects)
        {
          String m = MessageFormat.format("{0}@{1}:{2}v{3}", item.getEClass().getName(), item.getID(), item.getBranch().getID(), item.getVersion());
          sb.append(spacer).append(spacer).append(m).append("\n");
          // Feature deltas.
          for (CDOFeatureDelta delta : item.getFeatureDeltas())
          {
            printFeatureDelta(sb, delta, spacer, 3);
          }
        }

        return sb.toString();
      }

      /**
       * Pretty prints {@link CDOFeatureDelta}, recursing into {@link CDOListFeatureDelta}.
       *
       * @param sb
       *          {@link StringBuilder} where the output is written
       * @param delta
       *          {@link CDOFeatureDelta}
       * @param spacer
       *          the spacer used to increment the output
       * @param numberOfSpacer
       *          the minimal number of spacer in front of a line
       */
      private void printFeatureDelta(StringBuilder sb, CDOFeatureDelta delta, String spacer, int numberOfSpacer)
      {
        if (delta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta list = (CDOListFeatureDelta)delta;
          for (int i = 0; i < numberOfSpacer; i++)
          {
            sb.append(spacer);
          }

          sb.append("CDOListFeatureDelta").append("\n");
          for (CDOFeatureDelta c : list.getListChanges())
          {
            sb.append(spacer);
            printFeatureDelta(sb, c, spacer, numberOfSpacer);
          }
        }
        else
        {
          for (int i = 0; i < numberOfSpacer; i++)
          {
            sb.append(spacer);
          }

          sb.append(delta).append("\n");
        }
      }
    });
  }
}
