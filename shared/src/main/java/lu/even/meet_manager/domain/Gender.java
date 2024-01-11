package lu.even.meet_manager.domain;

public enum Gender {
    ALL("0", "All"),
    MALE("1", "Men"),
    FEMALE("2", "Women"),
    MIXED("3", "Mixed");


    final String code;

    public String getDescription() {
        return description;
    }

    final String description;

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
