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
import org.eclipse.emf.cdo.releng.doc.article.util.RefTag;
import org.eclipse.emf.cdo.releng.doc.article.util.SnippetTag;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class DocumentationResolver extends Visitor
{
  public DocumentationResolver()
  {
  }

  @Override
  public void visitArticleElement(ArticleElement articleElement) throws Exception
  {
    List<Tag> bodyTags = articleElement.getBodyTags();
    Tag[] tags = bodyTags.toArray(new Tag[bodyTags.size()]);
    for (int i = 0; i < tags.length; i++)
    {
      Tag tag = tags[i];
      if (tag instanceof SeeTag)
      {
        SeeTag seeTag = (SeeTag)tag;
        Tag resolvedTag = resolveTag(articleElement, seeTag);
        if (resolvedTag != seeTag)
        {
          bodyTags.set(i, resolvedTag);
        }
      }
    }

    super.visitArticleElement(articleElement);
  }

  private Tag resolveTag(ArticleElement articleElement, SeeTag tag)
  {
    Documentation documentation = articleElement.getDocumentation();

    MemberDoc referencedMember = tag.referencedMember();
    if (referencedMember != null)
    {
      ClassDoc referencedClass = referencedMember.containingClass();
      if (documentation.isTutorialClass(referencedClass))
      {
        // Inline example method
        return new SnippetTag(tag, referencedMember.position(), false);
      }

      // Refer to Java method
      return tag;
    }

    ClassDoc referencedClass = tag.referencedClass();
    if (referencedClass != null)
    {
      if (documentation.isTutorialClass(referencedClass))
      {
        if (documentation.isExampleClass(referencedClass))
        {
          // Inline example class
          return new SnippetTag(tag, referencedClass.position(), true);
        }

        // Refer to tutorial section
        ArticleElement target = documentation.getArticleElements().get(referencedClass);
        if (target == null)
        {
          throw new ArticleException("Unresolved reference: " + referencedClass);
        }

        return new RefTag(tag, target);
      }

      // Refer to Java class
      return tag;
    }

    return tag;
  }
}
