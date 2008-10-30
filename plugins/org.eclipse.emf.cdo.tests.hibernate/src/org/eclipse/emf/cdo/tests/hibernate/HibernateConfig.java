package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.hibernate.CDOHibernateUtil;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.teneo.TeneoUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.WrappedException;

import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class HibernateConfig extends RepositoryConfig
{
  public static final HibernateConfig INSTANCE = new HibernateConfig();

  public static final String MAPPING_FILE = "mappingfile";

  private static final long serialVersionUID = 1L;

  public HibernateConfig()
  {
    super("Hibernate");
  }

  @Override
  protected void initRepositoryProperties(Map<String, String> props)
  {
    super.initRepositoryProperties(props);
    props.put(Props.SUPPORTING_AUDITS, "false");
    props.put(Props.VERIFYING_REVISIONS, "false");

    try
    {
      final Properties teneoProperties = new Properties();
      teneoProperties.load(getClass().getResourceAsStream("/app.properties"));
      for (Object key : teneoProperties.keySet())
      {
        props.put((String)key, teneoProperties.getProperty((String)key));
      }
    }
    catch (Exception e)
    {
      throw WrappedException.wrap(e);
    }
  }

  @Override
  protected IStore createStore()
  {
    IHibernateMappingProvider mappingProvider = TeneoUtil.createMappingProvider();
    return CDOHibernateUtil.createStore(mappingProvider);
  }
}
