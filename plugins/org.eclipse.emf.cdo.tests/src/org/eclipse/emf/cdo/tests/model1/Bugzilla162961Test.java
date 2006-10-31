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
 * CDO objects not loading correctly.
 * 
 * If I can use the analogy of the library that will help. 
 * Assume that I have a class for Library which 'contains' all books. 
 * I also have a class, or classes, for genre that may have non-containment 
 * references to books. So a book could have references to-from a genre, 
 * and always has a reference to the Library, which is containment.
 *
 * Now I want to load all the books so I call library.getBooks(). 
 * This works consistently. And if I subsequently, during the same session, 
 * load  a genre with a call to fiction.getBooks() that works as well.
 *
 * However, if I restart to create a new session, and do the genre load 
 * first I will see the books in the genre loaded correctly, 
 * but a subsequent call to load all the books in the library will not 
 * have any of the books initially loaded in the genre.
 *
 * This is happening consistently, every time.
 *
 * It appears that loading the containment references from Library is doing 
 * something a bit different than the load of non-containment references 
 * for the genre. If the initial load is via containment all the non-containment 
 * references are correct. However, if the initial load is via non-containment 
 * references then the other references, specifically the containment reference, 
 * is not correct.
 *
 * I suspect this is strictly a client side issue since the object is 
 * loaded from the server. It seems to be the handling of setting all 
 * the references for an object is not correct.
 * 
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=162961
 */
