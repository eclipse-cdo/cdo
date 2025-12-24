/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import java.util.Collections;

/**
 * Tests performance difference on deletion undo between a XMI and CDO.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "To not be disturbed by other tests")
public class DeletionUndoPerformanceTests extends AbstractCDOTest
{
  private static final int CHILDREN_COUNT = 3;

  private static final int DEPTH = 9;

  public void testDeletionUndoWithXMI() throws Exception
  {
    ResourceSetImpl resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());

    URI uri = URI.createFileURI(createTempFile("test1", ".model1").getCanonicalPath());
    Resource resource = resourceSet.createResource(uri);

    populate(resource, CHILDREN_COUNT, DEPTH);
    testDeletionUndo(resource);
  }

  public void testDeletionUndoWithCDO() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1.model1"));

    populate(resource, CHILDREN_COUNT, DEPTH);
    testDeletionUndo(resource);
  }

  private void populate(Resource resource, long childrenCount, long depth) throws Exception
  {
    NodeA root = getModel3Factory().createNodeA();
    root.setName("" + 1);
    resource.getContents().add(root);

    populate(root, childrenCount, depth);
    resource.save(Collections.emptyMap());

    long elementCount = (long)((1L - Math.pow(childrenCount, depth + 1)) / (1L - childrenCount));
    System.out.println("Populated " + elementCount + " elements");
  }

  private void populate(NodeA root, long childrenCount, long depth) throws Exception
  {
    if (depth > 0)
    {
      for (long i = 0; i < childrenCount; i++)
      {
        NodeA child = getModel3Factory().createNodeA();
        child.setName("" + (Integer.valueOf(root.getName()) + 1 + i));
        populate(child, childrenCount, depth - 1);
        root.getChildren().add(child);
      }
    }
  }

  private void testDeletionUndo(Resource resource)
  {
    NodeA rootNodeA = (NodeA)resource.getContents().get(0);

    TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resource.getResourceSet());

    ((ComposedAdapterFactory)((AdapterFactoryEditingDomain)domain).getAdapterFactory()).addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

    CommandStack commandStack = domain.getCommandStack();

    for (int i = 0; i < 5; i++)
    {
      Command cmd = null;
      for (NodeA nodeA : rootNodeA.getChildren())
      {
        Command deleteCmd = DeleteCommand.create(domain, nodeA);
        if (cmd != null)
        {
          cmd = cmd.chain(deleteCmd);
        }
        else
        {
          cmd = deleteCmd;
        }
      }

      commandStack.execute(cmd);

      long start = System.currentTimeMillis();
      commandStack.undo();
      long duration = System.currentTimeMillis() - start;
      System.err.println("Duration: " + duration);
    }
  }
}
