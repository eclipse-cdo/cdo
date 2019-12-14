/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.util;

/**
 *
 * @author Martin Fluegge
 */

//import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.emf.cdo.dawn.internal.util.bundle.OM;
import org.eclipse.emf.cdo.dawn.util.exceptions.EClassIncompatibleException;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0
 */
public class DawnResourceHelper
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnResourceHelper.class);

  public static String getXmiId(EObject eObject)
  {
    Resource xmiResource = eObject.eResource();
    if (xmiResource == null)
    {
      return null;
    }

    return ((XMLResource)xmiResource).getID(eObject);
  }

  public static String getXmiId(EObject eObject, Resource xmiResource)
  {
    if (xmiResource == null)
    {
      return null;
    }

    return ((XMLResource)xmiResource).getID(eObject);
  }

  public static void setXmiId(EObject eObject, String id)
  {
    Resource xmiResource = eObject.eResource();
    if (xmiResource != null)
    {
      ((XMLResource)xmiResource).setID(eObject, id);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Could set xmi id, because object is not attached to a resource!"); //$NON-NLS-1$
      }
    }
  }

  // public static String resourceToString(Resource res)
  // {
  //
  // StringBuffer s = new StringBuffer();
  // Diagram dia = null;
  // for (Object o : res.getContents())
  // {
  // LOG.info(o);
  // if (o instanceof Diagram)
  // {
  // dia = (Diagram)o;
  // break;
  // }
  // }
  //
  // LOG.info("-----------------------------------------------------");
  //
  // LOG.info("Diagram" + dia + "");
  // if (dia != null)
  // {
  // try
  // {
  // for (Object o : dia.getChildren())
  // {
  // Node n = (Node)o;
  // EObject element = n.getElement();
  //
  // LOG.info(o + " (" + ResourceHelper.getXmiId((EObject)o) + ")");
  //
  // for (EAttribute attribute : element.eClass().getEAllAttributes())
  // {
  // LOG.info("\t" + attribute.getName() + ": " + element.eGet(attribute));
  // }
  //
  // LOG.info("- LayoutContraint: " + n.getLayoutConstraint() + "");
  // }
  //
  // EObject semanticConainer = dia.getElement();
  // LOG.info("SemanticConatiner" + semanticConainer + "");
  //
  // for (EObject k : semanticConainer.eContents())
  // {
  // LOG.info("Semantic Container Element: " + k + " (" + ResourceHelper.getXmiId(k) + ")" + "");
  // }
  // }
  // catch (Exception e)
  // {
  // e.printStackTrace();
  // }
  //
  // }
  // LOG.info("-----------------------------------------------------");
  // return s.toString();
  // }

  public static Resource loadFromFile(String file, ResourceSet rsSet)
  {
    URI uri = URI.createURI("file:/" + file);

    Resource res = rsSet.getResource(uri, true);
    return res;
  }

  public static void writeToFile(String path, String xml) throws IOException
  {
    File file = new File(path);
    FileWriter writer = new FileWriter(file);
    writer.write(xml);
    writer.close();
  }

  public static String saveToXML(XMLResource resource) throws IOException
  {

    resource.setXMLVersion("1.0");

    Map<Object, Object> saveOptions = new HashMap<>();
    // options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
    // options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
    // options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
    saveOptions.putAll(resource.getDefaultSaveOptions());
    saveOptions.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
    saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
    saveOptions.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
    saveOptions.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.TRUE);
    saveOptions.put(XMIResource.OPTION_SAVE_TYPE_INFORMATION, Boolean.TRUE);
    saveOptions.put(XMIResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
    saveOptions.put(XMIResource.OPTION_ENCODING, "UTF-8");

    final XMLHelper xmlHelper = new XMIHelperImpl(resource);

    XMISaveImpl x = new XMISaveImpl(xmlHelper);

    StringWriter sw = new StringWriter();
    x.save(resource, sw, saveOptions);
    return sw.toString();
  }

  public static Resource loadFromXML(String xml, ResourceSet rsSet) throws IOException
  {
    final Resource res;

    res = new XMIResourceImpl();
    final XMLHelper xmlHelper = new XMLHelperImpl();
    ByteArrayInputStream stringreader = new ByteArrayInputStream(xml.getBytes());
    XMILoadImpl xmiload = new XMILoadImpl(xmlHelper);
    xmiload.load((XMLResource)res, stringreader, Collections.EMPTY_MAP);

    /* setdummy URI */
    if (res.getURI() == null)
    {
      res.setURI(URI.createURI(""));
    }
    return res;
  }

  /**
   * returns the diagram from the resource if no diagram can be found it returns null.
   *
   * @param res
   * @return if it exists the diagram otherwise null
   */
  public static Diagram getDiagramFromResource(Resource res)
  {
    for (Object o : res.getContents())
    {
      if (o instanceof Diagram)
      {
        return (Diagram)o;
      }
    }
    return null;
  }

  /**
   * compares two eObjects by there xmi-id whether the are equal or not
   *
   * @param oldNode
   * @param newNode
   * @return true if xmi-id equal, else otherwise
   */
  public static boolean areSameObjects(EObject oldNode, EObject newNode)
  {
    String newXMI = getXmiId(newNode);
    String oldXMI = getXmiId(oldNode);

    if (newXMI.equals(oldXMI))
    {
      return true;
    }
    return false;
  }

  /**
   * returns the same Object in the other resource. The Object is identified by the xmi ID
   *
   * @param e
   * @param resource
   * @return an object in the given resource with the same xmi Id
   */
  public static EObject getSameEObjectFromOtherResource(EObject e, XMLResource resource)
  {
    String xmiId = DawnResourceHelper.getXmiId(e);
    // EObject ret = resource.getIDToEObjectMap().get(xmiId);
    EObject ret = resource.getEObject(xmiId);
    return ret;
  }

  /**
   * finds changed objects
   *
   * @param serverO
   * @param clientO
   * @return true if the obejcts are different
   */
  public static boolean objectsHaveChanged(Object serverO, Object clientO)
  {
    if (serverO instanceof Node && clientO instanceof Node)
    {
      Node s = (Node)serverO;
      Node c = (Node)clientO;

      if (objectsHaveChanged(s.getLayoutConstraint(), c.getLayoutConstraint()))
      {
        return true;
      }
      if (objectsHaveChanged(s.getElement(), c.getElement()))
      {
        return true;
      }

    }
    else if (serverO instanceof Edge && clientO instanceof Edge) // compare edges
    {
      Edge s = (Edge)serverO;
      Edge c = (Edge)clientO;
      RelativeBendpoints sr = (RelativeBendpoints)s.getBendpoints();
      RelativeBendpoints cr = (RelativeBendpoints)c.getBendpoints();
      if (sr.getPoints().size() != cr.getPoints().size())
      {
        return true;
      }
      int i = 0;
      for (Object o : sr.getPoints())
      {
        RelativeBendpoint sb = (RelativeBendpoint)o;
        RelativeBendpoint cb = (RelativeBendpoint)cr.getPoints().get(i);
        if (objectsHaveChanged(sb, cb))
        {
          return true;
        }
        i++;
      }
    }
    else if (serverO instanceof RelativeBendpoint && clientO instanceof RelativeBendpoint)
    {
      RelativeBendpoint sb = (RelativeBendpoint)serverO;
      RelativeBendpoint cb = (RelativeBendpoint)clientO;
      if (sb.getSourceX() != cb.getSourceX())
      {
        return true;
      }
      if (sb.getSourceY() != cb.getSourceY())
      {
        return true;
      }
      if (sb.getTargetX() != cb.getTargetX())
      {
        return true;
      }
      if (sb.getTargetY() != cb.getTargetY())
      {
        return true;
      }
    }
    else if (serverO instanceof Bounds && clientO instanceof Bounds)
    {
      if (((Bounds)serverO).getX() != ((Bounds)clientO).getX())
      {
        return true;
      }
      if (((Bounds)serverO).getY() != ((Bounds)clientO).getY())
      {
        return true;
      }
      if (((Bounds)serverO).getWidth() != ((Bounds)clientO).getWidth())
      {
        return true;
      }
      if (((Bounds)serverO).getHeight() != ((Bounds)clientO).getHeight())
      {
        return true;
      }
    }
    else if (serverO instanceof EObject && clientO instanceof EObject)
    {
      EObject s = (EObject)serverO;
      EObject c = (EObject)clientO;

      for (EAttribute attribute : s.eClass().getEAllAttributes())
      {
        Object co = c.eGet(attribute);
        Object so = s.eGet(attribute);
        if (co == null || so == null)
        {
          return false;
        }
        if (!co.equals(so))
        {
          return true;
        }
      }

      // checking size of chidlren
      if (s.eContents().size() != c.eContents().size())
      {
        return true;
      }
      int i = 0;
      // chekcing every child
      for (EObject sChild : s.eContents())
      {
        EObject cChild = c.eContents().get(i);
        if (objectsHaveChanged(sChild, cChild))
        {
          return true;
        }
        i++;
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("classes are not identical...no match {0} / {1}", serverO, clientO); //$NON-NLS-1$
      }
    }
    return false;
  }

  public static Resource loadResourceFromFileString(String filePath, ResourceSet rsSet) throws IOException
  {
    try
    {
      File file = new File(filePath);
      FileReader reader = new FileReader(file);
      BufferedReader Ein = new BufferedReader(reader);
      String s;
      StringBuffer buff = new StringBuffer();
      while ((s = Ein.readLine()) != null) // Null-Referenz
      {
        buff.append(s + System.getProperty("line.separator"));
      }
      Ein.close();
      Resource resource = DawnResourceHelper.loadFromXML(buff.toString(), rsSet);
      return resource;
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public static String readFromFile(String filePath) throws IOException
  {
    try
    {
      File file = new File(filePath);
      FileReader reader = new FileReader(file);
      BufferedReader Ein = new BufferedReader(reader);
      String s;
      StringBuffer buff = new StringBuffer();
      while ((s = Ein.readLine()) != null) // Null-Referenz
      {
        buff.append(s + System.getProperty("line.separator"));
      }
      Ein.close();
      return buff.toString();

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * copies the XMI-id from oldElement to new Element
   *
   * @param newElement
   * @param oldElement
   */
  public static void copyXmiId(EObject oldElement, EObject newElement)
  {
    DawnResourceHelper.setXmiId(newElement, DawnResourceHelper.getXmiId(oldElement));
  }

  /**
   * sets the objects id in the old resource to the new resource
   *
   * @param v
   * @param oldResource
   */
  public static void setXmiId(EObject v, XMLResource oldResource)
  {
    DawnResourceHelper.setXmiId(v, getXmiId(v, oldResource));
  }

  /**
   * creates a deep Copy and also copies xmi ids This method can be used if the copy should be insertet into newParent
   *
   * @param oldObject
   * @param newParent
   * @return the copy of the oldObject
   */
  @SuppressWarnings("unchecked")
  public static EObject createCopyAndInsert(EObject oldObject, EObject newParent)
  {
    EObject ret = EcoreUtil.copy(oldObject);

    EStructuralFeature containingFeature = oldObject.eContainingFeature();

    Object get = newParent.eGet(containingFeature);
    if (get instanceof Collection<?>)
    {
      Collection<EObject> list = (Collection<EObject>)get;
      list.add(ret);
    }
    else
    {
      newParent.eSet(containingFeature, ret);
    }

    setXmiIdForChildren(oldObject, ret);

    return ret;
  }

  public static EObject createCopy(EObject v)
  {
    EObject ret = EcoreUtil.copy(v);
    return ret;
  }

  private static int setXmiIdForChildren(EObject oldChild, EObject newChild)
  {
    DawnResourceHelper.setXmiId(newChild, DawnResourceHelper.getXmiId(oldChild));
    int i = 0;
    for (EObject oldC : oldChild.eContents())
    {
      EObject newC = newChild.eContents().get(i);
      setXmiIdForChildren(oldC, newC);
      i++;
    }
    return i;
  }

  /**
   * This method provides deep copying of all childrens xmi ids from oldObject to newObject
   *
   * @param oldObject
   * @param newObject
   */
  public static void copyXmiIds(EObject oldObject, EObject newObject)
  {
    setXmiIdForChildren(oldObject, newObject);
  }

  /**
   * this Method copies the values from one Element to the other curretnly only for attributes and not for references
   */
  @Deprecated
  public static void updateElement(EObject oldElement, EObject newElement)
  {
    if (oldElement == null)
    {
      newElement = oldElement;
      return;
    }

    for (EAttribute attribute : oldElement.eClass().getEAllAttributes())
    {
      newElement.eSet(attribute, oldElement.eGet(attribute));
    }
  }

  /**
   * @param leftObject
   * @param rightObject
   * @throws EClassIncompatibleException
   */
  // TODO delte removed references
  public static void updateEObject(EObject leftObject, EObject rightObject) throws EClassIncompatibleException
  {
    if (rightObject == null) // remove left Object
    {
      removeEObjectFromParent(leftObject);
      return;
    }

    if (!leftObject.eClass().equals(rightObject.eClass()))
    {
      throw new EClassIncompatibleException(leftObject.eClass().getName() + "(" + DawnResourceHelper.getXmiId(leftObject) + ")" + "/"
          + rightObject.eClass().getName() + "(" + DawnResourceHelper.getXmiId(leftObject) + ")");
    }

    updateEAttributes(leftObject, rightObject);
    updateEReferences(leftObject, rightObject);

    for (Object o : rightObject.eContents())
    {
      EObject rightChild = (EObject)o;
      EObject leftChild = getSameEObjectFromOtherResource(rightChild, (XMLResource)leftObject.eResource());
      if (leftChild != null) // child exists
      {
        updateEObject(leftChild, rightChild); // removes it
      }
      else
      // child must be created
      {
        if (o instanceof View)
        {
          View childView = (View)o;
          EObject childViewElement = childView.getElement();
          View childViewCopy = (View)createCopyAndInsert(rightChild, leftObject);
          if (childViewElement != null)
          {
            EObject childElementCopy = getSameEObjectFromOtherResource(childViewElement, (XMLResource)leftObject.eResource());

            updateEObject(childElementCopy, childViewElement);// not shure whether this is needed here
            childViewCopy.setElement(childElementCopy);
          }

          updateEObject(childViewCopy, childView);// not shure whether this is needed here
        }
        else
        {
          EObject childCopy = createCopyAndInsert(rightChild, leftObject);
          updateEObject(childCopy, rightChild); // not shure whether this is needed here
        }
      }
    }

    ArrayList<EObject> tempDeletionList = new ArrayList<>();

    for (Object leftChild : leftObject.eContents())
    {
      tempDeletionList.add((EObject)leftChild);
    }

    // here delete all which are not in the right object
    for (EObject leftChild : tempDeletionList)
    {
      EObject rightChild = getSameEObjectFromOtherResource(leftChild, (XMLResource)rightObject.eResource());
      updateEObject(leftChild, rightChild);
    }
  }

  private static void removeEObjectFromParent(EObject leftObject)
  {
    if (leftObject instanceof View)
    {
      View view = (View)leftObject;
      if (view.getElement() != null)
      {
        removeEObjectFromParent(view.getElement());
      }
    }

    EObject parent = leftObject.eContainer();
    EStructuralFeature containingFeature = leftObject.eContainingFeature();
    if (parent != null)
    {
      Object container = parent.eGet(containingFeature);

      if (container instanceof Collection<?>)
      {
        Collection<?> collection = (Collection<?>)container;
        collection.remove(leftObject);
      }
      else
      {
        parent.eSet(containingFeature, null);
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("arent is null, object ist still removed from parent : {0} ", leftObject); //$NON-NLS-1$
      }
    }
  }

  private static void updateEReferences(EObject leftObject, EObject rightObject) throws EClassIncompatibleException
  {
    for (EReference reference : rightObject.eClass().getEReferences())
    {
      updateReference(leftObject, rightObject, reference);
    }
  }

  @SuppressWarnings("unchecked")
  public static void updateReference(EObject leftParent, EObject rightParent, EReference reference) throws EClassIncompatibleException
  {

    if (reference.getName().equals("element"))
    {
      return;
    }
    Object leftReferenceObject = leftParent.eGet(reference);
    Object rightReferenceObject = rightParent.eGet(reference);

    if (leftReferenceObject != null)
    {

      if (leftReferenceObject instanceof Collection<?>)
      {
        Collection<EObject> leftCollection = (Collection<EObject>)leftReferenceObject;
        Collection<?> rightCollection = (Collection<?>)rightReferenceObject;
        for (Object o : rightCollection)
        {
          EObject rightCollectionChild = (EObject)o;
          EObject leftCollectionChild = getSameEObjectFromOtherResource(rightCollectionChild, (XMLResource)leftParent.eResource());

          if (leftCollectionChild == null) // create
          {
            leftCollectionChild = DawnResourceHelper.createCopy(rightCollectionChild);
            leftCollection.add(leftCollectionChild);
          }
          else
          {
            if (!rightCollectionChild.eResource().equals(leftParent.eResource())) // reference to another resource
            {
              leftCollection.remove(rightCollectionChild);
              leftCollection.add(leftCollectionChild);
            }

            updateEObject(leftCollectionChild, rightCollectionChild);
          }
        }
      }
      else
      {
        updateEObject((EObject)leftParent.eGet(reference), (EObject)rightParent.eGet(reference));
      }
    }
    else
    // create
    {
      if (rightReferenceObject != null)
      {
        leftParent.eSet(reference, EcoreUtil.copy((EObject)rightReferenceObject));
      }
    }
  }

  private static void updateEAttributes(EObject leftObject, EObject rightObject)
  {
    for (EAttribute attribute : rightObject.eClass().getEAllAttributes())
    {
      leftObject.eSet(attribute, rightObject.eGet(attribute));
    }
  }

  /**
   * @since 1.0
   */
  public static void deleteViewInResource(Resource resource, EObject e)
  {
    Diagram diagram = DawnResourceHelper.getDiagramFromResource(resource);
    EObject element = ((View)e).getElement();

    if (element != null)
    {
      removeElementFromContainer(element);
    }

    if (e instanceof Node)
    {
      View node = (View)e;
      diagram.removeChild(node);// ..getChildren().add(v);
      @SuppressWarnings("unchecked")
      List<Edge> toBeDeleted = new ArrayList<Edge>(node.getSourceEdges());
      for (Object obj : toBeDeleted)
      {
        Edge edge = (Edge)obj;
        deleteViewInResource(resource, edge);
      }
    }
    else if (e instanceof Edge)
    {
      Edge edge = (Edge)e;
      diagram.removeEdge(edge);// ..getChildren().add(v);
      edge.setSource(null);
      edge.setTarget(null);
    }
  }

  private static void removeElementFromContainer(EObject element)
  {
    EStructuralFeature containingFeature = element.eContainingFeature();
    EObject container = element.eContainer();
    if (container != null)
    {
      Object get = container.eGet(containingFeature);
      if (get instanceof Collection<?>)
      {
        Collection<?> list = (Collection<?>)get;
        list.remove(element);
      }
      else
      {
        container.eSet(containingFeature, null);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static void addElementToContainer(EObject element, EObject container, EStructuralFeature containingFeature)
  {
    // EStructuralFeature containingFeature = element.eContainingFeature();
    Object get = container.eGet(containingFeature);
    if (get instanceof Collection<?>)
    {
      Collection<EObject> list = (Collection<EObject>)get;
      list.add(element);
    }
    else
    {
      container.eSet(containingFeature, element);
    }
  }

  /**
   * checks if an element with element's id exists in the give resource
   *
   * @param res
   * @param element
   * @return true is the object with the same xmi id as element is contained in the given resource
   */
  public static boolean isObjectInResource(Resource res, EObject element)
  {
    EObject object = res.getEObject(DawnResourceHelper.getXmiId(element));
    if (object == null)
    {
      return false;
    }

    return true;
  }

  public static void printResource(Resource r)
  {
    for (EObject o : r.getContents())
    {
      printEObjectFull(o, 0);
    }
  }

  public static void printEObject(EObject o, int level)
  {
    for (Object child : o.eContents())
    {
      printEObject((EObject)child, level + 1);
    }
  }

  public static void printEObjectFull(EObject o, int level)
  {
    print(tabs(level) + "--------------------------------------------");
    print(tabs(level) + "Object: " + o);
    print(tabs(level) + "o.eContainer: " + o.eContainer());
    print(tabs(level) + "o.eContainingFeature: " + o.eContainingFeature());
    print(tabs(level) + "o.eContainmentFeature: " + o.eContainmentFeature());
    print(tabs(level) + "o.eIsProxy: " + o.eIsProxy());
    print(tabs(level) + "o.eResource: " + o.eResource());
    print(tabs(level) + "o.getClass: " + o.getClass());
    print(tabs(level) + "o.eClass: " + o.eClass());
    print(tabs(level) + "o.eCrossReferences: " + o.eCrossReferences());

    print(tabs(level) + "o.eClass.getClassifierID: " + o.eClass().getClassifierID());
    print(tabs(level) + "o.eClass.getFeatureCount: " + o.eClass().getFeatureCount());
    print(tabs(level) + "o.eClass.getInstanceClassName: " + o.eClass().getInstanceClassName());
    print(tabs(level) + "o.eClass.getInstanceTypeName: " + o.eClass().getInstanceTypeName());
    print(tabs(level) + "o.eClass.getName: " + o.eClass().getName());
    print(tabs(level) + "o.eClass.getDefaultValue: " + o.eClass().getDefaultValue());
    print(tabs(level) + "o.eClass.getEPackage: " + o.eClass().getEPackage());
    print(tabs(level) + "o.eClass.getEIDAttribute: " + o.eClass().getEIDAttribute());
    print(tabs(level) + "o.eClass.getInstanceClass: " + o.eClass().getInstanceClass());

    for (EAttribute attribute : o.eClass().getEAllAttributes())
    {
      print(tabs(level + 1) + "o.eClass.getEAllAttributes.attribute: '" + attribute.getName() + "': " + o.eGet(attribute));
    }
    for (EAnnotation annotation : o.eClass().getEAnnotations())
    {
      print(tabs(level + 1) + "o.eClass().getEAnnotations().annotation: '" + annotation);
    }
    for (EReference containment : o.eClass().getEAllContainments())
    {
      print(tabs(level + 1) + "o.eClass().getEAllContainments().containment: '" + containment);
    }
    for (EGenericType genericSupertype : o.eClass().getEAllGenericSuperTypes())
    {
      print(tabs(level + 1) + "o.eClass().getEAllGenericSuperTypes().genericSupertype: '" + genericSupertype);
    }
    for (EOperation operation : o.eClass().getEAllOperations())
    {
      print(tabs(level + 1) + "o.eClass().getEAllOperations().operation: '" + operation);
    }

    for (EReference reference : o.eClass().getEAllReferences())
    {
      print(tabs(level + 1) + "o.eClass().getEAllReferences().reference: '" + reference);
    }
    for (EClass supertype : o.eClass().getEAllSuperTypes())
    {
      print(tabs(level + 1) + "o.eClass().getEAllSuperTypes().supertype: '" + supertype);
    }
    for (EStructuralFeature structuralFeature : o.eClass().getEAllStructuralFeatures())
    {
      print(tabs(level + 1) + "o.eClass().getEAllStructuralFeatures().structuralFeature: '" + structuralFeature);
    }
    for (EAnnotation annotation : o.eClass().getEAnnotations())
    {
      print(tabs(level + 1) + "o.eClass().getEAnnotations().annotation: '" + annotation);
    }
    for (EAttribute attribute : o.eClass().getEAttributes())
    {
      print(tabs(level + 1) + "o.eClass().getEAttributes().annotation: '" + attribute);
    }
    for (Adapter adapter : o.eClass().eAdapters())
    {
      print(tabs(level + 1) + "o.eClass().eAdapters().adapter: '" + adapter);
    }

    for (Object child : o.eContents())
    {
      printEObjectFull((EObject)child, level + 2);
    }
  }

  private static String tabs(int level)
  {
    String s = "";
    for (int i = 0; i < level; i++)
    {
      s += "\t";
    }
    return s;
  }

  public static String getLocationFromResource(Resource r) throws MalformedURLException, IOException
  {
    return FileLocator.resolve(new URL(r.getURI().toString())).toString().replace("file:", "");
  }

  /**********************
   * finds EditPart from a View
   *
   * @param view
   * @param dawnDiagramEditor
   * @return find an Editpart for a view with the same xmi id
   ***************************************************************************************************/
  @Deprecated
  public static EditPart findEditPartByXMIId(View view, DiagramDocumentEditor dawnDiagramEditor)
  {
    DiagramEditPart diagramEditPart = dawnDiagramEditor.getDiagramEditPart();

    for (Object e : diagramEditPart.getChildren())
    {
      EditPart ep = (EditPart)e;
      if (DawnResourceHelper.getXmiId((EObject)ep.getModel()).equals(DawnResourceHelper.getXmiId(view)))
      {
        return ep;
      }
    }

    for (Object e : diagramEditPart.getConnections())
    {
      EditPart ep = (EditPart)e;
      if (DawnResourceHelper.getXmiId((EObject)ep.getModel()).equals(DawnResourceHelper.getXmiId(view)))
      {
        return ep;
      }
    }
    return null;
  }

  protected static void print(String s)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(s);
    }
  }
}
