/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.AssembleScripts;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.Chapter;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;
import org.eclipse.emf.cdo.releng.doc.article.Javadoc;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;
import org.eclipse.emf.cdo.releng.doc.article.Schemadoc;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Documentation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl#getContext <em>Context</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl#getEmbeddableElements <em>Embeddable
 * Elements</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl#getDependencies <em>Dependencies</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl#getProject <em>Project</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl#getPlugins <em>Plugins</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class DocumentationImpl extends StructuralElementImpl implements Documentation
{
  private static final String JAVADOC_MARKER_CLASS = "Javadoc";

  private static final String SCHEMADOC_MARKER_CLASS = "Schemadoc";

  /**
   * The cached value of the '{@link #getEmbeddableElements() <em>Embeddable Elements</em>}' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getEmbeddableElements()
   * @generated
   * @ordered
   */
  protected EList<EmbeddableElement> embeddableElements;

  /**
   * The cached value of the '{@link #getDependencies() <em>Dependencies</em>}' reference list. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getDependencies()
   * @generated
   * @ordered
   */
  protected EList<Documentation> dependencies;

  /**
   * The default value of the '{@link #getProject() <em>Project</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getProject()
   * @generated
   * @ordered
   */
  protected static final String PROJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getProject() <em>Project</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getProject()
   * @generated
   * @ordered
   */
  protected String project = PROJECT_EDEFAULT;

  /**
   * The cached value of the '{@link #getPlugins() <em>Plugins</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getPlugins()
   * @generated
   * @ordered
   */
  protected EList<Plugin> plugins;

  private StructuralElement defaultElement;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DocumentationImpl()
  {
    super();
  }

  DocumentationImpl(Context context, String project)
  {
    super(null, "plugins/" + project + "/html", context.getRoot());
    setContext(context);
    this.project = project;
    context.register(getId(), this);

    File projectFolder = getOutputFile().getParentFile();
    setTitle(AssembleScripts.getPluginName(projectFolder));

    analyzeDependencies(projectFolder);
    loadPlugins(projectFolder);
    analyzeDocumentation(projectFolder);
  }

  private void analyzeDependencies(File projectFolder)
  {
    Context context = getContext();
    for (String dependency : AssembleScripts.getDependencies(projectFolder))
    {
      if (context.getDocumentation(dependency) == null)
      {
        new DocumentationImpl(context, dependency);
      }
    }
  }

  private void analyzeDocumentation(File projectFolder)
  {
    Context context = getContext();
    for (ClassDoc classDoc : context.getRoot().classes())
    {
      if (classDoc.containingClass() == null)
      {
        File file = classDoc.position().file();
        if (ArticleUtil.containsFile(projectFolder, file))
        {
          PackageDoc packageDoc = classDoc.containingPackage();
          StructuralElement parent = analyzePackage(packageDoc);
          analyzeClass(parent, classDoc);
        }
      }
    }
  }

  private StructuralElement analyzePackage(PackageDoc packageDoc)
  {
    if (packageDoc != null)
    {
      Object value = getContext().lookup(packageDoc);
      if (value instanceof Category)
      {
        return (Category)value;
      }

      PackageDoc parentDoc = ArticleUtil.getParentPackage(getContext().getRoot(), packageDoc);
      if (ArticleUtil.isDocumented(packageDoc))
      {
        StructuralElement parent = analyzePackage(parentDoc);

        ClassDoc javadocMarkerClass = packageDoc.findClass(JAVADOC_MARKER_CLASS);
        if (javadocMarkerClass != null && javadocMarkerClass.isPackagePrivate())
        {
          return new JavadocImpl(parent, packageDoc);
        }

        ClassDoc schemadocMarkerClass = packageDoc.findClass(SCHEMADOC_MARKER_CLASS);
        if (schemadocMarkerClass != null && schemadocMarkerClass.isPackagePrivate())
        {
          return new SchemadocImpl(parent, packageDoc);
        }

        return new CategoryImpl(parent, packageDoc);
      }

      warnIfSubPackagesUndocumented(packageDoc, parentDoc);
    }

    return this;
  }

  private void analyzeClass(StructuralElement parent, ClassDoc classDoc)
  {
    // TODO Non-public classes?

    if (ArticleUtil.isIgnore(classDoc))
    {
      return;
    }

    if (parent instanceof Javadoc && classDoc.simpleTypeName().equals(JAVADOC_MARKER_CLASS))
    {
      return;
    }

    if (parent instanceof Schemadoc && classDoc.simpleTypeName().equals(SCHEMADOC_MARKER_CLASS))
    {
      return;
    }

    Tag[] externals = classDoc.tags("@external");
    if (externals != null && externals.length != 0)
    {
      String url = externals[0].inlineTags()[0].text();
      new ExternalArticleImpl(parent, classDoc, url);
      return;
    }

    if (ArticleUtil.isSnippet(getContext().getRoot(), classDoc))
    {
      new SnippetImpl(this, classDoc);
      analyzeClassChildren(this, classDoc);
    }
    else
    {
      Chapter chapter = createChapter(parent, classDoc);
      analyzeClassChildren(chapter, classDoc);
    }
  }

  private void analyzeClassChildren(StructuralElement parent, ClassDoc classDoc)
  {
    for (ClassDoc child : classDoc.innerClasses())
    {
      analyzeClass(parent, child);
    }

    for (MethodDoc methodDoc : classDoc.methods())
    {
      analyzeMethod(methodDoc);
    }
  }

  private void analyzeMethod(MethodDoc methodDoc)
  {
    if (ArticleUtil.isIgnore(methodDoc))
    {
      return;
    }

    if (ArticleUtil.isSnippet(getContext().getRoot(), methodDoc))
    {
      new SnippetImpl(this, methodDoc);
    }

    if (ArticleUtil.isFactory(methodDoc))
    {
      new FactoryImpl(this, methodDoc);
    }
  }

  private void warnIfSubPackagesUndocumented(PackageDoc packageDoc, PackageDoc parentDoc)
  {
    while (parentDoc != null)
    {
      if (ArticleUtil.isDocumented(parentDoc))
      {
        System.err.println("Undocumented category " + packageDoc.name());
        break;
      }

      parentDoc = ArticleUtil.getParentPackage(getContext().getRoot(), parentDoc);
    }
  }

  private Chapter createChapter(StructuralElement parent, ClassDoc classDoc)
  {
    if (parent instanceof Chapter)
    {
      return new ChapterImpl(parent, classDoc);
    }

    return new ArticleImpl(parent, classDoc);
  }

  private void loadPlugins(File projectFolder)
  {
    try
    {
      Resource resource = AssembleScripts.JavaDoc.getTocXmiResource(projectFolder, false);

      for (Object content : resource.getContents().toArray())
      {
        getPlugins().add((Plugin)content);
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
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
    return ArticlePackage.Literals.DOCUMENTATION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Context getContext()
  {
    if (eContainerFeatureID() != ArticlePackage.DOCUMENTATION__CONTEXT)
      return null;
    return (Context)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetContext(Context newContext, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newContext, ArticlePackage.DOCUMENTATION__CONTEXT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setContext(Context newContext)
  {
    if (newContext != eInternalContainer()
        || (eContainerFeatureID() != ArticlePackage.DOCUMENTATION__CONTEXT && newContext != null))
    {
      if (EcoreUtil.isAncestor(this, newContext))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newContext != null)
        msgs = ((InternalEObject)newContext).eInverseAdd(this, ArticlePackage.CONTEXT__DOCUMENTATIONS, Context.class,
            msgs);
      msgs = basicSetContext(newContext, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.DOCUMENTATION__CONTEXT, newContext,
          newContext));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<EmbeddableElement> getEmbeddableElements()
  {
    if (embeddableElements == null)
    {
      embeddableElements = new EObjectContainmentWithInverseEList<EmbeddableElement>(EmbeddableElement.class, this,
          ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS, ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION);
    }
    return embeddableElements;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<Documentation> getDependencies()
  {
    if (dependencies == null)
    {
      dependencies = new EObjectEList<Documentation>(Documentation.class, this,
          ArticlePackage.DOCUMENTATION__DEPENDENCIES);
    }
    return dependencies;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getProject()
  {
    return project;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<Plugin> getPlugins()
  {
    if (plugins == null)
    {
      plugins = new EObjectContainmentEList<Plugin>(Plugin.class, this, ArticlePackage.DOCUMENTATION__PLUGINS);
    }
    return plugins;
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      return basicSetContext((Context)otherEnd, msgs);
    case ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getEmbeddableElements()).basicAdd(otherEnd, msgs);
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      return basicSetContext(null, msgs);
    case ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS:
      return ((InternalEList<?>)getEmbeddableElements()).basicRemove(otherEnd, msgs);
    case ArticlePackage.DOCUMENTATION__PLUGINS:
      return ((InternalEList<?>)getPlugins()).basicRemove(otherEnd, msgs);
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.CONTEXT__DOCUMENTATIONS, Context.class, msgs);
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      return getContext();
    case ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS:
      return getEmbeddableElements();
    case ArticlePackage.DOCUMENTATION__DEPENDENCIES:
      return getDependencies();
    case ArticlePackage.DOCUMENTATION__PROJECT:
      return getProject();
    case ArticlePackage.DOCUMENTATION__PLUGINS:
      return getPlugins();
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      setContext((Context)newValue);
      return;
    case ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS:
      getEmbeddableElements().clear();
      getEmbeddableElements().addAll((Collection<? extends EmbeddableElement>)newValue);
      return;
    case ArticlePackage.DOCUMENTATION__DEPENDENCIES:
      getDependencies().clear();
      getDependencies().addAll((Collection<? extends Documentation>)newValue);
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      setContext((Context)null);
      return;
    case ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS:
      getEmbeddableElements().clear();
      return;
    case ArticlePackage.DOCUMENTATION__DEPENDENCIES:
      getDependencies().clear();
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
    case ArticlePackage.DOCUMENTATION__CONTEXT:
      return getContext() != null;
    case ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS:
      return embeddableElements != null && !embeddableElements.isEmpty();
    case ArticlePackage.DOCUMENTATION__DEPENDENCIES:
      return dependencies != null && !dependencies.isEmpty();
    case ArticlePackage.DOCUMENTATION__PROJECT:
      return PROJECT_EDEFAULT == null ? project != null : !PROJECT_EDEFAULT.equals(project);
    case ArticlePackage.DOCUMENTATION__PLUGINS:
      return plugins != null && !plugins.isEmpty();
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
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (project: ");
    result.append(project);
    result.append(')');
    return result.toString();
  }

  @Override
  protected String getKind()
  {
    return "Documentation";
  }

  public void setDefaultElement(StructuralElement defaultElement)
  {
    if (this.defaultElement != null)
    {
      System.err.println("Multiple default elements declared");
    }

    this.defaultElement = defaultElement;
  }

  @Override
  public RootDoc getDoc()
  {
    return (RootDoc)super.getDoc();
  }

  @Override
  public Object getId()
  {
    return project;
  }

  @Override
  public void generate() throws IOException
  {
    super.generate();
    generateToc();
  }

  private void generateToc() throws IOException
  {
    File project = getOutputFile().getParentFile();
    FileWriter out = null;

    try
    {
      File target = new File(project, "toc.xml");
      System.out.println("Generating " + target.getCanonicalPath());

      out = new FileWriter(target);
      BufferedWriter writer = new BufferedWriter(out);

      try
      {
        String href = "javadoc/overview-summary.html";
        if (defaultElement != null)
        {
          href = ((StructuralElementImpl)defaultElement).getTocHref();
        }

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
        writer.write("<?NLS TYPE=\"org.eclipse.help.toc\"?>\n\n");
        writer.write("<toc label=\"" + getDocumentation().getTitle() + "\" topic=\"" + href + "\">\n");

        generateTocEntries(writer, "\t");

        writer.write("</toc>\n");
        writer.flush();
      }
      finally
      {
      }
    }
    finally
    {
      ArticleUtil.close(out);
    }
  }
} // DocumentationImpl
