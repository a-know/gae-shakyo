package com.a_know.shakyo.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-18 01:33:00")
/** */
public final class KeyWordMeta extends org.slim3.datastore.ModelMeta<com.a_know.shakyo.model.KeyWord> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.KeyWord, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.KeyWord, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.KeyWord, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.a_know.shakyo.model.KeyWord, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<com.a_know.shakyo.model.KeyWord, java.util.Set<java.lang.String>> words = new org.slim3.datastore.StringCollectionAttributeMeta<com.a_know.shakyo.model.KeyWord, java.util.Set<java.lang.String>>(this, "words", "words", java.util.Set.class);

    private static final KeyWordMeta slim3_singleton = new KeyWordMeta();

    /**
     * @return the singleton
     */
    public static KeyWordMeta get() {
       return slim3_singleton;
    }

    /** */
    public KeyWordMeta() {
        super("KeyWord", com.a_know.shakyo.model.KeyWord.class);
    }

    @Override
    public com.a_know.shakyo.model.KeyWord entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.a_know.shakyo.model.KeyWord model = new com.a_know.shakyo.model.KeyWord();
        model.setKey(entity.getKey());
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        model.setWords(new java.util.HashSet<java.lang.String>(toList(java.lang.String.class, entity.getProperty("words"))));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.a_know.shakyo.model.KeyWord m = (com.a_know.shakyo.model.KeyWord) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("version", m.getVersion());
        entity.setProperty("words", m.getWords());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.a_know.shakyo.model.KeyWord m = (com.a_know.shakyo.model.KeyWord) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.a_know.shakyo.model.KeyWord m = (com.a_know.shakyo.model.KeyWord) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.a_know.shakyo.model.KeyWord m = (com.a_know.shakyo.model.KeyWord) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        com.a_know.shakyo.model.KeyWord m = (com.a_know.shakyo.model.KeyWord) model;
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
        com.a_know.shakyo.model.KeyWord m = (com.a_know.shakyo.model.KeyWord) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        if(m.getWords() != null){
            writer.setNextPropertyName("words");
            writer.beginArray();
            for(java.lang.String v : m.getWords()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        writer.endObject();
    }

    @Override
    protected com.a_know.shakyo.model.KeyWord jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.a_know.shakyo.model.KeyWord m = new com.a_know.shakyo.model.KeyWord();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        reader = rootReader.newObjectReader("words");
        {
            java.util.HashSet<java.lang.String> elements = new java.util.HashSet<java.lang.String>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("words");
            if(r != null){
                reader = r;
                int n = r.length();
                for(int i = 0; i < n; i++){
                    r.setIndex(i);
                    java.lang.String v = decoder0.decode(reader, (java.lang.String)null)                    ;
                    if(v != null){
                        elements.add(v);
                    }
                }
                m.setWords(elements);
            }
        }
        return m;
    }
}