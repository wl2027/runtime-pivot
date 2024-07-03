package sun.instrument;

public class RuntimePivotTransformerManager extends TransformerManager {

    private TransformerManager transformerManager;

    public RuntimePivotTransformerManager(TransformerManager transformerManager,boolean isRetransformable){
        super(isRetransformable);
        transformerManager = transformerManager;
    }
}
