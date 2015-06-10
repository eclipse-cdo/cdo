/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.util;

import org.eclipse.emf.cdo.releng.doc.article.Article;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;
import org.eclipse.emf.cdo.releng.doc.article.Callout;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.Chapter;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.Diagram;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;
import org.eclipse.emf.cdo.releng.doc.article.Embedding;
import org.eclipse.emf.cdo.releng.doc.article.Excel;
import org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint;
import org.eclipse.emf.cdo.releng.doc.article.ExternalArticle;
import org.eclipse.emf.cdo.releng.doc.article.ExternalTarget;
import org.eclipse.emf.cdo.releng.doc.article.Factory;
import org.eclipse.emf.cdo.releng.doc.article.Formatter;
import org.eclipse.emf.cdo.releng.doc.article.Identifiable;
import org.eclipse.emf.cdo.releng.doc.article.Image;
import org.eclipse.emf.cdo.releng.doc.article.JavaElement;
import org.eclipse.emf.cdo.releng.doc.article.JavaFormatter;
import org.eclipse.emf.cdo.releng.doc.article.JavaPackage;
import org.eclipse.emf.cdo.releng.doc.article.Javadoc;
import org.eclipse.emf.cdo.releng.doc.article.Link;
import org.eclipse.emf.cdo.releng.doc.article.LinkTarget;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;
import org.eclipse.emf.cdo.releng.doc.article.PluginResource;
import org.eclipse.emf.cdo.releng.doc.article.Schemadoc;
import org.eclipse.emf.cdo.releng.doc.article.Snippet;
import org.eclipse.emf.cdo.releng.doc.article.SourceCode;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.Text;
import org.eclipse.emf.cdo.releng.doc.article.Toc;
import org.eclipse.emf.cdo.releng.doc.article.XmlFormatter;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage
 * @generated
 */
