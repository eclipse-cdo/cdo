/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserInfo;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.ref.Interner;
import org.eclipse.net4j.util.security.SecurityUtil;

import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 * @since 4.20
 */
public class LDAPUserAuthenticator extends UserAuthenticator implements Entity.Store.Provider
{
  public static final String SCOPE_OBJECT = "object";

  public static final String SCOPE_ONELEVEL = "onelevel";

  public static final String SCOPE_SUBTREE = "subtree";

  public static final String FILTER_ANY = "(objectClass=*)";

  private final Tree config;

  private final Map<String, EnvironmentConfig> environmentConfigs = new HashMap<>();

  private final Map<LDAPDN, LDAPEntry> entries = new WeakHashMap<>();

  private final Entity.Store entityStore;

  public LDAPUserAuthenticator(Tree config)
  {
    this.config = config;

    Tree tree = config.child("entityStore");
    entityStore = tree == null ? null : new LDAPEntityStore(tree);
  }

  @Override
  public Class<? extends UserInfo> getUserInfoClass()
  {
    return LDAPUserInfo.class;
  }

  @Override
  public Entity.Store getEntityStore()
  {
    return entityStore;
  }

  @Override
  public UserInfo authenticateUser(String userID, char[] password)
  {
    String userPW = SecurityUtil.toString(password);
    if (!StringUtil.isEmpty(userPW))
    {
      try
      {
        LDAPEntry userEntry = searchUser(userID);
        if (userEntry != null)
        {
          return loginUser(userID, userPW, userEntry);
        }
      }
      catch (AuthenticationException ex)
      {
        //$FALL-THROUGH$
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    // Deny access.
    return null;
  }

  protected LDAPEntry searchUser(String userID) throws NamingException
  {
    Tree searchConfig = config.child("searchUser");
    if (searchConfig != null)
    {
      Map<String, String> stringSubstitutions = new HashMap<>();
      stringSubstitutions.put(stringSubstitutionKey("USER_ID"), userID);

      List<LDAPEntry> entries = ldapSearch(searchConfig, stringSubstitutions, null);
      if (entries.size() > 1)
      {
        throw new IllegalStateException("User " + userID + " has multiple LDAP entries: " + entries);
      }

      if (!entries.isEmpty())
      {
        return entries.get(0);
      }
    }

    // Deny access.
    return null;
  }

  protected LDAPUserInfo loginUser(String userID, String userPW, LDAPEntry userEntry) throws NamingException
  {
    Map<String, String> stringSubstitutions = new HashMap<>();
    stringSubstitutions.put(stringSubstitutionKey("USER_ID"), userID);
    stringSubstitutions.put(stringSubstitutionKey("USER_PW"), userPW);
    addStringSubstitutions(stringSubstitutions, "USER_", userEntry);

    Tree loginUserConfig = config.child("loginUser");
    Map<String, String> attributes = loginUserConfig.attributes();
    String environmentID = expandValue(attributes.get("environment"), stringSubstitutions);

    Hashtable<String, String> environment = createEnvironment(environmentID, stringSubstitutions);
    return ldapCall(environment, context -> createUserInfo(userID, userEntry, loginUserConfig, context));
  }

  protected LDAPUserInfo createUserInfo(String userID, LDAPEntry userEntry, Tree config, DirContext context) throws NamingException
  {
    Set<LDAPDN> groupDNs = new HashSet<>();

    Tree extractGroupsConfig = config.child("extractGroups");
    if (extractGroupsConfig != null)
    {
      List<LDAPEntry> entries = extractGroups(userID, userEntry, extractGroupsConfig, context);
      if (entries != null)
      {
        entries.forEach(entry -> groupDNs.add(entry.DN()));
      }
    }

    Tree searchGroupsConfig = config.child("searchGroups");
    if (searchGroupsConfig != null)
    {
      List<LDAPEntry> entries = searchGroups(userID, userEntry, searchGroupsConfig, context);
      if (entries != null)
      {
        entries.forEach(entry -> groupDNs.add(entry.DN()));
      }
    }

    return new LDAPUserInfo(userID, userEntry.DN(), groupDNs);
  }

  protected List<LDAPEntry> extractGroups(String userID, LDAPEntry userEntry, Tree config, DirContext context) throws NamingException
  {
    return null;
  }

  protected List<LDAPEntry> searchGroups(String userID, LDAPEntry userEntry, Tree config, DirContext context) throws NamingException
  {
    Map<String, String> stringSubstitutions = new HashMap<>();
    stringSubstitutions.put(stringSubstitutionKey("USER_ID"), userID);
    addStringSubstitutions(stringSubstitutions, "USER_", userEntry);

    return ldapSearch(config, stringSubstitutions, context);
  }

  public final Map<LDAPDN, LDAPEntry> ldapEntries(Collection<LDAPDN> dns)
  {
    Map<LDAPDN, LDAPEntry> result = new HashMap<>();

    synchronized (entries)
    {
      for (LDAPDN dn : dns)
      {
        LDAPEntry entry = entries.get(dn);
        if (entry != null)
        {
          result.put(dn, entry);
        }
      }
    }

    return result;
  }

  public final LDAPEntry ldapEntry(LDAPDN dn)
  {
    synchronized (entries)
    {
      return entries.get(dn);
    }
  }

  protected final LDAPEntry ldapEntry(LDAPDN dn, String rdn, Attributes attributes, Object object)
  {
    LDAPEntry entry = new LDAPEntry(dn, rdn, attributes, object);

    synchronized (entries)
    {
      entries.put(dn, entry);
    }

    return entry;
  }

  protected final LDAPEntry ldapEntry(SearchResult result)
  {
    LDAPDN dn = LDAPDN.create(result.getNameInNamespace());
    String rdn = result.getName();
    Attributes attributes = result.getAttributes();
    Object object = result.getObject();
    return ldapEntry(dn, rdn, attributes, object);
  }

  protected final List<LDAPEntry> ldapSearch(Tree searchConfig, Map<String, String> stringSubstitutions, DirContext outerContext) throws NamingException
  {
    Map<String, String> map = searchConfig.attributes();
    String start = expandValue(map.get("start"), stringSubstitutions);
    int scope = parseSearchScope(expandValue(map.get("scope"), stringSubstitutions));
    String filter = parseFilter(expandValue(map.get("filter"), stringSubstitutions));
    long countLimit = parseLong(expandValue(map.get("countLimit"), stringSubstitutions), 0L);
    int timeLimit = parseInt(expandValue(map.get("timeLimit"), stringSubstitutions), 0);
    String[] returnAttributes = parseStrings(expandValue(map.get("returnAttributes"), stringSubstitutions), new String[0]);
    boolean returnObject = parseBoolean(expandValue(map.get("returnObject"), stringSubstitutions), false);

    LDAPCallable<List<LDAPEntry>> callable = context -> {
      List<LDAPEntry> entries = new ArrayList<>();

      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(scope);
      searchControls.setCountLimit(countLimit);
      searchControls.setTimeLimit(timeLimit);
      searchControls.setReturningAttributes(returnAttributes);
      searchControls.setReturningObjFlag(returnObject);

      NamingEnumeration<SearchResult> results = context.search(start, filter, searchControls);
      while (results.hasMore())
      {
        SearchResult result = results.next();
        LDAPEntry entry = ldapEntry(result);
        entries.add(entry);
      }

      return entries;
    };

    String environmentID = expandValue(map.get("environment"), stringSubstitutions);
    if (environmentID == null && outerContext != null)
    {
      return callable.call(outerContext);
    }

    Hashtable<String, String> environment = createEnvironment(environmentID, stringSubstitutions);
    return ldapCall(environment, callable);
  }

  protected Hashtable<String, String> createEnvironment(String id, Map<String, String> stringSubstitutions)
  {
    Hashtable<String, String> environment = new Hashtable<>();
    Set<String> visited = new HashSet<>();

    fillEnvironment(id, stringSubstitutions, environment, visited);
    return environment;
  }

  protected void fillEnvironment(String id, Map<String, String> stringSubstitutions, Hashtable<String, String> environment, Set<String> visited)
  {
    if (!visited.add(id))
    {
      throw new IllegalStateException("Environment cycle detected: " + id);
    }

    EnvironmentConfig environmentConfig = environmentConfigs.get(id);
    if (environmentConfig == null)
    {
      throw new IllegalStateException("Environment not found: " + id);
    }

    List<String> inherits = environmentConfig.inherits();
    for (String inherit : inherits)
    {
      fillEnvironment(inherit, stringSubstitutions, environment, visited);
    }

    Map<String, String> properties = environmentConfig.properties();
    properties.forEach((name, value) -> environment.put(name, expandValue(value, stringSubstitutions)));
  }

  protected String expandValue(String value, Map<String, String> stringSubstitutions)
  {
    if (value == null)
    {
      return null;
    }

    IManagedContainer container = getContainer();
    return RepositoryConfigurator.expandValue(value, stringSubstitutions, container);
  }

  protected String stringSubstitutionKey(String str)
  {
    return '$' + str + '$';
  }

  protected void addStringSubstitutions(Map<String, String> stringSubstitutions, String prefix, LDAPEntry entry) throws NamingException
  {
    prefix = StringUtil.safe(prefix);

    stringSubstitutions.put(stringSubstitutionKey(prefix + "DN"), entry.DN().toString());
    stringSubstitutions.put(stringSubstitutionKey(prefix + "RDN"), entry.RDN());
    stringSubstitutions.put(stringSubstitutionKey(prefix + "PDN"), entry.PDN());

    Attributes attributes = entry.attributes();
    if (attributes != null)
    {
      NamingEnumeration<? extends Attribute> attrIt = attributes.getAll();
      while (attrIt.hasMore())
      {
        Attribute attribute = attrIt.next();
        String key = stringSubstitutionKey(prefix + "ATTR_" + attribute.getID());

        int size = attribute.size();
        if (size == 1)
        {
          Object value = attribute.get();
          if (value instanceof String)
          {
            stringSubstitutions.put(key, (String)value);
          }
        }
        else if (size > 1)
        {
          StringJoiner joiner = new StringJoiner(", ");

          NamingEnumeration<?> valueIt = attribute.getAll();
          while (valueIt.hasMore())
          {
            Object value = valueIt.next();
            if (value instanceof String)
            {
              joiner.add((String)value);
            }
          }

          if (joiner.length() != 0)
          {
            stringSubstitutions.put(key, "{" + joiner + "}");
          }
        }
      }
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(config, "config"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    config.children("environment", env -> {
      String id = env.attribute("id");
      String inherits = env.attribute("inherits");
      Map<String, String> properties = env.properties();
      environmentConfigs.put(id, new EnvironmentConfig(id, inherits, properties));
    });
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    environmentConfigs.clear();
    entries.clear();
  }

  protected static <T> T ldapCall(Hashtable<String, String> environment, LDAPCallable<T> callable) throws NamingException
  {
    DirContext context = new InitialDirContext(environment);

    try
    {
      return callable.call(context);
    }
    finally
    {
      context.close();
    }
  }

  protected static boolean parseBoolean(String str, boolean defaultValue)
  {
    if (str == null)
    {
      return defaultValue;
    }

    return Boolean.parseBoolean(str);
  }

  protected static int parseInt(String str, int defaultValue)
  {
    if (str == null)
    {
      return defaultValue;
    }

    return Integer.parseInt(str);
  }

  protected static long parseLong(String str, long defaultValue)
  {
    if (str == null)
    {
      return defaultValue;
    }

    return Long.parseLong(str);
  }

  protected static List<String> parseStrings(String str, List<String> defaultValue)
  {
    if (str == null)
    {
      return defaultValue;
    }

    if (str.length() != 0)
    {
      Set<String> result = new LinkedHashSet<>();

      StringTokenizer tokenizer = new StringTokenizer(str, ",");
      while (tokenizer.hasMoreTokens())
      {
        String id = tokenizer.nextToken().trim();
        if (!StringUtil.isEmpty(id))
        {
          result.add(id);
        }
      }

      if (!result.isEmpty())
      {
        return Collections.unmodifiableList(new ArrayList<>(result));
      }
    }

    return Collections.emptyList();
  }

  protected static String[] parseStrings(String str, String[] defaultValue)
  {
    if (str == null)
    {
      return defaultValue;
    }

    List<String> defaultList = defaultValue == null ? null : Arrays.asList(defaultValue);
    List<String> list = parseStrings(str, defaultList);
    return list.toArray(new String[list.size()]);
  }

  protected static int parseSearchScope(String str)
  {
    if (str == null || str.equals(SCOPE_SUBTREE))
    {
      return SearchControls.SUBTREE_SCOPE;
    }

    if (str.equals(SCOPE_ONELEVEL))
    {
      return SearchControls.ONELEVEL_SCOPE;
    }

    if (str.equals(SCOPE_OBJECT))
    {
      return SearchControls.OBJECT_SCOPE;
    }

    throw new IllegalArgumentException("Illegal search scope: " + str);
  }

  protected static String parseFilter(String str)
  {
    return str == null ? FILTER_ANY : str;
  }

  /**
   * @author Eike Stepper
   */
  public static final class EnvironmentConfig
  {
    private final String id;

    private final List<String> inherits;

    private final Map<String, String> properties;

    public EnvironmentConfig(String id, String inherits, Map<String, String> properties)
    {
      this.id = checkID(id);
      this.inherits = parseStrings(inherits, Collections.emptyList());
      this.properties = Collections.unmodifiableMap(properties);
    }

    public final String id()
    {
      return id;
    }

    public final List<String> inherits()
    {
      return inherits;
    }

    public Map<String, String> properties()
    {
      return properties;
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(id, inherits, properties);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      EnvironmentConfig other = (EnvironmentConfig)obj;
      return Objects.equals(id, other.id) && Objects.equals(inherits, other.inherits) && Objects.equals(properties, other.properties);
    }

    @Override
    public String toString()
    {
      return "EnvironmentConfig[" + id + "]";
    }

    private static String checkID(String str) throws IllegalArgumentException
    {
      if (!StringUtil.isEmpty(str))
      {
        if (str.indexOf(',') != -1)
        {
          throw new IllegalArgumentException("Illegal environment ID: " + str);
        }

        return str.trim();
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  protected interface LDAPCallable<T>
  {
    public T call(DirContext context) throws NamingException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class LDAPDN
  {
    private static final DNInterner INTERNER = new DNInterner();

    private final String value;

    private LDAPDN(String value)
    {
      this.value = Objects.requireNonNull(value);
    }

    @Override
    public String toString()
    {
      return value;
    }

    @Override
    public int hashCode()
    {
      return getHashCode(value);
    }

    private static int getHashCode(String value)
    {
      return Objects.hashCode(value);
    }

    public static LDAPDN create(String value)
    {
      return value == null ? null : INTERNER.intern(value);
    }

    /**
     * @author Eike Stepper
     */
    private static final class DNInterner extends Interner<LDAPDN>
    {
      public synchronized LDAPDN intern(String value)
      {
        int hashCode = getHashCode(value);
        for (Entry<LDAPDN> entry = getEntry(hashCode); entry != null; entry = entry.getNextEntry())
        {
          LDAPDN dn = entry.get();
          if (dn != null && Objects.equals(dn.value, value))
          {
            return dn;
          }
        }

        LDAPDN dn = new LDAPDN(value);
        addEntry(createEntry(dn, hashCode));
        return dn;
      }

      @Override
      protected int hashCode(LDAPDN id)
      {
        return getHashCode(id.value);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LDAPEntry
  {
    private final LDAPDN dn;

    private final String rdn;

    private final Attributes attributes;

    private final Object object;

    private LDAPEntry(LDAPDN dn, String rdn, Attributes attributes, Object object)
    {
      this.dn = Objects.requireNonNull(dn);
      this.rdn = rdn;
      this.attributes = attributes;
      this.object = object;
    }

    public LDAPDN DN()
    {
      return dn;
    }

    public String RDN()
    {
      return rdn;
    }

    public String PDN()
    {
      if (rdn == null)
      {
        return null;
      }

      String dn = this.dn.toString();
      String parentDN = dn.substring(rdn.length()).trim();
      if (parentDN.length() != 0 && parentDN.charAt(0) == ',')
      {
        parentDN = dn.substring(1).trim();
      }

      return parentDN;
    }

    public Attributes attributes()
    {
      return attributes;
    }

    public Object object()
    {
      return object;
    }

    @Override
    public int hashCode()
    {
      return dn.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      LDAPEntry other = (LDAPEntry)obj;
      return dn == other.dn;
    }

    @Override
    public String toString()
    {
      return "LDAPEntry[" + dn + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LDAPUserInfo extends UserInfo
  {
    private final LDAPDN userDN;

    private final Set<LDAPDN> groupDNs;

    public LDAPUserInfo(String userID, LDAPDN userDN, Set<LDAPDN> groupDNs)
    {
      super(Objects.requireNonNull(userID));
      this.userDN = Objects.requireNonNull(userDN);
      this.groupDNs = ObjectUtil.isEmpty(groupDNs) ? Collections.emptySet() : Collections.unmodifiableSet(groupDNs);
    }

    public LDAPUserInfo(String userID, LDAPDN userEntry)
    {
      this(userID, userEntry, null);
    }

    public final LDAPDN userDN()
    {
      return userDN;
    }

    public Set<LDAPDN> groupDNs()
    {
      return groupDNs;
    }

    public boolean groupMember(LDAPDN groupDN)
    {
      return groupDNs.contains(groupDN);
    }

    @Override
    protected boolean isStructurallyEqual(UserInfo userInfo)
    {
      LDAPUserInfo other = (LDAPUserInfo)userInfo;
      return userDN == other.userDN && groupDNs.equals(other.groupDNs());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LDAPEntityStore extends Entity.SingleNamespaceComputer
  {
    private final boolean exposeUserIDs;

    public LDAPEntityStore(Tree config)
    {
      super(CDOProtocolConstants.USER_INFO_NAMESPACE);
      exposeUserIDs = config.attribute("exposeUserIDs", false);
    }

    @Override
    protected Collection<String> computeNames()
    {
      if (exposeUserIDs)
      {
        // TODO Query LDAP for user IDs.
      }

      return Collections.emptySet();
    }

    @Override
    protected Entity computeEntity(String name)
    {
      LDAPUserInfo userInfo = (LDAPUserInfo)getRepositoryProtector().getUserInfo(name);
      if (userInfo != null)
      {
        LDAPEntry entry = ldapEntry(userInfo.userDN());
        if (entry != null)
        {
          Attributes attributes = entry.attributes();
          Entity.Builder builder = entityBuilder(name);

          for (String propertyName : CDOProtocolConstants.USER_INFO_PROPERTIES)
          {
            Attribute attribute = attributes.get(propertyName);
            if (attribute != null)
            {
              builder.property(propertyName, attribute.toString());
            }
          }

          return builder.build();
        }
      }

      return null;
    }
  }
}
