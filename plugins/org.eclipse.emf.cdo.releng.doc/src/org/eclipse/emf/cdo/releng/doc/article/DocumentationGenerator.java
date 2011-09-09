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

import org.eclipse.emf.cdo.releng.doc.article.DocumentationElement.Visitor;
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;
import org.eclipse.emf.cdo.releng.doc.article.util.RefTag;

import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DocumentationGenerator extends Visitor
{
  private HtmlWriter writer;

  public DocumentationGenerator()
  {
  }

  @Override
  public void visit(Article article) throws Exception
  {
    try
    {
      File outputFolder = article.getDocumentation().getOutputFolder();
      File file = new File(outputFolder, article.getPath() + ".html");
      file.getParentFile().mkdirs();
      writer = new HtmlWriter(file);

      generate(1, article);
      super.visit(article);
    }
    finally
    {
      HtmlWriter.close(writer);
    }
  }

  @Override
  public void visit(Section section) throws Exception
  {
    generate(section.getArticleLevel() + 1, section);
    super.visit(section);
  }

  private void generate(int level, ArticleElement articleElement) throws IOException
  {
    writer.writeHeading(level, articleElement.getTitle());
    writer.write("<a name=\"");
    writer.write(articleElement.getClassDoc().typeName());
    writer.write("\"/>");

    for (Tag tag : articleElement.getBodyTags())
    {
      if (tag instanceof RefTag)
      {
        RefTag refTag = (RefTag)tag;
        ArticleElement target = refTag.getTarget();

        StringBuilder href = new StringBuilder();
        for (int i = 0; i < articleElement.getLevel(); i++)
        {
          href.append("../");
        }

        String sourcePath = target.getClassDoc().position().file().getCanonicalPath();
        int baseLength = articleElement.getDocumentation().getBaseFolder().getCanonicalPath().length();
        String path = sourcePath.substring(baseLength + 1).replace('\\', '/');
        path = path.substring(0, path.length() - ".java".length());
        href.append(path);
        href.append(".html");

        if (target instanceof Section)
        {
          Section section = (Section)target;
          href.append("#");
          href.append(section.getClassDoc().typeName());
        }

        writer.writeHRef(href.toString(), tag.text());
      }
      else
      {
        writer.write(tag.text());
      }
    }

    writer.write("\n\n");
  }
}
