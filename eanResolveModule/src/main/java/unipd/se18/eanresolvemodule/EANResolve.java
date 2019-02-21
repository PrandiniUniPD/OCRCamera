package unipd.se18.eanresolvemodule;

/**
 * Useful set a type of recognition
 * It avoids the single point of failure relative to this process
 * @author Elia Bedin
 */
public class EANResolve {
    /**
     * Ids of the different EAN resolver API
     */
    public enum API {
        MIGNIFY
    }

    /**
     * Provides an EAN resolve
     * @param type The id of the recognition api requested
     * @return the object relative to the chosen type
     */
    public static EAN eanResolve(API type, EANResolveListener resolveListener){
        switch (type){
            case MIGNIFY: return new MignifyResolver(resolveListener);
            default: return new MignifyResolver(resolveListener);
        }
    }
}
