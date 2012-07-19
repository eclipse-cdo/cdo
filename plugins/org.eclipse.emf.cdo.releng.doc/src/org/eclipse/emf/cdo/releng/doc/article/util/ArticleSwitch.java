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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage
 * @generated
 */
public class ArticleSwitch<T>
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static ArticlePackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ArticleSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = ArticlePackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case ArticlePackage.DOCUMENTATION:
    {
      Documentation documentation = (Documentation)theEObject;
      T result = caseDocumentation(documentation);
      if (result == null)
      {
        result = caseStructuralElement(documentation);
      }
      if (result == null)
      {
        result = caseLinkTarget(documentation);
      }
      if (result == null)
      {
        result = caseIdentifiable(documentation);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.CONTEXT:
    {
      Context context = (Context)theEObject;
      T result = caseContext(context);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.CATEGORY:
    {
      Category category = (Category)theEObject;
      T result = caseCategory(category);
      if (result == null)
      {
        result = caseBody(category);
      }
      if (result == null)
      {
        result = caseStructuralElement(category);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(category);
      }
      if (result == null)
      {
        result = caseLinkTarget(category);
      }
      if (result == null)
      {
        result = caseIdentifiable(category);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.ARTICLE:
    {
      Article article = (Article)theEObject;
      T result = caseArticle(article);
      if (result == null)
      {
        result = caseChapter(article);
      }
      if (result == null)
      {
        result = caseBody(article);
      }
      if (result == null)
      {
        result = caseStructuralElement(article);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(article);
      }
      if (result == null)
      {
        result = caseLinkTarget(article);
      }
      if (result == null)
      {
        result = caseIdentifiable(article);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.CHAPTER:
    {
      Chapter chapter = (Chapter)theEObject;
      T result = caseChapter(chapter);
      if (result == null)
      {
        result = caseBody(chapter);
      }
      if (result == null)
      {
        result = caseStructuralElement(chapter);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(chapter);
      }
      if (result == null)
      {
        result = caseLinkTarget(chapter);
      }
      if (result == null)
      {
        result = caseIdentifiable(chapter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.SNIPPET:
    {
      Snippet snippet = (Snippet)theEObject;
      T result = caseSnippet(snippet);
      if (result == null)
      {
        result = caseEmbeddableElement(snippet);
      }
      if (result == null)
      {
        result = caseIdentifiable(snippet);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.DIAGRAM:
    {
      Diagram diagram = (Diagram)theEObject;
      T result = caseDiagram(diagram);
      if (result == null)
      {
        result = caseBodyElement(diagram);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.FACTORY:
    {
      Factory factory = (Factory)theEObject;
      T result = caseFactory(factory);
      if (result == null)
      {
        result = caseEmbeddableElement(factory);
      }
      if (result == null)
      {
        result = caseIdentifiable(factory);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.JAVA_ELEMENT:
    {
      JavaElement javaElement = (JavaElement)theEObject;
      T result = caseJavaElement(javaElement);
      if (result == null)
      {
        result = caseLinkTarget(javaElement);
      }
      if (result == null)
      {
        result = caseIdentifiable(javaElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.STRUCTURAL_ELEMENT:
    {
      StructuralElement structuralElement = (StructuralElement)theEObject;
      T result = caseStructuralElement(structuralElement);
      if (result == null)
      {
        result = caseLinkTarget(structuralElement);
      }
      if (result == null)
      {
        result = caseIdentifiable(structuralElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.LINK_TARGET:
    {
      LinkTarget linkTarget = (LinkTarget)theEObject;
      T result = caseLinkTarget(linkTarget);
      if (result == null)
      {
        result = caseIdentifiable(linkTarget);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.CALLOUT:
    {
      Callout callout = (Callout)theEObject;
      T result = caseCallout(callout);
      if (result == null)
      {
        result = caseBodyElementContainer(callout);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.EMBEDDABLE_ELEMENT:
    {
      EmbeddableElement embeddableElement = (EmbeddableElement)theEObject;
      T result = caseEmbeddableElement(embeddableElement);
      if (result == null)
      {
        result = caseIdentifiable(embeddableElement);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.EXTERNAL_TARGET:
    {
      ExternalTarget externalTarget = (ExternalTarget)theEObject;
      T result = caseExternalTarget(externalTarget);
      if (result == null)
      {
        result = caseLinkTarget(externalTarget);
      }
      if (result == null)
      {
        result = caseIdentifiable(externalTarget);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.IDENTIFIABLE:
    {
      Identifiable identifiable = (Identifiable)theEObject;
      T result = caseIdentifiable(identifiable);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.BODY:
    {
      Body body = (Body)theEObject;
      T result = caseBody(body);
      if (result == null)
      {
        result = caseStructuralElement(body);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(body);
      }
      if (result == null)
      {
        result = caseLinkTarget(body);
      }
      if (result == null)
      {
        result = caseIdentifiable(body);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.BODY_ELEMENT_CONTAINER:
    {
      BodyElementContainer bodyElementContainer = (BodyElementContainer)theEObject;
      T result = caseBodyElementContainer(bodyElementContainer);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.BODY_ELEMENT:
    {
      BodyElement bodyElement = (BodyElement)theEObject;
      T result = caseBodyElement(bodyElement);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.TEXT:
    {
      Text text = (Text)theEObject;
      T result = caseText(text);
      if (result == null)
      {
        result = caseBodyElement(text);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.LINK:
    {
      Link link = (Link)theEObject;
      T result = caseLink(link);
      if (result == null)
      {
        result = caseBodyElement(link);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.EMBEDDING:
    {
      Embedding embedding = (Embedding)theEObject;
      T result = caseEmbedding(embedding);
      if (result == null)
      {
        result = caseBodyElement(embedding);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.SOURCE_CODE:
    {
      SourceCode sourceCode = (SourceCode)theEObject;
      T result = caseSourceCode(sourceCode);
      if (result == null)
      {
        result = caseExternalTarget(sourceCode);
      }
      if (result == null)
      {
        result = caseLinkTarget(sourceCode);
      }
      if (result == null)
      {
        result = caseIdentifiable(sourceCode);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.PLUGIN:
    {
      Plugin plugin = (Plugin)theEObject;
      T result = casePlugin(plugin);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.JAVA_PACKAGE:
    {
      JavaPackage javaPackage = (JavaPackage)theEObject;
      T result = caseJavaPackage(javaPackage);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.JAVADOC:
    {
      Javadoc javadoc = (Javadoc)theEObject;
      T result = caseJavadoc(javadoc);
      if (result == null)
      {
        result = caseCategory(javadoc);
      }
      if (result == null)
      {
        result = caseBody(javadoc);
      }
      if (result == null)
      {
        result = caseStructuralElement(javadoc);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(javadoc);
      }
      if (result == null)
      {
        result = caseLinkTarget(javadoc);
      }
      if (result == null)
      {
        result = caseIdentifiable(javadoc);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.EXTERNAL_ARTICLE:
    {
      ExternalArticle externalArticle = (ExternalArticle)theEObject;
      T result = caseExternalArticle(externalArticle);
      if (result == null)
      {
        result = caseArticle(externalArticle);
      }
      if (result == null)
      {
        result = caseChapter(externalArticle);
      }
      if (result == null)
      {
        result = caseBody(externalArticle);
      }
      if (result == null)
      {
        result = caseStructuralElement(externalArticle);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(externalArticle);
      }
      if (result == null)
      {
        result = caseLinkTarget(externalArticle);
      }
      if (result == null)
      {
        result = caseIdentifiable(externalArticle);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.SCHEMADOC:
    {
      Schemadoc schemadoc = (Schemadoc)theEObject;
      T result = caseSchemadoc(schemadoc);
      if (result == null)
      {
        result = caseCategory(schemadoc);
      }
      if (result == null)
      {
        result = caseBody(schemadoc);
      }
      if (result == null)
      {
        result = caseStructuralElement(schemadoc);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(schemadoc);
      }
      if (result == null)
      {
        result = caseLinkTarget(schemadoc);
      }
      if (result == null)
      {
        result = caseIdentifiable(schemadoc);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.EXTENSION_POINT:
    {
      ExtensionPoint extensionPoint = (ExtensionPoint)theEObject;
      T result = caseExtensionPoint(extensionPoint);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.TOC:
    {
      Toc toc = (Toc)theEObject;
      T result = caseToc(toc);
      if (result == null)
      {
        result = caseBodyElement(toc);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.PLUGIN_RESOURCE:
    {
      PluginResource pluginResource = (PluginResource)theEObject;
      T result = casePluginResource(pluginResource);
      if (result == null)
      {
        result = caseExternalArticle(pluginResource);
      }
      if (result == null)
      {
        result = caseArticle(pluginResource);
      }
      if (result == null)
      {
        result = caseChapter(pluginResource);
      }
      if (result == null)
      {
        result = caseBody(pluginResource);
      }
      if (result == null)
      {
        result = caseStructuralElement(pluginResource);
      }
      if (result == null)
      {
        result = caseBodyElementContainer(pluginResource);
      }
      if (result == null)
      {
        result = caseLinkTarget(pluginResource);
      }
      if (result == null)
      {
        result = caseIdentifiable(pluginResource);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.EXCEL:
    {
      Excel excel = (Excel)theEObject;
      T result = caseExcel(excel);
      if (result == null)
      {
        result = caseBodyElement(excel);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.FORMATTER:
    {
      Formatter formatter = (Formatter)theEObject;
      T result = caseFormatter(formatter);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.JAVA_FORMATTER:
    {
      JavaFormatter javaFormatter = (JavaFormatter)theEObject;
      T result = caseJavaFormatter(javaFormatter);
      if (result == null)
      {
        result = caseFormatter(javaFormatter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.XML_FORMATTER:
    {
      XmlFormatter xmlFormatter = (XmlFormatter)theEObject;
      T result = caseXmlFormatter(xmlFormatter);
      if (result == null)
      {
        result = caseFormatter(xmlFormatter);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ArticlePackage.IMAGE:
    {
      Image image = (Image)theEObject;
      T result = caseImage(image);
      if (result == null)
      {
        result = caseBodyElement(image);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Documentation</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Documentation</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDocumentation(Documentation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Context</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Context</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseContext(Context object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Category</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Category</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCategory(Category object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Article</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Article</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseArticle(Article object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Chapter</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Chapter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChapter(Chapter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Snippet</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Snippet</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSnippet(Snippet object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Diagram</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Diagram</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDiagram(Diagram object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Factory</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Factory</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFactory(Factory object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Java Element</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Java Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJavaElement(JavaElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Structural Element</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Structural Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStructuralElement(StructuralElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Link Target</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Link Target</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLinkTarget(LinkTarget object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Callout</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Callout</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCallout(Callout object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Embeddable Element</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Embeddable Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEmbeddableElement(EmbeddableElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>External Target</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>External Target</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExternalTarget(ExternalTarget object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Identifiable</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Identifiable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIdentifiable(Identifiable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Body</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Body</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBody(Body object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Body Element Container</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Body Element Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBodyElementContainer(BodyElementContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Body Element</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Body Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBodyElement(BodyElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Text</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Text</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseText(Text object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Link</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Link</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLink(Link object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Embedding</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Embedding</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEmbedding(Embedding object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Source Code</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Source Code</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSourceCode(SourceCode object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Plugin</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Plugin</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePlugin(Plugin object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Java Package</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Java Package</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJavaPackage(JavaPackage object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Javadoc</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Javadoc</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJavadoc(Javadoc object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>External Article</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>External Article</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExternalArticle(ExternalArticle object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Schemadoc</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Schemadoc</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSchemadoc(Schemadoc object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Extension Point</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Extension Point</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExtensionPoint(ExtensionPoint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Toc</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Toc</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseToc(Toc object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Plugin Resource</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Plugin Resource</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePluginResource(PluginResource object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Excel</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Excel</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExcel(Excel object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Formatter</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Formatter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFormatter(Formatter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Java Formatter</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Java Formatter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseJavaFormatter(JavaFormatter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Xml Formatter</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Xml Formatter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseXmlFormatter(XmlFormatter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Image</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Image</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImage(Image object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // ArticleSwitch
