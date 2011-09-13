/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleException;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.sun.javadoc.RootDoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Context</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.ContextImpl#getBaseFolder <em>Base Folder</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.ContextImpl#getProject <em>Project</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.ContextImpl#getDocumentations <em>Documentations</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.ContextImpl#getRoot <em>Root</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ContextImpl extends EObjectImpl implements Context
{
  /**
   * The default value of the '{@link #getBaseFolder() <em>Base Folder</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getBaseFolder()
   * @generated
   * @ordered
   */
  protected static final File BASE_FOLDER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBaseFolder() <em>Base Folder</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getBaseFolder()
   * @generated
   * @ordered
   */
  protected File baseFolder = BASE_FOLDER_EDEFAULT;

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
   * The cached value of the '{@link #getDocumentations() <em>Documentations</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDocumentations()
   * @generated
   * @ordered
   */
  protected EList<Documentation> documentations;

  /**
   * The default value of the '{@link #getRoot() <em>Root</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getRoot()
   * @generated
   * @ordered
   */
  protected static final RootDoc ROOT_EDEFAULT = null;

  protected RootDoc root = ROOT_EDEFAULT;

  protected final Map<Object, Object> registry = new HashMap<Object, Object>();

  protected final Map<String, String> externalLinks = new HashMap<String, String>();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ContextImpl()
  {
    super();
  }

  ContextImpl(RootDoc root, File baseFolder, String project, String externals)
  {
    this.root = root;
    this.baseFolder = ArticleUtil.canonify(baseFolder);
    this.project = project;

    for (String external : externals.split(";"))
    {
      InputStream in = null;

      try
      {
        URL url = new URL(external + "/package-list");
        URLConnection connection = url.openConnection();
        in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null)
        {
          externalLinks.put(line, external);
        }
      }
      catch (Exception ex)
      {
        System.err.println("External link does not point to Javadocs: " + external);
      }
      finally
      {
        ArticleUtil.close(in);
      }
    }

    new DocumentationImpl(this, project);

    for (Documentation documentation : getDocumentations())
    {
      dump(documentation, "");
    }
  }

  private void dump(StructuralElement element, String prefix)
  {
    System.out.println(prefix + element.getTitle() + "   --> " + ArticleUtil.makeConsoleLink(element.getDoc()));

    for (StructuralElement child : element.getChildren())
    {
      dump(child, prefix + "  ");
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
    return ArticlePackage.Literals.CONTEXT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public File getBaseFolder()
  {
    return baseFolder;
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
  public EList<Documentation> getDocumentations()
  {
    if (documentations == null)
    {
      documentations = new EObjectContainmentWithInverseEList<Documentation>(Documentation.class, this,
          ArticlePackage.CONTEXT__DOCUMENTATIONS, ArticlePackage.DOCUMENTATION__CONTEXT);
    }
    return documentations;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public RootDoc getRoot()
  {
    return root;
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
    case ArticlePackage.CONTEXT__DOCUMENTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getDocumentations()).basicAdd(otherEnd, msgs);
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
    case ArticlePackage.CONTEXT__DOCUMENTATIONS:
      return ((InternalEList<?>)getDocumentations()).basicRemove(otherEnd, msgs);
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
    case ArticlePackage.CONTEXT__BASE_FOLDER:
      return getBaseFolder();
    case ArticlePackage.CONTEXT__PROJECT:
      return getProject();
    case ArticlePackage.CONTEXT__DOCUMENTATIONS:
      return getDocumentations();
    case ArticlePackage.CONTEXT__ROOT:
      return getRoot();
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
    case ArticlePackage.CONTEXT__DOCUMENTATIONS:
      getDocumentations().clear();
      getDocumentations().addAll((Collection<? extends Documentation>)newValue);
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
    case ArticlePackage.CONTEXT__DOCUMENTATIONS:
      getDocumentations().clear();
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
    case ArticlePackage.CONTEXT__BASE_FOLDER:
      return BASE_FOLDER_EDEFAULT == null ? baseFolder != null : !BASE_FOLDER_EDEFAULT.equals(baseFolder);
    case ArticlePackage.CONTEXT__PROJECT:
      return PROJECT_EDEFAULT == null ? project != null : !PROJECT_EDEFAULT.equals(project);
    case ArticlePackage.CONTEXT__DOCUMENTATIONS:
      return documentations != null && !documentations.isEmpty();
    case ArticlePackage.CONTEXT__ROOT:
      return ROOT_EDEFAULT == null ? getRoot() != null : !ROOT_EDEFAULT.equals(getRoot());
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
    result.append(" (baseFolder: ");
    result.append(baseFolder);
    result.append(", project: ");
    result.append(project);
    result.append(')');
    return result.toString();
  }

  public Documentation getDocumentation()
  {
    return getDocumentation(getProject());
  }

  public Documentation getDocumentation(String id)
  {
    for (Documentation documentation : getDocumentations())
    {
      if (documentation.getId().equals(id))
      {
        return documentation;
      }
    }

    return null;
  }

  public boolean isRegistered(Object id)
  {
    return registry.containsKey(id);
  }

  public void register(Object id, Object value)
  {
    if (registry.put(id, value) != null)
    {
      throw new ArticleException("Duplicate registration: " + id);
    }
  }

  public Object lookup(Object id)
  {
    return registry.get(id);
  }

  public String getExternalLink(String packageName)
  {
    String link = externalLinks.get(packageName);
    if (link != null)
    {
      return link + "/" + packageName.replace('.', '/');
    }

    return null;
  }
} // ContextImpl
