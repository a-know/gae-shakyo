package dto;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-17 01:21:15")
/** */
public final class ProfileMeta extends org.slim3.datastore.ModelMeta<dto.Profile> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<dto.Profile, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<dto.Profile, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<dto.Profile> profileUrl = new org.slim3.datastore.StringAttributeMeta<dto.Profile>(this, "profileUrl", "profileUrl");

    private static final ProfileMeta slim3_singleton = new ProfileMeta();

    /**
     * @return the singleton
     */
    public static ProfileMeta get() {
       return slim3_singleton;
    }

    /** */
    public ProfileMeta() {
        super("Profile", dto.Profile.class);
    }

    @Override
    public dto.Profile entityToModel(com.google.appengine.api.datastore.Entity entity) {
        dto.Profile model = new dto.Profile();
        model.setKey(entity.getKey());
        model.setProfileUrl((java.lang.String) entity.getProperty("profileUrl"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        dto.Profile m = (dto.Profile) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("profileUrl", m.getProfileUrl());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        dto.Profile m = (dto.Profile) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        dto.Profile m = (dto.Profile) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(dto.Profile) is not defined.");
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
        dto.Profile m = (dto.Profile) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getProfileUrl() != null){
            writer.setNextPropertyName("profileUrl");
            encoder0.encode(writer, m.getProfileUrl());
        }
        writer.endObject();
    }

    @Override
    protected dto.Profile jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        dto.Profile m = new dto.Profile();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("profileUrl");
        m.setProfileUrl(decoder0.decode(reader, m.getProfileUrl()));
        return m;
    }
}