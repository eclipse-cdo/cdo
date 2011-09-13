/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.util;

import org.eclipse.emf.cdo.releng.doc.article.Article;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Callout;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.Chapter;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.Diagram;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;
import org.eclipse.emf.cdo.releng.doc.article.Embedding;
import org.eclipse.emf.cdo.releng.doc.article.ExternalTarget;
import org.eclipse.emf.cdo.releng.doc.article.Factory;
import org.eclipse.emf.cdo.releng.doc.article.Identifiable;
import org.eclipse.emf.cdo.releng.doc.article.JavaElement;
import org.eclipse.emf.cdo.releng.doc.article.Link;
import org.eclipse.emf.cdo.releng.doc.article.LinkTarget;
import org.eclipse.emf.cdo.releng.doc.article.Snippet;
import org.eclipse.emf.cdo.releng.doc.article.SourceCode;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.Text;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage
 * @generated
 */
public class ArticleSwitch<T> extends Switch<T>
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
   * Checks whether this is a switch for the given package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @parameter ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case ArticlePackage.DOCUMENTATION:
    {
      Documentation documentation = (Documentation)theEObject;
      T result = caseDocumentation(documentation);
      if (result == null)
        result = caseStructuralElement(documentation);
      if (result == null)
        result = caseLinkTarget(documentation);
      if (result == null)
        result = caseIdentifiable(documentation);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.CONTEXT:
    {
      Context context = (Context)theEObject;
      T result = caseContext(context);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.CATEGORY:
    {
      Category category = (Category)theEObject;
      T result = caseCategory(category);
      if (result == null)
        result = caseBody(category);
      if (result == null)
        result = caseStructuralElement(category);
      if (result == null)
        result = caseLinkTarget(category);
      if (result == null)
        result = caseIdentifiable(category);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.ARTICLE:
    {
      Article article = (Article)theEObject;
      T result = caseArticle(article);
      if (result == null)
        result = caseChapter(article);
      if (result == null)
        result = caseBody(article);
      if (result == null)
        result = caseStructuralElement(article);
      if (result == null)
        result = caseLinkTarget(article);
      if (result == null)
        result = caseIdentifiable(article);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.CHAPTER:
    {
      Chapter chapter = (Chapter)theEObject;
      T result = caseChapter(chapter);
      if (result == null)
        result = caseBody(chapter);
      if (result == null)
        result = caseStructuralElement(chapter);
      if (result == null)
        result = caseLinkTarget(chapter);
      if (result == null)
        result = caseIdentifiable(chapter);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.SNIPPET:
    {
      Snippet snippet = (Snippet)theEObject;
      T result = caseSnippet(snippet);
      if (result == null)
        result = caseEmbeddableElement(snippet);
      if (result == null)
        result = caseIdentifiable(snippet);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.DIAGRAM:
    {
      Diagram diagram = (Diagram)theEObject;
      T result = caseDiagram(diagram);
      if (result == null)
        result = caseBodyElement(diagram);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.FACTORY:
    {
      Factory factory = (Factory)theEObject;
      T result = caseFactory(factory);
      if (result == null)
        result = caseEmbeddableElement(factory);
      if (result == null)
        result = caseIdentifiable(factory);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.JAVA_ELEMENT:
    {
      JavaElement javaElement = (JavaElement)theEObject;
      T result = caseJavaElement(javaElement);
      if (result == null)
        result = caseLinkTarget(javaElement);
      if (result == null)
        result = caseIdentifiable(javaElement);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.STRUCTURAL_ELEMENT:
    {
      StructuralElement structuralElement = (StructuralElement)theEObject;
      T result = caseStructuralElement(structuralElement);
      if (result == null)
        result = caseLinkTarget(structuralElement);
      if (result == null)
        result = caseIdentifiable(structuralElement);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.LINK_TARGET:
    {
      LinkTarget linkTarget = (LinkTarget)theEObject;
      T result = caseLinkTarget(linkTarget);
      if (result == null)
        result = caseIdentifiable(linkTarget);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.CALLOUT:
    {
      Callout callout = (Callout)theEObject;
      T result = caseCallout(callout);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.EMBEDDABLE_ELEMENT:
    {
      EmbeddableElement embeddableElement = (EmbeddableElement)theEObject;
      T result = caseEmbeddableElement(embeddableElement);
      if (result == null)
        result = caseIdentifiable(embeddableElement);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.EXTERNAL_TARGET:
    {
      ExternalTarget externalTarget = (ExternalTarget)theEObject;
      T result = caseExternalTarget(externalTarget);
      if (result == null)
        result = caseLinkTarget(externalTarget);
      if (result == null)
        result = caseIdentifiable(externalTarget);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.IDENTIFIABLE:
    {
      Identifiable identifiable = (Identifiable)theEObject;
      T result = caseIdentifiable(identifiable);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.BODY:
    {
      Body body = (Body)theEObject;
      T result = caseBody(body);
      if (result == null)
        result = caseStructuralElement(body);
      if (result == null)
        result = caseLinkTarget(body);
      if (result == null)
        result = caseIdentifiable(body);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.BODY_ELEMENT:
    {
      BodyElement bodyElement = (BodyElement)theEObject;
      T result = caseBodyElement(bodyElement);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.TEXT:
    {
      Text text = (Text)theEObject;
      T result = caseText(text);
      if (result == null)
        result = caseBodyElement(text);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.LINK:
    {
      Link link = (Link)theEObject;
      T result = caseLink(link);
      if (result == null)
        result = caseBodyElement(link);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.EMBEDDING:
    {
      Embedding embedding = (Embedding)theEObject;
      T result = caseEmbedding(embedding);
      if (result == null)
        result = caseBodyElement(embedding);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case ArticlePackage.SOURCE_CODE:
    {
      SourceCode sourceCode = (SourceCode)theEObject;
      T result = caseSourceCode(sourceCode);
      if (result == null)
        result = caseExternalTarget(sourceCode);
      if (result == null)
        result = caseLinkTarget(sourceCode);
      if (result == null)
        result = caseIdentifiable(sourceCode);
      if (result == null)
        result = defaultCase(theEObject);
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
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} // ArticleSwitch
