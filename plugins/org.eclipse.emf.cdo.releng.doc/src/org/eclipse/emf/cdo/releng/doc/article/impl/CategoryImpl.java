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

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Category</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class CategoryImpl extends BodyImpl implements Category
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CategoryImpl()
  {
    super();
  }

  CategoryImpl(StructuralElement parent, PackageDoc packageDoc)
  {
    super(parent, ArticleUtil.getSimplePackageName(packageDoc) + "/index.html", packageDoc);

    for (Tag tag : packageDoc.inlineTags())
    {
      if (tag.name().equals("@toc"))
      {
        return;
      }
    }

    elements.add(new TocImpl(null));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.CATEGORY;
  }

  @Override
  protected String getKind()
  {
    return "Category";
  }

  @Override
  protected void collectNavElements(List<StructuralElement> navElements)
  {
    navElements.add(this);
    super.collectNavElements(navElements);
  }

  @Override
  public PackageDoc getDoc()
  {
    return (PackageDoc)super.getDoc();
  }

  @Override
  public void generate() throws IOException
  {
    File sourceFolder = getDoc().position().file().getParentFile();
    copyResources(sourceFolder);

    super.generate();
    generate(getTocTarget());
  }

  @Override
  protected void generateBreadCrumbs(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    super.generateBreadCrumbs(out, linkSource);

    if (linkSource != this)
    {
      out.write(" > ");
      generateLink(out, linkSource, null);
    }
  }

  @Override
  public void generate(PrintWriter out) throws IOException
  {
    generateHeader(out);
    BodyElementContainerImpl.generate(out, this, getElements());
    generateFooter(out);
  }
} // CategoryImpl
