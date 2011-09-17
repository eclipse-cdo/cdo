/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.Embedding;
import org.eclipse.emf.cdo.releng.doc.article.Factory;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.MethodDoc;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Factory</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class FactoryImpl extends EmbeddableElementImpl implements Factory
{
  private MethodDoc methodDoc;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected FactoryImpl()
  {
    super();
  }

  FactoryImpl(Documentation documentation, MethodDoc methodDoc)
  {
    setDocumentation(documentation);
    this.methodDoc = methodDoc;
    documentation.getContext().register(getId(), this);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.FACTORY;
  }

  public void generate(PrintWriter out, Embedding embedder) throws IOException
  {
    // TODO: implement FactoryImpl.generate(out, embedder)
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getId()
  {
    return methodDoc;
  }
} // FactoryImpl
