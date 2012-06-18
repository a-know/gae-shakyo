package com.a_know.shakyo.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-18 21:27:26")
/** */
public final class AccessCounterMeta extends org.slim3.datastore.ModelMeta<com.a_know.shakyo.model.AccessCounter> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.AccessCounter, java.lang.Long> count = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.AccessCounter, java.lang.Long>(this, "count", "count", long.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.AccessCounter, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.AccessCounter, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.a_know.shakyo.model.AccessCounter> minutesTitle = new org.slim3.datastore.StringAttributeMeta<com.a_know.shakyo.model.AccessCounter>(this, "minutesTitle", "minutesTitle");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.AccessCounter, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.AccessCounter, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final AccessCounterMeta slim3_singleton = new AccessCounterMeta();

    /**
     * @return the singleton
     */
    public static AccessCounterMeta get() {
       return slim3_singleton;
    }

    /** */
    public AccessCounterMeta() {
        super("AccessCounter", com.a_know.shakyo.model.AccessCounter.class);
    }

    @Override
    public com.a_know.shakyo.model.AccessCounter entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.a_know.shakyo.model.AccessCounter model = new com.a_know.shakyo.model.AccessCounter();
        model.setCount(longToPrimitiveLong((java.lang.Long) entity.getProperty("count")));
        model.setKey(entity.getKey());
        model.setMinutesTitle((java.lang.String) entity.getProperty("minutesTitle"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.a_know.shakyo.model.AccessCounter m = (com.a_know.shakyo.model.AccessCounter) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("count", m.getCount());
        entity.setProperty("minutesTitle", m.getMinutesTitle());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.a_know.shakyo.model.AccessCounter m = (com.a_know.shakyo.model.AccessCounter) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.a_know.shakyo.model.AccessCounter m = (com.a_know.shakyo.model.AccessCounter) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.a_know.shakyo.model.AccessCounter m = (com.a_know.shakyo.model.AccessCounter) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        com.a_know.shakyo.model.AccessCounter m = (com.a_know.shakyo.model.AccessCounter) model;
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
        com.a_know.shakyo.model.AccessCounter m = (com.a_know.shakyo.model.AccessCounter) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        writer.setNextPropertyName("count");
        encoder0.encode(writer, m.getCount());
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getMinutesTitle() != null){
            writer.setNextPropertyName("minutesTitle");
            encoder0.encode(writer, m.getMinutesTitle());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.a_know.shakyo.model.AccessCounter jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.a_know.shakyo.model.AccessCounter m = new com.a_know.shakyo.model.AccessCounter();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("count");
        m.setCount(decoder0.decode(reader, m.getCount()));
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("minutesTitle");
        m.setMinutesTitle(decoder0.decode(reader, m.getMinutesTitle()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}