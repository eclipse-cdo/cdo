/*
 * <copyright>
 * </copyright>
 *
 * $Id$
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
    super(parent, ArticleUtil.getSimplePackageName(packageDoc), packageDoc);
    getDocumentation().getContext().register(getId(), this);

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
  public PackageDoc getDoc()
  {
    return (PackageDoc)super.getDoc();
  }

  @Override
  public void generate() throws IOException
  {
    File sourceFolder = getDoc().position().file().getParentFile();
    copyResources(sourceFolder, getOutputFile());

    super.generate();
    generate(getTocTarget());
  }

  @Override
  public void generate(PrintWriter out) throws IOException
  {
    out.write("<h" + 1 + ">");
    out.write(getTitle());
    out.write("</h" + 1 + ">\n");

    BodyElementContainerImpl.generate(out, this, getElements());
  }

  @Override
  public String linkFrom(StructuralElement source)
  {
    return super.linkFrom(source) + "/index.html";
  }

  @Override
  protected File getTocTarget()
  {
    return new File(super.getTocTarget(), "index.html");
  }
} // CategoryImpl
