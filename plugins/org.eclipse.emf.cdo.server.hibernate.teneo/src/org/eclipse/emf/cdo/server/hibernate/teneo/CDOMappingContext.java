package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEClass;
import org.eclipse.emf.teneo.hibernate.mapper.MappingContext;
import org.eclipse.emf.teneo.simpledom.Element;

/**
 * Mapping context for CDO. It provides cdo classes as propertyhandler etc.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 */
public class CDOMappingContext extends MappingContext
{

  /** Add a tuplizer element or not */
  @Override
  public void addTuplizerElement(Element entityElement, PAnnotatedEClass aclass)
  {
    Element tuplizerElement = new Element("tuplizer").addAttribute("entity-mode", "dynamic-map").addAttribute("class",
        "org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDORevisionTuplizer");
    entityElement.add(0, tuplizerElement);
    tuplizerElement = new Element("tuplizer").addAttribute("entity-mode", "pojo").addAttribute("class",
        "org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDORevisionTuplizer");
    entityElement.add(0, tuplizerElement);
  }

  @Override
  public String getComponentPropertyHandlerName()
  {
    return super.getComponentPropertyHandlerName();
  }

  @Override
  public String getIdPropertyHandlerName()
  {
    return null;
  }

  @Override
  public String getPropertyHandlerName()
  {
    return super.getPropertyHandlerName();
  }

  @Override
  public String getVersionPropertyHandlerName()
  {
    return null;
  }

  @Override
  public String getEnumUserType()
  {
    return super.getEnumUserType();
  }

  @Override
  public String getEnumIntegerUserType()
  {
    return super.getEnumIntegerUserType();
  }

  @Override
  public String getSyntheticIdPropertyHandlerName()
  {
    return super.getSyntheticIdPropertyHandlerName();
  }

  @Override
  public String getSyntheticVersionPropertyHandlerName()
  {
    return super.getSyntheticVersionPropertyHandlerName();
  }

  @Override
  public String getDynamicEnumUserType()
  {
    return super.getDynamicEnumIntegerUserType();
  }

  @Override
  public String getDynamicEnumIntegerUserType()
  {
    return super.getDynamicEnumIntegerUserType();
  }

  @Override
  public String getDefaultUserType()
  {
    return super.getDefaultUserType();
  }

  @Override
  public String getXSDDateUserType()
  {
    return super.getXSDDateUserType();
  }

  @Override
  public String getXSDDateTimeUserType()
  {
    return super.getXSDDateTimeUserType();
  }
}
