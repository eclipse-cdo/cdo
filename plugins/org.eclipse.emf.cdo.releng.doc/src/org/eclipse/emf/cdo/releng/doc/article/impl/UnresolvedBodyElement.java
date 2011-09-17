/*
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

import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;
import org.eclipse.emf.cdo.releng.doc.article.LinkTarget;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.util.EList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Eike Stepper
 */
public class UnresolvedBodyElement extends BodyElementImpl
{
  private static final boolean DEBUG = false;

  UnresolvedBodyElement(Tag tag)
  {
    super(tag);
  }

  public final String getText()
  {
    if (DEBUG)
    {
      return "<b><code><font color=\"#ff000000\">{" + getTag() + "}</font></code></b>";
    }

    return getTag().text();
  }

  public void generate(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    out.write(getText());
  }

  public BodyElement resolve(Context context)
  {
    Tag tag = getTag();
    if (tag instanceof SeeTag)
    {
      SeeTag seeTag = (SeeTag)tag;
      BodyElement resolved = resolveSeeTag(context, seeTag);
      if (resolved != null)
      {
        return resolved;
      }
    }

    System.err.println(ArticleUtil.makeConsoleLink("Unresolved link " + tag + " in ", tag.position()));
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
    }

    ClassDoc referencedClass = tag.referencedClass();
    if (referencedClass != null)
    {
      Object target = context.lookup(referencedClass);
      if (target == null)
      {
        target = resolveJavaElement(context, referencedClass, referencedMember);
      }

      if (target != null)
      {
        return createBodyElement(tag, target);
      }
    }

    return null;
  }

  private Object resolveJavaElement(Context context, ClassDoc classDoc, MemberDoc memberDoc)
  {
    String packageName = classDoc.containingPackage().name();
    for (Documentation documentation : context.getDocumentations())
    {
      File projectFolder = documentation.getOutputFile().getParentFile();
      File javadocFolder = new File(projectFolder, "javadoc");
      File packageFolder = new File(javadocFolder, packageName.replace('.', '/'));
      File classFile = new File(packageFolder, classDoc.typeName() + ".html");
      if (classFile.isFile())
      {
        return new JavaElementImpl(documentation, classDoc, classFile);
      }
    }

    String externalLink = context.getExternalLink(packageName);
    if (externalLink != null)
    {
      String url = externalLink + "/" + classDoc.typeName() + ".html";
      return new ExternalTargetImpl(context, classDoc, url);
    }

    return null;
  }

  private BodyElement createBodyElement(SeeTag tag, Object target)
  {
    if (target instanceof LinkTarget)
    {
      return new LinkImpl(tag, (LinkTarget)target);
    }

    if (target instanceof EmbeddableElement)
    {
      return new EmbeddingImpl(tag, (EmbeddableElement)target);
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
