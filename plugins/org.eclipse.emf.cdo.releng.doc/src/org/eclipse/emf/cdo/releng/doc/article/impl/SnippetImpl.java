/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;
import org.eclipse.emf.cdo.releng.doc.article.Callout;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.Embedding;
import org.eclipse.emf.cdo.releng.doc.article.Formatter;
import org.eclipse.emf.cdo.releng.doc.article.Snippet;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.XmlFormatter;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleException;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Snippet</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.SnippetImpl#getCallouts <em>Callouts</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.SnippetImpl#getFormatter <em>Formatter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SnippetImpl extends EmbeddableElementImpl implements Snippet
{
  private static final Pattern PATTERN = Pattern.compile("<[^>]+?>", Pattern.MULTILINE | Pattern.DOTALL);

  /**
   * The cached value of the '{@link #getCallouts() <em>Callouts</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @see #getCallouts()
   * @generated
   * @ordered
   */
  protected EList<Callout> callouts;

  /**
   * The cached value of the '{@link #getFormatter() <em>Formatter</em>}' containment reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @see #getFormatter()
   * @generated
   * @ordered
   */
  protected Formatter formatter;

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
    super(documentation, doc);
    initFormatter(doc);
    initCallouts(doc);
  }

  private void initFormatter(Doc doc)
  {
    Tag[] tags = doc.tags("@snippet");
    if (tags.length > 1)
    {
      throw new ArticleException("More than one format not allowed: " + ArticleUtil.makeConsoleLink(doc));
    }

    if (tags.length == 1)
    {
      String text = tags[0].text();
      if (text.length() != 0)
      {
        String format;
        String args;

        int pos = text.indexOf(' ');
        if (pos != -1)
        {
          format = text.substring(0, pos).trim().toLowerCase();
          args = text.substring(pos + 1).trim();
        }
        else
        {
          format = text;
          args = "";
        }

        format = format.trim().toLowerCase();
        args = args.trim();

        if (format.equals("xml"))
        {
          try
          {
            File folder = doc.position().file().getParentFile();
            File file = new File(folder, args).getCanonicalFile();

            XmlFormatter formatter = new XmlFormatterImpl();
            formatter.setFile(file);

            setFormatter(formatter);
          }
          catch (IOException ex)
          {
            ex.printStackTrace();
          }
        }
      }
    }

    if (getFormatter() == null)
    {
      setFormatter(new JavaFormatterImpl());
    }
  }

  private void initCallouts(Doc doc)
  {
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
  public Formatter getFormatter()
  {
    return formatter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetFormatter(Formatter newFormatter, NotificationChain msgs)
  {
    Formatter oldFormatter = formatter;
    formatter = newFormatter;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ArticlePackage.SNIPPET__FORMATTER,
          oldFormatter, newFormatter);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setFormatter(Formatter newFormatter)
  {
    if (newFormatter != formatter)
    {
      NotificationChain msgs = null;
      if (formatter != null)
      {
        msgs = ((InternalEObject)formatter).eInverseRemove(this, ArticlePackage.FORMATTER__SNIPPET, Formatter.class,
            msgs);
      }
      if (newFormatter != null)
      {
        msgs = ((InternalEObject)newFormatter).eInverseAdd(this, ArticlePackage.FORMATTER__SNIPPET, Formatter.class,
            msgs);
      }
      msgs = basicSetFormatter(newFormatter, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, ArticlePackage.SNIPPET__FORMATTER, newFormatter, newFormatter));
    }
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
    case ArticlePackage.SNIPPET__FORMATTER:
      if (formatter != null)
      {
        msgs = ((InternalEObject)formatter).eInverseRemove(this,
            EOPPOSITE_FEATURE_BASE - ArticlePackage.SNIPPET__FORMATTER, null, msgs);
      }
      return basicSetFormatter((Formatter)otherEnd, msgs);
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
    case ArticlePackage.SNIPPET__FORMATTER:
      return basicSetFormatter(null, msgs);
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
    case ArticlePackage.SNIPPET__FORMATTER:
      return getFormatter();
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
    case ArticlePackage.SNIPPET__FORMATTER:
      setFormatter((Formatter)newValue);
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
    case ArticlePackage.SNIPPET__FORMATTER:
      setFormatter((Formatter)null);
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
    case ArticlePackage.SNIPPET__FORMATTER:
      return formatter != null;
    }
    return super.eIsSet(featureID);
  }

  public void generate(PrintWriter out, Embedding embedder) throws IOException
  {
    Formatter formatter = getFormatter();

    String id = getDoc().name();
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
      title = formatter.getDefaultTitle(embedderTag);
    }

    out.write(NL);
    out.write(NL);

    String html = formatter.getSnippetHtml(out, id, title);
    html = processCallouts(id, html, imagePath);

    out.write("<div class=\"snippet\" style=\"margin-left:24px;\" align=\"left\">" + NL);
    out.write("  <a name=\"snippet_" + id + "\"></a>" + NL);
    out.write("  <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" + NL);
    out.write("    <tr>" + NL);
    out.write("      <td><img src=\"" + formatter.getTopLeftEditorIcon(imagePath) + "\"></td>" + NL);
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-top1.png); background-repeat:repeat-x;\" width=\"1px\"><font face=\"Segoe UI,Arial\" size=\"-1\">"
        + title + "</font></td>" + NL);
    out.write("      <td width=\"1px\"><img src=\"" + imagePath + "editor-close.png\"></td>" + NL);
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-top2.png); background-repeat:repeat-x;\">&nbsp;</td>" + NL);
    out.write("      <td><img src=\"" + imagePath + "editor-top-right.png\"></td>" + NL);
    out.write("    </tr>" + NL);
    out.write("    <tr>" + NL);
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-left.png); background-repeat:repeat-y;\">&nbsp;</td>" + NL);
    out.write("      <td colspan=\"3\" align=\"left\" valign=\"top\" nowrap>" + NL);
    out.write("        <div style=\"margin:10px 0px 10px 0px;\">" + NL);
    out.write("          <code>" + NL);

    out.write(html);
    out.write("" + NL);

    out.write("          </code>" + NL);
    out.write("        </div>" + NL);
    out.write("      </td>" + NL);
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-right.png); background-repeat:repeat-y;\">&nbsp;</td>" + NL);
    out.write("    </tr>" + NL);
    out.write("    <tr>" + NL);
    out.write("      <td><img src=\"" + imagePath + "editor-bottom-left.png\"></td>" + NL);
    out.write("      <td style=\"background-image:url(" + imagePath
        + "editor-bottom.png); background-repeat:repeat-x;\" colspan=\"3\">&nbsp;</td>" + NL);
    out.write("      <td><img src=\"" + imagePath + "editor-bottom-right.png\"></td>" + NL);
    out.write("    </tr>" + NL);
    out.write("  </table>" + NL);
    out.write("</div>" + NL);

    EList<Callout> callouts = getCallouts();
    if (!callouts.isEmpty())
    {
      out.write("<p>" + NL);
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

        out.write("</div>" + NL);
      }
    }

    out.write("<p>" + NL);
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

  private String processCallouts(String id, String html, String imagePath)
  {
    EList<Callout> callouts = getCallouts();
    int size = callouts.size();
    int callout = 0;

    String calloutMarker = getFormatter().getCalloutMarker();

    for (;;)
    {
      int pos = html.indexOf(calloutMarker);
      if (pos == -1)
      {
        break;
      }

      ++callout;

      String start = html.substring(0, pos);
      String rest = html.substring(pos + calloutMarker.length());
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
      if (callout < size)
      {
        Tag tag = callouts.get(size).getTag();
        String link = ArticleUtil.makeConsoleLink(tag.holder(), tag.position());
        System.err.println("Too many callout descriptions: " + link);
      }
      else
      {
        String link = ArticleUtil.makeConsoleLink(getDoc(), getDoc().position());
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

} // SnippetImpl
