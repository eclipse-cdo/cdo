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
package org.eclipse.emf.cdo.releng.doc.article.old;

import com.sun.javadoc.ClassDoc;

/**
 * @author Eike Stepper
 */
public class Section extends ArticleElement
{
  private final ArticleElement parent;

  Section(ArticleElement parent, ClassDoc classDoc)
  {
    super(classDoc);
    this.parent = parent;
    getDocumentation().registerArticleElement(this);
  }

  public final ArticleElement getParent()
  {
    return parent;
  }

  public Documentation getDocumentation()
  {
    return getArticle().getDocumentation();
  }

  public Article getArticle()
  {
    if (parent instanceof Article)
    {
      return (Article)parent;
    }

    return ((Section)parent).getArticle();
  }

  public int getLevel()
  {
    return parent.getLevel() + 1;
  }

  public int getArticleLevel()
  {
    return getLevel() - getArticle().getLevel();
  }

  public void accept(Visitor visitor) throws Exception
  {
    visitor.visit(this);
  }
}
