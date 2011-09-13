/**
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

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Category</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class CategoryImpl extends BodyImpl implements Category
{
  private PackageDoc packageDoc;

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
    this.packageDoc = packageDoc;
    getDocumentation().getContext().register(getId(), this);
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
  public PackageDoc getDoc()
  {
    return (PackageDoc)super.getDoc();
  }
} // CategoryImpl
