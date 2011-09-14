/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.AssembleScripts;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Javadoc;
import org.eclipse.emf.cdo.releng.doc.article.JavadocGroup;
import org.eclipse.emf.cdo.releng.doc.article.JavadocPackage;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.sun.javadoc.PackageDoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Javadoc</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.JavadocImpl#getGroups <em>Groups</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class JavadocImpl extends CategoryImpl implements Javadoc
{
  private EList<JavadocGroup> groups = new BasicEList<JavadocGroup>();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected JavadocImpl()
  {
    super();
  }

  JavadocImpl(StructuralElement parent, PackageDoc packageDoc)
  {
    super(parent, packageDoc);

    try
    {
      File projectFolder = getDocumentation().getOutputFile().getParentFile();
      Resource resource = AssembleScripts.JavaDoc.getTocResource(projectFolder, false);

      for (EObject eObject : resource.getContents())
      {
        groups.add((JavadocGroup)eObject);
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
    return ArticlePackage.Literals.JAVADOC;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public EList<JavadocGroup> getGroups()
  {
    return ECollections.unmodifiableEList(groups);
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
    case ArticlePackage.JAVADOC__GROUPS:
      return getGroups();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case ArticlePackage.JAVADOC__GROUPS:
      return !getGroups().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public void generate() throws IOException
  {
    // Do nothing
  }

  @Override
  protected void generateTocEntry(BufferedWriter writer, String prefix) throws IOException
  {
    writer.write(prefix + "<topic label=\"" + getTitle() + "\" href=\"javadoc/overview-summary.html\">\n");

    for (JavadocGroup javadocGroup : getGroups())
    {
      EList<JavadocPackage> packages = javadocGroup.getPackages();
      String href = getHref(packages.get(0));
      writer.write(prefix + "\t<topic label=\"" + javadocGroup.getName() + "\" href=\"" + href + "\">\n");

      for (JavadocPackage javadocPackage : packages)
      {
        href = getHref(javadocPackage);
        writer.write(prefix + "\t\t<topic label=\"" + javadocPackage.getName() + "\" href=\"" + href + "\" />\n");
      }

      writer.write(prefix + "\t</topic>\n");
    }

    writer.write(prefix + "</topic>\n");
  }

  private String getHref(JavadocPackage javadocPackage)
  {
    return "javadoc/" + javadocPackage.getName().replace('.', '/') + "/package-summary.html";
  }
} // JavadocImpl