public class ArticleAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static ArticlePackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ArticleAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = ArticlePackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   *
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ArticleSwitch<Adapter> modelSwitch = new ArticleSwitch<Adapter>()
  {
    @Override
    public Adapter caseDocumentation(Documentation object)
    {
      return createDocumentationAdapter();
    }

    @Override
    public Adapter caseContext(Context object)
    {
      return createContextAdapter();
    }

    @Override
    public Adapter caseCategory(Category object)
    {
      return createCategoryAdapter();
    }

    @Override
    public Adapter caseArticle(Article object)
    {
      return createArticleAdapter();
    }

    @Override
    public Adapter caseChapter(Chapter object)
    {
      return createChapterAdapter();
    }

    @Override
    public Adapter caseSnippet(Snippet object)
    {
      return createSnippetAdapter();
    }

    @Override
    public Adapter caseDiagram(Diagram object)
    {
      return createDiagramAdapter();
    }

    @Override
    public Adapter caseFactory(Factory object)
    {
      return createFactoryAdapter();
    }

    @Override
    public Adapter caseJavaElement(JavaElement object)
    {
      return createJavaElementAdapter();
    }

    @Override
    public Adapter caseStructuralElement(StructuralElement object)
    {
      return createStructuralElementAdapter();
    }

    @Override
    public Adapter caseLinkTarget(LinkTarget object)
    {
      return createLinkTargetAdapter();
    }

    @Override
    public Adapter caseCallout(Callout object)
    {
      return createCalloutAdapter();
    }

    @Override
    public Adapter caseEmbeddableElement(EmbeddableElement object)
    {
      return createEmbeddableElementAdapter();
    }

    @Override
    public Adapter caseExternalTarget(ExternalTarget object)
    {
      return createExternalTargetAdapter();
    }

    @Override
    public Adapter caseIdentifiable(Identifiable object)
    {
      return createIdentifiableAdapter();
    }

    @Override
    public Adapter caseBody(Body object)
    {
      return createBodyAdapter();
    }

    @Override
    public Adapter caseBodyElementContainer(BodyElementContainer object)
    {
      return createBodyElementContainerAdapter();
    }

    @Override
    public Adapter caseBodyElement(BodyElement object)
    {
      return createBodyElementAdapter();
    }

    @Override
    public Adapter caseText(Text object)
    {
      return createTextAdapter();
    }

    @Override
    public Adapter caseLink(Link object)
    {
      return createLinkAdapter();
    }

    @Override
    public Adapter caseEmbedding(Embedding object)
    {
      return createEmbeddingAdapter();
    }

    @Override
    public Adapter caseSourceCode(SourceCode object)
    {
      return createSourceCodeAdapter();
    }

    @Override
    public Adapter casePlugin(Plugin object)
    {
      return createPluginAdapter();
    }

    @Override
    public Adapter caseJavaPackage(JavaPackage object)
    {
      return createJavaPackageAdapter();
    }

    @Override
    public Adapter caseJavadoc(Javadoc object)
    {
      return createJavadocAdapter();
    }

    @Override
    public Adapter caseExternalArticle(ExternalArticle object)
    {
      return createExternalArticleAdapter();
    }

    @Override
    public Adapter caseSchemadoc(Schemadoc object)
    {
      return createSchemadocAdapter();
    }

    @Override
    public Adapter caseExtensionPoint(ExtensionPoint object)
    {
      return createExtensionPointAdapter();
    }

    @Override
    public Adapter caseToc(Toc object)
    {
      return createTocAdapter();
    }

    @Override
    public Adapter casePluginResource(PluginResource object)
    {
      return createPluginResourceAdapter();
    }

    @Override
    public Adapter caseExcel(Excel object)
    {
      return createExcelAdapter();
    }

    @Override
    public Adapter caseFormatter(Formatter object)
    {
      return createFormatterAdapter();
    }

    @Override
    public Adapter caseJavaFormatter(JavaFormatter object)
    {
      return createJavaFormatterAdapter();
    }

    @Override
    public Adapter caseXmlFormatter(XmlFormatter object)
    {
      return createXmlFormatterAdapter();
    }

    @Override
    public Adapter caseImage(Image object)
    {
      return createImageAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Documentation
   * <em>Documentation</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Documentation
   * @generated
   */
  public Adapter createDocumentationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Context
   * <em>Context</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Context
   * @generated
   */
  public Adapter createContextAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Category
   * <em>Category</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Category
   * @generated
   */
  public Adapter createCategoryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Article
   * <em>Article</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Article
   * @generated
   */
  public Adapter createArticleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Chapter
   * <em>Chapter</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Chapter
   * @generated
   */
  public Adapter createChapterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Snippet
   * <em>Snippet</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Snippet
   * @generated
   */
  public Adapter createSnippetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Diagram
   * <em>Diagram</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Diagram
   * @generated
   */
  public Adapter createDiagramAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Factory
   * <em>Factory</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Factory
   * @generated
   */
  public Adapter createFactoryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.JavaElement
   * <em>Java Element</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.JavaElement
   * @generated
   */
  public Adapter createJavaElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement
   * <em>Structural Element</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.StructuralElement
   * @generated
   */
  public Adapter createStructuralElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.LinkTarget
   * <em>Link Target</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.LinkTarget
   * @generated
   */
  public Adapter createLinkTargetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Callout
   * <em>Callout</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Callout
   * @generated
   */
  public Adapter createCalloutAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement
   * <em>Embeddable Element</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement
   * @generated
   */
  public Adapter createEmbeddableElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.ExternalTarget
   * <em>External Target</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.ExternalTarget
   * @generated
   */
  public Adapter createExternalTargetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Identifiable
   * <em>Identifiable</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Identifiable
   * @generated
   */
  public Adapter createIdentifiableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Body <em>Body</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Body
   * @generated
   */
  public Adapter createBodyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer
   * <em>Body Element Container</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer
   * @generated
   */
  public Adapter createBodyElementContainerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement
   * <em>Body Element</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.BodyElement
   * @generated
   */
  public Adapter createBodyElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Text <em>Text</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Text
   * @generated
   */
  public Adapter createTextAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Link <em>Link</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Link
   * @generated
   */
  public Adapter createLinkAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Embedding
   * <em>Embedding</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Embedding
   * @generated
   */
  public Adapter createEmbeddingAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.SourceCode
   * <em>Source Code</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.SourceCode
   * @generated
   */
  public Adapter createSourceCodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Plugin <em>Plugin</em>}
   * '. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful
   * to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Plugin
   * @generated
   */
  public Adapter createPluginAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage
   * <em>Java Package</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.JavaPackage
   * @generated
   */
  public Adapter createJavaPackageAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Javadoc
   * <em>Javadoc</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Javadoc
   * @generated
   */
  public Adapter createJavadocAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.ExternalArticle
   * <em>External Article</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.ExternalArticle
   * @generated
   */
  public Adapter createExternalArticleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Schemadoc
   * <em>Schemadoc</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Schemadoc
   * @generated
   */
  public Adapter createSchemadocAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint
   * <em>Extension Point</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint
   * @generated
   */
  public Adapter createExtensionPointAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Toc <em>Toc</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Toc
   * @generated
   */
  public Adapter createTocAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.PluginResource
   * <em>Plugin Resource</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.PluginResource
   * @generated
   */
  public Adapter createPluginResourceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Excel <em>Excel</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Excel
   * @generated
   */
  public Adapter createExcelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Formatter
   * <em>Formatter</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
   * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Formatter
   * @generated
   */
  public Adapter createFormatterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.JavaFormatter
   * <em>Java Formatter</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.JavaFormatter
   * @generated
   */
  public Adapter createJavaFormatterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.XmlFormatter
   * <em>Xml Formatter</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.XmlFormatter
   * @generated
   */
  public Adapter createXmlFormatterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.releng.doc.article.Image <em>Image</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.releng.doc.article.Image
   * @generated
   */
  public Adapter createImageAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // ArticleAdapterFactory
