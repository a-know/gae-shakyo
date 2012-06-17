package com.a_know.shakyo.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-17 01:21:15")
/** */
public final class MinutesMeta extends org.slim3.datastore.ModelMeta<com.a_know.shakyo.model.Minutes> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, com.google.appengine.api.users.User> author = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, com.google.appengine.api.users.User>(this, "author", "author", com.google.appengine.api.users.User.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.util.Date> createdAt = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.util.Date>(this, "createdAt", "createdAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.lang.Integer> memoCount = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.lang.Integer>(this, "memoCount", "memoCount", int.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.a_know.shakyo.model.Minutes> title = new org.slim3.datastore.StringAttributeMeta<com.a_know.shakyo.model.Minutes>(this, "title", "title");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.util.Date> updateAt = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.util.Date>(this, "updateAt", "updateAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Minutes, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final MinutesMeta slim3_singleton = new MinutesMeta();

    /**
     * @return the singleton
     */
    public static MinutesMeta get() {
       return slim3_singleton;
    }

    /** */
    public MinutesMeta() {
        super("Minutes", com.a_know.shakyo.model.Minutes.class);
    }

    @Override
    public com.a_know.shakyo.model.Minutes entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.a_know.shakyo.model.Minutes model = new com.a_know.shakyo.model.Minutes();
        model.setAuthor((com.google.appengine.api.users.User) entity.getProperty("author"));
        model.setCreatedAt((java.util.Date) entity.getProperty("createdAt"));
        model.setKey(entity.getKey());
        model.setMemoCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("memoCount")));
        model.setTitle((java.lang.String) entity.getProperty("title"));
        model.setUpdateAt((java.util.Date) entity.getProperty("updateAt"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.a_know.shakyo.model.Minutes m = (com.a_know.shakyo.model.Minutes) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("author", m.getAuthor());
        entity.setProperty("createdAt", m.getCreatedAt());
        entity.setProperty("memoCount", m.getMemoCount());
        entity.setProperty("title", m.getTitle());
        entity.setProperty("updateAt", m.getUpdateAt());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.a_know.shakyo.model.Minutes m = (com.a_know.shakyo.model.Minutes) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.a_know.shakyo.model.Minutes m = (com.a_know.shakyo.model.Minutes) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.a_know.shakyo.model.Minutes m = (com.a_know.shakyo.model.Minutes) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        com.a_know.shakyo.model.Minutes m = (com.a_know.shakyo.model.Minutes) model;
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
        com.a_know.shakyo.model.Minutes m = (com.a_know.shakyo.model.Minutes) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getAuthor() != null){
            writer.setNextPropertyName("author");
            encoder0.encode(writer, m.getAuthor());
        }
        if(m.getCreatedAt() != null){
            writer.setNextPropertyName("createdAt");
            encoder0.encode(writer, m.getCreatedAt());
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        writer.setNextPropertyName("memoCount");
        encoder0.encode(writer, m.getMemoCount());
        if(m.getTitle() != null){
            writer.setNextPropertyName("title");
            encoder0.encode(writer, m.getTitle());
        }
        if(m.getUpdateAt() != null){
            writer.setNextPropertyName("updateAt");
            encoder0.encode(writer, m.getUpdateAt());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.a_know.shakyo.model.Minutes jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.a_know.shakyo.model.Minutes m = new com.a_know.shakyo.model.Minutes();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("author");
        m.setAuthor(decoder0.decode(reader, m.getAuthor()));
        reader = rootReader.newObjectReader("createdAt");
        m.setCreatedAt(decoder0.decode(reader, m.getCreatedAt()));
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("memoCount");
        m.setMemoCount(decoder0.decode(reader, m.getMemoCount()));
        reader = rootReader.newObjectReader("title");
        m.setTitle(decoder0.decode(reader, m.getTitle()));
        reader = rootReader.newObjectReader("updateAt");
        m.setUpdateAt(decoder0.decode(reader, m.getUpdateAt()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}