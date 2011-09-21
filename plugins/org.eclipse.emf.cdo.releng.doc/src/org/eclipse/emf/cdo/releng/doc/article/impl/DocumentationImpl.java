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
import org.eclipse.emf.common.util.URI;
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
import java.io.PrintWriter;
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
  private static final String HTML = "html";

  private static final String PLUGINS = "plugins";

  private static final String JAVADOC_MARKER_CLASS = "Javadoc";

  private static final String SCHEMADOC_MARKER_CLASS = "Schemadoc";

  private static final boolean LOCAL = false;

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

  private boolean analyzed;

  private File projectFolder;

  private String basePathForChildren;

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
    super(null, null, context.getRoot());
    setContext(context);
    this.project = project;
    context.register(getId(), this);

    basePathForChildren = PLUGINS + "/" + project + "/" + HTML;
    projectFolder = new File(new File(getContext().getBaseFolder(), PLUGINS), project);

    setTitle(AssembleScripts.getPluginName(projectFolder));

    analyzeDependencies(projectFolder);
    loadPlugins(projectFolder);
    analyzeDocumentation(projectFolder);

    if (defaultElement == null)
    {
      throw new AssertionError("No default element declared in " + getTitle());
    }

    setPath(defaultElement.getFullPath());
    analyzed = true;
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
    ClassDoc[] classes = getContext().getRoot().classes();
    for (ClassDoc classDoc : classes)
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
    {
      return null;
    }
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
    if (newContext != eInternalContainer() || eContainerFeatureID() != ArticlePackage.DOCUMENTATION__CONTEXT
        && newContext != null)
    {
      if (EcoreUtil.isAncestor(this, newContext))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newContext != null)
      {
        msgs = ((InternalEObject)newContext).eInverseAdd(this, ArticlePackage.CONTEXT__DOCUMENTATIONS, Context.class,
            msgs);
      }
      msgs = basicSetContext(newContext, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.DOCUMENTATION__CONTEXT, newContext,
          newContext));
    }
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
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
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
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (project: ");
    result.append(project);
    result.append(')');
    return result.toString();
  }

  public File getProjectFolder()
  {
    return projectFolder;
  }

  @Override
  public String getBasePathForChildren()
  {
    return basePathForChildren;
  }

  public boolean isAnalyzed()
  {
    return analyzed;
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
      throw new AssertionError("Multiple default elements declared in " + getTitle());
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
  public String getTooltip()
  {
    return getTitle();
  }

  @Override
  public String linkFrom(StructuralElement source)
  {
    return defaultElement.linkFrom(source);
  }

  @Override
  protected String getTocHref()
  {
    return ((StructuralElementImpl)defaultElement).getTocHref();
  }

  @Override
  public void generate() throws IOException
  {
    EList<StructuralElement> children = getChildren();
    if (!children.isEmpty())
    {
      StructuralElement child = children.get(0);
      File sourceFolder = child.getDoc().position().file().getParentFile();
      copyResources(sourceFolder);
    }

    super.generate();

    generateToc(false);
    generateToc(true);
  }

  @Override
  protected void generateBreadCrumbs(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    super.generateBreadCrumbs(out, linkSource);

    if (linkSource != this)
    {
      generateLink(out, linkSource, null);
    }
  }

  private void generateToc(boolean html) throws IOException
  {
    TocWriter writer = null;

    try
    {
      if (html)
      {
        writer = new TocWriter.Html(projectFolder);
      }
      else
      {
        writer = new TocWriter.Xml(projectFolder);
      }

      writer.writeGroupStart(getDocumentation().getTitle(), getTocHref());
      generateTocEntries(writer);
      writer.writeGroupEnd();
    }
    finally
    {
      ArticleUtil.close(writer);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class TocWriter extends BufferedWriter
  {
    protected File project;

    protected int level;

    public TocWriter(File project, String filename) throws IOException
    {
      super(new FileWriter(new File(LOCAL ? project.getParentFile() : project, filename)));
      this.project = project;
    }

    public final void writePrefix() throws IOException
    {
      for (int i = 0; i < level; i++)
      {
        write("  ");
      }
    }

    public abstract void writeSingle(String label, String href) throws IOException;

    public abstract void writeGroupStart(String label, String href) throws IOException;

    public abstract void writeGroupEnd() throws IOException;

    /**
     * @author Eike Stepper
     */
    public static class Html extends TocWriter
    {
      private int id;

      private String idPrefix;

      public Html(File project) throws IOException
      {
        super(project, "toc.html");
        idPrefix = project.getName().replace('.', '_') + "_";

        if (LOCAL)
        {
          write("<LINK REL=stylesheet TYPE=\"text/css\" HREF=\"toc.css\">\n");
          write("\n");

          write("<script type=\"text/javascript\">\n");
          write("  function toggle(id)\n");
          write("  {\n");
          write("    e = document.getElementById(id);\n");
          write("    e.style.display = (e.style.display == \"\" ? \"none\" : \"\");\n");
          write("    img = document.getElementById(\"img_\" + id);\n");
          write("    img.src = (e.style.display == \"none\" ? \"plus.gif\" : \"minus.gif\");\n");
          write("  }\n");
          write("</script>\n");
          write("\n");

          write("<font face=\"Segoe UI,Arial\" size=\"-1\">\n");
          write("\n");
        }
      }

      @Override
      public void close() throws IOException
      {
        if (LOCAL)
        {
          write("\n");
          write("</font>\n");
        }

        super.close();
      }

      public String nextID()
      {
        return idPrefix + ++id;
      }

      public void writeHref(String label, String href) throws IOException
      {
        label = label.replaceAll(" ", "&nbsp;");
        if (level == 0)
        {
          label = "<b>" + label + "</b>";
        }

        URI uri = URI.createURI(href);
        if (uri.isRelative())
        {
          href = project.getName() + "/" + href;
        }

        write("<a href=\"" + href + "\" target=\"content\">" + label + "</a>");
      }

      public void writeImage(String name) throws IOException
      {
        write("<img src=\"" + name + "\"/>");
      }

      @Override
      public void writeSingle(String label, String href) throws IOException
      {
        writePrefix();
        write("<div class=\"te\"><span>");
        writeImage("empty.gif");
        writeImage("article.gif");
        writeHref(label, href);
        write("</span></div>\n");
      }

      @Override
      public void writeGroupStart(String label, String href) throws IOException
      {
        String id = nextID();

        writePrefix();
        write("<div class=\"te\">");
        write("<span><a href=\"javascript:toggle('" + id + "')\">");
        write("<img src=\"" + "plus.gif" + "\" id=\"img_" + id + "\"/>");
        write("</a>");

        if (level == 0)
        {
          writeImage("documentation.gif");
        }
        else
        {
          writeImage("category.gif");
        }

        writeHref(label, href);
        write("</span></div>\n");

        writePrefix();
        write("<div id=\"" + id + "\" style=\"display:none; margin-left:20px;\">\n");
        ++level;
      }

      @Override
      public void writeGroupEnd() throws IOException
      {
        --level;
        writePrefix();
        write("</div>\n");
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Xml extends TocWriter
    {
      public Xml(File project) throws IOException
      {
        super(project, "toc.xml");
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        write("<?NLS TYPE=\"org.eclipse.help.toc\"?>\n\n");
      }

      @Override
      public void writeSingle(String label, String href) throws IOException
      {
        writePrefix();
        write("<topic label=\"" + label + "\" href=\"" + href + "\" />\n");
      }

      @Override
      public void writeGroupStart(String label, String href) throws IOException
      {
        writePrefix();
        if (level == 0)
        {
          write("<toc label=\"" + label + "\" topic=\"" + href + "\">\n");
        }
        else
        {
          write("<topic label=\"" + label + "\" href=\"" + href + "\">\n");
        }

        ++level;
      }

      @Override
      public void writeGroupEnd() throws IOException
      {
        --level;

        writePrefix();
        if (level == 0)
        {
          write("</toc>\n");
        }
        else
        {
          write("</topic>\n");
        }
      }
    }
  }
} // DocumentationImpl
