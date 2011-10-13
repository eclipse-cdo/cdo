/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.Text;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.Tag;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Text</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class TextImpl extends BodyElementImpl implements Text
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TextImpl()
  {
    super();
  }

  TextImpl(Tag tag)
  {
    super(tag);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.TEXT;
  }

  public BodyElement copy()
  {
    return new TextImpl(getTag());
  }

  public void generate(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    out.write(getTag().text());
  }

} // TextImpl
