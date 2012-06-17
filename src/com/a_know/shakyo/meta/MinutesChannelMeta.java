package com.a_know.shakyo.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-17 01:21:15")
/** */
public final class MinutesChannelMeta extends org.slim3.datastore.ModelMeta<com.a_know.shakyo.model.MinutesChannel> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, java.util.Date> createdAt = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, java.util.Date>(this, "createdAt", "createdAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, com.google.appengine.api.datastore.Key> minutesKey = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, com.google.appengine.api.datastore.Key>(this, "minutesKey", "minutesKey", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.MinutesChannel, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final MinutesChannelMeta slim3_singleton = new MinutesChannelMeta();

    /**
     * @return the singleton
     */
    public static MinutesChannelMeta get() {
       return slim3_singleton;
    }

    /** */
    public MinutesChannelMeta() {
        super("MinutesChannel", com.a_know.shakyo.model.MinutesChannel.class);
    }

    @Override
    public com.a_know.shakyo.model.MinutesChannel entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.a_know.shakyo.model.MinutesChannel model = new com.a_know.shakyo.model.MinutesChannel();
        model.setCreatedAt((java.util.Date) entity.getProperty("createdAt"));
        model.setKey(entity.getKey());
        model.setMinutesKey((com.google.appengine.api.datastore.Key) entity.getProperty("minutesKey"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.a_know.shakyo.model.MinutesChannel m = (com.a_know.shakyo.model.MinutesChannel) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("createdAt", m.getCreatedAt());
        entity.setProperty("minutesKey", m.getMinutesKey());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.a_know.shakyo.model.MinutesChannel m = (com.a_know.shakyo.model.MinutesChannel) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.a_know.shakyo.model.MinutesChannel m = (com.a_know.shakyo.model.MinutesChannel) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.a_know.shakyo.model.MinutesChannel m = (com.a_know.shakyo.model.MinutesChannel) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        com.a_know.shakyo.model.MinutesChannel m = (com.a_know.shakyo.model.MinutesChannel) model;
        long version = m.getVersion() != null ? m.getVersion().longValue() : 0L;
        m.setVersion(Long.valueOf(version + 1L));
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
        com.a_know.shakyo.model.MinutesChannel m = (com.a_know.shakyo.model.MinutesChannel) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getCreatedAt() != null){
            writer.setNextPropertyName("createdAt");
            encoder0.encode(writer, m.getCreatedAt());
        }
        if(m.getMinutesKey() != null){
            writer.setNextPropertyName("minutesKey");
            encoder0.encode(writer, m.getMinutesKey());
        }
        if(m.getToken() != null){
            writer.setNextPropertyName("token");
            encoder0.encode(writer, m.getToken());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.a_know.shakyo.model.MinutesChannel jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.a_know.shakyo.model.MinutesChannel m = new com.a_know.shakyo.model.MinutesChannel();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("createdAt");
        m.setCreatedAt(decoder0.decode(reader, m.getCreatedAt()));
        reader = rootReader.newObjectReader("minutesKey");
        m.setMinutesKey(decoder0.decode(reader, m.getMinutesKey()));
        reader = rootReader.newObjectReader("token");
        m.setToken(decoder0.decode(reader, m.getToken()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}