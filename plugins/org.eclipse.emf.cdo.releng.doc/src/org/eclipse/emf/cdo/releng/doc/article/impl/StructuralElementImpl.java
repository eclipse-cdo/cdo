/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl.TocWriter;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Structural Element</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.StructuralElementImpl#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.StructuralElementImpl#getParent <em>Parent</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.StructuralElementImpl#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.StructuralElementImpl#getOutputFile <em>Output File</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.StructuralElementImpl#getDocumentation <em>Documentation</em>}
 * </li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class StructuralElementImpl extends LinkTargetImpl implements StructuralElement
{
  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<StructuralElement> children;

  /**
   * The default value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getTitle()
   * @generated
   * @ordered
   */
  protected static final String TITLE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getTitle()
   * @generated
   * @ordered
   */
  protected String title = TITLE_EDEFAULT;

  /**
   * The default value of the '{@link #getDoc() <em>Doc</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDoc()
   * @generated
   * @ordered
   */
  protected static final Doc DOC_EDEFAULT = null;

  private String fullPath;

  private File outputFile;

  private Doc doc;

  private boolean overview;

  private float number = 100f;

  private List<StructuralElement> sortedChildren;

  private String path;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected StructuralElementImpl()
  {
    super();
  }

  StructuralElementImpl(StructuralElement parent, String path, Doc doc)
  {
    setParent(parent);
    setPath(path);
    this.doc = doc;
    getDocumentation().registerElement(this);

    if (ArticleUtil.isTagged(doc, "@default"))
    {
      number = -1f;
      overview = true;

      Documentation documentation = getDocumentation();
      documentation.setDefaultElement(this);
      System.out.println("Default element of " + documentation.getProject() + ": " + getFullPath());

    }

    Tag[] tags = doc.tags("@number");
    if (tags != null && tags.length != 0)
    {
      Tag[] inlineTags = tags[0].inlineTags();
      if (inlineTags != null && inlineTags.length != 0)
      {
        number = Float.parseFloat(inlineTags[0].text());
      }
    }
  }

  final void setPath(String path)
  {
    this.path = path;
  }

  final void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.STRUCTURAL_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<StructuralElement> getChildren()
  {
    if (children == null)
    {
      children = new EObjectContainmentWithInverseEList<StructuralElement>(StructuralElement.class, this,
          ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN, ArticlePackage.STRUCTURAL_ELEMENT__PARENT);
    }
    return children;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public StructuralElement getParent()
  {
    if (eContainerFeatureID() != ArticlePackage.STRUCTURAL_ELEMENT__PARENT)
    {
      return null;
    }
    return (StructuralElement)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetParent(StructuralElement newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, ArticlePackage.STRUCTURAL_ELEMENT__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setParent(StructuralElement newParent)
  {
    if (newParent != eInternalContainer() || eContainerFeatureID() != ArticlePackage.STRUCTURAL_ELEMENT__PARENT
        && newParent != null)
    {
      if (EcoreUtil.isAncestor(this, newParent))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newParent != null)
      {
        msgs = ((InternalEObject)newParent).eInverseAdd(this, ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN,
            StructuralElement.class, msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.STRUCTURAL_ELEMENT__PARENT, newParent,
          newParent));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Documentation getDocumentation()
  {
    if (this instanceof Documentation)
    {
      return (Documentation)this;
    }

    StructuralElement parent = getParent();
    return parent.getDocumentation();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Doc getDoc()
  {
    return doc;
  }

  @Override
  public String linkFrom(StructuralElement source)
  {
    File sourceFile = source.getOutputFile();
    File targetFile = getOutputFile();
    return ArticleUtil.createLink(sourceFile, targetFile);
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
    case ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetParent((StructuralElement)otherEnd, msgs);
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
    case ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN:
      return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      return basicSetParent(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN,
          StructuralElement.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN:
      return getChildren();
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      return getParent();
    case ArticlePackage.STRUCTURAL_ELEMENT__TITLE:
      return getTitle();
    case ArticlePackage.STRUCTURAL_ELEMENT__DOCUMENTATION:
      return getDocumentation();
    case ArticlePackage.STRUCTURAL_ELEMENT__DOC:
      return getDoc();
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
    case ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN:
      getChildren().clear();
      getChildren().addAll((Collection<? extends StructuralElement>)newValue);
      return;
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      setParent((StructuralElement)newValue);
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
    case ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN:
      getChildren().clear();
      return;
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      setParent((StructuralElement)null);
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
    case ArticlePackage.STRUCTURAL_ELEMENT__CHILDREN:
      return children != null && !children.isEmpty();
    case ArticlePackage.STRUCTURAL_ELEMENT__PARENT:
      return getParent() != null;
    case ArticlePackage.STRUCTURAL_ELEMENT__TITLE:
      return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
    case ArticlePackage.STRUCTURAL_ELEMENT__DOCUMENTATION:
      return getDocumentation() != null;
    case ArticlePackage.STRUCTURAL_ELEMENT__DOC:
      return DOC_EDEFAULT == null ? getDoc() != null : !DOC_EDEFAULT.equals(getDoc());
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
    result.append(" (title: ");
    result.append(title);
    result.append(')');
    return result.toString();
  }

  public String getPath()
  {
    return path;
  }

  public final String getFullPath()
  {
    if (fullPath == null)
    {
      fullPath = createFullPath();
    }

    return fullPath;
  }

  protected String createFullPath()
  {
    StructuralElement parent = getParent();
    if (parent != null)
    {
      return parent.getBasePathForChildren() + "/" + path;
    }

    return path;
  }

  public String getBasePathForChildren()
  {
    return new File(getFullPath()).getParentFile().toString();
  }

  public final File getOutputFile()
  {
    checkAnalyzed();
    if (outputFile == null)
    {
      outputFile = createOutputFile();
    }

    return outputFile;
  }

  protected File createOutputFile()
  {
    return new File(getDocumentation().getContext().getBaseFolder(), getFullPath());
  }

  public final File getBaseFolderForChildren()
  {
    return new File(getDocumentation().getContext().getBaseFolder(), getBasePathForChildren());
  }

  protected void collectNavElements(List<StructuralElement> navElements)
  {
    for (StructuralElement child : getSortedChildren())
    {
      ((StructuralElementImpl)child).collectNavElements(navElements);
    }
  }

  public boolean isOverview()
  {
    return overview;
  }

  public float getNumber()
  {
    return number;
  }

  public int getDepth()
  {
    int depth = 0;
    for (StructuralElement child : getChildren())
    {
      depth = Math.max(depth, child.getDepth());
    }

    return 1 + depth;
  }

  public String getImagePath()
  {
    File source = getOutputFile();
    File target = new File(getDocumentation().getProjectFolder(), "images");
    return ArticleUtil.createLink(source, target);
  }

  @Override
  public String getDefaultLabel()
  {
    return title;
  }

  @Override
  public String getTooltip()
  {
    return getKind() + " in " + getDocumentation().getTitle();
  }

  protected abstract String getKind();

  public List<StructuralElement> getSortedChildren()
  {
    checkAnalyzed();
    if (sortedChildren == null)
    {
      sortedChildren = new ArrayList<StructuralElement>(getChildren());
      Collections.sort(sortedChildren, new Comparator<StructuralElement>()
      {
        public int compare(StructuralElement body1, StructuralElement body2)
        {
          return new Float(body1.getNumber()).compareTo(body2.getNumber());
        }
      });
    }

    return sortedChildren;
  }

  public void generate() throws IOException
  {
    checkAnalyzed();
    for (StructuralElement child : getSortedChildren())
    {
      child.generate();
    }
  }

  public void generate(PrintWriter out) throws IOException
  {
    checkAnalyzed();
    for (StructuralElement child : getSortedChildren())
    {
      child.generate(out);
    }
  }

  protected void generate(File file) throws IOException
  {
    checkAnalyzed();
    Documentation documentation = getDocumentation();
    String title = getTitle() + " (" + documentation.getTitle() + ")";
    File cssFile = new File(documentation.getBaseFolderForChildren(), "book.css");
    String css = ArticleUtil.createLink(getOutputFile(), cssFile);

    PrintWriter out = null;

    try
    {
      file.getParentFile().mkdirs();
      out = new PrintWriter(file);

      out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
      out.write("<HTML>\n");
      out.write("\n");
      out.write("<HEAD>\n");
      out.write("<TITLE>");
      out.write(title);
      out.write("</TITLE>\n");
      out.write("\n");
      out.write("<LINK REL=\"STYLESHEET\" HREF=\"" + css + "\" CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">\n");
      out.write("\n");
      out.write("<SCRIPT type=\"text/javascript\">\n");
      out.write("function windowTitle()\n");
      out.write("{\n");
      out.write("    if (location.href.indexOf('is-external=true') == -1) {\n");
      out.write("        parent.document.title=\"" + title + "\";\n");
      out.write("    }\n");
      out.write("}\n");
      out.write("</SCRIPT>\n");
      out.write("<NOSCRIPT></NOSCRIPT>\n");
      out.write("</HEAD>\n");
      out.write("\n");
      out.write("<BODY BGCOLOR=\"white\" onload=\"windowTitle();\">\n");

      out.write("<!-- ");
      out.write("<div class=\"help_breadcrumbs\">");
      generateBreadCrumbs(out, this);
      out.write("</div>");
      out.write(" -->\n");
      out.write("\n");

      generate(out);

      out.write("<HR>\n");
      out.write("<i>Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.</i>\n");
      out.write("</BODY>\n");
      out.write("</HTML>\n");
    }
    finally
    {
      ArticleUtil.close(out);
    }
  }

  protected void generateBreadCrumbs(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    checkAnalyzed();
    StructuralElement parent = getParent();
    if (parent instanceof StructuralElementImpl)
    {
      ((StructuralElementImpl)parent).generateBreadCrumbs(out, linkSource);
    }
  }

  protected void generateTocEntries(TocWriter writer) throws IOException
  {
    checkAnalyzed();
    for (StructuralElement child : getSortedChildren())
    {
      BodyImpl body = (BodyImpl)child;
      body.generateTocEntry(writer);
    }
  }

  protected void generateTocEntry(TocWriter writer) throws IOException
  {
    checkAnalyzed();
    writer.writeGroupStart(getTitle(), getTocHref(), null);
    generateTocEntries(writer);
    writer.writeGroupEnd();
  }

  protected File getTocTarget()
  {
    return getOutputFile();
  }

  protected String getTocHref()
  {
    File projectFolder = getDocumentation().getProjectFolder();
    File tocTarget = getTocTarget();
    return ArticleUtil.createLink(projectFolder, tocTarget);
  }

  protected void copyResources(File sourceFolder)
  {
    File targetFolder = getOutputFile().getParentFile();
    for (File file : sourceFolder.listFiles())
    {
      if (file.isFile())
      {
        String name = file.getName();
        if (!name.endsWith(".java") && !name.equals("package-info.java"))
        {
          File targetFile = new File(targetFolder, name);
          ArticleUtil.copyFile(file, targetFile);
        }
      }
    }
  }

  protected void checkAnalyzed()
  {
    Documentation documentation = getDocumentation();
    if (documentation == null || !documentation.isAnalyzed())
    {
      throw new AssertionError("Documentation analysis is not finished, yet");
    }
  }
} // StructuralElementImpl
