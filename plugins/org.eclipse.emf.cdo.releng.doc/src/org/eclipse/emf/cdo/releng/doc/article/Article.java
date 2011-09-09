/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article;

import com.sun.javadoc.ClassDoc;


/**
 * @author Eike Stepper
 */
public class Article extends ArticleElement
{
  private final CategoryElement parent;

  Article(Documentation documentation, ClassDoc classDoc)
  {
    super(classDoc);
    parent = documentation;
    getDocumentation().registerArticleElement(this);
  }

  Article(Category category, ClassDoc classDoc)
  {
    super(classDoc);
    parent = category;
    getDocumentation().registerArticleElement(this);
  }

  public final Documentation getDocumentation()
  {
    if (parent instanceof Documentation)
    {
      return (Documentation)parent;
    }

    return ((Category)parent).getDocumentation();
  }

  public final Category getCategory()
  {
    if (parent instanceof Category)
    {
      return (Category)parent;
    }

    return null;
  }

  public int getLevel()
  {
    return parent.getLevel() + 1;
  }

  public void accept(Visitor visitor) throws Exception
  {
    visitor.visit(this);
  }

  public String getPath()
  {
    String name = getClassDoc().typeName();

    String path = parent.getPath();
    if (path.length() == 0)
    {
      return name;
    }

    return path + "/" + name;
  }
}
