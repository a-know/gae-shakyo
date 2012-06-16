package com.a_know.shakyo.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-07 11:10:06")
/** */
public final class MemoMeta extends org.slim3.datastore.ModelMeta<com.a_know.shakyo.model.Memo> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, com.google.appengine.api.users.User> author = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, com.google.appengine.api.users.User>(this, "author", "author", com.google.appengine.api.users.User.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, java.util.Date> createdAt = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, java.util.Date>(this, "createdAt", "createdAt", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.a_know.shakyo.model.Memo> memo = new org.slim3.datastore.StringAttributeMeta<com.a_know.shakyo.model.Memo>(this, "memo", "memo");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, com.google.appengine.api.datastore.Key> minutes = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, com.google.appengine.api.datastore.Key>(this, "minutes", "minutes", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.Memo, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final MemoMeta slim3_singleton = new MemoMeta();

    /**
     * @return the singleton
     */
    public static MemoMeta get() {
       return slim3_singleton;
    }

    /** */
    public MemoMeta() {
        super("Memo", com.a_know.shakyo.model.Memo.class);
    }

    @Override
    public com.a_know.shakyo.model.Memo entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.a_know.shakyo.model.Memo model = new com.a_know.shakyo.model.Memo();
        model.setAuthor((com.google.appengine.api.users.User) entity.getProperty("author"));
        model.setCreatedAt((java.util.Date) entity.getProperty("createdAt"));
        model.setKey(entity.getKey());
        model.setMemo((java.lang.String) entity.getProperty("memo"));
        model.setMinutes((com.google.appengine.api.datastore.Key) entity.getProperty("minutes"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.a_know.shakyo.model.Memo m = (com.a_know.shakyo.model.Memo) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("author", m.getAuthor());
        entity.setProperty("createdAt", m.getCreatedAt());
        entity.setProperty("memo", m.getMemo());
        entity.setProperty("minutes", m.getMinutes());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.a_know.shakyo.model.Memo m = (com.a_know.shakyo.model.Memo) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.a_know.shakyo.model.Memo m = (com.a_know.shakyo.model.Memo) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.a_know.shakyo.model.Memo m = (com.a_know.shakyo.model.Memo) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        com.a_know.shakyo.model.Memo m = (com.a_know.shakyo.model.Memo) model;
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
        com.a_know.shakyo.model.Memo m = (com.a_know.shakyo.model.Memo) model;
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
        if(m.getMemo() != null){
            writer.setNextPropertyName("memo");
            encoder0.encode(writer, m.getMemo());
        }
        if(m.getMinutes() != null){
            writer.setNextPropertyName("minutes");
            encoder0.encode(writer, m.getMinutes());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.a_know.shakyo.model.Memo jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.a_know.shakyo.model.Memo m = new com.a_know.shakyo.model.Memo();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("author");
        m.setAuthor(decoder0.decode(reader, m.getAuthor()));
        reader = rootReader.newObjectReader("createdAt");
        m.setCreatedAt(decoder0.decode(reader, m.getCreatedAt()));
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("memo");
        m.setMemo(decoder0.decode(reader, m.getMemo()));
        reader = rootReader.newObjectReader("minutes");
        m.setMinutes(decoder0.decode(reader, m.getMinutes()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}