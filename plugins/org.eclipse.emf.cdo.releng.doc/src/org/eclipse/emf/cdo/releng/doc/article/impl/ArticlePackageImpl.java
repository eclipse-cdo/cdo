/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.Article;
import org.eclipse.emf.cdo.releng.doc.article.ArticleFactory;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;
import org.eclipse.emf.cdo.releng.doc.article.Callout;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.Chapter;
import org.eclipse.emf.cdo.releng.doc.article.Context;
import org.eclipse.emf.cdo.releng.doc.article.Diagram;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;
import org.eclipse.emf.cdo.releng.doc.article.Embedding;
import org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint;
import org.eclipse.emf.cdo.releng.doc.article.ExternalArticle;
import org.eclipse.emf.cdo.releng.doc.article.ExternalTarget;
import org.eclipse.emf.cdo.releng.doc.article.Factory;
import org.eclipse.emf.cdo.releng.doc.article.Identifiable;
import org.eclipse.emf.cdo.releng.doc.article.JavaElement;
import org.eclipse.emf.cdo.releng.doc.article.JavaPackage;
import org.eclipse.emf.cdo.releng.doc.article.Javadoc;
import org.eclipse.emf.cdo.releng.doc.article.Link;
import org.eclipse.emf.cdo.releng.doc.article.LinkTarget;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;
import org.eclipse.emf.cdo.releng.doc.article.Schemadoc;
import org.eclipse.emf.cdo.releng.doc.article.Snippet;
import org.eclipse.emf.cdo.releng.doc.article.SourceCode;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.Text;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import com.sun.javadoc.Doc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.File;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class ArticlePackageImpl extends EPackageImpl implements ArticlePackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass documentationEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass contextEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass categoryEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass articleEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass chapterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass snippetEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass diagramEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass factoryEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass javaElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass structuralElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass linkTargetEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass calloutEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass embeddableElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass externalTargetEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass identifiableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass bodyEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass bodyElementContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass bodyElementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass textEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass linkEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass embeddingEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass sourceCodeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass pluginEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass javaPackageEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass javadocEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass externalArticleEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass schemadocEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass extensionPointEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType rootDocEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType fileEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType docEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType tagEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#eNS_URI
   * @see #init()
   * @generated
   */
  private ArticlePackageImpl()
  {
    super(eNS_URI, ArticleFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link ArticlePackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static ArticlePackage init()
  {
    if (isInited)
      return (ArticlePackage)EPackage.Registry.INSTANCE.getEPackage(ArticlePackage.eNS_URI);

    // Obtain or create and register package
    ArticlePackageImpl theArticlePackage = (ArticlePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ArticlePackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new ArticlePackageImpl());

    isInited = true;

    // Create package meta-data objects
    theArticlePackage.createPackageContents();

    // Initialize created meta-data
    theArticlePackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theArticlePackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(ArticlePackage.eNS_URI, theArticlePackage);
    return theArticlePackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getDocumentation()
  {
    return documentationEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDocumentation_Context()
  {
    return (EReference)documentationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDocumentation_EmbeddableElements()
  {
    return (EReference)documentationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDocumentation_Dependencies()
  {
    return (EReference)documentationEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getDocumentation_Project()
  {
    return (EAttribute)documentationEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getDocumentation_Plugins()
  {
    return (EReference)documentationEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getContext()
  {
    return contextEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getContext_BaseFolder()
  {
    return (EAttribute)contextEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getContext_Project()
  {
    return (EAttribute)contextEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getContext_Documentations()
  {
    return (EReference)contextEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getContext_Root()
  {
    return (EAttribute)contextEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCategory()
  {
    return categoryEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getArticle()
  {
    return articleEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getChapter()
  {
    return chapterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getChapter_Article()
  {
    return (EReference)chapterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getSnippet()
  {
    return snippetEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getSnippet_Callouts()
  {
    return (EReference)snippetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getDiagram()
  {
    return diagramEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getFactory()
  {
    return factoryEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getJavaElement()
  {
    return javaElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getJavaElement_ClassFile()
  {
    return (EAttribute)javaElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getStructuralElement()
  {
    return structuralElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getStructuralElement_Children()
  {
    return (EReference)structuralElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getStructuralElement_Parent()
  {
    return (EReference)structuralElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getStructuralElement_Title()
  {
    return (EAttribute)structuralElementEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getStructuralElement_Path()
  {
    return (EAttribute)structuralElementEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getStructuralElement_FullPath()
  {
    return (EAttribute)structuralElementEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getStructuralElement_OutputFile()
  {
    return (EAttribute)structuralElementEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getStructuralElement_Documentation()
  {
    return (EReference)structuralElementEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getStructuralElement_Doc()
  {
    return (EAttribute)structuralElementEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getLinkTarget()
  {
    return linkTargetEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getLinkTarget_DefaultLabel()
  {
    return (EAttribute)linkTargetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getLinkTarget_Tooltip()
  {
    return (EAttribute)linkTargetEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getCallout()
  {
    return calloutEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getCallout_Snippet()
  {
    return (EReference)calloutEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getEmbeddableElement()
  {
    return embeddableElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getEmbeddableElement_Documentation()
  {
    return (EReference)embeddableElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getExternalTarget()
  {
    return externalTargetEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getExternalTarget_Url()
  {
    return (EAttribute)externalTargetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getIdentifiable()
  {
    return identifiableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getIdentifiable_Id()
  {
    return (EAttribute)identifiableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getBody()
  {
    return bodyEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getBody_Category()
  {
    return (EReference)bodyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getBody_Number()
  {
    return (EAttribute)bodyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getBodyElementContainer()
  {
    return bodyElementContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getBodyElementContainer_Elements()
  {
    return (EReference)bodyElementContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getBodyElement()
  {
    return bodyElementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getBodyElement_Container()
  {
    return (EReference)bodyElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getBodyElement_Tag()
  {
    return (EAttribute)bodyElementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getText()
  {
    return textEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getLink()
  {
    return linkEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getLink_Target()
  {
    return (EReference)linkEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getEmbedding()
  {
    return embeddingEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getEmbedding_Element()
  {
    return (EReference)embeddingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getSourceCode()
  {
    return sourceCodeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getPlugin()
  {
    return pluginEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getPlugin_Name()
  {
    return (EAttribute)pluginEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getPlugin_Packages()
  {
    return (EReference)pluginEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getPlugin_Label()
  {
    return (EAttribute)pluginEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getPlugin_ExtensionPoints()
  {
    return (EReference)pluginEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getJavaPackage()
  {
    return javaPackageEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getJavaPackage_Name()
  {
    return (EAttribute)javaPackageEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getJavaPackage_Plugin()
  {
    return (EReference)javaPackageEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getJavadoc()
  {
    return javadocEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getExternalArticle()
  {
    return externalArticleEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getExternalArticle_Url()
  {
    return (EAttribute)externalArticleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getSchemadoc()
  {
    return schemadocEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EClass getExtensionPoint()
  {
    return extensionPointEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EAttribute getExtensionPoint_Name()
  {
    return (EAttribute)extensionPointEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EReference getExtensionPoint_Plugin()
  {
    return (EReference)extensionPointEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getRootDoc()
  {
    return rootDocEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getFile()
  {
    return fileEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getDoc()
  {
    return docEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getTag()
  {
    return tagEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ArticleFactory getArticleFactory()
  {
    return (ArticleFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    documentationEClass = createEClass(DOCUMENTATION);
    createEReference(documentationEClass, DOCUMENTATION__CONTEXT);
    createEReference(documentationEClass, DOCUMENTATION__EMBEDDABLE_ELEMENTS);
    createEReference(documentationEClass, DOCUMENTATION__DEPENDENCIES);
    createEAttribute(documentationEClass, DOCUMENTATION__PROJECT);
    createEReference(documentationEClass, DOCUMENTATION__PLUGINS);

    contextEClass = createEClass(CONTEXT);
    createEAttribute(contextEClass, CONTEXT__BASE_FOLDER);
    createEAttribute(contextEClass, CONTEXT__PROJECT);
    createEReference(contextEClass, CONTEXT__DOCUMENTATIONS);
    createEAttribute(contextEClass, CONTEXT__ROOT);

    categoryEClass = createEClass(CATEGORY);

    articleEClass = createEClass(ARTICLE);

    chapterEClass = createEClass(CHAPTER);
    createEReference(chapterEClass, CHAPTER__ARTICLE);

    snippetEClass = createEClass(SNIPPET);
    createEReference(snippetEClass, SNIPPET__CALLOUTS);

    diagramEClass = createEClass(DIAGRAM);

    factoryEClass = createEClass(FACTORY);

    javaElementEClass = createEClass(JAVA_ELEMENT);
    createEAttribute(javaElementEClass, JAVA_ELEMENT__CLASS_FILE);

    structuralElementEClass = createEClass(STRUCTURAL_ELEMENT);
    createEReference(structuralElementEClass, STRUCTURAL_ELEMENT__CHILDREN);
    createEReference(structuralElementEClass, STRUCTURAL_ELEMENT__PARENT);
    createEAttribute(structuralElementEClass, STRUCTURAL_ELEMENT__TITLE);
    createEAttribute(structuralElementEClass, STRUCTURAL_ELEMENT__PATH);
    createEAttribute(structuralElementEClass, STRUCTURAL_ELEMENT__FULL_PATH);
    createEAttribute(structuralElementEClass, STRUCTURAL_ELEMENT__OUTPUT_FILE);
    createEReference(structuralElementEClass, STRUCTURAL_ELEMENT__DOCUMENTATION);
    createEAttribute(structuralElementEClass, STRUCTURAL_ELEMENT__DOC);

    linkTargetEClass = createEClass(LINK_TARGET);
    createEAttribute(linkTargetEClass, LINK_TARGET__DEFAULT_LABEL);
    createEAttribute(linkTargetEClass, LINK_TARGET__TOOLTIP);

    calloutEClass = createEClass(CALLOUT);
    createEReference(calloutEClass, CALLOUT__SNIPPET);

    embeddableElementEClass = createEClass(EMBEDDABLE_ELEMENT);
    createEReference(embeddableElementEClass, EMBEDDABLE_ELEMENT__DOCUMENTATION);

    externalTargetEClass = createEClass(EXTERNAL_TARGET);
    createEAttribute(externalTargetEClass, EXTERNAL_TARGET__URL);

    identifiableEClass = createEClass(IDENTIFIABLE);
    createEAttribute(identifiableEClass, IDENTIFIABLE__ID);

    bodyEClass = createEClass(BODY);
    createEReference(bodyEClass, BODY__CATEGORY);
    createEAttribute(bodyEClass, BODY__NUMBER);

    bodyElementContainerEClass = createEClass(BODY_ELEMENT_CONTAINER);
    createEReference(bodyElementContainerEClass, BODY_ELEMENT_CONTAINER__ELEMENTS);

    bodyElementEClass = createEClass(BODY_ELEMENT);
    createEReference(bodyElementEClass, BODY_ELEMENT__CONTAINER);
    createEAttribute(bodyElementEClass, BODY_ELEMENT__TAG);

    textEClass = createEClass(TEXT);

    linkEClass = createEClass(LINK);
    createEReference(linkEClass, LINK__TARGET);

    embeddingEClass = createEClass(EMBEDDING);
    createEReference(embeddingEClass, EMBEDDING__ELEMENT);

    sourceCodeEClass = createEClass(SOURCE_CODE);

    pluginEClass = createEClass(PLUGIN);
    createEAttribute(pluginEClass, PLUGIN__NAME);
    createEReference(pluginEClass, PLUGIN__PACKAGES);
    createEAttribute(pluginEClass, PLUGIN__LABEL);
    createEReference(pluginEClass, PLUGIN__EXTENSION_POINTS);

    javaPackageEClass = createEClass(JAVA_PACKAGE);
    createEAttribute(javaPackageEClass, JAVA_PACKAGE__NAME);
    createEReference(javaPackageEClass, JAVA_PACKAGE__PLUGIN);

    javadocEClass = createEClass(JAVADOC);

    externalArticleEClass = createEClass(EXTERNAL_ARTICLE);
    createEAttribute(externalArticleEClass, EXTERNAL_ARTICLE__URL);

    schemadocEClass = createEClass(SCHEMADOC);

    extensionPointEClass = createEClass(EXTENSION_POINT);
    createEAttribute(extensionPointEClass, EXTENSION_POINT__NAME);
    createEReference(extensionPointEClass, EXTENSION_POINT__PLUGIN);

    // Create data types
    rootDocEDataType = createEDataType(ROOT_DOC);
    fileEDataType = createEDataType(FILE);
    docEDataType = createEDataType(DOC);
    tagEDataType = createEDataType(TAG);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    documentationEClass.getESuperTypes().add(this.getStructuralElement());
    categoryEClass.getESuperTypes().add(this.getBody());
    articleEClass.getESuperTypes().add(this.getChapter());
    chapterEClass.getESuperTypes().add(this.getBody());
    snippetEClass.getESuperTypes().add(this.getEmbeddableElement());
    diagramEClass.getESuperTypes().add(this.getBodyElement());
    factoryEClass.getESuperTypes().add(this.getEmbeddableElement());
    javaElementEClass.getESuperTypes().add(this.getLinkTarget());
    structuralElementEClass.getESuperTypes().add(this.getLinkTarget());
    linkTargetEClass.getESuperTypes().add(this.getIdentifiable());
    calloutEClass.getESuperTypes().add(this.getBodyElementContainer());
    embeddableElementEClass.getESuperTypes().add(this.getIdentifiable());
    externalTargetEClass.getESuperTypes().add(this.getLinkTarget());
    bodyEClass.getESuperTypes().add(this.getStructuralElement());
    bodyEClass.getESuperTypes().add(this.getBodyElementContainer());
    textEClass.getESuperTypes().add(this.getBodyElement());
    linkEClass.getESuperTypes().add(this.getBodyElement());
    embeddingEClass.getESuperTypes().add(this.getBodyElement());
    sourceCodeEClass.getESuperTypes().add(this.getExternalTarget());
    javadocEClass.getESuperTypes().add(this.getCategory());
    externalArticleEClass.getESuperTypes().add(this.getArticle());
    schemadocEClass.getESuperTypes().add(this.getCategory());

    // Initialize classes and features; add operations and parameters
    initEClass(documentationEClass, Documentation.class, "Documentation", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDocumentation_Context(), this.getContext(), this.getContext_Documentations(), "context", null, 1,
        1, Documentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentation_EmbeddableElements(), this.getEmbeddableElement(),
        this.getEmbeddableElement_Documentation(), "embeddableElements", null, 0, -1, Documentation.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentation_Dependencies(), this.getDocumentation(), null, "dependencies", null, 0, -1,
        Documentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDocumentation_Project(), ecorePackage.getEString(), "project", null, 1, 1, Documentation.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDocumentation_Plugins(), this.getPlugin(), null, "plugins", null, 0, -1, Documentation.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        IS_DERIVED, IS_ORDERED);

    initEClass(contextEClass, Context.class, "Context", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getContext_BaseFolder(), this.getFile(), "baseFolder", null, 1, 1, Context.class, !IS_TRANSIENT,
        !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getContext_Project(), ecorePackage.getEString(), "project", null, 1, 1, Context.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getContext_Documentations(), this.getDocumentation(), this.getDocumentation_Context(),
        "documentations", null, 1, -1, Context.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getContext_Root(), this.getRootDoc(), "root", null, 1, 1, Context.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(categoryEClass, Category.class, "Category", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(articleEClass, Article.class, "Article", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(chapterEClass, Chapter.class, "Chapter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChapter_Article(), this.getArticle(), null, "article", null, 1, 1, Chapter.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
        IS_ORDERED);

    initEClass(snippetEClass, Snippet.class, "Snippet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSnippet_Callouts(), this.getCallout(), this.getCallout_Snippet(), "callouts", null, 0, -1,
        Snippet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(diagramEClass, Diagram.class, "Diagram", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(factoryEClass, Factory.class, "Factory", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(javaElementEClass, JavaElement.class, "JavaElement", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJavaElement_ClassFile(), this.getFile(), "classFile", null, 0, 1, JavaElement.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(structuralElementEClass, StructuralElement.class, "StructuralElement", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getStructuralElement_Children(), this.getStructuralElement(), this.getStructuralElement_Parent(),
        "children", null, 0, -1, StructuralElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStructuralElement_Parent(), this.getStructuralElement(), this.getStructuralElement_Children(),
        "parent", null, 0, 1, StructuralElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStructuralElement_Title(), ecorePackage.getEString(), "title", null, 1, 1,
        StructuralElement.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStructuralElement_Path(), ecorePackage.getEString(), "path", null, 1, 1, StructuralElement.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStructuralElement_FullPath(), ecorePackage.getEString(), "fullPath", null, 1, 1,
        StructuralElement.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        IS_DERIVED, IS_ORDERED);
    initEAttribute(getStructuralElement_OutputFile(), this.getFile(), "outputFile", null, 1, 1,
        StructuralElement.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        IS_DERIVED, IS_ORDERED);
    initEReference(getStructuralElement_Documentation(), this.getDocumentation(), null, "documentation", null, 1, 1,
        StructuralElement.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getStructuralElement_Doc(), this.getDoc(), "doc", null, 1, 1, StructuralElement.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(linkTargetEClass, LinkTarget.class, "LinkTarget", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLinkTarget_DefaultLabel(), ecorePackage.getEString(), "defaultLabel", null, 0, 1,
        LinkTarget.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getLinkTarget_Tooltip(), ecorePackage.getEString(), "tooltip", null, 1, 1, LinkTarget.class,
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    EOperation op = addEOperation(linkTargetEClass, ecorePackage.getEString(), "linkFrom", 1, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getStructuralElement(), "source", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(calloutEClass, Callout.class, "Callout", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCallout_Snippet(), this.getSnippet(), this.getSnippet_Callouts(), "snippet", null, 1, 1,
        Callout.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(embeddableElementEClass, EmbeddableElement.class, "EmbeddableElement", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEmbeddableElement_Documentation(), this.getDocumentation(),
        this.getDocumentation_EmbeddableElements(), "documentation", null, 1, 1, EmbeddableElement.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(externalTargetEClass, ExternalTarget.class, "ExternalTarget", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExternalTarget_Url(), ecorePackage.getEString(), "url", null, 0, 1, ExternalTarget.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(identifiableEClass, Identifiable.class, "Identifiable", IS_ABSTRACT, IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIdentifiable_Id(), ecorePackage.getEJavaObject(), "id", null, 1, 1, Identifiable.class,
        IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(bodyEClass, Body.class, "Body", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBody_Category(), this.getCategory(), null, "category", null, 0, 1, Body.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getBody_Number(), ecorePackage.getEInt(), "number", null, 0, 1, Body.class, !IS_TRANSIENT,
        !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(bodyElementContainerEClass, BodyElementContainer.class, "BodyElementContainer", IS_ABSTRACT,
        !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBodyElementContainer_Elements(), this.getBodyElement(), this.getBodyElement_Container(),
        "elements", null, 0, -1, BodyElementContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bodyElementEClass, BodyElement.class, "BodyElement", IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBodyElement_Container(), this.getBodyElementContainer(), this.getBodyElementContainer_Elements(),
        "container", null, 1, 1, BodyElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBodyElement_Tag(), this.getTag(), "tag", null, 0, 1, BodyElement.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(textEClass, Text.class, "Text", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(linkEClass, Link.class, "Link", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLink_Target(), this.getLinkTarget(), null, "target", null, 0, 1, Link.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(embeddingEClass, Embedding.class, "Embedding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEmbedding_Element(), this.getEmbeddableElement(), null, "element", null, 1, 1, Embedding.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);

    initEClass(sourceCodeEClass, SourceCode.class, "SourceCode", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(pluginEClass, Plugin.class, "Plugin", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPlugin_Name(), ecorePackage.getEString(), "name", null, 1, 1, Plugin.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPlugin_Packages(), this.getJavaPackage(), this.getJavaPackage_Plugin(), "packages", null, 1, -1,
        Plugin.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPlugin_Label(), ecorePackage.getEString(), "label", null, 1, 1, Plugin.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPlugin_ExtensionPoints(), this.getExtensionPoint(), this.getExtensionPoint_Plugin(),
        "extensionPoints", null, 0, -1, Plugin.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(javaPackageEClass, JavaPackage.class, "JavaPackage", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getJavaPackage_Name(), ecorePackage.getEString(), "name", null, 1, 1, JavaPackage.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getJavaPackage_Plugin(), this.getPlugin(), this.getPlugin_Packages(), "plugin", null, 1, 1,
        JavaPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(javadocEClass, Javadoc.class, "Javadoc", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(externalArticleEClass, ExternalArticle.class, "ExternalArticle", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExternalArticle_Url(), ecorePackage.getEString(), "url", null, 1, 1, ExternalArticle.class,
        !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(schemadocEClass, Schemadoc.class, "Schemadoc", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(extensionPointEClass, ExtensionPoint.class, "ExtensionPoint", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExtensionPoint_Name(), ecorePackage.getEString(), "name", null, 1, 1, ExtensionPoint.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExtensionPoint_Plugin(), this.getPlugin(), this.getPlugin_ExtensionPoints(), "plugin", null, 1,
        1, ExtensionPoint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize data types
    initEDataType(rootDocEDataType, RootDoc.class, "RootDoc", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(fileEDataType, File.class, "File", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(docEDataType, Doc.class, "Doc", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(tagEDataType, Tag.class, "Tag", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} // ArticlePackageImpl
