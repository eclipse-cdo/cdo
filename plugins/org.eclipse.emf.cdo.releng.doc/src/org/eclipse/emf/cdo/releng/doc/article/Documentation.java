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
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Documentation extends CategoryElement
{
  private final RootDoc root;

  private final File baseFolder;

  private final String outputPath;

  private final Map<ClassDoc, ArticleElement> articleElements = new HashMap<ClassDoc, ArticleElement>();

  Documentation(RootDoc root, String base, String outputPath) throws IOException
  {
    super(null);
    this.root = root;
    this.outputPath = outputPath;

    baseFolder = new File(base).getCanonicalFile();
    if (!baseFolder.exists())
    {
      if (!baseFolder.mkdirs())
      {
        throw new IOException("Could not create " + base);
      }
    }
  }

  void registerArticleElement(ArticleElement articleElement)
  {
    articleElements.put(articleElement.getClassDoc(), articleElement);
  }

  boolean isTutorialClass(ClassDoc classDoc)
  {
    for (ClassDoc doc : root.classes())
    {
      if (doc == classDoc)
      {
        return true;
      }
    }

    return false;
  }

  boolean isExampleClass(ClassDoc classDoc)
  {
    Tag[] exampleTags = classDoc.tags("@example");
    return exampleTags != null && exampleTags.length != 0;
  }

  @Override
  public Documentation getDocumentation()
  {
    return this;
  }

  public final Map<ClassDoc, ArticleElement> getArticleElements()
  {
    return articleElements;
  }

  public final RootDoc getRoot()
  {
    return root;
  }

  public final File getBaseFolder()
  {
    return baseFolder;
  }

  public final String getOutputPath()
  {
    return outputPath;
  }

  public File getOutputFolder()
  {
    return new File(baseFolder, outputPath);
  }

  @Override
  public String getPath()
  {
    return "";
  }

  @Override
  public String toString()
  {
    return Documentation.class.getSimpleName();
  }

  public void accept(Visitor visitor) throws Exception
  {
    visitor.visit(this);
  }

  public static Documentation create(RootDoc root, String base, String outputPath) throws Exception
  {
    Documentation documentation = new Documentation(root, base, outputPath);
    processTopLevelClasses(documentation);
    documentation.accept(new DocumentationResolver());

    return documentation;
  }

  private static void processTopLevelClasses(Documentation documentation)
  {
    for (ClassDoc classDoc : documentation.getRoot().classes())
    {
      if (classDoc.containingClass() == null && !classDoc.isPackagePrivate())
      {
        PackageDoc containingPackage = classDoc.containingPackage();
        Category category = documentation.getCategory(containingPackage.name());
        category.setPackageDoc(containingPackage);

        Article article = new Article(category, classDoc);
        category.getArticles().add(article);
        processClass(article);
      }
    }
  }

  private static void processClass(ArticleElement parent)
  {
    for (ClassDoc classDoc : parent.getClassDoc().innerClasses())
    {
      Section section = new Section(parent, classDoc);
      parent.getSections().add(section);
      processClass(section);
    }
  }
}
