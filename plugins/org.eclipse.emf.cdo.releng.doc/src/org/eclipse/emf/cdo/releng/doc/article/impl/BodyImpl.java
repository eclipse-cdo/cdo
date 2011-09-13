/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
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

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Body</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyImpl#getElements <em>Elements</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyImpl#getHtml <em>Html</em>}</li>
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
   * The default value of the '{@link #getHtml() <em>Html</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getHtml()
   * @generated
   * @ordered
   */
  protected static final String HTML_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getHtml() <em>Html</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getHtml()
   * @generated
   * @ordered
   */
  protected String html = HTML_EDEFAULT;

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

    Tag[] tags = doc.inlineTags();
    if (tags == null || tags.length == 0)
    {
      titleMissing();
      return;
    }

    int bodyStart = 0;

    Tag firstTag = tags[0];
    if (firstTag.name().equals("Text"))
    {
      ++bodyStart;

      String text = firstTag.text();
      int blockPos = getBlockPosition(text);
      if (blockPos != -1)
      {
        String rest = text.substring(blockPos);
        addElement(firstTag, rest);

        text = text.substring(0, blockPos);
      }

      text = text.replaceAll("\\s+", " ").trim();
      setTitle(text);
    }
    else
    {
      titleMissing();
    }

    for (int i = bodyStart; i < tags.length; i++)
    {
      Tag tag = tags[i];
      addElement(tag, tag.text());
    }
  }

  private void addElement(Tag tag, String text)
  {
    getElements().add(new UnresolvedBodyElementImpl(this, tag, text));
  }

  private void titleMissing()
  {
    Doc doc = getDoc();
    System.err.println("Warning: Title is missing in " + ArticleUtil.makeConsoleLink(doc));
    setTitle(doc.name());
  }

  private int getBlockPosition(String text)
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
          ArticlePackage.BODY__ELEMENTS, ArticlePackage.BODY_ELEMENT__BODY);
    }
    return elements;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getHtml()
  {
    return html;
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
    case ArticlePackage.BODY__HTML:
      return getHtml();
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
    case ArticlePackage.BODY__HTML:
      return HTML_EDEFAULT == null ? html != null : !HTML_EDEFAULT.equals(html);
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
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (html: ");
    result.append(html);
    result.append(')');
    return result.toString();
  }

  @Override
  public Object getId()
  {
    return getDoc();
  }

} // BodyImpl
