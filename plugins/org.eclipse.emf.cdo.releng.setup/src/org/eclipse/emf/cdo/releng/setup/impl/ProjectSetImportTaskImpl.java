/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.ProjectSetImportTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogMonitor;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.URIConverter;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkingSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Project Set Import Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectSetImportTaskImpl#getURL <em>URL</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectSetImportTaskImpl extends SetupTaskImpl implements ProjectSetImportTask
{
  /**
   * The default value of the '{@link #getURL() <em>URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getURL()
   * @generated
   * @ordered
   */
  protected static final String URL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getURL() <em>URL</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getURL()
   * @generated
   * @ordered
   */
  protected String uRL = URL_EDEFAULT;

  private Helper helper;

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected ProjectSetImportTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.PROJECT_SET_IMPORT_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getURL()
  {
    return uRL;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setURL(String newURL)
  {
    String oldURL = uRL;
    uRL = newURL;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PROJECT_SET_IMPORT_TASK__URL, oldURL, uRL));
    }
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT_SET_IMPORT_TASK__URL:
      return getURL();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT_SET_IMPORT_TASK__URL:
      setURL((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT_SET_IMPORT_TASK__URL:
      setURL(URL_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT_SET_IMPORT_TASK__URL:
      return URL_EDEFAULT == null ? uRL != null : !URL_EDEFAULT.equals(uRL);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
    result.append(" (uRL: ");
    result.append(uRL);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    URI uri = context.redirect(URI.createURI(getURL()));
    helper = new Helper(uri);
    return helper.isNeeded(context);
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    helper.perform(context);
  }

  private static class Helper
  {
    private URI uri;

    private String content;

    public Helper(URI uri)
    {
      this.uri = uri;
    }

    public boolean isNeeded(SetupTaskContext context) throws Exception
    {
      InputStream inputStream = null;
      try
      {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        inputStream = URIConverter.INSTANCE.createInputStream(uri, null);
        Document document = documentBuilder.parse(inputStream);
        document.getDocumentElement();
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("project");
        for (int i = 0, length = nodeList.getLength(); i < length; ++i)
        {
          Element element = (Element)nodeList.item(i);
          String reference = element.getAttribute("reference");
          String[] components = reference.split(",");
          String projectName = components[components.length - 1];
          if (!root.exists(new Path(new Path(projectName).lastSegment())))
          {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            out.close();
            content = out.toString();

            return true;
          }
        }
      }
      finally
      {
        IOUtil.close(inputStream);
      }

      return false;
    }

    @SuppressWarnings("restriction")
    public void perform(SetupTaskContext context) throws Exception
    {
      org.eclipse.team.internal.ui.wizards.ImportProjectSetOperation importProjectSetOperation = new org.eclipse.team.internal.ui.wizards.ImportProjectSetOperation(
          null, content, uri.toString(), new IWorkingSet[0]);
      importProjectSetOperation.run(new ProgressLogMonitor(context));
    }
  }

} // ProjectSetImportTaskImpl
