/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.Tag;

import java.io.IOException;
import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Body Element Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyElementContainerImpl#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class BodyElementContainerImpl extends EObjectImpl implements BodyElementContainer
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
  protected BodyElementContainerImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.BODY_ELEMENT_CONTAINER;
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
          ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS, ArticlePackage.BODY_ELEMENT__CONTAINER);
    }
    return elements;
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
    case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
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
    case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
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
    case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
      return getElements();
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
    case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
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
    case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
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
    case ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS:
      return elements != null && !elements.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  public static String analyzeTags(EList<BodyElement> elements, Tag[] tags, boolean withTitle)
  {
    String title = null;
    if (tags != null && tags.length != 0)
    {
      int bodyStart = 0;

      if (withTitle)
      {
        Tag firstTag = tags[0];
        if (firstTag.name().equals("Text"))
        {
          ++bodyStart;

          String text = firstTag.text();
          int blockPos = getBlockPosition(text);
          if (blockPos != -1)
          {
            String rest = text.substring(blockPos);
            addElement(elements, new TextTag(firstTag, rest));

            text = text.substring(0, blockPos);
          }

          text = text.replaceAll("\\s+", " ").trim();
          title = text;
        }
      }

      for (int i = bodyStart; i < tags.length; i++)
      {
        addElement(elements, tags[i]);
      }
    }

    return title;
  }

  private static void addElement(EList<BodyElement> elements, Tag tag)
  {
    if (tag.kind().equals("Text"))
    {
      elements.add(new TextImpl(null, tag));
    }
    else
    {
      elements.add(new UnresolvedBodyElement(null, tag));
    }
  }

  private static int getBlockPosition(String text)
  {
    String[] blocks = { "p", "br", "hr", "ul", "ol", "div", "table" };
    int result = Integer.MAX_VALUE;

    for (String block : blocks)
    {
      int index = text.indexOf("<" + block);
      if (index != -1 && index < result)
      {
        result = index;
      }
    }

    return result == Integer.MAX_VALUE ? -1 : result;
  }

  public static void generate(HtmlWriter out, StructuralElement linkSource, EList<BodyElement> elements)
      throws IOException
  {
    UnresolvedBodyElement.resolve(linkSource.getDocumentation().getContext(), elements);

    for (BodyElement element : elements)
    {
      element.generate(out, linkSource);
    }

    out.write("\n\n");
  }

} // BodyElementContainerImpl
