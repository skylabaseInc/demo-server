package io.mifos.dev.helper;

public class StringHelper {

    public StringHelper(){
        super();
    }

    public static String cleanString(final String payload){
        final String identifier = payload.replaceAll("^\"|\"$", "");
        return identifier;
    }
}
