package sa.com.sure.task.webservicesconsumer.database;

/**
 * Created by HussainHajjar on 5/13/2017.
 */

public class Column {

    public enum Type{TEXT, INTEGER, REAL, BLOB}

    private String name;
    private Type type;
    private boolean isMandatory;
    private boolean isPrimary;

    public Column(boolean isPrimary, String name, Type type){
        if(isPrimary) this.name = name;
        this.type = type;
        this.isMandatory = false;
        this.isPrimary = isPrimary;
    }

    public Column(String name, Type type){
        this.name = DatabaseConstants.COLUMN_PREFIX + name;
        this.type = type;
        this.isMandatory = false;
    }

    public Column(String name, Type type, boolean isMandatory){
        this.name = DatabaseConstants.COLUMN_PREFIX + name;
        this.type = type;
        this.isMandatory = isMandatory;
    }

    public String getName(){ return name; }

    public String getType(){ return type.toString(); }

    public boolean isMandatory(){ return isMandatory; }

    public boolean isPrimary(){ return isPrimary; }
}
