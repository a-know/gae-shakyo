package dto;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-06-19 13:58:55")
/** */
public final class LogDTOMeta extends org.slim3.datastore.ModelMeta<dto.LogDTO> {

    /** */
    public final org.slim3.datastore.StringAttributeMeta<dto.LogDTO> combined = new org.slim3.datastore.StringAttributeMeta<dto.LogDTO>(this, "combined", "combined");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<dto.LogDTO, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<dto.LogDTO, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<dto.LogDTO, java.util.List<java.lang.String>> logLevels = new org.slim3.datastore.StringCollectionAttributeMeta<dto.LogDTO, java.util.List<java.lang.String>>(this, "logLevels", "logLevels", java.util.List.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<dto.LogDTO, java.util.List<java.lang.String>> logLines = new org.slim3.datastore.StringCollectionAttributeMeta<dto.LogDTO, java.util.List<java.lang.String>>(this, "logLines", "logLines", java.util.List.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<dto.LogDTO> offset = new org.slim3.datastore.StringAttributeMeta<dto.LogDTO>(this, "offset", "offset");

    private static final LogDTOMeta slim3_singleton = new LogDTOMeta();

    /**
     * @return the singleton
     */
    public static LogDTOMeta get() {
       return slim3_singleton;
    }

    /** */
    public LogDTOMeta() {
        super("LogDTO", dto.LogDTO.class);
    }

    @Override
    public dto.LogDTO entityToModel(com.google.appengine.api.datastore.Entity entity) {
        dto.LogDTO model = new dto.LogDTO();
        model.setCombined((java.lang.String) entity.getProperty("combined"));
        model.setKey(entity.getKey());
        model.setLogLevels(toList(java.lang.String.class, entity.getProperty("logLevels")));
        model.setLogLines(toList(java.lang.String.class, entity.getProperty("logLines")));
        model.setOffset((java.lang.String) entity.getProperty("offset"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        dto.LogDTO m = (dto.LogDTO) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("combined", m.getCombined());
        entity.setProperty("logLevels", m.getLogLevels());
        entity.setProperty("logLines", m.getLogLines());
        entity.setProperty("offset", m.getOffset());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        dto.LogDTO m = (dto.LogDTO) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        dto.LogDTO m = (dto.LogDTO) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(dto.LogDTO) is not defined.");
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
        dto.LogDTO m = (dto.LogDTO) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getCombined() != null){
            writer.setNextPropertyName("combined");
            encoder0.encode(writer, m.getCombined());
        }
        if(m.getLogLevels() != null){
            writer.setNextPropertyName("logLevels");
            writer.beginArray();
            for(java.lang.String v : m.getLogLevels()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        if(m.getLogLines() != null){
            writer.setNextPropertyName("logLines");
            writer.beginArray();
            for(java.lang.String v : m.getLogLines()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        if(m.getOffset() != null){
            writer.setNextPropertyName("offset");
            encoder0.encode(writer, m.getOffset());
        }
        writer.endObject();
    }

    @Override
    protected dto.LogDTO jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        dto.LogDTO m = new dto.LogDTO();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("combined");
        m.setCombined(decoder0.decode(reader, m.getCombined()));
        reader = rootReader.newObjectReader("logLevels");
        {
            java.util.ArrayList<java.lang.String> elements = new java.util.ArrayList<java.lang.String>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("logLevels");
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
                m.setLogLevels(elements);
            }
        }
        reader = rootReader.newObjectReader("logLines");
        {
            java.util.ArrayList<java.lang.String> elements = new java.util.ArrayList<java.lang.String>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("logLines");
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
                m.setLogLines(elements);
            }
        }
        reader = rootReader.newObjectReader("offset");
        m.setOffset(decoder0.decode(reader, m.getOffset()));
        return m;
    }
}