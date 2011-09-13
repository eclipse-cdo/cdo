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

import org.eclipse.emf.cdo.releng.doc.article.old.util.TextTag;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleException;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class ArticleElement implements DocumentationElement
{
  private final ClassDoc classDoc;

  private final String title;

  private final List<Tag> bodyTags = new ArrayList<Tag>();

  private final List<Section> sections = new ArrayList<Section>();

  ArticleElement(ClassDoc classDoc)
  {
    this.classDoc = classDoc;

    bodyTags.addAll(Arrays.asList(classDoc.inlineTags()));
    if (bodyTags.isEmpty())
    {
      System.err.println("Warning: Title is missing in " + classDoc.position());
      title = classDoc.name();
      return;
    }

    final Tag firstTag = bodyTags.remove(0);
    if (!firstTag.name().equals("Text"))
    {
      throw new ArticleException("First tag is not text: " + firstTag.position());
    }

    String text = firstTag.text();
    int blockPos = getBlockPosition(text);
    if (blockPos == -1)
    {
      if (!bodyTags.isEmpty())
      {
        throw new ArticleException("Non-text tags not allowed in title: " + firstTag.position());
      }

      title = text.replaceAll("\\s+", " ").trim();
    }
    else
    {
      title = text.substring(0, blockPos).replaceAll("\\s+", " ").trim();

      final String rest = text.substring(blockPos);
      bodyTags.add(0, new TextTag(firstTag, rest));
    }
  }

  private int getBlockPosition(String text)
  {
    String[] blocks = { "p", "br", "hr", "ul", "ol", "div", "table" };
    int result = Integer.MAX_VALUE;

    for (String block : blocks)
    {
      int index = text.indexOf("<" + block);
      if (index != -1 && index < result)
      {
        result = index;
      }
    }

    return result == Integer.MAX_VALUE ? -1 : result;
  }

  public final String getID()
  {
    return classDoc.qualifiedTypeName();
  }

  public final ClassDoc getClassDoc()
  {
    return classDoc;
  }

  public final String getTitle()
  {
    return title;
  }

  public final List<Tag> getBodyTags()
  {
    return bodyTags;
  }

  public final List<Section> getSections()
  {
    return sections;
  }

  @Override
  public String toString()
  {
    return title;
  }
}
