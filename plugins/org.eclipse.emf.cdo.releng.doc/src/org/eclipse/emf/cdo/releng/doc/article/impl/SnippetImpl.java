/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;
import org.eclipse.emf.cdo.releng.doc.article.Callout;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.Embedding;
import org.eclipse.emf.cdo.releng.doc.article.Snippet;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Snippet</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.SnippetImpl#getCallouts <em>Callouts</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class SnippetImpl extends EmbeddableElementImpl implements Snippet
{
  private static Constructor<?> snippet;

  private static Method write;

  /**
   * The cached value of the '{@link #getCallouts() <em>Callouts</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getCallouts()
   * @generated
   * @ordered
   */
  protected EList<Callout> callouts;

  private Doc doc;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected SnippetImpl()
  {
    super();
  }

  SnippetImpl(Documentation documentation, Doc doc)
  {
    setDocumentation(documentation);
    this.doc = doc;
    documentation.getContext().register(getId(), this);

    int index = 0;
    for (Tag tag : doc.tags("@callout"))
    {
      new CalloutImpl(this, tag, ++index);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.SNIPPET;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<Callout> getCallouts()
  {
    if (callouts == null)
    {
      callouts = new EObjectContainmentWithInverseEList<Callout>(Callout.class, this, ArticlePackage.SNIPPET__CALLOUTS,
          ArticlePackage.CALLOUT__SNIPPET);
    }
    return callouts;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ArticlePackage.SNIPPET__CALLOUTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getCallouts()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ArticlePackage.SNIPPET__CALLOUTS:
      return ((InternalEList<?>)getCallouts()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ArticlePackage.SNIPPET__CALLOUTS:
      return getCallouts();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ArticlePackage.SNIPPET__CALLOUTS:
      getCallouts().clear();
      getCallouts().addAll((Collection<? extends Callout>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.SNIPPET__CALLOUTS:
      getCallouts().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.SNIPPET__CALLOUTS:
      return callouts != null && !callouts.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public Object getId()
  {
    return doc;
  }

  @Override
  public String getHtml(Embedding embedder)
  {
    CharArrayWriter result = new CharArrayWriter();

    HtmlWriter out = new HtmlWriter(result);
    out.write("\n\n");
    writeHtml(embedder, out);

    out.flush();

    return result.toString();
  }

  private void writeHtml(Embedding embedder, HtmlWriter out)
  {
    Tag tag = embedder.getTag();

    BodyElementContainer container = embedder.getContainer();
    if (!(container instanceof StructuralElement))
    {
      System.err.println(ArticleUtil.makeConsoleLink("Nested embedding in ", tag.position()));
      return;
    }

    StructuralElement structuralElement = (StructuralElement)container;

    File source = structuralElement.getOutputFile();
    File target = new File(getDocumentation().getOutputFile().getParentFile(), "images");
    String imagePath = ArticleUtil.createLink(source, target) + "/";

    Map<String, Object> options = new HashMap<String, Object>();
    String label = ((SeeTag)tag).label();
    if (label != null)
    {
      options.put("title", label);
    }

    options.put("includeSignature", doc instanceof ClassDoc);
    options.put("imagePath", imagePath);

    try
    {
      Object instance = snippet.newInstance(doc, options);
      write.invoke(instance, out);
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }

    out.write("<ol>\n");
    for (Callout callout : getCallouts())
    {
      out.write("<li>");

      try
      {
        BodyElementContainerImpl.generate(structuralElement, callout.getElements(), out);
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }

      out.write("</li>\n");
    }

    out.write("</ol>\n");
  }

  static
  {
    try
    {
      Class<?> c = Class.forName("de.escnet.CodeSnippet");
      snippet = c.getConstructor(Doc.class, Map.class);
      write = c.getMethod("write", PrintWriter.class);
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }
  }
} // SnippetImpl
