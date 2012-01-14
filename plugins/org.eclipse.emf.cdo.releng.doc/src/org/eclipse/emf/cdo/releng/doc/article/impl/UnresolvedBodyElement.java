/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  public BodyElement copy()
  {
    return new UnresolvedBodyElement(getTag());
  }

  public void generate(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    out.write(getText());
  }

  public List<? extends BodyElement> resolve(Context context)
  {
    List<? extends BodyElement> resolved = null;

    Tag tag = getTag();
    if (tag instanceof SeeTag)
    {
      SeeTag seeTag = (SeeTag)tag;
      resolved = resolveSeeTag(context, seeTag);
    }
    else if (tag.name().equals("@img"))
    {
      resolved = resolveImgTag(context, tag);
    }

    if (resolved != null)
    {
      return resolved;
    }

    System.err.println(ArticleUtil.makeConsoleLink("Unresolved link " + tag + " in ", tag.position()));
    return Collections.singletonList(this);
  }

  private List<? extends BodyElement> resolveSeeTag(Context context, SeeTag tag)
  {
    MemberDoc referencedMember = tag.referencedMember();
    if (referencedMember != null)
    {
      Object target = context.lookup(referencedMember);
      if (target != null)
      {
        return createBodyElements(context, tag, target);
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
        return createBodyElements(context, tag, target);
      }
    }

    return null;
  }

  private Object resolveJavaElement(Context context, ClassDoc classDoc, MemberDoc memberDoc)
  {
    String packageName = classDoc.containingPackage().name();
    for (Documentation documentation : context.getDocumentations())
    {
      File projectFolder = documentation.getProjectFolder();
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

  private List<? extends BodyElement> resolveImgTag(Context context, Tag tag)
  {
    File source = tag.position().file().getParentFile();
    String path = tag.text();

    try
    {
      File target = new File(source, path).getCanonicalFile();
      return Collections.singletonList(new ImageImpl(tag, target));
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  private List<? extends BodyElement> createBodyElements(Context context, SeeTag tag, Object target)
  {
    if (target instanceof LinkTarget)
    {
      if (target instanceof Body && tag.label().equals("!!inline!!"))
      {
        Body body = (Body)target;
        EList<BodyElement> elements = body.getElements();
        resolve(context, elements);

        List<BodyElement> inlined = new ArrayList<BodyElement>();
        for (BodyElement element : elements)
        {
          inlined.add(element.copy());
        }

        return inlined;
      }

      return Collections.singletonList(new LinkImpl(tag, (LinkTarget)target));
    }

    if (target instanceof EmbeddableElement)
    {
      return Collections.singletonList(new EmbeddingImpl(tag, (EmbeddableElement)target));
    }

    return null;
  }

  public static void resolve(Context context, EList<BodyElement> elements)
  {
    List<BodyElement> resolved = new ArrayList<BodyElement>();
    for (int i = 0; i < elements.size(); i++)
    {
      BodyElement element = elements.get(i);
      if (element instanceof UnresolvedBodyElement)
      {
        UnresolvedBodyElement unresolved = (UnresolvedBodyElement)element;
        resolved.addAll(unresolved.resolve(context));
      }
      else
      {
        resolved.add(element);
      }
    }

    elements.clear();
    elements.addAll(resolved);
  }
}
