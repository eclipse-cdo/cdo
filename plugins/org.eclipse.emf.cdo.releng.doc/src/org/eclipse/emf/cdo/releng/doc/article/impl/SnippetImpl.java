/*
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
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleException;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
  private static final String CALLOUT = "<font color=\"#3f7f5f\">/*&nbsp;callout&nbsp;*/</font>";

  private static final Pattern PATTERN = Pattern.compile("<[^>]+?>", Pattern.MULTILINE | Pattern.DOTALL);

  private static Constructor<?> snippet;

  private static Method getHtml;

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

  public void generate(PrintWriter out, Embedding embedder) throws IOException
  {
    String id = doc.name();
    int lastDot = id.lastIndexOf('.');
    if (lastDot != -1)
    {
      id = id.substring(lastDot + 1);
    }

    StructuralElement structuralElement = getStructuralElement(embedder);
    String imagePath = structuralElement.getImagePath() + "/";

    SeeTag embedderTag = (SeeTag)embedder.getTag();
    String title = embedderTag.label();
    if (title == null || title.length() == 0)
    {
      title = embedderTag.text() + ".java";
    }

    out.write("\n\n");

    String html = getCodeSnippetHtml(out, id, title);
    html = processCallouts(id, html, imagePath);

    out.write("<div class=\"snippet\" style=\"margin-left:24px;\" align=\"left\">\n");
    out.write("  <a name=\"snippet_" + id + "\"></a>\n");
    out.write("  <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
    out.write("    <tr>\n");
    out.write("      <td><img src=\"" + imagePath + "editor-top-left.png\"></td>\n");
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-top1.png); background-repeat:repeat-x;\" width=\"1px\"><font face=\"Segoe UI,Arial\" size=\"-1\">"
        + title + "</font></td>\n");
    out.write("      <td width=\"1px\"><img src=\"" + imagePath + "editor-close.png\"></td>\n");
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-top2.png); background-repeat:repeat-x;\">&nbsp;</td>\n");
    out.write("      <td><img src=\"" + imagePath + "editor-top-right.png\"></td>\n");
    out.write("    </tr>\n");
    out.write("    <tr>\n");
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-left.png); background-repeat:repeat-y;\">&nbsp;</td>\n");
    out.write("      <td colspan=\"3\" align=\"left\" valign=\"top\" nowrap>\n");
    out.write("        <div style=\"margin:10px 0px 10px 0px;\">\n");
    out.write("          <code>\n");

    out.write(html);
    out.write("\n");

    out.write("          </code>\n");
    out.write("        </div>\n");
    out.write("      </td>\n");
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-right.png); background-repeat:repeat-y;\">&nbsp;</td>\n");
    out.write("    </tr>\n");
    out.write("    <tr>\n");
    out.write("      <td><img src=\"" + imagePath + "editor-bottom-left.png\"></td>\n");
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-bottom.png); background-repeat:repeat-x;\" colspan=\"3\">&nbsp;</td>\n");
    out.write("      <td><img src=\"" + imagePath + "editor-bottom-right.png\"></td>\n");
    out.write("    </tr>\n");
    out.write("  </table>\n");
    out.write("</div>\n");

    EList<Callout> callouts = getCallouts();
    if (!callouts.isEmpty())
    {
      out.write("<p>\n");
      for (Callout callout : callouts)
      {
        String image = getCalloutImage(id, callout.getIndex(), false, "Jump to snippet...", imagePath);
        out.write("<div style=\"margin-left:24px;\">" + image + "&nbsp;");

        try
        {
          BodyElementContainerImpl.generate(out, structuralElement, callout.getElements());
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }

        out.write("</div>\n");
      }
    }

    out.write("<p>\n");
  }

  private StructuralElement getStructuralElement(Embedding embedder)
  {
    BodyElementContainer container = embedder.getContainer();
    if (container instanceof StructuralElement)
    {
      return (StructuralElement)container;
    }

    throw new ArticleException(ArticleUtil.makeConsoleLink("Nested embedding in ", embedder.getTag().position()));
  }

  private String getCodeSnippetHtml(PrintWriter out, String id, String title)
  {
    Map<String, Object> options = new HashMap<String, Object>();
    options.put("id", id);
    options.put("title", title);
    options.put("includeSignature", doc instanceof ClassDoc);

    try
    {
      Object instance = snippet.newInstance(doc, options);
      return (String)getHtml.invoke(instance);
    }
    catch (Error ex)
    {
      throw ex;
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private String processCallouts(String id, String html, String imagePath)
  {
    EList<Callout> callouts = getCallouts();
    int size = callouts.size();
    int callout = 0;

    for (;;)
    {
      int pos = html.indexOf(CALLOUT);
      if (pos == -1)
      {
        break;
      }

      ++callout;

      String start = html.substring(0, pos);
      String rest = html.substring(pos + CALLOUT.length());
      if (!rest.startsWith("&nbsp;"))
      {
        rest = "&nbsp;" + rest;
      }

      String alt = "";
      int index = callout - 1;
      if (index < size)
      {
        Tag tag = callouts.get(index).getTag();
        for (Tag inlineTag : tag.inlineTags())
        {
          alt += inlineTag.text();
        }
      }

      String image = getCalloutImage(id, callout, true, alt, imagePath);
      html = start + image + rest;
    }

    if (callout != size)
    {
      Tag tag = callouts.get(size - 1).getTag();
      SourcePosition position = tag.position();
      String link = ArticleUtil.makeConsoleLink(tag.holder(), position);

      if (callout < size)
      {
        System.err.println("Too many callout descriptions: " + link);
      }
      else if (callout > size)
      {
        System.err.println("Callout descriptions missing: " + link);
      }
    }

    return html;
  }

  private String getCalloutImage(String prefix, int number, boolean code, String alt, String imagePath)
  {
    String name = "callout_" + prefix + "_" + number;
    String nameSuffix = code ? "_code" : "";
    String hrefSuffix = code ? "" : "_code";

    String image = "<img src=\"" + imagePath + "callout-" + number
        + ".png\" width=\"16\" height=\"16\" border=\"0\" align=\"top\">";

    alt = PATTERN.matcher(alt).replaceAll("");
    alt = alt.replaceAll("\"", "&quot;");

    return "<a name=\"" + name + nameSuffix + "\" href=\"#" + name + hrefSuffix + "\" alt=\"" + alt + "\" title=\""
        + alt + "\">" + image + "</a>";
  }

  static
  {
    try
    {
      Class<?> c = Class.forName("de.escnet.CodeSnippet");
      snippet = c.getConstructor(Doc.class, Map.class);
      getHtml = c.getMethod("getHtml");
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }
  }
} // SnippetImpl
