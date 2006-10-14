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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import testmodel1.ExtendedNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BidiReferencesTest extends AbstractModel1Test
{
  private static final int LEVELS = 3;

  private static final int CHILDREN = 4;

  private static final int NODECOUNT = getNodeCount(LEVELS, CHILDREN);

  private static final int CHILDCOUNT = NODECOUNT - 1;

  public void testBasicTree() throws Exception
  {
    ExtendedNode created = createExtended("node");
    createTree(created, LEVELS, CHILDREN);
    saveRoot(created, "/test/resource");

    ExtendedNode loaded = (ExtendedNode) loadRoot("/test/resource");
    assertTree("node", loaded, LEVELS, CHILDREN);

    assertEquals(1, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_RESOURCE"));
    assertEquals(NODECOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_OBJECT"));
    assertEquals(NODECOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM EXTENDED_NODE"));
    assertEquals(CHILDCOUNT * 2, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    assertEquals(CHILDCOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE WHERE CONTENT"));
  }

  public void testBasicBidis() throws Exception
  {
    ExtendedNode created = createExtended("node");
    createTree(created, LEVELS, CHILDREN);
    createBidi(created, "111", "211");
    createBidi(created, "111", "212");
    createBidi(created, "111", "213");
    createBidi(created, "111", "311");
    createBidi(created, "111", "312");
    createBidi(created, "111", "313");
    saveRoot(created, "/test/resource");

    ExtendedNode loaded = (ExtendedNode) loadRoot("/test/resource");
    assertTree("node", loaded, LEVELS, CHILDREN);
    assertBidi(loaded, "111", "211");
    assertBidi(loaded, "111", "212");
    assertBidi(loaded, "111", "213");
    assertBidi(loaded, "111", "311");
    assertBidi(loaded, "111", "312");
    assertBidi(loaded, "111", "313");

    assertEquals(1, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_RESOURCE"));
    assertEquals(NODECOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_OBJECT"));
    assertEquals(NODECOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM EXTENDED_NODE"));
    assertEquals(CHILDCOUNT * 2 + 6 * 2, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    assertEquals(CHILDCOUNT, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE WHERE CONTENT"));
  }

  public void testDetachCount() throws Exception
  {
    ExtendedNode created = createExtended("node"); // root node
    createTree(created, LEVELS, CHILDREN); // tree of degree 4, height = root + 3
    saveRoot(created, "/test/resource"); // commit
    int expected = NODECOUNT;
    assertEquals(expected, getNodeCount(created));
    created.cdoGetResource().getResourceManager().stop();

    ExtendedNode loaded = (ExtendedNode) loadRoot("/test/resource"); // load another copy
    assertEquals(expected, getNodeCount(loaded));
    detachNode(loaded, "211"); // delete a leaf node
    loaded.eResource().save(null); // commit
    expected -= 1;
    assertEquals(expected, getNodeCount(loaded));

    detachNode(loaded, "4"); // delete a subtree
    loaded.eResource().save(null); // commit
    expected -= getNodeCount(LEVELS - 1, CHILDREN);
    assertEquals(expected, getNodeCount(loaded));
  }

  public void testDetachWithoutBidi() throws Exception
  {
    ExtendedNode created = createExtended("node"); // root node
    createTree(created, LEVELS, CHILDREN); // tree of degree 4, height = root + 3
    saveRoot(created, "/test/resource"); // commit
    int refs = CHILDCOUNT * 2; // expect num of refs = 2 per child
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    created.cdoGetResource().getResourceManager().stop();

    ExtendedNode loaded = (ExtendedNode) loadRoot("/test/resource"); // load another copy
    detachNode(loaded, "211"); // delete a leaf node
    loaded.eResource().save(null); // commit
    refs -= 2; // expect -2 containment refs 
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));

    detachNode(loaded, "4"); // delete a subtree
    loaded.eResource().save(null); // commit
    refs -= getNodeCount(LEVELS - 1, CHILDREN) * 2;
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
  }

  public void testDetachWithBidiSimple() throws Exception
  {
    ExtendedNode node = createExtended("node"); // root node
    ExtendedNode node1 = createExtended("node1", node); // child node 1
    ExtendedNode node2 = createExtended("node2", node); // child node 2

    saveRoot(node, "/test/resource"); // commit
    assertEquals(4, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));

    node1.getBidiSource().add(node2);
    node.eResource().save(null); // commit
    assertEquals(6, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));

    node.getChildren().remove(node1);
    EList bidiTarget = node2.getBidiTarget();
    assertTrue(bidiTarget.isEmpty());
    node.eResource().save(null); // commit
    assertEquals(2, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
  }

  public void testDetachWithBidi() throws Exception
  {
    ExtendedNode created = createExtended("node"); // root node
    createTree(created, LEVELS, CHILDREN); // tree of degree 4, height = root + 3
    saveRoot(created, "/test/resource"); // commit
    int refs = CHILDCOUNT * 2; // expect num of refs = 2 per child
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));

    // create 12 bidi refs
    createBidi(created, "111", "211");
    createBidi(created, "111", "212");
    createBidi(created, "111", "213");
    createBidi(created, "111", "214");
    createBidi(created, "111", "311");
    createBidi(created, "111", "312");
    createBidi(created, "111", "313");
    createBidi(created, "111", "314");
    createBidi(created, "111", "411");
    createBidi(created, "111", "412");
    createBidi(created, "111", "413");
    createBidi(created, "111", "414");
    created.eResource().save(null); // commit
    refs += 12 * 2; // expect 24 additional refs
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    created.cdoGetResource().getResourceManager().stop();

    ExtendedNode loaded = (ExtendedNode) loadRoot("/test/resource"); // load another copy
    detachNode(loaded, "211"); // delete a leaf node with 1 bidi ref
    loaded.eResource().save(null); // commit
    refs -= 2 + 2; // expect -2 containment refs, -2 bidi refs 
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
  }

  public void testRemoveBidi() throws Exception
  {
    ExtendedNode created = createExtended("node"); // root node
    createTree(created, LEVELS, CHILDREN); // tree of degree 4, height = root + 3
    saveRoot(created, "/test/resource"); // commit
    int refs = CHILDCOUNT * 2; // expect num of refs = 2 per child
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));

    // create 12 bidi refs
    createBidi(created, "111", "211");
    createBidi(created, "111", "212");
    createBidi(created, "111", "213");
    createBidi(created, "111", "214");
    createBidi(created, "111", "311");
    createBidi(created, "111", "312");
    createBidi(created, "111", "313");
    createBidi(created, "111", "314");
    createBidi(created, "111", "411");
    createBidi(created, "111", "412");
    createBidi(created, "111", "413");
    createBidi(created, "111", "414");
    created.eResource().save(null); // commit
    refs += 12 * 2; // expect 24 additional refs
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
    created.cdoGetResource().getResourceManager().stop();

    ExtendedNode loaded = (ExtendedNode) loadRoot("/test/resource"); // load another copy
    ExtendedNode node211 = findPath(loaded, "211");
    node211.getBidiSource().clear();
    loaded.eResource().save(null); // commit
    refs -= 2; // expect -2 bidi refs 
    assertEquals(refs, jdbc().queryForInt("SELECT COUNT(*) FROM CDO_REFERENCE"));
  }

  private void detachNode(ExtendedNode root, String path)
  {
    ExtendedNode node = findPath(root, path);
    EList children = node.getParent().getChildren();
    int size = children.size();
    children.remove(node);
    assertEquals(size - 1, children.size());
  }

  private void createBidi(ExtendedNode root, String path1, String path2)
  {
    ExtendedNode src = findPath(root, path1);
    ExtendedNode dst = findPath(root, path2);
    dst.getBidiSource().add(src);
  }

  private void assertBidi(ExtendedNode root, String path1, String path2)
  {
    ExtendedNode src = findPath(root, path1);
    ExtendedNode dst = findPath(root, path2);
    assertTrue(dst.getBidiSource().contains(src));
    assertTrue(src.getBidiTarget().contains(dst));
  }

  private ExtendedNode findPath(ExtendedNode root, String path)
  {
    String name = "node";
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < path.length(); i++)
    {
      name += path.charAt(i);
      list.add(name);
    }

    return (ExtendedNode) findPath(list.toArray(new String[list.size()]), root);
  }

  private void assertTree(String name, ExtendedNode root, int levels, int children)
  {
    assertNode(name, root);
    if (levels > 1)
    {
      assertEquals(children, root.getChildren().size());
      for (int i = 0; i < children; i++)
      {
        assertTree(name + (i + 1), (ExtendedNode) root.getChildren().get(i), levels - 1, children);
      }
    }
  }

  private static int getNodeCount(EObject root)
  {
    int count = 1;
    for (Iterator it = EcoreUtil.getAllContents(root, true); it.hasNext();)
    {
      it.next();
      ++count;
    }

    return count;
  }

  private static int getNodeCount(int levels, int children)
  {
    int level = children;
    int result = level;
    for (int i = 1; i < levels; i++)
    {
      level *= children;
      result += level;
    }

    return result + 1;
  }
}
