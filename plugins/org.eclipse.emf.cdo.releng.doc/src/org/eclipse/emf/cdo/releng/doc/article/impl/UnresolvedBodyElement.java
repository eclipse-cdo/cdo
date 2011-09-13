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
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.util.EList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

/**
 * @author Eike Stepper
 */
public class UnresolvedBodyElement extends BodyElementImpl
{
  UnresolvedBodyElement(Body body, Tag tag)
  {
    super(body, tag);
  }

  public final String getText()
  {
    return "<b><code><font color=\"#ff000000\">{" + getTag() + "}</font></code></b>";
  }

  @Override
  public String getHtml()
  {
    return getText();
  }

  public BodyElement resolve(Context context)
  {
    Tag tag = getTag();
    if (tag instanceof SeeTag)
    {
      SeeTag seeTag = (SeeTag)tag;
      return resolveSeeTag(context, seeTag);
    }

    System.err.println(ArticleUtil.makeConsoleLink("Warning: Unresolved link " + tag + " in ", tag.position()));
    return this;
  }

  private BodyElement resolveSeeTag(Context context, SeeTag tag)
  {
    MemberDoc referencedMember = tag.referencedMember();
    if (referencedMember != null)
    {
      Object target = context.lookup(referencedMember);
      if (target != null)
      {
        return createBodyElement(tag, target);
      }

      return this;
    }

    ClassDoc referencedClass = tag.referencedClass();
    if (referencedClass != null)
    {
      Object target = context.lookup(referencedClass);
      if (target != null)
      {
        return createBodyElement(tag, target);
      }
    }

    return this;
  }

  private BodyElement createBodyElement(SeeTag tag, Object target)
  {
    if (target instanceof StructuralElement)
    {
      return new LinkImpl(null, tag, (StructuralElement)target);
    }

    if (target instanceof EmbeddableElement)
    {
      return new EmbeddingImpl(null, tag, (EmbeddableElement)target);
    }

    return null;
  }

  public static void resolve(Context context, EList<BodyElement> elements)
  {
    for (int i = 0; i < elements.size(); i++)
    {
      BodyElement element = elements.get(i);
      if (element instanceof UnresolvedBodyElement)
      {
        UnresolvedBodyElement unresolved = (UnresolvedBodyElement)element;
        BodyElement resolved = unresolved.resolve(context);
        if (resolved != unresolved)
        {
          try
          {
            elements.set(i, resolved);
          }
          catch (Exception ex)
          {
            resolved = unresolved.resolve(context);
            ex.printStackTrace();
          }
        }
      }
    }
  }
}
