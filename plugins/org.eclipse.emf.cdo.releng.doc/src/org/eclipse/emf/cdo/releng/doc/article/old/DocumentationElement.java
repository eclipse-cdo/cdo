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

/**
 * @author Eike Stepper
 */
public interface DocumentationElement
{
  public Documentation getDocumentation();

  public int getLevel();

  public void accept(Visitor visitor) throws Exception;

  /**
   * @author Eike Stepper
   */
  public class Visitor
  {
    public void visit(Documentation documentation) throws Exception
    {
      visitCategoryElement(documentation);
    }

    public void visit(Category category) throws Exception
    {
      visitCategoryElement(category);
    }

    public void visit(Article article) throws Exception
    {
      visitArticleElement(article);
    }

    public void visit(Section section) throws Exception
    {
      visitArticleElement(section);
    }

    public void visitCategoryElement(CategoryElement categoryElement) throws Exception
    {
      for (Category category : categoryElement.getCategories())
      {
        category.accept(this);
      }

      for (Article article : categoryElement.getArticles())
      {
        article.accept(this);
      }

      visitDefault(categoryElement);
    }

    public void visitArticleElement(ArticleElement articleElement) throws Exception
    {
      for (Section section : articleElement.getSections())
      {
        section.accept(this);
      }

      visitDefault(articleElement);
    }

    protected void visitDefault(DocumentationElement object)
    {
    }
  }
}
