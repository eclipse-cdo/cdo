/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Body</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyImpl#getElements <em>Elements</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyImpl#getCategory <em>Category</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class BodyImpl extends StructuralElementImpl implements Body
{
  /**
   * The cached value of the '{@link #getElements() <em>Elements</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getElements()
   * @generated
   * @ordered
   */
  protected EList<BodyElement> elements;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected BodyImpl()
  {
    super();
  }

  BodyImpl(StructuralElement parent, String path, Doc doc)
  {
    super(parent, path, doc);

    String title = BodyElementContainerImpl.analyzeTags(getElements(), doc.inlineTags(), true);
    if (title != null)
    {
      setTitle(title);
    }
    else
    {
      titleMissing();
    }
  }

  private void titleMissing()
  {
    Doc doc = getDoc();
    System.err.println("Title is missing in " + ArticleUtil.makeConsoleLink(doc));
    setTitle(doc.name());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.BODY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<BodyElement> getElements()
  {
    if (elements == null)
    {
      elements = new EObjectContainmentWithInverseEList<BodyElement>(BodyElement.class, this,
          ArticlePackage.BODY__ELEMENTS, ArticlePackage.BODY_ELEMENT__CONTAINER);
    }
    return elements;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Category getCategory()
  {
    StructuralElement parent = getParent();
    if (parent instanceof Category)
    {
      return (Category)parent;
    }

    if (parent instanceof Body)
    {
      return ((Body)parent).getCategory();
    }

    return null;
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
    case ArticlePackage.BODY__ELEMENTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getElements()).basicAdd(otherEnd, msgs);
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
    case ArticlePackage.BODY__ELEMENTS:
      return ((InternalEList<?>)getElements()).basicRemove(otherEnd, msgs);
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
    case ArticlePackage.BODY__ELEMENTS:
      return getElements();
    case ArticlePackage.BODY__CATEGORY:
      return getCategory();
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
    case ArticlePackage.BODY__ELEMENTS:
      getElements().clear();
      getElements().addAll((Collection<? extends BodyElement>)newValue);
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
    case ArticlePackage.BODY__ELEMENTS:
      getElements().clear();
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
    case ArticlePackage.BODY__ELEMENTS:
      return elements != null && !elements.isEmpty();
    case ArticlePackage.BODY__CATEGORY:
      return getCategory() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == BodyElementContainer.class)
    {
      switch (derivedFeatureID)
      {
      case ArticlePackage.BODY__ELEMENTS:
        return ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == BodyElementContainer.class)
    {
      switch (baseFeatureID)
      {
      case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
        return ArticlePackage.BODY__ELEMENTS;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  @Override
  public Object getId()
  {
    return getDoc();
  }

  @Override
  public void generate(PrintWriter out) throws IOException
  {
    Tag[] authors = getDoc().tags("@author");
    if (authors.length > 0)
    {
      out.write("<p class=\"author\">Author");
      if (authors.length > 1)
      {
        out.write("s");
      }

      boolean first = true;
      for (Tag tag : authors)
      {
        if (first)
        {
          out.write(": ");
          first = false;
        }
        else
        {
          out.write(", ");
        }

        out.write(tag.text());
      }

      out.write("</p>\n");
    }

    EList<BodyElement> elements = getElements();
    BodyElementContainerImpl.generate(out, this, elements);
    super.generate(out);
  }

  protected void generateHeader(PrintWriter out)
  {
    out.write("<table border=\"0\">\n");
    out.write("\t<tr>\n");
    out.write("\t\t<td width=\"100%\"><h1>");
    out.write(getTitle());
    out.write("</h1></td>\n");
    out.write("\t\t<td align=\"right\" valign=\"middle\" nowrap>");
    generateNav(out);
    out.write("</td>\n");
    out.write("\t</tr>\n");
    out.write("</table>\n");
  }

  protected void generateFooter(PrintWriter out)
  {
    out.write("<p align=\"right\">\n");
    generateNav(out);
    out.write("</p>\n");
  }

  private void generateNav(PrintWriter out)
  {
    List<StructuralElement> elements = getDocumentation().getNavElements();
    int index = elements.indexOf(this);

    if (index > 0)
    {
      StructuralElement previous = elements.get(index - 1);
      generateNav(out, previous, "Backward");
    }

    out.write("&nbsp;");

    if (index < elements.size() - 1)
    {
      StructuralElement next = elements.get(index + 1);
      generateNav(out, next, "Forward");
    }
  }

  private void generateNav(PrintWriter out, StructuralElement target, String action)
  {
    String href = target.linkFrom(this);
    String tooltip = action + " to " + target.getTitle();
    String image = getImagePath() + "/" + action.toLowerCase() + ".png";

    out.write("<a href=\"" + href + "\" title=\"" + tooltip + "\">");
    out.write("<img src=\"" + image + "\" border=\"0\">");
    out.write("</a>");
  }
} // BodyImpl
