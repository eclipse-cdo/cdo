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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CategoryElement implements DocumentationElement
{
  private final CategoryElement parent;

  private final List<Category> categories = new ArrayList<Category>();

  private final List<Article> articles = new ArrayList<Article>();

  CategoryElement(CategoryElement parent)
  {
    this.parent = parent;
  }

  public final List<Category> getCategories()
  {
    return categories;
  }

  public final Category getCategory(String qualifiedName)
  {
    int dot = qualifiedName.indexOf('.');
    if (dot == -1)
    {
      return basicGetCategory(qualifiedName);
    }

    Category category = basicGetCategory(qualifiedName.substring(0, dot));
    return category.getCategory(qualifiedName.substring(dot + 1));
  }

  private synchronized Category basicGetCategory(String name)
  {
    for (Category category : categories)
    {
      if (category.getName().equals(name))
      {
        return category;
      }
    }

    Category category = new Category(this, name);
    categories.add(category);
    return category;
  }

  public final List<Article> getArticles()
  {
    return articles;
  }

  // TODO needed?
  public final Article getArticle(String id)
  {
    for (Article article : articles)
    {
      if (article.getID().equals(id))
      {
        return article;
      }
    }

    return null;
  }

  public Documentation getDocumentation()
  {
    if (parent instanceof Documentation)
    {
      return (Documentation)parent;
    }

    return ((Category)parent).getDocumentation();
  }

  public final Category getParent()
  {
    if (parent instanceof Category)
    {
      return (Category)parent;
    }

    return null;
  }

  public int getLevel()
  {
    return parent == null ? 0 : parent.getLevel() + 1;
  }

  public abstract String getPath();
}
