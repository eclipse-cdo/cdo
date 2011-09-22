/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.PluginResource;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;

import java.io.File;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Plugin Resource</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class PluginResourceImpl extends ExternalArticleImpl implements PluginResource
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected PluginResourceImpl()
  {
    super();
  }

  public PluginResourceImpl(StructuralElement parent, ClassDoc classDoc, String url)
  {
    super(parent, classDoc, url);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.PLUGIN_RESOURCE;
  }

  @Override
  public String linkFrom(StructuralElement source)
  {
    File sourceFile = source.getOutputFile();
    File targetFile = new File(getDocumentation().getProjectFolder(), getUrl());
    return ArticleUtil.createLink(sourceFile, targetFile);
  }
} // PluginResourceImpl
