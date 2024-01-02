package lu.even.meet_manager.domain;

public enum Gender {
    ALL("0", "All"),
    MALE("1", "Male"),
    FEMALE("2", "Female"),
    MIXED("3", "Mixed");


    String code;

    public String getDescription() {
        return description;
    }

    String description;

    Gender(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public static Gender getByCode(String code){
        for(Gender gender:values()){
            if(gender.code.equals(code)){
                return gender;
            }
        }
        return ALL;
    }
}