public class Bugzilla162961Test extends AbstractModel1Test
{
  public void testViaContainment() throws Exception
  {
    {
      TreeNode root = createNode("root");
      TreeNode books = createNode("books", root);
      TreeNode genres = createNode("genres", root);

      ExtendedNode book0 = createExtended("book0", books);
      ExtendedNode book1 = createExtended("book1", books);
      ExtendedNode book2 = createExtended("book2", books);
      ExtendedNode book3 = createExtended("book3", books);

      ExtendedNode genre0 = createExtended("genre0", genres);
      ExtendedNode genre1 = createExtended("genre1", genres);
      ExtendedNode genre2 = createExtended("genre2", genres);
      ExtendedNode genre3 = createExtended("genre3", genres);

      book0.getBidiSource().add(genre0);
      book0.getBidiSource().add(genre1);
      book0.getBidiSource().add(genre2);
      book0.getBidiSource().add(genre3);

      book1.getBidiSource().add(genre0);
      book1.getBidiSource().add(genre1);
      book1.getBidiSource().add(genre2);

      book2.getBidiSource().add(genre0);
      book2.getBidiSource().add(genre1);

      book3.getBidiSource().add(genre0);
      saveRoot(root, "/test/res");
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");
      TreeNode books = findChild("books", root);
      EList children = books.getChildren();
      assertEquals("book0", ((ExtendedNode) children.get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) children.get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) children.get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) children.get(3)).getStringFeature());
    }
  }

  public void testViaXRef() throws Exception
  {
    {
      TreeNode root = createNode("root");
      TreeNode books = createNode("books", root);
      TreeNode genres = createNode("genres", root);

      ExtendedNode book0 = createExtended("book0", books);
      ExtendedNode book1 = createExtended("book1", books);
      ExtendedNode book2 = createExtended("book2", books);
      ExtendedNode book3 = createExtended("book3", books);

      ExtendedNode genre0 = createExtended("genre0", genres);
      ExtendedNode genre1 = createExtended("genre1", genres);
      ExtendedNode genre2 = createExtended("genre2", genres);
      ExtendedNode genre3 = createExtended("genre3", genres);

      book0.getBidiSource().add(genre0);
      book0.getBidiSource().add(genre1);
      book0.getBidiSource().add(genre2);
      book0.getBidiSource().add(genre3);

      book1.getBidiSource().add(genre0);
      book1.getBidiSource().add(genre1);
      book1.getBidiSource().add(genre2);

      book2.getBidiSource().add(genre0);
      book2.getBidiSource().add(genre1);

      book3.getBidiSource().add(genre0);
      saveRoot(root, "/test/res");
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");
      TreeNode genres = findChild("genres", root);
      ExtendedNode genre0 = (ExtendedNode) genres.getChildren().get(0);
      assertEquals("book0", ((ExtendedNode) genre0.getBidiTarget().get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) genre0.getBidiTarget().get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) genre0.getBidiTarget().get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) genre0.getBidiTarget().get(3)).getStringFeature());

      TreeNode books = findChild("books", root);
      EList children = books.getChildren();
      assertEquals("book0", ((ExtendedNode) children.get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) children.get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) children.get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) children.get(3)).getStringFeature());
    }
  }

  public void testViaXRefBooksFirst() throws Exception
  {
    {
      TreeNode root = createNode("root");
      TreeNode books = createNode("books", root);
      TreeNode genres = createNode("genres", root);

      ExtendedNode book0 = createExtended("book0", books);
      ExtendedNode book1 = createExtended("book1", books);
      ExtendedNode book2 = createExtended("book2", books);
      ExtendedNode book3 = createExtended("book3", books);

      ExtendedNode genre0 = createExtended("genre0", genres);
      ExtendedNode genre1 = createExtended("genre1", genres);
      ExtendedNode genre2 = createExtended("genre2", genres);
      ExtendedNode genre3 = createExtended("genre3", genres);

      book0.getBidiSource().add(genre0);
      book0.getBidiSource().add(genre1);
      book0.getBidiSource().add(genre2);
      book0.getBidiSource().add(genre3);

      book1.getBidiSource().add(genre0);
      book1.getBidiSource().add(genre1);
      book1.getBidiSource().add(genre2);

      book2.getBidiSource().add(genre0);
      book2.getBidiSource().add(genre1);

      book3.getBidiSource().add(genre0);
      saveRoot(root, "/test/res");
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");
      TreeNode books = findChild("books", root);
      TreeNode genres = findChild("genres", root);

      ExtendedNode genre0 = (ExtendedNode) genres.getChildren().get(0);
      assertEquals("book0", ((ExtendedNode) genre0.getBidiTarget().get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) genre0.getBidiTarget().get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) genre0.getBidiTarget().get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) genre0.getBidiTarget().get(3)).getStringFeature());

      EList children = books.getChildren();
      assertEquals("book0", ((ExtendedNode) children.get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) children.get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) children.get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) children.get(3)).getStringFeature());
    }
  }

  public void testViaXRefSharedRoot() throws Exception
  {
    {
      TreeNode root = createNode("root");

      ExtendedNode book0 = createExtended("book0", root);
      ExtendedNode book1 = createExtended("book1", root);
      ExtendedNode book2 = createExtended("book2", root);
      ExtendedNode book3 = createExtended("book3", root);

      ExtendedNode genre0 = createExtended("genre0", root);
      ExtendedNode genre1 = createExtended("genre1", root);
      ExtendedNode genre2 = createExtended("genre2", root);
      ExtendedNode genre3 = createExtended("genre3", root);

      book0.getBidiSource().add(genre0);
      book0.getBidiSource().add(genre1);
      book0.getBidiSource().add(genre2);
      book0.getBidiSource().add(genre3);

      book1.getBidiSource().add(genre0);
      book1.getBidiSource().add(genre1);
      book1.getBidiSource().add(genre2);

      book2.getBidiSource().add(genre0);
      book2.getBidiSource().add(genre1);

      book3.getBidiSource().add(genre0);
      saveRoot(root, "/test/res");
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");

      ExtendedNode genre0 = (ExtendedNode) root.getChildren().get(4);
      assertEquals("book0", ((ExtendedNode) genre0.getBidiTarget().get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) genre0.getBidiTarget().get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) genre0.getBidiTarget().get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) genre0.getBidiTarget().get(3)).getStringFeature());

      EList children = root.getChildren();
      assertEquals("book0", ((ExtendedNode) children.get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) children.get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) children.get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) children.get(3)).getStringFeature());
    }
  }

  public void testViaXRefChildren2() throws Exception
  {
    {
      TreeNode root = createNode("root");

      // Insert books into children
      ExtendedNode book0 = createExtended("book0", root);
      ExtendedNode book1 = createExtended("book1", root);
      ExtendedNode book2 = createExtended("book2", root);
      ExtendedNode book3 = createExtended("book3", root);

      // Insert genres into children2
      ExtendedNode genre0 = createExtended("genre0");
      genre0.setParent2(root);
      ExtendedNode genre1 = createExtended("genre1");
      genre1.setParent2(root);
      ExtendedNode genre2 = createExtended("genre2");
      genre2.setParent2(root);
      ExtendedNode genre3 = createExtended("genre3");
      genre3.setParent2(root);

      book0.getBidiSource().add(genre0);
      book0.getBidiSource().add(genre1);
      book0.getBidiSource().add(genre2);
      book0.getBidiSource().add(genre3);

      book1.getBidiSource().add(genre0);
      book1.getBidiSource().add(genre1);
      book1.getBidiSource().add(genre2);

      book2.getBidiSource().add(genre0);
      book2.getBidiSource().add(genre1);

      book3.getBidiSource().add(genre0);
      saveRoot(root, "/test/res");
    }

    {
      TreeNode root = (TreeNode) loadRoot("/test/res");

      ExtendedNode genre0 = (ExtendedNode) root.getChildren2().get(0);
      assertEquals("book0", ((ExtendedNode) genre0.getBidiTarget().get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) genre0.getBidiTarget().get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) genre0.getBidiTarget().get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) genre0.getBidiTarget().get(3)).getStringFeature());

      EList children = root.getChildren();
      assertEquals("book0", ((ExtendedNode) children.get(0)).getStringFeature());
      assertEquals("book1", ((ExtendedNode) children.get(1)).getStringFeature());
      assertEquals("book2", ((ExtendedNode) children.get(2)).getStringFeature());
      assertEquals("book3", ((ExtendedNode) children.get(3)).getStringFeature());
    }
  }
}
