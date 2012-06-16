package dto;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-07 11:10:06")
/** */
public final class AuthDTOMeta extends org.slim3.datastore.ModelMeta<dto.AuthDTO> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<dto.AuthDTO, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<dto.AuthDTO, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<dto.AuthDTO, java.lang.Boolean> loggedIn = new org.slim3.datastore.CoreAttributeMeta<dto.AuthDTO, java.lang.Boolean>(this, "loggedIn", "loggedIn", boolean.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<dto.AuthDTO> loginURL = new org.slim3.datastore.StringAttributeMeta<dto.AuthDTO>(this, "loginURL", "loginURL");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<dto.AuthDTO> logoutURL = new org.slim3.datastore.StringAttributeMeta<dto.AuthDTO>(this, "logoutURL", "logoutURL");

    private static final AuthDTOMeta slim3_singleton = new AuthDTOMeta();

    /**
     * @return the singleton
     */
    public static AuthDTOMeta get() {
       return slim3_singleton;
    }

    /** */
    public AuthDTOMeta() {
        super("AuthDTO", dto.AuthDTO.class);
    }

    @Override
    public dto.AuthDTO entityToModel(com.google.appengine.api.datastore.Entity entity) {
        dto.AuthDTO model = new dto.AuthDTO();
        model.setKey(entity.getKey());
        model.setLoggedIn(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("loggedIn")));
        model.setLoginURL((java.lang.String) entity.getProperty("loginURL"));
        model.setLogoutURL((java.lang.String) entity.getProperty("logoutURL"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        dto.AuthDTO m = (dto.AuthDTO) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("loggedIn", m.isLoggedIn());
        entity.setProperty("loginURL", m.getLoginURL());
        entity.setProperty("logoutURL", m.getLogoutURL());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        dto.AuthDTO m = (dto.AuthDTO) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        dto.AuthDTO m = (dto.AuthDTO) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(dto.AuthDTO) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
    }

    @Override
    protected void postGet(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

    @Override
    protected boolean isCipherProperty(String propertyName) {
        return false;
    }

    @Override
    protected void modelToJson(org.slim3.datastore.json.JsonWriter writer, java.lang.Object model, int maxDepth, int currentDepth) {
        dto.AuthDTO m = (dto.AuthDTO) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        writer.setNextPropertyName("loggedIn");
        encoder0.encode(writer, m.isLoggedIn());
        if(m.getLoginURL() != null){
            writer.setNextPropertyName("loginURL");
            encoder0.encode(writer, m.getLoginURL());
        }
        if(m.getLogoutURL() != null){
            writer.setNextPropertyName("logoutURL");
            encoder0.encode(writer, m.getLogoutURL());
        }
        writer.endObject();
    }

    @Override
    protected dto.AuthDTO jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        dto.AuthDTO m = new dto.AuthDTO();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("loggedIn");
        m.setLoggedIn(decoder0.decode(reader, m.isLoggedIn()));
        reader = rootReader.newObjectReader("loginURL");
        m.setLoginURL(decoder0.decode(reader, m.getLoginURL()));
        reader = rootReader.newObjectReader("logoutURL");
        m.setLogoutURL(decoder0.decode(reader, m.getLogoutURL()));
        return m;
    }
}