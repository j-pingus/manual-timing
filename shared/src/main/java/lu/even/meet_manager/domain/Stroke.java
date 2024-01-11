package lu.even.meet_manager.domain;

public enum Stroke {
    CUSTOM("0","Custom"),
    FREESTYLE("1","Freestyle"),
    BACKSTROCKE("2","Backstrocke"),
    BREASTSTROKE("3","Breaststroke"),
    BUTTERFLY("4","Butterfly"),
    MEDLEY("5","Medley"),
    SURFACE("6","Surface"),
    BIFINS("7","Bifins"),
    APNEA("8","Apnea"),
    IMMERSION("9","Immersion");
    String code;

    public String getDescription() {
        return description;
    }

    String description;

    Stroke(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public static Stroke getByCode(String code){
        for(Stroke stroke:values()){
            if(stroke.code.equals(code)){
                return stroke;
            }
        }
        return CUSTOM;
    }
}
