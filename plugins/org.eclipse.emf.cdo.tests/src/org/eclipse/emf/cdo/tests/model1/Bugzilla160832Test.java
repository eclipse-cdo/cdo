/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.model1;


import org.eclipse.emf.common.util.EList;

import testmodel1.ExtendedNode;
import testmodel1.TreeNode;


/**
 * Object deletion leaves some opposite references.
 * 
 * Mark:
 * I started looking at the CDO code and found that in the serve side code
 * for detaching an object there is a call to removeReferences(). However,
 * this code only removed a single row from the database. Should it not
 * also remove the matching row for the opposite reference.??
 * 
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=160832
 */
public class Bugzilla160832Test extends AbstractModel1Test
{
  public void testDeleteSubTree() throws Exception
  {
    final int CHILDCOUNT = 10;

    {
      TreeNode root = createNode("root");
      TreeNode node0 = createExtended("node0", root);
      TreeNode node1 = createExtended("node1", root);
      for (int i = 0; i < CHILDCOUNT; i++)
      {
        ExtendedNode child = createExtended("child" + i, node0);
        child.getBidiSource().add(node1);
      }

      saveRoot(root, "/test/res");
      assertEquals(4 + 4 * CHILDCOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");
      EList nodes = root.getChildren();
      assertEquals(2, nodes.size());

      ExtendedNode node0 = (ExtendedNode) nodes.get(0);
      assertEquals("node0", node0.getStringFeature());
      assertEquals(CHILDCOUNT, node0.getChildren().size());

      ExtendedNode node1 = (ExtendedNode) nodes.get(1);
      assertEquals("node1", node1.getStringFeature());
      assertEquals(CHILDCOUNT, node1.getBidiTarget().size());

      root.getChildren().remove(0);
      root.eResource().save(null);
      assertEquals(2, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");
      EList nodes = root.getChildren();
      assertEquals(1, nodes.size());

      ExtendedNode node1 = (ExtendedNode) nodes.get(0);
      assertEquals("node1", node1.getStringFeature());
      assertEquals(0, node1.getBidiTarget().size());
    }
  }

  //  public void testUnsetNonExistingRefs()
  //  {
  //  }
}
